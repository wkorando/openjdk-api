package net.openjdk.api.datasource;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.openjdk.api.v1.release.binary.model.BinarySchema;
import net.openjdk.api.v1.release.information.models.InfoSchema;
import net.openjdk.api.v1.release.operating_systems.models.OSSchema;
import net.openjdk.api.v1.release.versions.models.VersionSchema;
import net.openjdk.api.v1.release.versions.models.VersionTypeSchema;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;


public class JDKNet implements DataSourceInterface {

    protected Logger logger = LoggerFactory.getLogger(JDKNet.class);

    protected final String jdkWebURL = "https://jdk.java.net";
    protected final HTMLtoXMLRoutine parser = new HTMLtoXMLRoutine();
    protected List<BinarySchema> binaries = Collections.synchronizedList(new ArrayList<>());
    protected List<InfoSchema> releases = Collections.synchronizedList(new ArrayList<>());
    protected List<VersionSchema> versions = Collections.synchronizedList(new ArrayList<>());
    protected Set<OSSchema> schemas = Collections.synchronizedSet(new HashSet<>());

    // run once in an hour
    @Scheduled(cron = "${application.datasource.scheduled_update_cron_value: 0 0 */1 * * *}")
    private void scheduledTask() {
        logger.info("running JDKNet.scheduledTask");
        readData();
    }

    @Value("${application.datasource.implementation.jdk_net.include_project_ea_builds}")
    private boolean includeProjectEABuilds;

    public void readData() {
        try {
            var newBinaries = Collections.synchronizedList(new ArrayList<BinarySchema>());
            var newReleases = Collections.synchronizedList(new ArrayList<InfoSchema>());
            var newVersions = Collections.synchronizedList(new ArrayList<VersionSchema>());
            var newSchemas = Collections.synchronizedSet(new HashSet<OSSchema>());

            var doc = parser.parseXMLtoDocument(jdkWebURL);
            getListOfGABuilds(doc).forEach(x->{
                try {
                    parseJDKVersionOSBinary(x, VersionTypeSchema.GA(),
                            newVersions, newBinaries, newSchemas, newReleases);
                } catch (Exception e) {
                    logger.warn("Unable to read OpenJDK GA builds and from jdk.java.net", e);
                }
            });
            getListOfEABuilds(doc).forEach(x->{
                try {
                    if (((Element) x).text().startsWith("JDK")) {
                        parseJDKVersionOSBinary(x.attributes().get("href"), VersionTypeSchema.EA(),
                                newVersions, newBinaries, newSchemas, newReleases);
                    } else {
                        if (includeProjectEABuilds) {
                            parseJDKVersionOSBinary(x.attributes().get("href"), VersionTypeSchema.ProjectEA(),
                                    newVersions, newBinaries, newSchemas, newReleases);
                        }
                    }
                } catch (Exception e) {
                    logger.warn("Unable to read OpenJDK EA OpenJDK project EA builds and from jdk.java.net", e);
                }
            });

            versions = newVersions;
            schemas = newSchemas;
            binaries = newBinaries;
            releases = newReleases;

        } catch (Exception e) {
            logger.warn("unable to read from jdk.java.net!", e);
        }
    }

    public JDKNet() {
        readData();
    }

    public JDKNet(Boolean includeProjectEABuilds) {
        this.includeProjectEABuilds = includeProjectEABuilds;
        readData();
    }

    private List<String> getListOfGABuilds(Document doc) {
        var gaURLList = new ArrayList<String>();
        doc.getElementsByClass("gap").stream()
                .filter(x-> x.toString().contains("JDK"))
                .forEach(x-> x.childNodes().forEach(ch -> {
                    var val = ch.attributes().get("href");
                    if (!val.isEmpty() && ch.toString().contains("JDK")) {
                        gaURLList.add(ch.attributes().get("href"));
                    }
                }));
        return gaURLList;
    }

    private List<Node> getListOfEABuilds(Document doc) {
        var eaURLList = new ArrayList<Node>();
        doc.select("h1").stream()
                .filter(x -> x.toString().contains("Early access"))
                .forEach(x-> x.childNodes().forEach(ch -> {
                    var val = ch.attributes().get("href");
                    if (!val.isEmpty()){
                        eaURLList.add(ch);
                    }
                }));
        return eaURLList;
    }

    private void parseJDKVersionOSBinary(
            String versionMajor, String buildType,
            List<VersionSchema> newVersions,
            List<BinarySchema> newBinaries,
            Set<OSSchema> newSchemas,
            List<InfoSchema> newReleases
    ) throws Exception {
        var version = versionMajor.replaceAll("/", "");
        var doc = parser.parseXMLtoDocument(String.format("%s%s", jdkWebURL, versionMajor));
        var realBuildNumber = "";
        var minor = "";
        var security = "";
        var buildNumberHeader = doc.getElementsByTag("h2").stream().filter(
                x-> x.toString().contains("Build")
        ).findFirst();
        if (buildNumberHeader.isPresent()){
            var parts = buildNumberHeader.get().childNode(0).toString().split(" ");

            // OpenJDK EA build come in a format of:
            // <h2>Build NUMBER (DATE)</h2>
            if (buildType.equalsIgnoreCase(VersionTypeSchema.EA())) {
                if (parts.length >= 2) {
                    realBuildNumber = parts[1];
                }
            }

            // OpenJDK project EA is unusual build number:
            // <incomplete-JDK-version>-<project-name>+<build-number>
            if (buildType.equalsIgnoreCase(VersionTypeSchema.ProjectEA())) {
                var buildParts = parts[1].replaceFirst("-", " ").split(" ");
                if (buildParts.length >= 2) {
                    minor = version;
                    version = buildParts[0];
                    var resultingBuildParts = buildParts[1].split("\\+");
                    if (resultingBuildParts.length > 1) {
                        realBuildNumber = resultingBuildParts[1];
                    } else {
                        realBuildNumber = buildParts[1];
                    }
                }
            }
        }

        // GA version info is in <h1> header
        var gaHeader = doc.getElementsByTag("h1").stream().filter(
                x-> x.text().contains("General-Availability Release")).findFirst();
        if (gaHeader.isPresent()) {
            var parts = gaHeader.get().text().split(" ");
            if (parts.length > 2) {
                var versionParts = parts[1].split("\\.");
                if (versionParts.length > 2) {
                    minor = versionParts[1];
                    security = versionParts[2];
                }
            }
        }

        var v = new VersionSchema(version, minor, security, realBuildNumber, buildType);
        readEABuildsFromPage(v, doc, newBinaries, newSchemas, newReleases);
        newVersions.add(v);
    }

    private void readEABuildsFromPage(
            VersionSchema version, Document doc,
            List<BinarySchema> newBinaries,
            Set<OSSchema> newSchemas,
            List<InfoSchema> newReleases
    ) {
        var tableContent = doc.select("table").select("tr");
        tableContent.forEach(x -> {
            var osParts = x.select("th").get(0).childNode(0).toString().toLowerCase().split("/");
            var osSchema = new OSSchema(osParts[1].strip(), osParts[0].strip());
            newSchemas.add(osSchema);
            var release = new InfoSchema(osSchema, version);
            newReleases.add(release);
            var binary = new BinarySchema(release, x.select("td").get(0).childNode(0).attributes().get("href"));
            newBinaries.add(binary);
        });
    }


    @Override
    public Stream<VersionSchema> getListOfAvailableVersions() {
        return versions.stream();
    }

    @Override
    public Stream<OSSchema> getListOfSupportedOperationSystems() {
        return schemas.stream();
    }

    public Stream<InfoSchema> getListOfReleasesBy(String version) {
        return DataCommons.getListOfReleasesBy(version, releases.stream());
    }

    @Override
    public Stream<InfoSchema> getListOfReleasesBy(String version, String os_family, String os_arch) {
        return DataCommons.getListOfReleasesBy(version, os_family, os_arch, releases.stream());
    }

    public Stream<BinarySchema> getBinariesBy(String version) {
        return DataCommons.getBinariesBy(version, binaries.stream());
    }

    @Override
    public Stream<BinarySchema> getBinariesBy(String version, String os_family, String os_arch) {
        return DataCommons.getBinariesBy(version, os_family, os_arch, binaries.stream());
    }

    @Override
    public BinarySchema getBinaryURL(String version, String os_family, String os_arch) {
        return DataCommons.getBinaryURL(version, os_family, os_arch, binaries.stream());
    }

    public List<InfoSchema> getListOfDetailedReleases() {
        var res = new ArrayList<InfoSchema>();
        getListOfAvailableVersions().toList().parallelStream().forEach(
                v -> res.addAll(getListOfReleasesBy(v.getMajor()).toList())
        );
        return res;
    }

    public List<BinarySchema> getListOfDetailedReleaseBinaries() {
        var res = new ArrayList<BinarySchema>();
        getListOfAvailableVersions().toList().parallelStream().forEach(
                v -> res.addAll(getBinariesBy(v.getMajor()).toList())
        );
        return res;
    }

    public void persistDetailedReleaseBinaries(String localStore) {
        var mapper = new ObjectMapper().writerWithDefaultPrettyPrinter();
        getListOfAvailableVersions().forEach(version -> {

            var versionJsonPath = Path.of(localStore + "/" + version.getMajor() + ".json");
            var allBins = getBinariesBy(version.getMajor()).toList();
            try {
                mapper.writeValue(versionJsonPath.toFile(), allBins);
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }

            allBins.forEach(binaryRelease -> {
                try {
                    var major = version.getMajor();
                    var os = binaryRelease.getReleaseInfo().getOSSchema();

                    var versionDirPath = Path.of(localStore + "/" + major);
                    if (Files.notExists(versionDirPath)) {
                        Files.createDirectory(versionDirPath);
                    }
                    var fmtJson = String.format(
                            "%s/%s/%s.%s.%s.json", localStore,
                            major, os.getUnderScoreAlias(),
                            version.getVersionID(), version.getVersion()
                    );
                    mapper.writeValue(Path.of(fmtJson).toFile(), binaryRelease);

                } catch (IOException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            });
        });
    }

}

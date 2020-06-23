package net.openjdk.api.datasource;

import net.openjdk.api.v1.release.binary.model.BinarySchema;
import net.openjdk.api.v1.release.information.models.InfoSchema;
import net.openjdk.api.v1.release.operating_systems.models.OSSchema;
import net.openjdk.api.v1.release.versions.models.VersionSchema;
import net.openjdk.api.v1.release.versions.models.VersionTypeSchema;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

import java.util.*;
import java.util.stream.Stream;


public class JDKNet implements DataSourceInterface {

    protected final String jdkWebURL = "https://jdk.java.net";
    protected final HTMLtoXMLRoutine parser = new HTMLtoXMLRoutine();

    protected List<BinarySchema> binaries = new ArrayList<>();
    protected List<InfoSchema> infos = new ArrayList<>();
    protected List<VersionSchema> versions = new ArrayList<>();
    protected Set<OSSchema> schemas = new HashSet<>();

    // parse `jdkWebURL` page
    // find GA and EA build references and URLs
    // parse each page
    // get binaries per OS and OS arch

    public JDKNet() throws Exception {
        var doc = parser.parseXMLtoDocument(jdkWebURL);
        getListOfGABuilds(doc).forEach(x->{
            try {
                parseJDKVersionOSBinary(x, VersionTypeSchema.GA());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        getListOfEABuilds(doc).forEach(x->{
            try {
                if (((Element) x).text().startsWith("JDK")) {
                    parseJDKVersionOSBinary(x.attributes().get("href"), VersionTypeSchema.EA());
                } else {
                    parseJDKVersionOSBinary(x.attributes().get("href"), VersionTypeSchema.ProjectEA());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
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

    private void parseJDKVersionOSBinary(String versionMajor, String buildType) throws Exception {
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
        readEABuildsFromPage(v, doc);
        versions.add(v);
    }

    private void readEABuildsFromPage(VersionSchema version, Document doc) {
        var tableContent = doc.select("table").select("tr");
        tableContent.forEach(x->{
            var osParts = x.select("th").get(0).childNode(0).toString().toLowerCase().split("/");
            var osSchema = new OSSchema(osParts[1].strip(), osParts[0].strip());
            schemas.add(osSchema);
            var release = new InfoSchema(osSchema, version);
            infos.add(release);
            var binary = new BinarySchema(release, x.select("td").get(0).childNode(0).attributes().get("href"));
            binaries.add(binary);
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

    @Override
    public Stream<InfoSchema> getListOfReleasesBy(String version, String os_family, String os_arch) {
        return DataCommons.getListOfReleasesBy(version, os_family, os_arch, infos.stream());
    }

    @Override
    public Stream<BinarySchema> getBinariesBy(String version, String os_family, String os_arch) {
        return DataCommons.getBinariesBy(version, os_family, os_arch, binaries.stream());
    }

    @Override
    public BinarySchema getBinaryURL(String version, String os_family, String os_arch) {
        return DataCommons.getBinaryURL(version, os_family, os_arch, binaries.stream());
    }

}

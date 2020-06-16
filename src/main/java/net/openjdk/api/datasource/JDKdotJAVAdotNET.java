package net.openjdk.api.datasource;

import net.openjdk.api.v1.release.binary.model.BinarySchema;
import net.openjdk.api.v1.release.information.models.InfoSchema;
import net.openjdk.api.v1.release.operating_systems.models.OSSchema;
import net.openjdk.api.v1.release.versions.models.VersionSchema;
import net.openjdk.api.v1.release.versions.models.VersionTypeSchema;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.springframework.http.HttpStatus;

import javax.xml.parsers.DocumentBuilderFactory;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;


public class JDKdotJAVAdotNET implements DataSourceInterface {

    protected final String jdkWebURL = "https://jdk.java.net/archive/";
    protected final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .followRedirects(HttpClient.Redirect.NORMAL)
            .connectTimeout(Duration.ofSeconds(60))
            .build();
    protected final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

    protected List<BinarySchema> binaries = new ArrayList<>();
    protected List<InfoSchema> infos = new ArrayList<>();
    protected List<VersionSchema> versions = new ArrayList<>();
    protected Set<OSSchema> schemas = new HashSet<>();

    protected VersionSchema parseVersionSchemaFromRaw(String version) {
        String major, minor, security, buildNumber;
        if (!version.contains("GA")) {
            // XX.YY.ZZ (build XX.YY.ZZ+SS)
            var parts = version.split(" ")[0].split("\\.");
            major = parts[0];
            minor = parts[1];
            security = parts[2];
        } else {
            // XX GA (build XX+YY)
            var parts = version.split(" ");
            major = parts[0];
            minor = "0";
            security = "0";
            // YY
        }
        buildNumber = version
                .split("\\+")[1]
                .replaceAll("\\p{P}","");
        return new VersionSchema(major, minor, security, buildNumber, VersionTypeSchema.GA());
    }

    protected void populateSingleEntity(Element i, VersionSchema versionSchema) {
        var os_family_node = i.childNode(1);

        if (os_family_node.childNodeSize() > 0 && !os_family_node.toString().contains("<td>")) {
            var os_family = os_family_node.childNode(0).toString().toLowerCase();
            if (os_family.equalsIgnoreCase("mac")) {
                os_family = "macOs";
            }
            var os_arch_node = i.childNode(3);
            var binary_node = i.childNode(5);
            var os_arch_raw = os_arch_node.childNode(0).toString();
            var osSchema = new OSSchema(
                    String.format("x%s", os_arch_raw.substring(0, 2)),
                    os_family.toLowerCase()
            );
            schemas.add(osSchema);

            var infoSchema = new InfoSchema(osSchema, versionSchema);
            infos.add(infoSchema);

            var binaryURL = binary_node.childNode(0).attributes().get("href");
            var binarySchema = new BinarySchema(infoSchema, binaryURL);
            binaries.add(binarySchema);
        }
    }

    protected VersionSchema populateVersion(Element versionNode) {
        var versionSchema = parseVersionSchemaFromRaw(
                versionNode.childNode(1)
                        .childNode(0).toString()
        );
        versions.add(versionSchema);
        return versionSchema;
    }

    protected void populateEntities(List<Element> info) {
        // but not all 6 needed, actually
        // required ones:
        // (index) 0 - OpenJDK release version
        // (index) 1 - windows release binary
        // (index) 2 - macOs release binary
        // (index) 3 - linux binary
        var versionNode = info.get(0);
        var versionSchema = populateVersion(versionNode);
        var subList = info.subList(1, 4);
        subList.forEach(i -> populateSingleEntity(i, versionSchema));
    }

    public void parseXML(String rawXML) {
        var doc = Jsoup.parse(rawXML);
        // OpenJDK releases is a table
        var jdkTable = doc.select("table");
        // selecting whatever is a table content
        var tableContent = jdkTable.select("tr");
        // estimating the size of a table
        var tableContentSize = tableContent.size();
        // each complete release consists of 6 rows of a table
        IntStream.range(0, tableContentSize/6).forEach(i -> {
            var v = tableContent.subList(i*6, (i+1)*6);
            populateEntities(v);
        });
        // the very first release (JDK 9 GA) table content is abnormal
        // the binary is available only for 1 operating system
        var abnormalVersion = tableContent.get(tableContentSize-3);
        var abnormalBinary = tableContent.get(tableContentSize-2);
        populateSingleEntity(abnormalBinary, populateVersion(abnormalVersion));
    }

    public String readHTML() throws Exception {
        var request = HttpRequest.newBuilder(new URI(jdkWebURL))
                .GET()
                .setHeader("User-Agent", "curl/7.64.1")
                .setHeader("Accept", "*/*")
                .build();
        var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != HttpStatus.OK.value()) {
            throw new Exception(String.format("Bad response code: %s\nBody: %s",
                    response.statusCode(), response.body()));
        }

        return response.body();
    }

    public JDKdotJAVAdotNET() {}

    @Override
    public List<VersionSchema> getListOfAvailableVersions() {
        return versions;
    }

    @Override
    public List<OSSchema> getListOfSupportedOperationSystems() {
        return new ArrayList<>(schemas);
    }

    @Override
    public List<InfoSchema> getListOfReleases() {
        return infos;
    }

    @Override
    public List<BinarySchema> getListOfBinaries() {
        return binaries;
    }

    @Override
    public Stream<BinarySchema> getBinariesPerVersion(String version) {
        return binaries.parallelStream().filter(
                x-> x.getReleaseInfo().getVersionSchema().isMatch(version)
        );
    }

    @Override
    public Stream<BinarySchema> getBinariesPerVersionAndOS(String version, String os_family) {
        return binaries.parallelStream().filter(
                x->{
                    var rel = x.getReleaseInfo();
                    return rel.getOSSchema().isMatch(os_family) &&
                            rel.getVersionSchema().isMatch(version);
                }
        );
    }

    @Override
    public BinarySchema getBinary(String version, String os_family, String os_arch) {
        var res = binaries.parallelStream().filter(
                x->{
                    var rel = x.getReleaseInfo();
                    return rel.getOSSchema().isMatch(os_family) &&
                            rel.getVersionSchema().isMatch(version);
                }
        ).collect(Collectors.toList());
        if (res.size() > 0) {
            return res.get(0);
        }
        return null;
    }
}

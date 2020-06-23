package net.openjdk.api.datasource;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.http.HttpStatus;

import javax.xml.parsers.DocumentBuilderFactory;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;


public class HTMLtoXMLRoutine {

    protected final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .followRedirects(HttpClient.Redirect.NORMAL)
            .connectTimeout(Duration.ofSeconds(60))
            .build();
    protected final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

    public Document parseXMLtoDocument(String webURL) throws Exception {
        return Jsoup.parse(readHTMLPage(webURL));
    }

    private String readHTMLPage(String jdkWebURL) throws Exception {
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

}

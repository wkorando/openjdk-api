package net.openjdk.cli;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.openjdk.api.datasource.JDKNet;

import java.io.File;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        var jdk = new JDKNet(true);
        var mapper = new ObjectMapper().writerWithDefaultPrettyPrinter();
        mapper.writeValue(new File("index.json"), jdk.getListOfDetailedReleaseBinaries());
//        System.out.print(mapper.writeValueAsString(jdk.getListOfDetailedReleaseBinaries()));
    }

}

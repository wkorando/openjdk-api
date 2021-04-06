package net.openjdk.cli;

import net.openjdk.api.datasource.JDKNet;


public class Main {

    public static void main(String[] args) {
        var jdk = new JDKNet(true);
        jdk.persistDetailedReleaseBinaries("api");
    }

}

package net.openjdk.api.v1.release.versions.models;

import java.util.Arrays;
import java.util.List;

public class VersionTypeSchema {

    protected static final String GA = "ga";
    protected static final String EA = "ea";

    public static List<String> getTypes() {
        return Arrays.asList(GA, EA);
    }

    public static String GA() {
        return GA;
    }

    public static String EA() {
        return EA;
    }

}

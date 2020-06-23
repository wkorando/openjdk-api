package net.openjdk.api.v1.release.versions.models;

import java.util.Arrays;
import java.util.List;

public class VersionTypeSchema {

    // OpenJDK general availability
    protected static final String GA = "jdk-ga";
    // OpenJDK early access
    protected static final String EA = "jdk-ea";
    // OpenJDK project early access
    protected static final String PEA = "jdk-project-pea";

    public static List<String> getTypes() {
        return Arrays.asList(GA, EA, PEA);
    }

    public static String GA() {
        return GA;
    }

    public static String EA() {
        return EA;
    }

    public static String ProjectEA() {
        return PEA;
    }

}

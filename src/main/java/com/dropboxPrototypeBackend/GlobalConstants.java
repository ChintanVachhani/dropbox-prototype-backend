package com.dropboxPrototypeBackend;

import java.nio.file.Paths;

public class GlobalConstants {
    //public static final String origin = "http://localhost:3000";
    public static final String origin = "*";
    public static final String boxPath = Paths.get(".", "box").toAbsolutePath().normalize().toString();
    public static final String server = "localhost";
    public static final int port = 8080;
}

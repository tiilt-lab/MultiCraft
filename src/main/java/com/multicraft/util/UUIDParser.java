package com.multicraft.util;

import java.util.UUID;

public class UUIDParser {

    public static UUID parse(String toParse) {
        try {
            String plain = toParse.replaceAll("[^A-Za-z0-9]", "");
            String formatted = String.format("%s-%s-%s-%s-%s", plain.substring(0, 8), plain.substring(8, 12), plain.substring(12, 16), plain.substring(16, 20), plain.substring(20));
            return UUID.fromString(formatted);
        } catch (Exception ignored) {
            return new UUID(0L, 0L);
        }
    }

}

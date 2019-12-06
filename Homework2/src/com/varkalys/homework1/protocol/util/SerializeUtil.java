package com.varkalys.homework1.protocol.util;

public final class SerializeUtil {

    private final static String DELIMITER = "\n";

    public static byte[] serializeAttributes(Object... attributes) {
        StringBuilder builder = new StringBuilder();
        for (Object attribute : attributes) {
            builder.append(attribute);
            builder.append(DELIMITER);
        }
        return builder.toString().getBytes();
    }

    public static String[] deserializeAttributes(byte[] attributeBytes) {
        String attributesStr = new String(attributeBytes).trim();
        return attributesStr.split(DELIMITER);
    }
}

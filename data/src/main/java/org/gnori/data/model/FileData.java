package org.gnori.data.model;

public record FileData(
        String id,
        String name,
        FileType type
) {

    public String suffix() {

        return switch (type) {
            case PHOTO -> type.getSuffix();
            case DOCUMENT -> extractSuffix(name);
        };
    }

    public String prefix() {
        return type.getPrefix();
    }

    private String extractSuffix(String name) {

        final int indexPoint = name.indexOf(".");

        return indexPoint != -1
                ? name.substring(indexPoint)
                : "";
    }
}

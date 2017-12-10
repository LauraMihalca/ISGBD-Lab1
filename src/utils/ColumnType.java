package utils;

import java.util.Arrays;

/**
 * Created by maria-roxana on 23.11.2017.
 */
public enum ColumnType {
    VARCHAR("varchar"),
    BIGINT("bigint"),
    INT("int");

    private String value;

    private ColumnType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static ColumnType getByValue(String value) {
        return Arrays.stream(ColumnType.values())
                .filter(ct -> ct.getValue().equals(value))
                .findFirst()
                .orElse(null);
    }
}

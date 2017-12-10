package domain.vo;

import utils.ColumnType;

/**
 * Created by maria-roxana on 23.11.2017.
 */
public class ColumnVO {
    private String name;
    private Long length;
    private ColumnType type;
    private boolean isPrimaryKey;

    public ColumnVO() {
    }

    public ColumnVO(String name, ColumnType type, Long length, boolean isPrimaryKey) {
        this.name = name;
        this.length = length;
        this.type = type;
        this.isPrimaryKey = isPrimaryKey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getLength() {
        return length;
    }

    public void setLength(Long length) {
        this.length = length;
    }

    public ColumnType getType() {
        return type;
    }

    public void setType(ColumnType type) {
        this.type = type;
    }

    public boolean isPrimaryKey() {
        return isPrimaryKey;
    }

    public void setPrimaryKey(boolean primaryKey) {
        isPrimaryKey = primaryKey;
    }
}

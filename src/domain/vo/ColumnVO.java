package domain.vo;

/**
 * Created by maria-roxana on 23.11.2017.
 */
public class ColumnVO {
    private String name;
    private Long length;
    private boolean isPrimaryKey;

    public ColumnVO() {
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

    public boolean isPrimaryKey() {
        return isPrimaryKey;
    }

    public void setPrimaryKey(boolean primaryKey) {
        isPrimaryKey = primaryKey;
    }
}

package domain.vo;

import java.util.List;

/**
 * Created by maria-roxana on 23.11.2017.
 * The class is used for operations on tables (Create and Drop)
 */
public class TableOperationVO {
    private String name;
    private List<ColumnVO> columns;

    public TableOperationVO() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ColumnVO> getColumns() {
        return columns;
    }

    public void setColumns(List<ColumnVO> columns) {
        this.columns = columns;
    }
}

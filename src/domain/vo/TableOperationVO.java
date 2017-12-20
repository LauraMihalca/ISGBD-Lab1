package domain.vo;

import utils.TableOperation;

import java.util.List;

/**
 * Created by maria-roxana on 23.11.2017.
 * The class is used for operations on tables (Create and Drop)
 */
public class TableOperationVO {
    private String name;
    private TableOperation tableOperation;
    private List<ColumnVO> columns;

    public TableOperationVO() {
    }

    public TableOperation getTableOperation() {
        return tableOperation;
    }

    public void setTableOperation(TableOperation tableOperation) {
        this.tableOperation = tableOperation;
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

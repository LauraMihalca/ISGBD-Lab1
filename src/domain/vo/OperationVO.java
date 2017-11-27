package domain.vo;

import utils.DataOperation;

import java.util.List;

/**
 * Created by maria-roxana on 23.11.2017.
 */
public class OperationVO {
    private String tableName;
    private DataOperation operation;
    private List<SelectionCriteriaVO> criteria; // this list will contain the data(pairs<Key,Value>) from where condition
    private List<SelectionCriteriaVO> newValues; // this list will be used for update.
    // It will contain the pairs<Key,Value> from set statement.
    // e.g <"name","Vlad"> means set name="Vlad"

    public OperationVO() {
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public DataOperation getOperation() {
        return operation;
    }

    public void setOperation(DataOperation operation) {
        this.operation = operation;
    }

    public List<SelectionCriteriaVO> getCriteria() {
        return criteria;
    }

    public void setCriteria(List<SelectionCriteriaVO> criteria) {
        this.criteria = criteria;
    }

    public List<SelectionCriteriaVO> getNewValues() {
        return newValues;
    }

    public void setNewValues(List<SelectionCriteriaVO> newValues) {
        this.newValues = newValues;
    }
}

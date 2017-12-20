package domain.vo;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import com.sun.org.apache.xpath.internal.operations.Operation;
import utils.ColumnType;
import utils.DataOperation;
import utils.TableOperation;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static utils.ColumnType.getByValue;
import static utils.DataOperation.DELETE;

/**
 * Created by user on 05.12.2017.
 * In BD Tables have format <K,V>
 * The value fields are separated by '*'
 */
public class CommonDao {
    private Connection conn;

    public CommonDao() throws SQLException {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setUser("root");
        dataSource.setPassword("root");
        dataSource.setServerName("localhost");
        dataSource.setDatabaseName("isgbd1");
        dataSource.setPortNumber(3306);
        conn = dataSource.getConnection();
    }

    public boolean existsTable(String tableName) throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT COUNT(`KEY`) FROM `tables` where `VALUE` like '" + tableName + "'");
        rs.next();

        boolean existsTable = rs.getLong(1) > 0;
        rs.close();
        stmt.close();
        return existsTable;
    }

    public void executeRowsOperation(OperationVO operation) throws SQLException {
        if (!existsTable(operation.getTableName())) {
            System.out.print("table not found");
            return;
        }
        if (!isValidType(operation)) {
            System.out.print("Invalid condition");
            return;
        }
        switch (operation.getOperation()) {
            case ADD:
                processAdd(operation);
                return;
            case UPDATE:
                processUpdate(operation);
                return;
            case DELETE:
                processDelete(operation);
                return;
        }
    }

    private void processAdd(OperationVO operationVO) throws SQLException {
        Long maxIdForPk = getMaxKeyFromTable(operationVO.getTableName());

        Statement stmt = conn.createStatement();
        StringBuilder stmtText = new StringBuilder("INSERT INTO `value`(`key`,pk_value,`value`) values(");
        stmtText.append(getTableId(operationVO.getTableName()))
                .append(",")
                .append(maxIdForPk + 1)
                .append(",'")
                .append(getInsertTextForValues(operationVO))
                .append("')");

        stmt.executeUpdate(stmtText.toString());

        stmt.close();
    }

    private String getInsertTextForValues(OperationVO operationVO) throws SQLException {
        String concatenatedValues = "";
        for (ColumnVO columnVO : getAllColumnVOs(getTableId(operationVO.getTableName()))) {
            String columnName = getValueForColumn(null, columnVO.getName(), operationVO);
            if (!isPrimaryKey(getTableId(operationVO.getTableName()), columnName)) {
                concatenatedValues += ("".equals(concatenatedValues) ? "" : "*") + columnName;
            }
        }
        return concatenatedValues;
    }

    private void processUpdate(OperationVO operation) throws SQLException {
        Long tableId = getTableId(operation.getTableName());
        Statement stmt = conn.createStatement();
        List<ColumnVO> allColumns = getAllColumnVOs(tableId);
        List<ValueVO> allValues = getDataByCriteria(operation);

        for (ValueVO valueVO : allValues) { // all lines matching criteria
            StringBuilder stmtText = new StringBuilder("UPDATE `value` SET `value`='");
            boolean isFirst = true;
            for (ColumnVO columnVO : allColumns) {
                stmtText.append(isFirst ? "" : "*");
                isFirst = false;
                stmtText.append(getValueForColumn(valueVO, columnVO.getName(), operation));
            }
            stmtText.append("' WHERE `key`=")
                    .append(tableId)
                    .append(" and pk_value=")
                    .append(valueVO.getPrimaryKey().getValue());
            stmt.executeUpdate(stmtText.toString());
        }
        stmt.close();
    }

    private String getValueForColumn(ValueVO valueVO, String columnName, OperationVO operation) throws SQLException {
        if (isPrimaryKey(getTableId(operation.getTableName()), columnName)) {
            return valueVO.getPrimaryKey().getValue();
        }
        SelectionCriteriaVO newValue = getNewValueForColumn(columnName, operation);
        if (newValue == null) {
            return valueVO.getOtherValues().stream()
                    .filter(c -> c.getKey().equalsIgnoreCase(columnName))
                    .map(SelectionCriteriaVO::getValue)
                    .findFirst()
                    .orElse(null);
        }

        //keep the old value
        return newValue.getValue();
    }

    private SelectionCriteriaVO getNewValueForColumn(String columnName, OperationVO operationVO) {
        return operationVO.getNewValues().stream()
                .filter(sc -> sc.getKey().equalsIgnoreCase(columnName))
                .findFirst()
                .orElse(null);
    }

    private void processDelete(OperationVO operation) throws SQLException {
        Long tableId = getTableId(operation.getTableName());
        StringBuilder stmtText = new StringBuilder("DELETE FROM `value` where `key` ="
                + tableId + " AND ");
        stmtText.append(getWhereConditionDependingPk(tableId, operation));
        System.out.println(stmtText);

        Statement stmt = conn.createStatement();
        stmt.executeUpdate(stmtText.toString());
        stmt.close();
    }

    private String getWhereConditionDependingPk(Long tableId, OperationVO operation) throws SQLException {
        if (operation.getCriteria() == null) return "true";

        Long valueIndex = getValuePosition(tableId, operation.getCriteria().getKey());

        if (isPrimaryKey(tableId, operation.getCriteria().getKey())) {
            return " pk_value=" + operation.getCriteria().getValue();
        }

        return new StringBuilder()
                .append(" SUBSTRING_INDEX(SUBSTRING_INDEX(`value`,'*',")
                .append(valueIndex)
                .append("),'*',-1) = '")
                .append(operation.getCriteria().getValue())
                .append("'")
//                .append('`')
//                .append(operation.getCriteria().getKey())
//                .append("` = ")
//                .append(operation.getCriteria().getValue())
                .toString();
    }

    // not done,for  condition key=value  it will check that value is by expected type
    private boolean isValidType(OperationVO operationVO) throws SQLException {
        List<String> columnsList = getColumns(getTableId(operationVO.getTableName()));
        SelectionCriteriaVO criteria = operationVO.getCriteria();
//        for (int i = 0; i < columnsList.size(); i++) {
//            String currentColumn = columnsList.get(i);
//            if (criteria.getKey().equals(currentColumn.substring(0, currentColumn.indexOf("#")))) {
////                if (criteria.getValue().equals(currentColumn.substring(currentColumn.indexOf("#"), currentColumn.lastIndexOf("#")))) {
//                if (criteria.getValue().length() <= Integer.parseInt(currentColumn.substring(currentColumn.lastIndexOf("#"), currentColumn.length()))) {
//                    return true;
//                }
////                }
//            }
//        }
        return true;
    }

    private Long getMaxKeyFromTable(String tableName) throws SQLException {
        Long tableId = getTableId(tableName);
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT MAX(`PK_VALUE`) FROM `value` WHERE `KEY`=" + tableId);
        rs.next();
        Long maxId = rs.getLong(1);
        rs.close();
        stmt.close();
        return maxId;
    }

    private Long getTableId(String tableName) throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT `key` FROM `tables` WHERE `VALUE` LIKE '" + tableName + "'");
        rs.next();

        Long maxId = rs.getLong(1);
        rs.close();
        stmt.close();
        return maxId;
    }


    private Long getValuePosition(Long tableId, String columnName) throws SQLException {
        Long value = 0L;
        for (String column : getColumns(tableId)) {
            value++;
            if (column.split("#")[0].equalsIgnoreCase(columnName)) break;
        }
        return value;
    }


    private List<String> getColumns(Long tableId) throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT value FROM `column` WHERE `key` =" +
                tableId);
        rs.next();

        String allColumns = rs.getString(1);
        rs.close();
        stmt.close();
        List<String> columns = new ArrayList<>();
        if (allColumns == null) return columns;

        columns.addAll(Arrays.asList(allColumns.split("\\*")));
        return columns;
    }

    private boolean isPrimaryKey(Long tableId, String columnName) throws SQLException {
        for (String column : getColumns(tableId)) {
            String[] columnAttributes = column.split("#");
            if (columnAttributes[0].equalsIgnoreCase(columnName)) {
                return columnAttributes[3].equals("1");
            }
        }
        return false;
    }

    private List<ValueVO> getDataByCriteria(OperationVO operation) throws SQLException {
        Long tableId = getTableId(operation.getTableName());
        StringBuilder stmtText = new StringBuilder("SELECT pk_value, value FROM `value` where `key` ="
                + tableId + " AND ");
        stmtText.append(getWhereConditionDependingPk(tableId, operation));

        List<ValueVO> valueVOS = new ArrayList<>();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(stmtText.toString());
        while (rs.next()) {
            ValueVO valueVO = new ValueVO();
            Long primaryKeyValue = rs.getLong(1);
            String[] otherValues = rs.getString(2).split("\\*");
            int i = 0;
            for (ColumnVO columnVO : getAllColumnVOs(tableId)) {
                if (columnVO.isPrimaryKey()) {
                    valueVO.setPrimaryKey(new SelectionCriteriaVO(columnVO.getName(), primaryKeyValue.toString()));
                } else {
                    valueVO.getOtherValues().add(new SelectionCriteriaVO(columnVO.getName(), otherValues[i]));
                    i++;
                }
            }
            valueVOS.add(valueVO);
        }

        rs.close();
        stmt.close();

        return valueVOS;
    }

    private ColumnVO toColumnVO(String fullColumn) {
        String[] columnAttributes = fullColumn.split("#");
        return new ColumnVO(columnAttributes[0],
                getByValue(columnAttributes[1]),
                Long.parseLong(columnAttributes[2]),
                columnAttributes[3].equals("1"));
    }

    private List<ColumnVO> getAllColumnVOs(Long tableId) throws SQLException {
        return getColumns(tableId).stream()
                .map(this::toColumnVO)
                .collect(Collectors.toList());
    }

    public void executeTableOperation(TableOperationVO tableOperationVO) throws SQLException {
        switch (tableOperationVO.getTableOperation()) {
            case CREATE: {
                processCreateTable(tableOperationVO);
                return;
            }
            case DROP: {
                processDropTable(tableOperationVO);
                return;
            }
        }
    }

    private void processDropTable(TableOperationVO tableOperationVO) throws SQLException {
        if (!existsTable(tableOperationVO.getName())) {
            System.out.println("The table " + tableOperationVO.getName() + " not exists");
            return;
        }
        OperationVO operationVO = new OperationVO();
        operationVO.setTableName(tableOperationVO.getName());
        operationVO.setOperation(DELETE); // delete all records from given table

        Long tableId = getTableId(tableOperationVO.getName());
        Statement stmt = conn.createStatement();
        stmt.executeUpdate("DELETE FROM `column` WHERE `key` =" + tableId);
        stmt.executeUpdate("DELETE FROM `tables` WHERE `key` =" + tableId);

        stmt.close();
    }

    private void processCreateTable(TableOperationVO operationVO) throws SQLException {
        if (existsTable(operationVO.getName())) {
            System.out.println("Table already exists!");
        }

        Statement stmt = conn.createStatement();
        stmt.executeUpdate("INSERT INTO `tables`(`key`) VALUE ('" + operationVO.getName() + "')");
        Long tableId = getTableId(operationVO.getName());

        for (ColumnVO columnVO : operationVO.getColumns()) {
            stmt.executeUpdate("INSERT INTO `columns`(`key`,`value`) VALUES(" + tableId + "," +
                    columnVO.getName() + "*" + columnVO.getType() + "*" + columnVO.getLength() + "*"
                    + (columnVO.isPrimaryKey() ? "1" : "0") +
                    ")");
        }
        stmt.close();

    }
}

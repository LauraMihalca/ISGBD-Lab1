package domain.vo;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import com.sun.org.apache.xpath.internal.operations.Operation;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        dataSource.setPortNumber(3306); //
        conn = dataSource.getConnection();
        Statement stmt = conn.createStatement();
        // used to test if update works it will be deleted
//        int rs = stmt.executeUpdate("INSERT into value values(2,'KK')"); //"SELECT COUNT(KEY) FROM tables");
//        rs.close();
        stmt.close();
//        conn.close();

    }

    public boolean existsTable(String tableName) throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT COUNT('KEY') FROM tables where VALUE like '" + tableName + "'");
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
            case UPDATE: {
                return;
            }
            case DELETE:
                processDelete(operation);
        }

    }

    private void processUpdate(OperationVO operation) throws SQLException {
        Long tableId = getTableId(operation.getTableName());
        Statement stmt = conn.createStatement();


        StringBuilder stmtText = new StringBuilder("UPDATE `data` SET value='");


        stmtText.append(getWhereConditionDependingPk(tableId, operation));
        stmt.executeUpdate(stmtText.toString());
        stmt.close();
    }

    private void processDelete(OperationVO operation) throws SQLException {
        Long tableId = getTableId(operation.getTableName());
        StringBuilder stmtText = new StringBuilder("DELETE FROM `data` where `key` ="
                + tableId + " AND ");
        stmtText.append(getWhereConditionDependingPk(tableId, operation));
        System.out.println(stmtText);

        Statement stmt = conn.createStatement();
        stmt.executeUpdate(stmtText.toString());
        stmt.close();
    }

    private String getWhereConditionDependingPk(Long tableId, OperationVO operation) throws SQLException {
        Long valueIndex = getValuePosition(tableId, operation.getCriteria().getKey());

        if (isPrimaryKey(tableId, operation.getCriteria().getKey())) {
            return "pk_value=" + operation.getCriteria().getValue();
        }

        StringBuilder stmtText = new StringBuilder(
                "SUBSTRING_INDEX(SUBSTRING_INDEX(`value`,'*'," + valueIndex + "),'*',-1) = '"
                        + operation.getCriteria().getValue() + "'");

        stmtText.append('`')
                .append(operation.getCriteria().getKey())
                .append("` = ")
                .append(operation.getCriteria().getValue());
        return stmtText.toString();
    }


    // not done,for  condition key=value  it will check that value is by expected type
    private boolean isValidType(OperationVO operationVO) throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT VALUE FROM `column` WHERE `KEY`= " +
                getTableId(operationVO.getTableName()));
        rs.next();

        Long maxId = rs.getLong(1);
        rs.close();
        stmt.close();
        return true;
    }

    //
    private Long getMaxKeyFromTable(String tableName) throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT MAX('KEY') FROM `" + tableName + "`");
        rs.next();

        Long maxId = rs.getLong(1);
        rs.close();
        stmt.close();
        return maxId;
    }

    private Long getTableId(String tableName) throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT `key` FROM `tables` WHERE VALUE LIKE `" + tableName + "`");
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
            if (column.split("#")[0].equals(columnName)) break;
        }
        return value;
    }


    private List<String> getColumns(Long tableId) throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT value FROM `column` WHERE `key` =" + tableId);
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
            if (columnAttributes[0].equals(columnName)) {
                return columnAttributes[3].equals("1");
            }
        }
        return false;
    }


    private List<String> getDataByCriteria(OperationVO operation) {
        return null;
    }
}

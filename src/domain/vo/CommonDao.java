package domain.vo;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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

        Long max = getMaxKeyFromTable("");
        StringBuilder stmtText = new StringBuilder("DELETE FROM `data` where" +
                "SUBSTRING_INDEX(SUBSTRING_INDEX(`value`,'*'," + max + "),'*',1) = '" + operation.getCriteria().get(0).getValue() + "'");


        for (SelectionCriteriaVO selectionCriteria : operation.getCriteria()) {
            stmtText.append('`').append(selectionCriteria.getKey()).append("` = ").append(selectionCriteria.getValue());
        }
        System.out.println(stmtText);

        Statement stmt = conn.createStatement();
        stmt.executeUpdate(stmtText.toString());
        stmt.close();
    }

    // not done,for  condition key=value  it will check that value is by expected type
    private boolean isValidType(OperationVO operationVO) throws SQLException {
        for (SelectionCriteriaVO selectionCriteriaVO : operationVO.getCriteria()) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT VALUE FROM `column` WHERE KEY ??????");
            rs.next();

            Long maxId = rs.getLong(1);
            rs.close();
            stmt.close();
        }
        return true;
    }

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
        ResultSet rs = stmt.executeQuery("SELECT `key` FROM `tables` WHERE VALUE LIKE " + tableName);
        rs.next();

        Long maxId = rs.getLong(1);
        rs.close();
        stmt.close();
        return maxId;
    }

}

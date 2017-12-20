package Repository;

import domain.vo.ColumnVO;
import domain.vo.OperationVO;
import domain.vo.SelectionCriteriaVO;
import domain.vo.TableOperationVO;
import utils.ColumnType;
import utils.ReservedWord;

import java.util.*;

import static utils.DataOperation.*;
import static utils.TableOperation.CREATE;
import static utils.TableOperation.DROP;

/**
 * Created by Laura on 11/19/2017
 */

public class Repository {

    public Repository() {

    }

    public boolean checkType(String value) {
        for (ColumnType c : ColumnType.values()) {
            if (c.name().equalsIgnoreCase(value)) {
                return true;
            }
        }
        return false;
    }

    public boolean validateType(String value) {
        try {
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    /*
    * de forma:
    * CREATE TABLE TABLE_NAME
    * DROP TABLE TABLE_NAME
    * */
    public TableOperationVO parseTableCommand(String command) throws Exception {
        StringTokenizer parts = new StringTokenizer(command, " ");
        String operation = parts.nextToken();
        if (operation.toUpperCase().equals("CREATE"))
            return parseCreateCommand(command);
        if (operation.toUpperCase().equals("DROP"))
            return parseDropCommand(command);
        System.out.println("NOT FOUND COMMAND");
        return null;
    }

    private TableOperationVO parseCreateCommand(String command) throws Exception {
        Map<String, Map<String, Long>> columnsHash = new HashMap<>();
        command = command.replaceAll(", ", ",");
        command = command.replaceAll(" \\(", "\\(");
        command = command.replaceAll("\\( ", "\\(");
        StringTokenizer parts = new StringTokenizer(command, " ");
        String operation = parts.nextToken() + " " + parts.nextToken();
        String tableName = parts.nextToken();
        tableName = tableName.substring(0, tableName.indexOf("("));
        String columns = command.substring(command.indexOf("(") + 1, command.lastIndexOf(")"));
        StringTokenizer columnsTokenizer = new StringTokenizer(columns, ",");
        String primaryKey = "";

        List<ColumnVO> columnVOS = new ArrayList<>();
        TableOperationVO operationVO = new TableOperationVO();
        operationVO.setTableOperation(CREATE);
        operationVO.setName(tableName);
        while (columnsTokenizer.hasMoreTokens()) {
            String column = columnsTokenizer.nextToken();
            String columnName = column.substring(0, column.indexOf(" "));
            Map<String, Long> typeAndSize = new HashMap<>();
            String type = column.substring(column.indexOf(" ") + 1, column.indexOf("("));
            if (!checkType(type)) {
                System.err.println("Please enter a valid type for " + columnName + ".");
                return null;
            }
            Long size = Long.parseLong(column.substring(column.indexOf("(") + 1, column.indexOf(")")));
            typeAndSize.put(type, size);
            if (!primaryKey.equals("")) {
                System.out.println("Only one PRIMARY KEY accepted.");
            } else {
                if (column.substring(column.lastIndexOf(" ") + 1, column.length()).equalsIgnoreCase(ReservedWord.PRIMARY_KEY.toString())) {
                    primaryKey = columnName;
                } else {
                    if (!column.substring(column.lastIndexOf(" ") + 1, column.length()).equals("")) {
                        System.out.println(column.substring(column.lastIndexOf(" ") + 1, column.length()) + " not supported/known. Please use Primary_key.");
                        return null;
                    }
                }
            }
            ColumnVO columnVO =new ColumnVO();
            columnVO.setName(columnName);
            columnVO.setLength(typeAndSize.values().iterator().next());
//            columnVO.setType(typeAndSize.keySet().iterator().next()); aici trebuie mapate valorile
            columnsHash.put(columnName, typeAndSize);
        }
//        columnVOS.add()
        TableOperationVO tableOperationVO = new TableOperationVO();
        tableOperationVO.setName(tableName);
        tableOperationVO.setTableOperation(CREATE);

        System.out.println("Operation: " + operation + "\n" +
                "Table name: " + tableName + "\n" +
                "Columns, Types and Sizes: " + columnsHash.toString() + "\n" +
                "Primary Key: " + primaryKey);
        return operationVO;
    }

    private TableOperationVO parseDropCommand(String command) {
        StringTokenizer parts = new StringTokenizer(command, " ");
        String operation = parts.nextToken() + " " + parts.nextToken();
        String tableName = parts.nextToken();
        TableOperationVO operationVO = new TableOperationVO();
        operationVO.setName(tableName);
        operationVO.setTableOperation(DROP);
//        operationVO.setTableOperation(DROP);
        System.out.println("Operation: " + operation + "\n" +
                "Table name: " + tableName);
        return operationVO;
    }

    /*
    * de forma:
    * INSERT INTO TABLE_NAME(COL1, COL2, ...) VALUES (VAL1, VAL2, ...)
    */
    public OperationVO parseInsertCommand(String command) throws Exception {
        command = command.replaceAll(", ", ",");
        command = command.replaceAll(" \\(", "\\(");
        command = command.replaceAll("\\( ", "\\(");
        List<String> columnsArray = new ArrayList<>();
        List<String> valuesArray = new ArrayList<>();
        StringTokenizer parts = new StringTokenizer(command, " ");
        String operation = parts.nextToken() + " " + parts.nextToken();
        String tableNameAndColumns = parts.nextToken();
        String values = parts.nextToken();
        String columns = tableNameAndColumns.substring(tableNameAndColumns.indexOf("("), tableNameAndColumns.indexOf(")"));
        columns = columns.replaceAll("\\(", "");
        StringTokenizer columnsTokenizer = new StringTokenizer(columns, ",");
        while (columnsTokenizer.hasMoreTokens()) {
            columnsArray.add(columnsTokenizer.nextToken());
        }
        String tableName = tableNameAndColumns.substring(0, tableNameAndColumns.indexOf("("));
        values = values.substring(values.indexOf("("), values.indexOf(")"));
        values = values.replaceAll("\\(", "");
        StringTokenizer valuesTokenizer = new StringTokenizer(values, ",");
        while (valuesTokenizer.hasMoreTokens()) {
            valuesArray.add(valuesTokenizer.nextToken());
        }

        if (columnsArray.size() != valuesArray.size())
            throw new Exception("Number of columns different from number of values.");

        OperationVO operationVO = new OperationVO();
        operationVO.setTableName(tableName);
        operationVO.setOperation(ADD);
        List<SelectionCriteriaVO> newValues = new ArrayList<>();
        for (int i = 0; i < columnsArray.size(); i++) {
            newValues.add(new SelectionCriteriaVO(columnsArray.get(i), valuesArray.get(i)));
        }
        operationVO.setNewValues(newValues);
        System.out.println("Operation: " + operation + "\n" +
                "Table: " + tableName + "\n" +
                "Columns: " + columnsArray.toString() + "\n" +
                "Values: " + valuesArray.toString());
        return operationVO;
    }

    /*
    * de forma:
    * DELETE FROM TABLE_NAME WHERE CONDITION
    * */
    public OperationVO parseDeleteCommand(String command) throws Exception {
        OperationVO deleteOperation = new OperationVO();
        deleteOperation.setOperation(DELETE);

        StringTokenizer parts = new StringTokenizer(command, " ");
        String operation = parts.nextToken() + " " + parts.nextToken();
        String tableName = parts.nextToken();
        String keyword2 = parts.nextToken();
        String condition = parts.nextToken(); // de forma id=3, nu id = 3
        String[] conditionParts = condition.split("=");

        if (conditionParts.length != 2) throw new Exception("Invalid condition");

        System.out.println("Operation: " + operation + "\n" +
                "Table name: " + tableName + "\n" +
                "Keyword: " + keyword2 + "\n" +
                "Condition: " + condition);

        SelectionCriteriaVO criteria = new SelectionCriteriaVO();
        criteria.setKey(conditionParts[0]);
        criteria.setValue(conditionParts[1]);
        deleteOperation.setTableName(tableName);
        deleteOperation.setCriteria(criteria);
        return deleteOperation;
    }

    /*
    * de forma:
    * UPDATE TABLE_NAME SET (COL1=VAL1, COL2=VAL2, ...) WHERE CONDITION
    * Conditia nu am reusit sa o sparg pt ca trebuie sa ma gandesc la o modalitate prin care sa acopar toate posibilitatile (=,>,<,>=,<=,==,!=)
    * */
    public OperationVO parseUpdateCommand(String command) {
        Map<String, String> updatedPairs = new HashMap<>();
        command = command.replaceAll("\\( ", "\\(");
        command = command.replaceAll(", ", ",");
        command = command.replaceAll(";", "");
        StringTokenizer parts = new StringTokenizer(command, " ");
        String operation = parts.nextToken();
        String tableName = parts.nextToken();
        String keyword = parts.nextToken();
        String columns = parts.nextToken();
        List<SelectionCriteriaVO> newValues = new ArrayList<>();
        columns = columns.substring(columns.indexOf("(") + 1, columns.indexOf(")"));
        StringTokenizer columnsTokenizer = new StringTokenizer(columns, ",");
        while (columnsTokenizer.hasMoreTokens()) {
            String column = columnsTokenizer.nextToken();
            updatedPairs.put(column.substring(0, column.indexOf("=")), column.substring(column.indexOf("=") + 1, column.length()));
            newValues.add(new SelectionCriteriaVO(column.substring(0, column.indexOf("=")), column.substring(column.indexOf("=") + 1, column.length())));
        }
        String keyword2 = parts.nextToken();
        String condition = parts.nextToken(); // de forma id=3, nu id = 3
        String key = condition.substring(0, condition.indexOf("="));
        String value = condition.substring(condition.indexOf("=") + 1, condition.length());
        OperationVO operationVO = new OperationVO();
        operationVO.setTableName(tableName);
        operationVO.setOperation(UPDATE);
        operationVO.setNewValues(newValues);
        operationVO.setCriteria(new SelectionCriteriaVO(key, value));
        System.out.println("Operation: " + operation + "\n" +
                "Table name: " + tableName + "\n" +
                "Keyword: " + keyword + "\n" +
                "Updated Pairs: " + updatedPairs.toString() + "\n" +
                "Keyword: " + keyword2 + "\n" +
                "Key: " + key + "\n" +
                "Value: " + value);
        return operationVO;
    }
}

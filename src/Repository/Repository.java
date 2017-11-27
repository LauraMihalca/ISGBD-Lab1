package Repository;

import domain.vo.OperationVO;
import domain.vo.SelectionCriteriaVO;

import java.util.*;

import static utils.DataOperation.DELETE;

/**
 * Created by Laura on 11/19/2017
 */

public class Repository {

    public Repository() {

    }

    /*
    * de forma:
    * CREATE TABLE TABLE_NAME
    * DROP TABLE TABLE_NAME
    * */
    public void parseTableCommand(String command) {
        command = command.replaceAll(";", "");
        StringTokenizer parts = new StringTokenizer(command, " ");
        String operation = parts.nextToken();
        if (operation.toUpperCase().equals("CREATE"))
            parseCreateCommand(command);
        if (operation.toUpperCase().equals("DROP"))
            parseDropCommand(command);
    }

    private void parseCreateCommand(String command) {
        Map<String, Map<String, Integer>> columnsHash = new HashMap<>();
        command = command.replaceAll(", ", ",");
        command = command.replaceAll(" \\(", "\\(");
        command = command.replaceAll("\\( ", "\\(");
        StringTokenizer parts = new StringTokenizer(command, " ");
        String operation = parts.nextToken() + " " + parts.nextToken();
        String tableName = parts.nextToken();
        tableName = tableName.substring(0, tableName.indexOf("("));
        String columns = command.substring(command.indexOf("(") + 1, command.lastIndexOf(")"));
        StringTokenizer columnsTokenizer = new StringTokenizer(columns, ",");
        while (columnsTokenizer.hasMoreTokens()) {
            String column = columnsTokenizer.nextToken();
            Map<String, Integer> typeAndSize = new HashMap<>();
            typeAndSize.put(column.substring(column.indexOf(" ") + 1, column.indexOf("(")), Integer.parseInt(column.substring(column.indexOf("(") + 1, column.indexOf(")"))));
            columnsHash.put(column.substring(0, column.indexOf(" ")), typeAndSize);
        }
        System.out.println("Operation: " + operation + "\n" +
                "Table name: " + tableName + "\n" +
                "Columns, Types and Sizes: " + columnsHash.toString());
    }

    private void parseDropCommand(String command) {
        StringTokenizer parts = new StringTokenizer(command, " ");
        String operation = parts.nextToken() + " " + parts.nextToken();
        String tableName = parts.nextToken();

        System.out.println("Operation: " + operation + "\n" +
                "Table name: " + tableName);
    }

    /*
    * de forma:
    * INSERT INTO TABLE_NAME(COL1, COL2, ...) VALUES (VAL1, VAL2, ...)
    */
    public void parseInsertCommand(String command) throws Exception {
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

        System.out.println("Operation: " + operation + "\n" +
                "Table: " + tableName + "\n" +
                "Columns: " + columnsArray.toString() + "\n" +
                "Values: " + valuesArray.toString());
    }

    /*
    * de forma:
    * DELETE FROM TABLE_NAME WHERE CONDITION
    * Conditia nu am reusit sa o sparg pt ca trebuie sa ma gandesc la o modalitate prin care sa acopar toate posibilitatile (=,>,<,>=,<=,==,!=)
    * */
    public OperationVO parseDeleteCommand(String command) throws Exception {
        System.out.print("hetree");
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
        List<SelectionCriteriaVO> criteriaList = new ArrayList<>();
        criteriaList.add(criteria);
        deleteOperation.setCriteria(criteriaList);
        return deleteOperation;
    }

    /*
    * de forma:
    * UPDATE TABLE_NAME SET (COL1=VAL1, COL2=VAL2, ...) WHERE CONDITION
    * Conditia nu am reusit sa o sparg pt ca trebuie sa ma gandesc la o modalitate prin care sa acopar toate posibilitatile (=,>,<,>=,<=,==,!=)
    * */
    public void parseUpdateCommand(String command) {
        Map<String, String> updatedPairs = new HashMap<>();
        command = command.replaceAll("\\( ", "\\(");
        command = command.replaceAll(", ", ",");
        command = command.replaceAll(";", "");
        StringTokenizer parts = new StringTokenizer(command, " ");
        String operation = parts.nextToken();
        String tableName = parts.nextToken();
        String keyword = parts.nextToken();
        String columns = parts.nextToken();
        columns = columns.substring(columns.indexOf("(") + 1, columns.indexOf(")"));
        StringTokenizer columnsTokenizer = new StringTokenizer(columns, ",");
        while (columnsTokenizer.hasMoreTokens()) {
            String column = columnsTokenizer.nextToken();
            updatedPairs.put(column.substring(0, column.indexOf("=")), column.substring(column.indexOf("=") + 1, column.length()));
        }
        String keyword2 = parts.nextToken();
        String condition = parts.nextToken(); // de forma id=3, nu id = 3
        String key = condition.substring(0, condition.indexOf("="));
        String value = condition.substring(condition.indexOf("=") + 1, condition.length());


        System.out.println("Operation: " + operation + "\n" +
                "Table name: " + tableName + "\n" +
                "Keyword: " + keyword + "\n" +
                "Updated Pairs: " + updatedPairs.toString() + "\n" +
                "Keyword: " + keyword2 + "\n" +
                "Key: " + key + "\n" +
                "Value: " + value);

    }
}

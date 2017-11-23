package Controller;

import Repository.Repository;

import java.util.StringTokenizer;

/**
 * Created by Laura on 11/4/2017
 */

public class Controller {

    Repository repository = new Repository();

    public Controller(){
    }

    public void createTable(String command){
        repository.parseTableCommand(command);
    }

    public void dropTable(String command){
        repository.parseTableCommand(command);
    }

    public void insertRecord(String command) throws Exception {
        repository.parseInsertCommand(command);
    }

    public void updateRecord(String command) throws Exception {
        repository.parseUpdateCommand(command);
    }

    public void deleteRecord(String command) throws Exception {
        repository.parseDeleteCommand(command);
    }


}

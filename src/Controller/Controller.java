package Controller;

import Repository.Repository;
import domain.vo.OperationVO;
import domain.vo.CommonDao;

import java.sql.SQLException;

/**
 * Created by Laura on 11/4/2017
 */

public class Controller {
    private CommonDao tableDao;

    Repository repository = new Repository();

    public Controller() {
        try {
            tableDao = new CommonDao();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createTable(String command) throws Exception {
        repository.parseTableCommand(command);
    }

    public void dropTable(String command) throws Exception {
        repository.parseTableCommand(command);
    }

    public void insertRecord(String command) throws Exception {
        repository.parseInsertCommand(command);
    }

    public void updateRecord(String command) throws Exception {
        repository.parseUpdateCommand(command);
    }

    public void deleteRecord(String command) throws Exception {
       OperationVO op= repository.parseDeleteCommand(command);
       tableDao.executeRowsOperation(op);
    }


}

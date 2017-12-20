package Controller;

import Repository.Repository;
import domain.vo.CommonDao;
import domain.vo.OperationVO;
import domain.vo.TableOperationVO;

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
        TableOperationVO operationVO = repository.parseTableCommand(command);
        if (operationVO == null) return;
        tableDao.executeTableOperation(operationVO);
    }

    public void dropTable(String command) throws Exception {
        TableOperationVO operationVO = repository.parseTableCommand(command);
        if (operationVO == null) return;
        tableDao.executeTableOperation(operationVO);
    }

    public void insertRecord(String command) throws Exception {
        OperationVO operationVO = repository.parseInsertCommand(command);
        tableDao.executeRowsOperation(operationVO);
    }

    public void updateRecord(String command) throws Exception {
        OperationVO operationVO = repository.parseUpdateCommand(command);
        tableDao.executeRowsOperation(operationVO);

    }

    public void deleteRecord(String command) throws Exception {
        OperationVO op = repository.parseDeleteCommand(command);
        tableDao.executeRowsOperation(op);
    }


}

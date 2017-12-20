import Controller.Controller;

import java.util.Scanner;
import java.util.StringTokenizer;

public class Main {

    public static void menu() {
        System.out.println("1. Command\n" +
                "2. Exit.\n");
    }

    public static String getUserInput() {
        Scanner scanner = new Scanner(System.in);
        String command = scanner.next();
        return command;
    }

    public static void manageCommand(String command) throws Exception {
        Controller controller = new Controller();
        StringTokenizer stringTokenizer = new StringTokenizer(command, " ");
        String keyword = stringTokenizer.nextToken();
        if (keyword.toUpperCase().equals("CREATE"))
            controller.createTable(command);
        if (keyword.toUpperCase().equals("DROP"))
            controller.dropTable(command);
        if (keyword.toUpperCase().equals("INSERT"))
            controller.insertRecord(command);
        if (keyword.toUpperCase().equals("UPDATE"))
            controller.updateRecord(command);
        if (keyword.toUpperCase().equals("DELETE")) {
            controller.deleteRecord(command);
        }
    }

    public static void main(String[] args) throws Exception {
        while (true) {
            menu();
            String command = getUserInput();
            if (command.equals("1")) {
                System.out.println("Enter a command:");
                Scanner scanner = new Scanner(System.in);
                String dbCommand = scanner.nextLine();
                manageCommand(dbCommand);
                // date de test
//                manageCommand("UPDATE STUDENT SET (name=icss) WHERE id=5");
//                manageCommand("UPDATE STUDENT SET (name=itWorks) WHERE name=namee");
//                manageCommand("UPDATE STUDENT SET (name=itWorks) WHERE name=nameee");
//                manageCommand("DELETE FROM student where name=icss");
//                manageCommand("DELETE FROM student where name=ics");
//                manageCommand("DROP TABLE kk"); // not exists -> shows message
//                manageCommand("DROP TABLE dd"); // existent is removed
//                manageCommand("CREATE TABLE test(idd BIGINT(25) Primary_key) ");
            } else {
                System.out.println("You are exiting the database system ...");
                return;
            }
        }
    }
}

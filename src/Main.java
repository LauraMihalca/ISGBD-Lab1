import Controller.Controller;

import java.util.Scanner;
import java.util.StringTokenizer;

public class Main {

    public static void menu(){
        System.out.println("1. Command\n" +
                "2. Exit.\n");
    }

    public static String getUserInput(){
        Scanner scanner = new Scanner(System.in);
        String command = scanner.next();
        return command;
    }

    public static void manageCommand(String command){
        Controller controller = new Controller();
        StringTokenizer stringTokenizer = new StringTokenizer(command, " ");
        String keyword = stringTokenizer.nextToken();
        switch (keyword){
            case "CREATE": controller.createTable(command);
            case "DROP": controller.dropTable(command);
            case "INSERT": controller.insertRecord(command);
            case "UPDATE": controller.updateRecord(command);
            case "DELETE": controller.deleteRecord(command);
        }
    }

    public static void main(String[] args) {
        while (true) {
            menu();
            String command = getUserInput();
            switch (command) {
                case ("1"):
                    System.out.println("Enter a command:");
                    Scanner scanner = new Scanner(System.in);
                    String dbCommand = scanner.next();
                    manageCommand(dbCommand);
                case ("2"): {
                    System.out.println("You are exiting the database system ...");
                    return;
                }
            }
        }
    }
}

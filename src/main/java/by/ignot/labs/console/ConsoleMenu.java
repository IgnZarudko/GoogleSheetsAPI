package by.ignot.labs.console;

import by.ignot.labs.sheetsapi.GoogleSheetsApi;
import by.ignot.labs.sheetsapi.StudentRow;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Scanner;

public class ConsoleMenu {

    private String menu = "[1] - Get all students \n" +
            "[2] - Get student by row number \n" +
            "[3] - Add student \n" +
            "[4] - Update Student \n" +
            "[0] - Exit application\n";

    private boolean menuIsStopped = false;
    Scanner scanner;

    public ConsoleMenu() {
        scanner = new Scanner(System.in);
    }

    public void start() {
        while(!menuIsStopped){
            System.out.println(menu);
            switch (scanner.nextInt()){
                case 0:
                    menuIsStopped = true;
                    break;
                case 1:
                    try{
                        System.out.println(GoogleSheetsApi.getAll());
                    }
                    catch (IOException | GeneralSecurityException e){
                        e.printStackTrace();
                    }
                    break;
                case 2:
                    try {
                        System.out.println("Input number of row:");
                        int row = scanner.nextInt();
                        System.out.println("Got student: \n");
                        System.out.println(GoogleSheetsApi.getStudentByRowNumber(row));
                    }
                    catch (IOException | GeneralSecurityException e){
                        e.printStackTrace();
                    }
                    break;
                case 3:
                    try{
                        addStudent();
                    }
                    catch (IOException | GeneralSecurityException e){
                        e.printStackTrace();
                    }
                    break;

                case 4:
                    try{
                        updateStudent();
                    }
                    catch (IOException | GeneralSecurityException e){
                        e.printStackTrace();
                    }
                break;
                default:
                    System.out.println("Wrong input\n");
                    break;
            }
        }
    }

    private void addStudent() throws GeneralSecurityException, IOException {
        System.out.println("Enter student name: ");
        String name = scanner.next();
        System.out.println("Enter student state: ");
        String state = scanner.next();
        System.out.println("Enter student activity: ");
        String activity = scanner.next();
        GoogleSheetsApi.addStudent(new StudentRow(name, state, activity));
    }

    private void updateStudent() throws GeneralSecurityException, IOException {
        System.out.println("Enter row number: ");
        int rowNumber = scanner.nextInt();
        System.out.println("Enter student name: ");
        String name = scanner.next();
        System.out.println("Enter student state: ");
        String state = scanner.next();
        System.out.println("Enter student activity: ");
        String activity = scanner.next();
        GoogleSheetsApi.updateStudentByRowNumber(rowNumber + 1, new StudentRow(name, state, activity));
    }
}

package CSC3300.project3;

import java.util.*;
import java.sql.*;

public class project3 {

    public static void main(String[] args) {
        Scanner werp = new Scanner(System.in);
        String input;

        // SQL-specific variable objects
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet;
        PreparedStatement prepStatement = null;
        int rowNum;

        // Variables for user input
        String title;
        String ID;
        String deptName;
        int credits;

        // Used for user input validation
        String string1 = "rRqQ";
        String string2 = "DdCcAaRrMmQq";
        String string3 = "TtCcBbXx";

        // Attempt to load MySQL JDBC Driver
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            // If no driver is found, terminates program
        } catch (ClassNotFoundException e) {
            System.err.println("Driver not found\n");
            werp.close();
            System.exit(1);
        }

        // If driver successfully loads, attempt to connect to database using connect()
        // function
        try { 
            connection = connect(werp);
            statement = connection.createStatement();
            // If connection fails, user is prompted to re-enter username and password
        } catch (SQLException e) {
            while (1 < 2) {
                System.out.println("Connection unsuccessful: Invalid username or password");
                printMenu(1);
                input = werp.nextLine();

                // Runs until user selects "r" or "q"
                while (!string1.contains(input)) {
                    System.out.println("Input is invalid. Please try again.\n");
                    printMenu(1);
                    input = werp.nextLine();
                }

                switch (input) {
                    // Attempts to re-connect user to database
                    case "r", "R":
                        try {
                            connection = connect(werp);
                            statement = connection.createStatement();
                            // If connection is unsuccessful, it prompts the user again
                        } catch (SQLException e2) {
                            continue;
                        }
                        break;
                    // Quits application and terminates program
                    case "q", "Q":
                        System.out.println("Exiting System." + "\nGoodbye!");
                        werp.close();
                        System.exit(0);
                        break;

                    default:
                        break;
                }
                break;
            }
        }

        // Main menu loop - runs until user inputs "q"
        while (1 < 3) {
            printMenu(2);
            input = werp.nextLine();

            // Runs until user selects one of the menu options
            while (!string2.contains(input)) {
                System.out.println("Invalid choice. Please try again.\n");
                printMenu(2);
                input = werp.nextLine();
            }

            // Attempts to execute SQL statements
            try {
                switch (input) {
                    // Retrieve all info about departments
                    case "d", "D":
                        // Executes SQL query
                        resultSet = statement.executeQuery("SELECT dept_name, building FROM department");

                        // Prints all results in a formatted manner
                        while (resultSet.next()) {
                            System.out.println(resultSet.getString("dept_name") + " | "
                                    + resultSet.getString("building"));
                        }
                        break;

                    // Retrieves all info on courses
                    case "c", "C":
                        // Executes SQL query
                        resultSet = statement.executeQuery("SELECT course_id, title, dept_name, credits FROM course");

                        // Prints all results in a formmated manner
                        while (resultSet.next()) {
                            System.out.println(resultSet.getString("course_id") + " | "
                                    + resultSet.getString("title") + " | "
                                    + resultSet.getString("dept_name")
                                    + " | " + resultSet.getString("credits"));
                        }
                        break;

                    // Adds a new course to the database
                    case "a", "A":
                        // Prompts user to enter course info
                        ID = getID(werp);
                        title = getTitle(werp);
                        deptName = getDepartment(werp);
                        credits = getCredit(werp);

                        // Executes SQL query and all related info to upadate database
                        PreparedStatement add = connection.prepareStatement("INSERT INTO course VALUES (?, ?, ?, ?)");
                        prepStatement = add;
                        add.setString(1, ID);
                        add.setString(2, title);
                        add.setString(3, deptName);
                        add.setInt(4, credits);
                        add.executeUpdate();

                        // Prints if no exceptions are thrown
                        System.out.println("Course addded successfully.\n");
                        break;

                    // Removes a course
                    case "r", "R":
                        // Retrieves course_id from database that the user wishes to delete
                        ID = getID(werp);

                        // Executes SQL query and all related info to update database
                        PreparedStatement remove = connection
                                .prepareStatement("DELETE FROM course WHERE course_id = ?");
                        prepStatement = remove; // Used to close PreparedStatement
                        remove.setString(1, ID);
                        rowNum = remove.executeUpdate(); // Used to determine if any rows were affected

                        // If no rows were affected, the course_id was invalid
                        if (rowNum == 0) {
                            System.out.println("Course ID not found in database.\n");
                            continue;
                        }

                        // Prints if no exception is thrown
                        System.out.println("Course removed successfully.\n");
                        break;

                    // Modify values of a course
                    case "m", "M":
                        // Retrieves course_id from database that the user wishes to modify
                        ID = getID(werp);

                        // Executes SQL query to retrieve course
                        PreparedStatement check = connection
                                .prepareStatement("SELECT course_id FROM course WHERE course_id = ?");
                        prepStatement = check; // Used to close PreparedStatement
                        check.setString(1, ID);
                        resultSet = check.executeQuery();

                        // If course is not found, course did not exist in the database
                        if (!resultSet.next()) {
                            System.out.println("Course ID not found.\n");
                            continue;
                        }
                        prepStatement.close();

                        // Prints appropriate menu for course modification
                        printMenu(3);
                        input = werp.nextLine(); 

                        // Runs until user selects one of the menu options
                        while (!string3.contains(input)) {
                            System.out.println("Invalid choice. Please try again.");
                            printMenu(3);
                            input = werp.nextLine();
                        }

                        switch (input) {
                            // User wants to modify course title
                            case "t", "T":
                                // Retrieves course title
                                title = getTitle(werp);

                                // Executes SQL query and related info to update database
                                PreparedStatement modTitle = connection
                                        .prepareStatement("UPDATE course SET title = ? WHERE course_id = ?");
                                prepStatement = modTitle; // Used to close PreparedStatement
                                modTitle.setString(1, title);
                                modTitle.setString(2, ID);
                                rowNum = modTitle.executeUpdate(); // Used to determine if any rows were affected

                                // If no rows were affected, the course_id was invalid
                                if (rowNum == 0) {
                                    System.out.println("Error! Course ID not found.\n");
                                    continue;
                                }

                                // Prints if no exception is thrown
                                System.out.println("Title modified successfully.\n");
                                break;

                            // User wants to modify course credit number
                            case "c", "C":
                                // Retrieves course credit number
                                credits = getCredit(werp);

                                // Executes SQL query and related info to update database
                                PreparedStatement modCredit = connection
                                        .prepareStatement("UPDATE course SET credits = ? WHERE course_ID = ?");
                                prepStatement = modCredit; // Used to close PreparedStatement
                                modCredit.setInt(1, credits);
                                modCredit.setString(2, ID);
                                rowNum = modCredit.executeUpdate(); // Used to determine if any rows were affected

                                // If no rows were affected, the course_id was invalid
                                if (rowNum == 0) {
                                    System.out.println("Course ID not found.\n");
                                    continue;
                                }

                                // Prints if no exception is thrown
                                System.out.println("Credits modified successfully.\n");
                                break;

                            // User wants to modify both the course title and credit number
                            case "b", "B":
                                // Retrieves course credit number and title
                                title = getTitle(werp);
                                credits = getCredit(werp);

                                // Executes SQL query and related info to update database
                                PreparedStatement mod = connection.prepareStatement(
                                        "UPDATE course SET title = ?, credits = ? WHERE course_id = ?");
                                prepStatement = mod; // Used to close PreparedStatement
                                mod.setString(1, title);
                                mod.setInt(2, credits);
                                mod.setString(3, ID);
                                rowNum = mod.executeUpdate(); // Used to determine if any rows were affected

                                // If no rows were affected, the course_id was invalid
                                if (rowNum == 0) {
                                    System.out.println("Course ID not found.\n");
                                    continue;
                                }

                                // Prints if no exception is thrown
                                System.out.println("Title and credits modified successfully.\n");
                                break;

                            // User cancels modification; no tuples are modified, and the usre is taken back
                            // to the main menu
                            case "x", "X":
                                System.out.println("\nExiting modification menu. No tuples have been modified.\n");
                                continue;

                            default:
                                break;
                        }
                        break;

                    // Exits system and terminates the program
                    case "q", "Q":
                        System.out.println("Exiting System." + "\nGoodbye!");
                        werp.close();
                        statement.close();
                        System.exit(0);
                        break;

                    default:
                        continue;
                }
                // Catches any exceptions thrown
            } catch (SQLException e) {
                System.err.println("Error: " + e.getMessage());
                // Closes PreparedStatement value if it isn't already closed
            } finally {
                try {
                    if (prepStatement != null) {
                        prepStatement.close();
                    }
                } catch (SQLException e) {
                    System.err.println("Failed to close prepared statement");
                }
            }
        }
    }

    // Public function - used to connect to the database
    // Prompts user for username and password
    public static Connection connect(Scanner i) throws SQLException {
        System.out.println("Please enter your username:");
        String username = i.nextLine();
        System.out.println("Please enter your password:");
        String password = i.nextLine();

        // Connects to database
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/university", username,
                password);

        System.out.println("Welcome to the university database, " + username);

        // Returns the connection
        return connection;
    }

    // Public function - prompts user for course_id attribute
    // Passes scanner object as parameter for user input
    public static String getID(Scanner i) {
        String courseID;
        // Runs until user inputs a valid course_id attribute
        while (1 < 4) {
            System.out.println("Enter course ID: ");
            courseID = i.nextLine();

            // If user input is empty
            if (courseID.isEmpty()) {
                System.out.println("Error! Course ID cannot be empty. Try again.\n");
                continue;
                // If user input doesn't match correct course_id format
                // Uses regular expressions to check if input matches correct format
            } else if (!courseID.matches("^[A-Z]{2,4}-\\d{3}$")) {
                System.out.println(
                        "Error! Course ID must be 2-4 capital letters followed by a dash and 3 digits. Try again.\n");
                continue;
            }
            break;
        }

        return courseID;
    }

    // Public function - prompts user for course title attribute
    // Passes scanner object as parameter for user input
    public static String getTitle(Scanner i) {
        String title;
        // Runs until user inputs a valid course title attribute
        while (1 < 5) {
            System.out.println("Enter course title: ");
            title = i.nextLine();

            // If user input is empty
            if (title.isEmpty()) {
                System.out.println("Error! Course title cannot be empty. Try again.\n");
                continue;
                // if user input is over specified character length
            } else if (title.length() > 50) {
                System.out.println("Error! Course title must be 50 characters or less. Try again.\n");
                continue;
            }
            break;
        }

        return title;
    }

    // Public function - prompts user for department attribute
    // Passes scanner object as parameter for user input
    public static String getDepartment(Scanner i) {
        String dept;
        // Runs until user inputs a valid department attribute
        while (1 < 6) {
            System.out.println("Enter department name: ");
            dept = i.nextLine();

            // If user input is empty
            if (dept.isEmpty()) {
                System.out.println("Error! Department name cannot be empty. Try again.\n");
                continue;
                // if user input is over specified character length
            } else if (dept.length() > 20) {
                System.out.println("Error! Department name must be 20 characters or less. Try again.\n");
                continue;
            }
            break;
        }

        return dept;
    }

    // Public function - prompts user for department attribute
    // Passes scanner object as parameter for user input
    public static int getCredit(Scanner i) {
        String credit;
        int creditNum;
        // Runs until user inputs a valid credit attribute
        while (1 < 7) {
            // Runs until user actually inputs something
            do {
                System.out.print("Enter number of credits: ");
                credit = i.nextLine();

                if (credit.isEmpty()) {
                    System.out.println("Number of credits cannot be empty. Try again."); 
                }
            } while (credit.isEmpty());

            // Attempts to parse user input to an integer value
            try { 
                creditNum = Integer.parseInt(credit);
            } catch (NumberFormatException e) {
                System.out.println("Number of credits must be an integer. Try again.");
                continue;
            }

            // If credit number is over specified credit number
            if (creditNum > 99) {
                System.out.println("Number of credits must be 2 digits or less. Try again.");
                continue;
            } else if (creditNum <= 0) {
                System.out.println("Number of credits must be greater than 0. Try again.");
                continue;
            }
            break;
        }

        return creditNum;
    }

    // Public function - used to print out relavent menus
    // Menu 1 is for username/passoword to connect to database
    // Menu 2 is the main menu for user actions
    // Menu 3 is the course modification menu
    // I wrote this so I wouldn't have to keep re-printing menus in the main
    // function because that would look awful
    public static void printMenu(int i) {
        switch (i) {
            case 1:
                System.out.println("\nChoose which action to perform:");
                System.out.println("(r) re-enter username and password");
                System.out.println("(q) Quit");
                System.out.println(">");
                break;

            case 2:
                System.out.println("\nChoose which action to perform:");
                System.out.println("(d) Retrieve all departments");
                System.out.println("(c) Retrieve all courses");
                System.out.println("(a) Add new course");
                System.out.println("(r) Remove a course");
                System.out.println("(m) Modify a course");
                System.out.println("(q) Quit");
                System.out.println(">");
                break;

            case 3:
                System.out.println("\nChoose which action to perform:");
                System.out.println("(t) Modify course title");
                System.out.println("(c) Modify course credits");
                System.out.println("(b) Modify course title and credits");
                System.out.println("(x) Cancel modification");
                System.out.println(">");
                break;

            default:
                break;
        }
    }
}
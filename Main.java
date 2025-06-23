import java.sql.*;
import java.util.Scanner;

public class Main {

    static final String DB_URL = "jdbc:mysql://localhost:3306/dbms";
    static final String DB_USER = "root";
    static final String DB_PASSWORD = "12345678";

    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            createTables(conn);
            Scanner scanner = new Scanner(System.in);
            boolean exit = false;

            while (!exit) {
                System.out.println("\n=== Event Participation System ===");
                System.out.println("1. Create User");
                System.out.println("2. Create Event");
                System.out.println("3. Register for Event");
                System.out.println("4. View Users");
                System.out.println("5. View Events");
                System.out.println("6. View Registrations");
                System.out.println("7. Delete User");
                System.out.println("8. Delete Event");
                System.out.println("9. Mark Attendance");
                System.out.println("10. View Attendance");
                System.out.println("11. Add Feedback");
                System.out.println("12. View Event Participation");
                System.out.println("13. Exit");
                System.out.print("Choose an option: ");

                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1 -> createUser(conn, scanner);
                    case 2 -> createEvent(conn, scanner);
                    case 3 -> registerForEvent(conn, scanner);
                    case 4 -> viewUsers(conn);
                    case 5 -> viewEvents(conn);
                    case 6 -> viewRegistrations(conn);
                    case 7 -> deleteUser(conn, scanner);
                    case 8 -> deleteEvent(conn, scanner);
                    case 9 -> markAttendance(conn, scanner);
                    case 10 -> viewAttendance(conn);
                    case 11 -> addFeedback(conn, scanner);
                    case 12 -> viewEventParticipation(conn);
                    case 13 -> exit = true;
                    default -> System.out.println("Invalid option.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    static void createTables(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();

        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS Users (user_id INT PRIMARY KEY AUTO_INCREMENT, username VARCHAR(50) NOT NULL, password VARCHAR(50) NOT NULL, email VARCHAR(100) NOT NULL)");
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS Student (user_id INT PRIMARY KEY, department VARCHAR(50), FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE CASCADE ON UPDATE CASCADE)");
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS Organizer (user_id INT PRIMARY KEY, organization VARCHAR(100), FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE CASCADE ON UPDATE CASCADE)");
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS Event (event_id INT PRIMARY KEY AUTO_INCREMENT, title VARCHAR(100), time TIME, date DATE, location VARCHAR(100), capacity INT, organizer_id INT, FOREIGN KEY (organizer_id) REFERENCES Organizer(user_id) ON DELETE CASCADE ON UPDATE CASCADE)");
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS Registration (event_id INT, user_id INT, reg_date DATE, PRIMARY KEY (event_id, user_id), FOREIGN KEY (event_id) REFERENCES Event(event_id) ON DELETE CASCADE ON UPDATE CASCADE, FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE CASCADE ON UPDATE CASCADE)");
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS Attendance (event_id INT, user_id INT, attended BOOLEAN, PRIMARY KEY (event_id, user_id), FOREIGN KEY (event_id) REFERENCES Event(event_id) ON DELETE CASCADE ON UPDATE CASCADE, FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE CASCADE ON UPDATE CASCADE)");
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS EventParticipation (event_id INT, user_id INT, organizer_id INT, attended BOOLEAN, feedback TEXT, PRIMARY KEY (event_id, user_id), FOREIGN KEY (event_id) REFERENCES Event(event_id) ON DELETE CASCADE ON UPDATE CASCADE, FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE CASCADE ON UPDATE CASCADE, FOREIGN KEY (organizer_id) REFERENCES Organizer(user_id) ON DELETE CASCADE ON UPDATE CASCADE)");

        stmt.close();
        System.out.println("Tables checked/created successfully!");
    }

    static void createUser(Connection conn, Scanner scanner) throws SQLException {
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();

        String sql = "INSERT INTO Users (username, password, email) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, email);
            stmt.executeUpdate();
            System.out.println("User created successfully.");
        }
    }

    static void createEvent(Connection conn, Scanner scanner) throws SQLException {
        System.out.print("Title: ");
        String title = scanner.nextLine();
        System.out.print("Time (HH:MM:SS): ");
        String time = scanner.nextLine();
        System.out.print("Date (YYYY-MM-DD): ");
        String date = scanner.nextLine();
        System.out.print("Location: ");
        String location = scanner.nextLine();
        System.out.print("Capacity: ");
        int capacity = scanner.nextInt();
        System.out.print("Organizer User ID: ");
        int organizerId = scanner.nextInt();
        scanner.nextLine();

        String sql = "INSERT INTO Event (title, time, date, location, capacity, organizer_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, title);
            stmt.setTime(2, Time.valueOf(time));
            stmt.setDate(3, Date.valueOf(date));
            stmt.setString(4, location);
            stmt.setInt(5, capacity);
            stmt.setInt(6, organizerId);
            stmt.executeUpdate();
            System.out.println("Event created successfully.");
        }
    }

    static void registerForEvent(Connection conn, Scanner scanner) throws SQLException {
        System.out.print("Event ID: ");
        int eventId = scanner.nextInt();
        System.out.print("User ID: ");
        int userId = scanner.nextInt();
        scanner.nextLine();

        String sql = "INSERT INTO Registration (event_id, user_id, reg_date) VALUES (?, ?, CURDATE())";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, eventId);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
            System.out.println("Registration successful.");
        }
    }

    static void viewUsers(Connection conn) throws SQLException {
        String sql = "SELECT * FROM Users";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                System.out.printf("ID: %d, Username: %s, Email: %s\n",
                        rs.getInt("user_id"), rs.getString("username"), rs.getString("email"));
            }
        }
    }

    static void viewEvents(Connection conn) throws SQLException {
        String sql = "SELECT * FROM Event";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                System.out.printf("ID: %d, Title: %s, Date: %s, Time: %s, Location: %s, Capacity: %d, Organizer ID: %d\n",
                        rs.getInt("event_id"), rs.getString("title"),
                        rs.getDate("date"), rs.getTime("time"),
                        rs.getString("location"), rs.getInt("capacity"),
                        rs.getInt("organizer_id"));
            }
        }
    }

    static void viewRegistrations(Connection conn) throws SQLException {
        String sql = "SELECT * FROM Registration";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                System.out.printf("Event ID: %d, User ID: %d, Registration Date: %s\n",
                        rs.getInt("event_id"), rs.getInt("user_id"), rs.getDate("reg_date"));
            }
        }
    }

    static void deleteUser(Connection conn, Scanner scanner) throws SQLException {
        System.out.print("User ID to delete: ");
        int userId = scanner.nextInt();
        scanner.nextLine();

        String sql = "DELETE FROM Users WHERE user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            int rows = stmt.executeUpdate();
            System.out.println(rows > 0 ? "User deleted." : "User not found.");
        }
    }

    static void deleteEvent(Connection conn, Scanner scanner) throws SQLException {
        System.out.print("Event ID to delete: ");
        int eventId = scanner.nextInt();
        scanner.nextLine();

        String sql = "DELETE FROM Event WHERE event_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, eventId);
            int rows = stmt.executeUpdate();
            System.out.println(rows > 0 ? "Event deleted." : "Event not found.");
        }
    }

    static void markAttendance(Connection conn, Scanner scanner) throws SQLException {
        System.out.print("Event ID: ");
        int eventId = scanner.nextInt();
        System.out.print("User ID: ");
        int userId = scanner.nextInt();
        System.out.print("Organizer ID: ");
        int organizerId = scanner.nextInt();
        System.out.print("Did the user attend? (true/false): ");
        boolean attended = scanner.nextBoolean();
        scanner.nextLine();

        String sql = "REPLACE INTO EventParticipation (event_id, user_id, organizer_id, attended, feedback) VALUES (?, ?, ?, ?, NULL)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, eventId);
            stmt.setInt(2, userId);
            stmt.setInt(3, organizerId);
            stmt.setBoolean(4, attended);
            stmt.executeUpdate();
            System.out.println("Attendance marked successfully in EventParticipation.");
        }
    }

    static void viewAttendance(Connection conn) throws SQLException {
        String sql = "SELECT * FROM EventParticipation";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                System.out.printf("Event ID: %d, User ID: %d, Attended: %s\n",
                        rs.getInt("event_id"), rs.getInt("user_id"),
                        rs.getBoolean("attended") ? "Yes" : "No");
            }
        }
    }

    static void addFeedback(Connection conn, Scanner scanner) throws SQLException {
        System.out.print("Event ID: ");
        int eventId = scanner.nextInt();
        System.out.print("User ID: ");
        int userId = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Your feedback: ");
        String feedback = scanner.nextLine();

        String sql = "UPDATE EventParticipation SET feedback = ? WHERE event_id = ? AND user_id = ? AND attended = TRUE";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, feedback);
            stmt.setInt(2, eventId);
            stmt.setInt(3, userId);
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Feedback added successfully.");
            } else {
                System.out.println("Feedback not added. Make sure the user attended the event.");
            }
        }
    }

    static void viewEventParticipation(Connection conn) throws SQLException {
        String sql = "SELECT * FROM EventParticipation";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                System.out.printf("Event ID: %d, User ID: %d, Organizer ID: %d, Attended: %s, Feedback: %s\n",
                        rs.getInt("event_id"), rs.getInt("user_id"), rs.getInt("organizer_id"),
                        rs.getBoolean("attended") ? "Yes" : "No", rs.getString("feedback"));
            }
        }
    }
}
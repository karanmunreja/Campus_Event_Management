import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DataBase {

    public static void createTables(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();

        // Users Table
        String Users = "CREATE TABLE IF NOT EXISTS Users (" +
                "user_id INT PRIMARY KEY AUTO_INCREMENT, " +
                "username VARCHAR(50) NOT NULL, " +
                "password VARCHAR(50) NOT NULL, " +
                "email VARCHAR(100) NOT NULL" +
                ")";
        stmt.executeUpdate(Users);

        // Student Table
        String Student = "CREATE TABLE IF NOT EXISTS Student (" +
                "user_id INT PRIMARY KEY, " +
                "department VARCHAR(50), " +
                "FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE CASCADE ON UPDATE CASCADE" +
                ")";
        stmt.executeUpdate(Student);

        // Organizer Table
        String Organizer = "CREATE TABLE IF NOT EXISTS Organizer (" +
                "user_id INT PRIMARY KEY, " +
                "organization VARCHAR(100), " +
                "FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE CASCADE ON UPDATE CASCADE" +
                ")";
        stmt.executeUpdate(Organizer);

        // Event Table
        String Event = "CREATE TABLE IF NOT EXISTS Event (" +
                "event_id INT PRIMARY KEY AUTO_INCREMENT, " +
                "title VARCHAR(100), " +
                "time TIME, " +
                "date DATE, " +
                "location VARCHAR(100), " +
                "capacity INT, " +
                "organizer_id INT, " +
                "FOREIGN KEY (organizer_id) REFERENCES Organizer(user_id) ON DELETE CASCADE ON UPDATE CASCADE" +
                ")";
        stmt.executeUpdate(Event);

        // Registration Table
        String Registration = "CREATE TABLE IF NOT EXISTS Registration (" +
                "event_id INT, " +
                "user_id INT, " +
                "reg_date DATE, " +
                "PRIMARY KEY (event_id, user_id), " +
                "FOREIGN KEY (event_id) REFERENCES Event(event_id) ON DELETE CASCADE ON UPDATE CASCADE, " +
                "FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE CASCADE ON UPDATE CASCADE" +
                ")";
        stmt.executeUpdate(Registration);

        // Attendance Table
        String Attendance = "CREATE TABLE IF NOT EXISTS Attendance (" +
                "event_id INT, " +
                "user_id INT, " +
                "attended BOOLEAN, " +
                "PRIMARY KEY (event_id, user_id), " +
                "FOREIGN KEY (event_id) REFERENCES Event(event_id) ON DELETE CASCADE ON UPDATE CASCADE, " +
                "FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE CASCADE ON UPDATE CASCADE" +
                ")";
        stmt.executeUpdate(Attendance);

        
        stmt.close();
        System.out.println("Tables checked/created successfully!");
    }
}

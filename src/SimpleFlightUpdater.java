import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

/**
 * Securely updates the delay status of flight LX354.
 *
 * The original implementation concatenated user input into
 * a SQL statement, allowing SQL injection to modify all rows.
 *
 * PreparedStatement treats user input as parameter data,
 * so SQL characters cannot alter the query structure.
 */
public class SimpleFlightUpdater {

    private static final String URL =
            "jdbc:mysql://localhost:3306/FlightDB?"
                    + "allowLoadLocalInfile=true"
                    + "&allowPublicKeyRetrieval=true"
                    + "&serverTimezone=Asia/Jerusalem"
                    + "&useSSL=false";

    private static final String USER = "root";

    public static void main(String[] args) {

        try (Scanner scanner = new Scanner(System.in)) {

            System.out.print("Enter MySQL root password: ");
            String password = scanner.nextLine();

            System.out.print(
                    "Enter new delay status for flight LX354: ");
            String userInput = scanner.nextLine();

            String sql =
                    "UPDATE Arrivals SET delay_status = ? "
                            + "WHERE flight_number = ?";

            try (Connection conn =
                         DriverManager.getConnection(
                                 URL, USER, password);
                 PreparedStatement pstmt =
                         conn.prepareStatement(sql)) {

                pstmt.setString(1, userInput);
                pstmt.setString(2, "LX354");

                int affectedRows = pstmt.executeUpdate();

                System.out.println(
                        "Number of affected rows: " + affectedRows);

            } catch (SQLException e) {
                System.err.println(
                        "Database error: " + e.getMessage());
            }
        }
    }
}
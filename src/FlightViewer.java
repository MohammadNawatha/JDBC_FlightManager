import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class FlightViewer {

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

            System.out.print("Enter terminal number (2, 3, 4, 5): ");
            String terminal = scanner.nextLine().trim();

            if (!terminal.matches("[2345]")) {
                System.out.println("Invalid terminal number.");
                return;
            }

            String sql =
                    "SELECT a.flight_number, r.from_city, "
                            + "a.scheduled, a.delay_status, a.terminal "
                            + "FROM Arrivals a "
                            + "INNER JOIN FlightRoutes r "
                            + "ON a.flight_number = r.flight_number "
                            + "WHERE a.terminal = ? "
                            + "ORDER BY a.scheduled ASC";

            try (Connection conn =
                         DriverManager.getConnection(URL, USER, password);
                 PreparedStatement pstmt =
                         conn.prepareStatement(sql)) {

                pstmt.setString(1, terminal);

                try (ResultSet rs = pstmt.executeQuery()) {

                    System.out.println();
                    System.out.println(
                            "Flights for Terminal " + terminal);
                    System.out.println(
                            "------------------------------------------------"
                                    + "--------------------------------------");

                    System.out.printf(
                            "%-12s %-20s %-15s %-22s %-10s%n",
                            "Flight", "From", "Scheduled", "Status", "Terminal");

                    System.out.println(
                            "------------------------------------------------"
                                    + "--------------------------------------");

                    int count = 0;

                    while (rs.next()) {
                        System.out.printf(
                                "%-12s %-20s %-15s %-22s %-10s%n",
                                rs.getString("flight_number"),
                                rs.getString("from_city"),
                                rs.getString("scheduled"),
                                rs.getString("delay_status"),
                                rs.getString("terminal")
                        );

                        count++;
                    }

                    if (count == 0) {
                        System.out.println(
                                "No flights found for this terminal.");
                    }

                    System.out.println();
                    System.out.println(
                            "Total flights found: " + count);
                }

            } catch (SQLException e) {
                System.err.println(
                        "Database error: " + e.getMessage());
            }
        }
    }
}
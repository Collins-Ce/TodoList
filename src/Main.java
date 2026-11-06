import java.sql.Connection;
import database.DBConnection;

public class Main {

    public static void main(String[] args) {

        Connection con = DBConnection.getConnection();

        if (con != null) {

            System.out.println("==============================");
            System.out.println("   TODO LIST DATABASE");
            System.out.println("==============================");

            System.out.println("Connected successfully!");
            System.out.println("Database: todo_db");

            try {
                con.close();
                System.out.println("Connection closed.");

            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            System.out.println("Connection unsuccessful!");
        }
    }
}
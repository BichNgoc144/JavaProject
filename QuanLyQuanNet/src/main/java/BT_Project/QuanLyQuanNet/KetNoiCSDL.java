package BT_Project.QuanLyQuanNet;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class KetNoiCSDL {
	 private static final String URL = "jdbc:mysql://localhost:3306/net_management?useSSL=false&serverTimezone=UTC";
	    private static final String USER = "root";
	    private static final String PASS = "";

	    public static Connection getConnection() throws SQLException {
	        return DriverManager.getConnection(URL, USER, PASS);
	    }
}

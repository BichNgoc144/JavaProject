package BT_Project.QuanLyQuanNet;

import java.sql.Connection;
import java.sql.SQLException;

public class TestConnection {
    public static void main(String[] args) {
        try {
            Connection conn = KetNoiCSDL.getConnection();
            if (conn != null) {
                System.out.println("✅ Kết nối MySQL thành công!");
                conn.close();
            }
        } catch (SQLException e) {
            System.out.println("❌ Lỗi kết nối MySQL:");
            e.printStackTrace();
        }
    }
}

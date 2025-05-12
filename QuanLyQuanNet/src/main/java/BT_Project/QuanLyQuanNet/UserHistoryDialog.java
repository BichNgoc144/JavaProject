package BT_Project.QuanLyQuanNet;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class UserHistoryDialog extends JDialog {

    public UserHistoryDialog(JFrame parent, int userId, String username) {
        super(parent, "Lịch sử sử dụng của: " + username, true);
        setSize(600, 400);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        String[] columnNames = {"Tên máy", "Thời gian bắt đầu", "Thời gian kết thúc", "Tổng tiền (VNĐ)"};
        DefaultTableModel model = new DefaultTableModel(null, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable historyTable = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(historyTable);
        add(scrollPane, BorderLayout.CENTER);

        try (Connection conn = KetNoiCSDL.getConnection()) {
            String sql = "SELECT machine_name, start_time, end_time, total_amount " +
                    "FROM machine_usage WHERE user_id = ? ORDER BY start_time DESC";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, userId);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        String machineName = rs.getString("machine_name");
                        Timestamp start = rs.getTimestamp("start_time");
                        Timestamp end = rs.getTimestamp("end_time");
                        double total = rs.getDouble("total_amount");

                        String startStr = (start != null) ? start.toString() : "N/A";
                        String endStr = (end != null) ? end.toString() : "Đang sử dụng";
                        String totalStr = String.format("%.0f", total);

                        model.addRow(new Object[]{machineName, startStr, endStr, totalStr});
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi tải lịch sử người dùng!");
        }
    }
}

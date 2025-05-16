package BT_Project.QuanLyQuanNet;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class UsageHistoryForm extends JFrame {

    private JTable historyTable;
    private DefaultTableModel historyModel;

    private int userId;

    public UsageHistoryForm(int userId) {
        this.userId = userId;
        setTitle("Usage History");
        setSize(700, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        String[] columnNames = {"Machine Name", "Start Time", "End Time", "Total Amount (VND)"};
        historyModel = new DefaultTableModel(null, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        historyTable = new JTable(historyModel);
        add(new JScrollPane(historyTable), BorderLayout.CENTER);
        
        // Thêm nút Home
        JButton btnHome = new JButton("Home");
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(btnHome);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Lắng nghe sự kiện khi nhấn nút Home
        btnHome.addActionListener(e -> {
            new GiaoDienUser(userId).setVisible(true);
            dispose();  // Đóng form hiện tại
        });

        loadHistory(userId);
    }

    private void loadHistory(int userId) {
        try (Connection conn = KetNoiCSDL.getConnection()) {
            String sql = "SELECT machine_name, start_time, end_time, total_amount " +
                    "FROM machine_usage WHERE user_id = ? ORDER BY start_time DESC";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, userId);  // ID người dùng truyền vào
                try (ResultSet rs = stmt.executeQuery()) {
                    historyModel.setRowCount(0);
                    while (rs.next()) {
                        String machineName = rs.getString("machine_name");
                        Timestamp start = rs.getTimestamp("start_time");
                        Timestamp end = rs.getTimestamp("end_time");
                        double total = rs.getDouble("total_amount");

                        String startStr = (start != null) ? start.toString() : "N/A";
                        String endStr = (end != null) ? end.toString() : "In Use";
                        String totalStr = String.format("%.0f", total);

                        historyModel.addRow(new Object[]{machineName, startStr, endStr, totalStr});
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading usage history!");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new UsageHistoryForm(1).setVisible(true));  // Test với user_id = 1
    }
}

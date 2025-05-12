package BT_Project.QuanLyQuanNet;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.sql.*;
import java.util.HashMap;

public class UserManagementForm extends JFrame {

    private JTable userTable;
    private HashMap<Integer, Integer> rowUserIdMap = new HashMap<>();  // Map: rowIndex -> userId
    private JButton btnLogout;


    public UserManagementForm() {
        setTitle("Quản lý người dùng");
        setSize(800, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);  // ✅ để có thể chạy độc lập
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        String[] columnNames = {"Tên người dùng", "Số dư còn lại (VNĐ)", "Lần sử dụng gần nhất", "Chi tiết"};
        DefaultTableModel model = new DefaultTableModel(null, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3;  // Chỉ cho phép click vào nút "Chi tiết"
            }
        };

        userTable = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(userTable);
        add(scrollPane, BorderLayout.CENTER);
        
        btnLogout = new JButton("Home");
        add(btnLogout, BorderLayout.SOUTH);

        // Thêm nút "Chi tiết" vào cột cuối
        userTable.getColumn("Chi tiết").setCellRenderer(new ButtonRenderer());
        userTable.getColumn("Chi tiết").setCellEditor(new ButtonEditor(new JCheckBox()));

        loadUserData();
        btnLogout.addActionListener(e -> {
            new GiaoDienAdmin().setVisible(true);
            dispose();  // Đóng form hiện tại
        });
    }

    private void loadUserData() {
        rowUserIdMap.clear();  // Xóa map cũ nếu có

        try (Connection conn = KetNoiCSDL.getConnection()) {
            String sql = "SELECT u.id, u.username, " +
                    "COALESCE(SUM(d.amount), 0) AS total_deposit, " +
                    "COALESCE((SELECT SUM(m.total_amount) FROM machine_usage m WHERE m.user_id = u.id), 0) AS total_usage, " +
                    "(SELECT MAX(m.start_time) FROM machine_usage m WHERE m.user_id = u.id) AS last_usage " +
                    "FROM users u " +
                    "LEFT JOIN deposit d ON u.id = d.user_id " +
                    "WHERE u.role != 'admin' " +  // ✅ Chỉ loại bỏ admin
                    "GROUP BY u.id, u.username";

            DefaultTableModel model = (DefaultTableModel) userTable.getModel();
            model.setRowCount(0);

            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                int rowIndex = 0;
                while (rs.next()) {
                    int userId = rs.getInt("id");
                    String username = rs.getString("username");
                    double totalDeposit = rs.getDouble("total_deposit");
                    double totalUsage = rs.getDouble("total_usage");
                    Timestamp lastUsage = rs.getTimestamp("last_usage");

                    double balance = totalDeposit - totalUsage;
                    if (balance < 0) balance = 0;

                    String lastUsageStr = (lastUsage != null) ? lastUsage.toString() : "Chưa sử dụng";

                    // Thêm dòng vào bảng
                    model.addRow(new Object[]{
                            username,
                            String.format("%.0f", balance),
                            lastUsageStr,
                            "Chi tiết"
                    });

                    // Lưu userId theo rowIndex
                    rowUserIdMap.put(rowIndex, userId);
                    rowIndex++;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu người dùng!");
        }
    }

    // Renderer cho nút Chi tiết
    private class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setText("Chi tiết");
        }
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            return this;
        }
    }

    // Editor cho nút Chi tiết
    private class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String username;
        private int userId;
        private boolean clicked;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton("Chi tiết");
            button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            username = (String) table.getValueAt(row, 0);
            userId = rowUserIdMap.get(row);  // Lấy userId từ map
            clicked = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (clicked) {
                // Mở dialog chi tiết
                UserHistoryDialog historyDialog = new UserHistoryDialog(UserManagementForm.this, userId, username);
                historyDialog.setVisible(true);
            }
            clicked = false;
            return "Chi tiết";
        }

        @Override
        public boolean stopCellEditing() {
            clicked = false;
            return super.stopCellEditing();
        }
    }

    // ✅ Hàm MAIN để chạy độc lập
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new UserManagementForm().setVisible(true));
    }
}

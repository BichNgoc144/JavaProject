package BT_Project.QuanLyQuanNet;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.HashMap;

public class UserManagementForm extends JFrame {
	
    private static final Color DARK_BG = new Color(15, 15, 25);
    private static final Color ACCENT_COLOR = new Color(0, 200, 255);
    private static final Color TEXT_COLOR = new Color(220, 220, 220);
    private static final Color TABLE_HEADER_BG = new Color(30, 30, 45);
    private static final Color TABLE_ROW_BG = new Color(25, 25, 40);
    private static final Color TABLE_ALT_ROW_BG = new Color(30, 30, 50);
    
    private JTable userTable;
    private HashMap<Integer, Integer> rowUserIdMap = new HashMap<>();
    private JButton btnBack;

    public UserManagementForm() {
        setTitle("QUẢN LÝ THÀNH VIÊN - GAME CENTER ADMIN");
        setSize(1000, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(DARK_BG);
        setLayout(new BorderLayout());
        
        // Panel tiêu đề
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(20, 20, 35));
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, ACCENT_COLOR));
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setAlignmentX(LEFT_ALIGNMENT);
        
        JLabel titleLabel = new JLabel("QUẢN LÝ THÀNH VIÊN");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(ACCENT_COLOR);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleLabel.setBorder(new EmptyBorder(15, 20, 10, 0));
        
        JLabel subTitleLabel = new JLabel("Danh sách thành viên và thông tin tài khoản");
        subTitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subTitleLabel.setForeground(TEXT_COLOR);
        subTitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        subTitleLabel.setBorder(new EmptyBorder(0, 20, 15, 0));
        
        headerPanel.add(titleLabel);
        headerPanel.add(subTitleLabel);
        add(headerPanel, BorderLayout.NORTH);

        // Tạo bảng với model
        String[] columnNames = {"TÊN THÀNH VIÊN", "SỐ DƯ (VNĐ)", "LẦN CUỐI ONLINE", "CHI TIẾT"};
        DefaultTableModel model = new DefaultTableModel(null, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3;
            }
        };

        userTable = new JTable(model) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                
                // Đổi màu xen kẽ các dòng
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? TABLE_ROW_BG : TABLE_ALT_ROW_BG);
                }
                
                // Đổi màu chữ
                c.setForeground(TEXT_COLOR);
                
                // Canh lề trái cho tất cả các ô
                if (c instanceof JLabel) {
                    ((JLabel) c).setHorizontalAlignment(SwingConstants.LEFT);
                }
                
                return c;
            }
        };

        // Thiết lập style cho bảng
        userTable.setSelectionBackground(ACCENT_COLOR.darker());
        userTable.setSelectionForeground(Color.WHITE);
        userTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        userTable.setRowHeight(30);
        userTable.setShowGrid(false);
        userTable.setIntercellSpacing(new Dimension(0, 0));
        
        // Thiết lập style cho header bảng
        JTableHeader header = userTable.getTableHeader();
        header.setBackground(TABLE_HEADER_BG);
        header.setForeground(ACCENT_COLOR);
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBorder(BorderFactory.createLineBorder(new Color(50, 50, 70)));
        header.setReorderingAllowed(false);
        
        // Canh lề trái cho tiêu đề cột
        ((DefaultTableCellRenderer)header.getDefaultRenderer()).setHorizontalAlignment(SwingConstants.LEFT);

        JScrollPane scrollPane = new JScrollPane(userTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(DARK_BG);
        add(scrollPane, BorderLayout.CENTER);
        
        // Thêm nút chi tiết với căn lề trái
        userTable.getColumn("CHI TIẾT").setCellRenderer(new ButtonRenderer());
        userTable.getColumn("CHI TIẾT").setCellEditor(new ButtonEditor(new JCheckBox()));
        
        // Thiết lập căn lề trái cho các cột còn lại
        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
        leftRenderer.setHorizontalAlignment(JLabel.LEFT);
        userTable.getColumnModel().getColumn(0).setCellRenderer(leftRenderer); // USERNAME
        userTable.getColumnModel().getColumn(1).setCellRenderer(leftRenderer); // BALANCE
        userTable.getColumnModel().getColumn(2).setCellRenderer(leftRenderer); // LAST ONLINE

        // Panel nút điều khiển
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlPanel.setBackground(new Color(20, 20, 35));
        controlPanel.setBorder(new EmptyBorder(10, 20, 10, 0));
        
        btnBack = createGamingButton("QUAY LẠI", new Color(150, 0, 255));
        btnBack.addActionListener(e -> {
            new GiaoDienAdmin().setVisible(true);
            dispose();
        });
        controlPanel.add(btnBack);
        
        JButton btnRefresh = createGamingButton("LÀM MỚI", ACCENT_COLOR);
        btnRefresh.addActionListener(e -> loadUserData());
        controlPanel.add(btnRefresh);
        
        add(controlPanel, BorderLayout.SOUTH);

        loadUserData();
    }

    private JButton createGamingButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color.darker(), 2),
            BorderFactory.createEmptyBorder(8, 20, 8, 20)
        ));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(color.brighter());
            }
            
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
            }
        });
        
        return button;
    }

    private void loadUserData() {
        rowUserIdMap.clear();

        try (Connection conn = KetNoiCSDL.getConnection()) {
            String sql = "SELECT u.id, u.username, " +
                    "COALESCE(SUM(d.amount), 0) AS total_deposit, " +
                    "COALESCE((SELECT SUM(m.total_amount) FROM machine_usage m WHERE m.user_id = u.id), 0) AS total_usage, " +
                    "(SELECT MAX(m.start_time) FROM machine_usage m WHERE m.user_id = u.id) AS last_usage " +
                    "FROM users u " +
                    "LEFT JOIN deposit d ON u.id = d.user_id " +
                    "WHERE u.role != 'admin' " +
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

                    String lastUsageStr = (lastUsage != null) ? lastUsage.toString() : "CHƯA SỬ DỤNG";

                    model.addRow(new Object[]{
                            username,
                            String.format("%,.0f", balance),
                            lastUsageStr,
                            "XEM CHI TIẾT"
                    });

                    rowUserIdMap.put(rowIndex, userId);
                    rowIndex++;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Lỗi khi tải dữ liệu người dùng: " + e.getMessage(), 
                "Lỗi", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
            setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 10)); // Giảm padding bên trái
            setFont(new Font("Segoe UI", Font.BOLD, 12));
            setHorizontalAlignment(SwingConstants.LEFT); // Canh lề trái
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            setText(value.toString());
            setBackground(isSelected ? ACCENT_COLOR.darker() : new Color(70, 70, 90));
            setForeground(Color.WHITE);
            return this;
        }
    }

    private class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String username;
        private int userId;
        private boolean clicked;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton("XEM CHI TIẾT");
            button.setOpaque(true);
            button.setBackground(new Color(70, 70, 90));
            button.setForeground(Color.WHITE);
            button.setFont(new Font("Segoe UI", Font.BOLD, 12));
            button.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 10)); // Giảm padding bên trái
            button.setHorizontalAlignment(SwingConstants.LEFT); // Canh lề trái
            button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            username = (String) table.getValueAt(row, 0);
            userId = rowUserIdMap.get(row);
            clicked = true;
            button.setBackground(ACCENT_COLOR);
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (clicked) {
                UserHistoryDialog historyDialog = new UserHistoryDialog(UserManagementForm.this, userId, username);
                historyDialog.setVisible(true);
            }
            clicked = false;
            return "XEM CHI TIẾT";
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            UserManagementForm form = new UserManagementForm();
            form.setVisible(true);
        });
    }
}

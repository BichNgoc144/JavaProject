package BT_Project.QuanLyQuanNet;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.text.NumberFormat;
import java.util.Locale;

public class UsageHistoryForm extends JFrame {

    private JTable historyTable;
    private DefaultTableModel historyModel;
    private int userId;

    private final Color DARK_BG = new Color(15, 15, 25);
    private final Color NEON_BLUE = new Color(0, 200, 255);
    private final Color NEON_PURPLE = new Color(170, 0, 255);
    private final Color NEON_GREEN = new Color(0, 255, 100);
    private final Color TABLE_ROW1 = new Color(30, 30, 50);
    private final Color TABLE_ROW2 = new Color(40, 40, 60);

    public UsageHistoryForm(int userId) {
        this.userId = userId;
        
        // Frame setup
        setTitle("SESSION HISTORY - NET SYSTEM");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        // Main panel with gaming background
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(DARK_BG);
        
        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        JLabel titleLabel = new JLabel("SESSION HISTORY", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Impact", Font.BOLD, 24));
        titleLabel.setForeground(NEON_BLUE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Table setup
        String[] columnNames = {"MÁY", "BẮT ĐẦU", "KẾT THÚC", "TỔNG TIỀN (VND)"};
        historyModel = new DefaultTableModel(null, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        historyTable = new JTable(historyModel) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? TABLE_ROW1 : TABLE_ROW2);
                    c.setForeground(Color.WHITE);
                    
                    // Highlight "In Use" sessions
                    if (column == 2 && getValueAt(row, column).equals("IN USE")) {
                        c.setForeground(NEON_GREEN);
                    }
                    
                    // Format currency column
                    if (column == 3) {
                        ((JLabel)c).setHorizontalAlignment(SwingConstants.RIGHT);
                    }
                }
                return c;
            }
        };
        
        // Table styling
        historyTable.setFont(new Font("Consolas", Font.PLAIN, 14));
        historyTable.setSelectionBackground(NEON_PURPLE);
        historyTable.setSelectionForeground(Color.BLACK);
        historyTable.setGridColor(new Color(60, 60, 80));
        historyTable.setRowHeight(30);
        historyTable.setShowGrid(true);
        historyTable.setIntercellSpacing(new Dimension(0, 1));
        
        // Header styling
        JTableHeader header = historyTable.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 14));
        header.setBackground(DARK_BG);
        header.setForeground(NEON_BLUE);
        header.setBorder(BorderFactory.createLineBorder(new Color(50, 50, 80)));
        
        // Scroll pane with glow effect
        JScrollPane scrollPane = new JScrollPane(historyTable);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
        	    BorderFactory.createLineBorder(new Color(0, 100, 255, 100)), 
        	    BorderFactory.createLineBorder(DARK_BG, 3)
        	));
        scrollPane.getViewport().setBackground(DARK_BG);
        
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Footer with home button
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setOpaque(false);
        footerPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        JButton btnHome = createGamingButton("QUAY LẠI");
        btnHome.addActionListener(e -> {
            new GiaoDienUser(userId).setVisible(true);
            dispose();
        });
        
        footerPanel.add(btnHome);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        loadHistory(userId);
    }

    private JButton createGamingButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                
                // Button background
                if (getModel().isPressed()) {
                    g2.setColor(NEON_PURPLE.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(NEON_PURPLE.brighter());
                } else {
                    g2.setColor(new Color(NEON_PURPLE.getRed(), NEON_PURPLE.getGreen(), 
                                       NEON_PURPLE.getBlue(), 150));
                }
                
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                
                // Button border
                g2.setColor(NEON_PURPLE);
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                
                // Button text
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Arial", Font.BOLD, 12));
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString(getText(), x, y);
                
                g2.dispose();
            }
        };
        
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(200, 40));
        
        return button;
    }

    private void loadHistory(int userId) {
        try (Connection conn = KetNoiCSDL.getConnection()) {
            String sql = "SELECT machine_name, start_time, end_time, total_amount " +
                    "FROM machine_usage WHERE user_id = ? ORDER BY start_time DESC";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, userId);
                try (ResultSet rs = stmt.executeQuery()) {
                    historyModel.setRowCount(0);
                    NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
                    
                    while (rs.next()) {
                        String machineName = rs.getString("machine_name");
                        Timestamp start = rs.getTimestamp("start_time");
                        Timestamp end = rs.getTimestamp("end_time");
                        double total = rs.getDouble("total_amount");

                        String startStr = (start != null) ? start.toString() : "N/A";
                        String endStr = (end != null) ? end.toString() : "IN USE";
                        String totalStr = (total > 0) ? currencyFormat.format(total) : "-";
                        
                        historyModel.addRow(new Object[]{machineName, startStr, endStr, totalStr});
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "ERROR LOADING GAME HISTORY!", "SYSTEM ERROR", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new UsageHistoryForm(1).setVisible(true));
    }
}
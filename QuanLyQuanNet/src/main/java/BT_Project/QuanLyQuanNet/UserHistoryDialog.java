package BT_Project.QuanLyQuanNet;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

import java.awt.*;
import java.sql.*;

public class UserHistoryDialog extends JDialog {

    public UserHistoryDialog(JFrame parent, int userId, String username) {
        super(parent, "PLAYER HISTORY: " + username.toUpperCase(), true);
        setSize(800, 600);
        setLocationRelativeTo(parent);
        
        Color darkBlue = new Color(10, 10, 40);
        Color neonBlue = new Color(0, 200, 255);
        Color neonPurple = new Color(170, 0, 255);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(darkBlue);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("PLAYER HISTORY: " + username.toUpperCase());
        titleLabel.setFont(new Font("Consolas", Font.BOLD, 20));
        titleLabel.setForeground(neonBlue);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        String[] columnNames = {"MÁY", "BẮT ĐẦU", "KẾT THÚC", "TỔNG TIỀN (VND)"};
        DefaultTableModel model = new DefaultTableModel(null, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable historyTable = new JTable(model) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (isRowSelected(row)) {
                    c.setBackground(neonPurple);
                    c.setForeground(Color.BLACK);
                } else {
                    c.setBackground(row % 2 == 0 ? new Color(20, 20, 60) : new Color(30, 30, 70));
                    c.setForeground(row % 2 == 0 ? neonBlue : Color.WHITE);
                }
                return c;
            }
        };
        
        historyTable.setFont(new Font("Consolas", Font.PLAIN, 14));
        historyTable.setSelectionBackground(neonPurple);
        historyTable.setSelectionForeground(Color.BLACK);
        historyTable.setGridColor(new Color(50, 50, 100));
        historyTable.setShowGrid(true);
        historyTable.setRowHeight(30);
        historyTable.setIntercellSpacing(new Dimension(0, 1));
        
        JTableHeader header = historyTable.getTableHeader();
        header.setFont(new Font("Consolas", Font.BOLD, 14));
        header.setBackground(darkBlue);
        header.setForeground(neonBlue);
        header.setBorder(BorderFactory.createLineBorder(new Color(50, 50, 100)));
        
        JScrollPane scrollPane = new JScrollPane(historyTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(darkBlue);
        
        // Add glow effect to scroll pane
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
        	    BorderFactory.createLineBorder(new Color(0, 100, 255, 100)), 
        	    BorderFactory.createLineBorder(darkBlue, 3)
        	));
        
        // Add components to main panel
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Footer with gaming stats
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setBackground(darkBlue);
        footerPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        JLabel statsLabel = new JLabel("SYSTEM ONLINE | NETCAFE v1.0");
        statsLabel.setFont(new Font("Consolas", Font.ITALIC, 12));
        statsLabel.setForeground(new Color(150, 150, 150));
        
        footerPanel.add(statsLabel);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
       
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
                        String endStr = (end != null) ? end.toString() : "IN-GAME";
                        String totalStr = String.format("%,.0f", total);

                        model.addRow(new Object[]{machineName, startStr, endStr, totalStr});
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "ERROR LOADING PLAYER HISTORY!", "SYSTEM ERROR", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
}

package BT_Project.QuanLyQuanNet;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;
import org.jfree.chart.*;
import org.jfree.chart.plot.*;
import org.jfree.data.category.*;

public class RevenueStatisticsForm extends JFrame {
    private static final Color DARK_BG = new Color(15, 15, 25);
    private static final Color ACCENT_COLOR = new Color(0, 200, 255);
    private static final Color TEXT_COLOR = new Color(220, 220, 220);
    private static final Color TABLE_HEADER_BG = new Color(30, 30, 45);
    private static final Color TABLE_ROW_BG = new Color(25, 25, 40);
    
    private DefaultTableModel tableModel;
    private JPanel chartPanel;
    private JTable revenueTable;
    private JComboBox<String> timeRangeCombo;
    private JButton btnBack;

    public RevenueStatisticsForm() {
        setTitle("THỐNG KÊ DOANH THU - GAME CENTER ADMIN");
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        getContentPane().setBackground(DARK_BG);
        setLayout(new BorderLayout(10, 10));
        

        // Panel tiêu đề
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(20, 20, 35));
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, ACCENT_COLOR));
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        
        JLabel titleLabel = new JLabel("THỐNG KÊ DOANH THU");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(ACCENT_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBorder(new EmptyBorder(15, 0, 10, 0));
        
        JLabel subTitleLabel = new JLabel("Phân tích doanh thu theo ngày/tháng");
        subTitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subTitleLabel.setForeground(TEXT_COLOR);
        subTitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subTitleLabel.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        headerPanel.add(titleLabel);
        headerPanel.add(subTitleLabel);
        add(headerPanel, BorderLayout.NORTH);

        // Panel điều khiển
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        controlPanel.setBackground(new Color(20, 20, 35));
        controlPanel.setBorder(new EmptyBorder(10, 0, 10, 0));
        
        timeRangeCombo = new JComboBox<>(new String[]{"Theo ngày", "Theo tháng"});
        styleComboBox(timeRangeCombo);
        
        btnBack = createGamingButton("QUAY LẠI", new Color(150, 0, 255));
        JButton btnRefresh = createGamingButton("LÀM MỚI", ACCENT_COLOR);
        
        controlPanel.add(new JLabel("Xem theo:"));
        controlPanel.add(timeRangeCombo);
        controlPanel.add(btnRefresh);
        controlPanel.add(btnBack);
        add(controlPanel, BorderLayout.SOUTH);

        // Panel chính chứa bảng và biểu đồ
        JPanel mainContentPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        mainContentPanel.setBackground(DARK_BG);
        mainContentPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Bảng dữ liệu
        tableModel = new DefaultTableModel(null, new String[]{"Thời gian", "Doanh thu (VNĐ)"}) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        revenueTable = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                c.setBackground(row % 2 == 0 ? TABLE_ROW_BG : new Color(30, 30, 50));
                c.setForeground(TEXT_COLOR);
                return c;
            }
        };
        
        // Thiết lập style cho bảng
        revenueTable.setSelectionBackground(ACCENT_COLOR.darker());
        revenueTable.setSelectionForeground(Color.WHITE);
        revenueTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        revenueTable.setRowHeight(25);
        revenueTable.setShowGrid(false);
        
        // Thiết lập style cho header bảng
        JTableHeader header = revenueTable.getTableHeader();
        header.setBackground(TABLE_HEADER_BG);
        header.setForeground(ACCENT_COLOR);
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBorder(BorderFactory.createLineBorder(new Color(50, 50, 70)));
        
        JScrollPane tableScrollPane = new JScrollPane(revenueTable);
        tableScrollPane.setBorder(BorderFactory.createLineBorder(new Color(50, 50, 70)));
        mainContentPanel.add(tableScrollPane);

        // Panel biểu đồ
        chartPanel = new JPanel(new BorderLayout());
        chartPanel.setBorder(BorderFactory.createLineBorder(new Color(50, 50, 70)));
        mainContentPanel.add(chartPanel);

        add(mainContentPanel, BorderLayout.CENTER);

        // Sự kiện
        timeRangeCombo.addActionListener(e -> {
            String selection = (String) timeRangeCombo.getSelectedItem();
            if ("Theo ngày".equals(selection)) {
                loadDailyRevenue();
            } else {
                loadMonthlyRevenue();
            }
        });

        btnBack.addActionListener(e -> {
            new GiaoDienAdmin().setVisible(true);
            dispose();
        });

        btnRefresh.addActionListener(e -> {
            String selection = (String) timeRangeCombo.getSelectedItem();
            if ("Theo ngày".equals(selection)) {
                loadDailyRevenue();
            } else {
                loadMonthlyRevenue();
            }
        });

        loadDailyRevenue();
    }

    private void styleComboBox(JComboBox<String> comboBox) {
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        comboBox.setBackground(new Color(30, 30, 45));
        comboBox.setForeground(TEXT_COLOR);
        comboBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(50, 50, 70)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, 
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setBackground(isSelected ? ACCENT_COLOR : new Color(30, 30, 45));
                setForeground(isSelected ? Color.BLACK : TEXT_COLOR);
                return this;
            }
        });
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

    private void loadDailyRevenue() {
        String[] columns = {"Ngày", "Doanh thu (VNĐ)"};
        tableModel.setColumnIdentifiers(columns);

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        try (Connection conn = KetNoiCSDL.getConnection()) {
            String sql = "SELECT DATE(start_time) AS date, SUM(total_amount) AS total_revenue " +
                    "FROM machine_usage " +
                    "WHERE total_amount > 0 " +
                    "GROUP BY DATE(start_time) " +
                    "ORDER BY date DESC LIMIT 30"; // Giới hạn 30 ngày gần nhất
            
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                tableModel.setRowCount(0);
                while (rs.next()) {
                    String date = rs.getString("date");
                    double revenue = rs.getDouble("total_revenue");
                    tableModel.addRow(new Object[]{date, String.format("%,.0f", revenue)});
                    dataset.addValue(revenue, "Doanh thu", date);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Lỗi khi tải dữ liệu doanh thu ngày: " + e.getMessage(), 
                "Lỗi", 
                JOptionPane.ERROR_MESSAGE);
        }

        createChart(dataset, "DOANH THU THEO NGÀY", "Ngày", "VNĐ");
    }

    private void loadMonthlyRevenue() {
        String[] columns = {"Tháng", "Doanh thu (VNĐ)"};
        tableModel.setColumnIdentifiers(columns);

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        try (Connection conn = KetNoiCSDL.getConnection()) {
            String sql = "SELECT DATE_FORMAT(start_time, '%Y-%m') AS month, SUM(total_amount) AS total_revenue " +
                    "FROM machine_usage " +
                    "WHERE total_amount > 0 " +
                    "GROUP BY DATE_FORMAT(start_time, '%Y-%m') " +
                    "ORDER BY month DESC LIMIT 12"; // Giới hạn 12 tháng gần nhất
            
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                tableModel.setRowCount(0);
                while (rs.next()) {
                    String month = rs.getString("month");
                    double revenue = rs.getDouble("total_revenue");
                    tableModel.addRow(new Object[]{month, String.format("%,.0f", revenue)});
                    dataset.addValue(revenue, "Doanh thu", month);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Lỗi khi tải dữ liệu doanh thu tháng: " + e.getMessage(), 
                "Lỗi", 
                JOptionPane.ERROR_MESSAGE);
        }

        createChart(dataset, "DOANH THU THEO THÁNG", "Tháng", "VNĐ");
    }

    private void createChart(DefaultCategoryDataset dataset, String title, String xAxis, String yAxis) {
        JFreeChart chart = ChartFactory.createBarChart(
                title,
                xAxis,
                yAxis,
                dataset
        );

        // Thiết lập màu sắc cho biểu đồ
        chart.setBackgroundPaint(DARK_BG);
        chart.getTitle().setPaint(ACCENT_COLOR);
        chart.getTitle().setFont(new Font("Segoe UI", Font.BOLD, 18));
        
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(new Color(25, 25, 40));
        plot.setRangeGridlinePaint(new Color(80, 80, 80));
        plot.setDomainGridlinePaint(new Color(80, 80, 80));
        plot.getRenderer().setSeriesPaint(0, ACCENT_COLOR);
        
        // Thiết lập màu sắc cho các trục
        plot.getDomainAxis().setLabelPaint(TEXT_COLOR);
        plot.getDomainAxis().setTickLabelPaint(TEXT_COLOR);
        plot.getRangeAxis().setLabelPaint(TEXT_COLOR);
        plot.getRangeAxis().setTickLabelPaint(TEXT_COLOR);

        chartPanel.removeAll();
        chartPanel.add(new ChartPanel(chart) {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(600, 400);
            }
        }, BorderLayout.CENTER);
        chartPanel.validate();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            RevenueStatisticsForm form = new RevenueStatisticsForm();
            form.setVisible(true);
        });
    }
}

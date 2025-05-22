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
    private JButton btnRefresh;
    private JButton btnBack;
    private JButton btnDailyView;
    private JButton btnMonthlyView;
    private boolean isMonthlyView = false;

    public RevenueStatisticsForm() {
        setTitle("THỐNG KÊ DOANH THU - GAME CENTER ADMIN");
        setSize(800, 600);
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

        // Panel chính chứa bảng và biểu đồ
        JPanel mainContentPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        mainContentPanel.setBackground(DARK_BG);
        mainContentPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Bảng dữ liệu
        tableModel = new DefaultTableModel(null, new String[]{"Ngày", "Doanh thu (VNĐ)"}) {
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
        chartPanel.setBackground(new Color(25, 25, 40));
        chartPanel.setBorder(BorderFactory.createLineBorder(new Color(50, 50, 70)));
        mainContentPanel.add(chartPanel);

        add(mainContentPanel, BorderLayout.CENTER);
        
        // Panel chọn chế độ xem
        JPanel viewPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        viewPanel.setBackground(new Color(20, 20, 35));
        viewPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(ACCENT_COLOR), 
            "CHẾ ĐỘ XEM", 
            TitledBorder.CENTER, 
            TitledBorder.TOP, 
            new Font("Segoe UI", Font.BOLD, 12), 
            ACCENT_COLOR
        ));
        
        btnDailyView = createGamingButton("THEO NGÀY", new Color(0, 150, 200));
        btnMonthlyView = createGamingButton("THEO THÁNG", new Color(0, 150, 200));
        btnDailyView.setEnabled(false); // Mặc định là chế độ xem theo ngày
        
        viewPanel.add(btnDailyView);
        viewPanel.add(btnMonthlyView);
        
        // Footer panel with buttons
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(new Color(20, 20, 35));
        footerPanel.setBorder(new EmptyBorder(5, 10, 5, 10));
        
        footerPanel.add(viewPanel, BorderLayout.NORTH);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(new Color(20, 20, 35));
        
        btnRefresh = createGamingButton("LÀM MỚI", ACCENT_COLOR);
        btnBack = createGamingButton("QUAY LẠI", new Color(150, 0, 255));
        
        buttonPanel.add(btnRefresh);
        buttonPanel.add(btnBack);
        footerPanel.add(buttonPanel, BorderLayout.CENTER);
        
        add(footerPanel, BorderLayout.SOUTH);

        // Sự kiện
        btnBack.addActionListener(e -> {
            new GiaoDienAdmin().setVisible(true);
            dispose();
        });

        btnRefresh.addActionListener(e -> {
            if (isMonthlyView) {
                loadMonthlyRevenue();
            } else {
                loadDailyRevenue();
            }
        });
        
        btnDailyView.addActionListener(e -> {
            if (isMonthlyView) { // Chỉ thực hiện khi đang ở chế độ xem theo tháng
                isMonthlyView = false;
                btnDailyView.setEnabled(false);
                btnMonthlyView.setEnabled(true);
                loadDailyRevenue();
            }
        });
        
        btnMonthlyView.addActionListener(e -> {
            if (!isMonthlyView) { // Chỉ thực hiện khi đang ở chế độ xem theo ngày
                isMonthlyView = true;
                btnMonthlyView.setEnabled(false);
                btnDailyView.setEnabled(true);
                loadMonthlyRevenue();
            }
        });

        loadDailyRevenue();
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
        tableModel.setRowCount(0);

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        try (Connection conn = KetNoiCSDL.getConnection()) {
            // Fix SQL error by making sure all columns in SELECT are in GROUP BY or using aggregate functions
            String sql = "SELECT DATE(start_time) AS date, SUM(total_amount) AS total_revenue " +
                    "FROM machine_usage " +
                    "WHERE total_amount > 0 " +
                    "GROUP BY DATE(start_time) " +
                    "ORDER BY date DESC LIMIT 30"; // Giới hạn 30 ngày gần nhất
            
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
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
        tableModel.setRowCount(0);

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        try (Connection conn = KetNoiCSDL.getConnection()) {
            // SQL để lấy doanh thu theo tháng
            String sql = "SELECT DATE_FORMAT(start_time, '%Y-%m') AS month, " +
                         "SUM(total_amount) AS total_revenue " +
                         "FROM machine_usage " +
                         "WHERE total_amount > 0 " +
                         "GROUP BY DATE_FORMAT(start_time, '%Y-%m') " +
                         "ORDER BY month DESC LIMIT 24"; // Giới hạn 24 tháng gần nhất
            
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    String month = rs.getString("month");
                    double revenue = rs.getDouble("total_revenue");
                    
                    // Định dạng tháng cho dễ đọc
                    String formattedMonth = month;
                    try {
                        String[] parts = month.split("-");
                        int year = Integer.parseInt(parts[0]);
                        int monthNum = Integer.parseInt(parts[1]);
                        formattedMonth = "Tháng " + monthNum + "/" + year;
                    } catch (Exception e) {
                        // Nếu lỗi định dạng, giữ nguyên giá trị ban đầu
                    }
                    
                    tableModel.addRow(new Object[]{formattedMonth, String.format("%,.0f", revenue)});
                    dataset.addValue(revenue, "Doanh thu", formattedMonth);
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

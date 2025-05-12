package BT_Project.QuanLyQuanNet;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

public class RevenueStatisticsForm extends JFrame {

    private DefaultTableModel tableModel;
    private JPanel chartPanel;
    private JTable revenueTable;
    private JComboBox<String> comboBox;

    public RevenueStatisticsForm() {
        setTitle("Revenue Statistics");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Top panel: ComboBox + Home button
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        comboBox = new JComboBox<>(new String[]{"By Day", "By Month"});
        topPanel.add(new JLabel("Select View:"));
        topPanel.add(comboBox);

        JButton btnHome = new JButton("Home");
        topPanel.add(btnHome);

        add(topPanel, BorderLayout.NORTH);

        // Table + Chart
        tableModel = new DefaultTableModel(null, new String[]{"Time", "Total Revenue (VND)"});
        revenueTable = new JTable(tableModel);
        add(new JScrollPane(revenueTable), BorderLayout.WEST);

        chartPanel = new JPanel(new BorderLayout());
        add(chartPanel, BorderLayout.CENTER);

        // Event: ComboBox selection change
        comboBox.addActionListener(e -> {
            String selection = (String) comboBox.getSelectedItem();
            if ("By Day".equals(selection)) {
                loadDailyRevenue();
            } else {
                loadMonthlyRevenue();
            }
        });

        // Event: Home button
        btnHome.addActionListener(e -> {
            new GiaoDienAdmin().setVisible(true);
            dispose();
        });

        // Load default: By Day
        loadDailyRevenue();
    }

    // Load revenue by day
    private void loadDailyRevenue() {
        String[] columns = {"Date", "Total Revenue (VND)"};
        tableModel.setColumnIdentifiers(columns);

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        try (Connection conn = KetNoiCSDL.getConnection()) {
            String sql = "SELECT DATE(start_time) AS date, SUM(total_amount) AS total_revenue " +
                    "FROM machine_usage " +
                    "WHERE total_amount > 0 " +
                    "GROUP BY DATE(start_time) " +
                    "ORDER BY date ASC";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                tableModel.setRowCount(0);
                while (rs.next()) {
                    String date = rs.getString("date");
                    double revenue = rs.getDouble("total_revenue");
                    tableModel.addRow(new Object[]{date, String.format("%.0f", revenue)});
                    dataset.addValue(revenue, "Revenue", date);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading daily statistics!");
        }

        // Draw chart (Line Chart)
        JFreeChart chart = ChartFactory.createBarChart(
                "Daily Revenue",
                "Date",
                "Revenue (VND)",
                dataset
        );

        chartPanel.removeAll();
        chartPanel.add(new ChartPanel(chart), BorderLayout.CENTER);
        chartPanel.validate();
    }

    // Load revenue by month
    private void loadMonthlyRevenue() {
        String[] columns = {"Month", "Total Revenue (VND)"};
        tableModel.setColumnIdentifiers(columns);

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        try (Connection conn = KetNoiCSDL.getConnection()) {
            String sql = "SELECT DATE_FORMAT(start_time, '%Y-%m') AS month, SUM(total_amount) AS total_revenue " +
                    "FROM machine_usage " +
                    "WHERE total_amount > 0 " +
                    "GROUP BY DATE_FORMAT(start_time, '%Y-%m') " +
                    "ORDER BY month ASC";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                tableModel.setRowCount(0);
                while (rs.next()) {
                    String month = rs.getString("month");
                    double revenue = rs.getDouble("total_revenue");
                    tableModel.addRow(new Object[]{month, String.format("%.0f", revenue)});
                    dataset.addValue(revenue, "Revenue", month);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading monthly statistics!");
        }

        // Draw chart (Line Chart)
        JFreeChart chart = ChartFactory.createBarChart(
                "Monthly Revenue",
                "Month",
                "Revenue (VND)",
                dataset
        );

        chartPanel.removeAll();
        chartPanel.add(new ChartPanel(chart), BorderLayout.CENTER);
        chartPanel.validate();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RevenueStatisticsForm().setVisible(true));
    }
}

package BT_Project.QuanLyQuanNet;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;

import java.text.NumberFormat;
import java.util.Locale;

public class GiaoDienUser extends JFrame {
    private int userId;
    private JLabel balanceLabel;

    public GiaoDienUser() {
        this(1);
    }

    public GiaoDienUser(int userId) {
    	System.out.println("init userID "+ userId );
        this.userId = userId;
        setTitle("User - Quản lý Quán Net");
        setSize(450, 320);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(20, 30, 20, 30)); // padding

        JLabel welcomeLabel = new JLabel("Chào mừng bạn đến quán của chúng tôi!", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 17));
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(welcomeLabel);

        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        balanceLabel = new JLabel("", SwingConstants.CENTER);
        balanceLabel.setFont(new Font("Arial", Font.BOLD, 16));
        balanceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        updateBalanceDisplay();
        mainPanel.add(balanceLabel);

        mainPanel.add(Box.createRigidArea(new Dimension(0, 25)));

        JButton useMachineButton = new JButton("Sử dụng máy");
        JButton depositButton = new JButton("Nạp tiền");
        JButton viewHistoryButton = new JButton("Xem lịch sử");
        JButton btnLogout = new JButton("Thoát tài khoản");

        Font btnFont = new Font("Arial", Font.PLAIN, 15);
        JButton[] buttons = { useMachineButton, depositButton, viewHistoryButton, btnLogout };

        for (JButton btn : buttons) {
            btn.setFont(btnFont);
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            btn.setMaximumSize(new Dimension(200, 40));
            mainPanel.add(btn);
            mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        useMachineButton.addActionListener(e -> {
            new UseMachineForm(this.userId).setVisible(true);
            dispose();
        });

        depositButton.addActionListener(e -> {
            new DepositForm(this.userId).setVisible(true);
            dispose();
        });

        viewHistoryButton.addActionListener(e -> new UsageHistoryForm(userId).setVisible(true));

        btnLogout.addActionListener(e -> {
            new HomePage().setVisible(true);
            dispose();
        });

        add(mainPanel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new GiaoDienUser().setVisible(true);
        });
    }

    private double getTotalDeposit() {
        double totalDeposit = 0.0;

        try (Connection conn = KetNoiCSDL.getConnection()) {
            String sql = "SELECT SUM(amount) AS total FROM deposit WHERE user_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            System.out.println("userId " + userId + " " + this.userId) ;
            stmt.setInt(1, userId);
            System.out.println("userId " + userId + " " + this.userId) ;

            ResultSet rs = stmt.executeQuery();

            if (rs.next() && rs.getObject("total") != null) {
                totalDeposit = rs.getDouble("total");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi truy vấn số dư: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }

        return totalDeposit;
    }

    private double getTotalSpent() {
        double totalSpent = 0.0;

        try (Connection conn = KetNoiCSDL.getConnection()) {
            String sql = "SELECT SUM(total_amount) AS total FROM machine_usage WHERE user_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next() && rs.getObject("total") != null) {
                totalSpent = rs.getDouble("total");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi truy vấn chi tiêu: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }

        return totalSpent;
    }

    public void updateBalanceDisplay() {
        double totalDeposit = getTotalDeposit();
        double totalSpent = getTotalSpent();
        double currentBalance = totalDeposit - totalSpent;

        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        String formattedBalance = currencyFormatter.format(currentBalance);
        System.out.println("++"+ totalDeposit);
        System.out.println("--"+totalSpent );


        System.out.println(formattedBalance);
        balanceLabel.setText("Số dư hiện tại: " + formattedBalance);

        if (currentBalance > 0) {
            balanceLabel.setForeground(new Color(0, 128, 0));
        } else if (currentBalance == 0) {
            balanceLabel.setForeground(Color.BLACK);
        } else {
            balanceLabel.setForeground(Color.RED);
        }
    }
}

package BT_Project.QuanLyQuanNet;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GiaoDienUser extends JFrame {
    public GiaoDienUser() {
        setTitle("User - Quản lý Quán Net");
        setSize(400, 250);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Thiết lập layout
        setLayout(new BorderLayout(10, 10));

        // Thêm JLabel thông báo
        JLabel label = new JLabel("Chào mừng bạn đến quán của chúng tôi!", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        add(label, BorderLayout.CENTER);

        // Panel chứa 2 nút
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(2, 1, 10, 10));  // 2 dòng, 1 cột

        // Nút "Sử dụng máy"
        JButton useMachineButton = new JButton("Sử dụng máy");
        useMachineButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new UseMachineForm().setVisible(true);
                dispose();  // Đóng form hiện tại
            }
        });

        // Nút "Nạp tiền"
        JButton depositButton = new JButton("Nạp tiền");
        depositButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new DepositForm().setVisible(true);
                dispose();  // Đóng form hiện tại nếu muốn
            }
        });
        
        JButton viewHistoryButton = new JButton("Xem lịch sử");
        viewHistoryButton.addActionListener(e -> new UsageHistoryForm(1).setVisible(true)); 

        
     // Nút Thoát tài khoản
        JButton btnLogout = new JButton("Thoát tài khoản");
        btnLogout.addActionListener(e -> {
            new HomePage().setVisible(true);
            dispose();  // Đóng giao diện Admin
        });

        // Thêm nút vào panel
        buttonPanel.add(useMachineButton);
        buttonPanel.add(depositButton);
        buttonPanel.add(viewHistoryButton);
        buttonPanel.add(btnLogout);

        // Thêm panel vào BorderLayout.SOUTH
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new GiaoDienUser().setVisible(true);
        });
    }
}

package BT_Project.QuanLyQuanNet;

import javax.swing.*;
import java.awt.*;

public class GiaoDienAdmin extends JFrame {
    public GiaoDienAdmin() {
        setTitle("Giao diện Admin");
        setSize(400, 200);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(3, 1, 10, 10));  // 3 nút thẳng hàng

        // Nút Quản lý máy
        JButton btnManageMachines = new JButton("Quản lý máy");
        btnManageMachines.addActionListener(e -> {
            new MachineCreationForm().setVisible(true);
            dispose();  // Đóng giao diện Admin nếu muốn
        });

        // Nút Quản lý người dùng
        JButton btnManageUsers = new JButton("Quản lý người dùng");
        btnManageUsers.addActionListener(e -> {
            new UserManagementForm().setVisible(true);
            dispose();  // Đóng giao diện Admin nếu muốn
        });
        
        //Thống kê doanh thu
        JButton btnStatistics = new JButton("Thống kê doanh thu");
        btnStatistics.addActionListener(e -> {
            new RevenueStatisticsForm().setVisible(true);
            dispose();  // Đóng giao diện Admin
        });

        // Nút Thoát tài khoản
        JButton btnLogout = new JButton("Thoát tài khoản");
        btnLogout.addActionListener(e -> {
            new HomePage().setVisible(true);
            dispose();  // Đóng giao diện Admin
        });

        // Thêm nút vào form
        add(btnManageMachines);
        add(btnManageUsers);
        add(btnStatistics);
        add(btnLogout);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GiaoDienAdmin().setVisible(true));
    }
}


package BT_Project.QuanLyQuanNet;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GiaoDienAdmin extends JFrame {
    
   
    private static final Color DARK_BG = new Color(15, 15, 25); 
    private static final Color ACCENT_COLOR = new Color(0, 200, 255); 
    private static final Color SECONDARY_COLOR = new Color(150, 0, 255); 
    private static final Color TEXT_COLOR = new Color(220, 220, 220); 
    private static final Color HOVER_COLOR = new Color(30, 30, 45); 
    
    public GiaoDienAdmin() {
        setTitle("GAME CENTER - ADMIN DASHBOARD");
        setSize(1000, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        
        // Tạo thanh sidebar gaming
        JPanel sidebarPanel = createGamingSidebar();
        add(sidebarPanel, BorderLayout.WEST);
        
        // Panel nội dung chính
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(DARK_BG);
        mainPanel.setLayout(new BorderLayout());
        
        // Header với hiệu ứng gaming
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(20, 20, 35));
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, ACCENT_COLOR));
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        
        JLabel titleLabel = new JLabel("GAME CENTER ADMIN");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(ACCENT_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBorder(new EmptyBorder(20, 0, 10, 0));
        
        JLabel subTitleLabel = new JLabel("Quản lý hệ thống quán game");
        subTitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subTitleLabel.setForeground(TEXT_COLOR);
        subTitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subTitleLabel.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        headerPanel.add(titleLabel);
        headerPanel.add(subTitleLabel);
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Content panel với hiệu ứng grid gaming
        JPanel contentPanel = new JPanel();
        contentPanel.setBackground(DARK_BG);
        contentPanel.setLayout(new GridLayout(2, 2, 20, 20));
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Tạo các card thống kê
        String[] stats = {"Máy đang hoạt động", "Doanh thu hôm nay", "Thành viên online", "Tổng số máy"};
        Color[] colors = {ACCENT_COLOR, SECONDARY_COLOR, new Color(0, 255, 150), new Color(255, 100, 0)};
        
        for (int i = 0; i < stats.length; i++) {
            contentPanel.add(createStatCard(stats[i], "1,250", colors[i]));
        }
        
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        // Footer gaming
        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(new Color(10, 10, 20));
        footerPanel.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, ACCENT_COLOR));
        JLabel footerLabel = new JLabel("© 2023 GAME CENTER - Hệ thống quản lí quán net chất lượng");
        footerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        footerLabel.setForeground(TEXT_COLOR);
        footerLabel.setBorder(new EmptyBorder(10, 20, 10, 20));
        footerPanel.add(footerLabel);
        
        mainPanel.add(footerPanel, BorderLayout.SOUTH);
        add(mainPanel, BorderLayout.CENTER);
    }
    
    private JPanel createGamingSidebar() {
        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setBackground(new Color(20, 20, 35));
        sidebarPanel.setPreferredSize(new Dimension(250, getHeight()));
        sidebarPanel.setLayout(new BorderLayout());
        sidebarPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 2, ACCENT_COLOR));
        
        // Header sidebar
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(25, 25, 40));
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBorder(new EmptyBorder(20, 0, 30, 0));
        
        try {
            ImageIcon logoIcon = new ImageIcon("assets/logo.png");
            JLabel logoLabel = new JLabel(new ImageIcon(
                logoIcon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH)));
            logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            headerPanel.add(logoLabel);
        } catch (Exception e) {
            JLabel logoLabel = new JLabel("GAME CENTER");
            logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
            logoLabel.setForeground(ACCENT_COLOR);
            logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            headerPanel.add(logoLabel);
        }
        
        JLabel adminLabel = new JLabel("ADMIN PANEL");
        adminLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        adminLabel.setForeground(TEXT_COLOR);
        adminLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        adminLabel.setBorder(new EmptyBorder(10, 0, 0, 0));
        headerPanel.add(adminLabel);
        
        sidebarPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Menu chức năng gaming
        JPanel menuPanel = new JPanel();
        menuPanel.setBackground(new Color(20, 20, 35));
        menuPanel.setLayout(new GridLayout(0, 1, 0, 5));
        menuPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        String[] menuItems = {
            "QUẢN LÝ MÁY", 
            "QUẢN LÝ NGƯỜI DÙNG", 
            "DOANH THU & BÁO CÁO", 
            "ĐĂNG XUẤT"
        };
        
        for (String item : menuItems) {
            JButton menuButton = createGamingMenuButton(item);
            menuButton.addActionListener(new MenuButtonListener());
            menuPanel.add(menuButton);
        }
        
        sidebarPanel.add(menuPanel, BorderLayout.CENTER);
        
        return sidebarPanel;
    }
    
    private JButton createGamingMenuButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(TEXT_COLOR);
        button.setBackground(new Color(30, 30, 45));
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(50, 50, 70), 1),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setFocusPainted(false);
        
        // Hiệu ứng hover gaming
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(HOVER_COLOR);
                button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(ACCENT_COLOR, 1),
                    BorderFactory.createEmptyBorder(15, 20, 15, 20)
                ));
            }
            
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(30, 30, 45));
                button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(50, 50, 70), 1),
                    BorderFactory.createEmptyBorder(15, 20, 15, 20)
                ));
            }
        });
        
        return button;
    }
    
    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel();
        card.setBackground(new Color(25, 25, 40));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(50, 50, 70), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        titleLabel.setForeground(TEXT_COLOR);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        valueLabel.setForeground(color);
        valueLabel.setBorder(new EmptyBorder(10, 0, 20, 0));
        
        // Thanh progress bar style gaming
        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setValue((int)(Math.random() * 100));
        progressBar.setForeground(color);
        progressBar.setBackground(new Color(40, 40, 60));
        progressBar.setBorder(BorderFactory.createEmptyBorder());
        progressBar.setStringPainted(true);
        progressBar.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        
        card.add(titleLabel);
        card.add(valueLabel);
        card.add(progressBar);
        
        return card;
    }
    
    private class MenuButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JButton source = (JButton) e.getSource();
            String buttonText = source.getText();
            
            switch (buttonText) {
                case "QUẢN LÝ MÁY":
                    new MachineCreationForm().setVisible(true);
                    dispose();
                    break;
                case "QUẢN LÝ NGƯỜI DÙNG":
                    new UserManagementForm().setVisible(true);
                    dispose();
                    break;
                case "DOANH THU & BÁO CÁO":
                    new RevenueStatisticsForm().setVisible(true);
                    dispose();
                    break;
                case "ĐĂNG XUẤT":
                    new HomePage().setVisible(true);
                    dispose();
                    break;
                default:
                    JOptionPane.showMessageDialog(null, "Chức năng: " + buttonText);
            }
        }
    }
    
    public static void main(String[] args) {
        try {
            // Set look and feel để UI đẹp hơn
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            GiaoDienAdmin adminGUI = new GiaoDienAdmin();
            adminGUI.setVisible(true);
            
            // Hiệu ứng fade in
            adminGUI.setOpacity(0f);
            new Thread(() -> {
                try {
                    for (float i = 0; i <= 1; i += 0.05f) {
                        Thread.sleep(20);
                        final float opacity = i;
                        SwingUtilities.invokeLater(() -> adminGUI.setOpacity(opacity));
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        });
    }
}

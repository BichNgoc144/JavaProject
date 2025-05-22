package BT_Project.QuanLyQuanNet;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GiaoDienAdmin extends JFrame {
    
    private Image backgroundImage;
    private static final Color DARK_BG = new Color(15, 15, 25); 
    private static final Color ACCENT_COLOR = new Color(0, 200, 255); 
    private static final Color SECONDARY_COLOR = new Color(150, 0, 255); 
    private static final Color TEXT_COLOR = new Color(220, 220, 220); 
    private static final Color HOVER_COLOR = new Color(30, 30, 45);
    
    // Stats labels
    private JLabel activeMachinesLabel;
    private JLabel todayRevenueLabel;
    private JLabel onlineUsersLabel;
    private JLabel totalMachinesLabel;
    private Timer statsUpdateTimer;
    
    public GiaoDienAdmin() {
        setTitle("GAME CENTER - ADMIN DASHBOARD");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Main content panel with border layout
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Load gaming background image
        try {
            backgroundImage = new ImageIcon(new URL("https://images.rawpixel.com/image_800/cHJpdmF0ZS9sci9pbWFnZXMvd2Vic2l0ZS8yMDI0LTAzL3Jhd3BpeGVsb2ZmaWNlMjFfYV9taW5pbWFsX2FuZF9sZXNzX2RldGFpbF9pbGx1c3RyYXRpb25fdXNpbmdfbF82MTgxMmRlNS1kYWQxLTQwZTctOTc0My03ODcxZDJmZTMzZmYuanBn.jpg")).getImage();
        } catch (Exception e) {
            e.printStackTrace();
            mainPanel.setBackground(DARK_BG);
        }
        
        // Create a layered pane for background image and content
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(800, 600));
        
        // Background panel with image
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                } else {
                    g.setColor(DARK_BG);
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
                
                // Add semi-transparent overlay
                g.setColor(new Color(15, 15, 25, 180));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        backgroundPanel.setBounds(0, 0, 800, 600);
        layeredPane.add(backgroundPanel, JLayeredPane.DEFAULT_LAYER);
        
        // Content panel - transparent to show background
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        contentPanel.setBounds(0, 0, 800, 600);
        
        // Header panel with title
        JPanel headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, ACCENT_COLOR));
        headerPanel.setPreferredSize(new Dimension(800, 100));
        headerPanel.setLayout(new BorderLayout());
        
        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        
        JLabel titleLabel = new JLabel("GAME CENTER ADMIN");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(ACCENT_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel subTitleLabel = new JLabel("Quản lý hệ thống quán game");
        subTitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subTitleLabel.setForeground(TEXT_COLOR);
        subTitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        titlePanel.add(Box.createVerticalGlue());
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createVerticalStrut(5));
        titlePanel.add(subTitleLabel);
        titlePanel.add(Box.createVerticalGlue());
        
        headerPanel.add(titlePanel, BorderLayout.CENTER);
        
        // Main content with welcome message and buttons
        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(new EmptyBorder(20, 50, 20, 50));
        
        // Welcome text
        JLabel welcomeLabel = new JLabel("WELCOME BACK, ADMIN");
        welcomeLabel.setFont(new Font("Impact", Font.BOLD, 36));
        welcomeLabel.setForeground(new Color(0, 255, 255));
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel systemStatusLabel = new JLabel("SYSTEM ONLINE | READY");
        systemStatusLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        systemStatusLabel.setForeground(new Color(0, 255, 100));
        systemStatusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Stats panel
        JPanel statsPanel = new JPanel();
        statsPanel.setOpaque(false);
        statsPanel.setLayout(new GridLayout(2, 2, 15, 15));
        statsPanel.setBorder(new EmptyBorder(20, 0, 20, 0));
        statsPanel.setMaximumSize(new Dimension(800, 120));
        statsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Add stats boxes
        activeMachinesLabel = new JLabel("0", SwingConstants.CENTER);
        JPanel activeMachinesPanel = createStatBox("MÁY ĐANG HOẠT ĐỘNG", activeMachinesLabel, ACCENT_COLOR);
        
        todayRevenueLabel = new JLabel("0 ₫", SwingConstants.CENTER);
        JPanel todayRevenuePanel = createStatBox("DOANH THU HÔM NAY", todayRevenueLabel, SECONDARY_COLOR);
        
        onlineUsersLabel = new JLabel("0", SwingConstants.CENTER);
        JPanel onlineUsersPanel = createStatBox("NGƯỜI DÙNG ONLINE", onlineUsersLabel, new Color(0, 255, 150));
        
        totalMachinesLabel = new JLabel("0", SwingConstants.CENTER);
        JPanel totalMachinesPanel = createStatBox("TỔNG SỐ MÁY", totalMachinesLabel, new Color(255, 100, 0));
        
        statsPanel.add(activeMachinesPanel);
        statsPanel.add(todayRevenuePanel);
        statsPanel.add(onlineUsersPanel);
        statsPanel.add(totalMachinesPanel);
        
        // Button panel with 2x2 grid
        JPanel buttonGrid = new JPanel();
        buttonGrid.setOpaque(false);
        buttonGrid.setLayout(new GridLayout(2, 2, 20, 20));
        buttonGrid.setMaximumSize(new Dimension(700, 250));
        buttonGrid.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Create buttons with different colors
        JButton machineButton = createActionButton("QUẢN LÝ MÁY", new Color(0, 200, 255));
        JButton userButton = createActionButton("QUẢN LÝ NGƯỜI DÙNG", new Color(150, 0, 255));
        JButton revenueButton = createActionButton("DOANH THU & BÁO CÁO", new Color(0, 255, 150));
        JButton logoutButton = createActionButton("ĐĂNG XUẤT", new Color(255, 100, 0));
        
        buttonGrid.add(machineButton);
        buttonGrid.add(userButton);
        buttonGrid.add(revenueButton);
        buttonGrid.add(logoutButton);
        
        // Add action listeners
        machineButton.addActionListener(e -> {
            new MachineCreationForm().setVisible(true);
            dispose();
        });
        
        userButton.addActionListener(e -> {
            new UserManagementForm().setVisible(true);
            dispose();
        });
        
        revenueButton.addActionListener(e -> {
            new RevenueStatisticsForm().setVisible(true);
            dispose();
        });
        
        logoutButton.addActionListener(e -> {
            new HomePage().setVisible(true);
            dispose();
        });
        
        // Add components to center panel with spacing
        centerPanel.add(welcomeLabel);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(systemStatusLabel);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(statsPanel);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(buttonGrid);
        centerPanel.add(Box.createVerticalStrut(10));
        
        // Footer panel
        JPanel footerPanel = new JPanel();
        footerPanel.setOpaque(false);
        footerPanel.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, ACCENT_COLOR));
        
        JLabel footerLabel = new JLabel("© 2023 GAME CENTER - Hệ thống quản lí quán net chất lượng");
        footerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        footerLabel.setForeground(TEXT_COLOR);
        footerLabel.setBorder(new EmptyBorder(10, 20, 10, 20));
        
        footerPanel.add(footerLabel);
        
        // Add all panels to content panel
        contentPanel.add(headerPanel, BorderLayout.NORTH);
        contentPanel.add(centerPanel, BorderLayout.CENTER);
        contentPanel.add(footerPanel, BorderLayout.SOUTH);
        
        // Add content panel to layered pane
        layeredPane.add(contentPanel, JLayeredPane.PALETTE_LAYER);
        
        // Add layered pane to main panel and set as content pane
        mainPanel.add(layeredPane, BorderLayout.CENTER);
        setContentPane(mainPanel);
        
        // Start stats update timer (update every 10 seconds)
        startStatsUpdateTimer();
    }
    
    private JPanel createStatBox(String title, JLabel valueLabel, Color color) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(new Color(25, 25, 40, 200));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 2),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        titleLabel.setForeground(TEXT_COLOR);
        
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valueLabel.setForeground(color);
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(valueLabel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JButton createActionButton(String text, Color color) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                
                // Enable antialiasing for smoother edges
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Button background
                if (getModel().isPressed()) {
                    g2.setColor(color.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(color.brighter());
                } else {
                    g2.setColor(color);
                }
                
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                
                // Button border glow effect
                g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 150));
                g2.setStroke(new BasicStroke(3));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                
                // Button text
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 18));
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString(getText(), x, y);
                
                g2.dispose();
            }
            
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(300, 100);
            }
        };
        
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        
        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            
            public void mouseExited(java.awt.event.MouseEvent evt) {
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
        
        return button;
    }
    
    private void startStatsUpdateTimer() {
        // Fetch stats immediately
        updateStats();
        
        // Set up timer to update stats every 10 seconds
        statsUpdateTimer = new Timer(10000, e -> updateStats());
        statsUpdateTimer.start();
    }
    
    @Override
    public void dispose() {
        // Stop the timer when the form is closed
        if (statsUpdateTimer != null) {
            statsUpdateTimer.stop();
        }
        super.dispose();
    }
    
    private void updateStats() {
        try (Connection conn = KetNoiCSDL.getConnection()) {
            // 1. Active machines (machines currently in use)
            String activeMachinesSql = "SELECT COUNT(*) AS active_machines FROM machine_usage " +
                                     "WHERE end_time IS NULL";
            
            // 2. Today's revenue
            String todayRevenueSql = "SELECT SUM(total_amount) AS today_revenue FROM machine_usage " +
                                   "WHERE DATE(start_time) = CURDATE()";
            
            // 3. Online users (users with active sessions)
            String onlineUsersSql = "SELECT COUNT(DISTINCT user_id) AS online_users FROM machine_usage " +
                                  "WHERE end_time IS NULL";
            
            // 4. Total machines
            String totalMachinesSql = "SELECT COUNT(*) AS total_machines FROM machines";
            
            // Execute queries and update labels
            try (Statement stmt = conn.createStatement()) {
                // Active machines
                try (ResultSet rs = stmt.executeQuery(activeMachinesSql)) {
                    if (rs.next()) {
                        activeMachinesLabel.setText(String.valueOf(rs.getInt("active_machines")));
                    }
                }
                
                // Today's revenue
                try (ResultSet rs = stmt.executeQuery(todayRevenueSql)) {
                    if (rs.next()) {
                        double revenue = rs.getDouble("today_revenue");
                        todayRevenueLabel.setText(String.format("%,.0f ₫", revenue));
                    }
                }
                
                // Online users
                try (ResultSet rs = stmt.executeQuery(onlineUsersSql)) {
                    if (rs.next()) {
                        onlineUsersLabel.setText(String.valueOf(rs.getInt("online_users")));
                    }
                }
                
                // Total machines
                try (ResultSet rs = stmt.executeQuery(totalMachinesSql)) {
                    if (rs.next()) {
                        totalMachinesLabel.setText(String.valueOf(rs.getInt("total_machines")));
                    }
                }
            }
            
        } catch (SQLException ex) {
            ex.printStackTrace();
            // Don't show error message for background update
            System.err.println("Error updating stats: " + ex.getMessage());
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

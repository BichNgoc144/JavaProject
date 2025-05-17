package BT_Project.QuanLyQuanNet;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.NumberFormat;
import java.util.Locale;
public class GiaoDienUser extends JFrame {
    private int userId;
    private JLabel balanceLabel;
    private Timer balanceUpdateTimer;

    // Gaming color scheme
    private final Color DARK_BG = new Color(15, 15, 25);
    private final Color NEON_BLUE = new Color(0, 200, 255);
    private final Color NEON_PURPLE = new Color(170, 0, 255);
    private final Color NEON_GREEN = new Color(0, 255, 100);
    private final Color NEON_RED = new Color(255, 50, 50);

    public GiaoDienUser() {
        this(1);
    }

    public GiaoDienUser(int userId) {
        this.userId = userId;
        System.out.println("Initializing user ID: " + userId);
        
        // Frame setup
        setTitle("GAMER DASHBOARD - NET SYSTEM");
        setSize(600, 500); // Larger size for better UI
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Main panel with gaming background
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(30, 40, 30, 40));
        mainPanel.setBackground(DARK_BG);
        
        // Header with gaming style
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerPanel.setOpaque(false);
        
        JLabel welcomeLabel = new JLabel("WELCOME", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Impact", Font.BOLD, 28));
        welcomeLabel.setForeground(NEON_BLUE);
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        welcomeLabel.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        JLabel subTitleLabel = new JLabel("Player Dashboard", SwingConstants.CENTER);
        subTitleLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        subTitleLabel.setForeground(new Color(150, 150, 150));
        subTitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        headerPanel.add(welcomeLabel);
        headerPanel.add(subTitleLabel);
        mainPanel.add(headerPanel);
        
        mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        
        // Balance display with gaming style
        JPanel balancePanel = new JPanel();
        balancePanel.setLayout(new BoxLayout(balancePanel, BoxLayout.Y_AXIS));
        balancePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        balancePanel.setOpaque(false);
        balancePanel.setBorder(new CompoundBorder(
            new LineBorder(new Color(50, 50, 80), 2),
            new EmptyBorder(15, 30, 15, 30)
        ));
        
        JLabel balanceTitle = new JLabel("SỐ DƯ", SwingConstants.CENTER);
        balanceTitle.setFont(new Font("Arial", Font.BOLD, 16));
        balanceTitle.setForeground(new Color(200, 200, 200));
        balanceTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        balanceLabel = new JLabel("", SwingConstants.CENTER);
        balanceLabel.setFont(new Font("Arial", Font.BOLD, 24));
        balanceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        updateBalanceDisplay();
        
        balancePanel.add(balanceTitle);
        balancePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        balancePanel.add(balanceLabel);
        mainPanel.add(balancePanel);
        
        mainPanel.add(Box.createRigidArea(new Dimension(0, 40)));
        
        // Action buttons with gaming style
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(2, 2, 20, 20));
        buttonPanel.setOpaque(false);
        buttonPanel.setMaximumSize(new Dimension(400, 200));
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        String[] buttonTexts = {"SỬ DỤNG MÁY", "NẠP TIỀN", "LỊCH SỬ", "THOÁT"};
        Color[] buttonColors = {NEON_BLUE, NEON_GREEN, NEON_PURPLE, NEON_RED};
        
        for (int i = 0; i < buttonTexts.length; i++) {
            JButton button = createGamingButton(buttonTexts[i], buttonColors[i]);
            buttonPanel.add(button);
        }
        
        // Assign actions to buttons
        ((JButton)buttonPanel.getComponent(0)).addActionListener(e -> {
            new UseMachineForm(this.userId).setVisible(true);
            dispose();
        });
        
        ((JButton)buttonPanel.getComponent(1)).addActionListener(e -> {
            new DepositForm(this.userId).setVisible(true);
            dispose();
        });
        
        ((JButton)buttonPanel.getComponent(2)).addActionListener(e -> {
            new UsageHistoryForm(userId).setVisible(true);
        });
        
        ((JButton)buttonPanel.getComponent(3)).addActionListener(e -> {
            new HomePage().setVisible(true);
            dispose();
        });
        
        mainPanel.add(buttonPanel);
        
        // Footer with system info
        JLabel footerLabel = new JLabel("SYSTEM ONLINE | Player ID: " + userId, SwingConstants.CENTER);
        footerLabel.setFont(new Font("Courier New", Font.PLAIN, 12));
        footerLabel.setForeground(new Color(100, 100, 100));
        footerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        footerLabel.setBorder(new EmptyBorder(20, 0, 0, 0));
        mainPanel.add(footerLabel);
        
        add(mainPanel);
        
        // Set up timer to periodically update balance
        balanceUpdateTimer = new Timer(5000, e -> updateBalanceDisplay());
        balanceUpdateTimer.start();
    }

    private JButton createGamingButton(String text, Color baseColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                
                // Button background with gradient
                if (getModel().isPressed()) {
                    g2.setColor(baseColor.darker());
                } else if (getModel().isRollover()) {
                    GradientPaint gp = new GradientPaint(0, 0, baseColor.brighter(), 
                                                       0, getHeight(), baseColor);
                    g2.setPaint(gp);
                } else {
                    g2.setColor(new Color(baseColor.getRed(), baseColor.getGreen(), 
                                     baseColor.getBlue(), 100));
                }
                
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                
                // Button border with glow effect
                g2.setColor(baseColor);
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
                
                // Button text
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Arial", Font.BOLD, 14));
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
        button.setPreferredSize(new Dimension(150, 50));
        
        // Add hover effects
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
        
        return button;
    }

    @Override
    public void dispose() {
        if (balanceUpdateTimer != null) {
            balanceUpdateTimer.stop();
        }
        super.dispose();
    }

    // Original database methods remain unchanged
    private double getTotalDeposit() {
        double totalDeposit = 0.0;
        try (Connection conn = KetNoiCSDL.getConnection()) {
            String sql = "SELECT SUM(amount) AS total FROM deposit WHERE user_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next() && rs.getObject("total") != null) {
                totalDeposit = rs.getDouble("total");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error querying balance: " + ex.getMessage(), 
                                        "SYSTEM ERROR", JOptionPane.ERROR_MESSAGE);
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
            JOptionPane.showMessageDialog(this, "Error querying spending: " + ex.getMessage(), 
                                        "SYSTEM ERROR", JOptionPane.ERROR_MESSAGE);
        }
        return totalSpent;
    }

    public void updateBalanceDisplay() {
        double totalDeposit = getTotalDeposit();
        double totalSpent = getTotalSpent();
        double currentBalance = totalDeposit - totalSpent;

        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        String formattedBalance = currencyFormatter.format(currentBalance);

        balanceLabel.setText(formattedBalance);

        // Update color based on balance
        if (currentBalance > 0) {
            balanceLabel.setForeground(NEON_GREEN);
        } else if (currentBalance == 0) {
            balanceLabel.setForeground(Color.WHITE);
        } else {
            balanceLabel.setForeground(NEON_RED);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new GiaoDienUser().setVisible(true);
        });
    }
}
package BT_Project.QuanLyQuanNet;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

public class HomePage extends JFrame {
    private Image backgroundImage;

    public HomePage() {
        setTitle("PHONG NET CHAT LUONG CAO - CONTROL PANEL");
        setSize(1024, 768); 
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Load gaming background image
        try {
        	backgroundImage = new ImageIcon(new URL("https://images.rawpixel.com/image_800/cHJpdmF0ZS9sci9pbWFnZXMvd2Vic2l0ZS8yMDI0LTAzL3Jhd3BpeGVsb2ZmaWNlMjFfYV9taW5pbWFsX2FuZF9sZXNzX2RldGFpbF9pbGx1c3RyYXRpb25fdXNpbmdfbF82MTgxMmRlNS1kYWQxLTQwZTctOTc0My03ODcxZDJmZTMzZmYuanBn.jpg")).getImage();
        } catch (Exception e) {
            e.printStackTrace();
            getContentPane().setBackground(Color.BLACK);
        }

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(1024, 768));

        // Add background panel
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                } else {
                    g.setColor(Color.BLACK);
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        backgroundPanel.setBounds(0, 0, 1024, 768);
        layeredPane.add(backgroundPanel, JLayeredPane.DEFAULT_LAYER);

        // Add content panel with semi-transparent background
        JPanel contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BorderLayout());
        contentPanel.setBounds(0, 0, 1024, 768);
        layeredPane.add(contentPanel, JLayeredPane.PALETTE_LAYER);

        JPanel buttonsPanel = createGamingButtonsPanel();
        contentPanel.add(buttonsPanel, BorderLayout.NORTH);

        // Add main title
        JLabel titleLabel = new JLabel("GAMING NET", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Impact", Font.BOLD, 48));
        titleLabel.setForeground(new Color(0, 255, 255)); // Cyan color
        titleLabel.setBorder(BorderFactory.createEmptyBorder(150, 0, 0, 0));
        contentPanel.add(titleLabel, BorderLayout.CENTER);

        // Add footer
        JLabel footerLabel = new JLabel("SYSTEM ONLINE | v2.0 | © 2023 GAMING NET", SwingConstants.CENTER);
        footerLabel.setFont(new Font("Courier New", Font.PLAIN, 14));
        footerLabel.setForeground(new Color(150, 150, 150));
        footerLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        contentPanel.add(footerLabel, BorderLayout.SOUTH);

        setContentPane(layeredPane);
    }

    private JPanel createGamingButtonsPanel() {
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setOpaque(false);
        buttonsPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 20, 20));
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 20));

        JButton loginButton = createGamingButton("LOGIN");
        loginButton.addActionListener(e -> openLoginForm());
        buttonsPanel.add(loginButton);

        JButton registerButton = createGamingButton("REGISTER");
        registerButton.addActionListener(e -> openRegistrationForm());
        buttonsPanel.add(registerButton);

        return buttonsPanel;
    }

    private JButton createGamingButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                
                // Button background
                if (getModel().isPressed()) {
                    g2.setColor(new Color(0, 100, 255));
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(0, 150, 255));
                } else {
                    g2.setColor(new Color(0, 50, 150, 200));
                }
                
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                
                // Button border
                g2.setColor(new Color(0, 255, 255));
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
            
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(120, 40);
            }
        };
        
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        
        // Add hover effect
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

    private void openLoginForm() {
        new LoginFrom().setVisible(true);
        dispose();
    }

    private void openRegistrationForm() {
        new RegistrationForm().setVisible(true);
        dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            HomePage homePage = new HomePage();
            homePage.setVisible(true);
        });
    }
}

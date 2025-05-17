package BT_Project.QuanLyQuanNet;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import javax.swing.border.*;

public class MachineCreationForm extends JFrame {
    private static final Color DARK_BG = new Color(15, 15, 25);
    private static final Color ACCENT_COLOR = new Color(0, 200, 255);
    private static final Color TEXT_COLOR = new Color(220, 220, 220);
    private static final Color FIELD_BG = new Color(30, 30, 45);
    
    private JTextField machineCodeField;
    private JTextField machineNameField;
    private JTextField hourlyRateField;
    private JTextField descriptionField;
    private JButton createMachineButton;
    private JButton btnBack;

    public MachineCreationForm() {
        setTitle("TẠO MÁY MỚI - GAME CENTER ADMIN");
        setSize(600, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(DARK_BG);
        setLayout(new BorderLayout());
        
        // Panel tiêu đề
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(20, 20, 35));
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, ACCENT_COLOR));
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        
        JLabel titleLabel = new JLabel("THÊM MÁY MỚI");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(ACCENT_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBorder(new EmptyBorder(15, 0, 10, 0));
        
        JLabel subTitleLabel = new JLabel("Quản lý hệ thống máy trong quán game");
        subTitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subTitleLabel.setForeground(TEXT_COLOR);
        subTitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subTitleLabel.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        headerPanel.add(titleLabel);
        headerPanel.add(subTitleLabel);
        add(headerPanel, BorderLayout.NORTH);

        // Panel chính chứa form
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(DARK_BG);
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBorder(new EmptyBorder(20, 50, 20, 50));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Nhập mã máy
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(createLabel("MÃ MÁY:"), gbc);
        
        gbc.gridx = 1;
        machineCodeField = createTextField();
        mainPanel.add(machineCodeField, gbc);

        // Nhập tên máy
        gbc.gridx = 0;
        gbc.gridy = 1;
        mainPanel.add(createLabel("TÊN MÁY:"), gbc);
        
        gbc.gridx = 1;
        machineNameField = createTextField();
        mainPanel.add(machineNameField, gbc);

        // Nhập số tiền theo giờ
        gbc.gridx = 0;
        gbc.gridy = 2;
        mainPanel.add(createLabel("GIÁ THEO GIỜ:"), gbc);
        
        gbc.gridx = 1;
        hourlyRateField = createTextField();
        mainPanel.add(hourlyRateField, gbc);

        // Nhập mô tả
        gbc.gridx = 0;
        gbc.gridy = 3;
        mainPanel.add(createLabel("MÔ TẢ:"), gbc);
        
        gbc.gridx = 1;
        descriptionField = createTextField();
        mainPanel.add(descriptionField, gbc);

        // Panel nút bấm
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.CENTER;
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(DARK_BG);
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));
        
        createMachineButton = createGamingButton("TẠO MÁY", ACCENT_COLOR);
        createMachineButton.addActionListener(e -> createMachine());
        buttonPanel.add(createMachineButton);
        
        btnBack = createGamingButton("QUAY LẠI", new Color(150, 0, 255));
        btnBack.addActionListener(e -> {
            new GiaoDienAdmin().setVisible(true);
            dispose();
        });
        buttonPanel.add(btnBack);
        
        mainPanel.add(buttonPanel, gbc);
        add(mainPanel, BorderLayout.CENTER);
        
        // Footer
        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(new Color(10, 10, 20));
        footerPanel.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, ACCENT_COLOR));
        JLabel footerLabel = new JLabel("© 2023 GAME CENTER - Quản lý máy");
        footerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        footerLabel.setForeground(TEXT_COLOR);
        footerLabel.setBorder(new EmptyBorder(10, 20, 10, 20));
        footerPanel.add(footerLabel);
        add(footerPanel, BorderLayout.SOUTH);
    }
    
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(TEXT_COLOR);
        return label;
    }
    
    private JTextField createTextField() {
        JTextField textField = new JTextField(20);
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textField.setForeground(TEXT_COLOR);
        textField.setBackground(FIELD_BG);
        textField.setBorder(BorderFactory.createCompoundBorder(
        	    BorderFactory.createLineBorder(new Color(50, 50, 70)), 
        	    BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        textField.setCaretColor(ACCENT_COLOR);
        return textField;
    }
    
    private JButton createGamingButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color.darker(), 2),
            BorderFactory.createEmptyBorder(10, 25, 10, 25)
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

    private boolean validateInputs() {
        if (machineCodeField.getText().isEmpty() || machineNameField.getText().isEmpty() || hourlyRateField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        try {
            Double.parseDouble(hourlyRateField.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Số tiền theo giờ phải là một số hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    private void createMachine() {
        if (!validateInputs()) return;

        String machineCode = machineCodeField.getText();
        String machineName = machineNameField.getText();
        double hourlyRate = Double.parseDouble(hourlyRateField.getText());
        String description = descriptionField.getText();

        try (Connection conn = KetNoiCSDL.getConnection()) {
            String sql = "INSERT INTO machines (machine_code, machine_name, hourly_rate, description) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, machineCode);
            stmt.setString(2, machineName);
            stmt.setDouble(3, hourlyRate);
            stmt.setString(4, description);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Máy đã được tạo thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                clearFields();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi khi tạo máy!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi kết nối cơ sở dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearFields() {
        machineCodeField.setText("");
        machineNameField.setText("");
        hourlyRateField.setText("");
        descriptionField.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            MachineCreationForm form = new MachineCreationForm();
            form.setVisible(true);
        });
    }
}

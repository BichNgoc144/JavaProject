package BT_Project.QuanLyQuanNet;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class MachineCreationForm extends JFrame {
    private JTextField machineCodeField;
    private JTextField machineNameField;
    private JTextField hourlyRateField;
    private JTextField descriptionField;
    private JButton createMachineButton;
    private JButton btnLogout;

    public MachineCreationForm() {
        setTitle("Tạo Máy - Quản lý Quán Net");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(5, 2, 10, 10)); // Dùng GridLayout để dễ dàng căn chỉnh các ô nhập liệu

        // Nhập mã máy
        add(new JLabel("Mã máy:"));
        machineCodeField = new JTextField();
        add(machineCodeField);

        // Nhập tên máy
        add(new JLabel("Tên máy:"));
        machineNameField = new JTextField();
        add(machineNameField);

        // Nhập số tiền theo giờ
        add(new JLabel("Số tiền theo giờ:"));
        hourlyRateField = new JTextField();
        add(hourlyRateField);

        // Nhập mô tả
        add(new JLabel("Mô tả:"));
        descriptionField = new JTextField();
        add(descriptionField);

        // Tạo nút để tạo máy
        createMachineButton = new JButton("Tạo máy");
        add(createMachineButton);
        
        // Nút Home (thoát về giao diện user)
        btnLogout = new JButton("Home");
        add(btnLogout);

        // Xử lý sự kiện khi bấm nút Tạo máy
        createMachineButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createMachine();
            }
        });
     // Nút Home
        btnLogout.addActionListener(e -> {
            new GiaoDienAdmin().setVisible(true);
            dispose();  // Đóng form hiện tại
        });
    }

    // Kiểm tra tính hợp lệ của các thông tin
    private boolean validateInputs() {
        if (machineCodeField.getText().isEmpty() || machineNameField.getText().isEmpty() || hourlyRateField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin!");
            return false;
        }

        // Kiểm tra số tiền theo giờ là số hợp lệ
        try {
            Double.parseDouble(hourlyRateField.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Số tiền theo giờ phải là một số hợp lệ!");
            return false;
        }

        return true;
    }

    // Xử lý việc tạo máy
    private void createMachine() {
        if (!validateInputs()) {
            return;
        }

        String machineCode = machineCodeField.getText();
        String machineName = machineNameField.getText();
        double hourlyRate = Double.parseDouble(hourlyRateField.getText());
        String description = descriptionField.getText();

        try (Connection conn = KetNoiCSDL.getConnection()) {
            // SQL để thêm máy mới vào cơ sở dữ liệu
            String sql = "INSERT INTO machines (machine_code, machine_name, hourly_rate, description) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, machineCode);
            stmt.setString(2, machineName);
            stmt.setDouble(3, hourlyRate);
            stmt.setString(4, description);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Máy đã được tạo thành công!");
                clearFields();  // Làm mới các trường nhập liệu
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi khi tạo máy!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi kết nối cơ sở dữ liệu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Làm mới các trường nhập liệu
    private void clearFields() {
        machineCodeField.setText("");
        machineNameField.setText("");
        hourlyRateField.setText("");
        descriptionField.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MachineCreationForm().setVisible(true);
        });
    }
}

package BT_Project.QuanLyQuanNet;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class MachineCreationForm extends JFrame {
    private static final Color DARK_BG = new Color(15, 15, 25);
    private static final Color ACCENT_COLOR = new Color(0, 200, 255);
    private static final Color TEXT_COLOR = new Color(220, 220, 220);
    private static final Color FIELD_BG = new Color(30, 30, 45);
    private static final Color TABLE_HEADER_BG = new Color(30, 30, 45);
    private static final Color TABLE_ROW_BG = new Color(25, 25, 40);
    private static final Color TABLE_GRID_COLOR = new Color(60, 60, 100);
    private static final Color ROW_EVEN = new Color(30, 30, 50);
    private static final Color ROW_ODD = new Color(40, 40, 60);
    private static final Color ROW_SELECTED = new Color(70, 50, 100);
    private static final Color UPDATE_COLOR = new Color(0, 255, 150);
    private static final Color DELETE_COLOR = new Color(255, 80, 80);
    
    private JTextField machineCodeField;
    private JTextField machineNameField;
    private JTextField hourlyRateField;
    private JTextField descriptionField;
    private JButton createMachineButton;
    private JButton updateMachineButton;
    private JButton btnBack;
    private JTable machineTable;
    private DefaultTableModel tableModel;
    private JButton deleteMachineButton;
    
    // Current machine ID for updates
    private int currentMachineId = -1;

    public MachineCreationForm() {
        setTitle("QUẢN LÝ MÁY - GAME CENTER ADMIN");
        setSize(900, 600); // Tăng chiều rộng để đảm bảo đủ không gian cho cả hai phần
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(DARK_BG);
        setLayout(new BorderLayout());
        
        // Panel tiêu đề
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(20, 20, 35));
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, ACCENT_COLOR));
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        
        JLabel titleLabel = new JLabel("QUẢN LÝ MÁY");
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
        
        // Panel chính chia làm 2 phần: bảng danh sách và form nhập
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(550); // Tăng kích thước cho phần bảng danh sách
        splitPane.setDividerSize(3);
        splitPane.setBorder(null);
        splitPane.setBackground(DARK_BG);
        splitPane.setUI(new BasicSplitPaneUI() {
            @Override
            public BasicSplitPaneDivider createDefaultDivider() {
                return new BasicSplitPaneDivider(this) {
                    @Override
                    public void paint(Graphics g) {
                        g.setColor(new Color(40, 40, 60));
                        g.fillRect(0, 0, getSize().width, getSize().height);
                        
                        // Vẽ đường kẻ ở giữa
                        g.setColor(ACCENT_COLOR);
                        g.fillRect(1, 0, 1, getSize().height);
                    }
                };
            }
        });
        
        // Panel bảng danh sách máy
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(DARK_BG);
        tablePanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Tạo bảng danh sách máy
        String[] columnNames = {"ID", "Mã máy", "Tên máy", "Giá theo giờ", "Mô tả"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Không cho phép sửa trực tiếp bảng
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) {
                    return Integer.class;
                }
                return String.class;
            }
        };
        
        machineTable = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                
                // Màu nền cho hàng chẵn và lẻ khác nhau để phân biệt
                c.setBackground(row % 2 == 0 ? ROW_EVEN : ROW_ODD);
                c.setForeground(TEXT_COLOR);
                
                // Thêm đường viền cho mỗi ô để rõ ràng hơn
                JComponent jc = (JComponent) c;
                jc.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, TABLE_GRID_COLOR));
                
                if (column == 3) { // Cột giá tiền
                    ((JLabel)c).setHorizontalAlignment(SwingConstants.RIGHT);
                    c.setForeground(UPDATE_COLOR);
                }
                
                // Highlight hàng được chọn
                if (isRowSelected(row)) {
                    jc.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, ACCENT_COLOR));
                    c.setBackground(ROW_SELECTED);
                }
                
                return c;
            }
        };
        
        // Thiết lập style cho bảng
        machineTable.setSelectionBackground(ACCENT_COLOR.darker());
        machineTable.setSelectionForeground(Color.WHITE);
        machineTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        machineTable.setRowHeight(40);
        machineTable.setShowGrid(true);
        machineTable.setGridColor(TABLE_GRID_COLOR);
        machineTable.setIntercellSpacing(new Dimension(1, 1));
        machineTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        
        // Thiết lập style cho header bảng
        JTableHeader header = machineTable.getTableHeader();
        header.setBackground(TABLE_HEADER_BG);
        header.setForeground(ACCENT_COLOR);
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBorder(BorderFactory.createMatteBorder(2, 0, 2, 0, ACCENT_COLOR));
        header.setReorderingAllowed(false); // Không cho phép kéo thả cột
        
        // Đặt renderer cho header để tạo style nhất quán
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setBackground(TABLE_HEADER_BG);
                label.setForeground(ACCENT_COLOR);
                label.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 1, TABLE_GRID_COLOR));
                return label;
            }
        });
        
        // Ẩn cột ID
        machineTable.getColumnModel().getColumn(0).setMinWidth(0);
        machineTable.getColumnModel().getColumn(0).setMaxWidth(0);
        machineTable.getColumnModel().getColumn(0).setWidth(0);
        
        // Điều chỉnh kích thước cột - tối ưu cho phần bảng lớn hơn
        machineTable.getColumnModel().getColumn(1).setPreferredWidth(90);  // Mã máy
        machineTable.getColumnModel().getColumn(2).setPreferredWidth(140); // Tên máy
        machineTable.getColumnModel().getColumn(3).setPreferredWidth(120); // Giá
        machineTable.getColumnModel().getColumn(4).setPreferredWidth(200); // Mô tả
        
        // Thêm sự kiện chọn hàng
        machineTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = machineTable.getSelectedRow();
                if (selectedRow >= 0) {
                    populateFieldsFromTable(selectedRow);
                }
            }
        });
        
        // Thiết lập chế độ chọn
        machineTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        machineTable.setRowSelectionAllowed(true);
        machineTable.setColumnSelectionAllowed(false);
        machineTable.setCellSelectionEnabled(false);
        
        JScrollPane tableScrollPane = new JScrollPane(machineTable);
        tableScrollPane.setBorder(BorderFactory.createLineBorder(new Color(50, 50, 70), 2));
        tableScrollPane.getViewport().setBackground(DARK_BG);
        tablePanel.add(tableScrollPane, BorderLayout.CENTER);
        
        // Panel tiêu đề bảng
        JPanel tableHeaderPanel = new JPanel();
        tableHeaderPanel.setBackground(new Color(20, 20, 35));
        tableHeaderPanel.setLayout(new BorderLayout());
        tableHeaderPanel.setBorder(new EmptyBorder(10, 0, 10, 0));
        
        JLabel tableTitle = new JLabel("DANH SÁCH MÁY", SwingConstants.CENTER);
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        tableTitle.setForeground(ACCENT_COLOR);
        tableHeaderPanel.add(tableTitle, BorderLayout.CENTER);
        
        JButton refreshButton = createGamingButton("LÀM MỚI", ACCENT_COLOR);
        refreshButton.addActionListener(e -> loadMachines());
        tableHeaderPanel.add(refreshButton, BorderLayout.EAST);
        
        tablePanel.add(tableHeaderPanel, BorderLayout.NORTH);
        
        // Thêm panel bảng vào split pane
        splitPane.setLeftComponent(tablePanel);
        
        // Panel form nhập thông tin
        JPanel formPanel = new JPanel(new BorderLayout());
        formPanel.setBackground(DARK_BG);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 2, 0, 0, new Color(40, 40, 60)),
            BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));
        
        // Tiêu đề form với panel background
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BorderLayout());
        titlePanel.setBackground(new Color(20, 20, 35));
        titlePanel.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, ACCENT_COLOR));
        
        JLabel formTitle = new JLabel("THÔNG TIN MÁY", SwingConstants.CENTER);
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        formTitle.setForeground(ACCENT_COLOR);
        formTitle.setBorder(new EmptyBorder(10, 0, 10, 0));
        titlePanel.add(formTitle, BorderLayout.CENTER);
        
        // Thêm ghi chú về mã máy và tên máy
        JLabel noteLabel = new JLabel("Lưu ý: Mã máy và tên máy phải là duy nhất", SwingConstants.CENTER);
        noteLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        noteLabel.setForeground(new Color(255, 200, 100));
        titlePanel.add(noteLabel, BorderLayout.SOUTH);
        
        formPanel.add(titlePanel, BorderLayout.NORTH);
        
        // Panel chính chứa form - sử dụng BoxLayout cho hiển thị đẹp hơn
        JPanel inputPanel = new JPanel();
        inputPanel.setBackground(DARK_BG);
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        
        // Tạo panel nền cho các field với hiệu ứng nổi
        JPanel formFieldsPanel = new JPanel();
        formFieldsPanel.setLayout(new GridBagLayout());
        formFieldsPanel.setBackground(new Color(25, 25, 40));
        formFieldsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(40, 40, 60), 1),
            BorderFactory.createEmptyBorder(12, 12, 12, 12) // Giảm padding để tiết kiệm không gian
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 5, 6, 5); // Giảm khoảng cách giữa các thành phần
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        
        // Nhập mã máy
        gbc.gridx = 0;
        gbc.gridy = 0;
        formFieldsPanel.add(createLabel("MÃ MÁY:"), gbc);
        
        gbc.gridx = 1;
        machineCodeField = createTextField();
        formFieldsPanel.add(machineCodeField, gbc);

        // Nhập tên máy
        gbc.gridx = 0;
        gbc.gridy = 1;
        formFieldsPanel.add(createLabel("TÊN MÁY:"), gbc);
        
        gbc.gridx = 1;
        machineNameField = createTextField();
        formFieldsPanel.add(machineNameField, gbc);

        // Nhập số tiền theo giờ
        gbc.gridx = 0;
        gbc.gridy = 2;
        formFieldsPanel.add(createLabel("GIÁ THEO GIỜ:"), gbc);
        
        gbc.gridx = 1;
        hourlyRateField = createTextField();
        formFieldsPanel.add(hourlyRateField, gbc);

        // Nhập mô tả
        gbc.gridx = 0;
        gbc.gridy = 3;
        formFieldsPanel.add(createLabel("MÔ TẢ:"), gbc);
        
        gbc.gridx = 1;
        descriptionField = createTextField();
        formFieldsPanel.add(descriptionField, gbc);
        
        // Thêm form fields panel vào input panel
        inputPanel.add(formFieldsPanel);
        
        formPanel.add(inputPanel, BorderLayout.CENTER);
        
        // Panel nút bấm - Tối ưu hóa bố cục
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(25, 25, 40));
        buttonPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(2, 0, 0, 0, new Color(40, 40, 60)),
            BorderFactory.createEmptyBorder(10, 5, 10, 5) // Giảm padding
        ));
        buttonPanel.setLayout(new GridLayout(2, 2, 5, 5)); // Thay đổi thành 2x2 grid để tiết kiệm không gian
        
        createMachineButton = createGamingButton("THÊM MỚI", ACCENT_COLOR);
        createMachineButton.addActionListener(e -> createMachine());
        createMachineButton.setPreferredSize(new Dimension(80, 35)); // Giảm kích thước nút
        buttonPanel.add(createMachineButton);
        
        updateMachineButton = createGamingButton("CẬP NHẬT", UPDATE_COLOR);
        updateMachineButton.addActionListener(e -> updateMachine());
        updateMachineButton.setEnabled(false);
        buttonPanel.add(updateMachineButton);
        
        deleteMachineButton = createGamingButton("XÓA", DELETE_COLOR);
        deleteMachineButton.addActionListener(e -> deleteMachine());
        deleteMachineButton.setEnabled(false);
        buttonPanel.add(deleteMachineButton);
        
        JButton clearButton = createGamingButton("LÀM MỚI", new Color(100, 100, 150));
        clearButton.addActionListener(e -> clearFields());
        buttonPanel.add(clearButton);
        
        formPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Thêm form panel vào split pane
        splitPane.setRightComponent(formPanel);
        
        // Thêm split pane vào main panel
        add(splitPane, BorderLayout.CENTER);
        
        // Panel nút quay lại
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(new Color(10, 10, 20));
        footerPanel.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, ACCENT_COLOR));
        
        btnBack = createGamingButton("QUAY LẠI", new Color(150, 0, 255));
        btnBack.addActionListener(e -> {
            new GiaoDienAdmin().setVisible(true);
            dispose();
        });
        
        JPanel backButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        backButtonPanel.setBackground(new Color(10, 10, 20));
        backButtonPanel.add(btnBack);
        
        JLabel footerLabel = new JLabel("© 2023 GAME CENTER - Quản lý máy", SwingConstants.CENTER);
        footerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        footerLabel.setForeground(TEXT_COLOR);
        footerLabel.setBorder(new EmptyBorder(10, 20, 10, 20));
        
        footerPanel.add(backButtonPanel, BorderLayout.EAST);
        footerPanel.add(footerLabel, BorderLayout.CENTER);
        add(footerPanel, BorderLayout.SOUTH);
        
        // Tải danh sách máy từ database
        loadMachines();
    }
    
    // Tải danh sách máy từ database
    private void loadMachines() {
        try (Connection conn = KetNoiCSDL.getConnection()) {
            String sql = "SELECT id, machine_code, machine_name, hourly_rate, description FROM machines";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            tableModel.setRowCount(0);  // Xóa dữ liệu cũ

            while (rs.next()) {
                int id = rs.getInt("id");
                String machineCode = rs.getString("machine_code");
                String machineName = rs.getString("machine_name");
                double hourlyRate = rs.getDouble("hourly_rate");
                String description = rs.getString("description");
                
                String hourlyRateFormatted = String.format("%,.0f VNĐ", hourlyRate);

                tableModel.addRow(new Object[]{id, machineCode, machineName, hourlyRateFormatted, description});
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi tải danh sách máy: " + e.getMessage(), 
                                         "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(ACCENT_COLOR);
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        label.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 5));
        return label;
    }
    
    private JTextField createTextField() {
        JTextField textField = new JTextField(15);
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textField.setForeground(TEXT_COLOR);
        textField.setBackground(FIELD_BG);
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, ACCENT_COLOR), 
                BorderFactory.createEmptyBorder(6, 10, 6, 10) // Giảm padding để làm gọn
        ));
        textField.setCaretColor(ACCENT_COLOR);
        textField.setPreferredSize(new Dimension(160, 32)); // Giảm kích thước để làm gọn hơn
        
        // Thêm hiệu ứng focus
        textField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                textField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 2, 0, UPDATE_COLOR),
                        BorderFactory.createEmptyBorder(6, 10, 6, 10)
                ));
            }
            
            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                textField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 2, 0, ACCENT_COLOR),
                        BorderFactory.createEmptyBorder(6, 10, 6, 10)
                ));
            }
        });
        
        return textField;
    }
    
    private JButton createGamingButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12)); // Giảm kích thước font
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color.darker(), 1), // Viền mỏng hơn
            BorderFactory.createEmptyBorder(5, 8, 5, 8) // Padding nhỏ hơn
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
        
        // Kiểm tra xem tên máy đã tồn tại chưa
        if (isNameExists(machineName, -1)) {
            JOptionPane.showMessageDialog(
                this,
                "Tên máy '" + machineName + "' đã tồn tại trong hệ thống.\n" +
                "Vui lòng sử dụng một tên máy khác.",
                "Lỗi - Tên Máy Trùng Lặp",
                JOptionPane.ERROR_MESSAGE
            );
            
            // Focus vào trường tên máy để người dùng có thể sửa ngay
            machineNameField.requestFocus();
            machineNameField.selectAll();
            return;
        }

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
                loadMachines();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi khi tạo máy!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Kiểm tra xem có phải lỗi trùng lặp mã máy không
            if (e.getMessage().contains("Duplicate entry") && e.getMessage().contains("machine_code")) {
                JOptionPane.showMessageDialog(this, 
                    "Mã máy '" + machineCode + "' đã tồn tại trong hệ thống.\n" +
                    "Hãy liên hệ với quản trị viên để cập nhật cơ sở dữ liệu nếu bạn muốn cho phép mã máy trùng lặp.", 
                    "Lỗi Trùng Lặp Mã Máy", JOptionPane.WARNING_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi kết nối cơ sở dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void updateMachine() {
        if (!validateInputs() || currentMachineId == -1) return;

        String machineCode = machineCodeField.getText();
        String machineName = machineNameField.getText();
        double hourlyRate = Double.parseDouble(hourlyRateField.getText());
        String description = descriptionField.getText();
        
        // Kiểm tra xem tên máy đã tồn tại chưa (ngoại trừ chính nó)
        if (isNameExists(machineName, currentMachineId)) {
            JOptionPane.showMessageDialog(
                this,
                "Tên máy '" + machineName + "' đã tồn tại trong hệ thống.\n" +
                "Vui lòng sử dụng một tên máy khác.",
                "Lỗi - Tên Máy Trùng Lặp",
                JOptionPane.ERROR_MESSAGE
            );
            
            // Focus vào trường tên máy để người dùng có thể sửa ngay
            machineNameField.requestFocus();
            machineNameField.selectAll();
            return;
        }

        try (Connection conn = KetNoiCSDL.getConnection()) {
            String sql = "UPDATE machines SET machine_code=?, machine_name=?, hourly_rate=?, description=? WHERE id=?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, machineCode);
            stmt.setString(2, machineName);
            stmt.setDouble(3, hourlyRate);
            stmt.setString(4, description);
            stmt.setInt(5, currentMachineId);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Máy đã được cập nhật thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                clearFields();
                loadMachines();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật máy!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Kiểm tra xem có phải lỗi trùng lặp mã máy không
            if (e.getMessage().contains("Duplicate entry") && e.getMessage().contains("machine_code")) {
                JOptionPane.showMessageDialog(this, 
                    "Mã máy '" + machineCode + "' đã tồn tại trong hệ thống.\n" +
                    "Hãy liên hệ với quản trị viên để cập nhật cơ sở dữ liệu nếu bạn muốn cho phép mã máy trùng lặp.", 
                    "Lỗi Trùng Lặp Mã Máy", JOptionPane.WARNING_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi kết nối cơ sở dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void deleteMachine() {
        if (currentMachineId == -1) return;
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc chắn muốn xóa máy này không?", 
            "Xác nhận xóa", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try (Connection conn = KetNoiCSDL.getConnection()) {
            // Kiểm tra xem có máy nào đang được sử dụng không
            String checkSql = "SELECT COUNT(*) FROM machine_usage WHERE machine_id=? AND end_time IS NULL";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setInt(1, currentMachineId);
            ResultSet rs = checkStmt.executeQuery();
            
            if (rs.next() && rs.getInt(1) > 0) {
                JOptionPane.showMessageDialog(this, "Không thể xóa máy đang được sử dụng!", 
                                             "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Tiến hành xóa máy
            String sql = "DELETE FROM machines WHERE id=?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, currentMachineId);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Máy đã được xóa thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                clearFields();
                loadMachines();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi khi xóa máy!", "Lỗi", JOptionPane.ERROR_MESSAGE);
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
        currentMachineId = -1;
        createMachineButton.setEnabled(true);
        updateMachineButton.setEnabled(false);
        deleteMachineButton.setEnabled(false);
    }
    
    private void populateFieldsFromTable(int row) {
        // Hiệu ứng highlight khi chọn hàng
        machineTable.setRowSelectionInterval(row, row);
        
        currentMachineId = (int) tableModel.getValueAt(row, 0);
        machineCodeField.setText((String) tableModel.getValueAt(row, 1));
        machineNameField.setText((String) tableModel.getValueAt(row, 2));
        
        // Xử lý giá theo giờ (loại bỏ "VNĐ" và dấu phẩy)
        String hourlyRateStr = (String) tableModel.getValueAt(row, 3);
        hourlyRateStr = hourlyRateStr.replace("VNĐ", "").replace(",", "").trim();
        hourlyRateField.setText(hourlyRateStr);
        
        descriptionField.setText((String) tableModel.getValueAt(row, 4));
        
        // Kích hoạt nút cập nhật và xóa
        updateMachineButton.setEnabled(true);
        deleteMachineButton.setEnabled(true);
        createMachineButton.setEnabled(false);
        
        // Thêm hiệu ứng nhấp nháy nhẹ để thông báo đã chọn máy
        new Thread(() -> {
            try {
                Color originalBackground = machineCodeField.getBackground();
                Color highlightColor = new Color(60, 60, 100);
                
                for (int i = 0; i < 3; i++) {
                    machineCodeField.setBackground(highlightColor);
                    machineNameField.setBackground(highlightColor);
                    hourlyRateField.setBackground(highlightColor);
                    descriptionField.setBackground(highlightColor);
                    Thread.sleep(100);
                    
                    machineCodeField.setBackground(originalBackground);
                    machineNameField.setBackground(originalBackground);
                    hourlyRateField.setBackground(originalBackground);
                    descriptionField.setBackground(originalBackground);
                    Thread.sleep(100);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
    
    // Đã xóa các lớp ButtonRenderer và ButtonEditor vì không cần thiết nữa

    /**
     * Kiểm tra xem tên máy đã tồn tại trong cơ sở dữ liệu hay chưa
     * 
     * @param machineName Tên máy cần kiểm tra
     * @param currentMachineId ID của máy hiện tại (khi cập nhật, để loại trừ chính nó)
     * @return true nếu tên máy đã tồn tại, false nếu chưa
     */
    private boolean isNameExists(String machineName, int currentMachineId) {
        try (Connection conn = KetNoiCSDL.getConnection()) {
            String sql = "SELECT COUNT(*) FROM machines WHERE machine_name = ? AND id != ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, machineName);
            stmt.setInt(2, currentMachineId); // Nếu là thêm mới, currentMachineId = -1 nên sẽ kiểm tra tất cả

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
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

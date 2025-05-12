package BT_Project.QuanLyQuanNet;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

public class HomePage extends JFrame {
    private JLabel carouselLabel;
    private int currentImageIndex = 0;
    private final String[] imageUrls = {
        "https://lienquan.garena.vn/wp-content/uploads/2024/02/1920.350-KV.jpg",
        "https://lienquan.garena.vn/wp-content/uploads/2024/02/Cover-homepage_1920x350-4.png",
        "https://lienquan.garena.vn/wp-content/uploads/2024/02/SeaTalk_IMG_20250219_111035.png"
    };

    public HomePage() {
        setTitle("Trang Chủ - Quản lý Quán Net");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Đặt nền trắng cho toàn bộ trang
        getContentPane().setBackground(Color.WHITE);
        setLayout(new BorderLayout());

        // Tạo panel cho các nút đăng nhập và đăng ký
        JPanel buttonsPanel = createButtonsPanel();
        add(buttonsPanel, BorderLayout.NORTH);  // Thêm vào phía trên cùng

        // Tạo panel quảng cáo
        JPanel adPanel = createAdCarousel();
        add(adPanel, BorderLayout.CENTER);  // Thêm vào giữa màn hình

        // Tạo Timer để thay đổi hình ảnh mỗi 10 giây
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                changeImage(); // Gọi phương thức changeImage để thay đổi hình ảnh
            }
        }, 0, 10000); // 10,000 ms = 10 giây
    }

    // Tạo panel quảng cáo với hình ảnh lớn
    private JPanel createAdCarousel() {
        JPanel carouselPanel = new JPanel();
        carouselPanel.setLayout(new GridLayout(1, 1));

        // Tạo label cho quảng cáo
        carouselLabel = new JLabel();
        try {
            // Tải hình ảnh đầu tiên từ URL
            ImageIcon initialImage = new ImageIcon(new URL(imageUrls[currentImageIndex]));
            carouselLabel.setIcon(initialImage);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Cập nhật kích thước hình ảnh cho vừa với giao diện
        carouselLabel.setPreferredSize(new Dimension(750, 350));  // Đặt kích thước phù hợp cho hình ảnh
        carouselPanel.add(carouselLabel);

        return carouselPanel;
    }

    // Chuyển hình ảnh quảng cáo
    private void changeImage() {
        currentImageIndex = (currentImageIndex + 1) % imageUrls.length;
        try {
            // Tải hình ảnh mới từ URL
            ImageIcon newImage = new ImageIcon(new URL(imageUrls[currentImageIndex]));
            carouselLabel.setIcon(newImage);  // Cập nhật hình ảnh
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Tạo panel cho các nút Đăng nhập và Đăng ký
    private JPanel createButtonsPanel() {
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new FlowLayout(FlowLayout.RIGHT)); // Đặt các nút ở phía bên phải
        buttonsPanel.setPreferredSize(new Dimension(800, 60));  // Giảm kích thước panel chứa các nút

        // Tạo nút Đăng nhập
        JButton loginButton = new JButton("Đăng nhập");
        loginButton.setPreferredSize(new Dimension(100, 30));  // Giảm kích thước nút Đăng nhập
        loginButton.addActionListener(e -> openLoginForm());  // Mở form đăng nhập
        buttonsPanel.add(loginButton);

        // Tạo nút Đăng ký
        JButton registerButton = new JButton("Đăng ký");
        registerButton.setPreferredSize(new Dimension(100, 30));  // Giảm kích thước nút Đăng ký
        registerButton.addActionListener(e -> openRegistrationForm());  // Mở form đăng ký
        buttonsPanel.add(registerButton);

        return buttonsPanel;
    }

    private void openLoginForm() {
        // Mở form đăng nhập
        new LoginFrom().setVisible(true);
        dispose();  // Đóng form trang chủ khi chuyển đến trang đăng nhập
    }

    private void openRegistrationForm() {
        // Mở form đăng ký
        new RegistrationForm().setVisible(true);
        dispose();  // Đóng form trang chủ khi chuyển đến trang đăng ký
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new HomePage().setVisible(true);
        });
    }
}

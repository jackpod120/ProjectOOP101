package ClassroomProject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ReservationUI extends JFrame {
    private final Teacher teacher;
    private final ReservationSystem reservationSystem;

    public ReservationUI(Teacher teacher, ReservationSystem reservationSystem) {
        super("Classroom Reservation System");
        this.teacher = teacher;
        this.reservationSystem = reservationSystem;

        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Header แสดงชื่อผู้ใช้
        JLabel header = new JLabel("👋 Welcome, " + teacher.getName());
        header.setFont(new Font("SansSerif", Font.BOLD, 18));
        header.setHorizontalAlignment(SwingConstants.CENTER);
        add(header, BorderLayout.NORTH);

        // ปุ่มหลัก
        JPanel buttonPanel = new JPanel();
        JButton reserveButton = new JButton("Book a classroom");
        JButton viewButton = new JButton("View booking schedule");
        JButton logoutButton = new JButton("Log out");
        buttonPanel.add(reserveButton);
        buttonPanel.add(viewButton);
        buttonPanel.add(logoutButton);
        add(buttonPanel, BorderLayout.CENTER);

        // Action: ปุ่มจองห้อง
        reserveButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "The room reservation function is not yet enabled in this version 😊");
        });

        // Action: ปุ่มดูตาราง
        viewButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Show booking schedule (under development");
        });

        // Action: ปุ่มออกจากระบบ
        logoutButton.addActionListener(e -> {
            dispose(); // ปิดหน้าปัจจุบัน
            LoginUI.show(new AuthSystem()); // กลับไปหน้า Login
        });
    }
}


package ClassroomProject;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class ReservationUI extends JFrame {
    private final Teacher teacher;
    private final ReservationSystem reservationSystem;
    private Classroom classroom;

    public ReservationUI(Teacher teacher, ReservationSystem reservationSystem) {
        super("Classroom Reservation System");
        this.teacher = teacher;
        this.reservationSystem = reservationSystem;

        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10)); // ใช้ BorderLayout เพื่อแบ่งโซน ซ้าย-กลาง
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10)); // เพิ่มช่องว่างรอบๆ ขอบ
        add(mainPanel);

        JPanel leftPanel = createLeftControlPanel();
        mainPanel.add(leftPanel, BorderLayout.WEST); // เพิ่ม Panel ซ้ายเข้าไปในโซน WEST

        JPanel centerPanel = createSchedulerPanel();
        mainPanel.add(centerPanel, BorderLayout.CENTER); // เพิ่ม Panel กลางเข้าไปในโซน CENTER

    }

    private JPanel createLeftControlPanel() {
        JPanel panel = new JPanel();
        // ใช้ BoxLayout เพื่อจัดเรียง Component จากบนลงล่าง
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPreferredSize(new Dimension(180, 0)); // กำหนดความกว้างของ Panel

        // Dropdown สำหรับเลือกห้อง
        // Create a model for the dropdown
        JComboBox<Classroom> roomSelector = new JComboBox<>();
        List<Classroom> classrooms = reservationSystem.getClassrooms();
        if (classrooms != null && !classrooms.isEmpty()) {
            for (Classroom classroom : classrooms) {
                roomSelector.addItem(classroom);
            }
        }
        this.classroom = classrooms.getFirst();
        roomSelector.setMaximumSize(new Dimension(Integer.MAX_VALUE, roomSelector.getPreferredSize().height));

        // ปุ่มต่างๆ
        JButton bookButton = new JButton("  Book a room");
        bookButton.setBackground(new Color(0x1B877A)); // สีเขียวเข้ม
        bookButton.setForeground(Color.WHITE);
        // สามารถเพิ่มไอคอนได้ด้วย: bookButton.setIcon(new ImageIcon("path/to/plus-icon.png"));

        JButton editButton = new JButton("  Edit");
        editButton.setBackground(new Color(0xE7E7C5)); // สีครีม

        JButton exportButton = new JButton("  Export");
        exportButton.setBackground(new Color(0xD6A6A8)); // สีชมพูอ่อน

        // จัดสไตล์ปุ่มให้เหมือนกัน
        styleButton(bookButton);
        styleButton(editButton);
        styleButton(exportButton);

        // เพิ่ม Components เข้าไปใน Panel ซ้าย
        panel.add(roomSelector);
        panel.add(Box.createRigidArea(new Dimension(0, 15))); // เพิ่มช่องว่าง
        panel.add(bookButton);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(editButton);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(exportButton);
        panel.add(Box.createVerticalGlue());
        roomSelector.addActionListener(e -> {
            this.classroom = (Classroom) roomSelector.getSelectedItem();
            //มาเพิ่มโค้ดตรงนี้สำหรับเปลี่ยนตารางสอนของแต่ละห้อง
        });
        bookButton.addActionListener(e -> {
            System.out.println("เปิดเมนูการจอง...");
            new BookRoomUI(this.teacher,this.reservationSystem,this.classroom);
            this.dispose();
        });
        editButton.addActionListener(e -> {
           JOptionPane.showMessageDialog(this, "This function is currently under development.", "Underdeveloping", JOptionPane.WARNING_MESSAGE);
        });
        exportButton.addActionListener(e -> {
           JOptionPane.showMessageDialog(this, "This function is currently under development.", "Underdeveloping", JOptionPane.WARNING_MESSAGE);
        });
        return panel;
    }

    /**
     * เมธอดสำหรับจัดสไตล์ปุ่ม
     */
    private void styleButton(JButton button) {
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        button.setHorizontalAlignment(SwingConstants.LEFT); // จัดข้อความชิดซ้าย
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(10, 20, 10, 20));
    }

    /**
     * เมธอดสำหรับสร้าง Panel ตารางเวลาด้านขวา
     */
    private JPanel createSchedulerPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        // ส่วนหัว (Header)
        JLabel headerLabel = new JLabel("< >  Month/Year");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        headerLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
        panel.add(headerLabel, BorderLayout.NORTH);

        // ส่วนตาราง (Grid)
        JPanel gridPanel = new JPanel();
        int rows = 7; // 1 แถวสำหรับหัวข้อเวลา + 6 แถวสำหรับวัน
        int cols = 12; // 1 คอลัมน์สำหรับวัน + 11 คอลัมน์สำหรับเวลา
        gridPanel.setLayout(new GridLayout(rows, cols));

        // ข้อมูลสำหรับหัวตาราง
        String[] timeSlots = {
                "Day/Time", "8.00-9.00", "9.00-10.00", "10.00-11.00", "11.00-12.00",
                "12.00-13.00", "13.00-14.00", "14.00-15.00", "15.00-16.00",
                "16.00-17.00", "17.00-18.00", "18.00-19.00"
        };
        String[] days = {"MON", "TUE", "WED", "THU", "FRI", "SAT"};

        // Border สำหรับช่องตาราง
        Border cellBorder = BorderFactory.createLineBorder(Color.LIGHT_GRAY);

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                JPanel cell = new JPanel();
                cell.setBorder(cellBorder);

                if (i == 0) { // แถวแรกเป็นหัวข้อเวลา
                    cell.setBackground(new Color(0xF5F5F5)); // สีเทาอ่อน
                    JLabel timeLabel = new JLabel(timeSlots[j], SwingConstants.CENTER);
                    timeLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
                    cell.add(timeLabel);
                } else if (j == 0) { // คอลัมน์แรกเป็นวัน
                    cell.setBackground(new Color(0xF5F5F5));
                    JLabel dayLabel = new JLabel(days[i-1], SwingConstants.CENTER);
                    dayLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
                    cell.add(dayLabel);
                } else { // ช่องตารางเปล่าๆ
                    cell.setBackground(Color.WHITE);
                }
                gridPanel.add(cell);
            }
        }

        panel.add(gridPanel, BorderLayout.CENTER);

        return panel;
    }

}


package ClassroomProject;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener; // (เพิ่มอันนี้ ถ้ายังไม่มี)
import java.awt.event.ActionEvent; // (เพิ่มอันนี้ ถ้ายังไม่มี)

// --- นี่คือกลุ่มที่สำคัญสำหรับแก้ Error ---
import java.util.Locale;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.DayOfWeek;
import java.time.Month; // (เพิ่มอันนี้ ถ้ายังไม่มี)
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
// ----------------------------------------

import java.util.ArrayList; // (เพิ่มอันนี้ ถ้ายังไม่มี)
import java.util.List;
import java.util.Comparator;
import java.util.Vector;
import java.util.stream.Collectors;

// 🔼 สิ้นสุดส่วน import 🔼

public class ReservationUI extends JFrame {
    private final Teacher teacher;
    private final ReservationSystem reservationSystem;
    private Classroom classroom;
    private JPanel mainPanel;     // Make this an instance variable
    private JPanel centerPanel;   // Make this an instance variable
    // --- 🔽 เพิ่ม 4 บรรทัดนี้ 🔽 ---
    private JPanel scheduleContainerPanel; // Panel ใหม่สำหรับรวม Navigation และ ตาราง
    private JPanel navigationPanel;
    private JLabel currentWeekLabel; // Label ที่แสดง "Week of..."
    private java.time.LocalDate currentWeekStart; // วันจันทร์ของสัปดาห์ที่กำลังดู
    // --- 🔽 เพิ่ม 2 บรรทัดนี้ 🔽 ---
    private JList<Booking> myBookingsList; // JList สำหรับแสดงการจองของเรา
    private DefaultListModel<Booking> myBookingsListModel; // Model สำหรับจัดการข้อมูลใน JList

    public ReservationUI(Teacher teacher, ReservationSystem reservationSystem) {
        super("Classroom Reservation System");
        this.teacher = teacher;
        this.reservationSystem = reservationSystem;

        // --- 🔽 [แก้ไข] 🔽 ---
        // 1. ตั้งค่าสัปดาห์เริ่มต้นเป็นสัปดาห์ปัจจุบัน
        this.currentWeekStart = java.time.LocalDate.now()
                .with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));

        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        this.mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        add(mainPanel);

        // 2. สร้าง Left Panel (เหมือนเดิม)
        JPanel leftPanel = createLeftControlPanel();
        mainPanel.add(leftPanel, BorderLayout.WEST);

        // 3. สร้าง Container ใหม่สำหรับ ตาราง+ปุ่มเลื่อนวัน
        this.scheduleContainerPanel = new JPanel(new BorderLayout(10, 10));

        // 4. สร้าง Navigation Panel (ปุ่มเลื่อน)
        this.navigationPanel = createNavigationPanel();

        // 5. สร้าง ตารางเวลา (Scheduler)
        // (เราต้องเลือก classroom เริ่มต้นก่อน)
        if (!this.reservationSystem.getClassrooms().isEmpty()) {
            this.classroom = this.reservationSystem.getClassrooms().get(0);
        }
        this.centerPanel = createSchedulerPanel();

        // 6. เพิ่ม Nav และ ตาราง เข้าไปใน Container
        this.scheduleContainerPanel.add(this.navigationPanel, BorderLayout.NORTH);
        this.scheduleContainerPanel.add(this.centerPanel, BorderLayout.CENTER);

        // 7. เพิ่ม Container หลักเข้าสู่ mainPanel
        mainPanel.add(this.scheduleContainerPanel, BorderLayout.CENTER);

        // --- 🔼 [สิ้นสุดการแก้ไข] 🔼 ---

        // (โค้ดสำหรับแสดง 'My Bookings' ทางด้านซ้าย เหมือนเดิม)
        refreshMyBookings();
    }

    /**
     * [ใหม่] รีเฟรชรายการ "My Bookings" (การจองของเรา) ใน Panel ด้านซ้าย
     */
    public void refreshMyBookings() {
        if (myBookingsListModel == null) {
            return; // ยังไม่พร้อม
        }

        myBookingsListModel.clear();
        java.util.List<Booking> allMyBookings = new java.util.ArrayList<>();

        // 1. วนลูปทุกห้อง
        for (Classroom c : reservationSystem.getClassrooms()) {
            // 2. วนลูปทุกการจองในห้องนั้น
            for (Booking b : c.getBookings()) {
                // 3. ถ้าเป็นของเรา ให้เพิ่ม
                if (b.getTeacher().equals(this.teacher)) {
                    allMyBookings.add(b);
                }
            }
        }

        // 4. เรียงลำดับการจองตามวันที่ (ใหม่สุดไปเก่าสุด)
        allMyBookings.sort(Comparator.comparing(Booking::getDate).reversed());

        // 5. เพิ่มเข้าไปใน ListModel
        for (Booking b : allMyBookings) {
            myBookingsListModel.add(0, b); // เพิ่มจากด้านบน
        }
    }

    /**
     * [ใหม่] สร้าง Panel สำหรับปุ่มเลื่อนสัปดาห์ (Prev, Next, Today)
     */
    private JPanel createNavigationPanel() {
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        navPanel.setBackground(new Color(0xF5F5F5));
        navPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        JButton todayButton = new JButton("Today");
        JButton prevWeekButton = new JButton("< Prev");
        JButton nextWeekButton = new JButton("Next >");

        this.currentWeekLabel = new JLabel("", SwingConstants.CENTER);
        this.currentWeekLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        updateNavigationLabel(); // ตั้งค่าข้อความเริ่มต้น

        // Action: Today
        todayButton.addActionListener(e -> {
            this.currentWeekStart = java.time.LocalDate.now()
                    .with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
            refreshSchedule();
        });

        // Action: Previous Week
        prevWeekButton.addActionListener(e -> {
            this.currentWeekStart = this.currentWeekStart.minusWeeks(1);
            refreshSchedule();
        });

        // Action: Next Week
        nextWeekButton.addActionListener(e -> {
            this.currentWeekStart = this.currentWeekStart.plusWeeks(1);
            refreshSchedule();
        });

        navPanel.add(todayButton);
        navPanel.add(prevWeekButton);
        navPanel.add(this.currentWeekLabel);
        navPanel.add(nextWeekButton);

        return navPanel;
    }

    /**
     * [ใหม่] อัปเดตข้อความ Label ที่แสดงช่วงวันที่ของสัปดาห์
     */
    private void updateNavigationLabel() {
        java.time.LocalDate weekEnd = this.currentWeekStart.plusDays(5); // MON -> SAT
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("d MMM yyyy", java.util.Locale.ENGLISH);

        String labelText = String.format("Week: %s - %s",
                this.currentWeekStart.format(formatter),
                weekEnd.format(formatter)
        );
        this.currentWeekLabel.setText(labelText);
    }

    /**
     * [แก้ไข] สร้าง Panel ควบคุมด้านซ้าย (ที่รวมปุ่ม "Edit")
     */
    private JPanel createLeftControlPanel() {
        // ใช้ BoxLayout เพื่อจัดเรียงจากบนลงล่าง
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setPreferredSize(new Dimension(280, 600));
        leftPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Controls"),
                new EmptyBorder(10, 10, 10, 10)
        ));

        // --- 1. Welcome Label ---
        JLabel welcomeLabel = new JLabel("Welcome, " + teacher.getName());
        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        welcomeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        leftPanel.add(welcomeLabel);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // --- 2. Room Selector ---
        JLabel roomLabel = new JLabel("Select Classroom:");
        roomLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        leftPanel.add(roomLabel);

        // (ใช้ Vector สำหรับ JComboBox)
        Vector<Classroom> classroomVector = new Vector<>(this.reservationSystem.getClassrooms());
        JComboBox<Classroom> roomComboBox = new JComboBox<>(classroomVector);
        roomComboBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        roomComboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, roomComboBox.getPreferredSize().height));

        // เลือกห้องแรกเป็นค่าเริ่มต้น
        if (this.classroom != null) {
            roomComboBox.setSelectedItem(this.classroom);
        }

        roomComboBox.addActionListener(e -> {
            // เมื่อเลือกห้องใหม่ ให้อัปเดต 'this.classroom'
            this.classroom = (Classroom) roomComboBox.getSelectedItem();
            // สั่งให้ตารางด้านขวารีเฟรช
            refreshSchedule();
        });
        leftPanel.add(roomComboBox);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // --- 3. Book Button ---
        JButton bookButton = new JButton("Book This Room");
        bookButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        bookButton.setBackground(new Color(0x1B877A));
        bookButton.setForeground(Color.WHITE);
        bookButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        bookButton.addActionListener(e -> {
            // เปิดหน้า BookRoomUI
            new BookRoomUI(teacher, reservationSystem, this.classroom).setVisible(true);
            this.dispose();
        });
        leftPanel.add(bookButton);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 20))); // เพิ่มช่องว่าง

        // --- 4. My Bookings List ---
        JLabel myBookingsLabel = new JLabel("My Bookings");
        myBookingsLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        myBookingsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        leftPanel.add(myBookingsLabel);

        // [แก้ไข]
        // 4a. สร้าง Model และ List โดยใช้ตัวแปร instance
        this.myBookingsListModel = new DefaultListModel<>();
        this.myBookingsList = new JList<>(this.myBookingsListModel);

        this.myBookingsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.myBookingsList.setCellRenderer(new BookingCellRenderer()); // (เดี๋ยวเราจะเพิ่มคลาสนี้)

        JScrollPane scrollPane = new JScrollPane(this.myBookingsList);
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollPane.setPreferredSize(new Dimension(260, 300));
        leftPanel.add(scrollPane);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // --- 5. Edit Button ---
        JButton editButton = new JButton("Edit Selected Booking");
        editButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        editButton.addActionListener(e -> {
            // [แก้ไข] ใช้ this.myBookingsList
            Booking selectedBooking = this.myBookingsList.getSelectedValue();

            if (selectedBooking != null) {
                // ค้นหาว่าการจองนี้ อยู่ในห้องไหน
                Classroom bookingClassroom = null;
                for (Classroom c : reservationSystem.getClassrooms()) {
                    if (c.getBookings().contains(selectedBooking)) {
                        bookingClassroom = c;
                        break;
                    }
                }

                if (bookingClassroom != null) {
                    // เปิดหน้า EditUI
                    new EditUI(teacher, reservationSystem, bookingClassroom, selectedBooking, this).setVisible(true);
                    this.setVisible(false);
                } else {
                    JOptionPane.showMessageDialog(this, "Error: Could not find classroom for this booking.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a booking from 'My Bookings' list to edit.");
            }
        });
        leftPanel.add(editButton);

        // --- 6. Spacer ---
        leftPanel.add(Box.createVerticalGlue()); // ดันทุกอย่างขึ้นไป

        return leftPanel;
    }

    /**
     * เมธอดสำหรับจัดสไตล์ปุ่ม
     */
    private void styleButton(JButton button) {
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        button.setHorizontalAlignment(SwingConstants.LEFT); // จัดข้อความชิดซ้าย
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(10, 20, 10, 20));
    }

    /**
     * เมธอดสำหรับสร้าง Panel ตารางเวลาด้านขวา
     * (เวอร์ชันอัปเดต 4: เปลี่ยนสีการจองของเราตามวัน + สีมินิมอล)
     */
    /**
     * เมธอดสำหรับสร้าง Panel ตารางเวลาด้านขวา
     * (เวอร์ชันอัปเดต 5: แสดงวันที่จริง และ ตรวจสอบการจองตามวันที่)
     */
    private JPanel createSchedulerPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        // ส่วนหัว (Header) - (ย้ายไปอยู่ใน Navigation Panel แล้ว)
        String headerText = (this.classroom != null) ? "Schedule: " + this.classroom.getName() : "Schedule";
        JLabel headerLabel = new JLabel(headerText, SwingConstants.CENTER);
        headerLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        headerLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
        panel.add(headerLabel, BorderLayout.NORTH); // ยังคงแสดงชื่อห้องไว้

        // ส่วนตาราง (Grid)
        JPanel gridPanel = new JPanel();

        // --- 1. กำหนดช่วงเวลา (เหมือนเดิม) ---
        String[] timeHeaders = {
                "8:00-9:00", "9:00-10:00", "10:00-11:00", "11:00-12:00",
                "12:00-13:00", "13:00-14:00", "14:00-15:00", "15:00-16:00",
                "16:00-17:00", "17:00-18:00"
        };
        java.time.LocalTime[] slotStartTimes = {
                java.time.LocalTime.of(8, 0), java.time.LocalTime.of(9, 0),
                java.time.LocalTime.of(10, 0), java.time.LocalTime.of(11, 0),
                java.time.LocalTime.of(12, 0), java.time.LocalTime.of(13, 0),
                java.time.LocalTime.of(14, 0), java.time.LocalTime.of(15, 0),
                java.time.LocalTime.of(16, 0), java.time.LocalTime.of(17, 0)
        };
        java.time.LocalTime[] slotEndTimes = {
                java.time.LocalTime.of(9, 0), java.time.LocalTime.of(10, 0),
                java.time.LocalTime.of(11, 0), java.time.LocalTime.of(12, 0),
                java.time.LocalTime.of(13, 0), java.time.LocalTime.of(14, 0),
                java.time.LocalTime.of(15, 0), java.time.LocalTime.of(16, 0),
                java.time.LocalTime.of(17, 0), java.time.LocalTime.of(18, 0)
        };

        // --- 2. [แก้ไข] สร้าง Array ของวันและวันที่ จาก currentWeekStart ---
        String[] dayNames = {"MON", "TUE", "WED", "THU", "FRI", "SAT"};
        java.time.LocalDate[] datesForThisWeek = new java.time.LocalDate[6];
        String[] dayHeaders = new String[6]; // สำหรับแสดงผล (เช่น "MON\n3")
        java.time.format.DateTimeFormatter dayFormatter = java.time.format.DateTimeFormatter.ofPattern("d");

        for (int i = 0; i < 6; i++) {
            datesForThisWeek[i] = this.currentWeekStart.plusDays(i);
            // ใช้ HTML เพื่อให้ขึ้นบรรทัดใหม่
            dayHeaders[i] = String.format("<html><div style='text-align: center;'>%s<br><span style='font-size:14pt; font-weight:bold;'>%s</span></div></html>",
                    dayNames[i],
                    datesForThisWeek[i].format(dayFormatter)
            );
        }
        // -----------------------------------------------------------------

        int rows = 1 + dayHeaders.length; // 1 + 6
        int cols = 1 + timeHeaders.length; // 1 + 10

        gridPanel.setLayout(new GridLayout(rows, cols, 2, 2));
        Border cellBorder = BorderFactory.createLineBorder(Color.LIGHT_GRAY);

        List<Booking> bookings = (this.classroom != null) ? this.classroom.getBookings() : new java.util.ArrayList<>();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                JPanel cell = new JPanel(new BorderLayout(2, 2));
                cell.setBorder(cellBorder);

                if (i == 0) { // แถวหัวข้อเวลา
                    cell.setBackground(new Color(0xF5F5F5));
                    JLabel timeLabel = (j == 0) ? new JLabel("Day/Time", SwingConstants.CENTER) : new JLabel(timeHeaders[j - 1], SwingConstants.CENTER);
                    timeLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
                    cell.add(timeLabel, BorderLayout.CENTER);

                } else if (j == 0) { // คอลัมน์วัน (ที่อัปเดตแล้ว)
                    cell.setBackground(new Color(0xF5F5F5));
                    // [แก้ไข] ใช้ dayHeaders ที่มี HTML
                    JLabel dayLabel = new JLabel(dayHeaders[i - 1], SwingConstants.CENTER);
                    dayLabel.setFont(new Font("SansSerif", Font.BOLD, 10)); // ปรับขนาด Font พื้นฐาน
                    cell.add(dayLabel, BorderLayout.CENTER);

                } else { // ช่องตารางข้อมูล

                    // --- 3. [แก้ไข] ตรวจสอบการจองด้วย "วันที่" (LocalDate) ---

                    // ดึง "วันที่" และ "เวลา" ของช่องตารางปัจจุบัน
                    java.time.LocalDate cellDate = datesForThisWeek[i - 1]; // e.g., 2025-11-03
                    java.time.LocalTime cellStartTime = slotStartTimes[j - 1]; // e.g., 09:00
                    java.time.LocalTime cellEndTime = slotEndTimes[j - 1];   // e.g., 10:00

                    Booking matchedBooking = null;
                    for (Booking b : bookings) {

                        // 3a. เช็คว่าการจอง (b) อยู่ใน "วันที่" เดียวกับช่องตาราง (cellDate) หรือไม่
                        if (b.getDate().isEqual(cellDate)) {

                            // 3b. ถ้าใช่, เช็คว่า "เวลา" ทับซ้อนกับช่องตารางหรือไม่
                            java.time.LocalTime bookingStartTime = b.getTimeSlot().getStartTime();
                            java.time.LocalTime bookingEndTime = b.getTimeSlot().getEndTime();

                            // Logic: (StartA < EndB) และ (StartB < EndA)
                            boolean timeOverlap = bookingStartTime.isBefore(cellEndTime) &&
                                    cellStartTime.isBefore(bookingEndTime);

                            if (timeOverlap) {
                                matchedBooking = b;
                                break;
                            }
                        }
                    }
                    // -------------------------------------------------------------

                    if (matchedBooking != null) {
                        // --- 4. ถ้ามีการจอง ---
                        if (matchedBooking.getTeacher().equals(this.teacher)) {
                            // 4a. จอง "ของเรา" -> ตั้งค่าสีตามวัน
                            java.time.DayOfWeek bookingDay = cellDate.getDayOfWeek(); // ใช้ DayOfWeek ของช่องตาราง
                            Color bookingColor;
                            switch (bookingDay) {
                                case MONDAY: bookingColor = new Color(0xFFF9C4); break; // เหลือง
                                case TUESDAY: bookingColor = new Color(0xF8BBD0); break; // ชมพู
                                case WEDNESDAY: bookingColor = new Color(0xC8E6C9); break; // เขียว
                                case THURSDAY: bookingColor = new Color(0xFFE0B2); break; // ส้ม
                                case FRIDAY: bookingColor = new Color(0xB3E5FC); break; // ฟ้า
                                case SATURDAY: bookingColor = new Color(0xD1C4E9); break; // ม่วง
                                default: bookingColor = new Color(0xE0E0E0);
                            }
                            cell.setBackground(bookingColor);
                        } else {
                            // 4b. จอง "ของคนอื่น"
                            cell.setBackground(new Color(0xE0E0E0)); // สีเทา
                        }

                        // (ส่วนแสดงผลข้อความ เหมือนเดิม)
                        String courseText = String.format("<html><div style='text-align: center;'>%s<br>(%s)</div></html>",
                                matchedBooking.getCourse(), matchedBooking.getCode());
                        JLabel courseLabel = new JLabel(courseText, SwingConstants.CENTER);
                        courseLabel.setFont(new Font("SansSerif", Font.BOLD, 12));

                        JLabel teacherLabel = new JLabel(matchedBooking.getTeacher().getName(), SwingConstants.CENTER);
                        teacherLabel.setFont(new Font("SansSerif", Font.PLAIN, 10));

                        if (matchedBooking.getTeacher().equals(this.teacher)) {
                            courseLabel.setForeground(Color.DARK_GRAY);
                            teacherLabel.setForeground(Color.DARK_GRAY);
                        } else {
                            courseLabel.setForeground(Color.BLACK);
                            teacherLabel.setForeground(Color.DARK_GRAY);
                        }
                        cell.add(courseLabel, BorderLayout.CENTER);
                        cell.add(teacherLabel, BorderLayout.SOUTH);

                    } else {
                        // --- 5. ถ้าไม่มีการจอง ---
                        cell.setBackground(Color.WHITE);
                    }
                }
                gridPanel.add(cell);
            }
        }

        panel.add(gridPanel, BorderLayout.CENTER);
        return panel;
    }
    /**
     * [แก้ไข] รีเฟรชตารางเวลา (เมื่อเปลี่ยนห้อง หรือ เปลี่ยนสัปดาห์)
     */
    public void refreshSchedule() {
        System.out.println("Refreshing schedule for week: " + this.currentWeekStart);

        // 1. อัปเดต Label สัปดาห์
        if (this.currentWeekLabel != null) {
            updateNavigationLabel();
        }

        // 2. ลบตารางเก่า (centerPanel) ออกจาก Container
        if (centerPanel != null) {
            this.scheduleContainerPanel.remove(centerPanel);
        }

        // 3. สร้างตารางใหม่ด้วยข้อมูลล่าสุด (classroom + currentWeekStart)
        this.centerPanel = createSchedulerPanel();

        // 4. เพิ่มตารางใหม่กลับเข้าไป
        this.scheduleContainerPanel.add(this.centerPanel, BorderLayout.CENTER);

        // 5. สั่งให้ UI วาดใหม่
        this.scheduleContainerPanel.revalidate();
        this.scheduleContainerPanel.repaint();
    }

    class BookingCellRenderer extends JPanel implements ListCellRenderer<Booking> {

        private JLabel roomLabel;
        private JLabel courseLabel;
        private JLabel dateTimeLabel;
        private JPanel textPanel; // ⬅️ นี่คือตัวแปร textPanel ที่ถูกประกาศ

        public BookingCellRenderer() {
            setLayout(new BorderLayout(5, 5));
            setBorder(new EmptyBorder(5, 5, 5, 5));

            // Panel สำหรับข้อความ
            textPanel = new JPanel(new GridLayout(2, 1)); // ⬅️ นี่คือตอนที่มันถูกสร้าง
            courseLabel = new JLabel();
            courseLabel.setFont(new Font("SansSerif", Font.BOLD, 14));

            dateTimeLabel = new JLabel();
            dateTimeLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));

            textPanel.add(courseLabel);
            textPanel.add(dateTimeLabel);

            // Label สำหรับชื่อห้อง (จะอยู่ด้านขวา)
            roomLabel = new JLabel();
            roomLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
            roomLabel.setHorizontalAlignment(SwingConstants.RIGHT);

            add(textPanel, BorderLayout.CENTER);
            add(roomLabel, BorderLayout.EAST);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends Booking> list, Booking booking, int index,
                                                      boolean isSelected, boolean cellHasFocus) {

            // --- 1. ค้นหาชื่อห้อง
            String roomName = "Unknown Room";
            for (Classroom c : reservationSystem.getClassrooms()) {
                if (c.getBookings().contains(booking)) {
                    roomName = c.getName();
                    break;
                }
            }

            // --- 2. ตั้งค่าข้อความ
            roomLabel.setText(roomName);
            courseLabel.setText(String.format("%s (%s)", booking.getCourse(), booking.getCode()));

            // --- 3. ตั้งค่าวันที่/เวลา
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEE d MMM yyyy", java.util.Locale.ENGLISH);
            String dateStr = booking.getDate().format(dateFormatter);
            String timeStr = String.format("%s - %s",
                    booking.getTimeSlot().getStartTime(),
                    booking.getTimeSlot().getEndTime());
            dateTimeLabel.setText(dateStr + " | " + timeStr);

            // --- 4. ตั้งค่าสี
            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
                textPanel.setBackground(list.getSelectionBackground()); // ⬅️ ใช้งาน textPanel
                textPanel.setForeground(list.getSelectionForeground());
                roomLabel.setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
                textPanel.setBackground(list.getBackground()); // ⬅️ ใช้งาน textPanel
                textPanel.setForeground(list.getForeground());
                roomLabel.setForeground(Color.GRAY);
            }

            // ไฮไลท์วันที่ที่ผ่านมาแล้ว
            if (booking.getDate().isBefore(LocalDate.now())) { // ⬅️ ใช้งาน LocalDate
                courseLabel.setForeground(Color.GRAY);
                dateTimeLabel.setForeground(Color.GRAY);
            } else {
                courseLabel.setForeground(Color.BLACK);
                dateTimeLabel.setForeground(Color.DARK_GRAY);
            }

            if(isSelected){
                courseLabel.setForeground(list.getSelectionForeground());
                dateTimeLabel.setForeground(list.getSelectionForeground());
            }

            return this;
        }
    }

} // ⬅️ นี่คือ } ปิดท้ายของคลาส ReservationUI (โค้ด BookingCellRenderer ต้องอยู่ก่อนหน้านี้)



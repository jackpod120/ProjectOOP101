package ClassroomProject;
import javax.swing.*;
import java.awt.*;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.Month;
import java.util.Objects;

public class BookRoomUI extends JFrame {

    private final Teacher teacher;
    private final Classroom classroom;

    public BookRoomUI(Teacher teacher, ReservationSystem reservationSystem, Classroom classroom) {
        this.teacher = teacher;
        this.classroom = classroom;
        setTitle("Book a Room");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        // Main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(new Color(245, 247, 250));
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Card-like panel
        JPanel card = new JPanel();
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true));
        card.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8, 10, 8, 10);
        c.fill = GridBagConstraints.HORIZONTAL;

        // Header
        JLabel header = new JLabel("Book a room");
        header.setFont(new Font("SansSerif", Font.BOLD, 16));
        c.gridx = 0; c.gridy = 0; c.gridwidth = 2;
        card.add(header, c);

        JLabel subHeader = new JLabel("Enter your information below to Book a room");
        subHeader.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subHeader.setForeground(Color.GRAY);
        c.gridy++;
        card.add(subHeader, c);

        // Fields
        c.gridwidth = 1;
        c.gridy++;
        card.add(new JLabel("Course : "), c);
        JTextField courseField = new JTextField();
        c.gridx = 1;
        card.add(courseField, c);

        c.gridx = 0; c.gridy++;
        card.add(new JLabel("Code : "), c);
        JTextField codeField = new JTextField();
        c.gridx = 1;
        card.add(codeField, c);

        c.gridx = 0; c.gridy++;
        card.add(new JLabel("Time : "), c);
        JComboBox<String> timeBox = new JComboBox<>(new String[]{"Select",
                "8.00-8.30","8.30-9.00","9.00-9.30",
                "9.30-10.00","10.00-10.30","10.30-11.00",
                "11.00-11.30","11.30-12.00","13.00-13.30",
                "13.30-14.00","14.00-14.30","14.30-15.00",
                "15.00-15.30","15.30-16.00","16.00-16.30",
                "16.30-17.00","17.00-17.30","17.30-18.00",
                "18.00-18.30","18.30-19.00"
        });
        c.gridx = 1;
        card.add(timeBox, c);

        c.gridx = 0; c.gridy++;
        card.add(new JLabel("Booking type : "), c);
        JComboBox<String> bookingTypeBox = new JComboBox<>(new String[]{"Select","Daily","Monthly","Term"});
        c.gridx = 1;
        card.add(bookingTypeBox, c);

        c.gridx = 0; c.gridy++;
        card.add(new JLabel("Days : "), c);
        JComboBox<String> daysBox = new JComboBox<>(new String[]{"Select","Mon","Tue","Wed","Thu","Fri","Sat"});
        c.gridx = 1;
        card.add(daysBox, c);

        // Confirm button
        c.gridx = 0; c.gridy++;
        c.gridwidth = 2;
        JButton confirmBtn = new JButton("confirm");
        confirmBtn.setBackground(new Color(0, 102, 91));
        confirmBtn.setForeground(Color.WHITE);
        confirmBtn.setFocusPainted(false);
        confirmBtn.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        card.add(confirmBtn, c);
        confirmBtn.addActionListener(_ -> {
            System.out.println("กำลังพยายามจอง...");
            String type = Objects.requireNonNull(bookingTypeBox.getSelectedItem()).toString();
            String days = Objects.requireNonNull(daysBox.getSelectedItem()).toString();
            String time = Objects.requireNonNull(timeBox.getSelectedItem()).toString();
            String course = courseField.getText();
            String code = codeField.getText();
            ReservationType reservationType = null;
            DayOfWeek dayOfWeek = null;
            LocalTime timeTimeSlotStart = null;
            LocalTime timeTimeSlotEnd = null;
            TimeSlot timeSlot;
            boolean isAvailable = true;
            switch (type) {
                case "Daily":
                    reservationType = ReservationType.DAILY;
                    break;
                    case "Monthly":
                        reservationType = ReservationType.MONTHLY;
                        break;
                        case "Term":
                            reservationType = ReservationType.TERM;
                            break;
                            default:
                                System.out.println("❌ เกิดข้อผิดพลาด, โปรดใส่ประเภทการจอง");
                                JOptionPane.showMessageDialog(this,"Please select the type you want to book.","Error",JOptionPane.ERROR_MESSAGE);
                                isAvailable = false;
                                break;
            }
            switch (days) {
                case "Mon":
                    dayOfWeek = DayOfWeek.MONDAY;
                    break;
                    case "Tue":
                        dayOfWeek = DayOfWeek.TUESDAY;
                        break;
                        case "Wed":
                            dayOfWeek = DayOfWeek.WEDNESDAY;
                            break;
                            case "Thu":
                                dayOfWeek = DayOfWeek.THURSDAY;
                                break;
                                case "Fri":
                                    dayOfWeek = DayOfWeek.FRIDAY;
                                    break;
                                    case "Sat":
                                        dayOfWeek = DayOfWeek.SATURDAY;
                                        break;
                                        default:
                                            System.out.println("❌ เกิดข้อผิดพลาด, โปรดใส่วันที่ต้องการจอง");
                                            JOptionPane.showMessageDialog(this,"Please select the day you want to book.","Error",JOptionPane.ERROR_MESSAGE);
                                            isAvailable = false;
                                            break;
            }
            switch (time) {
                case "8.00-8.30":
                    timeTimeSlotStart = LocalTime.of(8, 0);
                    timeTimeSlotEnd = LocalTime.of(8, 30);
                    break;
                    case "8.30-9.00":
                        timeTimeSlotStart = LocalTime.of(8, 30);
                        timeTimeSlotEnd = LocalTime.of(9, 0);
                        break;
                        case "9.00-9.30":
                            timeTimeSlotStart = LocalTime.of(9, 0);
                            timeTimeSlotEnd = LocalTime.of(9, 30);
                            break;
                            case "9.30-10.00":
                                timeTimeSlotStart = LocalTime.of(9, 30);
                                timeTimeSlotEnd = LocalTime.of(10, 0);
                                break;
                                case "10.00-10.30":
                                    timeTimeSlotStart = LocalTime.of(10, 0);
                                    timeTimeSlotEnd = LocalTime.of(10, 30);
                                    break;
                                    case "10.30-11.00":
                                        timeTimeSlotStart = LocalTime.of(10, 30);
                                        timeTimeSlotEnd = LocalTime.of(11, 0);
                                        break;
                                        case "11.00-11.30":
                                            timeTimeSlotStart = LocalTime.of(11, 0);
                                            timeTimeSlotEnd = LocalTime.of(11, 30);
                                            break;
                                            case "11.30-12.00":
                                                timeTimeSlotStart = LocalTime.of(11, 30);
                                                timeTimeSlotEnd = LocalTime.of(12, 0);
                                                break;
                                                case "13.00-13.30":
                                                    timeTimeSlotStart = LocalTime.of(13, 0);
                                                    timeTimeSlotEnd = LocalTime.of(13, 30);
                                                    break;
                                                    case "13.30-14.00":
                                                        timeTimeSlotStart = LocalTime.of(13, 30);
                                                        timeTimeSlotEnd = LocalTime.of(14, 0);
                                                        break;
                                                        case "14.00-14.30":
                                                            timeTimeSlotStart = LocalTime.of(14, 0);
                                                            timeTimeSlotEnd = LocalTime.of(14, 30);
                                                            break;
                                                            case "14.30-15.00":
                                                                timeTimeSlotStart = LocalTime.of(14, 30);
                                                                timeTimeSlotEnd = LocalTime.of(15, 0);
                                                                break;
                                                                case "15.00-15.30":
                                                                    timeTimeSlotStart = LocalTime.of(15, 0);
                                                                    timeTimeSlotEnd = LocalTime.of(15, 30);
                                                                    break;
                                                                    case "15.30-16.00":
                                                                        timeTimeSlotStart = LocalTime.of(15, 30);
                                                                        timeTimeSlotEnd = LocalTime.of(16, 0);
                                                                        break;
                                                                        case "16.00-16.30":
                                                                            timeTimeSlotStart = LocalTime.of(16, 0);
                                                                            timeTimeSlotEnd = LocalTime.of(16, 30);
                                                                            break;
                                                                            case "16.30-17.00":
                                                                                timeTimeSlotStart = LocalTime.of(16, 30, 0);
                                                                                timeTimeSlotEnd = LocalTime.of(17, 0, 0);
                                                                                break;
                                                                                case "17.00-17.30":
                                                                                    timeTimeSlotStart = LocalTime.of(17, 0);
                                                                                    timeTimeSlotEnd = LocalTime.of(17, 30);
                                                                                    break;
                                                                                    case "17.30-18.00":
                                                                                        timeTimeSlotStart = LocalTime.of(17, 30);
                                                                                        timeTimeSlotEnd = LocalTime.of(18, 0);
                                                                                        break;
                                                                                        case "18.00-18.30":
                                                                                            timeTimeSlotStart = LocalTime.of(18, 0);
                                                                                            timeTimeSlotEnd = LocalTime.of(18, 30);
                                                                                            break;
                                                                                            case "18.30-19.00":
                                                                                                timeTimeSlotStart = LocalTime.of(18, 30);
                                                                                                timeTimeSlotEnd = LocalTime.of(19, 0);
                                                                                                break;
                                                                                                default:
                                                                                                    System.out.println("❌ เกิดข้อผิดพลาด, โปรดใส่เวลาที่ต้องการจอง");
                                                                                                    JOptionPane.showMessageDialog(this,"Please select the time you want to book.","Error",JOptionPane.ERROR_MESSAGE);
                                                                                                    isAvailable = false;
                                                                                                    break;
            }
            if(course.isEmpty()){
                System.out.println("❌ เกิดข้อผิดพลาด, โปรดใส่ชื่อวิชา");
                JOptionPane.showMessageDialog(this,"Please enter your course's name you want to book.","Error",JOptionPane.ERROR_MESSAGE);
            }
            if(code.isEmpty()){
                System.out.println("❌ เกิดข้อผิดพลาด, โปรดใส่รหัสวิชา");
                JOptionPane.showMessageDialog(this,"Please enter your course's code you want to book.","Error",JOptionPane.ERROR_MESSAGE);

            }
            if(isAvailable) {
                timeSlot = new TimeSlot(dayOfWeek,timeTimeSlotStart,timeTimeSlotEnd);
                if(reservationSystem.makeReservation(this.teacher, this.classroom, timeSlot, reservationType, 2025, Month.AUGUST, course, code)) {
                    this.classroom.displaySchedule();
                    new ReservationUI(teacher, reservationSystem).setVisible(true);
                    this.dispose();
                }else{
                    JOptionPane.showMessageDialog(this,"Sorry, your time is already in use.","Error",JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JButton goBackButton = new JButton("< Go Back");
        goBackButton.setFont(new Font("SansSerif", Font.BOLD, 10));
        goBackButton.setBackground(new Color(230, 230, 230));
        c.gridx = 0; c.gridy++; c.gridwidth = 1;
        card.add(goBackButton, c);
        goBackButton.addActionListener(e -> {
            new ReservationUI(teacher, reservationSystem).setVisible(true);
            this.dispose();
        });

        // Add card to main panel
        gbc.fill = GridBagConstraints.NONE;
        mainPanel.add(card, gbc);

        add(mainPanel);
        setVisible(true);
    }

}


package ClassroomProject;
import javax.swing.*;
import java.awt.*;
import java.time.Month;

public class BookRoomUI extends JFrame {

    private final Teacher teacher;
    private final ReservationSystem reservationSystem;
    private final Classroom classroom;

    public BookRoomUI(Teacher teacher, ReservationSystem reservationSystem, Classroom classroom) {
        this.teacher = teacher;
        this.reservationSystem = reservationSystem;
        this.classroom = classroom;
        setTitle("Book a Room");
        setSize(400, 400);
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
        JComboBox<String> timeBox = new JComboBox<>(new String[]{"example"});
        c.gridx = 1;
        card.add(timeBox, c);

        c.gridx = 0; c.gridy++;
        card.add(new JLabel("Booking type : "), c);
        JComboBox<String> bookingTypeBox = new JComboBox<>(new String[]{"example"});
        c.gridx = 1;
        card.add(bookingTypeBox, c);

        c.gridx = 0; c.gridy++;
        card.add(new JLabel("Days : "), c);
        JComboBox<String> daysBox = new JComboBox<>(new String[]{"example"});
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
        confirmBtn.addActionListener(e -> {
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


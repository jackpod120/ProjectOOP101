package ClassroomProject;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

public class LoginUI extends JFrame {
    private final AuthSystem authSystem;
    private final CardLayout cardLayout;
    private final ReservationSystem reservationSystem;
    private final JPanel cardPanel;

    public LoginUI(AuthSystem authSystem, ReservationSystem reservationSystem) {
        super("Classroom Reservation - Auth");
        this.authSystem = authSystem;
        this.reservationSystem = reservationSystem;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(520, 420);
        setLocationRelativeTo(null);

        getContentPane().setLayout(new BorderLayout());

        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setBorder(new EmptyBorder(16, 16, 16, 16));
        getContentPane().add(centerWrapper, BorderLayout.CENTER);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230)),
                new EmptyBorder(18, 24, 24, 24)
        ));

        GridBagConstraints rootGbc = new GridBagConstraints();
        rootGbc.gridx = 0;
        rootGbc.gridy = 0;
        rootGbc.fill = GridBagConstraints.HORIZONTAL;
        centerWrapper.add(cardPanel, rootGbc);

        cardPanel.add(createSignInPanel(), "signin");
        cardPanel.add(createSignUpPanel(), "signup");

        cardLayout.show(cardPanel, "signin");
    }

    private JPanel createHeader(String title, String subtitle) {
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setOpaque(false);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 18f));
        JLabel subLabel = new JLabel(subtitle);
        subLabel.setForeground(new Color(110, 110, 110));

        header.add(titleLabel);
        header.add(Box.createVerticalStrut(4));
        header.add(subLabel);
        header.add(Box.createVerticalStrut(12));
        return header;
    }

    private JLabel createLink(String text, String cardToShow) {
        JLabel link = new JLabel(text);
        link.setForeground(new Color(20, 115, 105));
        link.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        link.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                cardLayout.show(cardPanel, cardToShow);
            }
        });
        return link;
    }

    private JPanel createSignUpPanel() {
        JPanel root = new JPanel();
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));

        root.add(createHeader("Create an account", "Enter your information below to create your account"));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.anchor = GridBagConstraints.WEST;

        JTextField nameField = new JTextField(24);
        JLabel gmailLabel = new JLabel("Email");
        JTextField gmailField = new JTextField(24);
        JLabel passwordLabel = new JLabel("Password");
        JPasswordField passwordField = new JPasswordField(24);
        JLabel idLabel = new JLabel("ID");
        JTextField idField = new JTextField(24);
        JButton signUpButton = new JButton("Create Account");

        gbc.gridx = 0; gbc.gridy = 0; form.add(gmailLabel, gbc);
        gbc.gridx = 1; form.add(gmailField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; form.add(new JLabel("Full Name"), gbc);
        gbc.gridx = 1; form.add(nameField, gbc);
        gbc.gridx = 0; gbc.gridy = 2; form.add(passwordLabel, gbc);
        gbc.gridx = 1; form.add(passwordField, gbc);
        gbc.gridx = 0; gbc.gridy = 3; form.add(idLabel, gbc);
        gbc.gridx = 1; form.add(idField, gbc);
        gbc.gridx = 1; gbc.gridy = 4; form.add(signUpButton, gbc);

        root.add(form);
        root.add(Box.createVerticalStrut(12));

        JPanel footer = new JPanel();
        footer.setOpaque(false);
        footer.add(new JLabel("Already have an account? "));
        footer.add(createLink("Sign in", "signin"));
        root.add(footer);

        signUpButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String gmail = gmailField.getText().trim();
            String id = idField.getText().trim();
            String password = new String(passwordField.getPassword());

            if (name.isEmpty() || gmail.isEmpty() || id.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter all information", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }

            boolean ok = authSystem.signUp(name, gmail, id, password);
            if (ok) {
                JOptionPane.showMessageDialog(this, "✅ Sign Up successed! Congratulation, " + name, "Sign Up", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "❌ Sign Up Failed: Gmail account has already sign up", "Sign Up", JOptionPane.ERROR_MESSAGE);
            }
        });

        return root;
    }

    private JPanel createSignInPanel() {
        JPanel root = new JPanel();
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        root.setOpaque(false);
        root.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        root.add(createHeader("Login to your account", "Enter your email below to login to your account"));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel gmailLabel = new JLabel("Email");
        JTextField gmailField = new JTextField(24);
        JLabel passwordLabel = new JLabel("Password");
        JPasswordField passwordField = new JPasswordField(24);
        JButton signInButton = new JButton("Login");

        gbc.gridx = 0; gbc.gridy = 0; form.add(gmailLabel, gbc);
        gbc.gridx = 1; form.add(gmailField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; form.add(passwordLabel, gbc);
        gbc.gridx = 1; form.add(passwordField, gbc);
        gbc.gridx = 1; gbc.gridy = 2; form.add(signInButton, gbc);

        root.add(form);
        root.add(Box.createVerticalStrut(12));

        JPanel footer = new JPanel();
        footer.setOpaque(false);
        footer.add(new JLabel("Don't have an account? "));
        footer.add(createLink("Sign up", "signup"));
        root.add(footer);

        signInButton.addActionListener(e -> {
            String gmail = gmailField.getText().trim();
            String password = new String(passwordField.getPassword());

            if (gmail.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter Gmail and Password", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Teacher t = authSystem.signIn(gmail, password);
            if (t != null) {
                JOptionPane.showMessageDialog(this,
                        "✅ Sign In succeeded! Greeting, " + t.getName(),
                        "Sign In", JOptionPane.INFORMATION_MESSAGE);

                // **THE FIX**: Pass the *existing* reservationSystem instance
                new ReservationUI(t, this.reservationSystem).setVisible(true);
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "❌ Sign In failed: Invalid Gmail or Password",
                        "Sign In Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        return root;
    }
}



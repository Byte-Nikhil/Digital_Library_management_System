package LibraryGUI;

import database.DBConnection;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class LoginFrame extends JFrame implements ActionListener {

    // ─────────────────────────────────────────
    // COMPONENTS
    // ─────────────────────────────────────────
    private JPanel       mainPanel, leftPanel, rightPanel;
    private JTextField   emailField;
    private JPasswordField passwordField;
    private JCheckBox    showPassword, rememberMe;
    private JButton      loginButton;
    private JLabel       logoLabel, headingLabel, subHeadingLabel;
    private JLabel       emailLabel, passwordLabel, footerLabel;
    private JLabel       welcomeLabel, descLabel, iconLabel;

    // Toast
    private JPanel toastPanel;
    private JLabel toastLabel;
    private Timer  toastTimer;

    // ─────────────────────────────────────────
    // THEME  (mirrors AdminDashboard)
    // ─────────────────────────────────────────
    private static final Color PRIMARY       = new Color(24,  95, 220);
    private static final Color PRIMARY_DARK  = new Color(16,  70, 170);
    private static final Color PRIMARY_LIGHT = new Color(230, 241, 251);
    private static final Color BG_PAGE       = new Color(235, 239, 248);
    private static final Color BG_CARD       = Color.WHITE;
    private static final Color BG_RIGHT      = new Color(24,  95, 220);
    private static final Color BORDER_CLR    = new Color(218, 218, 225);
    private static final Color TEXT_MAIN     = new Color(25,  25,  35);
    private static final Color TEXT_SEC      = new Color(100, 110, 130);
    private static final Color TEXT_MUTED    = new Color(160, 165, 180);

    private static final Font F_LOGO    = new Font("Segoe UI", Font.BOLD,  20);
    private static final Font F_HEADING = new Font("Segoe UI", Font.BOLD,  24);
    private static final Font F_SUB     = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font F_LABEL   = new Font("Segoe UI", Font.BOLD,  13);
    private static final Font F_INPUT   = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font F_BUTTON  = new Font("Segoe UI", Font.BOLD,  14);
    private static final Font F_SMALL   = new Font("Segoe UI", Font.PLAIN, 12);

    // Placeholder strings
    private static final String PH_EMAIL = "Enter your email address";
    private static final String PH_PASS  = "Enter your password";

    // =========================================
    // CONSTRUCTOR
    // =========================================
    public LoginFrame() {

        setTitle("LibraryMS — Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(820, 520));

        // Responsive sizing: 70% of screen width, fixed card feel
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int w = Math.min((int)(screen.width  * 0.72), 1100);
        int h = Math.min((int)(screen.height * 0.78), 680);
        w = Math.max(w, 820);
        h = Math.max(h, 520);
        setSize(w, h);
        setLocationRelativeTo(null);

        // Full-window background
        getContentPane().setLayout(new GridBagLayout());
        getContentPane().setBackground(BG_PAGE);

        // ── Card wrapper ─────────────────────
        mainPanel = new JPanel(new GridLayout(1, 2, 0, 0));
        mainPanel.setBackground(BG_CARD);
        mainPanel.setPreferredSize(new Dimension(820, 500));
        mainPanel.setBorder(new CompoundBorder(
            new LineBorder(BORDER_CLR, 1, true),
            BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));

        // Drop shadow simulation via wrapper
        JPanel shadowWrap = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                // Soft shadow
                for (int i = 4; i > 0; i--) {
                    int alpha = 18 * (5 - i);
                    g2.setColor(new Color(0, 0, 0, alpha));
                    g2.fillRoundRect(i, i, getWidth() - i, getHeight() - i, 14, 14);
                }
                super.paintComponent(g);
            }
        };
        shadowWrap.setOpaque(false);

        leftPanel  = new JPanel(new GridBagLayout());
        leftPanel.setBackground(BG_CARD);
        leftPanel.setBorder(new EmptyBorder(36, 44, 36, 44));

        rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(BG_RIGHT);

        setupLeftPanel();
        setupRightPanel();

        mainPanel.add(leftPanel);
        mainPanel.add(rightPanel);

        shadowWrap.add(mainPanel);
        add(shadowWrap);

        setVisible(true);
    }

    // =========================================
    // LEFT PANEL — LOGIN FORM
    // =========================================
    private void setupLeftPanel() {

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill    = GridBagConstraints.HORIZONTAL;
        gbc.gridx   = 0;
        gbc.weightx = 1.0;

        // ── Logo ─────────────────────────────
        logoLabel = new JLabel("📘  LibraryMS");
        logoLabel.setFont(F_LOGO);
        logoLabel.setForeground(PRIMARY);
        gbc.gridy  = 0;
        gbc.insets = new Insets(0, 0, 22, 0);
        leftPanel.add(logoLabel, gbc);

        // ── Heading ──────────────────────────
        headingLabel = new JLabel("Log in to your account");
        headingLabel.setFont(F_HEADING);
        headingLabel.setForeground(TEXT_MAIN);
        gbc.gridy  = 1;
        gbc.insets = new Insets(0, 0, 4, 0);
        leftPanel.add(headingLabel, gbc);

        // ── Sub-heading ───────────────────────
        subHeadingLabel = new JLabel("Welcome back! Please enter your credentials.");
        subHeadingLabel.setFont(F_SUB);
        subHeadingLabel.setForeground(TEXT_SEC);
        gbc.gridy  = 2;
        gbc.insets = new Insets(0, 0, 20, 0);
        leftPanel.add(subHeadingLabel, gbc);

        // ── Toast ─────────────────────────────
        toastPanel = buildToastPanel();
        gbc.gridy  = 3;
        gbc.insets = new Insets(0, 0, 6, 0);
        leftPanel.add(toastPanel, gbc);

        // ── Email label ───────────────────────
        emailLabel = new JLabel("Email");
        emailLabel.setFont(F_LABEL);
        emailLabel.setForeground(new Color(60, 65, 80));
        gbc.gridy  = 4;
        gbc.insets = new Insets(0, 0, 5, 0);
        leftPanel.add(emailLabel, gbc);

        // ── Email field ───────────────────────
        emailField = new JTextField(PH_EMAIL);
        emailField.setFont(F_INPUT);
        emailField.setForeground(TEXT_MUTED);
        emailField.setPreferredSize(new Dimension(0, 40));
        emailField.setBorder(new CompoundBorder(
            new LineBorder(BORDER_CLR, 1, true),
            new EmptyBorder(0, 12, 0, 12)
        ));
        applyPlaceholderFocus(emailField, PH_EMAIL);
        gbc.gridy  = 5;
        gbc.insets = new Insets(0, 0, 14, 0);
        leftPanel.add(emailField, gbc);

        // ── Password label ────────────────────
        passwordLabel = new JLabel("Password");
        passwordLabel.setFont(F_LABEL);
        passwordLabel.setForeground(new Color(60, 65, 80));
        gbc.gridy  = 6;
        gbc.insets = new Insets(0, 0, 5, 0);
        leftPanel.add(passwordLabel, gbc);

        // ── Password field ────────────────────
        passwordField = new JPasswordField(PH_PASS);
        passwordField.setFont(F_INPUT);
        passwordField.setForeground(TEXT_MUTED);
        passwordField.setEchoChar((char) 0);          // show placeholder text
        passwordField.setPreferredSize(new Dimension(0, 40));
        passwordField.setBorder(new CompoundBorder(
            new LineBorder(BORDER_CLR, 1, true),
            new EmptyBorder(0, 12, 0, 12)
        ));
        applyPasswordPlaceholderFocus(passwordField, PH_PASS);
        gbc.gridy  = 7;
        gbc.insets = new Insets(0, 0, 8, 0);
        leftPanel.add(passwordField, gbc);

        // ── Checkboxes row ────────────────────
        JPanel optRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        optRow.setBackground(BG_CARD);

        showPassword = styledCheckbox("Show password");
        rememberMe   = styledCheckbox("Remember me");
        showPassword.addActionListener(this);

        optRow.add(showPassword);
        optRow.add(Box.createRigidArea(new Dimension(16, 0)));
        optRow.add(rememberMe);

        gbc.gridy  = 8;
        gbc.insets = new Insets(0, 0, 20, 0);
        leftPanel.add(optRow, gbc);

        // ── Login button ──────────────────────
        loginButton = new JButton("Log In");
        loginButton.setFont(F_BUTTON);
        loginButton.setBackground(PRIMARY);
        loginButton.setForeground(Color.WHITE);
        loginButton.setOpaque(true);
        loginButton.setPreferredSize(new Dimension(0, 42));
        loginButton.setFocusPainted(false);
        loginButton.setBorder(new CompoundBorder(
            new LineBorder(PRIMARY, 1, true),
            new EmptyBorder(0, 0, 0, 0)
        ));
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { loginButton.setBackground(PRIMARY_DARK); }
            @Override public void mouseExited (MouseEvent e) { loginButton.setBackground(PRIMARY); }
        });
        loginButton.addActionListener(this);

        gbc.gridy  = 9;
        gbc.insets = new Insets(0, 0, 14, 0);
        leftPanel.add(loginButton, gbc);

        // ── Footer label ──────────────────────
        footerLabel = new JLabel("Digital Library Management System",
                                  SwingConstants.CENTER);
        footerLabel.setFont(F_SMALL);
        footerLabel.setForeground(TEXT_MUTED);
        gbc.gridy  = 10;
        gbc.insets = new Insets(0, 0, 0, 0);
        leftPanel.add(footerLabel, gbc);
    }

    // =========================================
    // RIGHT PANEL — BRANDING
    // =========================================
    private void setupRightPanel() {

        rightPanel.setLayout(new GridBagLayout());

        JPanel inner = new JPanel();
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
        inner.setOpaque(false);

        // Icon
        iconLabel = new JLabel("📚");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 80));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Decorative badge around icon
        JPanel iconBadge = new JPanel(new GridBagLayout());
        iconBadge.setOpaque(true);
        iconBadge.setBackground(new Color(255, 255, 255, 28));
        iconBadge.setPreferredSize(new Dimension(130, 130));
        iconBadge.setMaximumSize (new Dimension(130, 130));
        iconBadge.setBorder(new LineBorder(new Color(255,255,255,50), 2, true));
        iconBadge.setAlignmentX(Component.CENTER_ALIGNMENT);
        iconBadge.add(iconLabel);

        // Heading
        welcomeLabel = new JLabel("Connect with your Library");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Description
        descLabel = new JLabel(
            "<html><center>Manage books, members,<br>issue records and library<br>activities with ease.</center></html>");
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        descLabel.setForeground(new Color(200, 220, 255));
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Feature chips
        JPanel chips = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        chips.setOpaque(false);
        chips.setAlignmentX(Component.CENTER_ALIGNMENT);
        chips.add(rightChip("📘 Books"));
        chips.add(rightChip("👥 Members"));
        chips.add(rightChip("🔖 Issues"));

        inner.add(iconBadge);
        inner.add(Box.createVerticalStrut(24));
        inner.add(welcomeLabel);
        inner.add(Box.createVerticalStrut(12));
        inner.add(descLabel);
        inner.add(Box.createVerticalStrut(20));
        inner.add(chips);

        rightPanel.add(inner);
    }

    private JLabel rightChip(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lbl.setForeground(Color.WHITE);
        lbl.setOpaque(true);
        lbl.setBackground(new Color(255, 255, 255, 35));
        lbl.setBorder(new CompoundBorder(
            new LineBorder(new Color(255,255,255,70), 1, true),
            new EmptyBorder(5, 12, 5, 12)
        ));
        return lbl;
    }

    // =========================================
    // TOAST
    // =========================================
    private JPanel buildToastPanel() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        p.setBackground(new Color(252, 235, 235));
        p.setBorder(new CompoundBorder(
            new LineBorder(new Color(240, 149, 149), 1, true),
            new EmptyBorder(0, 4, 0, 4)
        ));
        p.setPreferredSize(new Dimension(0, 36));
        p.setVisible(false);

        toastLabel = new JLabel();
        toastLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        toastLabel.setForeground(new Color(163, 45, 45));
        p.add(toastLabel);
        return p;
    }

    private void showToast(String msg, boolean err) {
        Color bg  = err ? new Color(252, 235, 235) : new Color(234, 243, 222);
        Color brd = err ? new Color(240, 149, 149)  : new Color(150, 196, 89);
        Color fg  = err ? new Color(163, 45, 45)    : new Color(59, 109, 17);

        toastPanel.setBackground(bg);
        toastPanel.setBorder(new CompoundBorder(
            new LineBorder(brd, 1, true),
            new EmptyBorder(0, 4, 0, 4)
        ));
        toastLabel.setForeground(fg);
        toastLabel.setText(msg);
        toastPanel.setVisible(true);

        if (toastTimer != null && toastTimer.isRunning()) toastTimer.stop();
        toastTimer = new Timer(3500, e -> toastPanel.setVisible(false));
        toastTimer.setRepeats(false);
        toastTimer.start();
        revalidate();
        repaint();
    }

    // =========================================
    // PLACEHOLDER FOCUS HELPERS
    // =========================================
    private void applyPlaceholderFocus(JTextField field, String ph) {
        field.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                if (field.getText().equals(ph)) {
                    field.setText("");
                    field.setForeground(TEXT_MAIN);
                }
                field.setBorder(new CompoundBorder(
                    new LineBorder(PRIMARY, 2, true),
                    new EmptyBorder(0, 11, 0, 11)
                ));
            }
            @Override public void focusLost(FocusEvent e) {
                if (field.getText().isBlank()) {
                    field.setText(ph);
                    field.setForeground(TEXT_MUTED);
                }
                field.setBorder(new CompoundBorder(
                    new LineBorder(BORDER_CLR, 1, true),
                    new EmptyBorder(0, 12, 0, 12)
                ));
            }
        });
    }

    private void applyPasswordPlaceholderFocus(JPasswordField field, String ph) {
        field.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                String current = new String(field.getPassword());
                if (current.equals(ph)) {
                    field.setText("");
                    field.setForeground(TEXT_MAIN);
                    // Restore echo char if Show Password is unchecked
                    if (!showPassword.isSelected()) {
                        field.setEchoChar('●');
                    }
                }
                field.setBorder(new CompoundBorder(
                    new LineBorder(PRIMARY, 2, true),
                    new EmptyBorder(0, 11, 0, 11)
                ));
            }
            @Override public void focusLost(FocusEvent e) {
                if (new String(field.getPassword()).isBlank()) {
                    field.setEchoChar((char) 0);
                    field.setText(ph);
                    field.setForeground(TEXT_MUTED);
                }
                field.setBorder(new CompoundBorder(
                    new LineBorder(BORDER_CLR, 1, true),
                    new EmptyBorder(0, 12, 0, 12)
                ));
            }
        });
    }

    // =========================================
    // STYLED CHECKBOX
    // =========================================
    private JCheckBox styledCheckbox(String text) {
        JCheckBox cb = new JCheckBox(text);
        cb.setFont(F_SMALL);
        cb.setForeground(TEXT_SEC);
        cb.setBackground(BG_CARD);
        cb.setFocusPainted(false);
        cb.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return cb;
    }

    // =========================================
    // ACTION LISTENER
    // =========================================
    @Override
    public void actionPerformed(ActionEvent e) {

        // ── Show/hide password ────────────────
        if (e.getSource() == showPassword) {
            String current = new String(passwordField.getPassword());
            // Only toggle echo if the field has real content (not placeholder)
            if (!current.equals(PH_PASS)) {
                passwordField.setEchoChar(
                    showPassword.isSelected() ? (char) 0 : '●'
                );
            }
        }

        // ── Login ─────────────────────────────
        if (e.getSource() == loginButton) {
            handleLogin();
        }
    }

    // =========================================
    // LOGIN LOGIC
    // =========================================
    private void handleLogin() {

        String email    = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        // Treat placeholder values as empty
        if (email.equals(PH_EMAIL))   email    = "";
        if (password.equals(PH_PASS)) password = "";

        if (email.isEmpty() || password.isEmpty()) {
            showToast("Please enter your email and password", true);
            return;
        }

        try {
            Connection con = DBConnection.getConnection();

            String query = "SELECT * FROM users WHERE email = ? AND password = ?";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, email);
            pst.setString(2, password);

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                String role   = rs.getString("role");
                int    userId = rs.getInt("user_id");

                showToast("Login successful! Redirecting…", false);

                // Short delay before opening dashboard
                Timer delay = new Timer(800, ev -> {
                    dispose();
                    if ("ADMIN".equalsIgnoreCase(role)) {
                        new AdminDashboard();
                    } else {
                        new StudentDashboard(userId);
                    }
                });
                delay.setRepeats(false);
                delay.start();

            } else {
                showToast("Invalid email or password — please try again", true);
                passwordField.setText("");
                passwordField.setForeground(TEXT_MAIN);
                if (!showPassword.isSelected()) passwordField.setEchoChar('●');
                passwordField.requestFocus();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            showToast("Database connection error — check your connection", true);
        }
    }
}
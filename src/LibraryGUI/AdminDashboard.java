package LibraryGUI;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class AdminDashboard extends JFrame implements ActionListener {

    // ─────────────────────────────────────────
    // PANELS
    // ─────────────────────────────────────────
    JPanel sidebarPanel;
    JPanel topPanel;
    JPanel contentPanel;

    // ─────────────────────────────────────────
    // LABELS
    // ─────────────────────────────────────────
    JLabel titleLabel;
    JLabel welcomeLabel;
    JLabel subTitleLabel;

    // ─────────────────────────────────────────
    // BUTTONS
    // ─────────────────────────────────────────
    JButton addBookButton;
    JButton viewBooksButton;
    JButton issueBookButton;
    JButton logoutButton;
    JButton returnBookButton;

    // Currently active nav button
    private JButton activeButton = null;

    // ─────────────────────────────────────────
    // COLORS
    // ─────────────────────────────────────────
    Color primaryColor = new Color(24, 95, 220);
    Color hoverColor = new Color(16, 70, 170);
    Color activeColor = new Color(10, 50, 130);
    Color backgroundColor = new Color(245, 247, 252);
    Color sidebarTextColor = Color.WHITE;
    Color sidebarSubColor = new Color(180, 200, 245);

    // Sidebar width (fixed)
    private static final int SIDEBAR_W = 230;

    // =========================================
    // CONSTRUCTOR
    // =========================================
    public AdminDashboard() {

        // ── Frame setup ──────────────────────
        setTitle("Admin Dashboard - LibraryMS");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Responsive: 88% of screen, min 960×580
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int w = Math.max((int) (screen.width * 0.88), 960);
        int h = Math.max((int) (screen.height * 0.90), 580);
        setSize(w, h);
        setMinimumSize(new Dimension(860, 540));
        setLocationRelativeTo(null);

        // Root layout: sidebar LEFT, everything else CENTER
        setLayout(new BorderLayout());

        // ── Sidebar ──────────────────────────
        add(buildSidebar(), BorderLayout.WEST);

        // ── Right side: top bar + content ────
        JPanel rightSide = new JPanel(new BorderLayout());
        rightSide.setBackground(backgroundColor);
        rightSide.add(buildTopPanel(), BorderLayout.NORTH);
        rightSide.add(buildContentArea(), BorderLayout.CENTER);
        add(rightSide, BorderLayout.CENTER);

        // Show default welcome page
        showWelcomePage();

        setVisible(true);
    }

    // =========================================
    // SIDEBAR
    // =========================================
    private JPanel buildSidebar() {

        sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(primaryColor);
        sidebarPanel.setPreferredSize(new Dimension(SIDEBAR_W, 0));
        sidebarPanel.setBorder(BorderFactory.createEmptyBorder(24, 0, 24, 0));

        // ── Brand ────────────────────────────
        JPanel brandPanel = new JPanel();
        brandPanel.setLayout(new BoxLayout(brandPanel, BoxLayout.Y_AXIS));
        brandPanel.setBackground(primaryColor);
        brandPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        brandPanel.setBorder(BorderFactory.createEmptyBorder(0, 18, 0, 18));
        brandPanel.setMaximumSize(new Dimension(SIDEBAR_W, 70));

        titleLabel = new JLabel("📚  LibraryMS");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(sidebarTextColor);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel appLabel = new JLabel("Digital Library Management");
        appLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        appLabel.setForeground(sidebarSubColor);
        appLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        brandPanel.add(titleLabel);
        brandPanel.add(Box.createVerticalStrut(4));
        brandPanel.add(appLabel);

        sidebarPanel.add(brandPanel);
        sidebarPanel.add(Box.createVerticalStrut(32));

        // ── Nav buttons ──────────────────────
        addBookButton = createMenuButton("📘  Add Book");
        viewBooksButton = createMenuButton("📚  View Books");
        issueBookButton = createMenuButton("📖  Issue Book");
        returnBookButton = createMenuButton("📕  Return Book");

        sidebarPanel.add(addBookButton);
        sidebarPanel.add(Box.createVerticalStrut(4));
        sidebarPanel.add(viewBooksButton);
        sidebarPanel.add(Box.createVerticalStrut(4));
        sidebarPanel.add(issueBookButton);
        sidebarPanel.add(returnBookButton);

        // Logout pinned to bottom
        sidebarPanel.add(Box.createVerticalGlue());

        // Thin separator
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(255, 255, 255, 40));
        sep.setMaximumSize(new Dimension(SIDEBAR_W - 36, 1));
        sep.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebarPanel.add(sep);
        sidebarPanel.add(Box.createVerticalStrut(12));

        logoutButton = createMenuButton("🚪  Logout");
        sidebarPanel.add(logoutButton);

        return sidebarPanel;
    }

    // =========================================
    // NAV BUTTON
    // =========================================
    private JButton createMenuButton(String text) {

        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                if (getClientProperty("active") == Boolean.TRUE) {
                    g2.setColor(activeColor);
                } else if (getModel().isRollover()) {
                    g2.setColor(hoverColor);
                } else {
                    g2.setColor(primaryColor);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };

        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(sidebarTextColor);
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        button.setMaximumSize(new Dimension(SIDEBAR_W, 44));
        button.setPreferredSize(new Dimension(SIDEBAR_W, 44));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Repaint on hover
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.repaint();
            }
        });

        button.addActionListener(this);
        return button;
    }

    // Set active state on nav button
    private void setActive(JButton btn) {
        if (activeButton != null) {
            activeButton.putClientProperty("active", Boolean.FALSE);
            activeButton.repaint();
        }
        if (btn != null) {
            btn.putClientProperty("active", Boolean.TRUE);
            btn.repaint();
        }
        activeButton = btn;
    }

    // =========================================
    // TOP PANEL
    // =========================================
    private JPanel buildTopPanel() {

        topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.setPreferredSize(new Dimension(0, 90));
        topPanel.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 1, 0, new Color(218, 218, 225)),
                new EmptyBorder(0, 30, 0, 30)));

        // Text block
        JPanel textBlock = new JPanel();
        textBlock.setLayout(new BoxLayout(textBlock, BoxLayout.Y_AXIS));
        textBlock.setBackground(Color.WHITE);
        textBlock.setAlignmentY(Component.CENTER_ALIGNMENT);

        welcomeLabel = new JLabel("Welcome Admin 👋");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 21));
        welcomeLabel.setForeground(new Color(25, 25, 35));
        welcomeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        subTitleLabel = new JLabel("Manage your library resources efficiently");
        subTitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subTitleLabel.setForeground(new Color(100, 110, 130));
        subTitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        textBlock.add(Box.createVerticalGlue());
        textBlock.add(welcomeLabel);
        textBlock.add(Box.createVerticalStrut(4));
        textBlock.add(subTitleLabel);
        textBlock.add(Box.createVerticalGlue());

        topPanel.add(textBlock, BorderLayout.CENTER);
        return topPanel;
    }

    // =========================================
    // CONTENT AREA
    // =========================================
    private JPanel buildContentArea() {

        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(backgroundColor);
        return contentPanel;
    }

    // =========================================
    // OPEN PANEL HELPER
    // =========================================
    private void openPanel(JPanel panel) {

    JScrollPane scrollPane =
            new JScrollPane(panel);

    scrollPane.setBorder(null);

    scrollPane.getVerticalScrollBar()
            .setUnitIncrement(16);

    scrollPane.getViewport()
            .setBackground(backgroundColor);

    contentPanel.removeAll();

    contentPanel.add(
            scrollPane,
            BorderLayout.CENTER);

    contentPanel.revalidate();

    contentPanel.repaint();
}

    // =========================================
    // DEFAULT WELCOME PAGE
    // =========================================
    private void showWelcomePage() {

        setActive(null);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(backgroundColor);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(new CompoundBorder(
                new LineBorder(new Color(218, 218, 225), 1, true),
                new EmptyBorder(40, 60, 40, 60)));

        JLabel icon = new JLabel("📚", SwingConstants.CENTER);
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel heading = new JLabel("Library Management Dashboard");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 22));
        heading.setForeground(new Color(25, 25, 35));
        heading.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sub = new JLabel("Select an option from the sidebar to get started");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        sub.setForeground(new Color(100, 110, 130));
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Quick-action chips row
        JPanel chips = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        chips.setBackground(Color.WHITE);
        chips.setAlignmentX(Component.CENTER_ALIGNMENT);
        chips.add(chip("📘 Add Book", new Color(230, 241, 251), new Color(24, 95, 220)));
        chips.add(chip("📚 View Books", new Color(234, 243, 222), new Color(59, 109, 17)));
        chips.add(chip("📖 Issue Book", new Color(252, 235, 235), new Color(163, 45, 45)));

        card.add(icon);
        card.add(Box.createVerticalStrut(16));
        card.add(heading);
        card.add(Box.createVerticalStrut(8));
        card.add(sub);
        card.add(Box.createVerticalStrut(28));
        card.add(chips);

        panel.add(card);
        openPanel(panel);
    }

    private JLabel chip(String text, Color bg, Color fg) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(fg);
        lbl.setBackground(bg);
        lbl.setOpaque(true);
        lbl.setBorder(new CompoundBorder(
                new LineBorder(fg.brighter(), 1, true),
                new EmptyBorder(6, 14, 6, 14)));
        return lbl;
    }

    // =========================================
    // ACTION LISTENER
    // =========================================
    @Override
    public void actionPerformed(ActionEvent e) {

        // ── Add Book ─────────────────────────
        if (e.getSource() == addBookButton) {
            setActive(addBookButton);
            openPanel(new AddBookFrame());
        }

        // ── View Books ───────────────────────
        else if (e.getSource() == viewBooksButton) {
            setActive(viewBooksButton);
            openPanel(new ViewBooksFrame());
        }

        // ── Issue Book ───────────────────────
        else if (e.getSource() == issueBookButton) {
            setActive(issueBookButton);
            openPanel(new IssueBookFrame());
        } // ── Return Book ──────────────────────
        else if (e.getSource() == returnBookButton) {

            setActive(returnBookButton);

            openPanel(new ReturnBookFrame());
        }

        // ── Logout ───────────────────────────
        else if (e.getSource() == logoutButton) {
            int choice = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to logout?",
                    "Logout Confirmation",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            if (choice == JOptionPane.YES_OPTION) {
                dispose();
                new LoginFrame();
            }
        }
    }

    // =========================================
    // MAIN
    // =========================================
    public static void main(String[] args) {

        // Enable font antialiasing
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        try {
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        SwingUtilities.invokeLater(AdminDashboard::new);
    }
}
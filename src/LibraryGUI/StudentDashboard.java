package LibraryGUI;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class StudentDashboard extends JFrame {

    // ─────────────────────────────────────────
    // PANELS
    // ─────────────────────────────────────────
    private JPanel sidebarPanel;
    private JPanel topPanel;
    private JPanel centerContent;

    // ─────────────────────────────────────────
    // LABELS
    // ─────────────────────────────────────────
    private JLabel welcomeLabel;
    private JLabel subTitleLabel;

    // ─────────────────────────────────────────
    // BUTTONS
    // ─────────────────────────────────────────
    private JButton myBooksBtn;
    private JButton logoutBtn;

    // Active nav tracking
    private JButton activeButton = null;

    // CardLayout for content switching
    private CardLayout cardLayout;

    // ─────────────────────────────────────────
    // THEME  (identical to AdminDashboard)
    // ─────────────────────────────────────────
    private static final Color PRIMARY       = new Color(24,  95, 220);
    private static final Color PRIMARY_DARK  = new Color(16,  70, 170);
    private static final Color PRIMARY_DARKER= new Color(10,  50, 130);
    private static final Color BG_PAGE       = new Color(245, 247, 252);
    private static final Color BG_CARD       = Color.WHITE;
    private static final Color BORDER_CLR    = new Color(218, 218, 225);
    private static final Color TEXT_MAIN     = new Color(25,  25,  35);
    private static final Color TEXT_SEC      = new Color(100, 110, 130);
    private static final Color SIDEBAR_SUB   = new Color(180, 200, 245);

    private static final Font F_BRAND   = new Font("Segoe UI", Font.BOLD,  20);
    private static final Font F_TAGLINE = new Font("Segoe UI", Font.PLAIN, 11);
    private static final Font F_NAV     = new Font("Segoe UI", Font.BOLD,  14);
    private static final Font F_HEADING = new Font("Segoe UI", Font.BOLD,  21);
    private static final Font F_SUB     = new Font("Segoe UI", Font.PLAIN, 13);

    private static final int SIDEBAR_W = 230;

    // User
    private final int userId;

    // =========================================
    // CONSTRUCTOR
    // =========================================
    public StudentDashboard(int userId) {

        this.userId = userId;

        setTitle("Student Dashboard - LibraryMS");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Responsive sizing — same as AdminDashboard
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int w = Math.max((int)(screen.width  * 0.88), 960);
        int h = Math.max((int)(screen.height * 0.90), 580);
        setSize(w, h);
        setMinimumSize(new Dimension(860, 540));
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        add(buildSidebar(),   BorderLayout.WEST);

        JPanel rightSide = new JPanel(new BorderLayout());
        rightSide.setBackground(BG_PAGE);
        rightSide.add(buildTopPanel(),    BorderLayout.NORTH);
        rightSide.add(buildContentArea(), BorderLayout.CENTER);
        add(rightSide, BorderLayout.CENTER);

        // Show welcome page by default
        showWelcomePage();

        setVisible(true);
    }

    // =========================================
    // SIDEBAR
    // =========================================
    private JPanel buildSidebar() {

        sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(PRIMARY);
        sidebarPanel.setPreferredSize(new Dimension(SIDEBAR_W, 0));
        sidebarPanel.setBorder(BorderFactory.createEmptyBorder(24, 0, 24, 0));

        // ── Brand ────────────────────────────
        JPanel brand = new JPanel();
        brand.setLayout(new BoxLayout(brand, BoxLayout.Y_AXIS));
        brand.setBackground(PRIMARY);
        brand.setAlignmentX(Component.CENTER_ALIGNMENT);
        brand.setBorder(BorderFactory.createEmptyBorder(0, 18, 0, 18));
        brand.setMaximumSize(new Dimension(SIDEBAR_W, 70));

        JLabel logo = new JLabel("📚  LibraryMS");
        logo.setFont(F_BRAND);
        logo.setForeground(Color.WHITE);
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel tagline = new JLabel("Student Portal");
        tagline.setFont(F_TAGLINE);
        tagline.setForeground(SIDEBAR_SUB);
        tagline.setAlignmentX(Component.LEFT_ALIGNMENT);

        brand.add(logo);
        brand.add(Box.createVerticalStrut(4));
        brand.add(tagline);

        sidebarPanel.add(brand);
        sidebarPanel.add(Box.createVerticalStrut(32));

        // ── Nav buttons ──────────────────────
        myBooksBtn = createNavButton("📖  My Issued Books", "BOOKS");

        sidebarPanel.add(myBooksBtn);
        sidebarPanel.add(Box.createVerticalStrut(4));

        // Logout pinned to bottom
        sidebarPanel.add(Box.createVerticalGlue());

        // Thin separator
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(255, 255, 255, 40));
        sep.setMaximumSize(new Dimension(SIDEBAR_W - 36, 1));
        sep.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebarPanel.add(sep);
        sidebarPanel.add(Box.createVerticalStrut(12));

        logoutBtn = createNavButton("🚪  Logout", "LOGOUT");
        sidebarPanel.add(logoutBtn);

        return sidebarPanel;
    }

    // =========================================
    // NAV BUTTON  (identical painting to AdminDashboard)
    // =========================================
    private JButton createNavButton(String text, String key) {

        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                if (getClientProperty("active") == Boolean.TRUE) {
                    g2.setColor(PRIMARY_DARKER);
                } else if (getModel().isRollover()) {
                    g2.setColor(PRIMARY_DARK);
                } else {
                    g2.setColor(PRIMARY);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };

        btn.setFont(F_NAV);
        btn.setForeground(Color.WHITE);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        btn.setMaximumSize(new Dimension(SIDEBAR_W, 44));
        btn.setPreferredSize(new Dimension(SIDEBAR_W, 44));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);

        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.repaint(); }
            @Override public void mouseExited (MouseEvent e) { btn.repaint(); }
        });

        btn.addActionListener(e -> handleNav(btn, key));
        return btn;
    }

    private void handleNav(JButton btn, String key) {

        if ("LOGOUT".equals(key)) {
            int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to logout?",
                "Logout Confirmation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );
            if (choice == JOptionPane.YES_OPTION) {
                dispose();
                new LoginFrame();
            }
            return;
        }

        setActive(btn);
        cardLayout.show(centerContent, key);
        updateTopBar(key);
    }

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
        topPanel.setBackground(BG_CARD);
        topPanel.setPreferredSize(new Dimension(0, 90));
        topPanel.setBorder(new CompoundBorder(
            new MatteBorder(0, 0, 1, 0, BORDER_CLR),
            new EmptyBorder(0, 30, 0, 30)
        ));

        JPanel textBlock = new JPanel();
        textBlock.setLayout(new BoxLayout(textBlock, BoxLayout.Y_AXIS));
        textBlock.setBackground(BG_CARD);

        welcomeLabel = new JLabel("Welcome Student \uD83D\uDC4B");
        welcomeLabel.setFont(F_HEADING);
        welcomeLabel.setForeground(TEXT_MAIN);
        welcomeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        subTitleLabel = new JLabel("Access and manage your borrowed books");
        subTitleLabel.setFont(F_SUB);
        subTitleLabel.setForeground(TEXT_SEC);
        subTitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        textBlock.add(Box.createVerticalGlue());
        textBlock.add(welcomeLabel);
        textBlock.add(Box.createVerticalStrut(4));
        textBlock.add(subTitleLabel);
        textBlock.add(Box.createVerticalGlue());

        topPanel.add(textBlock, BorderLayout.CENTER);
        return topPanel;
    }

    private void updateTopBar(String key) {
        switch (key) {
            case "BOOKS" -> {
                welcomeLabel.setText("My Issued Books");
                subTitleLabel.setText("All books currently issued or previously borrowed by you");
            }
            default -> {
                welcomeLabel.setText("Welcome Student \uD83D\uDC4B");
                subTitleLabel.setText("Access and manage your borrowed books");
            }
        }
    }

    // =========================================
    // CONTENT AREA  (CardLayout)
    // =========================================
    private JPanel buildContentArea() {

        cardLayout   = new CardLayout();
        centerContent = new JPanel(cardLayout);
        centerContent.setBackground(BG_PAGE);

        // HOME — welcome card
        centerContent.add(buildWelcomeCard(), "HOME");

        // BOOKS — MyBooksFrame (now a JPanel)
        centerContent.add(new MyBooksFrame(userId), "BOOKS");

        return centerContent;
    }

    // =========================================
    // WELCOME CARD
    // =========================================
    private JPanel buildWelcomeCard() {

        JPanel page = new JPanel(new GridBagLayout());
        page.setBackground(BG_PAGE);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(BG_CARD);
        card.setBorder(new CompoundBorder(
            new LineBorder(BORDER_CLR, 1, true),
            new EmptyBorder(40, 60, 40, 60)
        ));

        // Icon
        JLabel icon = new JLabel("📖", SwingConstants.CENTER);
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Heading
        JLabel heading = new JLabel("Student Library Portal");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 22));
        heading.setForeground(TEXT_MAIN);
        heading.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Sub
        JLabel sub = new JLabel("Select an option from the sidebar to get started");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        sub.setForeground(TEXT_SEC);
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Quick-action chip
        JPanel chips = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        chips.setBackground(BG_CARD);
        chips.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel chip = new JLabel("📖  My Issued Books");
        chip.setFont(new Font("Segoe UI", Font.BOLD, 12));
        chip.setForeground(PRIMARY);
        chip.setBackground(new Color(230, 241, 251));
        chip.setOpaque(true);
        chip.setBorder(new CompoundBorder(
            new LineBorder(new Color(185, 212, 244), 1, true),
            new EmptyBorder(6, 14, 6, 14)
        ));
        chips.add(chip);

        card.add(icon);
        card.add(Box.createVerticalStrut(16));
        card.add(heading);
        card.add(Box.createVerticalStrut(8));
        card.add(sub);
        card.add(Box.createVerticalStrut(28));
        card.add(chips);

        page.add(card);
        return page;
    }

    private void showWelcomePage() {
        setActive(null);
        cardLayout.show(centerContent, "HOME");
    }

    // =========================================
    // MAIN
    // =========================================
    public static void main(String[] args) {

        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> new StudentDashboard(1));
    }
}
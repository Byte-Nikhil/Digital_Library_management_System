package LibraryGUI;

import database.DBConnection;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalDate;

public class IssueBookFrame extends JPanel {

    // ─────────────────────────────────────────
    // COMPONENTS
    // ─────────────────────────────────────────
    JTextField userIdField;
    JTextField bookIdField;
    JTextField dueDateField;

    JButton issueButton;
    JButton resetButton;

    // Info labels shown after ID lookup
    private JLabel bookInfoLabel;
    private JLabel userInfoLabel;

    // Toast
    private JPanel toastPanel;
    private JLabel toastLabel;
    private Timer  toastTimer;

    // ─────────────────────────────────────────
    // THEME  (matches AdminDashboard / LoginFrame)
    // ─────────────────────────────────────────
    private static final Color PRIMARY       = new Color(24,  95, 220);
    private static final Color PRIMARY_DARK  = new Color(16,  70, 170);
    private static final Color SUCCESS       = new Color(59, 109,  17);
    private static final Color BG_PAGE       = new Color(245, 247, 252);
    private static final Color BG_CARD       = Color.WHITE;
    private static final Color BORDER_CLR    = new Color(218, 218, 225);
    private static final Color TEXT_MAIN     = new Color(25,  25,  35);
    private static final Color TEXT_SEC      = new Color(100, 110, 130);
    private static final Color TEXT_MUTED    = new Color(160, 165, 180);
    private static final Color SECTION_CLR   = new Color(130, 140, 160);

    private static final Font F_HEADING = new Font("Segoe UI", Font.BOLD,  22);
    private static final Font F_SUB     = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font F_SECTION = new Font("Segoe UI", Font.BOLD,  11);
    private static final Font F_LABEL   = new Font("Segoe UI", Font.BOLD,  13);
    private static final Font F_INPUT   = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font F_BUTTON  = new Font("Segoe UI", Font.BOLD,  13);
    private static final Font F_INFO    = new Font("Segoe UI", Font.ITALIC, 12);

    // Placeholders
    private static final String PH_USER = "Enter Student / User ID";
    private static final String PH_BOOK = "Enter Book ID";
    private static final String PH_DATE = "YYYY-MM-DD";

    // =========================================
    // CONSTRUCTOR
    // =========================================
    public IssueBookFrame() {

        setLayout(new BorderLayout());
        setBackground(BG_PAGE);

        // add(buildPageHeader(), BorderLayout.NORTH);
        add(buildScrollPane(), BorderLayout.CENTER);
    }

    // =========================================
    // PAGE HEADER
    // =========================================
//     private JPanel buildPageHeader() {

//         JPanel ph = new JPanel(new BorderLayout());
//         ph.setBackground(BG_PAGE);
//         ph.setBorder(BorderFactory.createEmptyBorder(22, 30, 4, 30));

//         JLabel h = new JLabel("Issue Book");
//         h.setFont(F_HEADING);
//         h.setForeground(TEXT_MAIN);

//         JLabel s = new JLabel("Lend a book to a registered library member");
//         s.setFont(F_SUB);
//         s.setForeground(TEXT_SEC);

//         JPanel block = new JPanel();
//         block.setLayout(new BoxLayout(block, BoxLayout.Y_AXIS));
//         block.setBackground(BG_PAGE);
//         block.add(h);
//         block.add(Box.createVerticalStrut(3));
//         block.add(s);

//         ph.add(block, BorderLayout.WEST);
//         return ph;
//     }

    // =========================================
    // SCROLL PANE
    // =========================================
    private JScrollPane buildScrollPane() {

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(BG_PAGE);

        GridBagConstraints c = new GridBagConstraints();
        c.gridx   = 0;
        c.gridy   = 0;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.anchor  = GridBagConstraints.NORTH;
        c.fill    = GridBagConstraints.HORIZONTAL;
        c.insets  = new Insets(16, 30, 24, 30);

        wrapper.add(buildCard(), c);

        JScrollPane sp = new JScrollPane(wrapper);
        sp.setBorder(null);
        sp.getVerticalScrollBar().setUnitIncrement(16);
        sp.getViewport().setBackground(BG_PAGE);
        return sp;
    }

    // =========================================
    // MAIN CARD
    // =========================================
    private JPanel buildCard() {

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(BG_CARD);
        card.setBorder(new CompoundBorder(
            new LineBorder(BORDER_CLR, 1, true),
            new EmptyBorder(24, 32, 24, 32)
        ));

        // ── Card header ──────────────────────
        card.add(buildCardHeader());
        card.add(vgap(14));
        card.add(divider());
        card.add(vgap(8));

        // ── Toast ────────────────────────────
        toastPanel = buildToastPanel();
        card.add(toastPanel);
        card.add(vgap(4));

        // ── Section 1: Identifiers ────────────
        card.add(sectionLabel("BOOK & MEMBER"));
        card.add(vgap(10));
        card.add(buildIdRow());
        card.add(vgap(8));
        card.add(buildInfoRow());
        card.add(vgap(20));
        card.add(divider());
        card.add(vgap(16));

        // ── Section 2: Due Date ───────────────
        card.add(sectionLabel("LOAN PERIOD"));
        card.add(vgap(10));
        card.add(buildDueDateRow());
        card.add(vgap(24));
        card.add(divider());
        card.add(vgap(16));

        // ── Buttons ──────────────────────────
        card.add(buildButtonRow());

        return card;
    }

    // =========================================
    // CARD HEADER
    // =========================================
    private JPanel buildCardHeader() {

        JPanel row = new JPanel(new BorderLayout(14, 0));
        row.setBackground(BG_CARD);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));

        JLabel icon = new JLabel("📖", SwingConstants.CENTER);
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        icon.setOpaque(true);
        icon.setBackground(new Color(230, 241, 251));
        icon.setPreferredSize(new Dimension(44, 44));
        icon.setBorder(new LineBorder(new Color(185, 212, 244), 1, true));

        JPanel iconWrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 4));
        iconWrap.setBackground(BG_CARD);
        iconWrap.add(icon);

        JPanel text = new JPanel();
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        text.setBackground(BG_CARD);

        JLabel title = new JLabel("Issue a book");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(TEXT_MAIN);

        JLabel sub = new JLabel("Enter the book ID and member ID to process the loan");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sub.setForeground(TEXT_SEC);

        text.add(title);
        text.add(Box.createVerticalStrut(3));
        text.add(sub);

        row.add(iconWrap, BorderLayout.WEST);
        row.add(text,     BorderLayout.CENTER);
        return row;
    }

    // =========================================
    // ID ROW  (User ID + Book ID side by side)
    // =========================================
    private JPanel buildIdRow() {

        JPanel row = new JPanel(new GridLayout(1, 2, 18, 0));
        row.setBackground(BG_CARD);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 72));

        userIdField = fieldGroup(row, "Student / User ID", true, PH_USER);
        bookIdField = fieldGroup(row, "Book ID",           true, PH_BOOK);

        // Lookup on focus-lost
        userIdField.addFocusListener(new FocusAdapter() {
            @Override public void focusLost(FocusEvent e) { lookupUser(); }
        });
        bookIdField.addFocusListener(new FocusAdapter() {
            @Override public void focusLost(FocusEvent e) { lookupBook(); }
        });

        return row;
    }

    // =========================================
    // INFO ROW  (lookup results)
    // =========================================
    private JPanel buildInfoRow() {

        JPanel row = new JPanel(new GridLayout(1, 2, 18, 0));
        row.setBackground(BG_CARD);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));

        userInfoLabel = infoLbl("User details will appear here");
        bookInfoLabel = infoLbl("Book details will appear here");

        row.add(userInfoLabel);
        row.add(bookInfoLabel);
        return row;
    }

    private JLabel infoLbl(String text) {
        JLabel l = new JLabel(text);
        l.setFont(F_INFO);
        l.setForeground(TEXT_MUTED);
        return l;
    }

    // =========================================
    // DUE DATE ROW
    // =========================================
    private JPanel buildDueDateRow() {

        JPanel row = new JPanel(new GridLayout(1, 2, 18, 0));
        row.setBackground(BG_CARD);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 72));

        dueDateField = fieldGroup(row, "Due Date", true, PH_DATE);

        // Pre-fill with today + 14 days
        dueDateField.setText(LocalDate.now().plusDays(14).toString());
        dueDateField.setForeground(TEXT_MAIN);

        // Empty right slot
        JPanel empty = new JPanel();
        empty.setBackground(BG_CARD);
        row.add(empty);

        return row;
    }

    // =========================================
    // GENERIC FIELD GROUP
    // =========================================
    private JTextField fieldGroup(JPanel parent, String labelText,
                                  boolean required, String ph) {

        JPanel group = new JPanel();
        group.setLayout(new BoxLayout(group, BoxLayout.Y_AXIS));
        group.setBackground(BG_CARD);

        // Label
        JPanel labelRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        labelRow.setBackground(BG_CARD);

        JLabel lbl = new JLabel(labelText);
        lbl.setFont(F_LABEL);
        lbl.setForeground(new Color(60, 65, 80));
        labelRow.add(lbl);

        if (required) {
            JLabel star = new JLabel(" *");
            star.setFont(new Font("Segoe UI", Font.BOLD, 11));
            star.setForeground(PRIMARY);
            labelRow.add(star);
        }

        group.add(labelRow);
        group.add(Box.createVerticalStrut(5));

        // Input
        JTextField field = new JTextField(ph);
        field.setFont(F_INPUT);
        field.setForeground(TEXT_MUTED);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        field.setPreferredSize(new Dimension(0, 36));
        field.setBorder(new CompoundBorder(
            new LineBorder(BORDER_CLR, 1, true),
            new EmptyBorder(0, 10, 0, 10)
        ));

        field.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                if (field.getText().equals(ph)) {
                    field.setText("");
                    field.setForeground(TEXT_MAIN);
                }
                field.setBorder(new CompoundBorder(
                    new LineBorder(PRIMARY, 2, true),
                    new EmptyBorder(0, 9, 0, 9)
                ));
            }
            @Override public void focusLost(FocusEvent e) {
                if (field.getText().isBlank()) {
                    field.setText(ph);
                    field.setForeground(TEXT_MUTED);
                }
                field.setBorder(new CompoundBorder(
                    new LineBorder(BORDER_CLR, 1, true),
                    new EmptyBorder(0, 10, 0, 10)
                ));
            }
        });

        group.add(field);
        parent.add(group);
        return field;
    }

    // =========================================
    // BUTTON ROW
    // =========================================
    private JPanel buildButtonRow() {

        JPanel row = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        row.setBackground(BG_CARD);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        resetButton = makeBtn("↺  Reset",     new Color(240, 241, 246), TEXT_SEC, BORDER_CLR);
        issueButton = makeBtn("✔  Issue Book", PRIMARY,                  Color.WHITE, PRIMARY);

        resetButton.addActionListener(e -> clearFields());
        issueButton.addActionListener(e -> issueBook());

        row.add(resetButton);
        row.add(issueButton);
        return row;
    }

    private JButton makeBtn(String text, Color bg, Color fg, Color border) {

        JButton btn = new JButton(text);
        btn.setFont(F_BUTTON);
        btn.setPreferredSize(new Dimension(140, 36));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setOpaque(true);
        btn.setFocusPainted(false);
        btn.setBorder(new CompoundBorder(
            new LineBorder(border, 1, true),
            new EmptyBorder(0, 14, 0, 14)
        ));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        Color hover = bg.equals(PRIMARY) ? PRIMARY_DARK : new Color(225, 227, 234);
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.setBackground(hover); }
            @Override public void mouseExited (MouseEvent e) { btn.setBackground(bg); }
        });
        return btn;
    }

    // =========================================
    // TOAST
    // =========================================
    private JPanel buildToastPanel() {

        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        p.setBackground(new Color(234, 243, 222));
        p.setBorder(new CompoundBorder(
            new LineBorder(new Color(150, 196, 89), 1, true),
            new EmptyBorder(0, 4, 0, 4)
        ));
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        p.setVisible(false);

        toastLabel = new JLabel();
        toastLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        toastLabel.setForeground(new Color(59, 109, 17));
        p.add(toastLabel);
        return p;
    }

    private void showToast(String msg, boolean err) {

        Color bg  = err ? new Color(252, 235, 235) : new Color(234, 243, 222);
        Color brd = err ? new Color(240, 149, 149)  : new Color(150, 196, 89);
        Color fg  = err ? new Color(163, 45,  45)   : new Color(59, 109, 17);

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
    // DB LOOKUPS
    // =========================================
    private void lookupUser() {

        String raw = val(userIdField, PH_USER);
        if (raw.isEmpty()) {
            resetInfo(userInfoLabel, "User details will appear here");
            return;
        }
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                 "SELECT name FROM users WHERE user_id = ?")) {

            ps.setInt(1, Integer.parseInt(raw));
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                userInfoLabel.setText("👤  " + rs.getString("name"));
                userInfoLabel.setForeground(new Color(59, 109, 17));
            } else {
                userInfoLabel.setText("User not found");
                userInfoLabel.setForeground(new Color(163, 45, 45));
            }
        } catch (NumberFormatException ex) {
            userInfoLabel.setText("ID must be a number");
            userInfoLabel.setForeground(new Color(163, 45, 45));
        } catch (SQLException ex) {
            userInfoLabel.setText("Lookup error");
            userInfoLabel.setForeground(new Color(163, 45, 45));
        }
    }

    private void lookupBook() {

        String raw = val(bookIdField, PH_BOOK);
        if (raw.isEmpty()) {
            resetInfo(bookInfoLabel, "Book details will appear here");
            return;
        }
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                 "SELECT title, available_quantity FROM books WHERE book_id = ?")) {

            ps.setInt(1, Integer.parseInt(raw));
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int avail = rs.getInt("available_quantity");
                if (avail > 0) {
                    bookInfoLabel.setText("📗  " + rs.getString("title")
                            + "   |   Available: " + avail);
                    bookInfoLabel.setForeground(new Color(59, 109, 17));
                } else {
                    bookInfoLabel.setText("⚠  " + rs.getString("title") + " — Not available");
                    bookInfoLabel.setForeground(new Color(163, 45, 45));
                }
            } else {
                bookInfoLabel.setText("Book not found");
                bookInfoLabel.setForeground(new Color(163, 45, 45));
            }
        } catch (NumberFormatException ex) {
            bookInfoLabel.setText("ID must be a number");
            bookInfoLabel.setForeground(new Color(163, 45, 45));
        } catch (SQLException ex) {
            bookInfoLabel.setText("Lookup error");
            bookInfoLabel.setForeground(new Color(163, 45, 45));
        }
    }

    private void resetInfo(JLabel label, String text) {
        label.setText(text);
        label.setForeground(TEXT_MUTED);
    }

    // =========================================
    // ISSUE BOOK LOGIC
    // =========================================
    private void issueBook() {

        String userStr = val(userIdField,  PH_USER);
        String bookStr = val(bookIdField,  PH_BOOK);
        String dueDate = val(dueDateField, PH_DATE);

        // ── Validation ───────────────────────
        if (userStr.isEmpty() || bookStr.isEmpty() || dueDate.isEmpty()) {
            showToast("Please fill all required fields marked with *", true);
            return;
        }

        int userId, bookId;
        try {
            userId = Integer.parseInt(userStr);
            bookId = Integer.parseInt(bookStr);
        } catch (NumberFormatException ex) {
            showToast("User ID and Book ID must be numeric values", true);
            return;
        }

        try (Connection con = DBConnection.getConnection()) {

            // ── Check availability ───────────
            try (PreparedStatement check = con.prepareStatement(
                    "SELECT title, available_quantity FROM books WHERE book_id = ?")) {

                check.setInt(1, bookId);
                ResultSet rs = check.executeQuery();

                if (!rs.next()) {
                    showToast("Book ID " + bookId + " not found in the system", true);
                    return;
                }

                if (rs.getInt("available_quantity") <= 0) {
                    showToast("\"" + rs.getString("title") + "\" is currently not available", true);
                    return;
                }
            }

            // ── Insert issue record ──────────
            String issueSQL = "INSERT INTO issued_books "
                    + "(user_id, book_id, issue_date, due_date, issue_status, fine_amount) "
                    + "VALUES (?, ?, CURDATE(), ?, 'ISSUED', 0)";

            try (PreparedStatement pst = con.prepareStatement(issueSQL)) {

                pst.setInt   (1, userId);
                pst.setInt   (2, bookId);
                pst.setString(3, dueDate);

                int rows = pst.executeUpdate();

                if (rows > 0) {

                    // ── Decrement available qty ──
                    try (PreparedStatement upd = con.prepareStatement(
                            "UPDATE books SET available_quantity = available_quantity - 1 "
                          + "WHERE book_id = ?")) {
                        upd.setInt(1, bookId);
                        upd.executeUpdate();
                    }

                    showToast("Book issued successfully! Due: " + dueDate, false);
                    clearFields();

                } else {
                    showToast("Issue failed — please try again", true);
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            showToast("Database error: " + ex.getMessage(), true);
        }
    }

    // =========================================
    // CLEAR FIELDS
    // =========================================
    private void clearFields() {

        ph(userIdField,  PH_USER);
        ph(bookIdField,  PH_BOOK);

        dueDateField.setText(LocalDate.now().plusDays(14).toString());
        dueDateField.setForeground(TEXT_MAIN);
        dueDateField.setBorder(new CompoundBorder(
            new LineBorder(BORDER_CLR, 1, true),
            new EmptyBorder(0, 10, 0, 10)
        ));

        resetInfo(userInfoLabel, "User details will appear here");
        resetInfo(bookInfoLabel, "Book details will appear here");

        userIdField.requestFocus();
    }

    // =========================================
    // HELPERS
    // =========================================
    private void ph(JTextField f, String p) {
        f.setText(p);
        f.setForeground(TEXT_MUTED);
        f.setBorder(new CompoundBorder(
            new LineBorder(BORDER_CLR, 1, true),
            new EmptyBorder(0, 10, 0, 10)
        ));
    }

    private String val(JTextField f, String ph) {
        String v = f.getText().trim();
        return v.equals(ph) ? "" : v;
    }

    private Component vgap(int h) { return Box.createVerticalStrut(h); }

    private JPanel divider() {
        JPanel d = new JPanel();
        d.setBackground(BORDER_CLR);
        d.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        d.setPreferredSize(new Dimension(100, 1));
        return d;
    }

    private JPanel sectionLabel(String text) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        row.setBackground(BG_CARD);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        JLabel lbl = new JLabel(text);
        lbl.setFont(F_SECTION);
        lbl.setForeground(SECTION_CLR);
        row.add(lbl);
        return row;
    }
}
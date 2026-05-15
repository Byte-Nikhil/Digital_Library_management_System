package LibraryGUI;

import database.DBConnection;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class AddBookFrame extends JPanel {

    // ─────────────────────────────────────────
    // FIELDS
    // ─────────────────────────────────────────
    private JTextField titleField, authorField, isbnField,
                       publisherField, editionField,
                       categoryField, quantityField, shelfField;

    private JButton saveButton, resetButton;
    private JPanel  toastPanel;
    private JLabel  toastLabel;
    private Timer   toastTimer;

    // Placeholder strings
    private static final String PH_TITLE    = "e.g. Clean Code";
    private static final String PH_AUTHOR   = "e.g. Robert C. Martin";
    private static final String PH_ISBN     = "978-XXXXXXXXXX";
    private static final String PH_PUB      = "e.g. Prentice Hall";
    private static final String PH_EDITION  = "e.g. 3rd Edition";
    private static final String PH_CATEGORY = "e.g. 3";
    private static final String PH_QTY      = "e.g. 5";
    private static final String PH_SHELF    = "e.g. A-12";

    // Colors
    private static final Color PRIMARY      = new Color(24, 95, 220);
    private static final Color PRIMARY_DARK = new Color(16, 70, 170);
    private static final Color BG_PAGE      = new Color(245, 247, 252);
    private static final Color BG_CARD      = Color.WHITE;
    private static final Color BORDER_CLR   = new Color(218, 218, 225);
    private static final Color TEXT_MAIN    = new Color(25,  25,  35);
    private static final Color TEXT_SEC     = new Color(100, 110, 130);
    private static final Color TEXT_MUTED   = new Color(160, 165, 180);
    private static final Color SECTION_CLR  = new Color(130, 140, 160);

    // Fonts
    // private static final Font F_HEADING = new Font("Segoe UI", Font.BOLD,  22);
    // private static final Font F_SUB     = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font F_SECTION = new Font("Segoe UI", Font.BOLD,  11);
    private static final Font F_LABEL   = new Font("Segoe UI", Font.BOLD,  13);
    private static final Font F_INPUT   = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font F_BUTTON  = new Font("Segoe UI", Font.BOLD,  13);

    // =========================================
    public AddBookFrame() {
        setLayout(new BorderLayout());
        setBackground(BG_PAGE);
        // add(buildPageHeader(), BorderLayout.NORTH);
        add(buildScrollPane(), BorderLayout.CENTER);
    }

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
    // CARD
    // =========================================
    private JPanel buildCard() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(BG_CARD);
        card.setBorder(new CompoundBorder(
            new LineBorder(BORDER_CLR, 1, true),
            new EmptyBorder(24, 32, 24, 32)
        ));

        card.add(buildCardHeader());
        card.add(vgap(14));
        card.add(divider());
        card.add(vgap(8));

        // Toast notification
        toastPanel = buildToastPanel();
        card.add(toastPanel);
        card.add(vgap(2));

        // Section 1
        card.add(sectionLabel("BOOK DETAILS"));
        card.add(vgap(10));
        card.add(twoColRow("Book Title",  true,  PH_TITLE,
                           "Author Name", true,  PH_AUTHOR,
                           "title",       "author"));
        card.add(vgap(10));
        card.add(twoColRow("ISBN Number", true,  PH_ISBN,
                           "Publisher",   false, PH_PUB,
                           "isbn",        "publisher"));
        card.add(vgap(20));
        card.add(divider());
        card.add(vgap(16));

        // Section 2
        card.add(sectionLabel("CATALOG & INVENTORY"));
        card.add(vgap(10));
        card.add(twoColRow("Category ID",  true,  PH_CATEGORY,
                           "Edition",      false, PH_EDITION,
                           "category",     "edition"));
        card.add(vgap(10));
        card.add(twoColRow("Quantity",     true,  PH_QTY,
                           "Shelf Number", false, PH_SHELF,
                           "quantity",     "shelf"));
        card.add(vgap(24));
        card.add(divider());
        card.add(vgap(16));

        // Buttons
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

        JLabel icon = new JLabel("\uD83D\uDCDA", SwingConstants.CENTER);
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

        JLabel title = new JLabel("Add new book");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(TEXT_MAIN);

        JLabel sub = new JLabel("Fill in the details below to register a book in the library system");
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
    // SECTION LABEL
    // =========================================
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

    // =========================================
    // TWO-COLUMN ROW
    // =========================================
    private JPanel twoColRow(String l1, boolean r1, String ph1,
                             String l2, boolean r2, String ph2,
                             String k1, String k2) {
        JPanel row = new JPanel(new GridLayout(1, 2, 18, 0));
        row.setBackground(BG_CARD);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 72));

        JTextField f1 = fieldGroup(row, l1, r1, ph1);
        JTextField f2 = fieldGroup(row, l2, r2, ph2);
        assign(k1, f1);
        assign(k2, f2);
        return row;
    }

    private void assign(String k, JTextField f) {
        switch (k) {
            case "title"     -> titleField     = f;
            case "author"    -> authorField    = f;
            case "isbn"      -> isbnField      = f;
            case "publisher" -> publisherField = f;
            case "edition"   -> editionField   = f;
            case "category"  -> categoryField  = f;
            case "quantity"  -> quantityField  = f;
            case "shelf"     -> shelfField     = f;
        }
    }

    // =========================================
    // FIELD GROUP
    // =========================================
    private JTextField fieldGroup(JPanel parent, String labelText,
                                  boolean req, String ph) {
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

        if (req) {
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

        resetButton = makeBtn("Reset",     new Color(240, 241, 246), TEXT_SEC, BORDER_CLR);
        saveButton  = makeBtn("Save Book", PRIMARY,                  Color.WHITE, PRIMARY);

        resetButton.addActionListener(e -> clearFields());
        saveButton .addActionListener(e -> addBook());

        row.add(resetButton);
        row.add(saveButton);
        return row;
    }

    private JButton makeBtn(String text, Color bg, Color fg, Color border) {
        JButton btn = new JButton(text);
        btn.setFont(F_BUTTON);
        btn.setPreferredSize(new Dimension(130, 36));
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
    // HELPERS
    // =========================================
    private Component vgap(int h) { return Box.createVerticalStrut(h); }

    private JPanel divider() {
        JPanel d = new JPanel();
        d.setBackground(BORDER_CLR);
        d.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        d.setPreferredSize(new Dimension(100, 1));
        return d;
    }

    private String val(JTextField f, String ph) {
        String v = f.getText().trim();
        return v.equals(ph) ? "" : v;
    }

    // =========================================
    // ADD BOOK
    // =========================================
    private void addBook() {
        String title     = val(titleField,     PH_TITLE);
        String author    = val(authorField,    PH_AUTHOR);
        String isbn      = val(isbnField,      PH_ISBN);
        String publisher = val(publisherField, PH_PUB);
        String edition   = val(editionField,   PH_EDITION);
        String catStr    = val(categoryField,  PH_CATEGORY);
        String qtyStr    = val(quantityField,  PH_QTY);
        String shelf     = val(shelfField,     PH_SHELF);

        if (title.isEmpty() || author.isEmpty() || isbn.isEmpty()
                || catStr.isEmpty() || qtyStr.isEmpty()) {
            showToast("Please fill all required fields marked with *", true);
            return;
        }

        int catId, qty;
        try {
            catId = Integer.parseInt(catStr);
            qty   = Integer.parseInt(qtyStr);
            if (qty < 1) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            showToast("Category ID and Quantity must be valid positive numbers", true);
            return;
        }

        String sql = "INSERT INTO books (title, author, isbn, publisher, edition, "
                   + "category_id, total_quantity, available_quantity, shelf_number) "
                   + "VALUES (?,?,?,?,?,?,?,?,?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, title);
            ps.setString(2, author);
            ps.setString(3, isbn);
            ps.setString(4, publisher.isEmpty() ? null : publisher);
            ps.setString(5, edition.isEmpty()   ? null : edition);
            ps.setInt   (6, catId);
            ps.setInt   (7, qty);
            ps.setInt   (8, qty);
            ps.setString(9, shelf.isEmpty()     ? null : shelf);

            if (ps.executeUpdate() > 0) {
                showToast("Book \"" + title + "\" added successfully!", false);
                clearFields();
            } else {
                showToast("Insert failed — please try again", true);
            }

        } catch (SQLException ex) {
            showToast("DB Error: " + ex.getMessage(), true);
        }
    }

    // =========================================
    // CLEAR FIELDS
    // =========================================
    private void clearFields() {
        ph(titleField,     PH_TITLE);
        ph(authorField,    PH_AUTHOR);
        ph(isbnField,      PH_ISBN);
        ph(publisherField, PH_PUB);
        ph(editionField,   PH_EDITION);
        ph(categoryField,  PH_CATEGORY);
        ph(quantityField,  PH_QTY);
        ph(shelfField,     PH_SHELF);
        titleField.requestFocus();
    }

    private void ph(JTextField f, String p) {
        f.setText(p);
        f.setForeground(TEXT_MUTED);
        f.setBorder(new CompoundBorder(
            new LineBorder(BORDER_CLR, 1, true),
            new EmptyBorder(0, 10, 0, 10)
        ));
    }
}
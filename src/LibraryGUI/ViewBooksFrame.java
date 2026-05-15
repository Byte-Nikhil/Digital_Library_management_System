package LibraryGUI;

import database.DBConnection;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class ViewBooksFrame extends JPanel {

    // ─────────────────────────────────────────
    // COMPONENTS
    // ─────────────────────────────────────────
    JTable            table;
    JScrollPane       scrollPane;
    DefaultTableModel model;
    JTextField        searchField;
    JButton           searchButton;
    JButton           refreshButton;
    JButton           deleteButton;

    // Row count label
    private JLabel rowCountLabel;

    // Toast
    private JPanel toastPanel;
    private JLabel toastLabel;
    private Timer  toastTimer;

    // ─────────────────────────────────────────
    // THEME  (matches AdminDashboard / LoginFrame)
    // ─────────────────────────────────────────
    private static final Color PRIMARY       = new Color(24,  95, 220);
    private static final Color PRIMARY_DARK  = new Color(16,  70, 170);
    private static final Color BG_PAGE       = new Color(245, 247, 252);
    private static final Color BG_CARD       = Color.WHITE;
    private static final Color BORDER_CLR    = new Color(218, 218, 225);
    private static final Color TEXT_MAIN     = new Color(25,  25,  35);
    private static final Color TEXT_SEC      = new Color(100, 110, 130);
    private static final Color TEXT_MUTED    = new Color(160, 165, 180);
    private static final Color ROW_ALT       = new Color(250, 251, 253);
    private static final Color ROW_SEL       = new Color(230, 241, 251);
    private static final Color GRID_CLR      = new Color(235, 235, 240);
    private static final Color HEADER_BG     = new Color(248, 249, 252);

    private static final Font F_HEADING = new Font("Segoe UI", Font.BOLD,  22);
    private static final Font F_SUB     = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font F_LABEL   = new Font("Segoe UI", Font.BOLD,  13);
    private static final Font F_INPUT   = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font F_BUTTON  = new Font("Segoe UI", Font.BOLD,  13);
    private static final Font F_TABLE   = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font F_HEADER  = new Font("Segoe UI", Font.BOLD,  12);

    // Placeholder
    private static final String PH_SEARCH = "Search by title, author or ISBN…";

    // Table columns
    private static final String[] COLUMNS = {
        "ID", "Title", "Author", "ISBN",
        "Publisher", "Edition", "Category",
        "Total", "Available", "Shelf"
    };

    // Column preferred widths
    private static final int[] COL_WIDTHS = {
        44, 180, 140, 120, 120, 75, 70, 55, 72, 65
    };

    // =========================================
    // CONSTRUCTOR
    // =========================================
    public ViewBooksFrame() {

        setLayout(new BorderLayout());
        setBackground(BG_PAGE);

        add(buildPageHeader(), BorderLayout.NORTH);
        add(buildContent(),    BorderLayout.CENTER);

        loadBooks("");
    }

    // =========================================
    // PAGE HEADER
    // =========================================
    private JPanel buildPageHeader() {

        JPanel ph = new JPanel(new BorderLayout());
        ph.setBackground(BG_PAGE);
        ph.setBorder(BorderFactory.createEmptyBorder(22, 30, 4, 30));

        JLabel h = new JLabel("View Books");
        h.setFont(F_HEADING);
        h.setForeground(TEXT_MAIN);

        JLabel s = new JLabel("Browse and search the complete library catalog");
        s.setFont(F_SUB);
        s.setForeground(TEXT_SEC);

        JPanel block = new JPanel();
        block.setLayout(new BoxLayout(block, BoxLayout.Y_AXIS));
        block.setBackground(BG_PAGE);
        block.add(h);
        block.add(Box.createVerticalStrut(3));
        block.add(s);

        ph.add(block, BorderLayout.WEST);
        return ph;
    }

    // =========================================
    // MAIN CONTENT
    // =========================================
    private JPanel buildContent() {

        JPanel content = new JPanel(new BorderLayout(0, 12));
        content.setBackground(BG_PAGE);
        content.setBorder(BorderFactory.createEmptyBorder(14, 30, 24, 30));

        content.add(buildToolbar(),   BorderLayout.NORTH);
        content.add(buildTableCard(), BorderLayout.CENTER);

        return content;
    }

    // =========================================
    // TOOLBAR  (search + row count + buttons)
    // =========================================
    private JPanel buildToolbar() {

        JPanel bar = new JPanel(new BorderLayout(10, 0));
        bar.setBackground(BG_PAGE);

        // ── Search field ─────────────────────
        JPanel searchWrap = new JPanel(new BorderLayout(8, 0));
        searchWrap.setBackground(BG_CARD);
        searchWrap.setBorder(new CompoundBorder(
            new LineBorder(BORDER_CLR, 1, true),
            new EmptyBorder(0, 12, 0, 12)
        ));
        searchWrap.setPreferredSize(new Dimension(320, 38));

        JLabel searchIcon = new JLabel("🔍");
        searchIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));

        searchField = new JTextField(PH_SEARCH);
        searchField.setFont(F_INPUT);
        searchField.setForeground(TEXT_MUTED);
        searchField.setBorder(null);
        searchField.setBackground(BG_CARD);

        // Placeholder behaviour
        searchField.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                if (searchField.getText().equals(PH_SEARCH)) {
                    searchField.setText("");
                    searchField.setForeground(TEXT_MAIN);
                }
            }
            @Override public void focusLost(FocusEvent e) {
                if (searchField.getText().isBlank()) {
                    searchField.setText(PH_SEARCH);
                    searchField.setForeground(TEXT_MUTED);
                }
            }
        });

        // Live search on key release
        searchField.addKeyListener(new KeyAdapter() {
            @Override public void keyReleased(KeyEvent e) {
                String kw = searchField.getText().trim();
                loadBooks(kw.equals(PH_SEARCH) ? "" : kw);
            }
        });

        searchWrap.add(searchIcon,  BorderLayout.WEST);
        searchWrap.add(searchField, BorderLayout.CENTER);

        // ── Right side: row count + buttons ──
        rowCountLabel = new JLabel("0 books");
        rowCountLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        rowCountLabel.setForeground(TEXT_MUTED);

        searchButton  = makeBtn("Search",  PRIMARY,                  Color.WHITE, PRIMARY);
        refreshButton = makeBtn("Refresh", new Color(240, 241, 246), TEXT_SEC,   BORDER_CLR);
        deleteButton = makeBtn(
    "Delete",
    new Color(220, 53, 69),
    Color.WHITE,
    new Color(220, 53, 69)
);

        searchButton .addActionListener(e -> onSearch());
        refreshButton.addActionListener(e -> onRefresh());
        deleteButton.addActionListener(e -> onDelete());

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setBackground(BG_PAGE);
        right.add(rowCountLabel);
        right.add(refreshButton);
        right.add(deleteButton);
        right.add(searchButton);

        bar.add(searchWrap, BorderLayout.WEST);
        bar.add(right,      BorderLayout.EAST);

        return bar;
    }

    // =========================================
// DELETE SELECTED BOOK
// =========================================
private void onDelete() {

    // Get selected row
    int selectedRow = table.getSelectedRow();

    // No row selected
    if (selectedRow == -1) {
        showToast("Please select a book first", true);
        return;
    }

    // Get selected book title
    String bookTitle = model.getValueAt(selectedRow, 1).toString();

    // Confirmation dialog
    int confirm = JOptionPane.showConfirmDialog(
        this,
        "Are you sure you want to delete:\n\n" + bookTitle + " ?",
        "Confirm Delete",
        JOptionPane.YES_NO_OPTION,
        JOptionPane.WARNING_MESSAGE
    );

    // Cancel delete
    if (confirm != JOptionPane.YES_OPTION) {
        return;
    }

    // Get book ID
    int bookId = (int) model.getValueAt(selectedRow, 0);

    try (
        Connection con = DBConnection.getConnection();
        PreparedStatement pst =
            con.prepareStatement("DELETE FROM books WHERE book_id = ?")
    ) {

        pst.setInt(1, bookId);

        int rows = pst.executeUpdate();

        if (rows > 0) {

            // Refresh table
            loadBooks("");

            // Success toast
            showToast("Book deleted successfully", false);

        } else {
            showToast("Delete failed", true);
        }

    } catch (SQLException ex) {
        ex.printStackTrace();
        showToast("Database error: " + ex.getMessage(), true);
    }
}

    // =========================================
    // TABLE CARD
    // =========================================
    private JPanel buildTableCard() {

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(BG_CARD);
        card.setBorder(new LineBorder(BORDER_CLR, 1, true));

        // ── Model ────────────────────────────
        model = new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
            @Override public Class<?> getColumnClass(int c) {
                return (c == 0 || c == 6 || c == 7 || c == 8) ? Integer.class : String.class;
            }
        };

        // ── Table ────────────────────────────
        table = new JTable(model);
        table.setFont(F_TABLE);
        table.setRowHeight(36);
        table.setGridColor(GRID_CLR);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setSelectionBackground(ROW_SEL);
        table.setSelectionForeground(TEXT_MAIN);
        table.setFocusable(false);
        table.setAutoCreateRowSorter(true);     // click-to-sort on every column

        // ── Column widths ────────────────────
        TableColumnModel tcm = table.getColumnModel();
        for (int i = 0; i < COL_WIDTHS.length; i++) {
            tcm.getColumn(i).setPreferredWidth(COL_WIDTHS[i]);
        }

        // ── Header ───────────────────────────
        JTableHeader header = table.getTableHeader();
        header.setFont(F_HEADER);
        header.setBackground(HEADER_BG);
        header.setForeground(TEXT_SEC);
        header.setPreferredSize(new Dimension(0, 38));
        header.setBorder(new MatteBorder(0, 0, 1, 0, BORDER_CLR));
        header.setReorderingAllowed(false);

        // ── Alternating row renderer ─────────
        table.setDefaultRenderer(Object.class,  buildRenderer(SwingConstants.LEFT));
        table.setDefaultRenderer(Integer.class, buildRenderer(SwingConstants.CENTER));

        // ── Available qty — colour-coded ─────
        table.getColumnModel().getColumn(8).setCellRenderer(
            new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable t, Object v,
                        boolean sel, boolean foc, int row, int col) {
                    super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                    setHorizontalAlignment(SwingConstants.CENTER);
                    setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                    if (!sel) {
                        setBackground(row % 2 == 0 ? BG_CARD : ROW_ALT);
                        int qty = (v instanceof Integer) ? (Integer) v : 0;
                        setForeground(qty > 0 ? new Color(59, 109, 17) : new Color(163, 45, 45));
                    } else {
                        setForeground(TEXT_MAIN);
                    }
                    return this;
                }
            }
        );

        // ── Scroll pane ──────────────────────
        scrollPane = new JScrollPane(table);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(BG_CARD);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // ── Toast inside card (top) ──────────
        toastPanel = buildToastPanel();

        card.add(toastPanel, BorderLayout.NORTH);
        card.add(scrollPane, BorderLayout.CENTER);

        return card;
    }

    // ── Alternating row renderer helper ──────
    private DefaultTableCellRenderer buildRenderer(int align) {
        return new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                setHorizontalAlignment(align);
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                if (!sel) {
                    setBackground(row % 2 == 0 ? BG_CARD : ROW_ALT);
                    setForeground(TEXT_MAIN);
                }
                return this;
            }
        };
    }

    // =========================================
    // TOAST
    // =========================================
    private JPanel buildToastPanel() {

        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 7));
        p.setBackground(new Color(252, 235, 235));
        p.setBorder(new CompoundBorder(
            new MatteBorder(0, 0, 1, 0, new Color(240, 149, 149)),
            new EmptyBorder(0, 8, 0, 8)
        ));
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
        Color fg  = err ? new Color(163, 45,  45)   : new Color(59, 109,  17);

        toastPanel.setBackground(bg);
        toastPanel.setBorder(new CompoundBorder(
            new MatteBorder(0, 0, 1, 0, brd),
            new EmptyBorder(0, 8, 0, 8)
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
    // BUTTON FACTORY
    // =========================================
    private JButton makeBtn(String text, Color bg, Color fg, Color border) {

        JButton btn = new JButton(text);
        btn.setFont(F_BUTTON);
        btn.setPreferredSize(new Dimension(100, 38));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setOpaque(true);
        btn.setFocusPainted(false);
        btn.setBorder(new CompoundBorder(
            new LineBorder(border, 1, true),
            new EmptyBorder(0, 12, 0, 12)
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
    // LOAD / SEARCH  (unified)
    // =========================================
    private void loadBooks(String keyword) {

        model.setRowCount(0);

        String sql;
        boolean hasKeyword = keyword != null && !keyword.isBlank();

        if (hasKeyword) {
            sql = "SELECT * FROM books "
                + "WHERE title LIKE ? OR author LIKE ? OR isbn LIKE ? "
                + "ORDER BY title";
        } else {
            sql = "SELECT * FROM books ORDER BY title";
        }

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            if (hasKeyword) {
                String kw = "%" + keyword + "%";
                pst.setString(1, kw);
                pst.setString(2, kw);
                pst.setString(3, kw);
            }

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt   ("book_id"),
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getString("isbn"),
                    rs.getString("publisher"),
                    rs.getString("edition"),
                    rs.getInt   ("category_id"),
                    rs.getInt   ("total_quantity"),
                    rs.getInt   ("available_quantity"),
                    rs.getString("shelf_number")
                });
            }

            int count = model.getRowCount();
            rowCountLabel.setText(count + " book" + (count == 1 ? "" : "s"));

        } catch (SQLException ex) {
            ex.printStackTrace();
            showToast("Database error: " + ex.getMessage(), true);
        }
    }

    // =========================================
    // BUTTON ACTIONS
    // =========================================
    private void onSearch() {

        String kw = searchField.getText().trim();

        if (kw.isEmpty() || kw.equals(PH_SEARCH)) {
            showToast("Please enter a search keyword first", true);
            searchField.requestFocus();
            return;
        }

        loadBooks(kw);
    }

    private void onRefresh() {

        // Reset search field to placeholder
        searchField.setText(PH_SEARCH);
        searchField.setForeground(TEXT_MUTED);
        loadBooks("");
    }
}
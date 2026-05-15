package LibraryGUI;

import database.DBConnection;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class MyBooksFrame extends JPanel {

    // ─────────────────────────────────────────
    // COMPONENTS
    // ─────────────────────────────────────────
    JTable            table;
    JScrollPane       scrollPane;
    DefaultTableModel model;

    private JLabel rowCountLabel;
    private JButton refreshButton;

    // Toast
    private JPanel toastPanel;
    private JLabel toastLabel;
    private Timer  toastTimer;

    // User
    private final int userId;

    // ─────────────────────────────────────────
    // THEME  (matches entire app)
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

    // Status badge colors
    private static final Color STATUS_ISSUED_FG  = new Color(24,  95, 220);
    private static final Color STATUS_ISSUED_BG  = new Color(230, 241, 251);
    private static final Color STATUS_RETURN_FG  = new Color(59, 109,  17);
    private static final Color STATUS_RETURN_BG  = new Color(234, 243, 222);
    private static final Color STATUS_OVERDUE_FG = new Color(163, 45,  45);
    private static final Color STATUS_OVERDUE_BG = new Color(252, 235, 235);

    private static final Font F_HEADING = new Font("Segoe UI", Font.BOLD,  22);
    private static final Font F_SUB     = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font F_BUTTON  = new Font("Segoe UI", Font.BOLD,  13);
    private static final Font F_TABLE   = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font F_HEADER  = new Font("Segoe UI", Font.BOLD,  12);

    // Table columns
    private static final String[] COLUMNS = {
        "Issue ID", "Book ID", "Title", "Author",
        "Issue Date", "Due Date", "Return Date", "Status", "Fine (₹)"
    };

    // Column widths
    private static final int[] COL_WIDTHS = {
        65, 60, 200, 140, 90, 90, 90, 90, 75
    };

    // =========================================
    // CONSTRUCTOR
    // =========================================
    public MyBooksFrame(int userId) {

        this.userId = userId;

        setLayout(new BorderLayout());
        setBackground(BG_PAGE);

        add(buildPageHeader(), BorderLayout.NORTH);
        add(buildContent(),    BorderLayout.CENTER);

        loadMyBooks();
    }

    // =========================================
    // PAGE HEADER
    // =========================================
    private JPanel buildPageHeader() {

        JPanel ph = new JPanel(new BorderLayout());
        ph.setBackground(BG_PAGE);
        ph.setBorder(BorderFactory.createEmptyBorder(22, 30, 4, 30));

        JLabel h = new JLabel("My Issued Books");
        h.setFont(F_HEADING);
        h.setForeground(TEXT_MAIN);

        JLabel s = new JLabel("All books currently issued or previously borrowed by you");
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
    // TOOLBAR
    // =========================================
    private JPanel buildToolbar() {

        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(BG_PAGE);

        // ── Summary chips ─────────────────────
        JPanel chips = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        chips.setBackground(BG_PAGE);

        chips.add(statusChip("● Issued",   STATUS_ISSUED_FG,  STATUS_ISSUED_BG));
        chips.add(statusChip("● Returned", STATUS_RETURN_FG,  STATUS_RETURN_BG));
        chips.add(statusChip("● Overdue",  STATUS_OVERDUE_FG, STATUS_OVERDUE_BG));

        // ── Right: row count + refresh ────────
        rowCountLabel = new JLabel("0 records");
        rowCountLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        rowCountLabel.setForeground(TEXT_MUTED);

        refreshButton = makeBtn("Refresh", PRIMARY, Color.WHITE, PRIMARY);
        refreshButton.addActionListener(e -> loadMyBooks());

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setBackground(BG_PAGE);
        right.add(rowCountLabel);
        right.add(refreshButton);

        bar.add(chips, BorderLayout.WEST);
        bar.add(right, BorderLayout.EAST);

        return bar;
    }

    private JLabel statusChip(String text, Color fg, Color bg) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lbl.setForeground(fg);
        lbl.setBackground(bg);
        lbl.setOpaque(true);
        lbl.setBorder(new CompoundBorder(
            new LineBorder(fg.brighter(), 1, true),
            new EmptyBorder(4, 10, 4, 10)
        ));
        return lbl;
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
                if (c == 0 || c == 1)  return Integer.class;
                if (c == 8)            return Double.class;
                return String.class;
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
        table.setAutoCreateRowSorter(true);

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

        // ── Default renderer (alternating rows)
        DefaultTableCellRenderer defaultRenderer = buildRenderer(SwingConstants.LEFT);
        DefaultTableCellRenderer centerRenderer  = buildRenderer(SwingConstants.CENTER);
        DefaultTableCellRenderer rightRenderer   = buildRenderer(SwingConstants.RIGHT);

        table.setDefaultRenderer(Object.class,  defaultRenderer);
        table.setDefaultRenderer(Integer.class, centerRenderer);
        table.setDefaultRenderer(Double.class,  rightRenderer);

        // ── Status column — coloured badge ───
        tcm.getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                setHorizontalAlignment(SwingConstants.CENTER);
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));

                String status = v == null ? "" : v.toString().toUpperCase();

                if (!sel) {
                    setBackground(row % 2 == 0 ? BG_CARD : ROW_ALT);
                    switch (status) {
                        case "ISSUED"   -> { setForeground(STATUS_ISSUED_FG);  }
                        case "RETURNED" -> { setForeground(STATUS_RETURN_FG);  }
                        case "OVERDUE"  -> { setForeground(STATUS_OVERDUE_FG); }
                        default         -> { setForeground(TEXT_SEC); }
                    }
                } else {
                    setForeground(TEXT_MAIN);
                }

                // Capitalise display
                setText(status.isEmpty() ? "" :
                    status.substring(0, 1) + status.substring(1).toLowerCase());

                return this;
            }
        });

        // ── Fine column — red when > 0 ───────
        tcm.getColumn(8).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                setHorizontalAlignment(SwingConstants.RIGHT);
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 12));

                double fine = (v instanceof Double) ? (Double) v : 0.0;
                setText(String.format("₹ %.2f", fine));

                if (!sel) {
                    setBackground(row % 2 == 0 ? BG_CARD : ROW_ALT);
                    setForeground(fine > 0 ? STATUS_OVERDUE_FG : STATUS_RETURN_FG);
                } else {
                    setForeground(TEXT_MAIN);
                }
                return this;
            }
        });

        // ── Scroll pane ──────────────────────
        scrollPane = new JScrollPane(table);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(BG_CARD);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // ── Toast (top of card) ──────────────
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
    // LOAD DATA
    // =========================================
    private void loadMyBooks() {

        model.setRowCount(0);

        String sql =
            "SELECT ib.issue_id, "
          + "       b.book_id, "
          + "       b.title, "
          + "       b.author, "
          + "       ib.issue_date, "
          + "       ib.due_date, "
          + "       ib.return_date, "
          + "       ib.issue_status, "
          + "       ib.fine_amount "
          + "FROM issued_books ib "
          + "JOIN books b ON ib.book_id = b.book_id "
          + "WHERE ib.user_id = ? "
          + "ORDER BY ib.issue_date DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(1, userId);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt   ("issue_id"),
                    rs.getInt   ("book_id"),
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getDate  ("issue_date"),
                    rs.getDate  ("due_date"),
                    rs.getDate  ("return_date"),
                    rs.getString("issue_status"),
                    rs.getDouble("fine_amount")
                });
            }

            int count = model.getRowCount();
            rowCountLabel.setText(count + " record" + (count == 1 ? "" : "s"));

        } catch (SQLException ex) {
            ex.printStackTrace();
            showToast("Error loading issued books: " + ex.getMessage(), true);
        }
    }
}
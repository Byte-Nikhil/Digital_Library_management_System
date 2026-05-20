package LibraryGUI;

import database.DBConnection;

import javax.swing.*;
import javax.swing.border.*;

import java.awt.*;
import java.awt.event.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ReturnBookFrame extends JPanel
        implements ActionListener {

    // =========================================
    // COMPONENTS
    // =========================================

    JTextField userIdField;
    JTextField bookIdField;

    JButton returnButton;
    JButton resetButton;

    // =========================================
    // COLORS
    // =========================================

    Color primary =
            new Color(24,95,220);

    Color background =
            new Color(245,247,252);

    public ReturnBookFrame() {

        setLayout(new GridBagLayout());

        setBackground(background);

        // =========================================
        // MAIN CARD
        // =========================================

        JPanel card = new JPanel();

        card.setPreferredSize(
                new Dimension(950,500));

        card.setBackground(Color.WHITE);

        card.setLayout(null);

        card.setBorder(new CompoundBorder(

                new LineBorder(
                        new Color(220,220,220),
                        1,
                        true),

                new EmptyBorder(
                        20,
                        20,
                        20,
                        20)));

        add(card);

        // =========================================
        // TITLE
        // =========================================

        JLabel title =
                new JLabel(
                        "📕 Return Book");

        title.setFont(new Font(
                "Segoe UI",
                Font.BOLD,
                30));

        title.setBounds(50,40,300,40);

        card.add(title);

        JLabel sub =
                new JLabel(
                        "Return issued books from students");

        sub.setFont(new Font(
                "Segoe UI",
                Font.PLAIN,
                16));

        sub.setForeground(Color.GRAY);

        sub.setBounds(50,80,400,30);

        card.add(sub);

        // =========================================
        // USER ID
        // =========================================

        JLabel userLabel =
                new JLabel("Student / User ID");

        userLabel.setFont(new Font(
                "Segoe UI",
                Font.BOLD,
                18));

        userLabel.setBounds(80,170,220,30);

        card.add(userLabel);

        userIdField =
                createField();

        userIdField.setBounds(
                80,
                210,
                350,
                45);

        card.add(userIdField);

        // =========================================
        // BOOK ID
        // =========================================

        JLabel bookLabel =
                new JLabel("Book ID");

        bookLabel.setFont(new Font(
                "Segoe UI",
                Font.BOLD,
                18));

        bookLabel.setBounds(500,170,220,30);

        card.add(bookLabel);

        bookIdField =
                createField();

        bookIdField.setBounds(
                500,
                210,
                350,
                45);

        card.add(bookIdField);

        // =========================================
        // BUTTONS
        // =========================================

        resetButton =
                createButton(
                        "Reset",
                        new Color(130,130,130));

        resetButton.setBounds(
                500,
                350,
                150,
                45);

        card.add(resetButton);

        returnButton =
                createButton(
                        "Return Book",
                        primary);

        returnButton.setBounds(
                700,
                350,
                150,
                45);

        card.add(returnButton);
    }

    // =========================================
    // FIELD
    // =========================================

    private JTextField createField() {

        JTextField field =
                new JTextField();

        field.setFont(new Font(
                "Segoe UI",
                Font.PLAIN,
                16));

        field.setBorder(new CompoundBorder(

                new LineBorder(
                        new Color(220,220,220),
                        1,
                        true),

                new EmptyBorder(
                        5,
                        15,
                        5,
                        15)));

        return field;
    }

    // =========================================
    // BUTTON
    // =========================================

    private JButton createButton(
            String text,
            Color color) {

        JButton btn =
                new JButton(text);

        btn.setFont(new Font(
                "Segoe UI",
                Font.BOLD,
                15));

        btn.setForeground(Color.WHITE);

        btn.setBackground(color);

        btn.setFocusPainted(false);

        btn.setBorder(null);

        btn.setCursor(
                new Cursor(
                        Cursor.HAND_CURSOR));

        btn.addActionListener(this);

        return btn;
    }

    // =========================================
    // ACTIONS
    // =========================================

    @Override
    public void actionPerformed(
            ActionEvent e) {

        if (e.getSource() == resetButton) {

            userIdField.setText("");

            bookIdField.setText("");
        }

        if (e.getSource() == returnButton) {

            returnBook();
        }
    }

    // =========================================
    // RETURN BOOK
    // =========================================

    private void returnBook() {

        try {

            int userId =
                    Integer.parseInt(
                            userIdField.getText());

            int bookId =
                    Integer.parseInt(
                            bookIdField.getText());

            Connection con =
                    DBConnection.getConnection();

            // =====================================
            // CHECK TRANSACTION
            // =====================================

            String checkQuery =
                    "SELECT * FROM issued_books " +
                    "WHERE user_id=? AND book_id=? " +
                    "AND return_date IS NULL";

            PreparedStatement checkPst =
                    con.prepareStatement(checkQuery);

            checkPst.setInt(1,userId);

            checkPst.setInt(2,bookId);

            ResultSet rs =
                    checkPst.executeQuery();

            if (!rs.next()) {

                JOptionPane.showMessageDialog(
                        this,
                        "No issued book found");

                return;
            }

            int issueId =
                    rs.getInt("issue_id");

            // =====================================
            // UPDATE RETURN DATE
            // =====================================

            String returnQuery =
                    "UPDATE issued_books " +
                    "SET return_date = CURDATE() " +
                    "WHERE issue_id=?";

            PreparedStatement returnPst =
                    con.prepareStatement(returnQuery);

            returnPst.setInt(1, issueId);

            returnPst.executeUpdate();

            // =====================================
            // INCREASE BOOK QUANTITY
            // =====================================

            String qtyQuery =
                    "UPDATE books " +
                    "SET available_quantity = " +
                    "available_quantity + 1 " +
                    "WHERE book_id=?";

            PreparedStatement qtyPst =
                    con.prepareStatement(qtyQuery);

            qtyPst.setInt(1, bookId);

            qtyPst.executeUpdate();

            JOptionPane.showMessageDialog(
                    this,
                    "✅ Book Returned Successfully");

            userIdField.setText("");

            bookIdField.setText("");

        } catch (Exception ex) {

            ex.printStackTrace();

            JOptionPane.showMessageDialog(
                    this,
                    "Return Failed");
        }
    }
}
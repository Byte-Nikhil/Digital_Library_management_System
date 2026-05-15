package main;

import javax.swing.SwingUtilities;

import LibraryGUI.AdminDashboard;
import LibraryGUI.LoginFrame;

public class Main {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            // new LoginFrame();
            new AdminDashboard();
        });
    }
}
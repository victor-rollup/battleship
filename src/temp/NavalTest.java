package Battleship;

import javax.swing.JOptionPane;

public class NavalTest {
    public static void main(String[] args) {
        boolean isRunning = true;

        while (isRunning) {
            String response = JOptionPane.showInputDialog(
                "\n\n           N   A   V   A   L  \n" +
                "           ×   B   A   T   T   L   E   ×  \n\n" +
                "                      Click Start to Play \n\n",
                "Start"
            );

            if (response != null && response.trim().equalsIgnoreCase("Start")) {
                MainMenu menu = new MainMenu();
                isRunning = menu.showMenu(true);
            } else {
                isRunning = false;
            }
        }
    }
}

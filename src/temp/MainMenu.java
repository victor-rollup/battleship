package Battleship;

import java.util.ArrayList;
import java.util.List;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

public class MainMenu {
    private final List<Integer> scores = new ArrayList<>();

    public boolean showMenu(boolean isRunning) {
        while (isRunning) {
            int option = promptOption();

            switch (option) {
                case 1:
                    startNewGame();
                    break;
                case 2:
                    showScoreboard();
                    break;
                case 3:
                    showInformation();
                    break;
                case 4:
                    showCredits();
                    break;
                case 5:
                    isRunning = false;
                    break;
                default:
                    break;
            }
        }
        return false;
    }

    private int promptOption() {
        try {
            String input = JOptionPane.showInputDialog(
                "Main Menu\n\n" +
                "1. New Game\n" +
                "2. Scoreboard\n" +
                "3. Information\n" +
                "4. Credits\n" +
                "5. Exit Game\n\n"
            );
            if (input != null) {
                return Integer.parseInt(input.trim());
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid option. Try again.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        return 0;
    }

    private void startNewGame() {
        String answer;
        do {
            NavalBattle battle = new NavalBattle();
            battle.createBattle();
            battle.startBattle(true);
            answer = JOptionPane.showInputDialog("Do you want to play again? (Yes/No)");
        } while (answer != null && (answer.equalsIgnoreCase("Yes") || answer.equalsIgnoreCase("Si")));
    }

    private void showScoreboard() {
        if (scores.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No scores available yet.", "Scoreboard", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void showInformation() {
        JOptionPane.showMessageDialog(null, "Sink all enemy ships by guessing their grid coordinates!", "Information", JOptionPane.INFORMATION_MESSAGE);
    }

    public void showCredits() {
        Icon logo = new ImageIcon(getClass().getResource("/batalla_naval/ITC.png"));
        JOptionPane.showMessageDialog(null,
            "                  Instituto Tecnológico de Celaya\n" +
            "          Ingeniería en Sistemas Computacionales\n" +
            "                Programación Orientada a Objetos\n\n" +
            " Program developed by Víctor Eduardo León Carreño.",
            "Credits", JOptionPane.OK_OPTION, logo);
    }
}

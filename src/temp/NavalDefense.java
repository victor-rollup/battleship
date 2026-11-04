package Battleship;

import javax.swing.JOptionPane;

public class NavalDefense {
    private String name;
    private int ships;
    private int score;
    private int ammo;
    private int hits;
    private int consecutiveHits;

    public NavalDefense(String name, int ships, int score, int ammo) {
        this.name = name;
        this.ships = ships;
        this.score = score;
        this.ammo = ammo;
        this.hits = 0;
        this.consecutiveHits = 0;
    }

    public String getName() {
        return name;
    }

    public void promptForName() {
        this.name = JOptionPane.showInputDialog("Enter your army's name:");
        if (this.name == null || this.name.trim().isEmpty()) {
            this.name = "Navy";
        }
    }

    public int getShips() {
        return ships;
    }

    public void setShips(int ships) {
        this.ships = ships;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getAmmo() {
        return ammo;
    }

    public void setAmmo(int ammo) {
        this.ammo = ammo;
    }

    public int getHits() {
        return hits;
    }

    public int getConsecutiveHits() {
        return consecutiveHits;
    }

    public int promptCoordinates() {
        int col = -1;
        int row = -1;

        while (col == -1) {
            String inputX = JOptionPane.showInputDialog(buildStatusMessage() + "\nEnter coordinate \"X\" (A-G):\n\n");
            if (inputX == null) continue;
            inputX = inputX.trim().toUpperCase();

            switch (inputX) {
                case "A": col = 0; break;
                case "B": col = 1; break;
                case "C": col = 2; break;
                case "D": col = 3; break;
                case "E": col = 4; break;
                case "F": col = 5; break;
                case "G": col = 6; break;
                default:
                    JOptionPane.showMessageDialog(null, "Invalid X coordinate. Try again.", "Coordinate \"X\"", JOptionPane.ERROR_MESSAGE);
            }
        }

        while (row == -1) {
            try {
                String inputY = JOptionPane.showInputDialog(buildStatusMessage() + "\nEnter coordinate \"Y\" (1-7):\n\n");
                if (inputY != null) {
                    int val = Integer.parseInt(inputY.trim());
                    if (val >= 1 && val <= 7) {
                        row = val - 1;
                    } else {
                        JOptionPane.showMessageDialog(null, "Invalid Y coordinate. Try again.", "Coordinate \"Y\"", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Invalid Y coordinate. Try again.", "Coordinate \"Y\"", JOptionPane.ERROR_MESSAGE);
            }
        }

        return row * BattleMap.GRID_DIMENSION + col;
    }

    private String buildStatusMessage() {
        return "\"" + getName() + "\"\nPoints: " + getScore() +
               "\nShips: " + getShips() + "     Ammo: " + getAmmo() +
               "\nHits: " + getHits() + "\nConsecutive Hits: " + getConsecutiveHits();
    }

    public void registerHit(String shipType) {
        JOptionPane.showMessageDialog(null, "You hit an enemy ship!");
        this.hits++;
        this.consecutiveHits++;

        if (shipType.equals(BattleMap.SINGLE_SHIP)) {
            setScore(getScore() + 50);
            awardAmmo(1);
        } else {
            setScore(getScore() + 100);
            awardAmmo(2);
        }
    }

    public void awardAmmo(int amount) {
        if (amount == 1) {
            JOptionPane.showMessageDialog(null, "You earned 1 bomb!");
        } else {
            setAmmo(getAmmo() + 1);
            JOptionPane.showMessageDialog(null, "You earned 2 bombs!");
        }
    }

    public void registerMiss() {
        this.consecutiveHits = 0;
        consumeAmmo();
    }

    public void consumeAmmo() {
        setAmmo(getAmmo() - 1);
        JOptionPane.showMessageDialog(null, "You missed the shot!", "Miss!", JOptionPane.WARNING_MESSAGE);
    }

    public void checkConsecutiveReward() {
        if (consecutiveHits > 0 && (consecutiveHits % 3) == 0) {
            setShips(getShips() + 1);
            JOptionPane.showMessageDialog(null, "3 consecutive hits! Reward: +1 Ship!", "Reward", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void checkShipLoss() {
        if (getAmmo() <= 0) {
            setShips(getShips() - 1);
            setAmmo(5);
            JOptionPane.showMessageDialog(null, "You ran out of ammo and lost a ship!");
        }
    }

    public boolean hasLost() {
        if (getShips() <= 0) {
            JOptionPane.showMessageDialog(null, "Mission failed... You lost all your ships.\nThe enemy has invaded!");
            return true;
        }
        return false;
    }

    public boolean hasWon(int remainingTargets) {
        if (remainingTargets <= 0) {
            JOptionPane.showMessageDialog(null, "Threat eliminated.\nYou won!");
            return true;
        }
        return false;
    }
}

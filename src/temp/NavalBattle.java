package Battleship;

import javax.swing.JOptionPane;

public class NavalBattle {
    private static final int TOTAL_SHIPS = 10;
    private final BattleMap map = new BattleMap();
    private final Battleship[] ships = new Battleship[TOTAL_SHIPS];
    private int remainingTargets = 0;

    public void createBattle() {
        map.createMap();
        map.createShotMap();

        for (int i = 0; i < TOTAL_SHIPS; i++) {
            int size = (Math.random() < 0.5) ? 1 : 2;
            boolean isVertical = Math.random() < 0.5;

            ships[i] = new Battleship(size, isVertical);
            map.placeShip(ships[i].getSize(), ships[i].isVertical());
            remainingTargets += ships[i].getSize();
        }

        map.displayDefaultMap();
    }

    public void startBattle(boolean isPlaying) {
        NavalDefense player = new NavalDefense("Navy", 3, 0, 4);

        player.promptForName();
        showIntroMessages(player);

        while (isPlaying) {
            int targetIndex = player.promptCoordinates();
            String tileType = map.receiveCoordinates(targetIndex);
            map.attackCoordinates();

            switch (tileType) {
                case BattleMap.SEA:
                    player.registerMiss();
                    player.checkShipLoss();
                    isPlaying = !player.hasLost();
                    break;
                case BattleMap.SHOT_MISSED:
                    JOptionPane.showMessageDialog(null, "Position already attacked.", "Try Again", JOptionPane.INFORMATION_MESSAGE);
                    break;
                default:
                    player.registerHit(tileType);
                    player.checkConsecutiveReward();
                    remainingTargets--;
                    if (player.hasWon(remainingTargets)) {
                        isPlaying = false;
                    }
                    break;
            }
            map.printShotMap();
        }

        JOptionPane.showMessageDialog(null, "\"" + player.getName() + "\"\nFinal Score: " + player.getScore());
        map.printMapType("Naval Battle \nFinal Result: \n\n\"Y\"");
    }

    private void showIntroMessages(NavalDefense player) {
        JOptionPane.showMessageDialog(null, 
            "The army \"" + player.getName() + "\" has been alerted of an invasion!\nWe must act quickly!", 
            "Emergency Alert", JOptionPane.INFORMATION_MESSAGE);
        JOptionPane.showMessageDialog(null, 
            "Our army has " + player.getShips() + " ships and " + player.getAmmo() + " bombs per ship.\n" +
            "Total ammo: " + (player.getShips() * player.getAmmo()) + " bombs. Earn more by hitting enemies.", 
            "General's Message", JOptionPane.INFORMATION_MESSAGE);
        JOptionPane.showMessageDialog(null, 
            "Guess the coordinates to strike enemy positions.\n\nGood luck!", 
            "Instructions", JOptionPane.INFORMATION_MESSAGE);
    }
}

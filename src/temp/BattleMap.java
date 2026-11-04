package Battleship;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class BattleMap {
    public static final int BOARD_SIZE = 49;
    public static final int GRID_DIMENSION = 7;

    public static final String SEA = "×";
    public static final String SINGLE_SHIP = "•";
    public static final String DOUBLE_SHIP = "Ø";
    public static final String SHOT_MISSED = "¤";

    private final List<String> realBoard = new ArrayList<>(Collections.nCopies(BOARD_SIZE, SEA));
    private final List<String> playerBoard = new ArrayList<>(Collections.nCopies(BOARD_SIZE, SEA));
    private final Random random = new Random();

    private int lastTargetedIndex;

    public void createMap() {
        Collections.fill(realBoard, SEA);
    }

    public void createShotMap() {
        Collections.fill(playerBoard, SEA);
    }

    public void displayDefaultMap() {
        printGrid(playerBoard, "Naval Battle\n\n\"Y\"");
    }

    public void placeShip(int size, boolean isVertical) {
        boolean placed = false;
        String shipIcon = (size == 1) ? SINGLE_SHIP : DOUBLE_SHIP;

        while (!placed) {
            int index = random.nextInt(BOARD_SIZE);

            if (size == 1) {
                if (realBoard.get(index).equals(SEA)) {
                    realBoard.set(index, shipIcon);
                    placed = true;
                }
            } else if (size == 2) {
                if (isVertical) {
                    int neighborIndex = (index + GRID_DIMENSION < BOARD_SIZE) ? index + GRID_DIMENSION : index - GRID_DIMENSION;
                    if (isValidPlacement(index, neighborIndex)) {
                        realBoard.set(index, shipIcon);
                        realBoard.set(neighborIndex, shipIcon);
                        placed = true;
                    }
                } else {
                    int row = index / GRID_DIMENSION;
                    int col = index % GRID_DIMENSION;
                    int neighborIndex = (col + 1 < GRID_DIMENSION) ? index + 1 : index - 1;

                    if (neighborIndex / GRID_DIMENSION == row && isValidPlacement(index, neighborIndex)) {
                        realBoard.set(index, shipIcon);
                        realBoard.set(neighborIndex, shipIcon);
                        placed = true;
                    }
                }
            }
        }
    }

    private boolean isValidPlacement(int pos1, int pos2) {
        return pos1 >= 0 && pos1 < BOARD_SIZE && pos2 >= 0 && pos2 < BOARD_SIZE 
               && realBoard.get(pos1).equals(SEA) && realBoard.get(pos2).equals(SEA);
    }

    public String receiveCoordinates(int index) {
        this.lastTargetedIndex = index;
        return realBoard.get(index);
    }

    public void attackCoordinates() {
        String currentTile = realBoard.get(lastTargetedIndex);
        if (currentTile.equals(SEA)) {
            playerBoard.set(lastTargetedIndex, SHOT_MISSED);
        } else {
            playerBoard.set(lastTargetedIndex, currentTile);
        }
        realBoard.set(lastTargetedIndex, SHOT_MISSED);
    }

    public void printMapType(String header) {
        printGrid(realBoard, header);
    }

    public void printShotMap() {
        printGrid(playerBoard, "\nNaval Battle \n\n\"Y\"");
    }

    private void printGrid(List<String> board, String header) {
        System.out.println(header);
        for (int row = 0; row < GRID_DIMENSION; row++) {
            StringBuilder rowStr = new StringBuilder(" " + (row + 1) + "  ");
            for (int col = 0; col < GRID_DIMENSION; col++) {
                rowStr.append(board.get(row * GRID_DIMENSION + col)).append("  ");
            }
            System.out.println(rowStr);
        }
        System.out.println("    A  B  C  D  E  F  G \"X\"\n");
    }
}

package Battleship;

public class Battleship {
    private int size;
    private boolean isVertical;

    public Battleship(int size, boolean isVertical) {
        setSize(size);
        this.isVertical = isVertical;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = (size <= 0) ? 1 : size;
    }

    public boolean isVertical() {
        return isVertical;
    }

    public void setVertical(boolean isVertical) {
        this.isVertical = isVertical;
    }
}

package indianajones.gamepieces;

public class GamePiece {
    public int lines;
    int columns;
    public int line;
    public int column;
    public char display;

    public GamePiece(int lines, int columns) {
        this.lines = lines;
        this.columns = columns;
        this.line = 0;
        this.column = 0;

    }

    public void move() {
    }

    protected int getLine() {
        return line;
    }

    protected int getColumn() {
        return column;
    }
}

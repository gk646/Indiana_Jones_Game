package indianajones.gamepieces;

public class Grail extends GamePiece {

    public Grail(int lines, int columns) {
        super(lines, columns);
        this.display = '\u269A';
        this.line = (int) (Math.random() * lines);
        this.column = (int) (Math.random() * columns);

    }

    public void invisible() {
        this.display = ' ';

    }

}

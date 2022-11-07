package indianajones.gamepieces;

import java.nio.charset.StandardCharsets;

public class Obstacle{
    public int line;
    public int column;
    public char display;
    public Obstacle(int line, int column) {
        this.line = line;
        this.column = column;
        this.display = '\u2BBD';
    }

}

package indianajones.gamelogic;

public class Canvas {
    char[][] canvas;
    int lines;
    int columns;

    Canvas(int lines, int columns) {
        this.lines = lines;
        this.columns = columns;
        this.canvas = new char[lines][columns];
        fill(' ');
    }

    public void fill(char toFillWith) {
        for (int i = 0; i < this.canvas.length; i++) {
            for (int b = 0; b < canvas[0].length; b++) {
                canvas[i][b] = toFillWith;
            }
        }
    }

    void paint(int line, int column, char toPaint) {
        canvas[line][column] = toPaint;
    }

    String asString() {
        String output = "";
        for (char[] anvas : canvas) {
            for (char place : anvas) {
                output = output + place;
            }
            output += "\n";
        }
        return output;
    }
}

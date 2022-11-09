package indianajones.gamepieces;

public class SnakeDown extends Snake{

    public SnakeDown(int lines, int columns, Jones jones){
        super(lines,columns,jones);

    }

    @Override
    public void moveHorizontal() {
        if(line<=25){
            line++;
        }
    }
}

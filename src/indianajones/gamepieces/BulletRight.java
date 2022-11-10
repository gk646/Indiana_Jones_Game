package indianajones.gamepieces;

public class BulletRight extends Bullet{
    public BulletRight(int startline, int startcolumn){
        super(startline,startcolumn);


    }

    @Override
    public void move() {
        column++;
    }
}


package indianajones.gamepieces;

public class BulletLeft extends Bullet {
    public BulletLeft(int startline, int startcolumn){
        super(startline,startcolumn);

    }

    @Override
    public void move() {
        column--;
    }
}



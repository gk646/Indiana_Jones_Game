package indianajones.gamepieces;

public class BulletUp extends Bullet{

    public BulletUp(int startline, int startcolumn){
        super(startline,startcolumn);


    }

    @Override
    public void move() {
        line--;
    }
}

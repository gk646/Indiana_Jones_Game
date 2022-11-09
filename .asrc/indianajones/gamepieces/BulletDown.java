package indianajones.gamepieces;

public class BulletDown extends Bullet{

    public BulletDown(int startline, int startcolumn){
        super(startline,startcolumn);


    }

    @Override
    public void move() {
        line++;
    }
}

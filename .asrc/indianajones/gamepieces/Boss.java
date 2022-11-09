package indianajones.gamepieces;

public class Boss extends GamePiece {
    public int lifes;
    public int rof;
    public Jones jones;
    public Obstacle[] obstacles;

    public Boss(int lines, int columns, int line, int column, int lifes) {
        super(lines, columns);
        this.lifes = lifes;
        this.line = 13;
        this.column = 23;
        this.display = 'B';
    }

    public void shootBulletDownUp() {
        Bullet bullet = new Bullet(line, column);
    }
    public boolean isOnBullet(Bullet bullet){
        if(bullet.line==line && bullet.column==column){
            return true;
        }
        return false;
    }
}
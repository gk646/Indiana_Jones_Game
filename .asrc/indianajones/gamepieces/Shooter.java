package indianajones.gamepieces;

public class Shooter extends GamePiece {
    public int lifes;
    public int rof;
    public Jones jones;
    public Obstacle[] obstacles;

    Shooter(int lines, int columns, int lifes, int rof) {
        super(lines, columns);
        this.lifes = lifes;
        this.rof = rof;
    }

    @Override
    public void move() {
        if (Math.random() > 0.5) {
            if ((jones.getLine() - line > 0) && !shooterCollisionDown()) {
                line++;

            }
            if ((jones.getLine() - line) < 0) {
                if (shooterCollisionUp()) {
                } else {
                    line--;

                }
            }
        } else {
            if ((jones.getColumn() - column) < 0) {
                if (shooterCollisionLeft()) {
                } else {
                    column--;

                }
            } else if ((jones.getColumn() - column) > 0) {
                if (shooterCollisionRight()) {
                } else {
                    column++;

                }
            }
        }
    }

    public void shootBullet(){

    }
    private boolean shooterCollisionUp() {
        for (Obstacle obstacle : obstacles) {
            if (obstacle != null) {
                if (line - 1 == obstacle.line && column == obstacle.column) {
                    return true;

                }
            }
        }
        return false;
    }

    private boolean shooterCollisionDown() {
        for (Obstacle obstacle : obstacles) {
            if (obstacle != null) {
                if (line + 1 == obstacle.line && column == obstacle.column) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean shooterCollisionLeft() {

        for (Obstacle obstacle : obstacles) {
            if (obstacle != null) {
                if (line == obstacle.line && column - 1 == obstacle.column) {

                    return true;

                }
            }
        }
        return false;
    }

    private boolean shooterCollisionRight() {
        for (Obstacle obstacle : obstacles) {
            if (obstacle != null) {
                if (line == obstacle.line && column + 1 == obstacle.column) {

                    return true;

                }
            }
        }
        return false;
    }

}

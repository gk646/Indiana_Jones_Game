package indianajones.gamepieces;

public class Snake extends GamePiece {
    protected Jones jones;

    public Obstacle[] obstacles;

    public Snake(int lines, int columns, Jones jones) {
        super(lines, columns);
        this.jones = jones;
        this.display = 'S';
        this.obstacles = new Obstacle[lines * columns];
        this.line = (int) (Math.random() * (lines));
        this.column = columns - 1;
    }

    @Override
    public void move() {
        if (Math.random() > 0.5) {
            if ((jones.getLine() - line > 0) && !snakeCollisionDown()) {
                line++;

            }
            if ((jones.getLine() - line) < 0) {
                if (snakeCollisionUp()) {
                    line = line;
                } else {
                    line--;
                }
            }
        } else {
            if ((jones.getColumn() - column) < 0) {
                if (snakeCollisionLeft()) {
                    column = column;
                } else {
                    column--;
                }
            } else if ((jones.getColumn() - column) > 0) {
                if (snakeCollisionRight()) {
                    column = column;
                } else {
                    column++;
                }
            }
        }
    }
    public void moveHorizontal(){
        if(column>=1){
            column--;
        }
    }
    private boolean snakeCollisionUp() {
        int bonk = 0;
        for (Obstacle obstacle : obstacles) {
            if (obstacle != null) {
                if (line - 1 == obstacle.line && column == obstacle.column) {
                    bonk++;
                    break;

                }
            }
        }
        return bonk > 0;
    }

    private boolean snakeCollisionDown() {
        int bonk = 0;
        for (Obstacle obstacle : obstacles) {
            if (obstacle != null) {
                if (line + 1 == obstacle.line && column == obstacle.column) {
                    bonk++;
                    break;

                }
            }
        }
        return bonk > 0;
    }

    private boolean snakeCollisionLeft() {
        int bonk = 0;
        for (Obstacle obstacle : obstacles) {
            if (obstacle != null) {
                if (line == obstacle.line && column - 1 == obstacle.column) {
                    bonk++;
                    break;

                }
            }
        }
        return bonk > 0;
    }

    private boolean snakeCollisionRight() {
        int bonk = 0;
        for (Obstacle obstacle : obstacles) {
            if (obstacle != null) {
                if (line == obstacle.line && column + 1 == obstacle.column) {
                    bonk++;
                    break;

                }
            }
        }
        return bonk > 0;
    }


}


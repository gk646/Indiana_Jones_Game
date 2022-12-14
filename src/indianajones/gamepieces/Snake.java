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
                display = '\u2240';

            }
            if ((jones.getLine() - line) < 0) {
                if (snakeCollisionUp()) {
                } else {
                    line--;
                    display = '\u2240';
                }
            }
        } else {
            if ((jones.getColumn() - column) < 0) {
                if (snakeCollisionLeft()) {
                } else {
                    column--;
                    display = '\u223D';
                }
            } else if ((jones.getColumn() - column) > 0) {
                if (snakeCollisionRight()) {
                } else {
                    column++;
                    display = '\u223D';
                }
            }
        }
    }

    public void movePuzzle() {
        if (Math.random() > 0.8) {
            if (Math.random() > 0.5) {
                if ((jones.getLine() - line > 0) && !snakeCollisionDown()) {
                    line++;
                    display = '\u2240';

                }
                if ((jones.getLine() - line) < 0) {
                    if (snakeCollisionUp()) {
                    } else {
                        line--;
                        display = '\u2240';
                    }
                }
            } else {
                if ((jones.getColumn() - column) < 0) {
                    if (snakeCollisionLeft()) {
                    } else {
                        column--;
                        display = '\u223D';
                    }
                } else if ((jones.getColumn() - column) > 0) {
                    if (snakeCollisionRight()) {
                    } else {
                        column++;
                        display = '\u223D';
                    }
                }
            }
        }
    }

    public boolean jonesIsAbove() {
        return jones.getLine() - line < 0;
    }

    public boolean jonesIsBelow() {
        return jones.getLine() - line > 0;
    }

    public boolean jonesIsLeft() {
        return jones.getColumn() - column < 0;
    }

    public boolean jonesIsRight() {
        return jones.getColumn() - column > 0;
    }

    public void movePacMan() {
        if (Math.random() > 0.5) {
            if (jonesIsAbove()) {
                if (!snakeCollisionUp() && line > 1) {
                    line--;
                } else if (snakeCollisionUp() && jonesIsLeft() && !snakeCollisionLeft()) {
                    column--;
                } else if (snakeCollisionUp() && jonesIsRight() && !snakeCollisionRight()) {
                    column++;
                } else if (snakeCollisionUp() && snakeCollisionLeft() && !snakeCollisionRight()) {
                    column++;
                }
            } else if (jonesIsBelow()) {
                if (!snakeCollisionDown()) {
                    line++;
                } else if (snakeCollisionUp() && jonesIsLeft() && !snakeCollisionLeft()) {
                    column--;
                } else if (snakeCollisionDown() && jonesIsRight() && !snakeCollisionRight()) {
                    column++;
                } else if (!snakeCollisionUp() && line > 1) {
                    line--;
                }
            }
        } else {
            if (jonesIsLeft()) {
                if (!snakeCollisionLeft()) {
                    column--;
                } else if (!snakeCollisionUp() && line > 1) {
                    line--;
                } else if (!snakeCollisionDown()) {
                    line++;
                } else if (!snakeCollisionRight()) {
                    column++;
                }
            } else if (jonesIsRight()) {
                if (!snakeCollisionRight()) {
                    column++;
                } else if (!snakeCollisionUp() && line > 1) {
                    line--;
                } else if (!snakeCollisionDown()) {
                    line++;
                } else if (!snakeCollisionLeft()) {
                    column--;
                }
            }
        }
    }

    public void moveHorizontal() {
        if (column >= 1) {
            column--;
        }
    }

    private boolean snakeCollisionUp() {
        for (Obstacle obstacle : obstacles) {
            if (obstacle != null) {
                if (line - 1 == obstacle.line && column == obstacle.column) {
                    return true;

                }
            }
        }
        return false;
    }

    private boolean snakeCollisionDown() {
        for (Obstacle obstacle : obstacles) {
            if (obstacle != null) {
                if (line + 1 == obstacle.line && column == obstacle.column) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean snakeCollisionLeft() {

        for (Obstacle obstacle : obstacles) {
            if (obstacle != null) {
                if (line == obstacle.line && column - 1 == obstacle.column) {

                    return true;

                }
            }
        }
        return false;
    }

    private boolean snakeCollisionRight() {
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


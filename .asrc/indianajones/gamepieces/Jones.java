package indianajones.gamepieces;

import indianajones.bin.IndianaJones;
import indianajones.gamelogic.Canvas;
import indianajones.gamelogic.GameLogic;
import indianajones.gamelogic.GameView;

import java.awt.event.KeyEvent;

public class Jones extends GamePiece {
    public boolean powerUpEnabled;
    public boolean gotGrail;
    public Canvas canvas;
    public IndianaJones indianaJones;
    private GameView gameView;
    public Obstacle[] obstacles;

    public Jones(int lines, int columns, GameView gameView,IndianaJones indianaJones, Canvas canvas) {
        super(lines, columns);
        this.obstacles = new Obstacle[lines * columns];
        this.gameView = gameView;
        this.display = '\u24BF';
        this.canvas = canvas;
        this.indianaJones = indianaJones;
        this.line = (lines / 2);
        this.powerUpEnabled = false;
        this.column = 0;
    }

    @Override
    public void move() {
        Integer[] pressedKeys = gameView.getKeyCodesOfCurrentlyPressedKeys();
        for (int keyCode : pressedKeys) {
            if (keyCode == KeyEvent.VK_W) {
                if (line - 1 < 0 || jonesAgainstObstacleUp()) {
                    line = line;
                } else {
                    line -= 1;
                }
            } else if (keyCode == KeyEvent.VK_S) {
                if (line + 1 > lines - 1 || jonesAgainstObstacleDown()) {
                    line = line;
                } else {
                    line += 1;
                }
            } else if (keyCode == KeyEvent.VK_A) {
                if (column - 1 < 0 || jonesAgainstObstacleLeft()) {
                    column = column;
                } else {
                    column -= 1;
                }
            } else if(keyCode == KeyEvent.VK_D){
                for(int keycode: pressedKeys){
                    if (keycode == KeyEvent.VK_SPACE && powerUpEnabled) {
                        column += 5;
                    }
                }
            }if (keyCode == KeyEvent.VK_D) {
                if (column + 1 > columns - 1 || jonesAgainstObstacleRight()) {
                    column = column;
                } else {
                    column += 1;
                }
            }

        }
    }

    public boolean jonesAgainstObstacleUp() {
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

    public boolean jonesAgainstObstacleDown() {
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

    public boolean jonesAgainstObstacleLeft() {
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

    public boolean jonesAgainstObstacleRight() {
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

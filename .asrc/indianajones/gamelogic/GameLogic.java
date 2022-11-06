package indianajones.gamelogic;

import indianajones.bin.IndianaJones;
import indianajones.gamepieces.*;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.nio.charset.StandardCharsets;
import java.util.Random;

public class GameLogic {
    private int lines;
    public int seed;
    public GameView gameView;
    public IndianaJones indianaJones;
    private int columns;
    public int tickspeed;
    private Canvas canvas;
    public Obstacle[] obstacles;
    public Jones jones;
    public Snake[] snakes;
    private GamePiece[] gamePieces;
    public Grail grail;
    public Exit exit;
    private int numberOfSnakes;
    public Snake snake;
    public boolean gameOver;
    public Random random;

    public GameLogic(int lines, int columns, int tickspeed, int numberOfSnakes, GameView gameView, IndianaJones indianaJones) {
        this.lines = lines;
        this.columns = columns;
        this.indianaJones = indianaJones;
        this.canvas = new Canvas(lines, columns);
        obstacles = new Obstacle[lines * columns];
        this.numberOfSnakes = numberOfSnakes;
        this.random = new Random(seed);
        this.gameView = gameView;
        this.seed = 0;
        this.snake = new Snake(lines, columns, jones);
        this.tickspeed = tickspeed;
        this.exit = new Exit(lines, columns);
        this.grail = new Grail(lines, columns);
        this.jones = new Jones(lines, columns, gameView, indianaJones, canvas);
        this.gamePieces = new GamePiece[3 + numberOfSnakes];
        this.snakes = new Snake[numberOfSnakes];
        for (int i = 0; i < this.snakes.length; i++) {
            this.snakes[i] = new Snake(lines, columns, this.jones);
        }
        for (int i = 0; i < gamePieces.length; i++) {
            if (i < snakes.length) {
                gamePieces[i] = snakes[i];
            } else {
                gamePieces[i] = this.exit;
                gamePieces[i + 1] = this.grail;
                gamePieces[i + 2] = this.jones;

                break;
            }
        }
        gameView.print(canvas.asString(), 20);
    }

    public void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException ignored) {
        }
    }

    boolean jonesIsOnTheSameSpotAs(GamePiece gamePiece) {
        if (gamePiece != null) {
            return this.jones.line == gamePiece.line && this.jones.column == gamePiece.column;
        }
        return false;
    }

    private boolean snakeGotJones() {
        int snakebites = 0;
        for (Snake snake : this.snakes) {
            if (jonesIsOnTheSameSpotAs(snake)) {
                snakebites++;
                break;
            }
        }
        return snakebites > 0;
    }

    void gameLogic() {
        if (jonesIsOnTheSameSpotAs(this.grail)) {
            grail.invisible();
            jones.gotGrail = true;
        } else if (this.jones.gotGrail && jonesIsOnTheSameSpotAs(this.exit)) {
            gameView.print(canvas.asString(), 20);
            indianaJones.jonesWon = true;
            this.gameOver = true;
        } else if (snakeGotJones()) {
            gameView.print(canvas.asString(), 20);
            indianaJones.snakeWon = true;
            this.gameOver = true;
            indianaJones.lifes--;
        }
    }

    public void printHearts() {
        String str1 = "\u2661";
        byte[] charset = str1.getBytes(StandardCharsets.UTF_8);
        char heart = new String(charset, StandardCharsets.UTF_8).charAt(0);
        for (int i = 0; i < indianaJones.lifes; i++) {
            canvas.paint(26, 0 + i, heart);
        }

    }

    public void escapeMenu() throws InterruptedException {
        Integer[] pressedKeys = gameView.getKeyCodesOfCurrentlyPressedKeys();
        for (int keyCode : pressedKeys) {
            if (keyCode == KeyEvent.VK_ESCAPE) {
                boolean stop = false;
                while (!stop) {
                    //todo maybe flashing yes and no my / switch a and d confirm with enter
                    gameView.addTextToCanvas("\n".repeat(2) + " ".repeat(14) + "Do you wanna quit?", 0, 0, 20, Color.green, 0);
                    gameView.printCanvas();
                    Thread.sleep(125);
                    Integer[] pressedKeys1 = gameView.getKeyCodesOfCurrentlyPressedKeys();
                    for (int keypress : pressedKeys1) {
                        if (keypress == KeyEvent.VK_ENTER) {
                            gameOver = true;
                            indianaJones.lifes = 0;
                            indianaJones.snakeWon = true;
                            indianaJones.levelSelector = 0;
                            stop = true;

                        } else if (keypress == KeyEvent.VK_ESCAPE) {
                            stop = true;
                        }
                    }

                }
            }
        }
    }

    public void gameLoop() throws InterruptedException {
        while (!this.gameOver) {
            this.canvas.fill(' ');
            escapeMenu();
            for (GamePiece gamePiece : gamePieces) {
                gamePiece.move();
                canvas.paint(gamePiece.line, gamePiece.column, gamePiece.display);
            }
            for (Obstacle obstacle : obstacles) {
                if (obstacle != null) {
                    canvas.paint(obstacle.line, obstacle.column, obstacle.display);
                }
            }
            printHearts();
            gameView.print(canvas.asString(), 20);
            gameLogic();
            sleep(tickspeed);
        }
        sleep(500);
    }

    public void gameLoopCarChase() throws InterruptedException {
        int timegone = 0;
        int length = 75;
        this.snakes = new Snake[length];
        int arraylength = 0;
        int wave1 = 0;
        int wave2 = 0;
        int counter1 = 0;

        while (!this.gameOver) {

            escapeMenu();
            if (timegone > 450) {
                this.gameOver = true;
                indianaJones.jonesWon = true;
            }
            this.canvas.fill(' ');

            if (arraylength < length) {
                snakes[arraylength] = new Snake((int) (Math.random() * 27), 47, jones);
                arraylength++;
            }
            if (timegone > 250) {
                jones.powerUpEnabled = true;
                if (snakes[counter1] != null) {
                    if (wave1 <= 26) {
                        wave2 = wave1;
                    } else if (wave1 == 52) {
                        wave1 = 0;
                        wave2 = 0;
                    } else {
                        wave2--;
                    }
                    snakes[counter1].line = wave2;
                    snakes[counter1].column = 47;
                    wave1++;
                    if (counter1 < length - 1) {
                        counter1++;
                    } else {
                        counter1 = 0;
                    }
                }
            }
            for (Snake snake : snakes) {
                if (snake != null && snake.column > 0) {
                    snake.moveHorizontal();
                    canvas.paint(snake.line, snake.column, snake.display);
                } else if (snake != null && snake.column == 0) {
                    snake.column = 47;
                    snake.line = (int) (Math.random() * 27);
                }
            }
            for (GamePiece gamePiece : gamePieces) {
                if (gamePiece != null) {
                    gamePiece.move();
                    canvas.paint(gamePiece.line, gamePiece.column, gamePiece.display);
                }
            }
            printHearts();
            gameView.print(canvas.asString(), 20);
            gameLogic();
            sleep(120 - timegone / 10);
            timegone++;
        }
        //todo proper transition screens
        sleep(500);
    }


    public void modifyExit(Exit exit, int line, int column, char character) {
        exit.line = line;
        exit.column = column;
        exit.display = character;
    }

    public void paintLevelSelectText() {
        //Story
        canvas.paint(3, 22, 'S');
        canvas.paint(3, 23, 'T');
        canvas.paint(3, 24, 'O');
        canvas.paint(3, 25, 'R');
        canvas.paint(3, 26, 'Y');
        //Random
        canvas.paint(10, 14, 'R');
        canvas.paint(10, 15, 'A');
        canvas.paint(10, 16, 'N');
        canvas.paint(10, 17, 'D');
        canvas.paint(10, 18, 'O');
        canvas.paint(10, 19, 'M');
        //Quit
        canvas.paint(23, 22, 'Q');
        canvas.paint(23, 23, 'U');
        canvas.paint(23, 24, 'I');
        canvas.paint(23, 25, 'T');
        canvas.paint(23, 26, '!');
        // Continue
        canvas.paint(10,28,'C');
        canvas.paint(10,29,'O');
        canvas.paint(10,30,'N');
        canvas.paint(10,31,'T');
        canvas.paint(10,32,'I');
        canvas.paint(10,33,'N');
        canvas.paint(10,34,'U');
        canvas.paint(10,35,'E');


    }

    public void gameLoopLevelSelect() {
        Exit storyexit1 = new Exit(lines, columns);
        Exit randomexit2 = new Exit(lines, columns);
        Exit quitexit3 = new Exit(lines, columns);
        Exit exit4 = new Exit(lines, columns);
        modifyExit(storyexit1, 6, 24, ' ');
        modifyExit(randomexit2, 13, 17, ' ');
        modifyExit(quitexit3, 20, 24, ' ');
        modifyExit(exit4, 13, 31, ' ');

        while (!this.gameOver) {
            canvas.paint(storyexit1.line, storyexit1.column, storyexit1.display);
            canvas.paint(randomexit2.line, randomexit2.column, randomexit2.display);
            canvas.paint(quitexit3.line, quitexit3.column, quitexit3.display);
            canvas.paint(exit4.line, exit4.column, exit4.display);
            this.canvas.fill(' ');
            for (GamePiece gamePiece : gamePieces) {
                gamePiece.move();
                canvas.paint(gamePiece.line, gamePiece.column, gamePiece.display);
            }
            for (Obstacle obstacle : obstacles) {
                if (obstacle != null) {
                    canvas.paint(obstacle.line, obstacle.column, obstacle.display);
                }
            }
            paintLevelSelectText();
            gameView.print(canvas.asString(), 20);
            gameLogic();
            if (jones.line == storyexit1.line && jones.column == storyexit1.column) {
                indianaJones.levelSelector = 1;
                this.gameOver = true;
            } else if (jones.line == randomexit2.line && jones.column == randomexit2.column) {
                indianaJones.levelSelector = 2;
                this.gameOver = true;
            } else if (jones.line == quitexit3.line && jones.column == quitexit3.column) {
                indianaJones.levelSelector = 3;
                this.gameOver = true;
            } else if (jones.line == exit4.line && jones.column == exit4.column&& indianaJones.checkpoint1) {
                indianaJones.levelSelector = 4;
                this.gameOver = true;
            }
            sleep(tickspeed);

        }
        sleep(500);
    }

    public void fillArray(int[][] array) {
        int c = 0;
        for (int i = 0; i < array.length; i++) {
            for (int b = 0; b < array[0].length; b++) {
                if (array[i][b] == 1) {
                    obstacles[c++] = new Obstacle(i, b);
                }
            }
        }
    }

    public void fillArrayRandom() {
        seed = (int) (Math.random() * 4000);
        double randomNumber = random.nextDouble();
        int filler = 0;
        int[][] array = new int[27][48];
        for (int i = 0; i < 27; i++) {
            for (int b = 0; b < 48; b++) {
                array[i][b] = ((seed * 1000) / ((i + 1) * (b + 1)) % 2);
                if (filler % 7 == 0 || filler % 4 == 0 || filler % 3 == 0 || filler % 5 == 0) {
                    filler++;
                } else if (array[i][b] == 1) {
                    obstacles[filler++] = new Obstacle(i, b);
                }
            }
        }
    }

    public void level1() {
        // @formatter:off
        int[][] lvl1 = {
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},

        };
        // @formatter:on
        fillArray(lvl1);
    }

    public void levelSelect() {
        int[][] levelSelect = {
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},

        };
        fillArray(levelSelect);
    }
}





package indianajones.gamelogic;

import indianajones.bin.IndianaJones;
import indianajones.gamepieces.*;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Random;

public class GameLogic {
    private final int lines;
    public Bullet[] bullets;
    public int seed;
    public GameView gameView;
    public IndianaJones indianaJones;
    private final int columns;
    public int tickspeed;
    private final Canvas canvas;
    public Obstacle[] obstacles;
    public Jones jones;
    public Snake[] snakes;
    private final GamePiece[] gamePieces;
    public Grail grail;
    public Exit exit;
    private final int numberOfSnakes;
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
        bullets = new Bullet[10000];
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
        if (jonesIsOnTheSameSpotAs(this.grail) && !jones.gotGrail) {
            grail.invisible();
            jones.gotGrail = true;
            gameView.playSound("pickup.wav", false);

        } else if (this.jones.gotGrail && jonesIsOnTheSameSpotAs(this.exit)) {
            gameView.print(canvas.asString(), 22);
            indianaJones.jonesWon = true;
            this.gameOver = true;
            gameView.playSound("winsound.wav", false);
        } else if (snakeGotJones()) {
            gameView.print(canvas.asString(), 22);
            indianaJones.snakeWon = true;
            this.gameOver = true;
            if (indianaJones.lifes >= 2) {
                gameView.playSound("lostlife.wav", false);
            } else {
                gameView.playSound("bigloss.wav", false);
            }
            indianaJones.lifes--;
        }
    }

    public void gameLogicPacMan() {
        if (this.jones.gotGrail && jonesIsOnTheSameSpotAs(this.exit)) {
            gameView.print(canvas.asString(), 22);
            indianaJones.jonesWon = true;
            this.gameOver = true;
            gameView.playSound("winsound.wav", false);
        } else if (snakeGotJones()) {
            gameView.print(canvas.asString(), 22);
            indianaJones.snakeWon = true;
            this.gameOver = true;
            if (indianaJones.lifes >= 2) {
                gameView.playSound("pacman_death.wav", false);
            } else {
                gameView.playSound("pacman_death.wav", false);
            }
            indianaJones.lifes--;
        }
    }

    public void printHearts() {
        for (int i = 0; i < indianaJones.lifes; i++) {
            canvas.paint(26, i, '\u2661');
        }

    }

    public void escapeMenu() throws InterruptedException {
        Integer[] pressedKeys = gameView.getKeyCodesOfCurrentlyPressedKeys();
        for (int keyCode : pressedKeys) {
            if (keyCode == KeyEvent.VK_ESCAPE) {
                boolean stop = false;
                gameView.playSound("menuback.wav", false);
                sleep(110);
                while (!stop) {
                    gameView.addTextToCanvas("\n".repeat(2) + " ".repeat(18) + "Back to menu?", 0, 0, 22, Color.white, 0);
                    gameView.addTextToCanvas(" ".repeat(8) + "\"ESC\"\n" + " ".repeat(5) + "to continue", 100, 200, 22, Color.white, 0);
                    gameView.addTextToCanvas(" ".repeat(6) + "\"ENTER\"\n" + " ".repeat(4) + "to confirm", 520, 200, 22, Color.white, 0);
                    gameView.addTextToCanvas("Game paused!", 380, 450, 26, Color.white, 0);
                    gameView.printCanvas();
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
                            Thread.sleep(125);
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
            gameView.print(canvas.asString(), 22);
            gameLogic();
            sleep(tickspeed);
        }
        sleep(500);
    }

    public void gameLoopStandoff() throws InterruptedException {
        int timegone = 0;
        int length = 75;
        this.snakes = new Snake[length];
        int arraylength = 0;
        int wave1 = 0;
        int wave2 = 0;
        int counter1 = 0;
        exit.display = ' ';
        gameView.playSound("bumblebeekorsakov.wav", false);
        long time = 0;
        long time2 = 0;
        long delta = 0;
        while (!this.gameOver) {
            time = System.currentTimeMillis();
            this.canvas.fill(' ');
            gameLogic();
            if (delta >= 100) {
                if (timegone == 300) {
                    String powerup = "Press \"SPACE\" to teleport 6 fields\n" + " ".repeat(10) + "to the front\n";
                    for (int i = 1; i < powerup.length(); i++) {
                        gameView.addTextToCanvas("Powerup enabled!", 320, 50, 26, Color.white, 0);
                        gameView.addTextToCanvas(powerup.substring(0, i), 150, 200, 22, Color.white, 0);
                        gameView.printCanvas();
                        Thread.sleep(50);
                    }
                    boolean stop = false;
                    while (!stop) {
                        gameView.addTextToCanvas("Powerup enabled!", 320, 50, 26, Color.white, 0);
                        gameView.addTextToCanvas(powerup, 150, 200, 22, Color.white, 0);
                        gameView.addTextToCanvas("Press \"ENTER\" to contine!", 230, 400, 25, Color.white, 0);
                        gameView.printCanvas();
                        Integer[] pressedKeys1 = gameView.getKeyCodesOfCurrentlyPressedKeys();
                        for (int keypress : pressedKeys1) {

                            if (keypress == KeyEvent.VK_ENTER) {
                                stop = true;
                            }
                        }

                    }
                    jones.powerUpEnabled = true;
                }
                if (timegone >= 600) {
                    if (jones.gotGrail) {
                        grail.invisible();
                    } else {
                        grail.line = 13;
                        grail.column = 40;
                        grail.display = '\u269A';
                        exit.display = 'E';
                    }
                    for (Snake snake : snakes) {
                        if (snake != null && snake.column <= 2) {
                            snake.display = ' ';
                            snake.line = 0;
                            snake.column = 46;
                        } else if (snake != null && snake.column < 46) {
                            canvas.paint(snake.line, snake.column, snake.display);
                            snake.moveHorizontal();
                        } else if (snake != null && snake.line != 0) {
                            snake.line = 0;
                        }
                    }
                }
                if (arraylength < 65) {
                    snakes[arraylength] = new Snake((int) (Math.random() * 27), 47, jones);
                    arraylength++;
                }
                if (timegone > 350 && timegone < 600) {
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
                        if (counter1 < 65 - 1) {
                            counter1++;
                        } else {
                            counter1 = 0;
                        }
                    }
                }
                for (GamePiece gamePiece : gamePieces) {
                    if (gamePiece != null) {
                        gamePiece.move();
                        canvas.paint(gamePiece.line, gamePiece.column, gamePiece.display);
                    }
                }
                if (timegone < 600) {
                    for (Snake snake : snakes) {
                        if (snake != null && snake.column > 0) {
                            snake.moveHorizontal();
                            canvas.paint(snake.line, snake.column, snake.display);
                        } else if (snake != null && snake.column == 0) {
                            snake.column = 47;
                            snake.line = (int) (Math.random() * 27);
                        }
                    }
                }
                printHearts();
                gameView.print(canvas.asString(), 22);
                delta = 0;
                timegone++;
                escapeMenu();
            }
            Thread.sleep(10);
            time2 = System.currentTimeMillis();
            delta = time2 - time + delta;
        }

        sleep(500);
        gameView.stopAllSounds();
        jones.powerUpEnabled = false;
    }


    public void modifyExit(Exit exit, int line, int column, char character) {
        exit.line = line;
        exit.column = column;
        exit.display = character;
    }

    public void paintLevelSelectText() {
        //Movement
        gameView.addTextToCanvas("""
                Movement:
                W: Up
                A: Left
                S: Down
                D: Right
                Space: Powerup
                Esc: Menu""", 20, 20, 20, Color.white, 0);
        gameView.addTextToCanvas("""
                Gamepieces:
                Jones: \u24BF
                Snakes: S\\\u2240
                Grail: \u269A
                Obstacle: \u2BBD
                Exit: E
                Hearts: \u2661
                """, 20, 400, 20, Color.white, 0);
        if (jones.line > 10 && jones.line < 16 && jones.column < 26 && jones.column > 20) {
            gameView.addTextToCanvas("Description:\nGo near a gate to \nget " + "more info!", 630, 0, 18, Color.white, 0);
        } else if (jones.line > 9 && jones.line < 17 && jones.column <= 20 && jones.column >= 15) {
            gameView.addTextToCanvas("Description:\nThese are randomly\ngenerated levels with\na seed. Same seed same\nlevel! ", 630, 0, 18, Color.white, 0);
        } else if (jones.line > 9 && jones.line < 17 && jones.column <= 31 && jones.column >= 26) {
            gameView.addTextToCanvas("Description:\nWhen you finish a level\nthe game will save\nyour progess in a .txt\nfile!" + " Using \"continue\"\nputs you where you\nlast left off.", 630, 0, 18, Color.white, 0);
        } else if (jones.line >= 4 && jones.line <= 10 && jones.column <= 26 && jones.column >= 20) {
            gameView.addTextToCanvas("Descrption:\nStart your journey\nto bring home the holy\ngrail! You start from\nthe beginning!", 630, 0, 18, Color.white, 0);
        } else if (jones.line >= 16 && jones.line <= 22 && jones.column <= 26 && jones.column >= 20) {
            gameView.addTextToCanvas("Description:\nQuits the game! ", 630, 0, 18, Color.white, 0);

        }
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
        canvas.paint(10, 28, 'C');
        canvas.paint(10, 29, 'O');
        canvas.paint(10, 30, 'N');
        canvas.paint(10, 31, 'T');
        canvas.paint(10, 32, 'I');
        canvas.paint(10, 33, 'N');
        canvas.paint(10, 34, 'U');
        canvas.paint(10, 35, 'E');


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
            gameView.print(canvas.asString(), 22);
            gameLogic();
            if (jones.line == storyexit1.line && jones.column == storyexit1.column) {
                indianaJones.levelSelector = 1;
                this.gameOver = true;
                gameView.playSound("select.wav", false);
            } else if (jones.line == randomexit2.line && jones.column == randomexit2.column) {
                indianaJones.levelSelector = 2;
                this.gameOver = true;
                gameView.playSound("select.wav", false);
            } else if (jones.line == quitexit3.line && jones.column == quitexit3.column) {
                indianaJones.levelSelector = 3;
                this.gameOver = true;
                gameView.playSound("select.wav", false);
            } else if (jones.line == exit4.line && jones.column == exit4.column && indianaJones.checkpoint1) {
                indianaJones.levelSelector = 4;
                gameView.playSound("select.wav", false);
                this.gameOver = true;
            }
            sleep(tickspeed);

        }

        sleep(500);
    }

    public void gameLoopPacMan() throws InterruptedException {
        this.snakes = new Snake[4];
        int jonesgrails = 0;
        boolean initialize = true;
        grail.display = ' ';
        grail.line = 14;
        jones.isPacMan = true;
        grail.column = 45;
        obstacles[lines * columns - 2] = new Obstacle(13, 46);
        Grail[] grails = new Grail[4];


        while (!this.gameOver) {
            if (jonesgrails == 4) {
                obstacles[lines * columns - 2] = new Obstacle(14, 46);
                jones.gotGrail = true;
            }
            if (jones.column < 25 && initialize) {
                gameView.addTextToCanvas("Whats this?\nSeems familiar?!", 20, 20, 20, Color.white, 0);
                gameView.addTextToCanvas("Get all 4 grails and\na exit will open up!", 20, 500, 20, Color.white, 0);
            }
            if (jones.column >= 26 && initialize) {
                for (int i = 0; i < snakes.length; i++) {
                    gameView.playSound("pacman_beginning.wav", false);
                    obstacles[lines * columns - 1] = new Obstacle(13, 20);
                    snakes[i] = new Snake(lines, columns, jones);
                    snakes[i].line = 10;
                    snakes[i].column = 34;
                    grails[0] = new Grail(lines, columns);
                    grails[1] = new Grail(lines, columns);
                    grails[2] = new Grail(lines, columns);
                    grails[3] = new Grail(lines, columns);
                    grails[0].line = 0;
                    grails[0].column = 22;
                    grails[1].line = 0;
                    grails[1].column = 47;
                    grails[2].line = 25;
                    grails[2].column = 24;
                    grails[3].line = 25;
                    grails[3].column = 45;
                }
                for (Snake snake : snakes) {
                    snake.obstacles = obstacles;
                }
                initialize = false;
            }

            for (Grail grail : grails) {
                if (grail != null) {
                    if (jonesIsOnTheSameSpotAs(grail) && !jones.gotGrail) {
                        gameView.playSound("pacman_eatghost.wav", false);
                        grail.invisible();
                        grail.line = 12;
                        grail.column = 45;
                        jonesgrails++;


                    }
                }
            }
            this.canvas.fill(' ');
            escapeMenu();
            for (Grail grail : grails) {
                if (grail != null) {
                    canvas.paint(grail.line, grail.column, '\u25CC');
                }
            }
            for (GamePiece gamePiece : gamePieces) {
                gamePiece.move();
                canvas.paint(gamePiece.line, gamePiece.column, gamePiece.display);
            }

            for (Snake snake : snakes) {
                if (snake != null) {
                    canvas.paint(snake.line, snake.column, snake.display);
                    snake.movePacMan();
                }
            }
            for (Obstacle obstacle : obstacles) {
                if (obstacle != null) {
                    canvas.paint(obstacle.line, obstacle.column, obstacle.display);
                }
            }
            printHearts();
            gameView.print(canvas.asString(), 22);
            gameLogicPacMan();
            sleep(tickspeed);
        }
        jones.isPacMan = false;
        sleep(500);
    }

    public void gameLoopPuzzle() throws InterruptedException {
        snakes = new Snake[100];
        int timepassed = 0;
        int c = 0;
        boolean puzzle1 = false;
        boolean puzzle2 = false;
        boolean puzzle3 = false;
        boolean puzzle4 = false;
        boolean initialize = true;
        obstacles[lines * columns - 1] = new Obstacle(10, 24);
        obstacles[lines * columns - 2] = new Obstacle(12, 22);
        obstacles[lines * columns - 3] = new Obstacle(12, 26);
        obstacles[lines * columns - 4] = new Obstacle(14, 24);
        String start = "Maybe these weird notes in the corners\n     can help me get to the grail!" + "\n".repeat(8) + "      Press ENTER to continue...";
        for (int i = 1; i < start.length(); i++) {
            gameView.addTextToCanvas(start.substring(0, i), 110, 50, 22, Color.white, 0);
            gameView.printCanvas();
            Thread.sleep(15);
        }
        boolean stop = false;
        while (!stop) {
            Integer[] pressedKeys = gameView.getKeyCodesOfCurrentlyPressedKeys();
            for (int keyCode : pressedKeys) {
                if (keyCode == KeyEvent.VK_ENTER) {

                    stop = true;
                }
            }
        }
        while (!this.gameOver) {

            if (jones.line == 0 && jones.column == 47) {
                if (!puzzle1) {
                    puzzle1 = true;
                    gameView.playSound("tone1.wav", false);
                }
                if (puzzle2 || puzzle3 || puzzle4) {
                    gameView.playSound("tone5.wav", false);
                    puzzle1 = false;
                    puzzle2 = false;
                    puzzle3 = false;
                    puzzle4 = false;
                } else if (!puzzle1 && !puzzle2 && !puzzle3 && !puzzle4) {
                    gameView.playSound("tone1.wav", false);
                }
                puzzle1 = true;
                gameView.playSound("tone1.wav", false);
            } else if (jones.line == 26 && jones.column == 0) {
                if (puzzle1) {
                    puzzle2 = true;
                    puzzle1 = false;
                    gameView.playSound("tone1.wav", false);
                    gameView.playSound("tone2.wav", false);
                }
                if (puzzle3 || puzzle4) {
                    gameView.playSound("tone5.wav", false);
                    puzzle1 = false;
                    puzzle2 = false;
                    puzzle3 = false;
                    puzzle4 = false;
                } else if (!puzzle1 && !puzzle2 && !puzzle3 && !puzzle4) {
                    gameView.playSound("tone2.wav", false);
                }
            }
            if (jones.line == 26 && jones.column == 47) {
                if (puzzle2) {
                    puzzle3 = true;
                    puzzle2 = false;
                    gameView.playSound("tone1.wav", false);
                    gameView.playSound("tone2.wav", false);
                    gameView.playSound("tone3.wav", false);
                }
                if (puzzle1 || puzzle4) {
                    gameView.playSound("tone5.wav", false);
                    puzzle1 = false;
                    puzzle2 = false;
                    puzzle3 = false;
                    puzzle4 = false;
                } else if (!puzzle1 && !puzzle2 && !puzzle3 && !puzzle4) {
                    gameView.playSound("tone3.wav", false);
                }
            }
            if (jones.line == 0 && jones.column == 0) {
                if (puzzle3) {
                    puzzle4 = true;
                    puzzle3 = false;
                    gameView.playSound("tone1.wav", false);
                    gameView.playSound("tone2.wav", false);
                    gameView.playSound("tone3.wav", false);
                    gameView.playSound("tone4.wav", false);
                } else if (puzzle1 || puzzle2) {
                    gameView.playSound("tone5.wav", false);
                    puzzle1 = false;
                    puzzle2 = false;
                    puzzle3 = false;
                    puzzle4 = false;
                } else if (!puzzle1 && !puzzle2 && !puzzle3 && !puzzle4) {
                    gameView.playSound("tone4.wav", false);
                }
            }

            if (puzzle4 && initialize) {
                obstacles[lines * columns - 1] = new Obstacle(5, 16);
                obstacles[lines * columns - 2] = new Obstacle(5, 16);
                obstacles[lines * columns - 3] = new Obstacle(5, 16);
                obstacles[lines * columns - 4] = new Obstacle(5, 16);
                gameView.playSound("lockup.wav", false);
                initialize = false;
            }
            if (timepassed % 30 == 0 || timepassed == 0) {
                snakes[c] = new Snake(lines, columns, jones);
                if (Math.random() > 0.5) {
                    snakes[c].line = 0;
                    snakes[c].column = 23;
                    snake.display = '\u2240';
                } else {
                    snakes[c].line = 26;
                    snakes[c].column = 23;
                    snake.display = '\u2240';
                }
                c++;
            }
            for (Snake snake : snakes) {
                if (snake != null) {
                    snake.obstacles = obstacles;
                }
            }
            this.canvas.fill(' ');
            escapeMenu();
            for (Snake snake : snakes) {
                if (snake != null) {
                    canvas.paint(snake.line, snake.column, snake.display);
                    snake.movePuzzle();
                }
            }
            for (GamePiece gamePiece : gamePieces) {
                gamePiece.move();
                canvas.paint(gamePiece.line, gamePiece.column, gamePiece.display);
            }
            for (Obstacle obstacle : obstacles) {
                if (obstacle != null) {
                    canvas.paint(obstacle.line, obstacle.column, obstacle.display);
                }
            }
            for (int i = 0; i < indianaJones.lifes; i++) {
                canvas.paint(25, i, '\u2661');
            }
            canvas.paint(26, 0, '\u266B');
            canvas.paint(26, 47, '\u266B');
            canvas.paint(0, 0, '\u266B');
            canvas.paint(0, 47, '\u266B');
            gameView.print(canvas.asString(), 22);
            gameLogic();
            sleep(tickspeed);
            timepassed++;
        }
        sleep(500);
    }

    public void gameLoopGunFight() throws InterruptedException {
        Boss boss = new Boss(lines, columns, 13, 24, 100);
        int timegone = 0;
        snakes = new Snake[201];
        int c = 0;
        int wave1 = 0;
        int wave2 = 0;
        boolean stop = false;
        String start = "Shoot with Arrow Keys!" + "\n".repeat(8) + "Press ENTER to continue...";
        for (int i = 1; i < start.length(); i++) {
            gameView.addTextToCanvas(start.substring(0, i), 270, 50, 22, Color.white, 0);
            gameView.printCanvas();
            Thread.sleep(15);
        }
        gameView.playSound("megalovania.wav", false);
        long time = 0;
        long time1 = 0;
        long delta = 0;
        while (!stop) {
            Integer[] pressedKeys = gameView.getKeyCodesOfCurrentlyPressedKeys();
            for (int keyCode : pressedKeys) {
                if (keyCode == KeyEvent.VK_ENTER) {
                    stop = true;
                }
            }
        }

        while (!this.gameOver) {
            time = System.currentTimeMillis();
            if (delta >= 100) {


                if (c >= 175) {
                    c = 0;
                }

                if (timegone == 30) {
                    for (int i = 0; i < 25; i++) {
                        if (Math.random() > 0.5) {
                            snakes[c] = new Snake(lines, columns, jones);
                            snakes[c].line = (int) (Math.random() * 26);
                            snakes[c].column = 47;
                        } else {
                            snakes[c] = new SnakeDown(lines, columns, jones);
                            snakes[c].line = 1;
                            snakes[c].column = (int) (Math.random() * 47);
                            ;
                        }
                        c++;
                    }
                } else if (timegone == 65) {
                    for (int i = 0; i < 25; i++) {
                        if (Math.random() > 0.5) {
                            snakes[c] = new Snake(lines, columns, jones);
                            snakes[c].line = (int) (Math.random() * 26);
                            snakes[c].column = 47;
                        } else {
                            snakes[c] = new SnakeDown(lines, columns, jones);
                            snakes[c].line = 1;
                            snakes[c].column = (int) (Math.random() * 47);
                            ;
                        }
                        c++;
                    }
                } else if (timegone == 85) {
                    for (int i = 0; i < 25; i++) {
                        if (Math.random() > 0.5) {
                            snakes[c] = new Snake(lines, columns, jones);
                            snakes[c].line = (int) (Math.random() * 26);
                            snakes[c].column = 47;
                        } else {
                            snakes[c] = new SnakeDown(lines, columns, jones);
                            snakes[c].line = 1;
                            snakes[c].column = (int) (Math.random() * 47);
                            ;
                        }
                        c++;
                    }
                } else if (timegone == 105) {
                    for (int i = 0; i < 25; i++) {
                        if (Math.random() > 0.5) {
                            snakes[c] = new Snake(lines, columns, jones);
                            snakes[c].line = (int) (Math.random() * 26);
                            snakes[c].column = 47;
                        } else {
                            snakes[c] = new SnakeDown(lines, columns, jones);
                            snakes[c].line = 1;
                            snakes[c].column = (int) (Math.random() * 47);
                            ;
                        }
                        c++;
                    }
                } else if (timegone == 125) {
                    for (int i = 0; i < 25; i++) {
                        if (Math.random() > 0.5) {
                            snakes[c] = new Snake(lines, columns, jones);
                            snakes[c].line = (int) (Math.random() * 26);
                            snakes[c].column = 47;
                        } else {
                            snakes[c] = new SnakeDown(lines, columns, jones);
                            snakes[c].line = 1;
                            snakes[c].column = (int) (Math.random() * 47);
                            ;
                        }
                        c++;
                    }
                } else if (timegone == 145) {
                    for (int i = 0; i < 25; i++) {
                        if (Math.random() > 0.5) {
                            snakes[c] = new Snake(lines, columns, jones);
                            snakes[c].line = (int) (Math.random() * 26);
                            snakes[c].column = 47;
                        } else {
                            snakes[c] = new SnakeDown(lines, columns, jones);
                            snakes[c].line = 1;
                            snakes[c].column = (int) (Math.random() * 47);
                            ;
                        }
                        c++;
                    }
                } else if (timegone == 210) {
                    for (int i = 0; i < 20; i++) {
                        if (Math.random() > 0.5) {
                            snakes[c] = new Snake(lines, columns, jones);
                            snakes[c].line = (int) (Math.random() * 26);
                            snakes[c].column = 47;
                        } else {
                            snakes[c] = new SnakeDown(lines, columns, jones);
                            snakes[c].line = 1;
                            snakes[c].column = (int) (Math.random() * 47);
                            ;
                        }
                        c++;
                    }
                } else if (timegone == 220) {
                    for (int i = 0; i < 20; i++) {
                        if (Math.random() > 0.5) {
                            snakes[c] = new Snake(lines, columns, jones);
                            snakes[c].line = (int) (Math.random() * 26);
                            snakes[c].column = 47;
                        } else {
                            snakes[c] = new SnakeDown(lines, columns, jones);
                            snakes[c].line = 1;
                            snakes[c].column = (int) (Math.random() * 47);
                            ;
                        }
                        c++;
                    }
                } else if (timegone == 230) {
                    for (int i = 0; i < 20; i++) {
                        if (Math.random() > 0.5) {
                            snakes[c] = new Snake(lines, columns, jones);
                            snakes[c].line = (int) (Math.random() * 26);
                            snakes[c].column = 47;
                        } else {
                            snakes[c] = new SnakeDown(lines, columns, jones);
                            snakes[c].line = 1;
                            snakes[c].column = (int) (Math.random() * 47);
                            ;
                        }
                        c++;
                    }
                } else if (timegone == 235) {
                    for (int i = 0; i < 20; i++) {
                        if (Math.random() > 0.5) {
                            snakes[c] = new Snake(lines, columns, jones);
                            snakes[c].line = (int) (Math.random() * 26);
                            snakes[c].column = 47;
                        } else {
                            snakes[c] = new SnakeDown(lines, columns, jones);
                            snakes[c].line = 1;
                            snakes[c].column = (int) (Math.random() * 47);
                            ;
                        }
                        c++;
                    }
                } else if (timegone == 245) {
                    for (int i = 0; i < 20; i++) {
                        if (Math.random() > 0.5) {
                            snakes[c] = new Snake(lines, columns, jones);
                            snakes[c].line = (int) (Math.random() * 26);
                            snakes[c].column = 47;
                        } else {
                            snakes[c] = new SnakeDown(lines, columns, jones);
                            snakes[c].line = 1;
                            snakes[c].column = (int) (Math.random() * 47);
                            ;
                        }
                        c++;
                    }
                } else if (timegone == 260) {
                    for (int i = 0; i < 20; i++) {
                        if (Math.random() > 0.5) {
                            snakes[c] = new Snake(lines, columns, jones);
                            snakes[c].line = (int) (Math.random() * 26);
                            snakes[c].column = 47;
                        } else {
                            snakes[c] = new SnakeDown(lines, columns, jones);
                            snakes[c].line = 1;
                            snakes[c].column = (int) (Math.random() * 47);
                            ;
                        }
                        c++;
                    }
                } else if (timegone == 280) {
                    for (int i = 0; i < 20; i++) {
                        if (Math.random() > 0.5) {
                            snakes[c] = new Snake(lines, columns, jones);
                            snakes[c].line = (int) (Math.random() * 26);
                            snakes[c].column = 47;
                        } else {
                            snakes[c] = new SnakeDown(lines, columns, jones);
                            snakes[c].line = 1;
                            snakes[c].column = (int) (Math.random() * 47);
                            ;
                        }
                        c++;
                    }
                } else if (timegone == 290) {
                    for (int i = 0; i < 20; i++) {
                        if (Math.random() > 0.5) {
                            snakes[c] = new Snake(lines, columns, jones);
                            snakes[c].line = (int) (Math.random() * 26);
                            snakes[c].column = 47;
                        } else {
                            snakes[c] = new SnakeDown(lines, columns, jones);
                            snakes[c].line = 1;
                            snakes[c].column = (int) (Math.random() * 47);
                            ;
                        }
                        c++;
                    }
                } else if (timegone == 300) {
                    for (int i = 0; i < 20; i++) {
                        if (Math.random() > 0.5) {
                            snakes[c] = new Snake(lines, columns, jones);
                            snakes[c].line = (int) (Math.random() * 26);
                            snakes[c].column = 47;
                        } else {
                            snakes[c] = new SnakeDown(lines, columns, jones);
                            snakes[c].line = 1;
                            snakes[c].column = (int) (Math.random() * 47);
                            ;
                        }
                        c++;

                    }
                    if (timegone == 300) {
                        c = 0;
                    }
                } else if (timegone > 300) {
                    jones.powerUpEnabled = true;
                }

                if (timegone >= 370 && timegone <= 530) {
                    if (snakes[c] != null) {
                        if (wave1 <= 26) {
                            wave2 = wave1;
                        } else if (wave1 == 52) {
                            wave1 = 0;
                            wave2 = 0;
                        } else {
                            wave2--;
                        }
                        snakes[c].line = wave2;
                        snakes[c].column = 47;

                        wave1++;
                        c++;
                    } else {
                        snakes[c] = new Snake(lines, columns, jones);
                        snakes[c].line = (int) (Math.random() * 27);
                        snakes[c].column = 47;
                        c++;
                    }
                }
                if (timegone == 520) {
                    for (int i = 0; i < 25; i++) {
                        if (Math.random() > 0.5) {
                            snakes[c] = new Snake(lines, columns, jones);
                            snakes[c].line = (int) (Math.random() * 26);
                            snakes[c].column = 47;
                        } else {
                            snakes[c] = new SnakeDown(lines, columns, jones);
                            snakes[c].line = 1;
                            snakes[c].column = (int) (Math.random() * 47);
                            ;
                        }
                        c++;
                    }
                }
                if (timegone == 550) {
                    for (int i = 0; i < 25; i++) {
                        if (Math.random() > 0.5) {
                            snakes[c] = new Snake(lines, columns, jones);
                            snakes[c].line = (int) (Math.random() * 26);
                            snakes[c].column = 47;
                        } else {
                            snakes[c] = new SnakeDown(lines, columns, jones);
                            snakes[c].line = 1;
                            snakes[c].column = (int) (Math.random() * 47);
                            ;
                        }
                        c++;
                    }
                }
                if (timegone == 570) {
                    for (int i = 0; i < 25; i++) {
                        if (Math.random() > 0.5) {
                            snakes[c] = new Snake(lines, columns, jones);
                            snakes[c].line = (int) (Math.random() * 26);
                            snakes[c].column = 47;
                        } else {
                            snakes[c] = new SnakeDown(lines, columns, jones);
                            snakes[c].line = 1;
                            snakes[c].column = (int) (Math.random() * 47);
                            ;
                        }
                        c++;
                    }
                }
                if (timegone >= 550 && timegone <= 700) {
                    if (snakes[c] != null) {
                        if (wave1 <= 26) {
                            wave2 = wave1;
                        } else if (wave1 == 52) {
                            wave1 = 0;
                            wave2 = 0;
                        } else {
                            wave2--;
                        }
                        snakes[c].line = wave2;
                        snakes[c].column = 47;

                        wave1++;
                        c++;
                    } else {
                        snakes[c] = new Snake(lines, columns, jones);
                        snakes[c].line = (int) (Math.random() * 27);
                        snakes[c].column = 47;
                        c++;
                    }
                }
                if (timegone == 620) {
                    for (int i = 0; i < 25; i++) {
                        if (Math.random() > 0.5) {
                            snakes[c] = new Snake(lines, columns, jones);
                            snakes[c].line = (int) (Math.random() * 26);
                            snakes[c].column = 47;
                        } else {
                            snakes[c] = new SnakeDown(lines, columns, jones);
                            snakes[c].line = 1;
                            snakes[c].column = (int) (Math.random() * 47);
                            ;
                        }
                        c++;
                    }
                }
                if (timegone == 645) {
                    for (int i = 0; i < 25; i++) {
                        if (Math.random() > 0.5) {
                            snakes[c] = new Snake(lines, columns, jones);
                            snakes[c].line = (int) (Math.random() * 26);
                            snakes[c].column = 47;
                        } else {
                            snakes[c] = new SnakeDown(lines, columns, jones);
                            snakes[c].line = 1;
                            snakes[c].column = (int) (Math.random() * 47);
                            ;
                        }
                        c++;
                    }
                }
                if (timegone == 660) {
                    for (int i = 0; i < 25; i++) {
                        if (Math.random() > 0.5) {
                            snakes[c] = new Snake(lines, columns, jones);
                            snakes[c].line = (int) (Math.random() * 26);
                            snakes[c].column = 47;
                        } else {
                            snakes[c] = new SnakeDown(lines, columns, jones);
                            snakes[c].line = 1;
                            snakes[c].column = (int) (Math.random() * 47);
                            ;
                        }
                        c++;
                    }
                }
                if (timegone == 680) {
                    for (int i = 0; i < 25; i++) {
                        if (Math.random() > 0.5) {
                            snakes[c] = new Snake(lines, columns, jones);
                            snakes[c].line = (int) (Math.random() * 26);
                            snakes[c].column = 47;
                        } else {
                            snakes[c] = new SnakeDown(lines, columns, jones);
                            snakes[c].line = 1;
                            snakes[c].column = (int) (Math.random() * 47);
                            ;
                        }
                        c++;
                    }
                }
                if (timegone == 685) {
                    for (int i = 0; i < 25; i++) {
                        if (Math.random() > 0.5) {
                            snakes[c] = new Snake(lines, columns, jones);
                            snakes[c].line = (int) (Math.random() * 26);
                            snakes[c].column = 47;
                        } else {
                            snakes[c] = new SnakeDown(lines, columns, jones);
                            snakes[c].line = 1;
                            snakes[c].column = (int) (Math.random() * 47);
                            ;
                        }
                        c++;
                    }
                }
                if (timegone == 695) {
                    for (int i = 0; i < 25; i++) {
                        if (Math.random() > 0.5) {
                            snakes[c] = new Snake(lines, columns, jones);
                            snakes[c].line = (int) (Math.random() * 26);
                            snakes[c].column = 47;
                        } else {
                            snakes[c] = new SnakeDown(lines, columns, jones);
                            snakes[c].line = 1;
                            snakes[c].column = (int) (Math.random() * 47);
                            ;
                        }
                        c++;
                    }
                }
                if (timegone == 700) {
                    for (int i = 0; i < 25; i++) {
                        if (Math.random() > 0.5) {
                            snakes[c] = new Snake(lines, columns, jones);
                            snakes[c].line = (int) (Math.random() * 26);
                            snakes[c].column = 47;
                        } else {
                            snakes[c] = new SnakeDown(lines, columns, jones);
                            snakes[c].line = 1;
                            snakes[c].column = (int) (Math.random() * 47);
                            ;
                        }
                        c++;
                    }
                }
                if (timegone == 710) {
                    for (int i = 0; i < 25; i++) {
                        if (Math.random() > 0.5) {
                            snakes[c] = new Snake(lines, columns, jones);
                            snakes[c].line = (int) (Math.random() * 26);
                            snakes[c].column = 47;
                        } else {
                            snakes[c] = new SnakeDown(lines, columns, jones);
                            snakes[c].line = 1;
                            snakes[c].column = (int) (Math.random() * 47);
                            ;
                        }
                        c++;
                    }
                }
                if (timegone >= 730 && timegone <= 760) {
                    gameView.addTextToCanvas("If you survived that long, you desserve to win", 190, 50, 22, Color.white, 0);
                }
                if (boss.lifes == 0) {
                    indianaJones.jonesWon = true;
                    this.gameOver = true;
                    stop = false;
                    gameView.playSound("winsound.wav", false);
                    String ending = "You beat the game. Congratulations!" + "\n".repeat(2) + "Thank you for playing my first game" + "\n".repeat(6) + "You can delete the checkpoints.txt\nand start again!" + "\n\nPress ENTER to continue...";
                    for (int i = 1; i < ending.length(); i++) {
                        gameView.addTextToCanvas(ending.substring(0, i), 110, 50, 25, Color.white, 0);
                        gameView.printCanvas();
                        Thread.sleep(50);
                    }
                    while (!stop) {
                        Integer[] pressedKeys = gameView.getKeyCodesOfCurrentlyPressedKeys();
                        for (int keyCode : pressedKeys) {
                            if (keyCode == KeyEvent.VK_ENTER) {

                                stop = true;
                            }
                        }
                    }
                }
                if (timegone % 5 == 0) {
                    Integer[] pressedKeys = gameView.getKeyCodesOfCurrentlyPressedKeys();
                    for (int keyCode : pressedKeys) {
                        if (keyCode == KeyEvent.VK_UP) {
                            bullets[c] = new BulletUp(jones.line, jones.column);
                            c++;
                        } else if (keyCode == KeyEvent.VK_DOWN) {
                            bullets[c] = new BulletDown(jones.line, jones.column);
                            gameView.playSound("laserShoot.wav", false);
                            c++;
                        } else if (keyCode == KeyEvent.VK_LEFT) {
                            bullets[c] = new BulletLeft(jones.line, jones.column);
                            gameView.playSound("laserShoot.wav", false);
                            c++;
                        } else if (keyCode == KeyEvent.VK_RIGHT) {
                            bullets[c] = new BulletRight(jones.line, jones.column);
                            gameView.playSound("laserShoot.wav", false);
                            c++;
                        }
                    }

                }
                for (GamePiece gamePiece : gamePieces) {
                    gamePiece.move();
                }
                for (Bullet bullet : bullets) {
                    if (bullet != null) {
                        if (bullet.line > 0) {
                            canvas.paint(bullet.line, bullet.column, bullet.display);
                            if (boss.isOnBullet(bullet)) {
                                boss.lifes--;
                                bullet.line = -1;
                            }
                            bullet.move();
                        }
                        if (bullet.line == 0 || bullet.column == 0 || bullet.line == 26 || bullet.column == 46) {
                            bullet.line = -1;
                        }
                    }
                }
                for (Snake snake : snakes) {
                    if (snake != null) {
                        snake.moveHorizontal();
                    }
                }
                escapeMenu();
                delta = 0;
                timegone++;
            }
            this.canvas.fill(' ');

            for (Bullet bullet : bullets) {
                if (bullet != null) {
                    if (bullet.line > 0) {
                        canvas.paint(bullet.line, bullet.column, bullet.display);
                    }
                }
            }
            gameView.addTextToCanvas("Boss Health: " + boss.lifes, 730, 530, 19, Color.RED, 0);
            printHearts();
            for (Snake snake : snakes) {
                if (snake != null) {
                    canvas.paint(snake.line, snake.column, snake.display);
                }
            }
            gameLogic();
            if (timegone >= 160 && timegone < 210) {
                gameView.addTextToCanvas("\"You survived only the beginning!\"\n   " +
                        "Now comes the real challenge", 180, 50, 22, Color.RED, 0);
            }
            if (timegone >= 330 && timegone <= 370) {
                gameView.addTextToCanvas("\"Now you will face everything i have!\"", 150, 50, 22, Color.RED, 0);
                gameView.addTextToCanvas("Powerup enabled!", 320, 500, 23, Color.white, 0);
            }
            if (timegone < 50) {
                gameView.addTextToCanvas("\"You will never kill me!\"", 240, 50, 22, Color.RED, 0);
            }
            for (GamePiece gamePiece : gamePieces) {
                canvas.paint(gamePiece.line, gamePiece.column, gamePiece.display);
            }
            canvas.paint(boss.line, boss.column, boss.display);
            gameView.print(canvas.asString(), 22);

            time1 = System.currentTimeMillis();
            delta = time1 - time + delta;
        }
        sleep(500);
        gameView.stopAllSounds();
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

    public void fillArrayRandom(int seed) {
        this.seed = seed;
        int filler = 0;
        int[][] array = new int[27][48];
        for (int i = 0; i < 27; i++) {
            for (int b = 0; b < 48; b++) {
                array[i][b] = ((this.seed * 1000) / ((i + 1) * (b + 1)) % 2);
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

    public void pacMan() {
        // @formatter:off
        int[][] theTown = {{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 0}, {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 0}, {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 0}, {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 1, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1, 0}, {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 1, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1, 0}, {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1}, {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 1, 1, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 1, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 0, 1, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 1, 1, 0, 1, 1, 1, 1, 1}, {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 1, 1, 0, 1, 1, 1, 1, 1}, {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 0, 1, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 1, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 0, 1, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1, 1}, {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 0}, {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 0}, {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 0, 1, 1}, {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 0, 1, 1}, {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1}, {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}};
        // @formatter:on
        fillArray(theTown);
    }

    public void levelSelect() {
        // @formatter:off
        //RIP formatting
        int[][] levelSelect = {{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                // @formatter:on

        };
        fillArray(levelSelect);
    }

    public void gunFight() {
        // @formatter:off

        int[][] array = {
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}};
        // @formatter:on

        fillArray(array);
    }

    public void puzzleObstacles() {
        // @formatter:on
        int[][] array = {
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
        };
        // @formatter:off

        fillArray(array);
    }

}





package indianajones.bin;

import indianajones.gamelogic.GameLogic;
import indianajones.gamelogic.GameView;
import indianajones.gamepieces.Snake;
import java.awt.*;
import java.awt.event.KeyEvent;

public class IndianaJones {
    public int levelSelector;
    public boolean snakeWon;
    public boolean jonesWon;
    public int lifes;

    public void waitForEnterAndFlashingText(GameView gameView) throws InterruptedException {
        String Continue = "Press Enter to continue...";
        String jones = "Welcome to the Jungle Mr.Jones (J)" + "\n";
        String gameInfo = "Catch the Grail (G) and escape the snakes (S) ";
        int lines = 27;
        boolean stop = true;
        int ib = 0;
        while (stop) {
            Integer[] keyEvents = gameView.getKeyCodesOfCurrentlyPressedKeys();
            for (int keyEvent : keyEvents) {
                for (int i = 25; i <= 10000; i++) {
                    if (keyEvent == KeyEvent.VK_ENTER) {
                        stop = false;
                        break;
                    }
                }
            }
            gameView.addTextToCanvas(" ".repeat(4) + "Indiana Jones", 0, 0, 46, Color.orange, 0);
            gameView.addTextToCanvas("\n".repeat(lines / 4) + " ".repeat(6) + jones, 0, 0,
                    20, Color.blue, 0);
            gameView.addTextToCanvas("\n".repeat(8) + " ".repeat(2) + gameInfo, 0, 0, 20, Color.blue, 0);
            gameView.addTextToCanvas("\n".repeat(10) + " ".repeat(10) + Continue, 0, 0, 20, Color.blue, 0);
            gameView.printCanvas();
            Thread.sleep(100);
            ib++;
            for (int keyEvent : keyEvents) {
                for (int i = 25; i <= 10000; i++) {
                    if (keyEvent == KeyEvent.VK_ENTER) {
                        stop = false;
                        break;
                    }
                }
            }
            if (ib % 2 == 0) {
                gameView.addTextToCanvas(" ".repeat(4) + "Indiana Jones", 0, 0, 46, Color.orange, 0);
                gameView.addTextToCanvas("\n".repeat(lines / 4) + " ".repeat(6) + jones, 0, 0,
                        20, Color.blue, 0);
                gameView.addTextToCanvas("\n".repeat(8) + " ".repeat(2) + gameInfo, 0, 0, 20, Color.blue, 0);
                gameView.printCanvas();
                Thread.sleep(100);
                for (int keyEvent : keyEvents) {
                    for (int i = 25; i <= 10000; i++) {
                        if (keyEvent == KeyEvent.VK_ENTER) {
                            stop = false;
                            break;
                        }
                    }
                }
            }
        }
    }

    public void startScreen(int lines, GameView gameView, IndianaJones indianaJones) throws InterruptedException {

        gameView.setWindowTitle("IndianaJones- Escape the snakes");
        for (int i = 0; i < 200; i++) {
            gameView.addTextToCanvas(" ".repeat(4) + "Indiana Jones", 0, 0, 245 - i, Color.orange, 0);
            gameView.printCanvas();
            Thread.sleep(1);
        }
        Thread.sleep(1000);
        String jones = "Welcome to the Jungle Mr.Jones (J)" + "\n";
        int c = 0;
        for (int i = 0; i <= jones.length() - 1; i++) {
            gameView.addTextToCanvas(" ".repeat(4) + "Indiana Jones", 0, 0, 46, Color.orange, 0);
            gameView.addTextToCanvas("\n".repeat(lines / 4) + " ".repeat(6) + jones.substring(0, c++), 0, 0, 20, Color.blue, 0);
            gameView.addTextToCanvas("\n".repeat(lines / 4) + " ".repeat(6) + jones.substring(0, 1 + i), 0, 0, 20, Color.blue, 0);
            gameView.printCanvas();
            Thread.sleep(75);
        }
        String gameInfo = "Catch the Grail (G) and escape the snakes (S) ";
        c = 0;
        for (int i = 0; i <= gameInfo.length() - 1; i++) {
            gameView.addTextToCanvas(" ".repeat(4) + "Indiana Jones", 0, 0, 46, Color.orange, 0);
            gameView.addTextToCanvas("\n".repeat(lines / 4) + " ".repeat(6) + jones, 0, 0,
                    20, Color.blue, 0);
            gameView.addTextToCanvas("\n".repeat(8) + " ".repeat(2) + gameInfo.substring(0, c++), 0, 0, 20, Color.blue, 0);
            gameView.addTextToCanvas("\n".repeat(8) + " ".repeat(2) + gameInfo.substring(0, 1 + i), 0, 0, 20, Color.blue, 0);
            gameView.printCanvas();
            Thread.sleep(75);
        }
        c = 0;
        String Continue = "Press Enter to continue...";
        for (int i = 0; i <= Continue.length() - 1; i++) {
            gameView.addTextToCanvas(" ".repeat(4) + "Indiana Jones", 0, 0, 46, Color.orange, 0);
            gameView.addTextToCanvas("\n".repeat(lines / 4) + " ".repeat(6) + jones, 0, 0,
                    20, Color.blue, 0);
            gameView.addTextToCanvas("\n".repeat(8) + " ".repeat(2) + gameInfo, 0, 0, 20, Color.blue, 0);
            gameView.addTextToCanvas("\n".repeat(10) + " ".repeat(10) + Continue.substring(0, c++), 0, 0, 20, Color.blue, 0);
            gameView.addTextToCanvas("\n".repeat(10) + " ".repeat(10) + Continue.substring(0, 1 + i), 0, 0, 20, Color.blue, 0);
            gameView.printCanvas();
            Thread.sleep(75);
        }


        waitForEnterAndFlashingText(gameView);
        indianaJones.levelSelector = 0;
    }

    public void setObstaclesforGamePieces(GameLogic gameLogic) {
        gameLogic.jones.obstacles = gameLogic.obstacles;
        for (Snake snake : gameLogic.snakes) {
            snake.obstacles = gameLogic.obstacles;
        }
    }

    public void setJonesAndGrailInMiddle(int lines, int columns, GameLogic gameLogic) {
        gameLogic.jones.line = lines / 2;
        gameLogic.jones.column = columns / 2;
        gameLogic.grail.column = columns / 2;
        gameLogic.grail.line = lines / 2;
    }

    public void setupLevelSelectScreen(GameLogic gameLogic, int lines, int columns) {
        setJonesAndGrailInMiddle(lines, columns, gameLogic);
        gameLogic.levelSelect();
        setObstaclesforGamePieces(gameLogic);
        gameLogic.exit.display = ' ';
        gameLogic.exit.line = 26;
        gameLogic.exit.column = 46;
    }

    public static void main(String[] args) throws InterruptedException {
        IndianaJones indianaJones = new IndianaJones();
        indianaJones.lifes = 3;
        indianaJones.levelSelector = -1;
        indianaJones.snakeWon = false;
        indianaJones.jonesWon = false;
        int lines = 27;
        int columns = 48;
        int numberofsnakes = 25;
        int tickspeed = 100;
        boolean gameFinish = false;
        //Making Window
        GameView startScreen = new GameView();

        while (!gameFinish) {
            //StartScreen-----handled in method startScreen()
            if (indianaJones.levelSelector == -1) {
                indianaJones.startScreen(lines, startScreen, indianaJones);
            }
            // Level select
            if (indianaJones.levelSelector == 0) {
                GameLogic levelSelectScreen = new GameLogic(lines, columns, 100, 0, startScreen, indianaJones);
                indianaJones.setupLevelSelectScreen(levelSelectScreen, lines, columns);
                indianaJones.lifes = 3;
                levelSelectScreen.gameLoopLevelSelect();
            }
            //Level 1 / The Warehouse
            else if (indianaJones.levelSelector == 1) {
                startScreen.setWindowTitle("Indiana Jones - \"Level Selection\"");
                indianaJones.jonesWon = false;
                while (!indianaJones.jonesWon && indianaJones.lifes > 0) {
                    GameLogic level1 = new GameLogic(lines, columns, tickspeed, 30, startScreen, indianaJones);
                    level1.level1();
                    startScreen.setWindowTitle("Indiana Jones - \"The Warehouse\"");
                    indianaJones.setObstaclesforGamePieces(level1);
                    level1.grail.line = 13;
                    level1.grail.column = 24;
                    level1.gameLoop();
                    if (indianaJones.snakeWon && indianaJones.lifes<1) {
                        indianaJones.snakeWon = false;
                        indianaJones.levelSelector=0;
                    }
                    if(indianaJones.jonesWon){
                        indianaJones.levelSelector = 12;
                    }
                }
            }
            //Level 2 / The Car Chase
            else if (indianaJones.levelSelector == 12) {
                startScreen.setWindowTitle("Indiana Jones - \"The Chase\"");
                indianaJones.jonesWon = false;
                while (!indianaJones.jonesWon && indianaJones.lifes > 0){
                    GameLogic carChase = new GameLogic(lines, columns, tickspeed, 0, startScreen, indianaJones);

                    carChase.gameLoopCarChase();
                    if (indianaJones.snakeWon && indianaJones.lifes<1) {
                        indianaJones.snakeWon = false;
                        indianaJones.levelSelector=0;
                    }
                    if(indianaJones.jonesWon){
                        indianaJones.levelSelector = 0;
                    }
                }
            }

            // Level 2 / Random Levels

            else if (indianaJones.levelSelector == 2) {
                gameFinish = true;
                System.exit(1);
            }
            //Exit

            else if (indianaJones.levelSelector == 3) {
                gameFinish = true;
            //Checkpoints

            } else if (indianaJones.levelSelector == 4) {
                gameFinish = true;
            }
        }
        startScreen.closeGameView(true);
    }
}



        /*while (!gameLogic.gameIsWon) {
            numberofsnakes--;
            tickspeed += 25;
            gameLogic = new GameLogic(lines, columns, tickspeed, numberofsnakes);
            gameLogic.gameLoop();
            gameLogic.obstacles[0]= new Obstacle(10,15);
            gameLogic.gameView.closeGameView(false);
        }
        GameView endScreen = new GameView();
        for (int i = 4; i >= 0; i--) {
            endScreen.print("Du hast gewonnen\n"
                    + "Der Tickspeed war am Ende: " + tickspeed + "\n"
                    + "Die Schlangezahl war am Ende: " + numberofsnakes
                    + "\nDein Score ist: "+((100*numberofsnakes+250+10000)-tickspeed*10)
                    + "\n\nFenster schließt in " + i + " Sekunden...", 22);
            gameLogic.sleep(1000);
        }

        System.exit(1);*/

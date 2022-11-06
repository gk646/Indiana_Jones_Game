package indianajones.bin;

import indianajones.gamelogic.GameLogic;
import indianajones.gamelogic.GameView;
import indianajones.gamepieces.Snake;

import java.io.File;  // Import the File class
import java.io.FileNotFoundException;
import java.io.IOException;  // Import the IOException class to handle errors
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.awt.*;
import java.awt.event.KeyEvent;

import static java.nio.file.StandardOpenOption.APPEND;


public class IndianaJones {
    public int levelSelector;
    public boolean snakeWon;
    static boolean gameFinish = false;
    public boolean checkpoint1 = false;
    public boolean checkpoint2 = false;
    public boolean checkpoint3 = false;
    public boolean checkpoint4 = false;
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

    public static void main(String[] args) throws InterruptedException, IOException {
        IndianaJones indianaJones = new IndianaJones();
        indianaJones.lifes = 3;
        indianaJones.levelSelector = -1;
        indianaJones.snakeWon = false;
        indianaJones.jonesWon = false;
        int lines = 27;
        int columns = 48;
        int numberofsnakes = 25;
        int tickspeed = 100;

        Path path = Paths.get("checkpoint.txt");
        File checkpointtxt = new File("checkpoint.txt");
        if (checkpointtxt.exists()) {
            Scanner reader = new Scanner(checkpointtxt);
            while (reader.hasNextLine()) {
                String checkpointdata = reader.nextLine();
                if (checkpointdata.contains("1")) {
                    indianaJones.checkpoint1 = true;
                } else if (checkpointdata.contains("2")) {
                    indianaJones.checkpoint2 = true;
                } else if (checkpointdata.contains("3")) {
                    indianaJones.checkpoint3 = true;
                } else if (checkpointdata.contains("4")) {
                    indianaJones.checkpoint4 = true;
                }
            }
        } else {
            String standartText = ("""
                    This is the .txt for the Indiana Jones Game.
                    Please keep it in the same directory as the game file (.jar)
                    You have reached the following checkpoints:\s""");
            byte[] strToBytes = standartText.getBytes();
            Files.write(path, strToBytes);

        }

        //Making Window
        //todo transition screens
        GameView startScreen = new GameView();

        while (!gameFinish) {
            //StartScreen-----handled in method startScreen()
            if (indianaJones.levelSelector == -1) {
                indianaJones.startScreen(lines, startScreen, indianaJones);
            }
            // Level select
            //todo descriptions texts / level description
            if (indianaJones.levelSelector == 0) {
                GameLogic levelSelectScreen = new GameLogic(lines, columns, 100, 0, startScreen, indianaJones);
                startScreen.setWindowTitle("Indiana Jones - \"Level Selection\"");
                indianaJones.setupLevelSelectScreen(levelSelectScreen, lines, columns);
                indianaJones.lifes = 3;
                levelSelectScreen.gameLoopLevelSelect();
            }
            //Level 1 / The Warehouse
            else if (indianaJones.levelSelector == 1) {
                indianaJones.jonesWon = false;
                startScreen.setWindowTitle("Indiana Jones - \"The Warehouse\"");
                while (!indianaJones.jonesWon && indianaJones.lifes > 0) {
                    GameLogic level1 = new GameLogic(lines, columns, tickspeed, 30, startScreen, indianaJones);
                    level1.level1();
                    indianaJones.setObstaclesforGamePieces(level1);
                    level1.grail.line = 13;
                    level1.grail.column = 24;
                    level1.gameLoop();
                    //todo flashing screen when hit
                    if (indianaJones.snakeWon && indianaJones.lifes < 1) {
                        indianaJones.snakeWon = false;
                        indianaJones.levelSelector = 0;
                    }
                    if (indianaJones.jonesWon) {
                        if (!indianaJones.checkpoint1) {
                            Files.writeString(path, "\nYou have reached Checkpoint 1!", APPEND);
                        }
                        indianaJones.levelSelector = 12;
                    }
                }
            }
            //Level 1.1 / "The Standoff"
            else if (indianaJones.levelSelector == 12) {
                startScreen.setWindowTitle("Indiana Jones - \"The Standoff\"");
                indianaJones.jonesWon = false;
                while (!indianaJones.jonesWon && indianaJones.lifes > 0) {
                    GameLogic carChase = new GameLogic(lines, columns, tickspeed, 0, startScreen, indianaJones);

                    carChase.gameLoopCarChase();
                    // todo proper end scene  all snakes go off screen you get grail
                    //todo snake line on column 45
                    if (indianaJones.snakeWon && indianaJones.lifes < 1) {
                        indianaJones.snakeWon = false;
                        indianaJones.levelSelector = 0;
                    }
                    if (indianaJones.jonesWon) {
                        if (!indianaJones.checkpoint2) {
                            Files.writeString(path, "\nYou have reached Checkpoint 2!", APPEND);
                        }

                        indianaJones.levelSelector = 0;
                    }
                }
            }
            // Level 2 / Random Levels
            // todo make random levels playable
            else if (indianaJones.levelSelector == 2) {
                while (!indianaJones.jonesWon && indianaJones.lifes > 0) {
                    GameLogic randomlevel = new GameLogic(lines, columns, tickspeed, 30, startScreen, indianaJones);
                    randomlevel.fillArrayRandom();
                    startScreen.setWindowTitle("Indiana Jones - \"Random Level " + randomlevel.seed + "\"");
                    indianaJones.setObstaclesforGamePieces(randomlevel);
                    randomlevel.gameLoop();
                    //todo flashing screen when hit
                    if (indianaJones.snakeWon && indianaJones.lifes < 1) {
                        indianaJones.snakeWon = false;
                        indianaJones.levelSelector = 0;
                    }
                    if (indianaJones.jonesWon) {
                        indianaJones.levelSelector = 0;
                    }
                }
            }
            //Exit
            else if (indianaJones.levelSelector == 3) {
                gameFinish = true;

                //Checkpoints

            } else if (indianaJones.levelSelector == 4) {
                if(indianaJones.checkpoint1){
                    indianaJones.levelSelector=12;
                }
            }
        }
        startScreen.closeGameView(true);
    }
}

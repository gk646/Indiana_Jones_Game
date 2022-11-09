package indianajones.gamelogic;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.swing.Timer;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.font.TextAttribute;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Ein Fenster, welches das einfache Gestalten von Spielen erlaubt. Es wird eine Leinwand mit einer Auflösung von
 * {@value WIDTH} * {@value HEIGHT} Pixeln erzeugt, es kann Ton ausgegeben werden und Tastatur- und Mausereignisse
 * werden zurückliefert.
 *
 * @author Andreas Berl
 */
public class GameView {

    /**
     * Breite der Leinwand in Pixeln. Sie können diese Variable anpassen.
     * Achtung: WIDTH * HEIGHT muss im Bereich zwischen 500.000 Pixeln und 540.000 Pixeln liegen.
     */
    public static final int WIDTH = 1056; // Original: 960

    /**
     * Höhe der Leinwand in Pixeln. Sie können diese Variable anpassen.
     * Achtung: WIDTH * HEIGHT muss im Bereich zwischen 500.000 Pixeln und 540.000 Pixeln liegen.
     */
    public static final int HEIGHT = 594; // Original: 540

    /**
     * Rechteck mit den Grenzen der Leinwand.
     */
    public static final java.awt.Rectangle BOUNDS = new java.awt.Rectangle(0, 0, GameView.WIDTH, GameView.HEIGHT);

    // Klassen
    private final GameTime gameTime;
    private final Canvas canvas;
    private final Window window;
    private final Mouse mouse;
    private final Keyboard keyboard;
    private final Sound sound;
    private final SwingAdapter swingAdapter;

    /**
     * Es wird eine Leinwand mit einer Auflösung von {@value WIDTH} * {@value HEIGHT} Pixeln erzeugt (Breite =
     * {@value WIDTH} Pixel, Höhe = {@value HEIGHT}
     * Pixel).
     * <pre>
     * <code>
     * <br>
     * 0/0 . . . . . {@value WIDTH}/0<br>
     * . . . . . . . . . .<br>
     * . . . . . . . . . .<br>
     * . . . . . . . . . .<br>
     * 0/{@value HEIGHT} . . . {@value WIDTH}/{@value HEIGHT}<br>
     * <br>
     * </code>
     * </pre>
     */
    public GameView() {
        this.gameTime = new GameTime();
        this.swingAdapter = new SwingAdapter();
        this.window = new Window(swingAdapter);
        this.mouse = new Mouse(swingAdapter);
        this.keyboard = new Keyboard();
        this.sound = new Sound();
        this.canvas = new Canvas();

        this.swingAdapter.registerListeners(mouse, keyboard, sound);
    }

    /**
     * Setzt den Fenstertitel.
     *
     * @param title Der Fenstertitel
     */
    public void setWindowTitle(String title) {
        window.setTitle(title);
    }

    /**
     * Legt ein Symbol für die Titelleiste fest. Das Symbolfile muss in einem Verzeichnis "src/resources" liegen. Bitte
     * den Namen des Files ohne Verzeichnisnamen angeben, z.B.<code>setWindowIcon("Symbol.png")</code>.
     *
     * @param iconFileName Der Dateiname des Symbols.
     */
    public void setWindowIcon(String iconFileName) {
        window.setWindowIcon(iconFileName);
    }

    /**
     * Text, der in der Statuszeile angezeigt wird.
     *
     * @param statusText Text der Statuszeile.
     */
    public void setStatusText(String statusText) {
        window.setStatusText(statusText);
    }

    /**
     * Setzt die Hintergrundfarbe.
     *
     * @param backgroundColor Hintergrundfarbe
     */
    public void setBackgroundColor(Color backgroundColor) {
        canvas.setBackgroundColor(backgroundColor);
    }


    /**
     * Fügt eine neue Farbe zur Farbtabelle für Block-Grafiken hinzu oder überschreibt eine vorhandene Farbe mit neuen
     * Werten.
     * <pre>
     * <code>
     * <br>
     * Die bereits vordefinierte Farbtabelle:
     * 'R' = Color.RED
     * 'r' = Color.RED.brighter()
     * 'G' = Color.GREEN
     * 'g' = Color.GREEN.brighter()
     * 'B' = Color.BLUE
     * 'b' = Color.BLUE.brighter()
     * 'Y' = Color.YELLOW
     * 'y' = Color.YELLOW.brighter()
     * 'P' = Color.PINK
     * 'p' = Color.PINK.brighter()
     * 'C' = Color.CYAN
     * 'c' = Color.CYAN.brighter()
     * 'M' = Color.MAGENTA
     * 'm' = Color.MAGENTA.brighter()
     * 'O' = Color.ORANGE
     * 'o' = Color.ORANGE.brighter()
     * 'W' = Color.WHITE
     * 'L' = Color.BLACK
     * </code>
     * </pre>
     *
     * @param character Buchstabe, der der Farbe zugeordnet ist.
     * @param color     Die Farbe, die dem Buchstaben zugeordnet ist.
     */
    public void setColorForBlockImage(char character, Color color) {
        swingAdapter.setColorForBlockImage(character, color);
    }

    /**
     * Es wird ein Startbildschirm mit einem Auswahlmenü angezeigt. Die Auswahl des Benutzers wird zurückgegeben.
     *
     * @param title          Titel des Programms.
     * @param description    Beschreibung des Programms. Achtung, es steht nicht viel Platz zur Verfügung. An den
     *                       passenden Stellen müssen Zeilenumbrüche eingefügt werden.
     * @param selectionTitle Titel des Auswahlmenüs.
     * @param selectionItems Einträge des Auswahlmenüs.
     * @param selectedItem   Gibt an, welcher Eintrag vorselektiert sein soll. Der erste Eintrag hat den Wert 0.
     * @return Der vom Benutzer gewählte Eintrag. Der erste Eintrag hat den Wert 0.
     */
    public int showStartScreenWithChooseBox(String title, String description, String selectionTitle,
                                            String[] selectionItems, int selectedItem) {
        StartScreenWithChooseBox startScreenWithChooseBox = new StartScreenWithChooseBox(this, title, description,
                selectionTitle,
                selectionItems, selectedItem);
        pollKeyEvents();
        startScreenWithChooseBox.printStartScreen();
        return startScreenWithChooseBox.getSelectedItem();
    }

    /**
     * Es wird ein Startbildschirm mit einem einfachen Auswahlmenü angezeigt: "Easy", "Standard" und "Close Game". Die
     * Auswahl des Benutzers wird zurückgegeben. Falls der Benutzer "Beenden" wählt, wird das Programm sofort beendet.
     *
     * @param title       Titel des Programms.
     * @param description Beschreibung des Programms. Achtung, es steht nicht viel Platz zur Verfügung. An den passenden
     *                    Stellen müssen Zeilenumbrüche eingefügt werden.
     * @return true falls der Spieler "Easy" gewählt hat, ansonsten false.
     */
    public boolean showSimpleStartScreen(String title, String description) {
        return showSimpleStartScreen(title, description, false);
    }

    /**
     * Es wird ein Startbildschirm mit einem einfachen Auswahlmenü angezeigt: "Easy", "Standard" und "Close Game". Die
     * Auswahl des Benutzers wird zurückgegeben. Falls der Benutzer "Beenden" wählt, wird das Programm sofort beendet.
     *
     * @param title       Titel des Programms.
     * @param description Beschreibung des Programms. Achtung, es steht nicht viel Platz zur Verfügung. An den passenden
     *                    Stellen müssen Zeilenumbrüche eingefügt werden.
     * @param easy        Die Vorauswahl der Schwierigkeitsstufe. Falls true, wir die Schwierigkeitsstufe "Easy" vorausgewählt,
     *                    ansonsten "Standard".
     * @return true falls der Spieler "Easy" gewählt hat, ansonsten false.
     */
    public boolean showSimpleStartScreen(String title, String description, boolean easy) {
        SimpleStartScreen simpleStartScreen = new SimpleStartScreen(this, title, description, easy);
        pollKeyEvents();
        simpleStartScreen.printStartScreen();
        String result = simpleStartScreen.getSelectedItem();
        if (result.equals("Close")) {
            closeGameView(true);
        }
        return simpleStartScreen.getSelectedItem().equals("Easy");
    }

    /**
     * Es wird ein Endbildschirm mit einem einfachen Auswahlmenü angezeigt: "Neu starten" und "Beenden". Falls der
     * Benutzer "Beenden" wählt, wird das Programm sofort beendet.
     *
     * @param message Nachricht, die der Benutzer angezeigt bekommt.
     */
    public void showEndScreen(String message) {
        EndScreen endScreen = new EndScreen(this, message);
        pollKeyEvents();
        endScreen.printEndScreen();
        if (!endScreen.playAgain()) {
            closeGameView(true);
        }
    }

    /**
     * Diese Methode kann bunte Block-Grafiken anzeigen. Dazu muss ein farbcodierter <code>String</code> übergeben
     * werden, der dann auf die Leinwand (Canvas) übertragen wird, ohne die bisherigen Inhalte zu löschen. Die im
     * <code>String</code> enthaltenen Buchstaben werden als Farben interpretiert. Jeder Buchstabe repräsentiert einen
     * Block mit der Größe blockSize * blockSize. Beispiel: Ein rotes Dreieck mit grüner Füllung.
     * <br>
     * <pre>
     * <code>
     * <br>
     * String dreieck =
     * "   R   \n" +
     * "  RGR  \n" +
     * " RGGGR \n" +
     * "RRRRRRR\n";
     * <br>
     * </code>
     * </pre>
     * Um die Farbcodes zu interpretieren, wird eine Farbpalette ausgewertet. Die Farben der Farbpalette lassen sich
     * über die Methode {@link #setColorForBlockImage(char, Color)} anpassen.
     * <p>
     * Es sind nur Zeichen erlaubt, die in der Farbpalette vorkommen, das Leerzeichen (Space) und Zeilenumbrüche. Das
     * Leerzeichen ist transparent, man kann den Hintergrund sehen. Zusätzlich werden Koordinaten ausgewertet: (0, 0)
     * ist links oben {@link #GameView()}.
     * Negative Koordinaten können verwendet werden um Grafiken teilweise anzuzeigen.
     * <p>
     * Die Größe der Blöcke muss mit dem Parameter <code>blockSize</code> festgelegt werden. Beispielsweise bedeutet
     * <code>blockSize = 10</code>, dass ein Block die Fläche von 10 * 10 Pixeln belegt.
     * <p>
     * Die Grafik kann mit einer Rotation dargestellt werden, dabei wird um den Mittelpunkt der Grafik rotiert. Eine
     * Rotation um 0° stellt das Bild ohne Rotation dar, bei 180° steht das Bild auf dem Kopf.
     *
     * @param blockImage Das Bild als farbcodierter String.
     * @param x          x-Koordinate, bei welcher der Text angezeigt werden soll. 0 ist links.
     * @param y          y-Koordinate, bei welcher der Text angezeigt werden soll. 0 ist oben.
     * @param blockSize  Die Größe eines einzelnen Farbblocks.
     * @param rotation   Die Rotation des Bildes in Grad um den Mittelpunkt.
     * @see #setColorForBlockImage(char, Color)
     */
    public void addBlockImageToCanvas(String blockImage, double x, double y, double blockSize, double rotation) {
        BufferedImage image = swingAdapter.createImageFromColorString(blockImage);
        addImageToCanvasIfVisible(image, x, y, blockSize, rotation);
    }

    /**
     * Schreibt den übergebenen Text auf die Leinwand (Canvas), ohne die bisherigen Inhalte zu löschen. Zusätzlich
     * werden Koordinaten ausgewertet: (0, 0) entspricht links oben {@link #GameView()}.
     * Negative Koordinaten können verwendet werden um Texte teilweise anzuzeigen. Leerzeichen sind durchsichtig
     * (Objekte im Hintergrund sind zu sehen).
     * <p>
     * In dieser Methode muss die Schriftgröße angegeben werden, dabei bedeutet z.B. </code>fontSize = 20</code>, dass
     * ein Buchstabe eine Fläche von 20 * 20 Pixeln belegt.
     * <p>
     * Die Schrift kann mit einer Rotation dargestellt werden, dabei wird um den Mittelpunkt der Grafik rotiert. Eine
     * Rotation um 0° stellt die Schrift ohne Rotation dar, bei 180° steht die Schrift auf dem Kopf.
     *
     * @param text     Der anzuzeigende Text.
     * @param x        x-Koordinate, bei welcher der Text angezeigt werden soll. 0 ist links.
     * @param y        y-Koordinate, bei welcher der Text angezeigt werden soll. 0 ist oben.
     * @param fontSize Die Schriftgröße.
     * @param color    Die Farbe, in der der Text angezeigt werden soll.
     * @param rotation Die Rotation der Schrift in Grad um den Mittelpunkt.
     */
    public void addTextToCanvas(String text, double x, double y, double fontSize, Color color, double rotation) {
        BufferedImage image = swingAdapter.createImageFromText(text, color, (int) Math.round(fontSize));
        addImageToCanvasIfVisible(image, x, y, 1, rotation);
    }

    /**
     * Erzeugt eine Grafik aus einer Datei. Die Datei muss im Verzeichnis "src/resources" liegen. Bitte den Namen des
     * Files ohne Verzeichnisnamen angeben, z.B.<code>"Raumschiff.png"</code>.
     * <p>
     * Die Grafik wird auf die Leinwand (Canvas) übertragen, ohne die bisherigen Inhalte zu löschen.
     * <p>
     * Koordinaten werden ausgewertet: (0, 0) ist links oben {@link #GameView()}.
     * Negative Koordinaten können verwendet werden um Grafiken teilweise anzuzeigen.
     * <p>
     * Die Größe der Grafik mit dem Parameter <code>scaleFactor</code> festgelegt werden. Beispielsweise bedeutet
     * <code>scaleFactor = 1</code>, dass das Bild in Originalgröße angezeigt wird.
     * <p>
     * Die Grafik kann mit einer Rotation dargestellt werden, dabei wird um den Mittelpunkt der Grafik rotiert. Eine
     * Rotation um 0° stellt das Bild ohne Rotation dar, bei 180° steht das Bild auf dem Kopf.
     *
     * @param imageFile   Das File mit dem Bild.
     * @param x           x-Koordinate, bei welcher das Bild angezeigt werden soll. 0 ist links.
     * @param y           y-Koordinate, bei welcher das Bild angezeigt werden soll. 0 ist oben.
     * @param scaleFactor Skalierungsfaktor des Bildes.
     * @param rotation    Die Rotation des Bildes in Grad um den Mittelpunkt.
     */
    public void addImageToCanvas(String imageFile, double x, double y, double scaleFactor, double rotation) {
        BufferedImage image = swingAdapter.createImageFromFile(imageFile);
        addImageToCanvasIfVisible(image, x, y, scaleFactor, rotation);
    }

    private void addImageToCanvasIfVisible(BufferedImage image, double x, double y, double scaleFactor,
                                           double rotation) {
        int xInt = (int) Math.round(x);
        int yInt = (int) Math.round(y);
        int width = (int) (image.getWidth() * scaleFactor);
        int height = (int) (image.getHeight() * scaleFactor);
        int CenterX = xInt + width / 2;
        int CenterY = yInt + height / 2;
        int size = (int) Math.round((width + height) / 2d);
        if (intersectsGameViewBounds(CenterX - size, CenterY - size, 2 * size, 2 * size, 0)) {
            canvas.addImageToCanvas(image, xInt, yInt, scaleFactor, rotation);
        }
    }

    /**
     * Diese Methode kann ein farbiges Oval auf die Leinwand (Canvas) zeichnen, ohne die bisherigen Inhalte zu löschen.
     * <p>
     * Die Koordinaten werden wie folgt ausgewertet: (0, 0) ist links oben {@link #GameView()}.
     * Negative Koordinaten können verwendet werden um Ovale teilweise anzuzeigen.
     *
     * @param xCenter    x-Koordinate des Mittelpunkts des Ovals. 0 ist links.
     * @param yCenter    y-Koordinate des Mittelpunkts des Ovals. 0 ist oben.
     * @param width      Breite des Ovals.
     * @param height     Höhe des Ovals.
     * @param lineWeight Die Linienstärke des Ovals.
     * @param filled     Legt fest, ob das Oval gefüllt werden soll oder nicht.
     * @param color      Die Farbe des Ovals.
     */
    public void addOvalToCanvas(double xCenter, double yCenter, double width, double height, double lineWeight,
                                boolean filled, Color color) {
        int xInt = (int) Math.round(xCenter - width / 2);
        int yInt = (int) Math.round(yCenter - height / 2);
        int widthInt = (int) Math.round(width);
        int heightInt = (int) Math.round(height);
        if (intersectsGameViewBounds(xInt, yInt, widthInt, heightInt, lineWeight)) {
            canvas.addOvalToCanvas((int) Math.round(xCenter), (int) Math.round(yCenter), widthInt,
                    heightInt, (int) Math.round(lineWeight), filled, color);
        }
    }

    /**
     * Diese Methode kann ein farbiges Rechteck auf die Leinwand (Canvas) zeichnen, ohne die bisherigen Inhalte zu
     * löschen.
     * <p>
     * Die Koordinaten werden wie folgt ausgewertet: (0, 0) ist links oben {@link #GameView()}.
     * Negative Koordinaten können verwendet werden um Rechtecke teilweise anzuzeigen.
     *
     * @param x          x-Koordinate des linken oberen Ecks des Rechtecks. 0 ist links.
     * @param y          y-Koordinate des linken oberen Ecks des Rechtecks. 0 ist oben.
     * @param width      Breite des Rechtecks.
     * @param height     Höhe des Rechtecks.
     * @param lineWeight Die Linienstärke.
     * @param filled     Legt fest, ob das Rechteck gefüllt werden soll oder nicht.
     * @param color      Die Farbe des Rechtecks.
     */
    public void addRectangleToCanvas(double x, double y, double width, double height, double lineWeight,
                                     boolean filled, Color color) {
        int xInt = (int) Math.round(x);
        int yInt = (int) Math.round(y);
        int widthInt = (int) Math.round(width);
        int heightInt = (int) Math.round(height);
        if (intersectsGameViewBounds(xInt, yInt, widthInt, heightInt, lineWeight)) {
            canvas.addRectangleToCanvas(xInt, yInt, widthInt, heightInt, (int) Math.round(lineWeight), filled, color);
        }
    }


    /**
     * Diese Methode kann eine farbige Linie auf die Leinwand (Canvas) zeichnen, ohne die bisherigen Inhalte zu
     * löschen.
     * <p>
     * Die Koordinaten werden wie folgt ausgewertet: (0, 0) ist links oben {@link #GameView()}.
     * Negative Koordinaten können verwendet werden um Linien teilweise anzuzeigen.
     *
     * @param xStart     x-Koordinate des Startpunkts der Linie. 0 ist links.
     * @param yStart     y-Koordinate des Startpunkts der Linie. 0 ist oben.
     * @param xEnd       x-Koordinate des Endpunkts der Linie. 0 ist links.
     * @param yEnd       y-Koordinate des Endpunkts der Linie. 0 ist oben.
     * @param lineWeight Die Linienstärke.
     * @param color      Die Farbe der Linie.
     */
    public void addLineToCanvas(double xStart, double yStart, double xEnd, double yEnd, double lineWeight,
                                Color color) {
        int xStartInt = (int) Math.round(xStart);
        int yStartInt = (int) Math.round(yStart);
        int xEndInt = (int) Math.round(xEnd);
        int yEndInt = (int) Math.round(yEnd);
        int[] xs = new int[]{xStartInt, xEndInt};
        int[] ys = new int[]{yStartInt, yEndInt};
        if (intersectsGameViewBounds(xs, ys, lineWeight)) {
            canvas.addLineToCanvas(xStartInt, yStartInt, xEndInt, yEndInt, (int) Math.round(lineWeight), color);
        }
    }

    /**
     * Diese Methode kann eine farbige Poly-Linie (eine Linie zwischen mehreren Punkten) auf die Leinwand (Canvas)
     * zeichnen, ohne die bisherigen Inhalte zu löschen. Dazu müssen alle Punkte der Poly-Linie angegeben werden.
     * <p>
     * Die Koordinaten werden wie folgt ausgewertet: (0, 0) ist links oben {@link #GameView()}.
     * Negative Koordinaten können verwendet werden um die Linien teilweise anzuzeigen.
     *
     * @param xCoordinates Die x-Koordinaten der Punkte der Poly-Linie.
     * @param yCoordinates Die y-Koordinaten der Punkte der Poly-Linie.
     * @param lineWeight   Die Linienstärke.
     * @param color        Die Farbe der Poly-Linie.
     */
    public void addPolyLineToCanvas(double[] xCoordinates, double[] yCoordinates, double lineWeight, Color color) {
        int[] xs = convertDoubleToIntArray(xCoordinates);
        int[] ys = convertDoubleToIntArray(yCoordinates);
        if (intersectsGameViewBounds(xs, ys, lineWeight)) {
            canvas.addPolyLineToCanvas(xs, ys, (int) Math.round(lineWeight), color);
        }
    }


    /**
     * Diese Methode kann ein farbiges Polygon auf die Leinwand (Canvas) zeichnen, ohne die bisherigen Inhalte zu
     * löschen. Dazu müssen alle Punkte des Polygons angegeben werden. Der Letzte angegebene Punkt wird mit dem ersten
     * Punkt des Polygons verbunden.
     * <p>
     * Die Koordinaten werden wie folgt ausgewertet: (0, 0) ist links oben {@link #GameView()}.
     * Negative Koordinaten können verwendet werden um das Polygon teilweise anzuzeigen.
     *
     * @param xCoordinates Die x-Koordinaten der Punkte des Polygons.
     * @param yCoordinates Die y-Koordinaten der Punkte des Polygons.
     * @param lineWeight   Die Linienstärke.
     * @param filled       Legt fest, ob das Polygon gefüllt werden soll oder nicht.
     * @param color        Die Farbe des Polygons.
     */
    public void addPolygonToCanvas(double[] xCoordinates, double[] yCoordinates, double lineWeight, boolean filled,
                                   Color color) {
        int[] xs = convertDoubleToIntArray(xCoordinates);
        int[] ys = convertDoubleToIntArray(yCoordinates);
        if (intersectsGameViewBounds(xs, ys, lineWeight)) {
            canvas.addPolygonToCanvas(xs, ys, (int) Math.round(lineWeight), filled, color);
        }
    }

    private boolean intersectsGameViewBounds(int[] xs, int[] ys, double lineWeight) {
        IntSummaryStatistics statX = Arrays.stream(xs).summaryStatistics();
        IntSummaryStatistics statY = Arrays.stream(ys).summaryStatistics();
        return intersectsGameViewBounds(statX.getMin(), statY.getMin(), statX.getMax() - statX.getMin(),
                statY.getMax() - statY.getMin(), lineWeight);

    }

    private boolean intersectsGameViewBounds(int x, int y, int width, int height, double lineWeight) {
        int halfLineWeight = (int) Math.round(lineWeight / 2);
        java.awt.Rectangle rect = new java.awt.Rectangle(x - halfLineWeight,
                y - halfLineWeight,
                width + halfLineWeight,
                height + halfLineWeight);
        return rect.intersects(BOUNDS);
    }

    private int[] convertDoubleToIntArray(double[] original) {
        int[] converted = new int[original.length];
        for (int i = 0; i < converted.length; i++) {
            converted[i] = (int) Math.round(original[i]);
        }
        return converted;
    }


    /**
     * Zeigt den aktuellen Inhalt der Leinwand (Canvas) im Fenster an. Nach der Ausgabe wird der Inhalt der Leinwand
     * gelöscht. Zwischen zwei Aufrufen dieser Methode werden automatisch immer mindestens 8 Millisekunden eingefügt.
     * Das führt zu einer Darstellung von höchstens 120 Bildern pro Sekunde.
     */
    public void printCanvas() {
        window.printCanvas(canvas);
    }

    /**
     * Gibt den übergebenen Text direkt im Fenster aus. Es muss die Schriftgröße gewählt werden.
     * <p>
     * Zwischen zwei Aufrufen dieser Methode werden automatisch immer mindestens 8 Millisekunden eingefügt. Das führt zu
     * einer Darstellung von höchstens 120 Bildern pro Sekunde.
     *
     * @param string   Der anzuzeigende String.
     * @param fontSize Die Schriftgröße.
     */
    public void print(String string, int fontSize) {
        setBackgroundColor(Color.BLACK);
        addTextToCanvas(string, 0, 0, fontSize, Color.WHITE, 0);
        printCanvas();
    }

    /**
     * Liefert die Zeit in Millisekunden, die seit der Erzeugung des GameView-Fensters verstrichen sind.
     *
     * @return Zeit in Millisekunden.
     */
    public int getGameTimeInMilliseconds() {
        return gameTime.getTimeInMilliseconds();
    }

    /**
     * Sets a new timer with the given duration. After the duration, the timer will expire. The timer starts
     * immediately.
     *
     * @param name     The identifier of the timer to be checked.
     * @param id       The object that issued this request.
     * @param duration The duration of the timer. After this duration the timer will expire.
     */
    public void activateTimer(String name, Object id, long duration) {
        gameTime.activateTimer(name, id, duration);
    }

    /**
     * Checks if the timer that belongs to the given identifier already has expired.
     * This method returns <code>false</code>, as long as the timer is currently not running, even if it has never been
     * started. It returns <code>true</code>, as long the timer is running.
     *
     * @param name The identifier of the timer to be checked.
     * @param id   The object that the timer belongs to.
     * @return <code>false</code>, as long as the timer is currently not running, even if it has never been
     * started. <code>true</code>, as long the timer is running.
     */
    public boolean timerIsActive(String name, Object id) {
        return gameTime.timerIsActive(name, id);
    }

    /**
     * Starts a timer with the given duration and delivers an alarm after the given time. The method <code>alarm()</code>
     * will become <code>true</code> after the timer is due. The timer starts immediately.
     *
     * @param name     The identifier of the alarm to be set.
     * @param id       The object that issued this request.
     * @param duration The duration of the timer. After this duration, the method <code>alarm()</code> will become <code>true</code>.
     */
    public void setAlarm(String name, Object id, long duration) {
        gameTime.setAlarm(name, id, duration);
    }

    /**
     * Checks if the alarm that belongs to the given identifier already has been set. It returns <code>true</code> if the
     * timer is currently set and waiting for the delivery of an alarm.
     *
     * @param name The identifier of the alarm to be checked.
     * @param id   The object that the alarm belongs to.
     * @return returns <code>true</code> if the timer is currently set and waiting for the delivery of an alarm.
     */
    public boolean alarmIsSet(String name, Object id) {
        return gameTime.alarmIsSet(name, id);
    }

    /**
     * Checks if the alarm that belongs to the given identifier is due. This method returns <code>true</code> if
     * the alarm is due. After the timer was due and <code>alarm()</code> was called, the timer will be deleted.
     *
     * @param name The identifier of the alarm to be checked.
     * @param id   The object that the alarm belongs to.
     * @return <code>true</code> if the alarm is due.
     */
    public boolean alarm(String name, Object id) {
        return gameTime.alarm(name, id);
    }

    /**
     * Cancels the alarm that belongs to the given identifier, the timer will be deleted.
     *
     * @param name The identifier of the alarm to be canceled.
     * @param id   The object that the alarm belongs to.
     */
    public void cancelAlarm(String name, Object id) {
        gameTime.cancelAlarm(name, id);
    }

    /**
     * Liefert alle Tastenereignisse, die seit dem letzten Aufruf dieser Methode aufgelaufen sind als Array zurück. Es
     * werden maximal die neuesten 25 Ereignisse zurückgegeben, alte Ereignisse werden gelöscht.
     * <p>
     * Das Array enthält Ereignisse vom Typ {@link KeyEvent}. Der Typ des Events ist entweder<br>
     * <code>KeyEvent.KEY_PRESSED</code> (Taste wurde gedrückt),<br>
     * <code>KeyEvent.KEY_RELEASED</code> (Taste wurde losgelassen)<br>
     * oder <code>KeyEvent.KEY_TYPED</code>(Taste wurde getippt, funktioniert nur für sichtbare Zeichen).
     * <p>
     * Sichtbare Zeichen lassen sich mit der Methode {@link KeyEvent#getKeyChar()} direkt auslesen.
     * <p>
     * Bei Tastenereignissen gibt es die sogenannte Anschlagverzögerung. Das bedeutet, dass wenn man eine Taste gedrückt
     * hält, dann wird die Taste einmal ausgelöst, dann folgt eine kurze Pause, dann folgt eine schnelle Wiederholung
     * des Tastendrucks. Falls dieses Verhalten nicht gewünscht ist (z.B. bei der Steuerung von Spielfiguren), sollte
     * statt dessen die Methode {@link #getKeyCodesOfCurrentlyPressedKeys()} verwendet werden.
     *
     * <pre>
     * <code>
     * <br>
     * package demo;
     *
     * import java.awt.event.KeyEvent;
     *
     * public class KeyEventTest {
     *   GameView gameView;
     *
     *   public KeyEventTest() {
     *     gameView = new GameView();
     *     loop();
     *   }
     *
     *   public void loop() {
     *     while (true) {
     *       KeyEvent[] keyEvents = gameView.pollKeyEvents();
     *       for (KeyEvent keyEvent : keyEvents) {
     *         if (keyEvent.getID() == KeyEvent.KEY_TYPED) {
     *           gameView.print("Taste: " + keyEvent.getKeyChar(), 6);
     *         }
     *       }
     *     }
     *   }
     * }
     * </code>
     * </pre>
     * <br>
     *
     * @return Alle <code>KeyEvent</code> Ereignisse seit dem letzten Aufruf dieser Methode.
     * @see KeyEvent
     * @see #getKeyCodesOfCurrentlyPressedKeys()
     */
    public KeyEvent[] pollKeyEvents() {
        return keyboard.pollKeyEvents();
    }

    /**
     * Legt fest, ob die Maus im Fenster benutzt werden soll. Falls sie nicht benutzt wird, wird der Cursor der Maus auf
     * den Default-Ansicht zurückgesetzt und die Maus wird ausgeblendet. Falls sie benutzt wird, werden Maus-Ereignisse
     * erzeugt, die verwendet werden können. Die Standardeinstellung ist <code>false</code>.
     *
     * @param useMouse Legt fest, ob die Maus im Fenster benutzt werden soll.
     */
    public void useMouse(boolean useMouse) {
        mouse.useMouse(useMouse);
    }

    /**
     * Gibt zurück, ob die Maus eingeschaltet ist.
     *
     * @return true, falls die Maus eingeschaltet ist.
     */
    public boolean isMouseEnabled() {
        return mouse.useMouse;
    }

    /**
     * Legt ein neues Symbol für den Maus-Cursor fest. Die Bild-Datei muss im Verzeichnis "src/resources" liegen. Bitte
     * den Namen der Datei ohne Verzeichnisnamen angeben, z.B. <code>setMouseCursor("Cursor.png", false)</code>.
     *
     * @param fileName Name der Bild-Datei. Die Bild-Datei muss in einem Verzeichnis "src/resources" liegen.
     * @param centered Gibt an, ob der Hotspot des Cursors in der Mitte des Symbols oder oben links liegen soll.
     */
    public void setMouseCursor(String fileName, boolean centered) {
        mouse.setMouseCursor(fileName, centered);
    }

    /**
     * Der Maus-Cursor wird auf das Standard-Icon zurückgesetzt.
     */
    public void resetMouseCursor() {
        mouse.setStandardMouseCursor();
    }

    /**
     * Falls die Maus mit {@link #useMouse(boolean)} aktiviert wurde, liefert diese Methode alle gerade im Moment
     * gedrückten Tasten als <code>KeyCode</code> der Klasse {@link KeyEvent} als Array zurück. Es handelt sich dabei
     * um Ganzzahlen vom Typ <code>Integer</code>. Die Tasten sind in der Reihenfolge enthalten, in der sie gedrückt
     * wurden. Diese
     * Methode ist geeignet um die Steuerung von Spielfiguren zu realisieren.
     * <p>
     * Ein Abgleich der KeyCodes kann über Konstanten der Klasse {@link KeyEvent} erfolgen. Beispielsweise kann die
     * Leertaste mit Hilfe der Konstante {@link KeyEvent#VK_SPACE} abgeglichen werden.
     * <pre>
     * <code>
     * <br>
     * package demo;
     *
     * import java.awt.event.KeyEvent;
     *
     * public class PressedKeys {
     *   GameView gameView;
     *
     *   public PressedKeys() {
     *     gameView = new GameView();
     *     loop();
     *   }
     *
     *   private void loop() {
     *     while (true) {
     *       Integer[] pressedKeys = gameView.getKeyCodesOfCurrentlyPressedKeys();
     *       String result = "";
     *       for (int keyCode : pressedKeys) {
     *         if (keyCode == KeyEvent.VK_UP) {
     *           result += "UP\n";
     *         } else if (keyCode == KeyEvent.VK_DOWN) {
     *           result += "Down\n";
     *         } else if (keyCode == KeyEvent.VK_LEFT) {
     *           result += "Left\n";
     *         } else if (keyCode == KeyEvent.VK_RIGHT) {
     *           result += "Right\n";
     *         } else if (keyCode == KeyEvent.VK_SPACE) {
     *           result += "Space\n";
     *         }
     *       }
     *       gameView.print(result, 6);
     *     }
     *   }
     * }
     *
     * </code>
     * </pre>
     *
     * @return Alle gerade gedrückten Tasten als <code>KeyCode</code> in einem Array.
     * @see KeyEvent
     */
    public Integer[] getKeyCodesOfCurrentlyPressedKeys() {
        return keyboard.getKeyCodesOfCurrentlyPressedKeys();
    }

    /**
     * Falls die Maus mit {@link #useMouse(boolean)} aktiviert wurde, liefert diese Methode alle Mausereignisse die seit
     * dem letzten Aufruf dieser Methode aufgelaufen sind als Array zurück. Es werden maximal die neuesten 25 Ereignisse
     * zurückgegeben, alte Ereignisse werden gelöscht. Diese Methode ist geeignet um die Texteingaben vom Benutzer zu
     * realisieren.
     * <p>
     * Das Array enthält Ereignisse vom Typ {@link MouseEvent}. Das Ereignis enthält Koordinaten auf der Leinwand
     * (Canvas) und die Information ob die Maus gedrückt, losgelassen, geklickt oder nur bewegt wurde. Um festzustellen,
     * wie die Maus betätigt wurde, kann der Typ des Ereignisses mit {@link MouseEvent#getID()} abgefragt werden.
     * Folgende Konstanten werden weitergeleitet:
     * <br>
     * <code>MouseEvent.MOUSE_PRESSED</code> <br>
     * <code>MouseEvent.MOUSE_RELEASED</code> <br>
     * <code>MouseEvent.MOUSE_CLICKED</code> <br>
     * <code>MouseEvent.MOUSE_MOVED</code> <br>
     * <br>
     * Die Fensterkoordinaten können mit den Methoden<br> {@link MouseEvent#getX()} = X-Koordinate<br> {@link
     * MouseEvent#getY()} = Y-Koordinate<br> abgerufen werden, um X und Y-Koordinate des Ereignisses zu bestimmen.<br>
     * <br>
     * Beispiel zur Erkennung einer gedrückten Maustaste:<br>
     *
     * <pre>
     * <code>
     * <br>
     * package demo;
     *
     * import java.awt.event.MouseEvent;
     *
     * public class MouseEventTest {
     *   GameView gameView;
     *
     *   public MouseEventTest() {
     *     gameView = new GameView();
     *     gameView.useMouse(true);
     *     loop();
     *   }
     *
     *   public void loop() {
     *     int x = 0;
     *     int y = 0;
     *     while(true) {
     *       MouseEvent[] mouseEvents = gameView.pollMouseEvents();
     *       for (MouseEvent mouseEvent : mouseEvents) {
     *         if (mouseEvent.getID() == MouseEvent.MOUSE_PRESSED) {
     *           x = mouseEvent.getX();
     *           y = mouseEvent.getY();
     *         }
     *       }
     *       gameView.addTextToCanvas("X=" + x + " Y=" + y, x, y, 12, Color.WHITE);
     *       gameView.printCanvas();
     *     }
     *   }
     * }
     * </code>
     * </pre>
     * <p>
     * Mit {@link MouseEvent#getButton()} ()} lässt sich ermitteln, welche Maustaste betätigt wurde (links, rechts oder
     * die Mitte).
     *
     * @return Alle Mausereignisse seit dem letzten Aufruf dieser Methode.
     * @see MouseEvent
     */
    public MouseEvent[] pollMouseEvents() {
        return mouse.pollMouseEvents();
    }

    /**
     * Spielt einen Sound ab (z.B. eine wav.-Datei). Das Soundfile muss in einem Verzeichnis "src/resources" liegen.
     * Bitte den Namen des Files ohne Verzeichnisnamen angeben, z.B. <code>playSound("sound.wav", false)</code>. Der
     * Sound beendet sich selbst, sobald er fertig abgespielt wurde. Der Parameter "replay" kann genutzt werden, um den
     * Sound endlos zu wiederholen. Mit der Methode {@link #stopSound(int)} kann ein Sound frühzeitig beendet werden.
     * Mit der Methode {@link #stopAllSounds()} können alle laufenden Sounds beendet werden. Achten Sie auf Groß- und
     * Kleinschreibung beim Soundfile!
     *
     * @param sound  Name des Soundfiles. Das Soundfile muss in einem Verzeichnis "src/resources" liegen.
     * @param replay Legt fest, ob der Sound endlos wiederholt werden soll.
     * @return Die eindeutige Identifikationsnummer des Soundfiles wird zurückgegeben. Diese Nummer kann genutzt werden
     * um mit der Methode {@link #stopSound(int)} das Abspielen des Sounds zu beenden.
     */
    public int playSound(String sound, boolean replay) {
        return this.sound.playSound(sound, replay);
    }

    /**
     * Stoppt den Sound mit der angegebenen Nummer. Falls der Sound schon gestoppt wurde, passiert nichts.
     *
     * @param id Die eindeutige Identifikationsnummer des Soundfiles, das gestoppt werden soll.
     */
    public void stopSound(int id) {
        sound.stopSound(id);
    }

    /**
     * Stoppt alle gerade spielenden Sounds.
     */
    public void stopAllSounds() {
        sound.stopAllSounds();
    }

    /**
     * Schließt entweder nur das GameView-Fenster oder die ganze Anwendung.
     *
     * @param terminateEverything Wenn <code>true</code> ausgewählt wird, wird die komplette Anwendung mit
     *                            <code>System.exit(0)</code>beendet. Ansonsten wird nur das Fenster geschlossen.
     */
    public void closeGameView(boolean terminateEverything) {
        window.closeWindow(terminateEverything);
    }

    private static class GameTime {

        private final long startTimeInMilliseconds;
        private final HashMap<Long, Long> timers;
        private final HashMap<Long, Long> alarms;

        private GameTime() {
            this.startTimeInMilliseconds = System.currentTimeMillis();
            this.timers = new HashMap<>(200);
            this.alarms = new HashMap<>(200);
        }

        private int getCurrentTime() {
            return (int) (System.currentTimeMillis() - startTimeInMilliseconds);
        }

        private int getTimeInMilliseconds() {
            return getCurrentTime();
        }

        private void activateTimer(String name, Object id, long duration) {
            timers.put((long) name.hashCode() + System.identityHashCode(id), getCurrentTime() + duration);
        }

        private boolean timerIsActive(String name, Object id) {
            Long timerId = (long) name.hashCode() + System.identityHashCode(id);
            Long startTime = timers.get(timerId);
            if (startTime == null) {
                return false;
            }
            boolean expired = startTime - getCurrentTime() <= 0;
            if (expired) {
                timers.remove(timerId);
            }
            return !expired;
        }

        private void setAlarm(String name, Object id, long duration) {
            alarms.put((long) name.hashCode() + System.identityHashCode(id), getCurrentTime() + duration);
        }

        private boolean alarmIsSet(String name, Object id) {
            Long alarmId = (long) name.hashCode() + System.identityHashCode(id);
            return alarms.containsKey(alarmId);
        }

        private boolean alarm(String name, Object id) {
            Long alarmId = (long) name.hashCode() + System.identityHashCode(id);
            Long startTime = alarms.get(alarmId);
            if (startTime == null) {
                return false;
            }
            boolean expired = startTime - getCurrentTime() <= 0;
            if (expired) {
                alarms.remove(alarmId);
            }
            return expired;
        }

        public void cancelAlarm(String name, Object id) {
            Long alarmId = (long) name.hashCode() + System.identityHashCode(id);
            alarms.remove(alarmId);
        }
    }

    private static class PrintObject {
        int x;
        int y;
        Color color;

        public PrintObject(int x, int y, Color color) {
            this.x = x;
            this.y = y;
            this.color = color;
        }
    }

    private static class Oval extends PrintObject {
        int width;
        int height;
        int lineWeight;
        boolean filled;

        public Oval(int xCenter, int yCenter, int width, int height, int lineWeight, boolean filled, Color color) {
            super(xCenter, yCenter, color);
            this.width = width;
            this.height = height;
            this.lineWeight = lineWeight;
            this.filled = filled;
        }
    }

    private static class Rectangle extends PrintObject {
        int width;
        int height;
        int lineWeight;
        boolean filled;

        public Rectangle(int x, int y, int width, int height, int lineWeight, boolean filled, Color color) {
            super(x, y, color);
            this.width = width;
            this.height = height;
            this.lineWeight = lineWeight;
            this.filled = filled;
        }
    }

    private static class Line extends PrintObject {
        int xEnd;
        int yEnd;
        int lineWeight;

        public Line(int xStart, int yStart, int xEnd, int yEnd, int lineWeight, Color color) {
            super(xStart, yStart, color);
            this.xEnd = xEnd;
            this.yEnd = yEnd;
            this.lineWeight = lineWeight;
        }
    }

    private static class Polygon extends PrintObject {
        int[] xCoordinates;
        int[] yCoordinates;
        int lineWeight;
        boolean filled;

        public Polygon(int[] xCoordinates, int[] yCoordinates, int lineWeight, boolean filled, Color color) {
            super(xCoordinates[0], yCoordinates[0], color);
            this.xCoordinates = xCoordinates;
            this.yCoordinates = yCoordinates;
            this.lineWeight = lineWeight;
            this.filled = filled;
        }
    }

    private static class PolyLine extends PrintObject {
        int[] xCoordinates;
        int[] yCoordinates;
        int lineWeight;

        public PolyLine(int[] xCoordinates, int[] yCoordinates, int lineWeight, Color color) {
            super(xCoordinates[0], yCoordinates[0], color);
            this.xCoordinates = xCoordinates;
            this.yCoordinates = yCoordinates;
            this.lineWeight = lineWeight;
        }
    }

    private static class ImageObject extends PrintObject {
        BufferedImage image;
        double scaleFactor;
        double rotation;

        public ImageObject(int x, int y, BufferedImage image, double scaleFactor, double rotation) {
            super(x, y, Color.BLACK);
            this.scaleFactor = scaleFactor;
            this.image = image;
            this.rotation = rotation;
        }
    }

    private static class Canvas implements Cloneable {
        private Color backgroundColor;
        private final ArrayList<PrintObject> printObjects;

        Canvas() {
            this.backgroundColor = Color.black;
            this.printObjects = new ArrayList<>(30000);
        }

        void setBackgroundColor(Color backgroundColor) {
            this.backgroundColor = backgroundColor;
        }

        Color getBackgroundColor() {
            return backgroundColor;
        }

        ArrayList<PrintObject> getPrintObjects() {
            return printObjects;
        }

        public void addImageToCanvas(BufferedImage image, int x, int y, double scaleFactor, double rotation) {
            printObjects.add(new ImageObject(x, y, image, scaleFactor, rotation));
        }

        void addRectangleToCanvas(int x, int y, int width, int height, int lineWeight, boolean filled, Color color) {
            printObjects.add(new Rectangle(x, y, width, height, lineWeight, filled, color));
        }

        void addLineToCanvas(int xStart, int yStart, int xEnd, int yEnd, int lineWeight, Color color) {
            printObjects.add(new Line(xStart, yStart, xEnd, yEnd, lineWeight, color));
        }

        void addOvalToCanvas(int xCenter, int yCenter, int width, int height, int lineWeight, boolean filled,
                             Color color) {
            printObjects.add(new Oval(xCenter, yCenter, width, height, lineWeight, filled, color));
        }

        void addPolygonToCanvas(int[] xCoordinates, int[] yCoordinates, int lineWeight, boolean filled, Color color) {
            if (xCoordinates.length != yCoordinates.length) {
                throw new InputMismatchException("Die Anzahl der X- und Y-Koordinaten ist nicht gleich!");
            }
            printObjects.add(new Polygon(xCoordinates, yCoordinates, lineWeight, filled, color));
        }

        void addPolyLineToCanvas(int[] xCoordinates, int[] yCoordinates, int lineWeight, Color color) {
            if (xCoordinates.length != yCoordinates.length) {
                throw new InputMismatchException("Die Anzahl der X- und Y-Koordinaten ist nicht gleich!");
            }
            printObjects.add(new PolyLine(xCoordinates, yCoordinates, lineWeight, color));
        }
    }

    private static class Frame extends JFrame {

        private Mouse mouse;
        private Keyboard keyboard;

        private final JPanel statusBar;
        private JLabel statusLabelLinks;

        void registerListeners(Mouse mouse, Keyboard keyboard) {
            // Klassen
            this.mouse = mouse;
            this.keyboard = keyboard;
        }

        Frame(PaintingPanel paintingPanel) {

            statusBar = new JPanel() {
                {
                    setLayout(new BorderLayout());
                    setBorder(BorderFactory.createRaisedBevelBorder());
                    setBackground(Color.WHITE);
                    setForeground(Color.BLACK);
                    statusLabelLinks = new JLabel();
                    statusLabelLinks.setBackground(Color.WHITE);
                    statusLabelLinks.setForeground(Color.BLACK);
                    statusLabelLinks.setHorizontalAlignment(JLabel.LEFT);

                    JLabel statusLabelRechts = new JLabel(Version.getStatusSignature());
                    statusLabelRechts.setBackground(Color.WHITE);
                    statusLabelRechts.setForeground(Color.BLACK);
                    statusLabelRechts.setHorizontalAlignment(JLabel.RIGHT);
                    add(statusLabelLinks, BorderLayout.WEST);
                    add(statusLabelRechts, BorderLayout.EAST);
                }
            };

            JPanel center = new JPanel(new GridBagLayout());
            center.setBackground(Color.BLACK);
            center.add(paintingPanel);


            // Struktur
            paintingPanel.setPreferredSize(new Dimension(GameView.WIDTH, GameView.HEIGHT));
            JPanel textPanelAndStatusBar = new JPanel(new BorderLayout(5, 5));
            textPanelAndStatusBar.setBackground(Color.BLACK);
            textPanelAndStatusBar.add(new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)), BorderLayout.NORTH);
            textPanelAndStatusBar.add(new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)), BorderLayout.EAST);
            textPanelAndStatusBar.add(new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)), BorderLayout.WEST);
            textPanelAndStatusBar.add(center, BorderLayout.CENTER);
            textPanelAndStatusBar.add(statusBar, BorderLayout.SOUTH);
            add(textPanelAndStatusBar);

            // Eigenschaften
            setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            setTitle(Version.getStandardTitle());
            paintingPanel.requestFocus();
            setResizable(true);


            // Listeners
            addKeyListener(new KeyListener() {

                @Override
                public void keyTyped(KeyEvent keyEvent) {
                    all(keyEvent);
                }

                @Override
                public void keyReleased(KeyEvent keyEvent) {
                    all(keyEvent);
                }

                @Override
                public void keyPressed(KeyEvent keyEvent) {
                    all(keyEvent);
                }

                private void all(KeyEvent keyEvent) {
                    if (keyboard != null) {
                        keyboard.update(keyEvent);
                    }
                }
            });
            MouseAdapter mouseAdapter = new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent mouseEvent) {
                    all(mouseEvent);
                }

                @Override
                public void mousePressed(MouseEvent mouseEvent) {
                    all(mouseEvent);
                }

                @Override
                public void mouseMoved(MouseEvent mouseEvent) {
                    all(mouseEvent);
                }

                @Override
                public void mouseClicked(MouseEvent mouseEvent) {
                    all(mouseEvent);
                }

                private void all(MouseEvent mouseEvent) {
                    if (mouse != null) {
                        mouse.update(mouseEvent);
                    }
                }
            };
            paintingPanel.addMouseMotionListener(mouseAdapter);
            paintingPanel.addMouseListener(mouseAdapter);

            final Timer packTimer = new Timer(500, actionEvent -> {
                if (getExtendedState() != MAXIMIZED_BOTH) {
                    Point location = getLocation();
                    pack();
                    setLocation(location);
                }
            });
            packTimer.setRepeats(false);

            addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    super.componentResized(e);
                    double scalingFactor = Math.min(paintingPanel.getParent().getWidth() * 1d / GameView.WIDTH,
                            paintingPanel.getParent().getHeight() * 1d / GameView.HEIGHT);
                    int newWidth = (int) Math.round(GameView.WIDTH * scalingFactor);
                    int newHeight = (int) Math.round(GameView.HEIGHT * scalingFactor);
                    paintingPanel.setPreferredSize(new Dimension(newWidth, newHeight));
                    paintingPanel.setMinimumSize(new Dimension(newWidth, newHeight));
                    paintingPanel.setSize(new Dimension(newWidth, newHeight));
                    paintingPanel.setMaximumSize(new Dimension(newWidth, newHeight));
                    if (packTimer.isRunning()) {
                        packTimer.restart();
                    } else {
                        packTimer.start();
                    }
                    revalidate();
                }
            });

            // Location und Ausgeben
            int newWidth = (int) (GameView.WIDTH * 1.3);
            int newHeight = (int) (GameView.HEIGHT * 1.3);
            paintingPanel.setPreferredSize(new Dimension(newWidth, newHeight));

            pack();
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            if (getHeight() > screenSize.height || getWidth() > screenSize.width) {
                newWidth = GameView.WIDTH * 8 / 10;
                newHeight = GameView.HEIGHT * 8 / 10;
                paintingPanel.setPreferredSize(new Dimension(newWidth, newHeight));
                pack();
            }
            setLocationRelativeTo(null);
            setVisible(true);
        }

        JLabel getStatusLabelLinks() {
            return statusLabelLinks;
        }

        JPanel getStatusBar() {
            return statusBar;
        }
    }

    private static class Keyboard {
        private final ArrayBlockingQueue<KeyEvent> keyboardEvents;
        private final ArrayBlockingQueue<Integer> keyCodesOfCurrentlyPressedKeys;

        private final static int KEY_EVENT_BUFFER_SIZE = 25;

        Keyboard() {
            keyboardEvents = new ArrayBlockingQueue<>(KEY_EVENT_BUFFER_SIZE, true);
            keyCodesOfCurrentlyPressedKeys = new ArrayBlockingQueue<>(10, true);
        }

        void update(KeyEvent keyEvent) {
            if (keyboardEvents.size() == KEY_EVENT_BUFFER_SIZE) {
                keyboardEvents.remove();
            }
            keyboardEvents.add(keyEvent);
            if (keyEvent.getID() == KeyEvent.KEY_PRESSED) {
                if (!keyCodesOfCurrentlyPressedKeys.contains(keyEvent.getKeyCode()))
                    keyCodesOfCurrentlyPressedKeys.add(keyEvent.getKeyCode());
            } else if (keyEvent.getID() == KeyEvent.KEY_RELEASED) {
                keyCodesOfCurrentlyPressedKeys.remove(keyEvent.getKeyCode());
            }
        }

        KeyEvent[] pollKeyEvents() {
            KeyEvent[] events = new KeyEvent[0];
            if (keyboardEvents.size() > 0) {
                events = keyboardEvents.toArray(events);
                keyboardEvents.clear();
            }
            return events;
        }

        Integer[] getKeyCodesOfCurrentlyPressedKeys() {
            Integer[] keyCodes = new Integer[0];
            if (keyCodesOfCurrentlyPressedKeys.size() > 0) {
                keyCodes = keyCodesOfCurrentlyPressedKeys.toArray(keyCodes);
            }
            return keyCodes;
        }
    }

    private static class Mouse implements ActionListener {
        private final SwingAdapter swingAdapter;

        private boolean invisibleMouseCursor;
        private boolean invisibleMouseCursorMoved;
        private final Timer invisibleMouseTimer;

        private final static int MOUSE_EVENT_BUFFER_SIZE = 25;
        private final ArrayBlockingQueue<MouseEvent> mousePointerEvents;

        private boolean useMouse;

        Mouse(SwingAdapter swingAdapter) {
            this.swingAdapter = swingAdapter;
            this.invisibleMouseCursor = false;
            this.invisibleMouseCursorMoved = true;
            this.mousePointerEvents = new ArrayBlockingQueue<>(MOUSE_EVENT_BUFFER_SIZE, true);
            this.invisibleMouseTimer = new Timer(500, this);
            setMouseInvisible();
        }

        private void setMouseInvisible() {
            this.useMouse = false;
            setInvisibleMouseCursor();
            if (!invisibleMouseTimer.isRunning()) {
                invisibleMouseTimer.start();
            }
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            if (invisibleMouseCursorMoved) {
                if (invisibleMouseCursor) {
                    setStandardMouseCursor();
                }
                invisibleMouseCursorMoved = false;
            } else {
                if (!invisibleMouseCursor) {
                    setInvisibleMouseCursor();
                }
            }
        }

        void useMouse(boolean useMouse) {
            if (useMouse == this.useMouse) {
                return;
            }
            if (useMouse) {
                this.useMouse = true;
                setStandardMouseCursor();
                invisibleMouseTimer.stop();
            } else {
                setMouseInvisible();
            }
        }

        void setStandardMouseCursor() {
            this.invisibleMouseCursor = false;
            swingAdapter.setStandardMouseCursor();
        }

        void setMouseCursor(String cursorImageFile, boolean centered) {
            this.invisibleMouseCursor = false;
            swingAdapter.setMouseCursor(cursorImageFile, centered);
        }

        private void setInvisibleMouseCursor() {
            invisibleMouseCursor = true;
            swingAdapter.setInvisibleMouseCursor();
        }

        void update(MouseEvent mouseEvent) {
            if (useMouse) {
                int mouseEventY = GameView.HEIGHT * mouseEvent.getY() / swingAdapter.getTextDisplaySize().height;
                int mouseEventX = GameView.WIDTH * mouseEvent.getX() / swingAdapter.getTextDisplaySize().width;
                MouseEvent fixedMouseEvent = new MouseEvent(mouseEvent.getComponent(), mouseEvent.getID(),
                        mouseEvent.getWhen(), mouseEvent.getModifiersEx(),
                        mouseEventX, mouseEventY, mouseEvent.getClickCount(),
                        mouseEvent.isPopupTrigger(), mouseEvent.getButton());
                if (mousePointerEvents.size() == MOUSE_EVENT_BUFFER_SIZE) {
                    mousePointerEvents.remove();
                }
                mousePointerEvents.add(fixedMouseEvent);
            } else {
                invisibleMouseCursorMoved = true;
            }
        }

        MouseEvent[] pollMouseEvents() {
            MouseEvent[] events = new MouseEvent[0];
            if (mousePointerEvents.size() > 0) {
                events = mousePointerEvents.toArray(events);
                mousePointerEvents.clear();
            }
            return events;
        }
    }

    private static class Sound {
        private final ConcurrentHashMap<Integer, Clip> clips;
        private final ConcurrentHashMap<String, byte[]> storedSoundBytes;
        private final ConcurrentHashMap<String, Clip> storedClips;
        private static int soundCounter;

        Sound() {
            clips = new ConcurrentHashMap<>();
            storedSoundBytes = new ConcurrentHashMap<>();
            storedClips = new ConcurrentHashMap<>();
            soundCounter = 0;
        }

        private void readSoundBytesAndStoreAsBytesAndClip(String sound) {
            if (!storedSoundBytes.containsKey(sound)) {
                try (InputStream stream = GameView.class.getResourceAsStream("/resources/" + sound)) {
                    storedSoundBytes.put(sound, Objects.requireNonNull(stream).readAllBytes());
                } catch (IOException e) {
                    System.err.println("Soundfile \"" + sound + "\" konnte nicht gelesen werden!");
                    e.printStackTrace();
                    System.exit(1);
                }
            }
            byte[] soundBytes = storedSoundBytes.get(sound);
            Clip clip = retrieveNewOpenedClipFromSoundBytes(sound, soundBytes);
            addLineListener(sound.hashCode(), clip);
            storedClips.put(sound, clip);
        }

        private void addLineListener(Integer id, Clip clip) {
            clip.addLineListener(event -> {
                if (event.getType().equals(LineEvent.Type.STOP)) {
                    Clip finished = clips.remove(id);
                    if (!storedClips.contains(finished)) {
                        new Thread(() -> {
                            finished.flush();
                            finished.close();
                        }).start();
                    }
                }
            });
        }

        private Clip retrieveNewOpenedClipFromSoundBytes(String sound, byte[] soundBytes) {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(soundBytes);
            try (AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(byteArrayInputStream)) {
                Clip clip = AudioSystem.getClip();
                clip.open(audioInputStream);
                return clip;
            } catch (Exception e) {
                System.err.println("Soundfile \"" + sound + "\" konnte nicht abgespielt werden!");
                e.printStackTrace();
                System.exit(1);
            }
            return null;
        }

        int playSound(String sound, boolean replay) {
            Clip clip = storedClips.get(sound);
            if (clip == null) {
                // Longest runtime, but only once per sound.
                int id = sound.hashCode();
                new Thread(() -> readClipFromDiskAndPlay(id, sound, replay)).start();
                return id;
            } else {
                if (clip.isActive()) {
                    // Possibly long runtime, but only when sound is simultaneously started

                    return 0;
                } else {
                    // Very short runtime.
                    int id = sound.hashCode();
                    clip.setMicrosecondPosition(0);
                    play(id, clip, replay);
                    return id;
                }
            }
        }

        private void readClipFromDiskAndPlay(int id, String sound, boolean replay) {
            readSoundBytesAndStoreAsBytesAndClip(sound);
            Clip clip = storedClips.get(sound);
            play(id, clip, replay);
        }

        private void createNewClipFromStoredBytesAndPlay(int id, String sound, boolean replay) {
            Clip clip = retrieveNewOpenedClipFromSoundBytes(sound, storedSoundBytes.get(sound));
            addLineListener(id, clip);
            play(id, clip, replay);
        }

        private void play(int id, Clip clip, boolean replay) {
            clips.put(id, clip);
            if (replay) {
                clip.loop(Clip.LOOP_CONTINUOUSLY);
            } else {
                clip.start();
            }
        }

        void stopSound(int id) {
            Clip toStop = clips.get(id);
            if (toStop != null) {
                toStop.stop();
            } else {
                new Thread(() -> {
                    while (clips.containsKey(id) && clips.get(id) == null) {
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException ignored) {
                        }
                    }
                    Clip clip = clips.get(id);
                    if (clip != null) {
                        clip.stop();
                    }
                }).start();
            }
        }

        void stopAllSounds() {
            for (Integer id : clips.keySet()) {
                stopSound(id);
            }
        }
    }

    private static class SwingAdapter {

        private final PaintingPanel paintingPanel;
        private final Frame frame;
        private Sound sound;
        private Mouse mouse;
        private final Font font;
        private BufferedImage bufferedImage;
        private final BufferedImage[] bufferedImages;
        private int currentBufferedImage;
        private Graphics2D g2D;
        private HashMap<Character, Color> colorMap;
        private final HashMap<Integer, BufferedImage> imageMap;
        private double sizeOfImageMapInMB;
        private final static int IMAGE_MAP_LIMIT_IN_MB = 1000;

        SwingAdapter() {
            this.paintingPanel = new PaintingPanel();
            this.frame = new Frame(paintingPanel);
            this.bufferedImages = new BufferedImage[5];
            this.currentBufferedImage = 0;
            for (int i = 0; i < bufferedImages.length; i++) {
                bufferedImages[i] = new BufferedImage(GameView.WIDTH, GameView.HEIGHT, BufferedImage.TYPE_INT_RGB);
            }
            this.bufferedImage = bufferedImages[currentBufferedImage];
            this.g2D = bufferedImage.createGraphics();
            Map<TextAttribute, Object> fontMap = new HashMap<>();
            fontMap.put(TextAttribute.FAMILY, "Monospaced");
            fontMap.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
            this.font = new Font(fontMap);
            initColorMap();
            this.imageMap = new HashMap<>();
        }

        public void setColorForBlockImage(char character, Color color) {
            colorMap.put(character, color);
        }

        void registerListeners(Mouse mouse, Keyboard keyboard, Sound sound) {
            frame.registerListeners(mouse, keyboard);
            this.sound = sound;
            this.mouse = mouse;
        }

        private void initColorMap() {
            colorMap = new HashMap<>();
            colorMap.put('R', Color.RED);
            colorMap.put('r', Color.RED.darker());
            colorMap.put('G', Color.GREEN);
            colorMap.put('g', Color.GREEN.darker());
            colorMap.put('B', Color.BLUE);
            colorMap.put('b', Color.BLUE.darker());
            colorMap.put('Y', Color.YELLOW);
            colorMap.put('y', Color.YELLOW.darker());
            colorMap.put('P', Color.PINK);
            colorMap.put('p', Color.PINK.darker());
            colorMap.put('C', Color.CYAN);
            colorMap.put('c', Color.CYAN.darker());
            colorMap.put('M', Color.MAGENTA);
            colorMap.put('m', Color.MAGENTA.darker());
            colorMap.put('O', Color.ORANGE);
            colorMap.put('o', Color.ORANGE.darker());
            colorMap.put('W', Color.WHITE);
            colorMap.put('L', Color.BLACK);
        }

        // Anzeige
        void setStatusText(String statusText) {
            SwingUtilities.invokeLater(() -> {
                frame.getStatusLabelLinks().setText(statusText);
                int minWidth = frame.getStatusBar().getPreferredSize().width + 50;
                frame.setMinimumSize(new Dimension(minWidth, minWidth / WIDTH * HEIGHT));
            });
        }

        void printToDisplay(ArrayList<PrintObject> printObjects, Color backgroundColor) {
            currentBufferedImage = currentBufferedImage < bufferedImages.length - 1 ? ++currentBufferedImage : 0;
            this.bufferedImage = bufferedImages[currentBufferedImage];
            createImageFromPrintObjects(printObjects, backgroundColor);
            paintingPanel.bufferedImage = bufferedImage;
            paintingPanel.repaint();
        }

        private void createImageFromPrintObjects(ArrayList<PrintObject> printObjects, Color backgroundColor) {
            g2D = bufferedImage.createGraphics();
            g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2D.setColor(backgroundColor);
            g2D.fillRect(0, 0, GameView.WIDTH, GameView.HEIGHT);
            char[] chars;
            for (PrintObject p : printObjects) {
                if (p.color != null) {
                    g2D.setColor(p.color);
                    if (p.getClass() == Oval.class) {
                        Oval oval = (Oval) p;
                        int x = oval.x - oval.width / 2;
                        int y = oval.y - oval.height / 2;
                        if (oval.filled) {
                            g2D.fillOval(x, y, oval.width + oval.lineWeight, oval.height + oval.lineWeight);
                        } else {
                            g2D.setStroke(new BasicStroke(oval.lineWeight));
                            g2D.drawOval(x + oval.lineWeight / 2, y + oval.lineWeight / 2, oval.width, oval.height);
                        }
                    } else if (p.getClass() == Line.class) {
                        Line line = (Line) p;
                        g2D.setStroke(new BasicStroke(line.lineWeight));
                        g2D.drawLine(line.x, line.y, line.xEnd, line.yEnd);
                    } else if (p.getClass() == Rectangle.class) {
                        Rectangle rectangle = (Rectangle) p;
                        if (rectangle.filled) {
                            g2D.fillRect(rectangle.x, rectangle.y, rectangle.width + rectangle.lineWeight,
                                    rectangle.height + rectangle.lineWeight);
                        } else {
                            g2D.setStroke(new BasicStroke(rectangle.lineWeight));
                            g2D.drawRect(rectangle.x + rectangle.lineWeight / 2,
                                    rectangle.y + rectangle.lineWeight / 2, rectangle.width, rectangle.height);
                        }
                    } else if (p.getClass() == Polygon.class) {
                        Polygon polygon = (Polygon) p;
                        if (polygon.filled) {
                            g2D.fillPolygon(polygon.xCoordinates, polygon.yCoordinates, polygon.xCoordinates.length);
                        } else {
                            g2D.setStroke(new BasicStroke(polygon.lineWeight));
                            g2D.drawPolygon(polygon.xCoordinates, polygon.yCoordinates, polygon.xCoordinates.length);
                        }
                    } else if (p.getClass() == PolyLine.class) {
                        PolyLine polyLine = (PolyLine) p;
                        g2D.setStroke(new BasicStroke(polyLine.lineWeight));
                        g2D.drawPolyline(polyLine.xCoordinates, polyLine.yCoordinates, polyLine.xCoordinates.length);
                    } else if (p.getClass() == ImageObject.class) {
                        ImageObject imageObject = (ImageObject) p;
                        AffineTransform trans = g2D.getTransform();
                        trans.translate(imageObject.x, imageObject.y);
                        trans.scale(imageObject.scaleFactor, imageObject.scaleFactor);
                        trans.rotate(Math.toRadians(imageObject.rotation), imageObject.image.getWidth() / 2.0,
                                imageObject.image.getHeight() / 2.0);
                        g2D.drawImage(imageObject.image, trans, null);
                    }
                }
            }
            g2D.dispose();
        }

        BufferedImage createImageFromFile(String imageFileName) {
            int hash = imageFileName.hashCode();
            BufferedImage image = imageMap.get(hash);
            if (image == null) {
                try {
                    image = ImageIO.read(GameView.class.getResource("/resources/" + imageFileName));
                } catch (Exception e) {
                    e.printStackTrace();
                    System.err.println("Symbolfile \"" + imageFileName + "\" konnte nicht gefunden werden!");
                    System.exit(1);
                }
                addImageToMapOrClearMap(hash, image);
            }
            return image;
        }

        BufferedImage createImageFromColorString(String colorString) {
            int hash = colorString.hashCode();
            BufferedImage image = imageMap.get(hash);
            if (image == null) {
                String[] lines = colorString.split("\\R");
                int height = lines.length;
                int width = Arrays.stream(lines).mapToInt(String::length).max().orElse(1);
                image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB_PRE);
                Graphics2D g2D = image.createGraphics();
                for (int i = 0; i < lines.length; i++) {
                    char[] blocks = lines[i].toCharArray();
                    for (int j = 0; j < blocks.length; j++) {
                        Color color = colorMap.get(blocks[j]);
                        if (color != null) {
                            g2D.setColor(color);
                            g2D.fillRect(j, i, 1, 1);
                        }
                    }
                }
                g2D.dispose();
                addImageToMapOrClearMap(hash, image);
            }
            return image;
        }

        BufferedImage createImageFromText(String text, Color color, int fontSize) {
            int hash = Objects.hash(text, color, fontSize);
            BufferedImage image = imageMap.get(hash);
            if (image == null) {
                String[] lines = text.split("\\R");
                int height = lines.length * fontSize;
                int width = Arrays.stream(lines).mapToInt(String::length).max().orElse(1) * fontSize;
                image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB_PRE);
                Graphics2D imageG2D = image.createGraphics();
                Font imageFont = this.font.deriveFont((float) fontSize);
                imageG2D.setFont(imageFont);
                FontMetrics imageFontMetrics = imageG2D.getFontMetrics(imageFont);
                char[] chars;
                imageG2D.setColor(color);
                for (int i = 0; i < lines.length; i++) {
                    chars = lines[i].toCharArray();
                    for (int c = 0; c < chars.length; c++) {
                        imageG2D.drawChars(chars, c, 1,
                                (fontSize * c) + (fontSize - imageFontMetrics.charWidth('W')) / 2,
                                (i * fontSize) +
                                (fontSize + imageFontMetrics.getAscent() - imageFontMetrics.getDescent()
                                 - fontSize / 10) / 2);
                    }
                }
                g2D.dispose();
                addImageToMapOrClearMap(hash, image);
            }
            return image;
        }

        private void addImageToMapOrClearMap(int hash, BufferedImage image) {
            if (sizeOfImageMapInMB > IMAGE_MAP_LIMIT_IN_MB) {
                imageMap.clear();
                sizeOfImageMapInMB = 0;
            }
            imageMap.put(hash, image);
            sizeOfImageMapInMB += image.getHeight() * image.getWidth() * 0.000004;
        }

        // Fenster-Dekorationen
        void setTitle(String title) {
            frame.setTitle(title);
        }

        void setWindowIcon(String windowIcon) {
            Image fensterSymbol = null;
            try {
                fensterSymbol = new ImageIcon(GameView.class.getResource("/resources/" + windowIcon)).getImage();
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Symbolfile \"" + windowIcon + "\" konnte nicht gefunden werden!");
            }
            frame.setIconImage(fensterSymbol);
        }

        // Maus Cursor
        void setMouseCursor(String cursor, boolean centered) {
            try {
                Image im = new ImageIcon(GameView.class.getResource("/resources/" + cursor)).getImage();
                SwingUtilities.invokeLater(() -> paintingPanel.setCursor(createCursor(im, centered)));
            } catch (Exception e) {
                System.out.println("Cursor-Datei konnte nicht gefunden werden!");
                System.exit(1);
            }
        }

        private Cursor createCursor(Image im, boolean centered) {
            Toolkit toolkit = paintingPanel.getToolkit();
            Dimension cursorSize = Toolkit.getDefaultToolkit().getBestCursorSize(64, 64);
            Point cursorHotSpot = new Point(0, 0);
            if (centered) {
                cursorHotSpot = new Point(cursorSize.width / 2, cursorSize.height / 2);
            }
            return toolkit.createCustomCursor(im, cursorHotSpot, "Cross");
        }

        void setStandardMouseCursor() {
            SwingUtilities.invokeLater(() -> paintingPanel.setCursor(Cursor.getDefaultCursor()));
        }

        void setInvisibleMouseCursor() {
            Image im = new ImageIcon("").getImage();
            SwingUtilities.invokeLater(() -> paintingPanel.setCursor(createCursor(im, false)));
        }

        // Beenden
        void closeWindow(boolean terminateEverything) {
            frame.dispose();
            sound.stopAllSounds();
            mouse.invisibleMouseTimer.stop();
            if (terminateEverything) {
                System.exit(0);
            }
        }

        Dimension getTextDisplaySize() {
            return paintingPanel.getSize();
        }
    }

    private static class PaintingPanel extends JPanel {

        volatile BufferedImage bufferedImage;

        PaintingPanel() {
            setBackground(Color.BLACK);
            setForeground(Color.WHITE);
            bufferedImage = new BufferedImage(GameView.WIDTH, GameView.HEIGHT, BufferedImage.TYPE_INT_RGB);
            setDoubleBuffered(false);
            setIgnoreRepaint(true);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            double scalingFactor = Math.min(getWidth() * 1d / GameView.WIDTH, getHeight() * 1d / GameView.HEIGHT);
            Graphics2D g2D = (Graphics2D) g;
            AffineTransform trans = g2D.getTransform();
            trans.scale(scalingFactor, scalingFactor);
            g2D.setTransform(trans);
            g2D.drawImage(bufferedImage, 0, 0, null);
            g2D.dispose();
        }
    }


    private static class Window {

        private final SwingAdapter swingAdapter;
        private long lastPrintTimeInNanos;
        private final static int FRAMES_PER_SECOND = 120;
        private final static int NANOS_PER_FRAME = 1_000_000_000 / FRAMES_PER_SECOND;

        Window(SwingAdapter swingAdapter) {
            this.swingAdapter = swingAdapter;
            this.lastPrintTimeInNanos = System.nanoTime();
        }

        void printCanvas(Canvas canvas) {
            swingAdapter.printToDisplay(canvas.getPrintObjects(), canvas.getBackgroundColor());
            canvas.getPrintObjects().clear();
            int elapsedNanosSinceLastPrint = (int) (System.nanoTime() - lastPrintTimeInNanos);
            sleep(NANOS_PER_FRAME - elapsedNanosSinceLastPrint);
            lastPrintTimeInNanos = System.nanoTime();
        }

        private void sleep(int nanos) {
            try {
                Thread.sleep(Math.max(0, nanos / 1_000_000));
            } catch (InterruptedException ignored) {
            }
        }

        void setStatusText(String statusText) {
            swingAdapter.setStatusText(statusText);
        }

        void setWindowIcon(String windowIcon) {
            swingAdapter.setWindowIcon(windowIcon);
        }

        void setTitle(String title) {
            swingAdapter.setTitle(title);
        }

        void closeWindow(boolean terminateEverything) {
            swingAdapter.closeWindow(terminateEverything);
        }
    }

    private static class StartScreenWithChooseBox {
        private final GameView gameView;

        private final int lineWeight;
        private final int titleFontSize;
        private final String title;
        private final int titleHeight;
        private final Color font;
        private final Color frameAndTitle;

        private final String description;
        private final int descriptionFontSize;
        private final int yDescription;

        private final SelectionBox selectionBox;
        private final int selectionBoxLineWeight;
        private final int xSelectionBox;

        private final int enterBoxWidth;
        private final int enterBoxHeight;
        private final int ySelectionBox;
        private final java.awt.Rectangle enterBox;

        private final int yLowerLine;

        private boolean startScreenClosed;
        private final boolean useMouseBackup;


        StartScreenWithChooseBox(GameView gameView, String title, String description, String selectionTitle,
                                 String[] selectionItems, int selectedItem) {
            this.gameView = gameView;

            this.lineWeight = 5;
            this.title = title;
            this.titleFontSize = 45;
            this.titleHeight = (int) Math.rint(titleFontSize * 1.5);
            this.font = Color.GRAY;
            this.frameAndTitle = Color.WHITE;

            this.description = description;
            this.descriptionFontSize = 16;
            this.yDescription = titleHeight + 2 * lineWeight;

            int gap = 20;
            int selectionFontSize = 20;
            this.selectionBoxLineWeight = (int) Math.rint(selectionFontSize / 8d);
            this.selectionBox = new SelectionBox(gameView, selectionTitle, selectionItems, selectedItem,
                    selectionFontSize, selectionBoxLineWeight, font, Color.YELLOW,
                    Color.BLACK, frameAndTitle);
            this.xSelectionBox = gap;
            this.ySelectionBox = HEIGHT - selectionBox.getHeight() - gap;

            this.enterBoxWidth = WIDTH / 3 - 2 * gap;
            this.enterBoxHeight = 4 * descriptionFontSize;
            int yEnterBox = HEIGHT - enterBoxHeight - gap;
            this.enterBox = new java.awt.Rectangle(WIDTH - enterBoxWidth - gap, yEnterBox, enterBoxWidth,
                    enterBoxHeight);

            this.yLowerLine = Math.min(ySelectionBox - gap, yEnterBox - gap);
            this.startScreenClosed = false;
            useMouseBackup = gameView.isMouseEnabled();
            gameView.useMouse(true);
        }


        void printStartScreen() {
            while (!startScreenClosed) {
                checkUserInput();
                addRectangles();
                addTitle();
                addDescription();
                selectionBox.addSelectionBox(xSelectionBox, ySelectionBox);
                addEnterField();
                gameView.printCanvas();
            }
            gameView.useMouse(useMouseBackup);
        }

        private void checkUserInput() {
            // Tastendruck
            KeyEvent[] keyEvents = gameView.pollKeyEvents();
            for (KeyEvent keyEvent : keyEvents) {
                if (keyEvent.getID() == KeyEvent.KEY_PRESSED) {
                    if (keyEvent.getKeyCode() == KeyEvent.VK_UP) {
                        selectionBox.up();
                    } else if (keyEvent.getKeyCode() == KeyEvent.VK_DOWN) {
                        selectionBox.down();
                    } else if (keyEvent.getKeyCode() == KeyEvent.VK_ENTER) {
                        startScreenClosed = true;
                    }
                }
            }
            // Mausklick
            MouseEvent[] mouseEvents = gameView.pollMouseEvents();
            for (MouseEvent mouseEvent : mouseEvents) {
                if (mouseEvent.getID() == MouseEvent.MOUSE_PRESSED) {
                    selectionBox.processMouseClick(mouseEvent.getX(), mouseEvent.getY());
                    if (enterBox.contains(mouseEvent.getX(), mouseEvent.getY())) {
                        startScreenClosed = true;
                    }
                }
            }
        }

        private void addRectangles() {
            gameView.addRectangleToCanvas(lineWeight / 2d, lineWeight / 2d, WIDTH - 1 - lineWeight,
                    HEIGHT - 1 - lineWeight, lineWeight, false, font);
            gameView.addRectangleToCanvas(lineWeight / 2d, lineWeight / 2d, WIDTH - 1 - lineWeight,
                    titleHeight - lineWeight, lineWeight, false, font);
            gameView.addRectangleToCanvas(lineWeight / 2d, yLowerLine + lineWeight / 2d, WIDTH - 1 - lineWeight,
                    HEIGHT - yLowerLine - lineWeight, lineWeight, false, font);
        }

        private void addTitle() {
            gameView.addTextToCanvas(title, (WIDTH - title.length() * titleFontSize) / 2d,
                    ((int) (titleFontSize * 1.5) - titleFontSize) / 2d, titleFontSize, frameAndTitle
                    , 0);
        }

        private void addDescription() {
            gameView.addTextToCanvas(description, 2 * lineWeight, yDescription, descriptionFontSize, font, 0);
        }

        private void addEnterField() {
            gameView.addRectangleToCanvas(enterBox.x, enterBox.y, enterBox.width, enterBox.height,
                    selectionBoxLineWeight, false, frameAndTitle);
            int gap = 2 * selectionBoxLineWeight;
            gameView.addRectangleToCanvas(enterBox.x + gap, enterBox.y + gap, enterBox.width - 2 * gap,
                    enterBox.height - 2 * gap, selectionBoxLineWeight, false, frameAndTitle);
            String text = "Press ENTER or\n" + "click to start";
            int titleWidth = 14 * descriptionFontSize;
            int titleHeight = 2 * descriptionFontSize;
            gameView.addTextToCanvas(text, enterBox.x + (enterBoxWidth - titleWidth) / 2d,
                    enterBox.y + (enterBoxHeight - titleHeight) / 2d, descriptionFontSize,
                    frameAndTitle, 0);
        }

        int getSelectedItem() {
            return selectionBox.getSelectedItem();
        }

        private static class SelectionBox {
            private final GameView gameView;
            private final String title;
            private final String[] items;
            private final int fontSize;
            private int selectedItem;
            private final Color markerFont;
            private final Color markerHighlight;
            private final Color markerRectangle;
            private final Color frameAndTitle;

            private final int lineWeight;
            private final int titleHeight;
            private final int heightOfMarkerField;
            private final int heightOfMarkerBox;
            private final int height;
            private int widthOfMarkerField;
            private final int width;

            private final java.awt.Rectangle[] markerBounds;
            private final java.awt.Rectangle upBounds;
            private final java.awt.Rectangle downBounds;

            private int x;
            private int xLine;
            private int y;
            private int yMarkerBox;


            private SelectionBox(GameView gameView, String title, String[] items, int selectedItem, int fontSize,
                                 int lineWeight, Color markerFont, Color markerHighlight, Color markerRectangle,
                                 Color frameAndTitle) {
                this.gameView = gameView;
                this.title = title;
                this.items = items;
                this.fontSize = fontSize;
                this.selectedItem = selectedItem;
                this.markerFont = markerFont;
                this.markerHighlight = markerHighlight;
                this.markerRectangle = markerRectangle;
                this.frameAndTitle = frameAndTitle;

                this.lineWeight = lineWeight;
                this.titleHeight = (int) Math.rint(fontSize * 1.6);
                this.heightOfMarkerField = (int) Math.rint(fontSize * 1.25);
                this.heightOfMarkerBox = items.length * heightOfMarkerField + 2 * lineWeight;
                this.height = titleHeight + heightOfMarkerBox - lineWeight;
                calculateWidthOfMarkerField();
                this.width = widthOfMarkerField + 6 * lineWeight;

                this.markerBounds = new java.awt.Rectangle[items.length];
                for (int i = 0; i < items.length; i++) {
                    markerBounds[i] = new java.awt.Rectangle(0, 0, widthOfMarkerField, heightOfMarkerField);
                }
                this.upBounds = new java.awt.Rectangle(0, 0, 5 * lineWeight, markerBounds[0].height);
                this.downBounds = new java.awt.Rectangle(0, 0, 5 * lineWeight, markerBounds[items.length - 1].height);
            }

            private void calculateWidthOfMarkerField() {
                int letters = title.strip().length();
                for (String name : items) {
                    if (name.strip().length() > letters) {
                        letters = name.strip().length();
                    }
                }
                this.widthOfMarkerField = letters * fontSize + 2 * lineWeight;
            }

            void addSelectionBox(int x, int y) {
                this.x = x + lineWeight / 2;
                this.xLine = x + lineWeight;
                this.y = y + lineWeight / 2;
                this.yMarkerBox = y + titleHeight - lineWeight + lineWeight / 2;
                addTitleBox();
                addMarkerFields();
                addNavigationBox();
            }

            private void addTitleBox() {
                gameView.addRectangleToCanvas(x, y, width - lineWeight, titleHeight - lineWeight, lineWeight, false,
                        frameAndTitle);
                gameView.addTextToCanvas(title, xLine + (widthOfMarkerField - title.length() * fontSize) / 2d,
                        y + (titleHeight - fontSize) / 2d, fontSize, frameAndTitle, 0);
            }

            private void addMarkerFields() {
                gameView.addRectangleToCanvas(x, yMarkerBox, widthOfMarkerField + 2 * lineWeight - lineWeight,
                        heightOfMarkerBox - lineWeight, lineWeight, false, frameAndTitle);
                int yMarkerField = yMarkerBox + lineWeight / 2;
                for (int i = 0; i < items.length; i++) {
                    boolean isSelected = (i == selectedItem);
                    markerBounds[i].x = xLine;
                    markerBounds[i].y = yMarkerField + i * heightOfMarkerField;
                    addMarkerField(markerBounds[i], items[i], isSelected);
                }
                upBounds.x = markerBounds[0].x + markerBounds[0].width;
                upBounds.y = markerBounds[0].y;
                downBounds.x = markerBounds[items.length - 1].x + markerBounds[items.length - 1].width;
                downBounds.y = markerBounds[items.length - 1].y;
            }

            private void addMarkerField(java.awt.Rectangle bounds, String name, boolean isMarked) {
                if (isMarked) {
                    gameView.addRectangleToCanvas(bounds.x + lineWeight / 2d, bounds.y + lineWeight / 2d,
                            bounds.width - lineWeight, bounds.height - lineWeight, lineWeight,
                            true, markerHighlight);
                    gameView.addRectangleToCanvas(bounds.x + lineWeight / 2d, bounds.y + lineWeight / 2d,
                            bounds.width - lineWeight, bounds.height - lineWeight, 1, false,
                            markerRectangle);
                } else {
                    gameView.addRectangleToCanvas(bounds.x, bounds.y, bounds.width, bounds.height, 1, false,
                            markerFont);
                }
                gameView.addTextToCanvas(name, bounds.x + lineWeight, bounds.y + (bounds.height - fontSize) / 2d,
                        fontSize, markerFont, 0);
            }

            private void addNavigationBox() {
                int xUpDown = x + widthOfMarkerField + lineWeight;
                int yUp = yMarkerBox + lineWeight + fontSize / 2 + 2 * lineWeight;
                int yDown = yMarkerBox + items.length * heightOfMarkerField - fontSize / 2 - 2 * lineWeight;
                gameView.addRectangleToCanvas(xUpDown, yMarkerBox, 4 * lineWeight,
                        items.length * heightOfMarkerField + lineWeight, lineWeight, false,
                        frameAndTitle);
                gameView.addPolygonToCanvas(new double[]{xUpDown + lineWeight, xUpDown + 3 * lineWeight,
                        xUpDown + 2 * lineWeight}, new double[]{yUp, yUp, yUp - fontSize / 2d}, 1, true, frameAndTitle);
                gameView.addPolygonToCanvas(new double[]{xUpDown + lineWeight, xUpDown + 3 * lineWeight,
                                xUpDown + 2 * lineWeight}, new double[]{yDown, yDown,
                                yDown + fontSize / 2d}, 1, true,
                        frameAndTitle);
            }

            void processMouseClick(int x, int y) {
                for (int i = 0; i < markerBounds.length; i++) {
                    if (markerBounds[i].contains(x, y)) {
                        selectedItem = i;
                    }
                }
                if (upBounds.contains(x, y)) {
                    up();
                } else if (downBounds.contains(x, y)) {
                    down();
                }
            }

            void up() {
                if (selectedItem > 0) {
                    selectedItem--;
                }
            }

            void down() {
                if (selectedItem < items.length - 1) {
                    selectedItem++;
                }
            }

            int getHeight() {
                return height;
            }

            int getSelectedItem() {
                return selectedItem;
            }
        }
    }

    private static class Screen {
        protected final GameView gameView;
        protected final int gap;
        protected final int fontSize;
        protected final boolean useMouseBackup;
        protected boolean screenClosed;
        protected SelectionManager selectionManager;
        protected ArrayList<SimpleBox> simpleBoxes;

        protected Screen(GameView gameView, int gap, int fontSize) {
            this.gameView = gameView;
            this.gap = gap;
            this.fontSize = fontSize;
            this.useMouseBackup = gameView.isMouseEnabled();
            this.gameView.useMouse(true);
        }

        protected void setSimpleBoxes(ArrayList<SimpleBox> simpleBoxes, int highLighted) {
            this.simpleBoxes = simpleBoxes;
            this.selectionManager = new SelectionManager(simpleBoxes, highLighted);
        }

        protected void checkUserInput() {
            for (KeyEvent keyEvent : gameView.pollKeyEvents()) {
                if (keyEvent.getID() == KeyEvent.KEY_PRESSED) {
                    selectionManager.processKeyEvent(keyEvent);
                    if (keyEvent.getKeyCode() == KeyEvent.VK_ENTER) {
                        screenClosed = true;
                    }
                }
            }
            for (MouseEvent mouseEvent : gameView.pollMouseEvents()) {
                if (mouseEvent.getID() == MouseEvent.MOUSE_PRESSED) {
                    if (selectionManager.processMouseEvent(mouseEvent.getX(), mouseEvent.getY())) {
                        screenClosed = true;
                    }
                }
            }
        }

        protected Dimension calculateBounds(String text) {
            String[] lines = text.split("\\R");
            int longestLine = Arrays.stream(lines).mapToInt(String::length).max().orElse(1);
            return new Dimension(longestLine, Math.max(1, lines.length));
        }

        protected int calculateFontSizeForBounds(Dimension textBounds, int height) {
            return Math.min(WIDTH / textBounds.width, height / textBounds.height) - 1;
        }
    }

    private static class EndScreen extends Screen {

        private final String message;

        private EndScreen(GameView gameView, String message) {
            super(gameView, 20, 28);
            this.message = message;
            int height = 40;
            int width = 250;
            int x = (WIDTH - 2 * width - gap) / 2;
            int y = HEIGHT - height - gap;
            ArrayList<SimpleBox> simpleBoxes = new ArrayList<>(3);
            simpleBoxes.add(new SimpleBox("New Game", x, y, width, height, false));
            simpleBoxes.add(new SimpleBox("Close", x + width + gap, y, width, height, true));
            setSimpleBoxes(simpleBoxes, 0);
        }

        void printEndScreen() {
            while (!screenClosed) {
                checkUserInput();
                addMessageToCanvas();
                simpleBoxes.forEach(s -> s.addToCanvas(gameView));
                gameView.printCanvas();
            }
            gameView.useMouse(useMouseBackup);
        }

        private void addMessageToCanvas() {
            Dimension messageBounds = calculateBounds(message);
            int x = (GameView.WIDTH - messageBounds.width * fontSize) / 2;
            int y = (GameView.HEIGHT - messageBounds.height * fontSize - 200) / 2;
            gameView.addTextToCanvas(message, x, y, fontSize, Color.WHITE, 0);
        }

        boolean playAgain() {
            return simpleBoxes.get(0).isHighlighted;
        }
    }


    private static class SimpleStartScreen extends Screen {
        private final int titleHeight;
        private final String title;
        private final Color titleColor;
        private final String description;

        private SimpleStartScreen(GameView gameView, String title, String description, boolean easy) {
            super(gameView, 20, 16);
            this.title = title;
            this.titleHeight = HEIGHT / 4;
            this.titleColor = Color.RED.brighter();
            this.description = description;
            int height = 40;
            int width = 200;
            int x = (WIDTH - 3 * width - 2 * gap) / 2;
            int y = HEIGHT - height - gap;
            ArrayList<SimpleBox> simpleBoxes = new ArrayList<>(3);
            simpleBoxes.add(new SimpleBox("Easy", x, y, width, height, false));
            simpleBoxes.add(new SimpleBox("Standard", x + width + gap, y, width, height, false));
            simpleBoxes.add(new SimpleBox("Close", x + 2 * width + 2 * gap, y, width, height, true));
            if (easy) {
                setSimpleBoxes(simpleBoxes, 0);
            } else {
                setSimpleBoxes(simpleBoxes, 1);
            }
        }

        String getSelectedItem() {
            return selectionManager.getSelectedItem().text;
        }

        void printStartScreen() {
            while (!screenClosed) {
                checkUserInput();
                addTitle();
                gameView.addTextToCanvas(description, gap, titleHeight + gap, fontSize, Color.WHITE, 0);
                simpleBoxes.forEach(s -> s.addToCanvas(gameView));
                gameView.printCanvas();
            }
            gameView.useMouse(useMouseBackup);
        }

        private void addTitle() {
            Dimension textBounds = calculateBounds(title);
            int fontSize = calculateFontSizeForBounds(textBounds, titleHeight);
            gameView.addTextToCanvas(title, (WIDTH - (textBounds.width * fontSize)) / 2d,
                    (titleHeight - (textBounds.height * fontSize)) / 2d, fontSize, titleColor, 0);
        }
    }

    private static class SimpleBox extends java.awt.Rectangle {
        public final String text;
        public boolean isHighlighted;
        public boolean isQuitBox;
        private final int fontSize;

        private SimpleBox(String text, int x, int y, int width, int height, boolean isQuitBox) {
            super(x, y, width, height);
            this.text = text;
            this.isQuitBox = isQuitBox;
            this.fontSize = height / 2;
        }

        void addToCanvas(GameView gameView) {
            if (isHighlighted) {
                gameView.addRectangleToCanvas(x, y, width, height, 3, true, Color.DARK_GRAY);
                gameView.addRectangleToCanvas(x, y, width, height, 3, false, Color.YELLOW);
            } else {
                gameView.addRectangleToCanvas(x, y, width, height, 3, false, Color.WHITE);
            }
            gameView.addTextToCanvas(text, x + (width - text.length() * fontSize) / 2d, y + (height - fontSize) / 2d,
                    fontSize, Color.WHITE, 0);
        }
    }

    private static class SelectionManager {
        private final ArrayList<SimpleBox> simpleBoxes;
        private int highlightedBox;

        private SelectionManager(ArrayList<SimpleBox> simpleBoxes, int highlightedBox) {
            this.simpleBoxes = simpleBoxes;
            this.highlightedBox = highlightedBox;
            this.simpleBoxes.get(highlightedBox).isHighlighted = true;
        }

        public SimpleBox getSelectedItem() {
            return simpleBoxes.get(highlightedBox);
        }

        void processKeyEvent(KeyEvent keyEvent) {
            if (keyEvent.getKeyCode() == KeyEvent.VK_RIGHT || keyEvent.getKeyCode() == KeyEvent.VK_DOWN) {
                highlight(highlightedBox + 1);
            } else if (keyEvent.getKeyCode() == KeyEvent.VK_LEFT || keyEvent.getKeyCode() == KeyEvent.VK_UP) {
                highlight(highlightedBox - 1);
            }
        }

        boolean processMouseEvent(int x, int y) {
            for (int i = 0; i < simpleBoxes.size(); i++) {
                SimpleBox simpleBox = simpleBoxes.get(i);
                if (simpleBox.contains(x, y)) {
                    highlight(i);
                    return true;
                }
            }
            return false;
        }

        private void highlight(int boxToHighlight) {
            if (boxToHighlight >= 0 && boxToHighlight < simpleBoxes.size()) {
                simpleBoxes.forEach(s -> s.isHighlighted = false);
                simpleBoxes.get(boxToHighlight).isHighlighted = true;
                highlightedBox = boxToHighlight;
            }
        }
    }

    private static class Version {
        private final static String LAST_CHANGE = "2022-07-26";
        private final static LocalDate DATE = LocalDate.parse(LAST_CHANGE);
        private final static String VERSION = LAST_CHANGE.replaceAll("-", ".");
        private final static String VERSION_SHORT = VERSION.substring(0, 6);

        private final static String STANDARD_TITLE = "GameView";
        private final static String SIGNATURE = "";

        private static String getStatusSignature() {
            return "   " + SIGNATURE + " ";
        }

        private static String getStandardTitle() {
            return STANDARD_TITLE + " " + VERSION;
        }
    }
}

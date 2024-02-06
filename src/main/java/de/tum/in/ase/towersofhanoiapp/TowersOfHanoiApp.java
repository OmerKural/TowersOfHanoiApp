package de.tum.in.ase.towersofhanoiapp;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class TowersOfHanoiApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    // Colors
    private static final Color BACKGROUND_COLOR = Color.WHITE;
    private static final Color ROD_COLOR = Color.BLACK;
    private static final Color DISK_COLOR = Color.RED;

    // Statics
    private static final int CANVAS_WIDTH = 800;
    private static final int CANVAS_HEIGHT = 600;
    private static final int DISK_HEIGHT = 14;

    // For drawing
    private GraphicsContext graphics;

    // Widgets
    private Button nextStepButton;
    private Button restartButton;
    private ComboBox<Integer> comboBox;

    // Containers and the height value for the algorithm.
    private int towerHeight;
    private int[][] towers;
    private int[] heights;
    private List<Pair<Integer, Integer>> moves;

    /**
     * Initializes the window and UI.
     *
     * @param stage the primary stage for this application, onto which
     *              the application scene can be set.
     *              Applications may create other stages, if needed, but they will not be
     *              primary stages.
     */
    public void start(Stage stage) {
        stage.setTitle("Towers of Hanoi");

        moves = new ArrayList<>();
        heights = new int[3];

        Canvas canvas = new Canvas(CANVAS_WIDTH, CANVAS_HEIGHT);
        graphics = canvas.getGraphicsContext2D();
        drawBackground();
        drawRods();

        nextStepButton = new Button("Next Step");
        nextStepButton.setOnAction(e -> nextStep());
        nextStepButton.setMinSize(100, 40);
        nextStepButton.setDisable(true);

        restartButton = new Button("Restart");
        restartButton.setOnAction(e -> restart());
        restartButton.setMinSize(100, 40);
        restartButton.setDisable(true);

        ObservableList<Integer> options = FXCollections.observableArrayList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        comboBox = new ComboBox<>(options);
        comboBox.setMinSize(40, 40);
        comboBox.setVisibleRowCount(3);
        comboBox.setValue(1);

        Button setButton = new Button("Set Tower Height");
        setButton.setOnAction(e -> set());
        setButton.setMinSize(100, 40);

        HBox top = new HBox(comboBox, setButton);
        top.setSpacing(10);
        top.setAlignment(Pos.CENTER);
        HBox bot = new HBox(nextStepButton, restartButton);
        bot.setSpacing(10);
        bot.setAlignment(Pos.CENTER);

        BorderPane borderPane = new BorderPane(canvas);
        borderPane.setTop(top);
        borderPane.setBottom(bot);

        stage.setScene(new Scene(borderPane));
        stage.setResizable(false);
        stage.show();
    }

    // Sets the tower height.
    private void set() {
        towerHeight = comboBox.getValue();
        towers = new int[3][towerHeight];
        restart();
    }

    // Makes the next move.
    private void nextStep() {
        makeMove(moves.remove(0));
        draw();
        if (moves.isEmpty()) {
            nextStepButton.setDisable(true);
        }
    }

    // Restarts the hanoi tower, based on the last tower height input.
    private void restart() {
        moves.clear();
        initialise();
        fillMoves(towerHeight, 0, 1, 2);
        nextStepButton.setDisable(false);
        restartButton.setDisable(false);
    }

    // Initializes the hanoi tower, based on the last tower height input.
    private void initialise() {
        for (int i = 0; i < towerHeight; i++) {
            towers[0][i] = towerHeight - i;
        }

        heights[0] = towerHeight;
        heights[1] = 0;
        heights[2] = 0;

        draw();
    }

    /**
     * The main algorithm for Hanoi Towers. The function fills the moves list, which will be later read to make the moves.
     *
     * @param disks Number of disks.
     * @param from  Source stick
     * @param to    Target stick
     * @param spare The extra stick.
     */
    private void fillMoves(int disks, int from, int to, int spare) {
        // Base case:
        if (disks == 1) {
            moves.add(new Pair<>(from, to));
        }
        // Recursive calls:
        else {
            fillMoves(disks - 1, from, spare, to);
            moves.add(new Pair<>(from, to));
            fillMoves(disks - 1, spare, to, from);
        }
    }

    /**
     * Makes the move and changes the containers accordingly.
     * @param move Move to be made.
     */
    private void makeMove(Pair<Integer, Integer> move) {
        int source = move.getKey();
        int target = move.getValue();
        int moveDisk = towers[source][--heights[source]];
        towers[target][heights[target]++] = moveDisk;
    }

    // Helper to call all drawing methods at once.
    private void draw() {
        drawBackground();
        drawRods();
        drawDisks();
    }

    // Draw the background.
    private void drawBackground() {
        graphics.setFill(BACKGROUND_COLOR);
        graphics.fillRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
    }

    // Draw the disks.
    private void drawDisks() {
        int towerWidth = 2*DISK_HEIGHT;
        int gap = (CANVAS_WIDTH - 3 * towerWidth) / 4;
        graphics.setFill(DISK_COLOR);
        for (int t = 0; t < 3; t++) {
            int xPos = gap * (t + 1) + towerWidth * t;
            for (int i = 0; i < heights[t]; i++) {
                int disk = towers[t][i];
                graphics.fillRoundRect(xPos - 7 * disk - 7, 350 - 20 * i, DISK_HEIGHT * disk + DISK_HEIGHT, DISK_HEIGHT, 10, 10);
            }
        }
    }

    // Draw the rods.
    private void drawRods() {
        int towerWidth = 2*DISK_HEIGHT;
        int gap = (CANVAS_WIDTH - 3 * towerWidth) / 4;
        int stickWidth = 10;
        int stickHeight = 200;
        int stickXPos = gap + stickWidth / 2 - 10;
        int stickYPos = 150 + DISK_HEIGHT;
        graphics.setFill(ROD_COLOR);
        for (int i = 0; i < 3; i++) {
            graphics.fillRoundRect(stickXPos, stickYPos + 5, stickWidth, stickHeight, 5, 5);
            stickXPos += towerWidth + gap;
        }
        graphics.fillRect(0, stickYPos + stickHeight, CANVAS_WIDTH, CANVAS_HEIGHT - (stickYPos + stickHeight));
    }
}

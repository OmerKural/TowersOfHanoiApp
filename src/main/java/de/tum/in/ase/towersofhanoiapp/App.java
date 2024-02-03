package de.tum.in.ase.towersofhanoiapp;

import javafx.application.Application;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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

public class App extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    // Colors for background and disks.
    private static final Color BACKGROUND_COLOR = Color.rgb(217, 204, 185); // 4 colors used in drawing.
    private static final Color DISK_COLOR = Color.rgb(98, 68, 66);

    // For drawing
    private GraphicsContext graphics;

    // Widgets
    private Button nextStepButton;
    private Button restartButton;
    private Label towerHeightLabel;
    private TextField towerHeightField;

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

        Canvas canvas = new Canvas(430, 150);
        graphics = canvas.getGraphicsContext2D();
        graphics.setFill(BACKGROUND_COLOR);
        graphics.fillRect(0, 0, 430, 143);

        nextStepButton = new Button("Next Step");
        nextStepButton.setOnAction(e -> nextStep());
        nextStepButton.setDisable(true);

        restartButton = new Button("Restart");
        restartButton.setOnAction(e -> restart());
        restartButton.setDisable(true);

        towerHeightField = new TextField();
        towerHeightField.setPromptText("Tower Height");

        towerHeightLabel = new Label("Tower Height = ");

        Button setButton = new Button("Set Tower Height");
        setButton.setOnAction(e -> set());

        BorderPane borderPane = new BorderPane(canvas);
        HBox top = new HBox(towerHeightField, setButton, towerHeightLabel);
        top.setSpacing(10);
        borderPane.setTop(top);
        HBox bot = new HBox(nextStepButton, restartButton);
        bot.setSpacing(10);
        borderPane.setBottom(bot);

        stage.setScene(new Scene(borderPane));
        stage.setResizable(false);
        stage.show();
    }

    /**
     * Sets the tower height.
     */
    private void set() {
        try {
            towerHeight = Integer.parseInt(towerHeightField.getText());
            towers = new int[3][towerHeight];
            restart();
        } catch (NumberFormatException ignored) {
            towerHeightLabel.setText("Tower Height = ERROR");
            nextStepButton.setDisable(true);
            restartButton.setDisable(true);
        } finally {
            towerHeightField.clear();
        }
    }

    /**
     * Makes the next move.
     */
    private void nextStep() {
        makeMove(moves.remove(0));
        if (moves.isEmpty()) {
            nextStepButton.setDisable(true);
        }
    }

    /**
     * Restarts the hanoi tower, based on the last tower height input.
     */
    private void restart() {
        moves.clear();
        initialise();
        fillMoves(towerHeight, 0, 1, 2);
        towerHeightLabel.setText("Tower Height = " + towerHeight);
        nextStepButton.setDisable(false);
        restartButton.setDisable(false);
    }

    /**
     * Initializes the hanoi tower, based on the last tower height input.
     */
    private void initialise() {
        for (int i = 0; i < towerHeight; i++) {
            towers[0][i] = towerHeight - i;
        }

        heights[0] = towerHeight;
        heights[1] = 0;
        heights[2] = 0;

        // Draw background and clear screen.
        graphics.setFill(BACKGROUND_COLOR);
        graphics.fillRect(0, 0, 430, 143);
        // Draw the towers disk by disk.
        graphics.setFill(DISK_COLOR);
        for (int t = 0; t < 3; t++) {
            for (int i = 0; i < heights[t]; i++) {
                int disk = towers[t][i];
                graphics.fillRoundRect(75 + 140 * t - 5 * disk - 5, 116 - 12 * i, 10 * disk + 10, 10, 10, 10);
            }
        }
    }

    /**
     * The main algorithm for Hanoi Towers. The function fills the moves list, which will be later read to make the moves.
     * @param disks Number of disks.
     * @param from  Source stick
     * @param to    Target stick
     * @param spare The extra stick.
     */
    private void fillMoves(int disks, int from, int to, int spare) {
        if (disks == 1) {
            moves.add(new Pair<>(from, to));
        } else {
            fillMoves(disks - 1, from, spare, to);
            moves.add(new Pair<>(from, to));
            fillMoves(disks - 1, spare, to, from);
        }
    }

    /**
     * Makes the provided move visually.
     * @param move Move to be made.
     */
    private void makeMove(Pair<Integer, Integer> move) {
        int source = move.getKey();
        int target = move.getValue();
        int moveDisk = towers[source][--heights[source]];

        // Delete the top disk of the source stack by coloring it with the BACKGROUND_COLOR.
        graphics.setFill(BACKGROUND_COLOR);
        graphics.fillRoundRect(
                75 + 140 * source - 5 * moveDisk - 6,
                116 - 12 * heights[source] - 1, 10 * moveDisk + 12,
                12, 10, 10);
        // Draw the disk to the top of the target stack.
        graphics.setFill(DISK_COLOR);
        graphics.fillRoundRect(
                75 + 140 * target - 5 * moveDisk - 5,
                116 - 12 * heights[target], 10 * moveDisk + 10,
                10, 10, 10);

        // Save it to the container.
        towers[target][heights[target]++] = moveDisk;
    }
}
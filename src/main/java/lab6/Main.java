package lab6; // Package to organize the application

// --- JavaFX core ---
import javafx.application.Application; // Base class for any JavaFX app
import javafx.application.Platform;    // Allows running code on the JavaFX Application Thread
import javafx.stage.Stage;             // Represents the main window
import javafx.scene.Scene;             // Container for all UI elements

// --- UI controls ---
import javafx.scene.control.Button;    // Button control
import javafx.scene.control.Label;     // Text label
import javafx.scene.control.Alert;     // Popup dialog
import javafx.scene.control.Alert.AlertType; // Types of alerts (info, warning, etc.)

// --- Layouts ---
import javafx.scene.layout.BorderPane; // Layout with regions (top, center, bottom, etc.)
import javafx.scene.layout.HBox;       // Horizontal layout pane
import javafx.scene.layout.Pane;       // Simple layout for free positioning

// --- Shapes and colors ---
import javafx.scene.paint.Color;       // Color class
import javafx.scene.shape.Circle;      // Circle shape

// --- Events and utilities ---
import javafx.scene.input.MouseEvent;  // Mouse click event
import javafx.geometry.Insets;         // Padding utility

// --- Animations ---
import javafx.animation.Animation;     // Base class for animations
import javafx.animation.KeyFrame;      // Defines a single frame in a Timeline
import javafx.animation.Timeline;      // Timeline for repeated actions
import javafx.util.Duration;           // Represents time duration

// --- Sounds ---
import javafx.scene.media.AudioClip;   // Plays short sound effects

// --- Java utilities ---
import java.util.ArrayList;            // Dynamic list implementation
import java.util.List;                 // List interface
import java.util.Random;               // Random number generator

/**
 * Tap the Target: A reaction and accuracy JavaFX game.
 * Features: multiple circles, variable sizes/colors, mission (blue only),
 * timer (30s), levels that increase speed, sounds, and final stats alert.
 */
public class Main extends Application {

    // --- Game state ---
    private int score = 0;              // Current score
    private int hits = 0;               // Number of correct hits (blue circles)
    private int totalTargets = 0;       // Total circles that appeared
    private int level = 1;              // Current level
    private int lastLevelReached = 1;   // Last level reached
    private int timeLeft = 30;          // Time remaining in seconds

    // --- UI labels ---
    private final Label scoreLabel = new Label("Score: 0");
    private final Label levelLabel = new Label("Level: 1");
    private final Label missionLabel = new Label("Mission: Click BLUE circles");
    private final Label timerLabel = new Label("Time: 30s");

    // --- Random and animations ---
    private final Random random = new Random();
    private Timeline moveTimeline;
    private Timeline timerTimeline;

    // --- Game area and targets ---
    private final List<Circle> targets = new ArrayList<>();
    private Pane gameArea;

    // --- Sounds ---
    private AudioClip hitSound;
    private AudioClip missSound;

    // --- Control flag ---
    private boolean gameRunning = false;

    @Override
    public void start(Stage stage) {
        BorderPane root = new BorderPane();

        // Load sounds (if files exist in src/main/resources)
        try {
            hitSound = new AudioClip(getClass().getResource("/hit.wav").toString());
            missSound = new AudioClip(getClass().getResource("/miss.wav").toString());
        } catch (Exception e) {
            System.out.println("Sounds not loaded. Continuing without audio.");
        }

        // Top bar with title and buttons
        HBox topBar = new HBox(10);
        Button startBtn = new Button("Start");
        Button statsBtn = new Button("Stats");
        Label title = new Label("Tap the Target");
        topBar.getChildren().addAll(title, startBtn, statsBtn);
        topBar.setPadding(new Insets(10));

        // Game area
        gameArea = new Pane();
        gameArea.setPrefSize(500, 350);

        // Bottom bar with info labels
        HBox bottomBar = new HBox(15, scoreLabel, levelLabel, missionLabel, timerLabel);
        bottomBar.setPadding(new Insets(10));

        root.setTop(topBar);
        root.setCenter(gameArea);
        root.setBottom(bottomBar);

        // Button events
        startBtn.setOnAction(e -> startGame());
        statsBtn.setOnAction(e -> showStats());

        Scene scene = new Scene(root, 700, 500);
        stage.setTitle("Tap the Target");
        stage.setScene(scene);
        stage.show();
    }

    /** Starts or restarts the game. */
    private void startGame() {
        score = 0;
        hits = 0;
        totalTargets = 0;
        level = 1;
        lastLevelReached = 1;
        timeLeft = 30;
        gameRunning = true;

        // Update labels
        scoreLabel.setText("Score: 0");
        levelLabel.setText("Level: 1");
        missionLabel.setText("Mission: Click BLUE circles");
        timerLabel.setText("Time: 30s");

        // Clear area
        gameArea.getChildren().clear();
        targets.clear();

        // Create 3 circles
        for (int i = 0; i < 3; i++) {
            Circle c = createTarget();
            targets.add(c);
            gameArea.getChildren().add(c);
        }

        // Start timelines
        startMoveTimeline(1.0);   // initial speed
        startTimerTimeline();     // countdown
    }

    /** Creates a circle with random size and color. */
    private Circle createTarget() {
        double radius = 15 + random.nextInt(25);
        Circle c = new Circle(radius);
        c.setFill(randomColor());
        c.setOnMouseClicked(event -> handleClick(c));
        placeCircleRandomly(c);
        return c;
    }

    /** Moves all circles to random positions. */
    private void moveTargets() {
        if (!gameRunning) return;
        for (Circle c : targets) {
            c.setRadius(15 + random.nextInt(25));
            placeCircleRandomly(c);
            totalTargets++;
        }
    }

    /** Handles clicks on circles. */
    private void handleClick(Circle c) {
        if (!gameRunning) return;

        boolean isBlue = Color.BLUE.equals(c.getFill());
        if (isBlue) {
            score++;
            hits++;
            scoreLabel.setText("Score: " + score);
            if (hitSound != null) hitSound.play();
        } else {
            score = Math.max(0, score - 1);
            scoreLabel.setText("Score: " + score);
            if (missSound != null) missSound.play();
        }

        // Level up every 5 hits
        int computedLevel = 1 + (hits / 5);
        if (computedLevel > lastLevelReached) {
            level = computedLevel;
            lastLevelReached = computedLevel;
            levelLabel.setText("Level: " + level);

            double newSpeedSeconds = Math.max(0.3, 1.0 - (level * 0.1));
            restartMoveTimeline(newSpeedSeconds);
        }
    }

    /** Ends the game and shows final stats. */
    private void endGame() {
        if (!gameRunning) return;
        gameRunning = false;

        stopTimelines();

        double accuracy = (totalTargets > 0) ? (hits * 100.0 / totalTargets) : 0.0;

        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Game Over");
            alert.setHeaderText("Time is up!");
            alert.setContentText(
                    "Final Score: " + score +
                            "\nTargets appeared: " + totalTargets +
                            "\nHits: " + hits +
                            "\nAccuracy: " + String.format("%.1f", accuracy) + "%" +
                            "\nLevel reached: " + level
            );
            alert.showAndWait();
        });
    }

    /** Shows quick stats in the score label. */
    private void showStats() {
        double accuracy = (totalTargets > 0) ? (hits * 100.0 / totalTargets) : 0.0;
        scoreLabel.setText("Appeared: " + totalTargets +
                " | Hits: " + hits +
                " | Accuracy: " + String.format("%.1f", accuracy) + "%");
    }

    // --- Helper methods ---

    /** Places a circle randomly in the game area. */
    private void placeCircleRandomly(Circle c) {
        double x = random.nextDouble() * (gameArea.getWidth() - c.getRadius() * 2) + c.getRadius();
        double y = random.nextDouble() * (gameArea.getHeight() - c.getRadius() * 2) + c.getRadius();
        c.setCenterX(x);
        c.setCenterY(y);
    }

    /** Returns a random color from a small palette. */
    private Color randomColor() {
        Color[] colors = { Color.RED, Color.BLUE, Color.GREEN, Color.ORANGE };
        return colors[random.nextInt(colors.length)];
    }

    /** Starts the movement timeline at the given interval. */
    private void startMoveTimeline(double seconds) {
        moveTimeline = new Timeline(new KeyFrame(Duration.seconds(seconds), e -> moveTargets()));
        moveTimeline.setCycleCount(Animation.INDEFINITE);
        moveTimeline.play();
    }

    /** Restarts the movement timeline with a new interval. */
    private void restartMoveTimeline(double seconds) {
        if (moveTimeline != null) moveTimeline.stop();
        startMoveTimeline(seconds);
    }

    /** Starts the countdown timer timeline (30s -> 0). */
    private void startTimerTimeline() {
        timerTimeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            if (!gameRunning) return;
            timeLeft--;
            timerLabel.setText("Time: " + timeLeft + "s");
            if (timeLeft <= 0) {
                endGame();
            }
        }));
        timerTimeline.setCycleCount(Animation.INDEFINITE);
        timerTimeline.play();
    }

    /** Stops both timelines safely. */
    private void stopTimelines() {
        if (moveTimeline != null) moveTimeline.stop();
        if (timerTimeline != null) timerTimeline.stop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
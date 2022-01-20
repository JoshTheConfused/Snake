import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import java.util.ArrayList;

public class SnakeDriver extends Application {
    private boolean started = false, gameOver = true;
    private GraphicsContext gc;
    private Snake snake = new Snake();
    private boolean up,down,left,right;
    private ArrayList<Integer> animationQueue = new ArrayList<>();
    private boolean normalMode;
    private int frameWait = 0;
    private GridSpace apple = new GridSpace(7,12,0,0);
    private int growR, growC;
    private boolean animationBuffer = false;

    public void start(Stage stage) {
        for (int i=0; i<4; i++)
            animationQueue.add(3);
        Group root = new Group();
        Scene scene = new Scene(root);
        Canvas canvas = new Canvas(1440,960); //each square is 60x60, each snake piece will be smaller (50x50 or so)
        gc = canvas.getGraphicsContext2D();

        scene.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent t) {
                //System.out.println(up + ", " + down + ", " + left + ", " + right); //test
                //Detects keyboard inputs and changes booleans correspondingly.
                if (t.getCode() == KeyCode.W || t.getCode() == KeyCode.UP)
                    up = false;
                if (t.getCode() == KeyCode.S || t.getCode() == KeyCode.DOWN)
                    down = false;
                if (t.getCode() == KeyCode.A || t.getCode() == KeyCode.LEFT)
                    left = false;
                if (t.getCode() == KeyCode.D || t.getCode() == KeyCode.RIGHT)
                    right = false;
            }
        });

        scene.addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent t) {
                //System.out.println(up + ", " + down + ", " + left + ", " + right); //test
                if (t.getCode() == KeyCode.W || t.getCode() == KeyCode.UP)
                    up = true;
                if (t.getCode() == KeyCode.S || t.getCode() == KeyCode.DOWN)
                    down = true;
                if (t.getCode() == KeyCode.A || t.getCode() == KeyCode.LEFT)
                    left = true;
                if (t.getCode() == KeyCode.D || t.getCode() == KeyCode.RIGHT)
                    right = true;
            }
        });
        //System.out.println(up + ", " + down + ", " + left + ", " + right);
        scene.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                if (gameOver) {
                    gameOver = false;
                    started = false;
                }
                if (!started) {
                    started = true;
                    snake = new Snake();
                    if (t.getX() > 650)
                        normalMode = true;
                    else
                        normalMode = false;
                    apple = new GridSpace(7,12,0,0);
                }
            }
        });

        root.getChildren().add(canvas);
        stage.setTitle("Snake");
        stage.setScene(scene);
        stage.show();

        new AnimationTimer() {
            @Override
            public void handle(long now) {
                drawFrame();
                if (normalMode)
                    frameWait ++;
            }
        }.start();
    }

    public void drawFrame() {
        gc.setFill(Color.WHITE);
        gc.fillRect(0,0,1440,960);
        //title screens
        if (gameOver) {
            gc.setFont(Font.font("Arial",FontWeight.BOLD, 50));
            gc.setFill(Color.BLACK);
            gc.fillText("Click to Start", 550,320);
            gc.fillText("Step Mode",320,640);
            gc.fillText("Normal Mode", 780,640);
        }
        else {
            drawSnake();
            drawApple();
        }
        if (started) {
            gameOver = snake.lost() || snake.won();
        }
    }

    public void drawSnake() {
        for (int r=0; r<16; r++)
            for (int c=0; c<24; c++) {
                int x = c*60;
                int y = r*60;
                gc.setFill(Color.BLACK);
                gc.fillRect(x,y,60,60);
                gc.setFill(Color.GRAY);
                gc.fillRect(x+2,y+2,56,56);
            }
        //connect the snake's body segments form underneath it.
        int x = (snake.getTail().getCol()*60)+10;
        int y = (snake.getTail().getRow()*60)+10;
        gc.setFill(Color.CHARTREUSE);
        gc.fillRect(x,y,40,40);
        for (int i=0; i<animationQueue.size();)
            animationQueue.remove(i);
        animationQueue.addAll(snake.getQueue());
        animationQueue.remove(0);
        for (Integer i : animationQueue) {
            if (i == 0) {
                y -= 60;
                gc.fillRect(x,y,40,100);
            }
            else if (i == 1) {
                gc.fillRect(x, y, 40, 100);
                y += 60;
            }
            else if (i == 2) {
                x -= 60;
                gc.fillRect(x,y,100,40);
            }
            else if (i == 3) {
                gc.fillRect(x,y,100,40);
                x += 60;
            }
        }
        //draw green where the snake is.
        for (int r=0; r<16; r++)
            for (int c=0; c<24; c++) {
                if (snake.spaceIsFull(r,c)) {
                    if (snake.getHead().getRow() == r && snake.getHead().getCol() == c) {
                        gc.setFill(Color.GREEN);
                    }
                    else {
                        gc.setFill(Color.CHARTREUSE);
                    }
                    int x1 = (c*60)+10;
                    int y1 = (r*60)+10;
                    gc.fillRect(x1,y1,40,40);
                }
            }
        //auto movement
        if (frameWait >= 60) {
            if (snake.direction() == 0)
                up = true;
            if (snake.direction() == 1)
                down = true;
            if (snake.direction() == 2)
                left = true;
            if (snake.direction() == 3)
                right = true;
        }
        if (up) {
            if (snake.direction() != 1) {
                snake.addQueue(0);
                snake.useQueue();
                frameWait = 0;
                if (snake.isGrowing()) {
                    snake.setGrow(false);
                    snake.grow(growR,growC);
                    animationBuffer = false;
                }
            }
            up = false;
        }
        if (down) {
            if (snake.direction() != 0) {
                snake.addQueue(1);
                snake.useQueue();
                frameWait = 0;
                if (snake.isGrowing()) {
                    snake.setGrow(false);
                    snake.grow(growR, growC);
                    animationBuffer = false;
                }
            }
            down = false;
        }
        if (left) {
            if (snake.direction() != 3) {
                snake.addQueue(2);
                snake.useQueue();
                frameWait = 0;
                if (snake.isGrowing()) {
                    snake.setGrow(false);
                    snake.grow(growR, growC);
                    animationBuffer = false;
                }
            }
            left = false;
        }
        if (right) {
            if (snake.direction() != 2) {
                snake.addQueue(3);
                snake.useQueue();
                frameWait = 0;
                if (snake.isGrowing()) {
                    snake.setGrow(false);
                    snake.grow(growR, growC);
                    animationBuffer = false;
                }
            }
            right = false;
        }
    }

    public void drawApple() {
        int x = (apple.getCol()*60)+10;
        int y = (apple.getRow()*60)+10;
        gc.setFill(Color.RED);
        gc.fillRect(x,y,40,40);
        if (snake.getHead().getCol() == apple.getCol() && snake.getHead().getRow() == apple.getRow()) { //checks if the head is on the apple
            growR = snake.getTail().getRow();
            growC = snake.getTail().getCol();
            snake.setGrow(true);
            animationBuffer = true;
            System.out.println("GROWING");
            int x1 = (int)(Math.random()*24);
            int y1 = (int)(Math.random()*16);
            apple.setPos(y1,x1);
            while (snake.spaceIsFull(y1,x1)) {
                x1 = (int)(Math.random()*24);
                y1 = (int)(Math.random()*16);
                apple.setPos(y1,x1);
                System.out.println(x1 + ", " + y1);
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;

import java.security.Key;

interface Updatable {
    void update();
}

/* Game class will contain all game logic and object construction. All of the
 rules in our game or in game class as well. This class holds, the state of
 the game, win/lose conditions, instantiation and links the other Game Objects.
 */
class Game extends Pane implements Updatable {
    Cloud cloud = new Cloud();

    Pond pond = new Pond();

    Helipad helipad = new Helipad();

    Helicopter helicopter = new Helicopter();

    int counter = 0;

    public Game() {

        this.getChildren().clear();
        this.getChildren().addAll(cloud, pond, helipad, helicopter);

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (counter++ %2 == 0) {
                    helicopter.update();
                }
            }
        };
        timer.start();
    }
    @Override
    public void update() {
    }

    public void heliUp() {
        helicopter.up();
    }
    public void heliDown() {
        helicopter.down();
    }

    public void heliLeft() {
        helicopter.turnLeft();
    }

    public void heliRight() {
        helicopter.turnRight();
    }
}

/* GameObject class contains methods and fields that manage the common
aspects of our game objects. Any state or behaviour should be in this class.
 */
class GameObject extends Group {
    protected Translate myTranslate;
    protected Rotate myRotate;
    protected Scale myScale;

    public GameObject() {
        myTranslate = new Translate();
        myRotate = new Rotate();
        myScale = new Scale();
        this.getTransforms().addAll(myTranslate, myRotate, myScale);
    }
    public void rotate(double degrees) {
        myRotate.setAngle(degrees);
        myRotate.setPivotX(0);
        myRotate.setPivotY(0);
    }

    public void scale(double sx, double sy) {
        myScale.setX(sx);
        myScale.setY(sy);
    }

    public void translate(double tx, double ty) {
        myTranslate.setX(tx);
        myTranslate.setY(ty);
    }

    public void update(){
        for(Node n : getChildren()){
            if(n instanceof Updatable)
                ((Updatable)n).update();
        }
    }
    void add(Node node) {
        this.getChildren().add(node);
    }
}

class Pond extends GameObject {
    public Pond() {
        Ellipse pond = new Ellipse();
        pond.setRadiusX(20);
        pond.setRadiusY(20);
        pond.setFill(Color.BLUE);
        pond.setTranslateX(300);
        pond.setTranslateY(400);
        add(pond);
    }
}

class Cloud extends GameObject {
    public Cloud() {
        super();
        Ellipse cloud = new Ellipse();
        cloud.setRadiusX(50);
        cloud.setRadiusY(50);
        cloud.setFill(Color.WHITE);
        cloud.setTranslateX(150);
        cloud.setTranslateY(600);

        /*double randomX = Math.random() * (GameApp.GAME_WIDTH - cloud.getRadiusX());
        double randomY =
                Math.random() * (GameApp.GAME_HEIGHT * 2/3 - cloud.getRadiusY());
        cloud.setCenterX(randomX);
        cloud.setCenterY(randomY);*/

        add(cloud);
    }
}
class Helipad extends GameObject {
    public Helipad() {
        super();
        Rectangle rectangle = new Rectangle();
        rectangle.setWidth(100);
        rectangle.setHeight(100);
        rectangle.setFill(Color.TRANSPARENT);
        rectangle.setStyle("-fx-stroke: grey; " +
                "-fx-stroke-width: 2;");
        rectangle.setTranslateX(150);
        rectangle.setTranslateY(25);
        add(rectangle);

        Ellipse ellipse = new Ellipse();
        ellipse.setRadiusX(40);
        ellipse.setRadiusY(40);
        ellipse.setFill(Color.TRANSPARENT);
        ellipse.setStyle("-fx-stroke: grey;" + "-fx-stroke-width: 2;");
        ellipse.setTranslateX(200);
        ellipse.setTranslateY(75);
        add(ellipse);

    }

}

class Helicopter extends GameObject implements Updatable{
    double speed = 0;
    double maxSpeed = 10;
    int minSpeed = -2;

    public Helicopter() {
        super();
        Circle helicopter = new Circle(30, 30, 15);
        helicopter.setFill(Color.YELLOW);
        //helicopter.setCenterX(200);
        //helicopter.setCenterY(75);
        this.translate(170, 50);
        add(helicopter);

        Line heading = new Line();
        heading.setStartX(helicopter.getCenterX());
        heading.setStartY(helicopter.getCenterY());
        heading.setEndX(helicopter.getCenterX());
        heading.setEndY(helicopter.getCenterY() + 35);
        heading.setStroke(Color.YELLOW);
        add(heading);


        this.getTransforms().clear();
        this.getTransforms().addAll(myRotate, myTranslate);
    }

    public void up() {
        // increases the speed
        if (speed < maxSpeed){
            speed += 0.1;
        }
    }
    public void down() {
        // decreases the speed
        if (speed > minSpeed){
            speed -= 0.1;
        }
    }

    public void turnLeft() {

    }

    public void turnRight() {

    }

    @Override
    public void update() {
    // does the movement of the helicopter
    //myTranslate.setY(myTranslate.getY() + Math.sin(Math.toRadians(myRotate
        // .getAngle() * speed)));
    //myTranslate.setX(myTranslate.getX() + Math.sin(Math.toRadians(myRotate
        // .getAngle() * speed)));
        this.translate(myTranslate.getX(), myTranslate.getY() + speed);
    this.getTransforms().clear();
    this.getTransforms().addAll(myRotate, myTranslate);
    // updates the position of a helicopter
    }
}

class GameText extends GameObject {
    Text text;

    public GameText(String textString) {
        text = new Text(textString);
        text.setScaleY(-1);
        text.setFont(Font.font(12));
        add(text);
    }
}

public class GameApp extends Application {
        /* CONSTANTS */
        public static final int GAME_WIDTH = 400;
        public static final int GAME_HEIGHT = 800;

        Game root = new Game();
    @Override
        public void start(Stage primaryStage) {

            root.setScaleY(-1); // flipping the display (screen) to avoid mirroring

            Scene scene = new Scene(root, GAME_WIDTH, GAME_HEIGHT);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Rain Maker - Nataly Avalos");
            scene.setFill(Color.BLACK);


            scene.setOnKeyPressed(e -> {
                if (e.getCode() == KeyCode.UP){
                    root.heliUp();
                }
                if (e.getCode() == KeyCode.DOWN){
                    root.heliDown();
                }
                if (e.getCode() == KeyCode.LEFT){
                    root.heliLeft();
                }
                if (e.getCode() == KeyCode.RIGHT){
                    root.heliRight();
                }
        });

            primaryStage.show();
        }
        public static void main(String[] args) {launch(args);}
}

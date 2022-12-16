import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
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

    public void ignition() {
        helicopter.ignition();
    }

    public void boundingBoxes() {
        cloud.showCloudBounding();
        helipad.showHelipadBounding();
        helicopter.showHeliBounding();
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
        Circle pond = new Circle(40,40,20);
        pond.setFill(Color.BLUE);
        pond.setTranslateX(300);
        pond.setTranslateY(400);
        add(pond);

        GameText pondPercent = new GameText("0%");
        pondPercent.setTranslateX(pond.getTranslateX() + pond.getCenterX() - 5);
        pondPercent.setTranslateY(pond.getTranslateY() + pond.getCenterY() + 5);
        pondPercent.setFill(Color.WHITE);
        add(pondPercent);
    }
}

class Cloud extends GameObject implements Updatable{
    Rectangle boundingBox = new Rectangle();

    public Cloud() {
        super();
        Circle cloud = new Circle(100,100,50);
        cloud.setFill(Color.WHITE);
        cloud.setTranslateX(50);
        cloud.setTranslateY(500);
        add(cloud);

        GameText cloudPercent = new GameText("0%");
        cloudPercent.setTranslateX(cloud.getTranslateX() + cloud.getCenterX() - 5);
        cloudPercent.setTranslateY(cloud.getTranslateY() + cloud.getCenterY() + 5);
        cloudPercent.setFill(Color.BLUE);
        add(cloudPercent);

        boundingBox.setWidth(100);
        boundingBox.setHeight(100);
        boundingBox.setFill(Color.TRANSPARENT);
        boundingBox.setStyle("-fx-stroke: yellow; " +
                "-fx-stroke-width: 0.5;");
        boundingBox.setTranslateX(cloud.getTranslateX() + cloud.getCenterX() - 50);
        boundingBox.setTranslateY(cloud.getTranslateY() + cloud.getCenterY() - 50);
        boundingBox.setVisible(false);
        add(boundingBox);
        /*double randomX = Math.random() * (GameApp.GAME_WIDTH - cloud.getRadiusX());
        double randomY =
                Math.random() * (GameApp.GAME_HEIGHT * 2/3 - cloud.getRadiusY());
        cloud.setCenterX(randomX);
        cloud.setCenterY(randomY);*/
    }

    public void showCloudBounding() {
        if (boundingBox.isVisible())
            boundingBox.setVisible(false);
        else if (!boundingBox.isVisible())
            boundingBox.setVisible(true);
    }
}
class Helipad extends GameObject {
    Rectangle boundingBox = new Rectangle();

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

        boundingBox.setWidth(100);
        boundingBox.setHeight(100);
        boundingBox.setFill(Color.TRANSPARENT);
        boundingBox.setStyle("-fx-stroke: yellow; " +
                "-fx-stroke-width: 0.5;");
        boundingBox.setTranslateX(rectangle.getTranslateX() + rectangle.getWidth() - 100);
        boundingBox.setTranslateY(rectangle.getTranslateY() + rectangle.getHeight() - 100);
        boundingBox.setVisible(false);
        add(boundingBox);
    }

    public void showHelipadBounding() {
        if (boundingBox.isVisible())
            boundingBox.setVisible(false);
        else if (!boundingBox.isVisible())
            boundingBox.setVisible(true);
    }
}

class Helicopter extends GameObject implements Updatable{
    double speed = 0;
    double maxSpeed = 10;
    int minSpeed = -2;
    int heading = 0;
    boolean ignition = false;
    int fuel = 25000;
    Rectangle boundingBox = new Rectangle();
    GameText fuelText = new GameText("F:" + fuel);

    public Helicopter() {
        super();
        Circle helicopter = new Circle(30, 30, 15);
        helicopter.setFill(Color.YELLOW);
        this.translate(170, 45);
        add(helicopter);

        Line heading = new Line();
        heading.setStartX(helicopter.getCenterX());
        heading.setStartY(helicopter.getCenterY());
        heading.setEndX(helicopter.getCenterX());
        heading.setEndY(helicopter.getCenterY() + 35);
        heading.setStroke(Color.YELLOW);
        add(heading);

        fuelText.setTranslateX(10);
        fuelText.setTranslateY(10);
        fuelText.setFill(Color.YELLOW);
        add(fuelText);

        boundingBox.setWidth(70);
        boundingBox.setHeight(70);
        boundingBox.setFill(Color.TRANSPARENT);
        boundingBox.setStyle("-fx-stroke: yellow; " +
                "-fx-stroke-width: 0.5;");
        boundingBox.setTranslateX(-5);
        boundingBox.setTranslateY(-5);
        boundingBox.setVisible(false);
        add(boundingBox);

        this.getTransforms().clear();
        this.getTransforms().addAll(myRotate, myTranslate);
    }

    public void showHeliBounding() {
        if (boundingBox.isVisible())
            boundingBox.setVisible(false);
        else if (!boundingBox.isVisible())
            boundingBox.setVisible(true);
    }
    public void ignition() {
        ignition =! ignition;
    }
    public void up() {
        // increases the speed by 0.1
        if (ignition && speed < maxSpeed){
            speed += 0.1;
        }
    }
    public void down() {
        // decreases the speed by 0.1
        if (ignition && speed > minSpeed){
            speed -= 0.1;
        }
    }
    public void turnLeft() {
        // changes the heading of the helicopter by 15 degrees left
        if (ignition) {
            heading += 15;
        }
    }
    public void turnRight() {
        // changes the heading of the helicopter by 15 degrees right
        if (ignition) {
            heading -= 15;
        }
    }
    private void consumeFuel() {
        fuel -= 1;
    }

    @Override
    public void update() {
        // does the movement of the helicopter

        double deltaX = -speed * Math.sin(Math.toRadians(heading));
        double deltaY = speed * Math.cos(Math.toRadians(heading));

        this.translate(myTranslate.getX() + deltaX, myTranslate.getY() + deltaY);
        this.rotate(heading);

        this.getTransforms().clear();
        this.getTransforms().addAll(myTranslate, myRotate);

        // count down fuel
        if (ignition) {
            this.consumeFuel();
            fuelText.setText("F:" + fuel);
        }
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
    public GameText() {this("");}
    public void setText(String textString) {text.setText(textString);}

    public void setFill(Color color) {
        text.setFill(color);
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
                if (e.getCode() == KeyCode.I){
                    root.ignition();
                }
                if (e.getCode() == KeyCode.B){
                    root.boundingBoxes();
                }
        });

            primaryStage.show();
        }
        public static void main(String[] args) {launch(args);}
}

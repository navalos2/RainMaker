import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
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
    BackgroundImage backgroundImage = new BackgroundImage();

    AnimationTimer timer;
    int counter = 0;
    StringBuilder msg = new StringBuilder();
    Alert alert;

    public Game() {init();}

    public void init() {

        this.getChildren().clear();
        this.getChildren().addAll(backgroundImage, cloud, pond, helipad,
                helicopter);

        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (counter++ %2 == 0) {
                    // call each classes update method
                    helicopter.update();
                    cloud.update();
                    pond.update();
                    winloseConditions();

                    // check to see if the cloud has reached 30% cloudSaturation.
                    // if so, fill the pond
                    if(cloud.isCloudSaturation30()) {
                        pond.fillPond();
                    }
                }
            }
        };
        timer.start();
    }
    @Override
    public void update() {
    }

    // key event methods
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

    public void resetGame() {
        pond.resetPond();
        cloud.resetCloud();
        helicopter.resetHeli();
        helipad.resetHelipad();
        //this.getChildren().clear();

        cloud = new Cloud();
        pond = new Pond();
        helicopter = new Helicopter();
        helipad = new Helipad();

        init();
    }

    // check to see if cloud and helicopter intersect (same as Pong/Asteroids)
    public void cloudSeeding() {
        if (!Shape.intersect(helicopter.getBounds(),
                cloud.getBounds()).getBoundsInLocal().isEmpty()) {
            cloud.cloudSeeding();
        }
    }

    // determines win/lose conditions
    public void winloseWindow() {
        alert = new Alert(Alert.AlertType.CONFIRMATION, msg.toString(),
                ButtonType.YES, ButtonType.NO);

        alert.setOnHidden(e -> {
            if (alert.getResult() == ButtonType.YES) {
                resetGame();
                //timer.start();
            } else
                Platform.exit();
        });
        alert.show();
    }

    public void winloseConditions() {
        if (pond.fill == 100) {
            timer.stop();
            msg.append("You Win! Play Again?");

            winloseWindow();
        }

        if (helicopter.fuel == 0) {
            timer.stop();
            msg.append("You Lose! Play Again?");

            winloseWindow();
        }
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

/* The following classes hold our objects on the scene. Pond class, Cloud
class, Helipad class, Helicopter class */
class Pond extends GameObject implements Updatable {
    private GameText pondPercent;
    Circle pond;
    int fill = 28;
    int radius = 20;

    public Pond() {
        pond = new Circle(40,40, radius);
        pond.setFill(Color.BLUE);
        pond.setTranslateX(600);
        pond.setTranslateY(400);
        add(pond);

        pondPercent = (new GameText("28%"));
        pondPercent.setTranslateX(pond.getTranslateX() + pond.getCenterX() - 5);
        pondPercent.setTranslateY(pond.getTranslateY() + pond.getCenterY() + 5);
        pondPercent.setFill(Color.WHITE);
        add(pondPercent);
    }

    public void fillPond() {
        if (fill < 100) {
            pondPercent.setText("%" + fill++);
            radius++;
        }
    }

    public void update() {
        pondPercent.setText("%" + fill);
        pond.setRadius(radius);
    }

    public void resetPond() {
        this.getChildren().clear();
    }
}

class Cloud extends GameObject implements Updatable{
    Rectangle boundingBox = new Rectangle();
    Circle cloud = new Circle(100,100,50);
    GameText cloudPercent = new GameText("0%");

    int cloudSaturation = 0;
    int delayDesaturation = 0;
    int r = 255;
    int g = 255;
    int b = 255;
    boolean cloudSaturation30 = false;

    public Cloud() {
        super();

        cloud.setFill(Color.rgb(r,g,b));
        cloud.setTranslateX(100);
        cloud.setTranslateY(500);
        add(cloud);

        cloudPercent.setTranslateX(cloud.getTranslateX() + cloud.getCenterX() - 5);
        cloudPercent.setTranslateY(cloud.getTranslateY() + cloud.getCenterY() + 5);
        cloudPercent.setFill(Color.BLUE);
        add(cloudPercent);

        boundingBox.setWidth(100);
        boundingBox.setHeight(100);
        boundingBox.setFill(Color.TRANSPARENT);
        boundingBox.setStyle("-fx-stroke: yellow; " +
                "-fx-stroke-width: 1;");
        boundingBox.setTranslateX(cloud.getTranslateX() + cloud.getCenterX() - 50);
        boundingBox.setTranslateY(cloud.getTranslateY() + cloud.getCenterY() - 50);
        boundingBox.setVisible(false);
        add(boundingBox);
    }

    public void showCloudBounding() {
        if (boundingBox.isVisible())
            boundingBox.setVisible(false);
        else if (!boundingBox.isVisible())
            boundingBox.setVisible(true);
    }

    public Shape getBounds() { return boundingBox; }

    boolean isCloudSaturation30() {
        return cloudSaturation30;
    }

    public void cloudSeeding() {
        if (cloudSaturation <= 100) {
            cloudPercent.setText("%" + cloudSaturation++);
            cloud.setFill(Color.rgb(r--, g--, b--));
        }
    }

    public void update() {
        // delay desaturation, otherwise cloud desaturates too fast
        if (delayDesaturation == 0) {
            delayDesaturation = 50;
            // desaturates the cloud if no seeding is being done
            if (cloudSaturation != 0) {
                cloudPercent.setText("%" + cloudSaturation--);
                cloud.setFill(Color.rgb(r++, g++, b++));
                if (cloudSaturation > 30) {
                    cloudSaturation30 = true;
                 }
            }
        }
        else if (delayDesaturation < 51) {
            delayDesaturation--;
            cloudSaturation30 = false;
        }
    }

    public void resetCloud() {
        this.getChildren().clear();
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
        rectangle.setTranslateX(350);
        rectangle.setTranslateY(25);
        add(rectangle);

        Ellipse ellipse = new Ellipse();
        ellipse.setRadiusX(40);
        ellipse.setRadiusY(40);
        ellipse.setFill(Color.TRANSPARENT);
        ellipse.setStyle("-fx-stroke: grey;" + "-fx-stroke-width: 2;");
        ellipse.setTranslateX(400);
        ellipse.setTranslateY(75);
        add(ellipse);

        boundingBox.setWidth(100);
        boundingBox.setHeight(100);
        boundingBox.setFill(Color.TRANSPARENT);
        boundingBox.setStyle("-fx-stroke: yellow; " +
                "-fx-stroke-width: 1;");
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

    public void resetHelipad() {
        this.getChildren().clear();
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
    HeliBody heliBody = new HeliBody();
    HeliBlade heliBlade = new HeliBlade();

    public Helicopter() {
        super();
        /*Circle helicopter = new Circle(30, 30, 15);
        helicopter.setFill(Color.YELLOW);
        this.translate(370, 45);
        add(helicopter);

        Line heading = new Line();
        heading.setStartX(helicopter.getCenterX());
        heading.setStartY(helicopter.getCenterY());
        heading.setEndX(helicopter.getCenterX());
        heading.setEndY(helicopter.getCenterY() + 35);
        heading.setStroke(Color.YELLOW);
        add(heading);*/
        heliBody.setScaleX(-0.2);
        heliBody.setScaleY(-0.2);
        heliBody.setTranslateX(145);
        heliBody.setTranslateY(-180);
        add(heliBody);

        heliBlade.setScaleX(0.4);
        heliBlade.setScaleY(0.4);
        heliBlade.setTranslateX(285);
        heliBlade.setTranslateY(-10);
        add(heliBlade);

        fuelText.setTranslateX(380);
        fuelText.setTranslateY(30);
        fuelText.setFill(Color.YELLOW);
        add(fuelText);

        boundingBox.setWidth(80);
        boundingBox.setHeight(80);
        boundingBox.setFill(Color.TRANSPARENT);
        boundingBox.setStyle("-fx-stroke: yellow; " +
                "-fx-stroke-width: 1;");
        boundingBox.setTranslateX(360);
        boundingBox.setTranslateY(35);
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

    public Shape getBounds() { return boundingBox; }

    public void resetHeli() {
        this.getChildren().clear();
    }
}

/* A class for any text needed for the game such as percentages and the fuel
text for helicopter. */
class GameText extends GameObject {
    Text text;

    public GameText(String textString) {
        text = new Text(textString);
        text.setScaleY(-1);
        text.setFont(Font.font(String.valueOf(FontWeight.BOLD),12));
        add(text);
    }
    public GameText() {this("");}
    public void setText(String textString) {text.setText(textString);}

    public void setFill(Color color) {
        text.setFill(color);
    }
}

class HeliBody extends GameObject {
    public HeliBody() {
        Image heliBody = new Image("heliBody.png");
        ImageView heliBodyImageView = new ImageView(heliBody);
        add(heliBodyImageView);
    }
}

class HeliBlade extends GameObject {
    public HeliBlade() {
        Image heliBlade = new Image("heliBlade.png");
        ImageView heliBladeImageView = new ImageView(heliBlade);
        add(heliBladeImageView);
    }
}

class BackgroundImage extends GameObject {
    public BackgroundImage() {
        Image background = new Image("rainmaker-background.png");
        ImageView backgroundImageView = new ImageView(background);
        add(backgroundImageView);
    }
}

/* At the highest level we have the class GameApp. This class extends the
JavaFX Application class. The purpose of this class is to manage the high-level
aspects of our application and setup and show the initial Scene for your
application. The GameApp class sets up all keyboard event handlers to invoke
public methods in Game.*/
public class GameApp extends Application {
        /* CONSTANTS */
        public static final int GAME_WIDTH = 800;
        public static final int GAME_HEIGHT = 800;

        Game root = new Game();

    @Override
        public void start(Stage primaryStage) {
            root.setScaleY(-1); // flipping the display (screen) to avoid mirroring

            root.setStyle("-fx-background-color: black;");

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
                if (e.getCode() == KeyCode.SPACE){
                    root.cloudSeeding();
                }
                if (e.getCode() == KeyCode.R) {
                    root.resetGame();
                }
            });
            primaryStage.show();
        }
        public static void main(String[] args) {launch(args);}
}

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

import java.util.LinkedList;
import java.util.Random;


interface Updatable {
    void update();
}

/* Game class will contain all game logic and object construction. All of the
 rules in our game or in game class as well. This class holds, the state of
 the game, win/lose conditions, instantiation and links the other Game Objects.
 */
class Game extends Pane implements Updatable {
    //Cloud cloud = new Cloud();
    //Pond pond = new Pond();
    Helipad helipad = new Helipad();
    Helicopter helicopter = new Helicopter();
    BackgroundImage backgroundImage = new BackgroundImage();

    AnimationTimer timer;
    int counter = 0;
    StringBuilder msg = new StringBuilder();
    Alert alert;

    CloudLinkedList cloudLinkedList = new CloudLinkedList();
    PondLinkedList pondLinkedList = new PondLinkedList();

    public Game() {init();}

    public void init() {

        this.getChildren().clear();
        this.getChildren().addAll(backgroundImage, cloudLinkedList, pondLinkedList,
                helipad,
                helicopter);

        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (counter++ % 2 == 0) {
                    // call each classes update method
                    helicopter.update();
                    winloseConditions();

                    // check to see if the cloud has reached 30% cloudSaturation.
                    // if so, fill the pond
                    for (int i = 0; i < cloudLinkedList.size(); i++) {
                        if (cloudLinkedList.getCloud(i).isCloudSaturation30()) {
                            pondLinkedList.getPond(i).fillPond();
                        }
                    }

                    // call update for cloud linked list
                    for (int i = 0; i < cloudLinkedList.size(); i++) {
                        cloudLinkedList.getCloud(i).update();
                    }

                    // call update for pond linked list
                    for (int i = 0; i < pondLinkedList.size(); i++) {
                        pondLinkedList.getPond(i).update();
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
        if (!Shape.intersect(helipad.getBounds(),
                helicopter.getBounds()).getBoundsInLocal().isEmpty()) {
            helicopter.ignition();
        }
    }

    public void boundingBoxes() {
        for (int i = 0; i < cloudLinkedList.size(); i++) {
            cloudLinkedList.getCloud(i).showCloudBounding();
        }
        helipad.showHelipadBounding();
        helicopter.showHeliBounding();
    }

    public void resetGame() {
        pondLinkedList.reset();
        cloudLinkedList.reset();
        helicopter.resetHeli();
        helipad.resetHelipad();

        cloudLinkedList = new CloudLinkedList();
        pondLinkedList = new PondLinkedList();
        helicopter = new Helicopter();
        helipad = new Helipad();

        init();
    }

    // check to see if cloud and helicopter intersect (same as Pong/Asteroids)
    public void cloudSeeding() {
        for (int i = 0; i < (cloudLinkedList.size()); i++) {
            if(!Shape.intersect(cloudLinkedList.getCloud(i).getBounds(),
                    helicopter.getBounds()).getBoundsInLocal().isEmpty()){
                cloudLinkedList.getCloud(i).cloudSeeding();
            }
        }
        /*
        if ((helicopter.state instanceof ReadyState) && (!Shape.intersect(helicopter.getBounds(),
                cloud.getBounds()).getBoundsInLocal().isEmpty())) {
            cloud.cloudSeeding();
        }*/
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
        boolean pondIsFull = false;

        for (int i = 0; i < pondLinkedList.size(); i++) {
            if (pondLinkedList.getPond(i).fill == 100
                    && (!Shape.intersect(helicopter.getBounds(),
                    helipad.getBounds()).getBoundsInLocal().isEmpty())
                    && (helicopter.state instanceof OffState)) {
                pondIsFull = true;
            }
        }

        // Win Condition: IF the pond fills up to 100% AND the helicopter is
        // within bounds of the helipad AND the ignition is off THEN you win
        // the game.
        if (pondIsFull) {
            timer.stop();
            msg.append("You Win! Play Again?");

            winloseWindow();
        }
        // Lose Condition: IF the helicopter fuel runs out before you can
        // seed, fill pond, and land your helicopter, THEN you lose the game.
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
    Random random = new Random();
    int fill;
    int radius = random.nextInt(10,30);

    GameText pondPercent;
    Circle pond;

    public Pond() {
        fill = random.nextInt(10,30);

        pond = new Circle(radius);
        pond.setFill(Color.BLUE);
        add(pond);

        pondPercent = new GameText("28%");
        pondPercent.setTranslateX(-5);
        pondPercent.setTranslateY(5);
        pondPercent.setFill(Color.WHITE);
        add(pondPercent);

        this.translate(random.nextInt(0,GameApp.GAME_WIDTH),
                random.nextInt(GameApp.GAME_HEIGHT/3, GameApp.GAME_HEIGHT));
        this.getTransforms().clear();
        this.getTransforms().add(myTranslate);
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

class PondLinkedList extends GameObject implements Updatable{
    LinkedList<Pond> pondLinkedList;

    public PondLinkedList() {
        pondLinkedList = new LinkedList<>();

        for (int i = 0; i < Math.random() * 3; i++) {
            Pond pond = new Pond();
            pondLinkedList.add(pond);
            this.getChildren().add(pond);
        }
    }

    public void reset() {
        for (Pond pond: this.pondLinkedList){
            pond.resetPond();
        }
    }

    public Pond getPond(int i){return pondLinkedList.get(i);}
    public int size(){return pondLinkedList.size();}

}

class Cloud extends GameObject implements Updatable{
    Random random = new Random();
    int radius = random.nextInt(40,60);
    int cloudSaturation = 0;
    int delayDesaturation = 0;
    int r = 255;
    int g = 255;
    int b = 255;
    boolean cloudSaturation30 = false;

    Rectangle boundingBox = new Rectangle();
    Circle cloud = new Circle(radius);
    GameText cloudPercent = new GameText("0%");

    CloudStates state;

    public Cloud() {
        super();

        cloud.setFill(Color.rgb(r,g,b));
        add(cloud);

        cloudPercent.setTranslateX(-5);
        cloudPercent.setTranslateY(5);
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

        this.translate(random.nextInt(0,GameApp.GAME_WIDTH),
                random.nextInt(GameApp.GAME_HEIGHT/3, GameApp.GAME_HEIGHT));
        this.getTransforms().clear();
        this.getTransforms().add(myTranslate);    }

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

    public void changeState(CloudStates cloudStates) { this.state = state; }
}

class CloudLinkedList extends GameObject implements Updatable{
    LinkedList<Cloud> cloudLinkedList;

    public CloudLinkedList() {
        cloudLinkedList = new LinkedList<>();

        for (int i = 0; i < Math.random() * 3; i++) {
            Cloud cloud = new Cloud();
            cloudLinkedList.add(cloud);
            this.getChildren().add(cloud);
        }
    }

    public void reset() {
        for (Cloud cloud: this.cloudLinkedList){
            cloud.resetCloud();
        }
    }

    public Cloud getCloud(int i){return cloudLinkedList.get(i);}
    public int size(){return cloudLinkedList.size();}

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

    public Shape getBounds() {return boundingBox;}

    public void resetHelipad() {
        this.getChildren().clear();
    }
}

class Helicopter extends GameObject implements Updatable{
    double speed = 0;
    double maxSpeed = 10;
    int minSpeed = -2;
    int heading = 0;
    int fuel = 25000;

    Rectangle boundingBox = new Rectangle();
    GameText fuelText = new GameText("F:" + fuel);
    HeliBody heliBody = new HeliBody();
    HeliBlade heliBlade = new HeliBlade();

    // state stuff
    HeliStates state;
    int bladeSpeed = 0;

    public Helicopter() {
        super();
        this.translate(400, 75);

        state = new OffState(this);

        add(heliBody);
        add(heliBlade);

        fuelText.setTranslateX(-20);
        fuelText.setTranslateY(-40);
        fuelText.setFill(Color.YELLOW);
        add(fuelText);

        boundingBox.setWidth(80);
        boundingBox.setHeight(80);
        boundingBox.setFill(Color.TRANSPARENT);
        boundingBox.setStyle("-fx-stroke: yellow; " +
                "-fx-stroke-width: 1;");
        boundingBox.setTranslateX(-40);
        boundingBox.setTranslateY(-35);
        boundingBox.setVisible(false);
        add(boundingBox);

        this.getTransforms().clear();
        this.getTransforms().addAll(myTranslate, myRotate);
    }

    public void showHeliBounding() {
        if (boundingBox.isVisible())
            boundingBox.setVisible(false);
        else if (!boundingBox.isVisible())
            boundingBox.setVisible(true);
    }

    public void ignition() {
        state.IgnitionKey();
    }
    public void up() {
        // increases the speed by 0.1
        if (state instanceof ReadyState){
            if(speed < maxSpeed)
                speed += 0.1;
        }
    }
    public void down() {
        // decreases the speed by 0.1
        if (state instanceof ReadyState){
            if(speed > minSpeed)
                speed -= 0.1;
        }
    }
    public void turnLeft() {
        // changes the heading of the helicopter by 15 degrees left
        if (state instanceof ReadyState) {
            heading += 15;
        }
    }
    public void turnRight() {
        // changes the heading of the helicopter by 15 degrees right
        if (state instanceof ReadyState) {
            heading -= 15;
        }
    }

    private void consumeFuel() {
        fuel -= 3;
    }

    @Override
    public void update() {
        // does the movement of the helicopter
        rotate(heading);
        this.getTransforms().clear();

        double deltaX = -speed * Math.sin(Math.toRadians(heading));
        double deltaY = speed * Math.cos(Math.toRadians(heading));

        translate(myTranslate.getX() + deltaX, myTranslate.getY() + deltaY);

        this.getTransforms().addAll(myTranslate, myRotate);

        // state stuff
        bladeSpeed = state.UpdateHeliBlade(bladeSpeed);
        heliBlade.update(bladeSpeed);

        // count down fuel
        // state of startingstate because heli should start consuming fuel as
        // the heli is getting ready for take off
        if (state instanceof ReadyState) {
            this.consumeFuel();
            fuelText.setText("F:" + fuel);
        }

    }

    public Shape getBounds() { return boundingBox; }

    public void resetHeli() {
        this.getChildren().clear();
    }

    public void changeState(HeliStates state) { this.state = state; }


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
        scale(-1,-1);
        translate(40,45);
        this.getChildren().add(heliBodyImageView);
    }
}

class HeliBlade extends GameObject {
    AnimationTimer timer;
    int bladeSpeed;

    public HeliBlade() {
        Rectangle heliBlade = new Rectangle();
        heliBlade.setWidth(5);
        heliBlade.setHeight(75);
        heliBlade.setFill(Color.MAGENTA);
        heliBlade.setX(-3);
        heliBlade.setY(-20);
        add(heliBlade);


        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                HeliBlade.this.setRotate(HeliBlade.this.getRotate() + bladeSpeed);
            }
        };
        timer.start();
    }

    public void update(int bladeSpeed) {
        this.bladeSpeed = bladeSpeed;
    }
}

abstract class HeliStates {
    Helicopter helicopter;
    int maxBladeSpeed = 100;

    public HeliStates(Helicopter helicopter) {
        this.helicopter = helicopter;
    }

    abstract void IgnitionKey();
    abstract int UpdateHeliBlade(int bladeSpeed);

}

class OffState extends HeliStates {
    public OffState(Helicopter helicopter) {
        super(helicopter);
    }

    @Override
    void IgnitionKey() {
        // if ignition on then change from off state to starting state
        helicopter.changeState(new StartingState(helicopter));
    }

    @Override
    int UpdateHeliBlade(int bladeSpeed) {
        return 0;
    }
}

class StartingState extends HeliStates {
    public StartingState(Helicopter helicopter) {
        super(helicopter);
    }

    @Override
    void IgnitionKey() {
        // if ignition off then change from starting state to stopping
        helicopter.changeState(new StoppingState(helicopter));
    }

    @Override
    int UpdateHeliBlade(int bladeSpeed) {
            if (bladeSpeed < maxBladeSpeed) {
                bladeSpeed++;
            }
            if (bladeSpeed == maxBladeSpeed) {
                helicopter.changeState(new ReadyState(helicopter));
            }

        return bladeSpeed;
    }
}

class StoppingState extends HeliStates{
    public StoppingState(Helicopter helicopter) {
        super(helicopter);
    }

    @Override
    void IgnitionKey() {
        // if ignition on then change from stopping state to starting
        helicopter.changeState(new StartingState(helicopter));
    }

    @Override
    int UpdateHeliBlade(int bladeSpeed) {
            if (bladeSpeed > 0) {
                bladeSpeed--;
            }
            if (bladeSpeed == 0) {
                helicopter.changeState(new OffState(helicopter));
            }

        return bladeSpeed;
    }
}

class ReadyState extends HeliStates{
    public ReadyState(Helicopter helicopter) {
        super(helicopter);
    }

    @Override
    void IgnitionKey() {
        // if ignition off then change ready state to off state
        helicopter.changeState(new OffState(helicopter));
    }

    @Override
    int UpdateHeliBlade(int bladeSpeed) {
        return maxBladeSpeed;
    }
}

abstract class CloudStates {
    Cloud cloud;

    public CloudStates(Cloud cloud) {
        this.cloud = cloud;
    }

    abstract void CheckCloudState(int cloudPosition);

    abstract boolean DeadCloud();
}

class AliveCloudState extends CloudStates {
    public AliveCloudState(Cloud cloud) {
        super(cloud);
    }

    @Override
    void CheckCloudState(int cloudPosition) {

    }

    @Override
    boolean DeadCloud() {
        return false;
    }

    public void ifCloudExitsRight() {
        if (cloud.getTranslateX() > GameApp.GAME_WIDTH){
            cloud.changeState(new DeadCloudState(cloud));
        }
    }
}

class DeadCloudState extends CloudStates{
    public DeadCloudState(Cloud cloud) {
        super(cloud);
    }

    @Override
    void CheckCloudState(int cloudPosition) {

    }

    @Override
    boolean DeadCloud() {
        return false;
    }
}

class IsVisibleCloudState extends CloudStates {
    public IsVisibleCloudState(Cloud cloud) {
        super(cloud);
    }

    @Override
    void CheckCloudState(int cloudPosition) {

    }

    @Override
    boolean DeadCloud() {
        return false;
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

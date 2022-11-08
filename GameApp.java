import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;

import java.util.Random;

interface Updatable {
    void update();
}

/* Game class will contain all game logic and object construction. All of the
 rules in our game or in game class as well. This class holds, the state of
 the game, win/lose conditions, instantiation and links the other Game Objects.
 */
class Game extends Pane implements Updatable {
    public Game() {
        Cloud cloud = new Cloud();

        Pond pond = new Pond();

        Helipad helipad = new Helipad();
        //helipad.setTranslateX(GameApp.GAME_WIDTH / 2 - 50);
        //helipad.setTranslateY(25);
        helipad.myTranslate.setX(GameApp.GAME_WIDTH / 2 - 50);
        helipad.myTranslate.setY(25);

        Helicopter helicopter = new Helicopter();
        helicopter.myTranslate.setX(helipad.myTranslate.getX());
        helicopter.myTranslate.setY(helipad.myTranslate.getY());

        this.getChildren().clear();
        this.getChildren().addAll(cloud, pond, helipad, helicopter);
    }

    /* Initialize method that is invoked whenever a new game must be played.
       The init() method creates all of the new state of the world including
       the positioning of each game objects.

       DON'T FORGET to clear all children out of the Pane before initializing
       new objects.
     */
    public void init() {

    }

    /* In update() method, you will need to move your helicopter and check
       the win/lose status of the game.
     */
    @Override
    public void update() {

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
    void add(Node node) {
        this.getChildren().add(node);
    }
}

class Pond extends GameObject {
    public Pond() {
        Ellipse pond = new Ellipse();
        pond.setRadiusX(30);
        pond.setRadiusY(30);
        pond.setFill(Color.BLUE);
        pond.setTranslateX(new Random().nextDouble(GameApp.GAME_WIDTH));
        pond.setTranslateY(new Random().nextDouble(GameApp.GAME_HEIGHT) + 100);
        add(pond);
    }
}

class Cloud extends GameObject {
    public Cloud() {
        super();
        Ellipse cloud = new Ellipse();
        cloud.setRadiusX(30);
        cloud.setRadiusY(30);
        cloud.setFill(Color.WHITE);
        cloud.setTranslateX(new Random().nextDouble(GameApp.GAME_WIDTH));
        cloud.setTranslateY(new Random().nextDouble(GameApp.GAME_HEIGHT) + 100);
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
        add(rectangle);

        Ellipse ellipse = new Ellipse();
        ellipse.setRadiusX(40);
        ellipse.setRadiusY(40);
        ellipse.setFill(Color.TRANSPARENT);
        ellipse.setStyle("-fx-stroke: grey;" + "-fx-stroke-width: 2;");
        ellipse.setLayoutX(GameApp.GAME_WIDTH / 2 - 150);
        ellipse.setLayoutY(50);
        add(ellipse);

    }
}

class Helicopter extends GameObject {
    public Helicopter() {
        super();
        Circle body = new Circle(30, 30, 15);
        body.setFill(Color.YELLOW);
        add(body);
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

        @Override
        public void start(Stage primaryStage) throws Exception {
            Game root = new Game();

            root.setScaleY(-1); // flipping the display (screen) to avoid mirroring
            //root.setTranslateX(200);
            //root.setTranslateY(-400);
            Scene scene = new Scene(root, GAME_WIDTH, GAME_HEIGHT);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Rain Maker - Nataly Avalos");
            scene.setFill(Color.BLACK);
            primaryStage.show();

            AnimationTimer timer = new AnimationTimer() {
                @Override
                public void handle(long now) {

                }
            };
            timer.start();
        }

}

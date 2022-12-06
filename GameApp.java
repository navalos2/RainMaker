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

        Helicopter helicopter = new Helicopter();

        /*Helicopter helicopter = new Helicopter(helipad.myTranslate.getX(),
                helipad.myTranslate.getY());*/

        this.getChildren().clear();
        this.getChildren().addAll(cloud, pond, helipad, helicopter);

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();
            }
        };
        timer.start();
    }

    public void up() {
        // using object helicopter

    }
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
        pond.setRadiusX(20);
        pond.setRadiusY(20);
        pond.setFill(Color.BLUE);
        pond.setTranslateX(300);
        pond.setTranslateY(400);
       /* pond.setTranslateX(new Random().nextDouble(GameApp.GAME_WIDTH));
        pond.setTranslateY(new Random().nextDouble(GameApp.GAME_HEIGHT) + 100);*/
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
        /*cloud.setTranslateX(new Random().nextDouble(GameApp.GAME_WIDTH));
        cloud.setTranslateY(new Random().nextDouble(GameApp.GAME_HEIGHT) + 100);*/
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

        //this.myTranslate.setX(350);
        //this.myTranslate.setY(25);
    }

}

class Helicopter extends GameObject implements Updatable{
    boolean upPressed;
    boolean leftPressed;
    boolean rightPressed;

    public Helicopter() {
        super();
        Circle body = new Circle(30, 30, 15);
        body.setFill(Color.YELLOW);
        body.setTranslateX(170);
        body.setTranslateY(45);
        add(body);
    }

    public void increaseSpeed() {
        // focus on implenting something that changes how fast the heli moves

    }


    @Override
    public void update() {
        // actually does the movement
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

        public void keyPressed(KeyEvent evt) {
            KeyCode key = evt.getCode();
            System.out.println("Key Pressed: " + key);  // for testing


            if (key == KeyCode.UP) {
                // call to a method (UP) in game

            }
        }
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
        }

}

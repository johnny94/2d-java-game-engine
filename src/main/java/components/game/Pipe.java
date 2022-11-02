package components.game;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_UP;

import java.util.Optional;

import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;

import components.Component;
import components.PlayerController;
import jade.GameObject;
import jade.KeyListener;
import jade.Window;
import util.AssetPool;

public class Pipe extends Component {
    private static final KeyListener keyListener = KeyListener.getInstance();

    private Direction direction;
    private String connectingPipeName = "";
    private boolean isEntrance;

    private transient Optional<GameObject> connectingPipe;
    private transient float entranceVectorTolerance = 0.6f;
    private transient PlayerController collidingPlayer;

    public Pipe(Direction direction) {
        this.direction = direction;
    }

    @Override
    public void start() {
        connectingPipe = Window.get().getCurrentScene().getGameObject(connectingPipeName);
    }

    @Override
    public void update(float deltaTime) {
        if (!connectingPipe.isPresent()) {
            return;
        }

        if (collidingPlayer != null) {
            boolean playerEntering = false;
            switch(direction) {
                case Up:
                    if (keyListener.isKeyPressed(GLFW_KEY_DOWN) && isEntrance && playerAtEntrance()) {
                        playerEntering = true;
                    }
                    break;
                case Down:
                    if (keyListener.isKeyPressed(GLFW_KEY_UP) && isEntrance && playerAtEntrance()) {
                        playerEntering = true;
                    }
                    break;
                case Left:
                    if (keyListener.isKeyPressed(GLFW_KEY_RIGHT) && isEntrance && playerAtEntrance()) {
                        playerEntering = true;
                    }
                    break;
                case Right:
                    if (keyListener.isKeyPressed(GLFW_KEY_LEFT) && isEntrance && playerAtEntrance()) {
                        playerEntering = true;
                    }
                    break;
            }

            if (playerEntering) {
                collidingPlayer.setPosition(getPlayerPosition(connectingPipe.get()));
                AssetPool.getSound("assets/sounds/pipe.ogg").play();
            }
        }
    }

    public boolean playerAtEntrance() {
        if (collidingPlayer == null) {
            return false;
        }

        Vector2f pipeBottomLeft = new Vector2f(gameObject.transform.position)
                .sub(new Vector2f(gameObject.transform.scale).mul(0.5f));
        Vector2f pipeUpperRight = new Vector2f(gameObject.transform.position)
                .add(new Vector2f(gameObject.transform.scale).mul(0.5f));

        Vector2f playerBottomLeft = new Vector2f(collidingPlayer.gameObject.transform.position)
                .sub(new Vector2f(collidingPlayer.gameObject.transform.scale).mul(0.5f));
        Vector2f playerUpperRight = new Vector2f(collidingPlayer.gameObject.transform.position)
                .add(new Vector2f(collidingPlayer.gameObject.transform.scale).mul(0.5f));


        switch (direction) {
            case Up:
                return playerBottomLeft.y >= pipeUpperRight.y &&
                       playerUpperRight.x > pipeBottomLeft.x &&
                       playerBottomLeft.x < pipeUpperRight.x;
            case Down:
                return playerUpperRight.y <= pipeBottomLeft.y &&
                       playerUpperRight.x > pipeBottomLeft.x &&
                       playerBottomLeft.x < pipeUpperRight.x;
            case Right:
                return playerBottomLeft.x >= pipeUpperRight.x &&
                       playerUpperRight.y > pipeBottomLeft.y &&
                       playerBottomLeft.y < pipeUpperRight.y;
            case Left:
                return playerBottomLeft.x <= pipeBottomLeft.x &&
                       playerUpperRight.y > pipeBottomLeft.y &&
                       playerBottomLeft.y < pipeUpperRight.y;
            default:
                return false;
        }
    }

    @Override
    public void beginCollision(GameObject collidingObject, Contact contact, Vector2f hitNormal) {
        Optional<PlayerController> maybePlayerController = collidingObject.getComponent(PlayerController.class);
        if (maybePlayerController.isPresent()) {
            collidingPlayer = maybePlayerController.get();
        }
    }

    @Override
    public void endCollision(GameObject collidingObject, Contact contact, Vector2f hitNormal) {
        collidingPlayer = collidingObject.getComponent(PlayerController.class).orElse(null);
    }

    private Vector2f getPlayerPosition(GameObject pipe) {
        Pipe pipeComponent = pipe.getComponent(Pipe.class).get();
        switch (pipeComponent.direction) {
            case Up:
                return new Vector2f(pipe.transform.position).add(0, 0.5f);
            case Down:
                return new Vector2f(pipe.transform.position).add(0, -0.5f);
            case Left:
                return new Vector2f(pipe.transform.position).add(0.5f, 0);
            case Right:
                return new Vector2f(pipe.transform.position).add(-0.5f, 0);
            default:
                throw new IllegalArgumentException("Unknown direction");
        }
    }
}

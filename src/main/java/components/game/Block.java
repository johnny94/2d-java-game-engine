package components.game;

import java.util.Optional;

import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;

import components.Component;
import components.PlayerController;
import jade.GameObject;
import util.AssetPool;

public abstract class Block extends Component {
    private transient boolean bopGoingUp = true;
    private transient boolean doBopAnimation = false;
    private transient Vector2f bopStart;
    private transient Vector2f topBopLocation;
    private transient boolean active = true;

    private float bopSpeed = 0.4f;

    @Override
    public void start() {
        bopStart = new Vector2f(gameObject.transform.position);
        topBopLocation = new Vector2f(bopStart).add(0, 0.02f);
    }

    @Override
    public void update(float deltaTime) {
        if (doBopAnimation) {
            if (bopGoingUp) {
                if (gameObject.transform.position.y < topBopLocation.y) {
                    gameObject.transform.position.y += bopSpeed * deltaTime;
                } else {
                    bopGoingUp = false;
                }
            } else {
                if (gameObject.transform.position.y > bopStart.y) {
                    gameObject.transform.position.y -= bopSpeed * deltaTime;
                } else {
                    gameObject.transform.position.y = bopStart.y;
                    bopGoingUp = true;
                    doBopAnimation = false;
                }
            }
        }
    }

    @Override
    public void beginCollision(GameObject collidingObject, Contact contact, Vector2f hitNormal) {
        Optional<PlayerController> maybePlayerController = collidingObject.getComponent(PlayerController.class);
        if (maybePlayerController.isPresent()) {
            PlayerController playerController = maybePlayerController.get();
            if (active && hitNormal.y < -0.8) { // Player hit up
                doBopAnimation = true;
                AssetPool.getSound("assets/sounds/bump.ogg").play();
                playHit(playerController);
            }
        }
    }

    public void setInactive() {
        active = false;
    }

    abstract void playHit(PlayerController playerController);
}

package components.game;

import java.util.Optional;

import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;

import components.Component;
import components.PlayerController;
import jade.GameObject;
import physics2d.components.RigidBody2D;
import util.AssetPool;

public class MushroomAI extends Component {
    private transient boolean goingRight = true;
    private transient RigidBody2D rigidBody2D;
    private transient Vector2f speed = new Vector2f(1.0f, 0.0f);
    private transient float maxSpeed = 0.8f;
    private transient boolean hitPlayer;

    @Override
    public void start() {
        assert gameObject.getComponent(RigidBody2D.class).isPresent() : "RigidBody2d should not be null";

        this.rigidBody2D = gameObject.getComponent(RigidBody2D.class).get();
        AssetPool.getSound("assets/sounds/powerup_appears.ogg").play();
    }

    @Override
    public void update(float deltaTime) {
        if (goingRight && Math.abs(rigidBody2D.getVelocity().x) < maxSpeed) {
            rigidBody2D.addVelocity(speed);
        } else if (!goingRight && Math.abs(rigidBody2D.getVelocity().x) < maxSpeed) {
            rigidBody2D.addVelocity(new Vector2f(-speed.x, speed.y));
        }
    }

    @Override
    public void preSolve(GameObject collidingObject, Contact contact, Vector2f hitNormal) {
        Optional<PlayerController> maybePlayerController = collidingObject.getComponent(PlayerController.class);
        if (maybePlayerController.isPresent()) {
            PlayerController playerController = maybePlayerController.get();
            // It means we don't want the collision to happen
            contact.setEnabled(false);
            if (!hitPlayer) {
                playerController.powerUp();
                gameObject.destroy();
                hitPlayer = true;
            }
            return;
        }

        if (Math.abs(hitNormal.y) < 0.1) {
            goingRight = hitNormal.x < 0;
        }
    }
}

package components.game;

import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;

import components.Component;
import components.PlayerController;
import jade.GameObject;
import physics2d.components.RigidBody2D;
import util.AssetPool;

public class Flower extends Component {
    private transient RigidBody2D rigidBody2D;

    @Override
    public void start() {
        this.rigidBody2D = gameObject.getComponent(RigidBody2D.class).get();
        AssetPool.getSound("assets/sounds/powerup_appears.ogg").play();
    }

    @Override
    public void beginCollision(GameObject collidingObject, Contact contact, Vector2f hitNormal) {
        collidingObject.getComponent(PlayerController.class).ifPresent(playerController -> {
            playerController.powerUp();
            gameObject.destroy();
        });
    }
}

package components.game;

import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;

import components.Component;
import components.PlayerController;
import jade.GameObject;
import jade.Window;
import physics2d.Physics2D;
import physics2d.components.RigidBody2D;
import util.JMath;

public class Fireball extends Component {
    private static int fireballCount = 0;
    private static float innerPlayerWidth = 0.25f * 0.7f;
    private static float rayCastLength = -0.09f;


    public transient boolean goingRight;

    private transient RigidBody2D rigidBody2D;
    private transient float fireballSpeed = 1.7f;
    private transient Vector2f velocity = new Vector2f();
    private transient Vector2f acceleration = new Vector2f();
    private transient Vector2f terminalVelocity = new Vector2f(2.1f, 3.1f);
    private transient boolean onGround;
    private transient float lifeTime = 4.0f;

    public static boolean canSpawn() {
        return fireballCount < 4;
    }

    @Override
    public void start() {
        rigidBody2D = gameObject.getComponent(RigidBody2D.class).get();
        acceleration.y = Window.get().getPhysics().getGravity().y * 0.7f;
        fireballCount++;
    }

    @Override
    public void update(float deltaTime) {
        lifeTime -= deltaTime;
        if (lifeTime < 0) {
            disappear();
            return;
        }

        if (goingRight) {
            velocity.x = fireballSpeed;
        } else {
            velocity.y = -fireballSpeed;
        }

        checkOnGround();
        if (onGround) {
            acceleration.y = 1.5f;
            velocity.y = 2.5f;
        } else {
            acceleration.y = Window.get().getPhysics().getGravity().y * 0.7f;
        }

        velocity.y += acceleration.y * deltaTime;
        velocity.y = JMath.clamp(velocity.y, -terminalVelocity.y, terminalVelocity.y);
        rigidBody2D.setVelocity(velocity);
    }

    @Override
    public void preSolve(GameObject collidingObject, Contact contact, Vector2f hitNormal) {
        if (collidingObject.getComponent(PlayerController.class).isPresent() ||
            collidingObject.getComponent(Fireball.class).isPresent()) {
            contact.setEnabled(false);
        }
    }

    @Override
    public void beginCollision(GameObject collidingObject, Contact contact, Vector2f hitNormal) {
        if (Math.abs(hitNormal.x) > 0.8f) {
            goingRight = hitNormal.x < 0;
        }
    }

    public void disappear() {
        fireballCount--;
        gameObject.destroy();
    }

    private void checkOnGround() {
        onGround = Physics2D.checkOnGround(gameObject, innerPlayerWidth, rayCastLength);
    }
}

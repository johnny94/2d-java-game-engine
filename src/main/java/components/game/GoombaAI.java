package components.game;

import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;

import components.Component;
import components.PlayerController;
import components.animation.StateMachine;
import jade.Camera;
import jade.GameObject;
import jade.Window;
import physics2d.Physics2D;
import physics2d.components.RigidBody2D;
import util.AssetPool;
import util.JMath;

public class GoombaAI extends Component {
    private static final float innerPlayerWidth = 0.25f * 0.7f;
    private static final float rayCastLength = -0.14f;

    private transient boolean goingRight;
    private transient RigidBody2D rigidBody2D;
    private transient float walkSpeed = 0.6f;
    private transient Vector2f velocity = new Vector2f();
    private transient Vector2f acceleration = new Vector2f();
    private transient Vector2f terminalVelocity = new Vector2f();
    private transient boolean onGround;
    private transient boolean isDead;
    private transient float timeToKill = 0.5f;
    private transient StateMachine stateMachine;

    @Override
    public void start() {
        stateMachine = gameObject.getComponent(StateMachine.class).get();
        rigidBody2D = gameObject.getComponent(RigidBody2D.class).get();
        acceleration.y = Window.get().getPhysics().getGravity().y * 0.7f;
    }

    @Override
    public void update(float deltaTime) {
        Camera camera = Window.get().getCurrentScene().getCamera();
        if (gameObject.transform.position.x >
            camera.position.x + camera.getProjectionSize().x * camera.getZoom()) {
            return;
        }

        if (isDead) {
            timeToKill -= deltaTime;
            if (timeToKill <= 0) {
                this.gameObject.destroy();
            }
            return;
        }

        if (goingRight) {
            velocity.x = walkSpeed;
        } else {
            velocity.x = -walkSpeed;
        }

        checkOnGround();
        if (onGround) {
            acceleration.y = 0;
            velocity.y = 0;
        } else {
            acceleration.y = Window.get().getPhysics().getGravity().y * 0.7f;
        }

        velocity.y += acceleration.y * deltaTime;
        velocity.y = JMath.clamp(velocity.y, -terminalVelocity.y, terminalVelocity.y);
        rigidBody2D.setVelocity(velocity);
    }

    @Override
    public void beginCollision(GameObject collidingObject, Contact contact, Vector2f hitNormal) {
        if (isDead) {
            return;
        }

        collidingObject.getComponent(PlayerController.class).ifPresent(playerController -> {
            if (!playerController.isDead() && !playerController.isHurtInvincible() &&
                hitNormal.y > 0.58f) {
                playerController.enemyBounce();
                stomp();
            } else if (!playerController.isDead() && !playerController.isInvincible()) {
                playerController.die();
            }
        });

        if (Math.abs(hitNormal.y) < 0.1) {
            goingRight = hitNormal.x < 0;
        }

        collidingObject.getComponent(Fireball.class).ifPresent(fireball -> {
            stomp();
            fireball.disappear();
        });
    }

    private void checkOnGround() {
        onGround = Physics2D.checkOnGround(gameObject, innerPlayerWidth, rayCastLength);
    }

    public void stomp() {
        stomp(true);
    }

    private void stomp(boolean playSound) {
        isDead = true;
        velocity.zero();
        rigidBody2D.setVelocity(velocity);
        rigidBody2D.setAngularVelocity(0);
        rigidBody2D.setGravityScale(0);
        stateMachine.trigger("squashMe");
        rigidBody2D.setIsSensor();
        if (playSound) {
            AssetPool.getSound("assets/sounds/bump.ogg").play();
        }
    }
}

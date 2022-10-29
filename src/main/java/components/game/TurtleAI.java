package components.game;

import java.util.Optional;

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

public class TurtleAI extends Component {
    private static final float innerPlayerWidth = 0.25f * 0.7f;
    private static final float rayCastLength = -0.2f;

    private transient boolean goingRight = false;
    private transient RigidBody2D rigidBody2D;
    private transient float walkSpeed = 0.6f;
    private transient Vector2f velocity = new Vector2f();
    private transient Vector2f acceleration = new Vector2f();
    private transient Vector2f terminalVelocity = new Vector2f(2.1f, 3.1f);
    private transient boolean onGround = false;
    private transient boolean isDead = false;
    private transient boolean isMoving = false;
    private transient StateMachine stateMachine;
    private float movingDebounce = 0.32f;

    @Override
    public void start() {
        this.stateMachine = gameObject.getComponent(StateMachine.class).get();
        this.rigidBody2D = gameObject.getComponent(RigidBody2D.class).get();
        this.acceleration.y = Window.get().getPhysics().getGravity().y * 0.7f;
    }

    @Override
    public void update(float deltaTime) {
         movingDebounce -= deltaTime;
        Camera camera = Window.get().getCurrentScene().getCamera();
        if (this.gameObject.transform.position.x >
            camera.position.x + camera.getProjectionSize().x * camera.getZoom()) {
            return;
        }

        if (!isDead || isMoving) {
            if (goingRight) {
                gameObject.transform.scale.x = -0.25f;
                velocity.x = walkSpeed;
            } else {
                gameObject.transform.scale.x = 0.25f;
                velocity.x = -walkSpeed;
            }
        } else {
            velocity.x = 0;
        }

        checkOnGround();
        if (onGround) {
            this.acceleration.y = 0;
            this.velocity.y = 0;
        } else {
            this.acceleration.y = Window.get().getPhysics().getGravity().y * 0.7f;
        }
        this.velocity.y += this.acceleration.y * deltaTime;
        this.velocity.y = JMath.clamp(velocity.y, -terminalVelocity.y, terminalVelocity.y);
        this.rigidBody2D.setVelocity(velocity);

        if (this.gameObject.transform.position.x <
            Window.get().getCurrentScene().getCamera().position.x - 0.5f) {
            this.gameObject.destroy();
        }
    }

    @Override
    public void beginCollision(GameObject collidingObject, Contact contact, Vector2f hitNormal) {
        Optional<PlayerController> maybePlayerController = collidingObject.getComponent(PlayerController.class);
        if (maybePlayerController.isPresent()) {
            PlayerController playerController = maybePlayerController.get();

            if (!isDead && !playerController.isDead() &&
                !playerController.isHurtInvincible() && hitNormal.y > 0.58f) { // Player stomp the turtle
                playerController.enemyBounce();
                stomp();
                walkSpeed *= 3.0f;
            } else if (movingDebounce < 0 && !playerController.isDead() &&
                       !playerController.isHurtInvincible() &&
                       (isMoving || !isDead) && hitNormal.y < 0.58f) { // The case that turtle walk to the the player
                playerController.die();
            } else if (!playerController.isDead() && !playerController.isHurtInvincible()) {
                // The case that player stomps the shell
                if (isDead && hitNormal.y > 0.58f) {
                    playerController.enemyBounce();
                    isMoving = !isMoving;
                    goingRight = hitNormal.x < 0;
                } else if (isDead && !isMoving) { // The case that player touch the shell
                    isMoving = true;
                    goingRight = hitNormal.x < 0;
                    movingDebounce = 0.32f;
                }
            }
        } else if (Math.abs(hitNormal.y) < 0.1 && !collidingObject.isDead()) {
            goingRight = hitNormal.x < 0;
            if (isMoving && isDead) { // Turtle is in shell
                AssetPool.getSound("assets/sounds/bump.ogg").play();
            }
        }
    }

    @Override
    public void preSolve(GameObject collidingObject, Contact contact, Vector2f hitNormal) {
        Optional<GoombaAI> maybeGoomba = collidingObject.getComponent(GoombaAI.class);
        maybeGoomba.ifPresent(goomba -> {
            if (isDead && isMoving) {
                goomba.stomp();
                contact.setEnabled(false);
                AssetPool.getSound("assets/sounds/kick.ogg").play();
            }
        });
    }

    public void stomp() {
        this.isDead = true;
        this.isMoving = false;
        this.velocity.zero();
        this.rigidBody2D.setVelocity(this.velocity);
        this.rigidBody2D.setAngularVelocity(0.0f);
        this.rigidBody2D.setGravityScale(0.0f);
        this.stateMachine.trigger("squashMe");
        AssetPool.getSound("assets/sounds/bump.ogg").play();
    }

    private void checkOnGround() {
        onGround = Physics2D.checkOnGround(gameObject, innerPlayerWidth, rayCastLength);
    }
}

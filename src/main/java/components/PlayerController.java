package components;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT;

import org.joml.Vector2f;

import components.animation.StateMachine;
import jade.KeyListener;
import jade.Window;
import physics2d.components.RigidBody2D;

public class PlayerController extends Component {
    private static final KeyListener keyListener = KeyListener.getInstance();

    private float walkSpeed = 1.9f;
    private float jumpBoost = 1.0f;
    private float jumpImpulse = 3.0f;
    private float slowDownForce = 0.05f;
    private Vector2f terminalVelocity = new Vector2f(2.1f, 3.1f);

    private transient boolean onGround = false;
    private transient float groundDebounce = 0.0f;
    private transient float groundDebounceTime = 0.1f;

    private transient RigidBody2D rigidBody2D;
    private transient StateMachine stateMachine;
    private transient float bigJumpBoostFactor = 1.05f;
    private transient float playerWidth = 0.25f;
    private transient int jumpTime = 0;

    private transient Vector2f acceleration = new Vector2f();
    private transient Vector2f velocity = new Vector2f();
    private transient boolean isDead;
    private transient int enemyBounce;

    @Override
    public void start() {
        assert gameObject.getComponent(RigidBody2D.class).isPresent() : "RigidBody2D should not be null";
        assert gameObject.getComponent(StateMachine.class).isPresent() : "StateMachine should not be null";

        this.rigidBody2D = gameObject.getComponent(RigidBody2D.class).get();
        this.stateMachine = gameObject.getComponent(StateMachine.class).get();

        // We don't want box2d to control physics here so set to 0
        this.rigidBody2D.setGravityScale(0.0f);
    }

    @Override
    public void update(float deltaTime) {
        if (keyListener.isKeyPressed(GLFW_KEY_RIGHT)) {
            this.gameObject.transform.scale.x = playerWidth;
            this.acceleration.x = walkSpeed;

            // If Mario is going left
            if (velocity.x < 0) {
                stateMachine.trigger("switchDirection");
                velocity.x += slowDownForce;
            } else {
               stateMachine.trigger("startRunning");
            }
        } else if (keyListener.isKeyPressed(GLFW_KEY_LEFT)) {
            this.gameObject.transform.scale.x = -playerWidth;
            this.acceleration.x = -walkSpeed;

            // If Mario is going right
            if (velocity.x > 0) {
                stateMachine.trigger("switchDirection");
                velocity.x -= slowDownForce;
            } else {
                stateMachine.trigger("startRunning");
            }
        } else {
            acceleration.x = 0;
            if (velocity.x > 0) {
                velocity.x = Math.max(0, velocity.x - slowDownForce);
            } else if (velocity.x < 0) {
                velocity.x = Math.min(0, velocity.x + slowDownForce);
            }

            if (this.velocity.x == 0) {
                stateMachine.trigger("stopRunning");
            }
        }

        acceleration.y = Window.get().getPhysics().getGravity().y * 0.7f;

        velocity.add(acceleration.x * deltaTime, acceleration.y * deltaTime);

        // This is Clamp
        velocity.x = Math.max(Math.min(velocity.x, terminalVelocity.x), -terminalVelocity.x);
        velocity.y = Math.max(Math.min(velocity.y, terminalVelocity.y), -terminalVelocity.y);

        rigidBody2D.setVelocity(velocity);
        rigidBody2D.setAngularVelocity(0);
    }

}

package components;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;

import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;
import org.joml.Vector4f;

import components.animation.StateMachine;
import components.game.Ground;
import jade.GameObject;
import jade.KeyListener;
import jade.Window;
import physics2d.Physics2D;
import physics2d.components.PillBoxCollider;
import physics2d.components.RigidBody2D;
import physics2d.enums.BodyType;
import scenes.LevelEditorSceneInitializer;
import util.AssetPool;
import util.JMath;

public class PlayerController extends Component {
    private enum PlayerState {
        Small,
        Big,
        Fire,
        Invincible
    }

    private static final KeyListener keyListener = KeyListener.getInstance();

    private PlayerState playerState = PlayerState.Small;

    private float walkSpeed = 1.9f;
    private float jumpBoost = 1.0f;
    private float jumpImpulse = 3.0f;
    private float slowDownForce = 0.05f;
    private Vector2f terminalVelocity = new Vector2f(2.1f, 3.1f);

    private transient boolean onGround = false;

    // This will let player be able to jump when he leave ground in a short period
    private transient float groundDebounce = 0.0f;
    private transient float groundDebounceTime = 0.1f;

    private transient RigidBody2D rigidBody2D;
    private transient StateMachine stateMachine;
    private transient SpriteRenderer spriteRenderer;
    private transient float bigJumpBoostFactor = 1.05f;
    private transient float playerWidth = 0.25f;
    private transient int jumpTime;

    private transient float hurtInvincibilityTimeLeft;
    private transient float hurtInvincibilityTime = 1.4f;
    private transient float deadMaxHeight;
    private transient float deadMinHeight;
    private transient boolean deadGoingUp = true;
    private transient float blinkTime;

    private transient Vector2f acceleration = new Vector2f();
    private transient Vector2f velocity = new Vector2f();
    private transient boolean isDead;
    private transient int enemyBounce;

    @Override
    public void start() {
        assert gameObject.getComponent(RigidBody2D.class).isPresent() : "RigidBody2D should not be null";
        assert gameObject.getComponent(StateMachine.class).isPresent() : "StateMachine should not be null";
        assert gameObject.getComponent(SpriteRenderer.class).isPresent() : "SpriteRenderer should not be null";

        this.rigidBody2D = gameObject.getComponent(RigidBody2D.class).get();
        this.stateMachine = gameObject.getComponent(StateMachine.class).get();
        this.spriteRenderer = gameObject.getComponent(SpriteRenderer.class).get();

        // We don't want box2d to control physics here so set to 0
        this.rigidBody2D.setGravityScale(0.0f);
    }

    @Override
    public void update(float deltaTime) {
        if (isDead) {
            if (gameObject.transform.position.y < deadMaxHeight && deadGoingUp) {
                gameObject.transform.position.y += deltaTime * walkSpeed / 2.0f;
            } else if (gameObject.transform.position.y >= deadMaxHeight && deadGoingUp) {
                deadGoingUp = false;
            } else if (!deadGoingUp && gameObject.transform.position.y > deadMinHeight) {
                rigidBody2D.setBodyType(BodyType.KINEMATIC);
                acceleration.y = Window.get().getPhysics().getGravity().y * 0.7f;
                velocity.y += acceleration.y * deltaTime;
                velocity.y = JMath.clamp(velocity.y, -terminalVelocity.y, terminalVelocity.y);
                rigidBody2D.setVelocity(velocity);
                rigidBody2D.setAngularVelocity(0);
            } else if (!deadGoingUp && gameObject.transform.position.y <= deadMinHeight) {
                Window.get().changeScene(new LevelEditorSceneInitializer());
            }
            return;
        }

        if (hurtInvincibilityTimeLeft > 0) {
            hurtInvincibilityTimeLeft -= deltaTime;
            blinkTime -= deltaTime;

            if (blinkTime <= 0) {
                blinkTime = 0.2f;
                if (spriteRenderer.getColor().w == 1) {
                    spriteRenderer.setColor(new Vector4f(1, 1, 1, 0));
                } else {
                    spriteRenderer.setColor(new Vector4f(1, 1, 1, 1));
                }
            } else {
                if (spriteRenderer.getColor().w == 0) {
                    spriteRenderer.setColor(new Vector4f(1, 1, 1, 1));
                }
            }
        }

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

            if (velocity.x == 0) {
                stateMachine.trigger("stopRunning");
            }
        }

        checkOnGround();

        if (keyListener.isKeyPressed(GLFW_KEY_SPACE) && (jumpTime > 0 || onGround || groundDebounce > 0)) {
            // Start to jump
            if ((onGround || groundDebounce > 0) && jumpTime == 0) {
                AssetPool.getSound("assets/sounds/jump-small.ogg").play();
                jumpTime = 28;
                velocity.y = jumpImpulse;
            } else if (jumpTime > 0) {
                // jumping up but player still press the jump button
                // We let Mario can fly up a little bit.
                jumpTime--;
                // Seems this is how original Mario work.
                velocity.y = (jumpTime / 2.2f) * jumpBoost;
            } else { // on the highest point
                velocity.y = 0;
            }

            groundDebounce = 0;
        } else if (enemyBounce > 0) {
            enemyBounce--;
            velocity.y = (enemyBounce / 2.2f) * jumpBoost;
        } else if (!onGround){ // Handle the case of jumping or just leaving the ground
            if (jumpTime > 0) {
                velocity.y *= 0.35;
                jumpTime = 0;
            }

            groundDebounce -= deltaTime;

            // Only apply gravity when the player is on the ground
            acceleration.y = Window.get().getPhysics().getGravity().y * 0.7f;
        } else { // on the ground
            velocity.y = 0;
            acceleration.y = 0;
            groundDebounce = groundDebounceTime;
        }

        velocity.add(acceleration.x * deltaTime, acceleration.y * deltaTime);

        // This is Clamp
        velocity.x = Math.max(Math.min(velocity.x, terminalVelocity.x), -terminalVelocity.x);
        velocity.y = Math.max(Math.min(velocity.y, terminalVelocity.y), -terminalVelocity.y);

        rigidBody2D.setVelocity(velocity);
        rigidBody2D.setAngularVelocity(0);

        if(!onGround) {
            stateMachine.trigger("jump");
        } else {
            stateMachine.trigger("stopJumping");
        }
    }

    @Override
    public void beginCollision(GameObject collidingObject, Contact contact, Vector2f hitNormal) {
        if (isDead) {
            return;
        }

        if (collidingObject.getComponent(Ground.class).isPresent()) {
            if (Math.abs(hitNormal.x) > 0.8f) {
                velocity.x = 0;
            } else if (hitNormal.y > 0.8) { // Prevent from floating when hitting form the bottom of the Ground
                velocity.y = 0;
                acceleration.y = 0;
                jumpTime = 0;
            }
        }
    }

    public boolean isSmall() {
        return playerState == PlayerState.Small;
    }

    public boolean isDead() {
        return isDead;
    }

    public boolean isHurtInvincible() {
        return hurtInvincibilityTimeLeft > 0;
    }

    public boolean isInvincible() {
        return playerState == PlayerState.Invincible || isHurtInvincible();
    }

    public void die() {
        stateMachine.trigger("die");
        if (playerState == PlayerState.Small) {
            velocity.zero();
            acceleration.zero();
            rigidBody2D.setVelocity(velocity);
            isDead = true;
            rigidBody2D.setIsSensor();
            AssetPool.getSound("assets/sounds/mario_die.ogg").play();
            deadMaxHeight = gameObject.transform.position.y + 0.3f;
            rigidBody2D.setBodyType(BodyType.STATIC);
            if (gameObject.transform.position.y > 0) {
                deadMinHeight = -0.25f;
            }
        } else if (playerState == PlayerState.Big) {
            playerState = PlayerState.Small;
            gameObject.transform.scale.y = 0.25f;
            gameObject.getComponent(PillBoxCollider.class).ifPresent(pillBoxCollider -> {
                jumpBoost /= bigJumpBoostFactor;
                walkSpeed /= bigJumpBoostFactor;
                pillBoxCollider.setHeight(0.31f);
            });

            hurtInvincibilityTimeLeft = hurtInvincibilityTime;
            AssetPool.getSound("assets/sounds/pipe.ogg").play();

        } else if (playerState == PlayerState.Fire) {
            playerState = PlayerState.Big;
            hurtInvincibilityTimeLeft = hurtInvincibilityTime;
            AssetPool.getSound("assets/sounds/pipe.ogg").play();
        }
    }

    public void enemyBounce() {
        enemyBounce = 8;
    }

    public void powerUp() {
        if (playerState == PlayerState.Small) {
            playerState = PlayerState.Big;
            AssetPool.getSound("assets/sounds/powerup.ogg").play();
            gameObject.transform.scale.y = 0.42f;

            gameObject.getComponent(PillBoxCollider.class).ifPresent(pillBoxCollider -> {
                // Because we use Box2D to control our player
                // When we make the Box bigger we should increase "force" to make the character
                // act as the same the original one.
                jumpBoost *= bigJumpBoostFactor;
                walkSpeed *= bigJumpBoostFactor;
                pillBoxCollider.setHeight(0.63f);
            });
        } else if (playerState == PlayerState.Big) {
            playerState = PlayerState.Fire;
            AssetPool.getSound("assets/sounds/powerup.ogg").play();
        }

        stateMachine.trigger("powerup");
    }

    private void checkOnGround() {
        float innerPlayerWidth = playerWidth * 0.6f;
        float yVal = playerState == PlayerState.Small ? -0.14f : -0.24f;

        onGround = Physics2D.checkOnGround(gameObject, innerPlayerWidth, yVal);
    }
}

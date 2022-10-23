package physics2d.components;

import java.util.Optional;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.joml.Vector2f;

import components.Component;
import jade.Window;
import physics2d.enums.BodyType;

public class RigidBody2D extends Component {
    private Vector2f velocity = new Vector2f();
    private float angularDamping = 0.8f;
    private float angularVelocity = 0.0f;
    private float linearDamping = 0.9f;
    private float mass = 0;
    private float friction = 0.1f;

    private float gravityScale = 1.0f;
    private boolean isSensor;

    private BodyType bodyType = BodyType.DYNAMIC;

    private boolean fixedRotation = false;
    private boolean continuousCollision = false;

    private transient Body rawBody;

    @Override
    public void update(float deltaTime) {
        if (rawBody != null) {
            if (bodyType == BodyType.DYNAMIC || bodyType == BodyType.KINEMATIC) {
                gameObject.transform.position.set(rawBody.getPosition().x,
                                                  rawBody.getPosition().y);
                gameObject.transform.rotation = (float)Math.toDegrees(rawBody.getAngle());

                Vec2 velocity = rawBody.getLinearVelocity();
                setVelocity(new Vector2f(velocity.x, velocity.y));
            } else if (bodyType == BodyType.STATIC) {
                // We don't want to let physics engine to control transform if it is STATIC
                // e.g., BreakableBrick
                rawBody.setTransform(new Vec2(gameObject.transform.position.x, gameObject.transform.position.y),
                                     gameObject.transform.rotation);
            }
        }
    }

    public float getAngularVelocity() {
        return angularVelocity;
    }

    public void setAngularVelocity(float angularVelocity) {
        this.angularVelocity = angularVelocity;
        if (rawBody != null) {
            rawBody.setAngularVelocity(angularVelocity);
        }
    }

    public float getFriction() {
        return friction;
    }

    public void setFriction(float friction) {
        this.friction = friction;
    }

    public float getGravityScale() {
        return gravityScale;
    }

    public void setGravityScale(float gravityScale) {
        this.gravityScale = gravityScale;
        if (rawBody != null) {
            rawBody.setGravityScale(gravityScale);
        }
    }

    public boolean isSensor() {
        return isSensor;
    }

    public void setIsSensor() {
        isSensor = true;
        if (rawBody != null) {
            // Note. Why don't we just put setSensor method in this class?
            Window.get().getPhysics().setSensor(this, true);
        }
    }

    public void setNotSensor() {
        isSensor = false;
        if (rawBody != null) {
            Window.get().getPhysics().setSensor(this, false);
        }
    }

    public void addVelocity(Vector2f forceToAdd) {
        if (rawBody != null) {
            rawBody.applyForceToCenter(new Vec2(forceToAdd.x, forceToAdd.y));
        }
    }

    public void addImpulse(Vector2f impulse) {
        if (rawBody != null) {
            rawBody.applyLinearImpulse(new Vec2(impulse.x, impulse.y), rawBody.getWorldCenter());
        }
    }

    public Vector2f getVelocity() {

        return velocity;
    }

    public void setVelocity(Vector2f velocity) {
        this.velocity.set(velocity);
        if (rawBody != null) {
            rawBody.setLinearVelocity(new Vec2(velocity.x, velocity.y));
        }
    }



    public float getAngularDamping() {
        return angularDamping;
    }

    public void setAngularDamping(float angularDamping) {
        this.angularDamping = angularDamping;
    }

    public float getLinearDamping() {
        return linearDamping;
    }

    public void setLinearDamping(float linearDamping) {
        this.linearDamping = linearDamping;
    }

    public float getMass() {
        return mass;
    }

    public void setMass(float mass) {
        this.mass = mass;
    }

    public BodyType getBodyType() {
        return bodyType;
    }

    public void setBodyType(BodyType bodyType) {
        this.bodyType = bodyType;
    }

    public boolean isFixedRotation() {
        return fixedRotation;
    }

    public void setFixedRotation(boolean fixedRotation) {
        this.fixedRotation = fixedRotation;
    }

    public boolean isContinuousCollision() {
        return continuousCollision;
    }

    public void setContinuousCollision(boolean continuousCollision) {
        this.continuousCollision = continuousCollision;
    }

    public Optional<Body> getRawBody() {
        return Optional.ofNullable(rawBody);
    }

    public void setRawBody(Body rawBody) {
        this.rawBody = rawBody;
    }

    public void removeRawBody() {
        this.rawBody = null;
    }
}

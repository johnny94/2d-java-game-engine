package physics2d;

import java.util.Optional;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.World;
import org.joml.Vector2f;

import components.Transform;
import jade.GameObject;
import physics2d.components.Box2DCollider;
import physics2d.components.CircleCollider;
import physics2d.components.RigidBody2D;

public class Physics2D {
    private Vec2 gravity = new Vec2(0, -10.0f);
    private World world = new World(gravity);

    private float physicsTime = 0.0f;
    private float physicsTimeStep = 1.0f / 60.0f;
    private int velocityIteration = 8;
    private int positionIteration = 3;

    public void add(GameObject gameObject) {
        Optional<RigidBody2D> maybeRigidBody2D = gameObject.getComponent(RigidBody2D.class);
        if (maybeRigidBody2D.isPresent() && !maybeRigidBody2D.get().getRawBody().isPresent()) {
            RigidBody2D rigidBody2D = maybeRigidBody2D.get();
            Transform transform = gameObject.transform;

            BodyDef bodyDef = new BodyDef();
            bodyDef.angle = (float)Math.toRadians(transform.rotation);
            bodyDef.position.set(transform.position.x, transform.position.y);
            bodyDef.angularDamping = rigidBody2D.getAngularDamping();
            bodyDef.linearDamping = rigidBody2D.getLinearDamping();
            bodyDef.fixedRotation = rigidBody2D.isFixedRotation();
            bodyDef.bullet = rigidBody2D.isContinuousCollision();

            switch (rigidBody2D.getBodyType()) {
                case STATIC: bodyDef.type = BodyType.STATIC; break;
                case KINEMATIC: bodyDef.type = BodyType.KINEMATIC; break;
                case DYNAMIC: bodyDef.type = BodyType.DYNAMIC; break;
            }

            PolygonShape polygonShape = new PolygonShape();

            // Currently, a gameObject cannot have CircleCollider and Box2DCollider at the same time.
            gameObject.getComponent(CircleCollider.class).ifPresent(circleCollider -> {
                polygonShape.setRadius(circleCollider.getRadius());
            });

            gameObject.getComponent(Box2DCollider.class).ifPresent(box2DCollider -> {
                Vector2f halfSize = box2DCollider.getHalfSize().mul(0.5f);
                Vector2f offset = box2DCollider.getOffset();
                Vector2f origin = box2DCollider.getOrigin();

                polygonShape.setAsBox(halfSize.x, halfSize.y, new Vec2(origin.x, origin.y), 0);

                Vec2 pos = bodyDef.position;
                float xPos = pos.x + offset.x;
                float yPos = pos.y + offset.y;
                bodyDef.position.set(xPos, yPos);
            });

            Body body = this.world.createBody(bodyDef);
            rigidBody2D.setRawBody(body);
            body.createFixture(polygonShape, rigidBody2D.getMass());
        }
    }

    public void update(float deltaTime) {
        physicsTime += deltaTime;

        // Make sure the world updates every 1/60 sec
        if (physicsTime > 0) {
            physicsTime -= physicsTimeStep;
            world.step(physicsTimeStep, velocityIteration, positionIteration);
        }
    }

    public void destroyGameObject(GameObject gameObject) {
        Optional<RigidBody2D> maybeRigidBody2D = gameObject.getComponent(RigidBody2D.class);
        if (maybeRigidBody2D.isPresent()) {
            RigidBody2D rigidBody2D = maybeRigidBody2D.get();
            if (rigidBody2D.getRawBody().isPresent()) {
                world.destroyBody(rigidBody2D.getRawBody().get());
                rigidBody2D.removeRawBody();
            }
        }
    }
}

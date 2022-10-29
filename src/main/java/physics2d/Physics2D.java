package physics2d;

import java.util.Optional;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;
import org.joml.Vector2f;

import components.Transform;
import components.game.Ground;
import jade.GameObject;
import jade.Window;
import physics2d.components.Box2DCollider;
import physics2d.components.CircleCollider;
import physics2d.components.PillBoxCollider;
import physics2d.components.RigidBody2D;

public class Physics2D {
    private Vec2 gravity = new Vec2(0, -10.0f);
    private World world;

    private float physicsTime = 0.0f;
    private float physicsTimeStep = 1.0f / 60.0f;
    private int velocityIteration = 8;
    private int positionIteration = 3;

    public Physics2D() {
        world = new World(gravity);
        world.setContactListener(new JadeContactListener());
    }

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
            bodyDef.gravityScale = rigidBody2D.getGravityScale();
            bodyDef.angularVelocity = rigidBody2D.getAngularVelocity();

            bodyDef.userData = rigidBody2D.gameObject;


            switch (rigidBody2D.getBodyType()) {
                case STATIC: bodyDef.type = BodyType.STATIC; break;
                case KINEMATIC: bodyDef.type = BodyType.KINEMATIC; break;
                case DYNAMIC: bodyDef.type = BodyType.DYNAMIC; break;
            }

            Body body = world.createBody(bodyDef);
            body.m_mass = rigidBody2D.getMass();
            rigidBody2D.setRawBody(body);

            gameObject.getComponent(CircleCollider.class)
                      .ifPresent(circleCollider -> addCircleCollider(rigidBody2D, circleCollider));

            gameObject.getComponent(Box2DCollider.class)
                      .ifPresent(box2DCollider -> addBox2DCollider(rigidBody2D, box2DCollider));

            gameObject.getComponent(PillBoxCollider.class)
                      .ifPresent(pillBoxCollider -> addPillBoxCollider(rigidBody2D, pillBoxCollider));

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

    public void addBox2DCollider(RigidBody2D rb, Box2DCollider box2DCollider) {
        Optional<Body> mayBody = rb.getRawBody();
        assert mayBody.isPresent() : "Raw Body must not be null";

        Body body = mayBody.get();
        Vector2f halfSize = new Vector2f(box2DCollider.getHalfSize()).mul(0.5f);
        Vector2f offset = box2DCollider.getOffset();

        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(halfSize.x, halfSize.y, new Vec2(offset.x, offset.y), 0);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = polygonShape;
        fixtureDef.density = 1.0f;
        fixtureDef.friction = rb.getFriction();
        fixtureDef.userData = box2DCollider.gameObject;
        fixtureDef.isSensor = rb.isSensor();

        body.createFixture(fixtureDef);
    }

    public void resetBox2DCollider(RigidBody2D rigidBody2D, Box2DCollider box2DCollider) {
        Optional<Body> maybeBody = rigidBody2D.getRawBody();
        if (!maybeBody.isPresent()) {
            return;
        }

        Body body = maybeBody.get();
        destroyAllFixture(body);

        addBox2DCollider(rigidBody2D, box2DCollider);
        body.resetMassData();
    }

    public void addCircleCollider(RigidBody2D rb, CircleCollider circleCollider) {
        Optional<Body> mayBody = rb.getRawBody();
        assert mayBody.isPresent() : "Raw Body must not be null";

        Body body = mayBody.get();

        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(circleCollider.getRadius());
        circleShape.m_p.set(new Vec2(circleCollider.getOffset().x, circleCollider.getOffset().y));

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circleShape;
        fixtureDef.density = 1.0f;
        fixtureDef.friction = rb.getFriction();
        fixtureDef.userData = circleCollider.gameObject;
        fixtureDef.isSensor = rb.isSensor();

        body.createFixture(fixtureDef);
    }

    public void resetCircleCollider(RigidBody2D rigidBody2D, CircleCollider circleCollider) {
        Optional<Body> maybeBody = rigidBody2D.getRawBody();
        if (!maybeBody.isPresent()) {
            return;
        }

        Body body = maybeBody.get();
        destroyAllFixture(body);

        addCircleCollider(rigidBody2D, circleCollider);
        body.resetMassData();
    }

    public void addPillBoxCollider(RigidBody2D rb, PillBoxCollider pillBoxCollider) {
        assert rb.getRawBody().isPresent() : "Raw Body must not be null";
        addCircleCollider(rb, pillBoxCollider.getTopCircle());
        addBox2DCollider(rb, pillBoxCollider.getBox());
        addCircleCollider(rb, pillBoxCollider.getBottomCircle());
    }

    public void resetPillBoxCollider(RigidBody2D rigidBody2D, PillBoxCollider pillBoxCollider) {
        Optional<Body> maybeBody = rigidBody2D.getRawBody();
        if (!maybeBody.isPresent()) {
            return;
        }

        Body body = maybeBody.get();
        destroyAllFixture(body);

        addPillBoxCollider(rigidBody2D, pillBoxCollider);
        body.resetMassData();
    }

    public RayCastInfo rayCast(GameObject requestingObject, Vector2f point1, Vector2f point2) {
        RayCastInfo callback = new RayCastInfo(requestingObject);
        world.raycast(callback, new Vec2(point1.x, point1.y), new Vec2(point2.x, point2.y));

        return callback;
    }

    public void setSensor(RigidBody2D rigidBody2D, boolean isSensor) {
        if (!rigidBody2D.getRawBody().isPresent()) {
            return;
        }

        Body body = rigidBody2D.getRawBody().get();
        Fixture fixture = body.getFixtureList();
        while(fixture != null) {
            fixture.setSensor(isSensor);
            fixture = fixture.getNext();
        }
    }

    public boolean isLocked() {
        return world.isLocked();
    }

    public Vector2f getGravity() {
        return new Vector2f(world.getGravity().x, world.getGravity().y);
    }

    public static boolean checkOnGround(GameObject gameObject, float innerPlayerWidth, float height) {
        Vector2f rayCastBegin = new Vector2f(gameObject.transform.position);
        rayCastBegin.sub(innerPlayerWidth / 2, 0);
        Vector2f rayCastEnd = new Vector2f(rayCastBegin).add(0, height);
        RayCastInfo left = Window.get().getPhysics().rayCast(gameObject, rayCastBegin, rayCastEnd);

        Vector2f rayCastBegin2 = new Vector2f(rayCastBegin).add(innerPlayerWidth, 0);
        Vector2f rayCastEnd2 = new Vector2f(rayCastEnd).add(innerPlayerWidth, 0);
        RayCastInfo right = Window.get().getPhysics().rayCast(gameObject, rayCastBegin2, rayCastEnd2);

        //DebugDraw.drawLine(rayCastBegin, rayCastEnd, Color.RED);
        //DebugDraw.drawLine(rayCastBegin2, rayCastEnd2, Color.RED);

        return (left.hit && left.hitObject != null && left.hitObject.getComponent(Ground.class).isPresent()) ||
               (right.hit && right.hitObject != null && right.hitObject.getComponent(Ground.class).isPresent());
    }

    private void destroyAllFixture(Body body) {
        int size = fixtureListSize(body);
        for (int i = 0; i < size; i++) {
            body.destroyFixture(body.getFixtureList());
        }
    }

    private int fixtureListSize(Body body) {
        int size = 0;
        Fixture fixture = body.getFixtureList();
        while(fixture != null) {
            fixture = fixture.getNext();
            size++;
        }

        return size;
    }
}

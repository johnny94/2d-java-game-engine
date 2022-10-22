package physics2d.components;

import org.joml.Vector2f;

import components.Component;
import jade.Window;

public class PillBoxCollider extends Component {
    private transient CircleCollider topCircle = new CircleCollider();
    private transient CircleCollider bottomCircle = new CircleCollider();
    private transient Box2DCollider box = new Box2DCollider();
    private transient boolean resetFixtureNextFrame;

    private float width = 0.1f;
    private float height = 0.2f;
    public Vector2f offset = new Vector2f();

    @Override
    public void start() {
        topCircle.gameObject = gameObject;
        bottomCircle.gameObject = gameObject;
        box.gameObject = gameObject;
        recalculateCollider();
    }

    @Override
    public void update(float deltaTime) {
        if (resetFixtureNextFrame) {
            resetFixture();
        }
    }

    @Override
    public void editorUpdate(float deltaTime) {
        topCircle.editorUpdate(deltaTime);
        box.editorUpdate(deltaTime);
        bottomCircle.editorUpdate(deltaTime);

        if (resetFixtureNextFrame) {
            resetFixture();
        }
    }

    public void resetFixture() {
        if (Window.get().getPhysics().isLocked()) {
            resetFixtureNextFrame = true;
            return;
        }

        resetFixtureNextFrame = false;
        if (gameObject != null) {
            gameObject.getComponent(RigidBody2D.class)
                      .ifPresent(rigidBody2D -> Window.get().getPhysics()
                                                      .resetPillBoxCollider(rigidBody2D, this));
        }
    }

    private void recalculateCollider() {
        float circleRadius = width / 4;
        float boxHeight = height - 2 * circleRadius;
        topCircle.setRadius(circleRadius);
        topCircle.setOffset(new Vector2f(offset).add(0, boxHeight / 4.0f));

        bottomCircle.setRadius(circleRadius);
        bottomCircle.setOffset(new Vector2f(offset).sub(0, boxHeight / 4.0f));

        box.setHalfSize(new Vector2f(width / 2.0f, boxHeight / 2.0f));
        box.setOffset(offset);
    }

    public CircleCollider getTopCircle() {
        return topCircle;
    }

    public CircleCollider getBottomCircle() {
        return bottomCircle;
    }

    public Box2DCollider getBox() {
        return box;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
        recalculateCollider();
        resetFixture();
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
        recalculateCollider();
        resetFixture();
    }
}

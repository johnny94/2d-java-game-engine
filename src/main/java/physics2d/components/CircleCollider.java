package physics2d.components;

import org.joml.Vector2f;

import components.Component;
import renderer.DebugDraw;

public class CircleCollider extends Component {
    private float radius = 1f;
    private Vector2f offset = new Vector2f();

    @Override
    public void update(float deltaTime) {

    }

    @Override
    public void editorUpdate(float deltaTime) {
        Vector2f center = new Vector2f(gameObject.transform.position).add(offset);
        DebugDraw.drawCircle(center, radius);
    }

    public Vector2f getOffset() {
        return this.offset;
    }

    public void setOffset(Vector2f offset) {
        this.offset.set(offset);
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }
}

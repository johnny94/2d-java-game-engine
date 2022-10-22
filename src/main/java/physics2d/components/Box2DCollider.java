package physics2d.components;

import org.joml.Vector2f;

import components.Component;
import renderer.DebugDraw;

public class Box2DCollider extends Component {
    private Vector2f halfSize = new Vector2f(1);
    private Vector2f origin = new Vector2f();
    private Vector2f offset = new Vector2f();

    @Override
    public void update(float deltaTime) {

    }

    @Override
    public void editorUpdate(float deltaTime) {
        Vector2f center = new Vector2f(gameObject.transform.position).add(this.offset);
        DebugDraw.drawBox(center, this.halfSize, gameObject.transform.rotation);
    }

    public Vector2f getHalfSize() {
        return halfSize;
    }

    public void setHalfSize(Vector2f halfSize) {
        this.halfSize = halfSize;
    }

    public Vector2f getOrigin() {
        return this.origin;
    }

    public Vector2f getOffset() {
        return this.offset;
    }

    public void setOffset(Vector2f offset) {
        this.offset.set(offset);
    }
}

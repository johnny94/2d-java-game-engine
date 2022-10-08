package physics2d.components;

public class CircleCollider extends Collider {
    private float radius = 1f;

    @Override
    public void update(float deltaTime) {

    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }
}

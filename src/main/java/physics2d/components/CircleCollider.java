package physics2d.components;

import components.Component;

public class CircleCollider extends Component {
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

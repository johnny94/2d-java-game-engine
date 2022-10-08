package components;

import org.joml.Vector3f;
import org.joml.Vector4f;

// TODO: Maybe this can be removed?
public class RigidBody extends Component {
    // These are test fields
    private int colliderType = 0;
    private float friction = 0.8f;
    public Vector3f velocity = new Vector3f(0.0f, 0.5f, 0.0f);
    public transient Vector4f tmp = new Vector4f(0, 0, 0, 0);

    @Override
    public void update(float deltaTime) {

    }
}
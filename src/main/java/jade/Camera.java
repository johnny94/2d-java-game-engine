package jade;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class Camera {
    public Vector2f position;

    private Matrix4f projectionMatrix = new Matrix4f();
    private Matrix4f viewMatrix = new Matrix4f();

    public Camera(Vector2f position) {
        this.position = position;
        adjustProjection();
    }

    public void adjustProjection() {
        projectionMatrix.identity();
        projectionMatrix.ortho(0, 32.0f * 40.0f, 0, 32.0f * 21.0f,
                               0.0f, 100.0f);
    }

    public Matrix4f getProjectionMatrix() {
        return this.projectionMatrix;
    }

    public Matrix4f getViewMatrix() {
        Vector3f camaraFront = new Vector3f(0.0f, 0.0f, -1.0f);
        Vector3f cameraUp = new Vector3f(0.0f, 1.0f, -1.0f);

        viewMatrix.identity();
        return viewMatrix.lookAt(new Vector3f(position, 0.0f),
                                 camaraFront.add(position.x(), position.y(), 0.0f),
                                 cameraUp);
    }
}

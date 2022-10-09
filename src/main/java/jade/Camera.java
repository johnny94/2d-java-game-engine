package jade;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class Camera {
    private static final int projectionWidth = 6;
    private static final int projectionHeight = 3;
    private static final Vector2f projectionSize = new Vector2f(projectionWidth, projectionHeight);

    private float zoom = 1.0f;

    public Vector2f position;

    private Matrix4f projectionMatrix = new Matrix4f();
    private Matrix4f inverseProjectionMatrix = new Matrix4f();

    private Matrix4f viewMatrix = new Matrix4f();
    private Matrix4f inverseViewMatrix = new Matrix4f();

    public Camera(Vector2f position) {
        this.position = position;
        adjustProjection();
    }

    public void adjustProjection() {
        projectionMatrix.identity();
        projectionMatrix.ortho(0, projectionSize.x * zoom, 0, projectionSize.y * zoom,
                               0.0f, 100.0f);
        projectionMatrix.invert(inverseProjectionMatrix);
    }

    public Matrix4f getProjectionMatrix() {
        return this.projectionMatrix;
    }

    public Matrix4f getViewMatrix() {
        Vector3f camaraFront = new Vector3f(0.0f, 0.0f, -1.0f);
        Vector3f cameraUp = new Vector3f(0.0f, 1.0f, -1.0f);

        viewMatrix.identity();
        this.viewMatrix = viewMatrix.lookAt(new Vector3f(position, 0.0f),
                                            camaraFront.add(position.x(), position.y(), 0.0f),
                                            cameraUp);
        this.viewMatrix.invert(inverseViewMatrix);

        return this.viewMatrix;
    }

    public Matrix4f getInverseProjectionMatrix() {
        return inverseProjectionMatrix;
    }

    public Matrix4f getInverseViewMatrix() {
        return inverseViewMatrix;
    }

    public Vector2f getProjectionSize() {
        return projectionSize;
    }

    public float getZoom() {
        return zoom;
    }

    public void setZoom(float zoom) {
        this.zoom = zoom;
    }

    public void addZoom(float value) {
        this.zoom += value;
    }
}

package jade;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LAST;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;

public final class MouseListener {
    private static final class InstanceHolder {
        static final MouseListener instance = new MouseListener();
    }

    private double scrollX;
    private double scrollY;
    private double xPos, yPos;
    private double worldX, worldY;

    private int mouseButtonDown;

    private final boolean[] mouseButtonPressed = new boolean[GLFW_MOUSE_BUTTON_LAST + 1];
    private boolean isDragging;

    private Vector2f gameViewportPos = new Vector2f();
    private Vector2f gameViewportSize = new Vector2f();

    private MouseListener() {}

    public static MouseListener getInstance() {
        return InstanceHolder.instance;
    }

    public void mousePosCallback(long window, double xPos, double yPos) {
        if (mouseButtonDown > 0) {
            isDragging = true;
        }

        this.xPos = xPos;
        this.yPos = yPos;
    }

    public void mouseButtonCallback(long window, int button, int action, int mods) {
        if (action == GLFW_PRESS) {
            mouseButtonDown++;
            this.mouseButtonPressed[button] = true;
        } else if (action == GLFW_RELEASE) {
            mouseButtonDown--;
            this.mouseButtonPressed[button] = false;
            this.isDragging = false;
        }
    }

    public boolean mouseButtonDown(int button) {
        if (button < mouseButtonPressed.length) {
            return mouseButtonPressed[button];
        }

        return false;
    }

    public void mouseScrollCallback(long window, double xOffset, double yOffset) {
        this.scrollX = xOffset;
        this.scrollY = yOffset;
    }

    public void endFrame() {
        this.scrollX = 0;
        this.scrollY = 0;
    }

    public float getX() {
        return (float)xPos;
    }

    // ? What's this value
    public float getScreenX() {
        float currentX = getX() - gameViewportPos.x;
        currentX = (currentX / gameViewportSize.x) * 3840.0f; // TODO: Hardcoded
        return currentX;
    }

    public float getScreenY() {
        float currentY = getY() - gameViewportPos.y;
        currentY = 2160.0f - ((currentY / gameViewportSize.y) * 2160.0f); // TODO: Hardcoded
        return currentY;
    }

    public float getWorldX() {
        return getWorld().x;
    }

    public float getWorldY() {
        return getWorld().y;
    }

    public Vector2f getWorld() {
        float currentX = getX() - gameViewportPos.x;
        currentX = (currentX / gameViewportSize.x) * 2.0f - 1.0f;  // Convert to NDC

        float currentY = getY() - gameViewportPos.y;

        // Convert to NDC
        // Y coordinate need to be flip
        currentY = -((currentY / gameViewportSize.y) * 2.0f - 1.0f);

        Camera camera = Window.get().getCurrentScene().getCamera();
        Matrix4f inverseView = new Matrix4f(camera.getInverseViewMatrix());
        Matrix4f inverseProjection = new Matrix4f(camera.getInverseProjectionMatrix());

        Vector4f tmp = new Vector4f(currentX, currentY, 0, 1);
        tmp.mul(inverseView.mul(inverseProjection));

        return new Vector2f(tmp.x, tmp.y);
    }

    public float getY() {
        return (float)yPos;
    }

    public float getScrollX() {
        return (float)scrollX;
    }

    public float getScrollY() {
        return (float)scrollY;
    }

    public boolean isDragging() {
        return isDragging;
    }

    public boolean isPressed(int button) {
        return mouseButtonPressed[button];
    }

    public void setGameViewportPos(Vector2f gameViewportPos) {
        this.gameViewportPos.set(gameViewportPos);
    }

    public void setGameViewportSize(Vector2f gameViewportSize) {
        this.gameViewportSize.set(gameViewportSize);
    }
}

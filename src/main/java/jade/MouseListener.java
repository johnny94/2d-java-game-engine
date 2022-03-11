package jade;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LAST;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

import org.joml.Vector4f;

public final class MouseListener {
    private static final class InstanceHolder {
        static final MouseListener instance = new MouseListener();
    }

    private double scrollX;
    private double scrollY;
    private double xPos;
    private double yPos;
    private double xLast;
    private double yLast;

    private final boolean[] mouseButtonPressed = new boolean[GLFW_MOUSE_BUTTON_LAST + 1];
    private boolean isDragging;

    private MouseListener() {}

    public static MouseListener getInstance() {
        return InstanceHolder.instance;
    }

    public void mousePosCallback(long window, double xPos, double yPos) {
        this.xLast = this.xPos;
        this.yLast = this.yPos;
        this.xPos = xPos;
        this.yPos = yPos;

        this.isDragging = false;
        for(boolean value : mouseButtonPressed) {
            this.isDragging |= value;
        }
    }

    public void mouseButtonCallback(long window, int button, int action, int mods) {
        if (action == GLFW_PRESS) {
            this.mouseButtonPressed[button] = true;
        } else if (action == GLFW_RELEASE) {
            this.mouseButtonPressed[button] = false;
            this.isDragging = false;
        }
    }

    public void mouseScrollCallback(long window, double xOffset, double yOffset) {
        this.scrollX = xOffset;
        this.scrollY = yOffset;
    }

    public void endFrame() {
        this.scrollX = 0;
        this.scrollY = 0;
        this.xLast = this.xPos;
        this.yLast = this.yPos;
    }

    public float getX() {
        return (float)xPos;
    }

    public float getOrthoX() {
        float currentX = getX();

        // Convert to NDC
        currentX = (currentX / Window.get().getWidth()) * 2.0f - 1.0f;

        Vector4f tmp = new Vector4f(currentX, 0, 0, 1);
        Camera c = Window.get().getCurrentScene().getCamera();
        tmp.mul(c.getInverseProjectionMatrix())
           .mul(c.getInverseViewMatrix());

        return tmp.x;
    }

    public float getOrthoY() {
        float currentY = getY();

        // Convert to NDC
        currentY = (currentY / Window.get().getHeight()) * 2.0f - 1.0f;

        Vector4f tmp = new Vector4f(0, currentY, 0, 1);
        Camera c = Window.get().getCurrentScene().getCamera();
        tmp.mul(c.getInverseProjectionMatrix())
           .mul(c.getInverseViewMatrix());

        return tmp.y;
    }

    public float getY() {
        return (float)yPos;
    }

    public float getDeltaX() {
        return (float)(xLast - xPos);
    }

    public float getDeltaY() {
        return (float)(yLast - yPos);
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

}

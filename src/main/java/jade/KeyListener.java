package jade;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_LAST;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_UNKNOWN;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public final class KeyListener {
    private static final class InstanceHolder {
        static final KeyListener instance = new KeyListener();
    }

    private final boolean[] keyPressed = new boolean[GLFW_KEY_LAST + 1];
    private final boolean[] keyBeginPressed = new boolean[GLFW_KEY_LAST + 1];

    private KeyListener() {}

    public static KeyListener getInstance() {
        return InstanceHolder.instance;
    }

    public void keyCallback(long window, int key, int scancode, int action, int mods) {
        // TODO: Just ignore unknown key
        if (key == GLFW_KEY_UNKNOWN) {
            return;
        }

        if (action == GLFW_PRESS) {
            keyPressed[key] = true;
            keyBeginPressed[key] = true;
        } else if (action == GLFW_RELEASE) {
            keyPressed[key] = false;
            keyBeginPressed[key] = false;
        }
    }

    public boolean isKeyPressed(int key) {
        return keyPressed[key];
    }

    public boolean keyBeginPress(int key) {
        boolean result = keyBeginPressed[key];
        if (result) {
            keyBeginPressed[key] = false;
        }

        return result;
    }
}

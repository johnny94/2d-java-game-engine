package components;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_R;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_MIDDLE;

import org.joml.Vector2f;

import jade.Camera;
import jade.KeyListener;
import jade.MouseListener;

public class EditorCamera extends Component {

    private Camera levelEditorCamera;
    private Vector2f clickOrigin;

    private float lerpTime = 0.0f;

    private float dragDebounce = 0.032f;
    private float dragSensitivity = 30.0f;
    private float scrollSensitivity = 0.1f;

    private boolean reset;

    public EditorCamera(Camera levelEditorCamera) {
        this.levelEditorCamera = levelEditorCamera;
        this.clickOrigin = new Vector2f();
    }

    @Override
    public void editorUpdate(float deltaTime) {
        MouseListener mouseListener = MouseListener.getInstance();

        // Drag
        if (mouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_MIDDLE) && dragDebounce > 0) {
            clickOrigin = mouseListener.getWorld();
            dragDebounce -= deltaTime;
            return;
        } else if (mouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_MIDDLE)) {
            Vector2f mousePos = mouseListener.getWorld();
            Vector2f delta = new Vector2f(mousePos).sub(clickOrigin);
            levelEditorCamera.position.sub(delta.mul(deltaTime).mul(dragSensitivity));
            clickOrigin.lerp(mousePos, deltaTime);
        }

        if (dragDebounce <= 0 && !mouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_MIDDLE)) {
            dragDebounce = 0.1f;
        }

        // Zoom in/out
        if (mouseListener.getScrollY() != 0.0f) {
            float addValue = (float)Math.pow(Math.abs(mouseListener.getScrollY() * scrollSensitivity),
                                             1 / levelEditorCamera.getZoom());
            addValue *= -Math.signum(mouseListener.getScrollY());
            levelEditorCamera.addZoom(addValue);
        }

        // Reset
        if (KeyListener.getInstance().isKeyPressed(GLFW_KEY_R)) {
            reset = true;
        }

        if (reset) {
            levelEditorCamera.position.lerp(new Vector2f(), lerpTime);

            // Use lerp function to reset zoom
            levelEditorCamera.setZoom(levelEditorCamera.getZoom() +
                                      (1.0f - levelEditorCamera.getZoom()) * lerpTime);
            lerpTime += 0.1f * deltaTime;

            // Clamp if we close to origin
            if (Math.abs(levelEditorCamera.position.x) <= 5.0f &&
                Math.abs(levelEditorCamera.position.y) <= 5.0f) {
                lerpTime = 0.0f;
                levelEditorCamera.position.set(0, 0);
                levelEditorCamera.setZoom(1.0f);
                reset = false;
            }
        }


    }
}

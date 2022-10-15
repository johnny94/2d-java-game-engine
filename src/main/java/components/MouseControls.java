package components;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

import java.util.Optional;

import org.joml.Vector4f;

import components.animation.StateMachine;
import jade.GameObject;
import jade.KeyListener;
import jade.MouseListener;
import jade.Window;
import util.Settings;

public class MouseControls extends Component {
    private static final MouseListener mouseListener = MouseListener.getInstance();
    private static final float DEBOUNCE_TIME = 0.05f;

    private GameObject holdingObject;
    private float debounce = DEBOUNCE_TIME;

    public void pickUpObject(GameObject go) {
        if (this.holdingObject != null) {
            this.holdingObject.destroy();
        }

        this.holdingObject = go;
        this.holdingObject.getComponent(SpriteRenderer.class).get()
                          .setColor(new Vector4f(0.8f, 0.8f, 0.8f, 0.5f));
        this.holdingObject.addComponent(new NonPickable());
        Window.get().getCurrentScene().addGameObject(go);
    }

    public void place() {
        GameObject go = this.holdingObject.copy();
        go.getComponent(StateMachine.class).ifPresent(StateMachine::refreshTexture);

        Optional<SpriteRenderer> maybeRenderer = go.getComponent(SpriteRenderer.class);
        if (maybeRenderer.isPresent()) {
            maybeRenderer.get().setColor(new Vector4f(1, 1, 1, 1));
        }
        go.removeComponent(NonPickable.class);
        Window.get().getCurrentScene().addGameObject(go);
    }

    @Override
    public void editorUpdate(float deltaTime) {
        debounce -= deltaTime;
        if (this.holdingObject != null && debounce <= 0) {
            holdingObject.transform.position.x = (int)Math.floor(mouseListener.getWorldX() / Settings.GRID_WIDTH) *
                                                 Settings.GRID_WIDTH + Settings.GRID_WIDTH / 2.0f;
            holdingObject.transform.position.y = (int)Math.floor(mouseListener.getWorldY() / Settings.GRID_HEIGHT) *
                                                 Settings.GRID_HEIGHT + Settings.GRID_HEIGHT / 2.0f;

            if (mouseListener.isPressed(GLFW_MOUSE_BUTTON_LEFT)) {
                place();
                debounce = DEBOUNCE_TIME;
            }

            if (KeyListener.getInstance().isKeyPressed(GLFW_KEY_ESCAPE)) {
                holdingObject.destroy();
                holdingObject = null;
            }
        }
    }
}

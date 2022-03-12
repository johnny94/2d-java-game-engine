package components;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

import jade.GameObject;
import jade.MouseListener;
import jade.Window;

public class MouseControls extends Component {
    private static final MouseListener mouseListener = MouseListener.getInstance();
    private GameObject holdingObject;

    public void pickUpObject(GameObject go) {
        this.holdingObject = go;
        Window.get().getCurrentScene().addGameObject(go);
    }

    public void place() {
        this.holdingObject = null;
    }

    @Override
    public void update(double deltaTime) {
        if (this.holdingObject != null) {
            holdingObject.transform.position.x = mouseListener.getOrthoX() - 16;
            holdingObject.transform.position.y = mouseListener.getOrthoY() - 16;
            if (mouseListener.isPressed(GLFW_MOUSE_BUTTON_LEFT)) {
                place();
            }
        }
    }
}

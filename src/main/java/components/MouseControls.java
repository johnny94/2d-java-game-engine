package components;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

import jade.GameObject;
import jade.MouseListener;
import jade.Window;
import util.Settings;

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
    public void update(float deltaTime) {
        if (this.holdingObject != null) {
            holdingObject.transform.position.x = (int)(mouseListener.getOrthoX() / Settings.GRID_WIDTH) *
                                                 Settings.GRID_WIDTH;
            holdingObject.transform.position.y = (int)(mouseListener.getOrthoY() / Settings.GRID_HEIGHT)*
                                                 Settings.GRID_HEIGHT;

            if (mouseListener.isPressed(GLFW_MOUSE_BUTTON_LEFT)) {
                place();
            }
        }
    }
}

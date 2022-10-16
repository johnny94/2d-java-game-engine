package components;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_K;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_CONTROL;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import editor.PropertiesWindow;
import jade.GameObject;
import jade.KeyListener;
import jade.Window;
import util.Settings;

public class KeyControls extends Component {

    @Override
    public void editorUpdate(float deltaTime) {
        PropertiesWindow propertiesWindow = Window.get().getImGuiLayer().getPropertiesWindow();
        Optional<GameObject> maybeActiveGameObject = propertiesWindow.getActiveGameObject();

        // For multi select
        List<GameObject> activeGameObjects = propertiesWindow.getActiveGameObjects();
        KeyListener keyListener = KeyListener.getInstance();

        // Can we write single select and multi select within within this if?
        if (keyListener.isKeyPressed(GLFW_KEY_LEFT_CONTROL) && keyListener.keyBeginPress(GLFW_KEY_D) &&
            maybeActiveGameObject.isPresent()) {
            GameObject newObject = maybeActiveGameObject.get().copy();
            Window.get().getCurrentScene().addGameObject(newObject);
            newObject.transform.position.add(Settings.GRID_WIDTH, 0.0f);
            propertiesWindow.setActiveGameObject(newObject);
        } else if (keyListener.isKeyPressed(GLFW_KEY_LEFT_CONTROL) && keyListener.keyBeginPress(GLFW_KEY_D) &&
                   activeGameObjects.size() > 1) {

            // TODO: I think this is not necessary
            List<GameObject> gameObjects = new ArrayList<>(activeGameObjects);
            propertiesWindow.clearSelected();
            for (GameObject go : gameObjects) {
                GameObject tmp = go.copy();
                Window.get().getCurrentScene().addGameObject(tmp);
                propertiesWindow.addActiveGameObject(tmp);
            }

        } else if (keyListener.isKeyPressed(GLFW_KEY_K)) {
            for (GameObject go : activeGameObjects) {
                go.destroy();
            }
            propertiesWindow.clearSelected();
        }
    }
}

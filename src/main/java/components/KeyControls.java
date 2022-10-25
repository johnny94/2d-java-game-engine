package components;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_K;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_L;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_CONTROL;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_SHIFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_PERIOD;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_UP;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import components.animation.StateMachine;
import editor.PropertiesWindow;
import jade.GameObject;
import jade.KeyListener;
import jade.Window;
import util.Settings;

public class KeyControls extends Component {
    private static final float DEBOUNCE_TIME = 0.2f;
    private float debounce = 0;

    @Override
    public void editorUpdate(float deltaTime) {
        debounce -= deltaTime;

        PropertiesWindow propertiesWindow = Window.get().getImGuiLayer().getPropertiesWindow();
        Optional<GameObject> maybeActiveGameObject = propertiesWindow.getActiveGameObject();

        // For multi select
        List<GameObject> activeGameObjects = propertiesWindow.getActiveGameObjects();
        KeyListener keyListener = KeyListener.getInstance();

        float multiplier = keyListener.isKeyPressed(GLFW_KEY_LEFT_SHIFT) ? 0.1f : 1;

        // Can we write single select and multi select within within this if?
        if (keyListener.isKeyPressed(GLFW_KEY_LEFT_CONTROL) && keyListener.keyBeginPress(GLFW_KEY_D) &&
            maybeActiveGameObject.isPresent()) {
            GameObject newObject = maybeActiveGameObject.get().copy();
            Window.get().getCurrentScene().addGameObject(newObject);
            newObject.transform.position.add(Settings.GRID_WIDTH, 0.0f);
            propertiesWindow.setActiveGameObject(newObject);

            newObject.getComponent(StateMachine.class).ifPresent(StateMachine::refreshTexture);

        } else if (keyListener.isKeyPressed(GLFW_KEY_LEFT_CONTROL) && keyListener.keyBeginPress(GLFW_KEY_D) &&
                   activeGameObjects.size() > 1) {

            // TODO: I think this is not necessary
            List<GameObject> gameObjects = new ArrayList<>(activeGameObjects);
            propertiesWindow.clearSelected();
            for (GameObject go : gameObjects) {
                GameObject copy = go.copy();
                Window.get().getCurrentScene().addGameObject(copy);
                propertiesWindow.addActiveGameObject(copy);
                copy.getComponent(StateMachine.class).ifPresent(StateMachine::refreshTexture);
            }

        } else if (keyListener.isKeyPressed(GLFW_KEY_K)) {
            for (GameObject go : activeGameObjects) {
                go.destroy();
            }
            propertiesWindow.clearSelected();
        } else if (keyListener.isKeyPressed(GLFW_KEY_L) && debounce < 0) {
            debounce = DEBOUNCE_TIME;
            for (GameObject go : activeGameObjects) {
                go.transform.zIndex++;
            }
        } else if (keyListener.isKeyPressed(GLFW_KEY_PERIOD) && debounce < 0) {
            debounce = DEBOUNCE_TIME;
            for (GameObject go : activeGameObjects) {
                go.transform.zIndex--;
            }
        } else if (keyListener.isKeyPressed(GLFW_KEY_UP) && debounce < 0) {
            debounce = DEBOUNCE_TIME;
            for (GameObject go : activeGameObjects) {
                go.transform.position.y += Settings.GRID_HEIGHT * multiplier;
            }
        }else if (keyListener.isKeyPressed(GLFW_KEY_DOWN) && debounce < 0) {
            debounce = DEBOUNCE_TIME;
            for (GameObject go : activeGameObjects) {
                go.transform.position.y -= Settings.GRID_HEIGHT * multiplier;
            }
        }else if (keyListener.isKeyPressed(GLFW_KEY_LEFT) && debounce < 0) {
            debounce = DEBOUNCE_TIME;
            for (GameObject go : activeGameObjects) {
                go.transform.position.x -= Settings.GRID_WIDTH * multiplier;
            }
        }else if (keyListener.isKeyPressed(GLFW_KEY_RIGHT) && debounce < 0) {
            debounce = DEBOUNCE_TIME;
            for (GameObject go : activeGameObjects) {
                go.transform.position.x += Settings.GRID_WIDTH * multiplier;
            }
        }
    }
}

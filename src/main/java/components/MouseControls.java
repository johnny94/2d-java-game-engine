package components;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector4f;

import components.animation.StateMachine;
import jade.GameObject;
import jade.KeyListener;
import jade.MouseListener;
import jade.Window;
import renderer.DebugDraw;
import renderer.PickingTexture;
import scenes.Scene;
import util.Settings;

public class MouseControls extends Component {
    private static final MouseListener mouseListener = MouseListener.getInstance();
    private static final float DEBOUNCE_TIME = 0.2f;

    private GameObject holdingObject;
    private float debounce = DEBOUNCE_TIME;

    // Box select
    private boolean boxSelectSet;
    private Vector2f boxSelectStart = new Vector2f();
    private Vector2f boxSelectEnd = new Vector2f();

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

        PickingTexture pickingTexture = Window.get().getImGuiLayer().getPropertiesWindow().getPickingTexture();
        Scene currentScene = Window.get().getCurrentScene();
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
        } else if (!mouseListener.isDragging() && mouseListener.isPressed(GLFW_MOUSE_BUTTON_LEFT) &&
                  debounce <= 0) {
            int x = (int)MouseListener.getInstance().getScreenX();
            int y = (int)MouseListener.getInstance().getScreenY();
            int gameObjectUid = pickingTexture.readPixel(x, y);
            Optional<GameObject> pick = currentScene.getGameObject(gameObjectUid);
            if (pick.isPresent() && !pick.get().getComponent(NonPickable.class).isPresent()) {
                Window.get().getImGuiLayer().getPropertiesWindow().setActiveGameObject(pick.get());
            } else if (!pick.isPresent() && !MouseListener.getInstance().isDragging()) {
                Window.get().getImGuiLayer().getPropertiesWindow().clearSelected();
            }

            debounce = 0.2f;
        } else if (mouseListener.isDragging() && mouseListener.isPressed(GLFW_MOUSE_BUTTON_LEFT)) {
            if (!boxSelectSet) {
                Window.get().getImGuiLayer().getPropertiesWindow().clearSelected();
                boxSelectStart = mouseListener.getScreen();
                boxSelectSet = true;
            }
            boxSelectEnd = mouseListener.getScreen();
            Vector2f boxSelectStartWorld = mouseListener.screenToWorld(boxSelectStart);
            Vector2f boxSelectEndWorld = mouseListener.screenToWorld(boxSelectEnd);
            Vector2f halfSize = new Vector2f(boxSelectEndWorld).sub(boxSelectStartWorld).mul(0.5f);
            DebugDraw.drawBox(new Vector2f(boxSelectStartWorld).add(halfSize),
                              new Vector2f(halfSize).mul(2.0f), 0);
        } else if (boxSelectSet) {
            boxSelectSet = false;
            int screenStartX = (int)boxSelectStart.x;
            int screenStartY = (int)boxSelectStart.y;
            int screenEndX = (int)boxSelectEnd.x;
            int screenEndY = (int)boxSelectEnd.y;
            boxSelectStart.zero();
            boxSelectEnd.zero();

            if (screenEndX < screenStartY) {
                int tmp = screenStartX;
                screenStartX = screenEndX;
                screenEndX = tmp;
            }

            if (screenEndY < screenStartY) {
                int tmp = screenStartY;
                screenStartY = screenEndY;
                screenEndY = tmp;
            }

            float[] gameObjectIds = pickingTexture.readPixels(
                    new Vector2i(screenStartX, screenStartY),
                    new Vector2i(screenEndX, screenEndY)
            );

            Set<Integer> uniqueGameObjectIds = new HashSet<>();
            for (float id : gameObjectIds) {
                uniqueGameObjectIds.add((int)id);
            }

            for (int id : uniqueGameObjectIds) {
                Optional<GameObject> maybeGameObject = Window.get().getCurrentScene().getGameObject(id);
                if (maybeGameObject.isPresent()) {
                    GameObject go = maybeGameObject.get();
                    if (!go.getComponent(NonPickable.class).isPresent()) {
                        Window.get().getImGuiLayer().getPropertiesWindow().addActiveGameObject(go);
                    }
                }
            }
        }
    }
}

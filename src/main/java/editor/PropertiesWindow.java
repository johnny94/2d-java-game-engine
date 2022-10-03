package editor;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

import java.util.Optional;

import imgui.ImGui;
import jade.GameObject;
import jade.MouseListener;
import renderer.PickingTexture;
import scenes.Scene;

public class PropertiesWindow {
    private Optional<GameObject> activeGameObject = Optional.empty();
    private PickingTexture pickingTexture;

    private float deBounce = 0.2f;

    public PropertiesWindow(PickingTexture pickingTexture) {
        this.pickingTexture = pickingTexture;
    }

    public void update(double deltaTime, Scene currentScene) {
        deBounce -= deltaTime;

        if (MouseListener.getInstance().isPressed(GLFW_MOUSE_BUTTON_LEFT) && deBounce < 0) {
            int x = (int)MouseListener.getInstance().getScreenX();
            int y = (int)MouseListener.getInstance().getScreenY();
            int gameObjectUid = pickingTexture.readPixel(x, y);
            activeGameObject = currentScene.getGameObject(gameObjectUid);
            this.deBounce = 0.2f;
        }
    }

    public void imGui() {
        if (activeGameObject.isPresent()) {
            ImGui.begin("Properties");
            activeGameObject.get().imGui();
            ImGui.end();
        }
    }

    public Optional<GameObject> getActiveGameObject() {
        return this.activeGameObject;
    }
}

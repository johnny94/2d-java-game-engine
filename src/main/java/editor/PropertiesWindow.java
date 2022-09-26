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

    public PropertiesWindow(PickingTexture pickingTexture) {
        this.pickingTexture = pickingTexture;
    }

    public void update(double deltaTime, Scene currentScene) {
        if (MouseListener.getInstance().isPressed(GLFW_MOUSE_BUTTON_LEFT)) {
            int x = (int)MouseListener.getInstance().getScreenX();
            int y = (int)MouseListener.getInstance().getScreenY();
            int gameObjectUid = pickingTexture.readPixel(x, y);
            activeGameObject = currentScene.getGameObject(gameObjectUid);
        }
    }

    public void imGui() {
        if (activeGameObject.isPresent()) {
            ImGui.begin("Properties");
            activeGameObject.get().imGui();
            ImGui.end();
        }
    }
}

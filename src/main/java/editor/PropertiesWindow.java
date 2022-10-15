package editor;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

import java.util.Optional;

import components.NonPickable;
import imgui.ImGui;
import jade.GameObject;
import jade.MouseListener;
import physics2d.components.Box2DCollider;
import physics2d.components.CircleCollider;
import physics2d.components.RigidBody2D;
import renderer.PickingTexture;
import scenes.Scene;

public class PropertiesWindow {
    private GameObject activeGameObject;
    private PickingTexture pickingTexture;

    private float deBounce = 0.2f;

    public PropertiesWindow(PickingTexture pickingTexture) {
        this.pickingTexture = pickingTexture;
    }

    public void update(double deltaTime, Scene currentScene) {
        deBounce -= deltaTime;

        if (!MouseListener.getInstance().isDragging() && MouseListener.getInstance().isPressed(GLFW_MOUSE_BUTTON_LEFT) && deBounce < 0) {
            int x = (int)MouseListener.getInstance().getScreenX();
            int y = (int)MouseListener.getInstance().getScreenY();
            int gameObjectUid = pickingTexture.readPixel(x, y);
            Optional<GameObject> pick = currentScene.getGameObject(gameObjectUid);
            if (pick.isPresent() && !pick.get().getComponent(NonPickable.class).isPresent()) {
                activeGameObject = pick.get();
            } else if (!pick.isPresent() && !MouseListener.getInstance().isDragging()) {
                activeGameObject = null;
            }
            this.deBounce = 0.2f;
        }
    }

    public void imGui() {
        if (activeGameObject != null) {
            ImGui.begin("Properties");

            if (ImGui.beginPopupContextWindow("ComponentAdder")) {
                if (ImGui.menuItem("Add RigidBody")) {
                    if (!activeGameObject.getComponent(RigidBody2D.class).isPresent()) {
                        activeGameObject.addComponent(new RigidBody2D());
                    }
                }

                if (ImGui.menuItem("Add BoxCollider")) {
                    if (!activeGameObject.getComponent(Box2DCollider.class).isPresent() &&
                        !activeGameObject.getComponent(CircleCollider.class).isPresent()) {
                        activeGameObject.addComponent(new Box2DCollider());
                    }
                }

                if (ImGui.menuItem("Add CircleCollider")) {
                    if (!activeGameObject.getComponent(CircleCollider.class).isPresent() &&
                        !activeGameObject.getComponent(Box2DCollider.class).isPresent()) {
                        activeGameObject.addComponent(new CircleCollider());
                    }
                }

                ImGui.endPopup();
            }

            activeGameObject.imGui();
            ImGui.end();
        }
    }

    public Optional<GameObject> getActiveGameObject() {
        return Optional.ofNullable(activeGameObject);
    }

    public void setActiveGameObject(GameObject gameObject) {
        this.activeGameObject = gameObject;
    }
}

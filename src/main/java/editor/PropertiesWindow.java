package editor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import imgui.ImGui;
import jade.GameObject;
import physics2d.components.Box2DCollider;
import physics2d.components.CircleCollider;
import physics2d.components.RigidBody2D;
import renderer.PickingTexture;

public class PropertiesWindow {
    private List<GameObject> activeGameObjects = new ArrayList<>();
    private GameObject activeGameObject;
    private PickingTexture pickingTexture;

    public PropertiesWindow(PickingTexture pickingTexture) {
        this.pickingTexture = pickingTexture;
    }

    public void imGui() {
        if (activeGameObjects.size() == 1 && activeGameObjects.get(0) != null) {
            activeGameObject = activeGameObjects.get(0);
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
        return activeGameObjects.size() == 1 ? Optional.ofNullable(activeGameObject) : Optional.empty();
    }

    public List<GameObject> getActiveGameObjects() {
        return this.activeGameObjects;
    }

    public void clearSelected() {
        this.activeGameObjects.clear();
    }

    public void setActiveGameObject(GameObject gameObject) {
        if (gameObject != null) {
            clearSelected();
            this.activeGameObjects.add(gameObject);
        }
    }

    public void addActiveGameObject(GameObject gameObject) {
        if (gameObject != null) {
            this.activeGameObjects.add(gameObject);
        }
    }

    public PickingTexture getPickingTexture() {
        return this.pickingTexture;
    }
}

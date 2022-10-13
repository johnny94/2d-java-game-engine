package editor;

import java.util.List;

import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;
import jade.GameObject;
import jade.Window;

public class SceneHierarchyWindow {
    private static final String payloadDragDropType = "SceneHierarchy";

    public void imGui() {
        ImGui.begin("Scene Hierarchy");
        List<GameObject> gameObjects = Window.get().getCurrentScene().getGameObjects();

        int index = 0;
        for(GameObject gameObject : gameObjects) {
            if (!gameObject.isDoSerialization()) {
                continue;
            }

            boolean treeNodeOpen = doTreeNode(gameObject, index);
            if (treeNodeOpen) {
                ImGui.treePop();
            }
            index++;
        }
        ImGui.end();
    }

    private boolean doTreeNode(GameObject gameObject, int index) {
        ImGui.pushID(index);
        boolean treeNodeOpen = ImGui.treeNodeEx(
                gameObject.name,
                ImGuiTreeNodeFlags.DefaultOpen |
                ImGuiTreeNodeFlags.FramePadding |
                ImGuiTreeNodeFlags.OpenOnArrow |
                ImGuiTreeNodeFlags.SpanAvailWidth,
                gameObject.name
        );
        ImGui.popID();

        if (ImGui.beginDragDropSource()) {
            ImGui.setDragDropPayload(payloadDragDropType, gameObject);

            ImGui.text(gameObject.name);

            ImGui.endDragDropSource();
        }

        if (ImGui.beginDragDropTarget()) {
            GameObject payloadObj = ImGui.acceptDragDropPayload(payloadDragDropType, GameObject.class);
            if (payloadObj != null) {
                System.out.println("Payload accepted: " + payloadObj.name);
            }

            ImGui.endDragDropTarget();
        }

        return treeNodeOpen;
    }
}

package editor;

import java.util.List;

import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;
import jade.GameObject;
import jade.Window;

public class SceneHierarchyWindow {
    public void imGui() {
        ImGui.begin("Scene Hierarchy");
        List<GameObject> gameObjects = Window.get().getCurrentScene().getGameObjects();

        int index = 0;
        for(GameObject gameObject : gameObjects) {
            if (!gameObject.isDoSerialization()) {
                continue;
            }

            ImGui.pushID(index);
            boolean treeNodeOpen = ImGui.treeNodeEx(
                    gameObject.getName(),
                    ImGuiTreeNodeFlags.DefaultOpen |
                    ImGuiTreeNodeFlags.FramePadding |
                    ImGuiTreeNodeFlags.OpenOnArrow |
                    ImGuiTreeNodeFlags.SpanAvailWidth,
                    gameObject.getName()
            );
            ImGui.popID();
            if (treeNodeOpen) {
                ImGui.treePop();
            }
            index++;
        }
        ImGui.end();
    }
}

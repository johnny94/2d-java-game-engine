package jade;

import imgui.ImGui;

public class ImGuiLayer {
    private boolean showText;

    public void imGui() {
        ImGui.begin("Hello");
        if (ImGui.button("I am a button")) {
            showText = true;
        }

        if (showText) {
            ImGui.text("You clicked a button");
            ImGui.sameLine();
            if (ImGui.button("Hide text")) {
                showText = false;
            }
        }
        ImGui.end();
    }
}

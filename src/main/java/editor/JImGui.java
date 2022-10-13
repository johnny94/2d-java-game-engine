package editor;

import org.joml.Vector2f;
import org.joml.Vector4f;

import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiStyleVar;
import imgui.type.ImString;

public class JImGui {
    private static final float DEFAULT_COLUMN_WIDTH = 220.0f;

    public static void drawVec2Control(String label, Vector2f values) {
        drawVec2Control(label, values, 0.0f);
    }

    public static void drawVec2Control(String label, Vector2f values, float resetValue) {
        drawVec2Control(label, values, resetValue, DEFAULT_COLUMN_WIDTH);
    }

    public static void drawVec2Control(String label, Vector2f values, float resetValue, float columnWidth) {
        ImGui.pushID(label);

        ImGui.columns(2);
        ImGui.setColumnWidth(0, columnWidth);
        ImGui.text(label);
        ImGui.nextColumn();

        ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 0, 0);
        float lineHeight = ImGui.getFontSize() + ImGui.getStyle().getFramePaddingY() * 2.0f;
        Vector2f buttonSize = new Vector2f(lineHeight + 3.0f, lineHeight);
        float widthEach = (ImGui.calcItemWidth() - buttonSize.x * 2.0f) / 2.0f;

        // X controller
        ImGui.pushItemWidth(widthEach);
        ImGui.pushStyleColor(ImGuiCol.Button, 0.8f, 0.1f, 0.15f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0.9f, 0.2f, 0.2f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0.8f, 0.1f, 0.15f, 1.0f);

        if (ImGui.button("X", buttonSize.x, buttonSize.y)) {
            values.x = resetValue;
        }
        ImGui.popStyleColor(3);

        ImGui.sameLine();
        float[] vecValueX = { values.x };
        ImGui.dragFloat("##x", vecValueX, 0.1f);
        ImGui.popItemWidth();
        ImGui.sameLine();

        // Y controller
        ImGui.pushItemWidth(widthEach);
        ImGui.pushStyleColor(ImGuiCol.Button, 0.2f, 0.7f, 0.2f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0.3f, 0.8f, 0.3f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0.2f, 0.7f, 0.2f, 1.0f);
        if (ImGui.button("Y", buttonSize.x, buttonSize.y)) {
            values.y = resetValue;
        }
        ImGui.popStyleColor(3);

        ImGui.sameLine();
        float[] vecValueY = { values.y };
        ImGui.dragFloat("##y", vecValueY, 0.1f);
        ImGui.popItemWidth();
        ImGui.sameLine();


        ImGui.nextColumn();
        values.x = vecValueX[0];
        values.y = vecValueY[0];

        ImGui.popStyleVar();
        ImGui.columns(1);
        ImGui.popID();
    }

    public static float drawFloat(String label, float values) {
        ImGui.pushID(label);

        ImGui.columns(2);
        ImGui.setColumnWidth(0, DEFAULT_COLUMN_WIDTH);
        ImGui.text(label);
        ImGui.nextColumn();

        float[] valArr = { values };
        ImGui.dragFloat("##dragFloat", valArr, 0.1f);

        ImGui.columns(1);
        ImGui.popID();

        return valArr[0];
    }

    public static int drawInt(String label, int values) {
        ImGui.pushID(label);

        ImGui.columns(2);
        ImGui.setColumnWidth(0, DEFAULT_COLUMN_WIDTH);
        ImGui.text(label);
        ImGui.nextColumn();

        int[] valArr = { values };
        ImGui.dragInt("##dragInt", valArr, 0.1f);

        ImGui.columns(1);
        ImGui.popID();

        return valArr[0];
    }

    public static boolean colorPicker4(String label, Vector4f color) {
        boolean result = false;
        ImGui.pushID(label);

        ImGui.columns(2);
        ImGui.setColumnWidth(0, DEFAULT_COLUMN_WIDTH);
        ImGui.text(label);
        ImGui.nextColumn();

        float[] imColor = { color.x, color.y, color.z, color.w };
        if (ImGui.colorEdit4("##colorPicker", imColor)) {
            color.set(imColor[0], imColor[1], imColor[2], imColor[3]);
            result = true;
        }

        ImGui.columns(1);
        ImGui.popID();

        return result;
    }

    public static String inputText(String label, String text) {
        ImGui.pushID(label);

        ImGui.columns(2);
        ImGui.setColumnWidth(0, DEFAULT_COLUMN_WIDTH);
        ImGui.text(label);
        ImGui.nextColumn();

        ImString result = new ImString(text, 256);
        if (ImGui.inputText("##" + label, result)) {
            ImGui.columns(1);
            ImGui.popID();
            return result.get();
        }

        ImGui.columns(1);
        ImGui.popID();

        return text;
    }
}

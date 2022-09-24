package editor;

import org.joml.Vector2f;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiWindowFlags;
import jade.MouseListener;
import jade.Window;

public class GameViewWindow {
    private static float leftX, rightX, topY, bottomY;

    public static void imgui() {
        ImGui.begin("Game Viewport", ImGuiWindowFlags.NoScrollbar |
                                     ImGuiWindowFlags.NoScrollWithMouse);

        ImVec2 windowSize = getLargestSizeForViewPort();
        ImVec2 windowPos = getCenteredPositionForViewport(windowSize);

        ImGui.setCursorPos(windowPos.x, windowPos.y);
        ImVec2 topLeft = new ImVec2();
        ImGui.getCursorScreenPos(topLeft);
        topLeft.x -= ImGui.getScrollX();
        topLeft.y -= ImGui.getScrollY();
        leftX = topLeft.x;
        bottomY = topLeft.y; // The coordinate between ImGui and openGL is inverse.
        rightX = topLeft.x + windowSize.x;
        topY = bottomY + windowSize.y;

        MouseListener.getInstance().setGameViewportPos(new Vector2f(topLeft.x, topLeft.y));
        MouseListener.getInstance().setGameViewportSize(new Vector2f(windowSize.x, windowSize.y));


        int texture2d = Window.get().getFramebuffer().getTextureId();
        ImGui.image(texture2d, windowSize.x, windowSize.y, 0, 1, 1, 0);

        ImGui.end();
    }

    private static ImVec2 getLargestSizeForViewPort() {
        ImVec2 windowSize = getWindowSize();

        float aspectWidth = windowSize.x;
        float aspectHeight = aspectWidth / Window.get().getTargetAspectRatio();
        if (aspectHeight > windowSize.y) {
            // Swtich to pillarbox mode
            aspectHeight = windowSize.y;
            aspectWidth = aspectHeight * Window.get().getTargetAspectRatio();
        }

        return new ImVec2(aspectWidth, aspectHeight);
    }

    private static ImVec2 getCenteredPositionForViewport(ImVec2 aspectSize) {
        ImVec2 windowSize = getWindowSize();
        float viewportX = (windowSize.x / 2.0f) - (aspectSize.x / 2.0f);
        float viewportY = (windowSize.y / 2.0f) - (aspectSize.y / 2.0f);

        return new ImVec2(viewportX + ImGui.getCursorPosX(),
                          viewportY + ImGui.getCursorPosY());
    }

    private static ImVec2 getWindowSize() {
        ImVec2 windowSize = new ImVec2();
        ImGui.getContentRegionAvail(windowSize);
        windowSize.x -= ImGui.getScrollX();
        windowSize.y -= ImGui.getScrollY();

        return windowSize;
    }




}

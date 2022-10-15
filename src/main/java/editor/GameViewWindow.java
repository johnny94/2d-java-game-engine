package editor;

import org.joml.Vector2f;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiWindowFlags;
import jade.MouseListener;
import jade.Window;
import observers.EventSystem;
import observers.events.Event;
import observers.events.EventType;

public class GameViewWindow {
    private float leftX, rightX, topY, bottomY;
    private boolean isPlaying;

    public void imgui() {
        ImGui.begin("Game Viewport", ImGuiWindowFlags.NoScrollbar |
                                     ImGuiWindowFlags.NoScrollWithMouse |
                                     ImGuiWindowFlags.MenuBar);

        ImGui.beginMenuBar();
        if (ImGui.menuItem("Play", "", isPlaying, !isPlaying)) {
            isPlaying = true;
            EventSystem.notify(null, new Event(EventType.GameEngineStartPlay));
        }

        if (ImGui.menuItem("Stop", "", !isPlaying, isPlaying)) {
            isPlaying = false;
            EventSystem.notify(null, new Event(EventType.GameEngineStopPlay));
        }
        ImGui.endMenuBar();


        ImGui.setCursorPos(ImGui.getCursorPosX(), ImGui.getCursorPosY());
        ImVec2 windowSize = getLargestSizeForViewPort();
        ImVec2 windowPos = getCenteredPositionForViewport(windowSize);
        ImGui.setCursorPos(windowPos.x, windowPos.y);

        leftX = windowPos.x + 10;
        bottomY = windowPos.y; // The coordinate between ImGui and openGL is inverse.
        rightX = windowPos.x + windowSize.x + 10;
        topY = windowPos.y + windowSize.y;


        int texture2d = Window.get().getFramebuffer().getTextureId();
        ImGui.image(texture2d, windowSize.x, windowSize.y, 0, 1, 1, 0);

        // TODO: The result is different from the video "#44 Even More Bug Fixing 17:41"
        MouseListener.getInstance().setGameViewportPos(new Vector2f(windowPos.x + 10, windowPos.y));
        MouseListener.getInstance().setGameViewportSize(new Vector2f(windowSize.x, windowSize.y));


        ImGui.end();
    }
    public boolean getWantCaptureMouse() {
        return MouseListener.getInstance().getX() >= leftX && MouseListener.getInstance().getX() <= rightX &&
               MouseListener.getInstance().getY() >= bottomY && MouseListener.getInstance().getY() <= topY;
    }

    private ImVec2 getLargestSizeForViewPort() {
        ImVec2 windowSize = new ImVec2();
        ImGui.getContentRegionAvail(windowSize);

        float aspectWidth = windowSize.x;
        float aspectHeight = aspectWidth / Window.get().getTargetAspectRatio();
        if (aspectHeight > windowSize.y) {
            // Swtich to pillarbox mode
            aspectHeight = windowSize.y;
            aspectWidth = aspectHeight * Window.get().getTargetAspectRatio();
        }

        return new ImVec2(aspectWidth, aspectHeight);
    }

    private ImVec2 getCenteredPositionForViewport(ImVec2 aspectSize) {
        ImVec2 windowSize = new ImVec2();
        ImGui.getContentRegionAvail(windowSize);

        float viewportX = (windowSize.x / 2.0f) - (aspectSize.x / 2.0f);
        float viewportY = (windowSize.y / 2.0f) - (aspectSize.y / 2.0f);

        return new ImVec2(viewportX + ImGui.getCursorPosX(),
                          viewportY + ImGui.getCursorPosY());
    }
}

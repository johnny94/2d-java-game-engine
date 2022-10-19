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
    private boolean isPlaying;
    private boolean hover;

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

        ImVec2 windowSize = getLargestSizeForViewPort();

        // The following two lines are problematic
        //ImVec2 windowPos = getCenteredPositionForViewport(windowSize);
        //ImGui.setCursorPos(windowPos.x, windowPos.y);

        // TODO: The one problem here is that the position of "game window" is fixed.
        // But this give the correct result so I will ignore for now because it doesn't affect the functionality.
        ImVec2 windowPos = ImGui.getCursorScreenPos();


        int texture2d = Window.get().getFramebuffer().getTextureId();
        ImGui.imageButton(texture2d, windowSize.x, windowSize.y, 0, 1, 1, 0);
        hover = ImGui.isItemHovered();

        // TODO: The result is different from the video "#44 Even More Bug Fixing 17:41"
        // But ImGui.getCursorScreenPos() will give me the correct result so I change it.
        // The original implementation will change the position when we change the size of GameViewWindow
        // But seems the way that author use to calculate position of game window is not correct.
        // He uses the absolute coordinate(getCursorPos) but I think we should use the relative coordinate.
        MouseListener.getInstance().setGameViewportPos(new Vector2f(windowPos.x, windowPos.y));
        MouseListener.getInstance().setGameViewportSize(new Vector2f(windowSize.x, windowSize.y));

        ImGui.end();
    }
    public boolean getWantCaptureMouse() {
        return hover;
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

        // This shit seems has problem
        return new ImVec2(viewportX + ImGui.getCursorScreenPosX(),
                          viewportY + ImGui.getCursorScreenPosY());
    }
}

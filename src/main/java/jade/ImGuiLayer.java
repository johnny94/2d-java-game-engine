package jade;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_2;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_3;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_4;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_5;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.glfwGetCurrentContext;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwSetMouseButtonCallback;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;

import editor.GameViewWindow;
import editor.MenuBar;
import editor.PropertiesWindow;
import editor.SceneHierarchyWindow;
import imgui.ImFontAtlas;
import imgui.ImFontConfig;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.ImGuiViewport;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiConfigFlags;
import imgui.flag.ImGuiFreeTypeBuilderFlags;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import imgui.type.ImBoolean;
import renderer.PickingTexture;
import scenes.Scene;

public class ImGuiLayer {
    private long glfwWindowPtr;
    private final ImGuiImplGlfw imGuiGlfw = new ImGuiImplGlfw();
    private final ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();

    private GameViewWindow gameViewWindow;
    private PropertiesWindow propertiesWindow;
    private MenuBar menuBar;
    private SceneHierarchyWindow sceneHierarchyWindow;

    public ImGuiLayer(long glfwWindowPtr, PickingTexture pickingTexture) {
        this.glfwWindowPtr = glfwWindowPtr;
        this.gameViewWindow = new GameViewWindow();
        this.propertiesWindow = new PropertiesWindow(pickingTexture);
        this.menuBar = new MenuBar();
        this.sceneHierarchyWindow = new SceneHierarchyWindow();
    }

    public void initImGui() {
        ImGui.createContext();
        ImGuiIO io = ImGui.getIO();
        io.addConfigFlags(ImGuiConfigFlags.ViewportsEnable);
        io.setConfigFlags(ImGuiConfigFlags.DockingEnable);

        // Set fonts
        ImFontAtlas imFontAtlas = io.getFonts();
        ImFontConfig imFontConfig = new ImFontConfig();

        imFontConfig.setPixelSnapH(true);
        io.getFonts().addFontFromFileTTF("assets/fonts/NotoSans-Regular.ttf", 24,
                                         imFontConfig, imFontAtlas.getGlyphRangesDefault());

        imFontConfig.destroy();

        imFontAtlas.setFlags(ImGuiFreeTypeBuilderFlags.LightHinting);
        imFontAtlas.build();

        /*
        *
        * Handle user input
        *
        */
        glfwSetMouseButtonCallback(glfwWindowPtr, (w, button, action, mods) -> {
            final boolean[] mouseDown = new boolean[5];

            mouseDown[0] = button == GLFW_MOUSE_BUTTON_1 && action != GLFW_RELEASE;
            mouseDown[1] = button == GLFW_MOUSE_BUTTON_2 && action != GLFW_RELEASE;
            mouseDown[2] = button == GLFW_MOUSE_BUTTON_3 && action != GLFW_RELEASE;
            mouseDown[3] = button == GLFW_MOUSE_BUTTON_4 && action != GLFW_RELEASE;
            mouseDown[4] = button == GLFW_MOUSE_BUTTON_5 && action != GLFW_RELEASE;

            io.setMouseDown(mouseDown);

            if (!io.getWantCaptureMouse() && mouseDown[1]) {
                ImGui.setWindowFocus(null);
            }

            if (!io.getWantCaptureMouse() || gameViewWindow.getWantCaptureMouse()) {
                MouseListener.getInstance().mouseButtonCallback(w, button, action, mods);
            }
        });

        imGuiGlfw.init(glfwWindowPtr, true);
        imGuiGl3.init("#version 330 core");
    }

    public PropertiesWindow getPropertiesWindow() {
        return propertiesWindow;
    }

    public void update(double deltaTime, Scene currentScene) {
        startFrame(deltaTime);

        setupDockSpace();
        currentScene.imGui();
        gameViewWindow.imgui();

        propertiesWindow.update(deltaTime, currentScene);
        propertiesWindow.imGui();
        sceneHierarchyWindow.imGui();

        endFrame();
    }

    private void startFrame(double deltaTime) {
        imGuiGlfw.newFrame();
        ImGui.newFrame();
    }

    private void endFrame() {
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glViewport(0, 0, Window.get().getWidth(), Window.get().getHeight());
        glClearColor(0,0,0, 1);
        glClear(GL_COLOR_BUFFER_BIT);

        ImGui.render();
        imGuiGl3.renderDrawData(ImGui.getDrawData());

        if (ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
            final long backupWindowPtr = glfwGetCurrentContext();
            ImGui.updatePlatformWindows();
            ImGui.renderPlatformWindowsDefault();
            glfwMakeContextCurrent(backupWindowPtr);
        }
    }

    public void dispose() {
        imGuiGl3.dispose();
        imGuiGlfw.dispose();
        ImGui.destroyContext();
    }

    private void setupDockSpace() {
        int windowFlags = ImGuiWindowFlags.MenuBar | ImGuiWindowFlags.NoDocking;
        ImGuiViewport imGuiViewport = ImGui.getMainViewport();
        ImGui.setNextWindowPos(imGuiViewport.getWorkPosX(), imGuiViewport.getWorkPosY());
        ImGui.setNextWindowSize(imGuiViewport.getWorkSizeX(), imGuiViewport.getWorkSizeY());
        ImGui.setNextWindowViewport(imGuiViewport.getID());

        ImGui.setNextWindowPos(0.0f, 0.0f, ImGuiCond.Always);
        ImGui.setNextWindowSize(Window.get().getWidth(), Window.get().getHeight());
        ImGui.pushStyleVar(ImGuiStyleVar.WindowRounding, 0.0f);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowBorderSize, 0.0f);

        windowFlags |= ImGuiWindowFlags.NoTitleBar | ImGuiWindowFlags.NoCollapse |
                       ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoMove |
                       ImGuiWindowFlags.NoBringToFrontOnFocus | ImGuiWindowFlags.NoNavFocus;

        ImGui.begin("Dockspace demo", new ImBoolean(true), windowFlags);
        ImGui.popStyleVar(2);

        ImGui.dockSpace(ImGui.getID("Docksapce"));

        menuBar.imGui();
        ImGui.end();
    }
}

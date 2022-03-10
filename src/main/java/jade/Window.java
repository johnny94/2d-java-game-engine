package jade;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MAJOR;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MINOR;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_MAXIMIZED;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_CORE_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_FORWARD_COMPAT;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwGetCurrentContext;
import static org.lwjgl.glfw.GLFW.glfwGetTime;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetMouseButtonCallback;
import static org.lwjgl.glfw.GLFW.glfwSetScrollCallback;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_RENDERER;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_VERSION;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glGetString;
import static org.lwjgl.system.MemoryUtil.NULL;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import imgui.ImFontAtlas;
import imgui.ImFontConfig;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiConfigFlags;
import imgui.flag.ImGuiFreeTypeBuilderFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;

public final class Window {
    private static final MouseListener mouseListener = MouseListener.getInstance();
    private static final KeyListener keyListener = KeyListener.getInstance();

    private long glfwWindowPtr;

    private final int width;
    private final int height;
    private final String title;

    private Scene currentScene;

    // ImGui
    private final ImGuiLayer imGuiLayer;
    private final ImGuiImplGlfw imGuiGlfw = new ImGuiImplGlfw();
    private final ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();
    private static final String GLSL_VERSION = "#version 330 core";

    private Window(ImGuiLayer imGuiLayer) {
        this.width = 1920;
        this.height = 1080;
        this.title = "Mario";
        this.imGuiLayer = imGuiLayer;
    }

    private static final class WindowHolder {
        static final Window window = new Window(new ImGuiLayer());
    }

    public Scene getCurrentScene() {
        return currentScene;
    }

    public static Window get() {
        return WindowHolder.window;
    }

    public void run() {
        System.out.println("Hello LWJGL " + Version.getVersion() + '!');

        init();
        loop();

        dispose();
    }

    public void changeScene(int index) {
        switch(index) {
            case 0:
                currentScene = new LevelEditorScene();
                break;
            case 1:
                currentScene = new LevelScene();
                break;
            default:
                assert false : "Unknown scene '" + index + "'";
        }

        currentScene.init();
        currentScene.start();
    }

    private void init() {
        initWindow();
        initImGui();
        imGuiGlfw.init(glfwWindowPtr, true);
        imGuiGl3.init(GLSL_VERSION);
    }

    private void initWindow() {
        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        glfwDefaultWindowHints();

        decideGlGlslVersions();

        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);

        glfwWindowPtr = glfwCreateWindow(this.width, this.height, this.title, NULL, NULL);
        if (glfwWindowPtr == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        // Set callbacks
        glfwSetCursorPosCallback(glfwWindowPtr, mouseListener::mousePosCallback);
        glfwSetMouseButtonCallback(glfwWindowPtr, mouseListener::mouseButtonCallback);
        glfwSetScrollCallback(glfwWindowPtr, mouseListener::mouseScrollCallback);
        glfwSetKeyCallback(glfwWindowPtr, keyListener::keyCallback);

        // Make the OpenGL context current
        glfwMakeContextCurrent(glfwWindowPtr);
        // Enable v-sync
        glfwSwapInterval(1);

        glfwShowWindow(glfwWindowPtr);

        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        System.err.println("OpenGL version: " + glGetString(GL_VERSION));
        System.err.println("Device: " + glGetString(GL_RENDERER));

        get().changeScene(0);
    }

    private void decideGlGlslVersions() {
        // We will use "#version 330 core" in the shader so the version is 3.3
        // Ref https://en.wikipedia.org/wiki/OpenGL_Shading_Language
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE); // Required for Mac
    }

    private void initImGui() {
        ImGui.createContext();
        ImGuiIO io = ImGui.getIO();
        io.addConfigFlags(ImGuiConfigFlags.ViewportsEnable);

        // Set fonts
        ImFontAtlas imFontAtlas = io.getFonts();
        ImFontConfig imFontConfig = new ImFontConfig();

        imFontConfig.setPixelSnapH(true);
        io.getFonts().addFontFromFileTTF("assets/fonts/NotoSans-Regular.ttf", 32,
                                         imFontConfig, imFontAtlas.getGlyphRangesDefault());

        imFontConfig.destroy();

        imFontAtlas.setFlags(ImGuiFreeTypeBuilderFlags.LightHinting);
        imFontAtlas.build();
    }

    private void loop() {
        double beginTime = glfwGetTime();
        double endTime;
        double dt = -1.0;

        currentScene.load();
        while(!glfwWindowShouldClose(glfwWindowPtr)) {
            startFrame();

            imGuiLayer.imGui();

            // NOTE: Maybe It will be better to merge update and sceneImGui method?
            if (dt > 0) {
                currentScene.update(dt);
            }
            currentScene.sceneImGui();

            endFrame();

            endTime = glfwGetTime();
            dt = endTime - beginTime;
            beginTime = endTime;
        }

        currentScene.saveExit();
    }

    private void startFrame() {
        glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT);

        // ImGui Start frame
        imGuiGlfw.newFrame();
        ImGui.newFrame();
    }

    private void endFrame() {
        ImGui.render();
        imGuiGl3.renderDrawData(ImGui.getDrawData());

        if (ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
            final long backupWindowPtr = glfwGetCurrentContext();
            ImGui.updatePlatformWindows();
            ImGui.renderPlatformWindowsDefault();
            glfwMakeContextCurrent(backupWindowPtr);
        }

        glfwSwapBuffers(glfwWindowPtr);
        glfwPollEvents();
    }

    private void dispose() {
        imGuiGl3.dispose();
        imGuiGlfw.dispose();
        disposeImGui();
        disposeWindow();
    }

    private void disposeWindow() {
        // Free the memory
        glfwFreeCallbacks(glfwWindowPtr);
        glfwDestroyWindow(glfwWindowPtr);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void disposeImGui() {
        ImGui.destroyContext();
    }
}

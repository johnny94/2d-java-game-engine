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
import static org.lwjgl.glfw.GLFW.glfwGetTime;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetMouseButtonCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.openal.ALC10.ALC_DEFAULT_DEVICE_SPECIFIER;
import static org.lwjgl.openal.ALC10.alcCloseDevice;
import static org.lwjgl.openal.ALC10.alcCreateContext;
import static org.lwjgl.openal.ALC10.alcDestroyContext;
import static org.lwjgl.openal.ALC10.alcGetString;
import static org.lwjgl.openal.ALC10.alcMakeContextCurrent;
import static org.lwjgl.openal.ALC10.alcOpenDevice;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_RENDERER;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_VERSION;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glGetString;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.system.MemoryUtil.NULL;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.ALCapabilities;
import org.lwjgl.opengl.GL;

import observers.EventSystem;
import observers.Observer;
import observers.events.Event;
import renderer.DebugDraw;
import renderer.Framebuffer;
import renderer.PickingTexture;
import renderer.Renderer;
import renderer.Shader;
import scenes.LevelEditorSceneInitializer;
import scenes.Scene;
import scenes.SceneInitializer;
import util.AssetPool;

public final class Window implements Observer {
    private static final MouseListener mouseListener = MouseListener.getInstance();
    private static final KeyListener keyListener = KeyListener.getInstance();

    private long glfwWindowPtr;

    private int width;
    private int height;
    private final String title;

    private Scene currentScene;

    private Framebuffer framebuffer;
    private PickingTexture pickingTexture;

    private boolean runtimePlaying;

    // ImGui
    private ImGuiLayer imGuiLayer;

    // Audio
    private long audioContext;
    private long audioDevice;

    private Window() {
        this.width = 1920;
        this.height = 1080;
        this.title = "Mario";
        EventSystem.addObserver(this);
    }

    @Override
    public void onNotify(GameObject object, Event event) {
        switch (event.type) {
            case GameEngineStartPlay:
                this.runtimePlaying = true;
                currentScene.save();
                changeScene(new LevelEditorSceneInitializer());
                break;
            case GameEngineStopPlay:
                this.runtimePlaying = false;
                changeScene(new LevelEditorSceneInitializer());
                break;
            case LoadLevel:
                changeScene(new LevelEditorSceneInitializer());
                break;
            case SaveLevel:
                currentScene.save();
                break;
        }
    }

    private static final class WindowHolder {
        static final Window window = new Window();
    }

    public Scene getCurrentScene() {
        return currentScene;
    }

    public static Window get() {
        return WindowHolder.window;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return height;
    }

    public float getTargetAspectRatio() {
        return 16.0f / 9.0f;
    }

    public Framebuffer getFramebuffer() {
        return framebuffer;
    }

    public void run() {
        System.out.println("Hello LWJGL " + Version.getVersion() + '!');

        init();
        loop();

        dispose();
    }

    public void changeScene(SceneInitializer sceneInitializer) {
        if (currentScene != null) {
            currentScene.destroy();
        }

        imGuiLayer.getPropertiesWindow().setActiveGameObject(null);
        currentScene = new Scene(sceneInitializer);
        currentScene.load();
        currentScene.init();
        currentScene.start();
    }

    public ImGuiLayer getImGuiLayer() {
        return this.imGuiLayer;
    }

    private void init() {
        initWindow();
        this.imGuiLayer = new ImGuiLayer(glfwWindowPtr, pickingTexture);
        imGuiLayer.initImGui();
        changeScene(new LevelEditorSceneInitializer());
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
        glfwSetKeyCallback(glfwWindowPtr, keyListener::keyCallback);
        glfwSetWindowSizeCallback(glfwWindowPtr, (window, width, height) -> {
            this.width = width;
            this.height = height;
        });

        // Make the OpenGL context current
        glfwMakeContextCurrent(glfwWindowPtr);
        // Enable v-sync
        glfwSwapInterval(1);

        glfwShowWindow(glfwWindowPtr);

        // Initialize the audio device
        String defaultDeviceName = alcGetString(0, ALC_DEFAULT_DEVICE_SPECIFIER);
        audioDevice = alcOpenDevice(defaultDeviceName);

        int[] attributes = {0};
        audioContext = alcCreateContext(audioDevice, attributes);
        alcMakeContextCurrent(audioContext);

        ALCCapabilities alcCapabilities = ALC.createCapabilities(audioDevice);
        ALCapabilities alCapabilities = AL.createCapabilities(alcCapabilities);

        if (!alCapabilities.OpenAL10) {
            assert false : "Audio library not supported.";
        }


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

        framebuffer = new Framebuffer(3840, 2160);
        pickingTexture = new PickingTexture(3840, 2160);
        glViewport(0, 0, 3840, 2160);
    }

    private void decideGlGlslVersions() {
        // We will use "#version 330 core" in the shader so the version is 3.3
        // Ref https://en.wikipedia.org/wiki/OpenGL_Shading_Language
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE); // Required for Mac
    }

    private void loop() {
        float beginTime = (float)glfwGetTime();
        float endTime;
        float dt = -1.0f;

        Shader defaultShader = AssetPool.loadShader("assets/shaders/default.glsl");
        Shader pickingShader = AssetPool.loadShader("assets/shaders/pickingShader.glsl");

        while(!glfwWindowShouldClose(glfwWindowPtr)) {

            // Render pass 1. Render to picking texture
            glDisable(GL_BLEND);
            pickingTexture.enableWriting();
            glViewport(0, 0, 3840, 2160);
            glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            Renderer.bindShader(pickingShader);
            currentScene.render();

            pickingTexture.disableWriting();
            glEnable(GL_BLEND);

            // Render pass 2. Render to framebuffer
            framebuffer.bind();
            glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT);

            // NOTE: Maybe It will be better to merge update and sceneImGui method?
            if (dt > 0) {
                DebugDraw.draw();
                Renderer.bindShader(defaultShader);
                if (runtimePlaying) {
                    currentScene.update(dt);
                } else {
                    currentScene.editorUpdate(dt);
                }

                currentScene.render();
            }
            framebuffer.unBind();

            imGuiLayer.update(dt, currentScene);

            glfwSwapBuffers(glfwWindowPtr);
            MouseListener.getInstance().endFrame();

            glfwPollEvents();

            endTime = (float)glfwGetTime();
            dt = endTime - beginTime;
            beginTime = endTime;
        }
    }

    private void dispose() {
        imGuiLayer.dispose();
        disposeAudio();
        disposeWindow();
    }

    private void disposeAudio() {
        alcDestroyContext(audioContext);
        alcCloseDevice(audioDevice);
    }

    private void disposeWindow() {
        // Free the memory
        glfwFreeCallbacks(glfwWindowPtr);
        glfwDestroyWindow(glfwWindowPtr);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }
}

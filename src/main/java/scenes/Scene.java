package scenes;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import components.Component;
import imgui.ImGui;
import jade.Camera;
import jade.GameObject;
import renderer.Renderer;
import util.GsonUtils;

public abstract class Scene {
    private boolean isRunning;

    protected Renderer renderer = new Renderer();
    protected Camera camera;
    protected final List<GameObject> gameObjects = new ArrayList<>();
    protected GameObject activeGameObject = null;
    protected boolean levelLoaded;

    public void init() { }

    public void start() {
        for (GameObject g : gameObjects) {
            g.start();
            this.renderer.add(g);
        }
        isRunning = true;
    }

    public abstract void update(double deltaTime);
    public abstract void render();

    public void addGameObject(GameObject gameObject) {
        // If we add a game object in a running scene, we start it immediately.
        // Otherwise, just add it.
        if (!isRunning) {
            gameObjects.add(gameObject);
        } else {
            gameObjects.add(gameObject);
            gameObject.start();
            this.renderer.add(gameObject);
        }
    }

    public Camera getCamera() {
        return this.camera;
    }

    // Note: I think this should be invoked by update
    public void sceneImGui() {
        if (activeGameObject != null) {
            ImGui.begin("Inspector");
            activeGameObject.imGui();
            ImGui.end();
        }

        imGui();
    }

    public void imGui() {

    }

    public void saveExit() {
        try(FileWriter writer = new FileWriter("level.json")) {
            writer.write(GsonUtils.DEFAULT_GSON.toJson(this.gameObjects));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void load() {
        Path p = Paths.get("level.json");
        if (!p.toFile().exists()) {
            System.out.println("Not found level.json. Create empty scene.");
            return;
        }

        String level = "";
        try {
            level = new String(Files.readAllBytes(Paths.get("level.json")));
        } catch (IOException e) {
            throw new RuntimeException("Failed to open level.json", e);
        }

        if (!level.isEmpty()) {
            int maxGameObjectId = Integer.MIN_VALUE;
            int maxComponentId = Integer.MIN_VALUE;

            GameObject[] objs = GsonUtils.DEFAULT_GSON.fromJson(level, GameObject[].class);
            this.gameObjects.clear();
            for (GameObject obj : objs) {
                addGameObject(obj);

                for (Component c : obj.getComponents()) {
                    if (c.getUid() > maxComponentId) {
                        maxComponentId = c.getUid();
                    }
                }

                if (obj.getUid() > maxGameObjectId) {
                    maxGameObjectId = obj.getUid();
                }
            }

            maxGameObjectId++;
            maxComponentId++;

            GameObject.init(maxGameObjectId);
            Component.init(maxComponentId);
            levelLoaded = true;
        }
    }
}

package scenes;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.joml.Vector2f;

import components.Component;
import components.Transform;
import jade.Camera;
import jade.GameObject;
import physics2d.Physics2D;
import renderer.Renderer;
import util.GsonUtils;

public class Scene {
    private boolean isRunning;

    private final Renderer renderer;
    private Camera camera;
    private final List<GameObject> gameObjects;
    private final Physics2D physics2D;

    private final SceneInitializer sceneInitializer;

    public Scene(SceneInitializer sceneInitializer) {
        this.sceneInitializer = sceneInitializer;
        this.renderer = new Renderer();
        this.physics2D = new Physics2D();
        this.gameObjects = new ArrayList<>();
    }

    public void init() {
        camera = new Camera(new Vector2f(0, 0));
        sceneInitializer.loadResource(this);
        sceneInitializer.init(this);
    }

    public void start() {
        for (int i = 0; i < gameObjects.size(); i++) {
            GameObject g =gameObjects.get(i);
            g.start();
            this.renderer.add(g);
            this.physics2D.add(g);
        }
        isRunning = true;
    }

    public void editorUpdate(float deltaTime) {
        this.camera.adjustProjection();

        for (int i = 0; i < gameObjects.size(); i++) {
            GameObject go = gameObjects.get(i);
            go.editorUpdate(deltaTime);

            if (go.isDead()) {
                gameObjects.remove(i);
                this.renderer.destroyGameObject(go);
                this.physics2D.destroyGameObject(go);
                i--;
            }
        }
    }

    public void update(float deltaTime) {
        this.camera.adjustProjection();
        this.physics2D.update(deltaTime);

        for (int i = 0; i < gameObjects.size(); i++) {
            GameObject go = gameObjects.get(i);
            go.update(deltaTime);

            if (go.isDead()) {
                gameObjects.remove(i);
                this.renderer.destroyGameObject(go);
                this.physics2D.destroyGameObject(go);
                i--;
            }
        }
    }

    public void render() {
        this.renderer.render();
    }

    public GameObject createGameObject(String name) {
        // TODO: Can we put this in the GameObject constructor?
        GameObject gameObject = new GameObject(name);
        gameObject.addComponent(new Transform());
        gameObject.transform = gameObject.getComponent(Transform.class).get();

        return gameObject;
    }

    public void addGameObject(GameObject gameObject) {
        // If we add a game object in a running scene, we start it immediately.
        // Otherwise, just add it.
        if (!isRunning) {
            gameObjects.add(gameObject);
        } else {
            gameObjects.add(gameObject);
            gameObject.start();
            this.renderer.add(gameObject);
            this.physics2D.add(gameObject);
        }
    }

    public List<GameObject> getGameObjects() {
        return this.gameObjects;
    }

    public Optional<GameObject> getGameObject(int uid) {
        return gameObjects.stream()
                          .filter(gameObject -> gameObject.getUid() == uid)
                          .findFirst();
    }

    public Camera getCamera() {
        return this.camera;
    }

    public Physics2D getPhysics() {
        return physics2D;
    }

    public void imGui() {
        sceneInitializer.imGui();
    }

    public void save() {
        try(FileWriter writer = new FileWriter("level.json")) {
            List<GameObject> target = this.gameObjects.stream().filter(GameObject::isDoSerialization)
                                                      .collect(Collectors.toList());
            writer.write(GsonUtils.DEFAULT_GSON.toJson(target));
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
        }
    }

    public void destroy() {
        for(GameObject go : gameObjects) {
            go.destroy();
        }
    }
}

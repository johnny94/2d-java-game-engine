package jade;

import java.util.ArrayList;
import java.util.List;

import renderer.Renderer;

public abstract class Scene {
    private boolean isRunning;

    protected Renderer renderer = new Renderer();
    protected Camera camera;
    protected final List<GameObject> gameObjects = new ArrayList<>();

    public void init() { }

    public void start() {
        for (GameObject g : gameObjects) {
            g.start();
            this.renderer.add(g);
        }
        isRunning = true;
    }

    public abstract void update(double deltaTime);

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
}

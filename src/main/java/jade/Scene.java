package jade;

import java.util.ArrayList;
import java.util.List;

public abstract class Scene {
    protected Camera camera;
    private boolean isRunning;

    protected final List<GameObject> gameObjects = new ArrayList<>();

    public void init() { }

    public void start() {
        for (GameObject g : gameObjects) {
            g.start();
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
        }
    }
}

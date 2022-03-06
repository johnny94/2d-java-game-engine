package jade;

import org.joml.Vector2f;

import components.SpriteRenderer;
import util.AssetPool;

public class LevelEditorScene extends Scene {

    @Override
    public void init() {
        this.camera = new Camera(new Vector2f(-250, 0));

        GameObject object1 = new GameObject("Obj1",
                                            new Transform(new Vector2f(100, 100), new Vector2f(256, 256)));
        object1.addComponent(new SpriteRenderer(AssetPool.loadTexture("assets/images/awesomeface.png")));

        addGameObject(object1);

        loadResource();
    }

    private void loadResource() {
        AssetPool.loadShader("assets/shaders/default.glsl");
    }

    @Override
    public void update(double deltaTime) {
        for (GameObject g : gameObjects) {
            g.update(deltaTime);
        }

        this.renderer.render();
    }
}

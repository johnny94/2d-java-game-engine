package jade;

import org.joml.Vector2f;
import org.joml.Vector4f;

import components.Sprite;
import components.SpriteRenderer;
import components.SpriteSheet;
import util.AssetPool;

public class LevelEditorScene extends Scene {

    private GameObject object1;

    @Override
    public void init() {
        loadResource();
        this.camera = new Camera(new Vector2f(-250, 0));
        if (levelLoaded) {
            return;
        }

        SpriteSheet spriteSheet = AssetPool.getSpriteSheet("assets/images/spritesheet.png");

        object1 = new GameObject("Obj1",
                                 new Transform(new Vector2f(100, 100), new Vector2f(256, 256)),
                                 -1);
        object1.addComponent(new SpriteRenderer(new Vector4f(1.0f, 0.0f, 0.0f, 1.0f)));
        addGameObject(object1);
        this.activeGameObject = object1;

        GameObject object2 = new GameObject("Obj2",
                                 new Transform(new Vector2f(100, 200), new Vector2f(256, 256)),
                                            -2);
        object2.addComponent(new SpriteRenderer(new Sprite(AssetPool.loadTexture("assets/images/green.png"))));
        addGameObject(object2);
    }

    private void loadResource() {
        AssetPool.loadShader("assets/shaders/default.glsl");
        AssetPool.loadSpriteSheet("assets/images/spritesheet.png",
                                  new SpriteSheet(AssetPool.loadTexture("assets/images/spritesheet.png"),
                                                  16, 16, 26, 0));
    }

    @Override
    public void update(double deltaTime) {
        // I think this should be moved to super class
        for (GameObject g : gameObjects) {
            g.update(deltaTime);
        }

        this.renderer.render();
    }
}

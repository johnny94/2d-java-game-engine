package jade;

import org.joml.Vector2f;

import components.SpriteRenderer;
import components.SpriteSheet;
import util.AssetPool;

public class LevelEditorScene extends Scene {

    private GameObject object1;

    @Override
    public void init() {
        loadResource();

        this.camera = new Camera(new Vector2f(-250, 0));

        SpriteSheet spriteSheet = AssetPool.getSpriteSheet("assets/images/spritesheet.png");

        object1 = new GameObject("Obj1",
                                 new Transform(new Vector2f(100, 100), new Vector2f(256, 256)));
        object1.addComponent(new SpriteRenderer(spriteSheet.getSprite(0)));
        addGameObject(object1);
    }

    private void loadResource() {
        AssetPool.loadShader("assets/shaders/default.glsl");
        AssetPool.loadSpriteSheet("assets/images/spritesheet.png",
                                  new SpriteSheet(AssetPool.loadTexture("assets/images/spritesheet.png"),
                                                  16, 16, 26, 0));
    }

    @Override
    public void update(double deltaTime) {
        object1.transform.position.x += 10 * deltaTime;

        // I think this should be moved to super class
        for (GameObject g : gameObjects) {
            g.update(deltaTime);
        }

        this.renderer.render();
    }
}

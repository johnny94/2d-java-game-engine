package scenes;

import components.GameCamera;
import components.SpriteRenderer;
import components.SpriteSheet;
import components.animation.StateMachine;
import jade.GameObject;
import util.AssetPool;

public class LevelSceneInitializer extends SceneInitializer {
    @Override
    public void init(Scene scene) {
        GameObject camera = scene.createGameObject("Game Camera");
        camera.addComponent(new GameCamera(scene.getCamera()));
        camera.start();
        scene.addGameObject(camera);
    }

    @Override
    public void loadResource(Scene scene) {
        AssetPool.loadShader("assets/shaders/default.glsl");

        AssetPool.loadSpriteSheet("assets/images/spritesheets/decorationsAndBlocks.png",
                                  new SpriteSheet(AssetPool.loadTexture("assets/images/spritesheets/decorationsAndBlocks.png"),
                                                  16, 16, 81, 0));
        AssetPool.loadSpriteSheet("assets/images/spritesheets/spritesheet.png",
                                  new SpriteSheet(AssetPool.loadTexture("assets/images/spritesheets/spritesheet.png"),
                                                  16, 16, 26, 0));
        AssetPool.loadSpriteSheet("assets/images/spritesheets/turtle.png",
                                  new SpriteSheet(AssetPool.loadTexture("assets/images/spritesheets/turtle.png"),
                                                  16, 24, 4, 0));
        AssetPool.loadSpriteSheet("assets/images/spritesheets/bigSpritesheet.png",
                                  new SpriteSheet(AssetPool.loadTexture("assets/images/spritesheets/bigSpritesheet.png"),
                                                  16, 32, 42, 0));
        AssetPool.loadSpriteSheet("assets/images/spritesheets/pipes.png",
                                  new SpriteSheet(AssetPool.loadTexture("assets/images/spritesheets/pipes.png"),
                                                  32, 32, 4, 0));
        AssetPool.loadSpriteSheet("assets/images/spritesheets/items.png",
                                  new SpriteSheet(AssetPool.loadTexture("assets/images/spritesheets/items.png"),
                                                  16, 16, 43, 0));
        AssetPool.loadSpriteSheet("assets/images/gizmos.png",
                                  new SpriteSheet(AssetPool.loadTexture("assets/images/gizmos.png"),
                                                  24, 48, 3, 0));
        AssetPool.loadTexture("assets/images/green.png");

        AssetPool.addSound("assets/sounds/main-theme-overworld.ogg", true);
        AssetPool.addSound("assets/sounds/flagpole.ogg", false);
        AssetPool.addSound("assets/sounds/break_block.ogg", false);
        AssetPool.addSound("assets/sounds/bump.ogg", false);
        AssetPool.addSound("assets/sounds/coin.ogg", false);
        AssetPool.addSound("assets/sounds/gameover.ogg", false);
        AssetPool.addSound("assets/sounds/jump-small.ogg", false);
        AssetPool.addSound("assets/sounds/mario_die.ogg", false);
        AssetPool.addSound("assets/sounds/pipe.ogg", false);
        AssetPool.addSound("assets/sounds/powerup.ogg", false);
        AssetPool.addSound("assets/sounds/powerup_appears.ogg", false);
        AssetPool.addSound("assets/sounds/stage_clear.ogg", false);
        AssetPool.addSound("assets/sounds/stomp.ogg", false);
        AssetPool.addSound("assets/sounds/kick.ogg", false);
        AssetPool.addSound("assets/sounds/invincible.ogg", false);

        // Note: I think this should be done when deserialize the Texture.
        for (GameObject go : scene.getGameObjects()) {
            go.getComponent(SpriteRenderer.class)
              .ifPresent(spr -> {
                  if (spr.getTexture().isPresent()) {
                      String texturePath = spr.getTexture().get().getFilepath();
                      spr.setTexture(AssetPool.loadTexture(texturePath));
                  }
              });

            go.getComponent(StateMachine.class).ifPresent(StateMachine::refreshTexture);
        }
    }

    @Override
    public void imGui() {
        // empty
    }
}

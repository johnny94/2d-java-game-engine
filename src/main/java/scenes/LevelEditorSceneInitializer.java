package scenes;

import java.nio.file.Path;

import org.joml.Vector2f;

import components.EditorCamera;
import components.GizmoManager;
import components.GridLines;
import components.MouseControls;
import components.Sprite;
import components.SpriteRenderer;
import components.SpriteSheet;
import components.animation.StateMachine;
import imgui.ImGui;
import imgui.ImVec2;
import jade.GameObject;
import jade.Prefabs;
import jade.Sound;
import util.AssetPool;
import util.Settings;

public class LevelEditorSceneInitializer extends SceneInitializer {
    private SpriteSheet spriteSheet;
    private GameObject levelEditorObject;

    @Override
    public void init(Scene scene) {
        spriteSheet = AssetPool.getSpriteSheet("assets/images/spritesheets/decorationsAndBlocks.png");
        SpriteSheet gizmos = AssetPool.getSpriteSheet("assets/images/gizmos.png");

        levelEditorObject = scene.createGameObject("LevelEditor");
        levelEditorObject.setNoSerialize();
        levelEditorObject.addComponent(new MouseControls());
        levelEditorObject.addComponent(new GridLines());
        levelEditorObject.addComponent(new EditorCamera(scene.getCamera()));
        levelEditorObject.addComponent(new GizmoManager(gizmos));

        scene.addGameObject(levelEditorObject);
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
        ImGui.begin("Level Editor Stuff");
        levelEditorObject.imGui();
        ImGui.end();

        ImGui.begin("Objects");

        if (ImGui.beginTabBar("WindowTabBar")) {

            blockTab();
            prefabTab();
            soundTab();

            ImGui.endTabBar();
        }
        ImGui.end();
    }

    private void blockTab() {
        if (ImGui.beginTabItem("Blocks")) {
            ImVec2 windowPos = ImGui.getWindowPos();
            ImVec2 windowSize = ImGui.getWindowSize();
            ImVec2 itemSpacing = ImGui.getStyle().getItemSpacing();

            float windowRightX = windowPos.x + windowSize.x;
            for (int i = 0; i < spriteSheet.numSprite(); i++) {
                Sprite sprite = spriteSheet.getSprite(i);
                float widgetWidth = sprite.getWidth() * 4;
                float widgetHeight = sprite.getHeight() * 4;
                int texId = sprite.getTextureId();
                Vector2f[] texCoords = sprite.getTexCoords();

                ImGui.pushID(i);
                if (ImGui.imageButton(texId, widgetWidth, widgetHeight,
                                      texCoords[2].x, texCoords[0].y,
                                      texCoords[0].x, texCoords[2].y)) {
                    GameObject go = Prefabs.generateSpriteObject(sprite, Settings.GRID_WIDTH, Settings.GRID_HEIGHT);
                    levelEditorObject.getComponent(MouseControls.class)
                                     .ifPresent(m -> m.pickUpObject(go));
                }
                ImGui.popID();

                ImVec2 lastButtonPos = ImGui.getItemRectMax();
                float nextButtonRightX = lastButtonPos.x + itemSpacing.x + widgetWidth;
                if (i + 1 < spriteSheet.numSprite() && nextButtonRightX < windowRightX) {
                    ImGui.sameLine();
                }
            }

            ImGui.endTabItem();
        }
    }

    private void prefabTab() {
        if (ImGui.beginTabItem("Prefabs")) {
            // Mario
            SpriteSheet spriteSheet = AssetPool.getSpriteSheet("assets/images/spritesheets/spritesheet.png");
            Sprite sprite = spriteSheet.getSprite(0);
            float widgetWidth = sprite.getWidth() * 4;
            float widgetHeight = sprite.getHeight() * 4;
            int texId = sprite.getTextureId();
            Vector2f[] texCoords = sprite.getTexCoords();
            if (ImGui.imageButton(texId, widgetWidth, widgetHeight,
                                  texCoords[2].x, texCoords[0].y,
                                  texCoords[0].x, texCoords[2].y)) {
                GameObject go = Prefabs.generateMario();
                levelEditorObject.getComponent(MouseControls.class)
                                 .ifPresent(m -> m.pickUpObject(go));
            }
            ImGui.sameLine();

            // Question Block
            spriteSheet = AssetPool.getSpriteSheet("assets/images/spritesheets/items.png");
            sprite = spriteSheet.getSprite(0);
            texId = sprite.getTextureId();
            texCoords = sprite.getTexCoords();
            if (ImGui.imageButton(texId, widgetWidth, widgetHeight,
                                  texCoords[2].x, texCoords[0].y,
                                  texCoords[0].x, texCoords[2].y)) {
                GameObject go = Prefabs.generateQuestionBlock();
                levelEditorObject.getComponent(MouseControls.class)
                                 .ifPresent(m -> m.pickUpObject(go));
            }
            ImGui.sameLine();

            ImGui.endTabItem();
        }
    }

    private void soundTab() {
        if (ImGui.beginTabItem("Sounds")) {
            for (Sound sound : AssetPool.getAllSounds()) {
                Path path = sound.getFilepath();
                if (ImGui.button(path.getFileName().toString())) {
                    if (!sound.isPlaying()) {
                        sound.play();
                    } else {
                      sound.stop();
                    }
                }

                if (ImGui.getContentRegionAvailX() > 100) {
                    ImGui.sameLine();
                }
            }

            ImGui.endTabItem();
        }
    }
}
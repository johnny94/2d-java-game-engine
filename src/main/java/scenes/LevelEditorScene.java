package scenes;

import org.joml.Vector2f;
import org.joml.Vector3f;

import components.GridLines;
import components.MouseControls;
import components.Sprite;
import components.SpriteRenderer;
import components.SpriteSheet;
import imgui.ImGui;
import imgui.ImVec2;
import jade.Camera;
import jade.GameObject;
import jade.Prefabs;
import jade.Transform;
import renderer.DebugDraw;
import util.AssetPool;
import util.Settings;

public class LevelEditorScene extends Scene {

    private GameObject object1;
    private SpriteSheet spriteSheet;

    // Special Object that will not be added to and gameObject list (we don't want this to be serialized)
    // Note: Maybe we can find a way to organize this kind of special object.
    private GameObject levelEditorObject = new GameObject("LevelEditor",
                                                          new Transform(new Vector2f(), new Vector2f()), 0);

    @Override
    public void init() {
        levelEditorObject.addComponent(new MouseControls());
        levelEditorObject.addComponent(new GridLines());

        loadResource();
        this.camera = new Camera(new Vector2f(0, 0));
        spriteSheet = AssetPool.getSpriteSheet("assets/images/spritesheets/decorationsAndBlocks.png");
        if (levelLoaded) {
            if (!gameObjects.isEmpty()) {
                this.activeGameObject = this.gameObjects.get(0);
            }
            return;
        }

        /*object1 = new GameObject("Obj1",
                                 new Transform(new Vector2f(100, 100), new Vector2f(256, 256)),
                                 -1);
        object1.addComponent(new SpriteRenderer(new Vector4f(1.0f, 0.0f, 0.0f, 1.0f)));
        object1.addComponent(new RigidBody());
        addGameObject(object1);
        this.activeGameObject = object1;

        GameObject object2 = new GameObject("Obj2",
                                 new Transform(new Vector2f(100, 200), new Vector2f(256, 256)),
                                            -2);
        object2.addComponent(new SpriteRenderer(new Sprite(AssetPool.loadTexture("assets/images/green.png"),
                                                           256,  256)));
        addGameObject(object2);*/
    }

    private void loadResource() {
        AssetPool.loadShader("assets/shaders/default.glsl");

        AssetPool.loadSpriteSheet("assets/images/spritesheets/decorationsAndBlocks.png",
                                  new SpriteSheet(AssetPool.loadTexture("assets/images/spritesheets/decorationsAndBlocks.png"),
                                                  16, 16, 81, 0));
        AssetPool.loadTexture("assets/images/green.png");

        // Note: I think this should be done when deserialize the Texture.
        for(GameObject go : gameObjects) {
            go.getComponent(SpriteRenderer.class)
              .ifPresent(spr -> {
                  if (spr.getTexture().isPresent()) {
                      String texturePath = spr.getTexture().get().getFilepath();
                      spr.setTexture(AssetPool.loadTexture(texturePath));
                  }
              });
        }
    }

    @Override
    public void update(double deltaTime) {
        levelEditorObject.update(deltaTime);

        DebugDraw.drawBox(new Vector2f(200, 200), new Vector2f(64, 24), 30.0f,
                          new Vector3f(0,1,0), 1);
        DebugDraw.drawCircle(new Vector2f(300, 300), 64, new Vector3f(1,0,0), 1);

        // I think this should be moved to super class
        for (GameObject g : gameObjects) {
            g.update(deltaTime);
        }

        this.renderer.render();
    }

    @Override
    public void imGui() {
        ImGui.begin("Test Window");

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

        ImGui.end();
    }
}

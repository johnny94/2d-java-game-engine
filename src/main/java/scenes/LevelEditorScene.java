package scenes;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import components.MouseControls;
import components.RigidBody;
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

public class LevelEditorScene extends Scene {

    private GameObject object1;
    private SpriteSheet spriteSheet;

    private MouseControls mouseControls = new MouseControls();

    @Override
    public void init() {
        loadResource();
        this.camera = new Camera(new Vector2f(-250, 0));
        DebugDraw.drawLine(new Vector2f(0, 0), new Vector2f(800, 800),
                           new Vector3f(1, 0, 0), 120);
        spriteSheet = AssetPool.getSpriteSheet("assets/images/spritesheets/decorationsAndBlocks.png");
        if (levelLoaded) {
            this.activeGameObject = this.gameObjects.get(0);
            return;
        }

        object1 = new GameObject("Obj1",
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
        addGameObject(object2);
    }

    private void loadResource() {
        AssetPool.loadShader("assets/shaders/default.glsl");

        AssetPool.loadSpriteSheet("assets/images/spritesheets/decorationsAndBlocks.png",
                                  new SpriteSheet(AssetPool.loadTexture("assets/images/spritesheets/decorationsAndBlocks.png"),
                                                  16, 16, 81, 0));
        AssetPool.loadTexture("assets/images/green.png");
    }

    @Override
    public void update(double deltaTime) {

        // Special component that responsible for drag game objects
        mouseControls.update(deltaTime);

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
                                  texCoords[0].x, texCoords[0].y,
                                  texCoords[2].x, texCoords[2].y)) {
                GameObject go = Prefabs.generateSpriteObject(sprite, widgetWidth, widgetHeight);
                mouseControls.pickUpObject(go);
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

package jade;

import components.Sprite;
import components.SpriteRenderer;

public class Prefabs {
    public static GameObject generateSpriteObject(Sprite sprite, float scaleX, float scaleY) {
        GameObject block = Window.get().getCurrentScene().createGameObject("Sprite_Object_Gen");
        block.transform.scale.x = scaleX;
        block.transform.scale.y = scaleY;
        block.addComponent(new SpriteRenderer(sprite));

        return block;
    }
}

package jade;

import org.joml.Vector2f;

import components.Sprite;
import components.SpriteRenderer;

public class Prefabs {
    public static GameObject generateSpriteObject(Sprite sprite, float scaleX, float scaleY) {
        GameObject go = new GameObject("Sprite_Object_Gen",
                                       new Transform(new Vector2f(), new Vector2f(scaleX, scaleY)),
                                       0);
        go.addComponent(new SpriteRenderer(sprite));

        return go;
    }
}

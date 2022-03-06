package components;

import java.util.Optional;

import org.joml.Vector2f;
import org.joml.Vector4f;

import renderer.Texture;

public class SpriteRenderer extends Component {
    private final Vector4f color;
    private final Sprite sprite;

    public SpriteRenderer(Vector4f color) {
        this(color, new Sprite(null));
    }

    public SpriteRenderer(Sprite sprite) {
        this(new Vector4f(1.0f, 1.0f, 1.0f, 1.0f), sprite);
    }

    public SpriteRenderer(Vector4f color, Sprite sprite) {
        this.color = color;
        this.sprite = sprite;
    }

    @Override
    public void start() {

    }

    @Override
    public void update(double deltaTime) {

    }

    public Vector4f getColor() {
        return this.color;
    }

    public Optional<Texture> getTexture() {
        return sprite.getTexture();
    }

    public Vector2f[] getTextCoords() {
        return sprite.getTexCoords();
    }
}

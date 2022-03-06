package components;

import java.util.Optional;

import org.joml.Vector2f;
import org.joml.Vector4f;

import renderer.Texture;

public class SpriteRenderer extends Component {
    private Vector4f color;
    private Vector2f[] textCoords;
    private Texture texture;

    public SpriteRenderer(Vector4f color) {
        this(color, null);
    }

    public SpriteRenderer(Texture texture) {
        this(new Vector4f(1.0f, 1.0f, 1.0f, 1.0f), texture);
    }

    public SpriteRenderer(Vector4f color, Texture texture) {
        this.color = color;
        this.texture = texture;
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
        return Optional.ofNullable(this.texture);
    }

    public Vector2f[] getTextCoords() {
        Vector2f[] t = {
                new Vector2f(1, 1),
                new Vector2f(1, 0),
                new Vector2f(0, 0),
                new Vector2f(0, 1)
        };

        return t;
    }
}

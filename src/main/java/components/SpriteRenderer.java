package components;

import java.util.Optional;

import org.joml.Vector2f;
import org.joml.Vector4f;

import jade.Transform;
import renderer.Texture;

public class SpriteRenderer extends Component {
    private final Vector4f color;
    private Sprite sprite;

    private Transform lastTransform;
    private boolean isDirty;

    public SpriteRenderer(Vector4f color) {
        this(color, new Sprite(null));
    }

    public SpriteRenderer(Sprite sprite) {
        this(new Vector4f(1.0f, 1.0f, 1.0f, 1.0f), sprite);
    }

    public SpriteRenderer(Vector4f color, Sprite sprite) {
        this.color = color;
        this.sprite = sprite;
        this.isDirty = true;
    }

    @Override
    public void start() {
        this.lastTransform = this.gameObject.transform.copy();
    }

    @Override
    public void update(double deltaTime) {
        if (!this.lastTransform.equals(this.gameObject.transform)) {
            this.gameObject.transform.copy(lastTransform);
            this.isDirty = true;
        }

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

    public boolean isDirty() {
        return this.isDirty;
    }

    public void setClean() {
        this.isDirty = false;
    }

    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
        this.isDirty = true;
    }

    public void setColor(Vector4f color) {
        if (!this.color.equals(color)) {
            this.color.set(color);
            this.isDirty = true;
        }
    }
}

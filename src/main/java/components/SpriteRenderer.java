package components;

import java.util.Optional;

import org.joml.Vector2f;
import org.joml.Vector4f;

import editor.JImGui;
import renderer.Texture;

public class SpriteRenderer extends Component {
    private Vector4f color;
    private Sprite sprite;

    private transient Transform lastTransform;
    private transient boolean isDirty;

    public SpriteRenderer() {
        this(new Vector4f(1.0f, 1.0f, 1.0f, 1.0f), new Sprite());
    }

    public SpriteRenderer(Vector4f color) {
        this(color, new Sprite());
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
    public void update(float deltaTime) {
        if (!this.lastTransform.equals(this.gameObject.transform)) {
            this.gameObject.transform.copy(lastTransform);
            this.isDirty = true;
        }
    }

    @Override
    public void editorUpdate(float deltaTime) {
        if (!this.lastTransform.equals(this.gameObject.transform)) {
            this.gameObject.transform.copy(lastTransform);
            this.isDirty = true;
        }
    }

    @Override
    public void imGui() {
        if (JImGui.colorPicker4("Color Picker", this.color)) {
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

    public void setDirty() {
        isDirty = true;
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

    public void setTexture(Texture texture) {
        this.sprite.setTexture(texture);
    }
}

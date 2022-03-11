package components;

import java.util.Optional;

import org.joml.Vector2f;

import renderer.Texture;

public class Sprite {
    private int width;
    private int height;

    private Texture texture;
    private Vector2f[] texCoords;

    public Sprite() {
        this(null, -1, -1);
    }

    public Sprite(Texture texture, int width, int height) {
        this(texture, width, height, new Vector2f[]{
                new Vector2f(1, 1),
                new Vector2f(1, 0),
                new Vector2f(0, 0),
                new Vector2f(0, 1)
        });
    }

    public Sprite(Texture texture, int width, int height, Vector2f[] texCoord) {
        this.texture = texture;
        this.texCoords = texCoord;
        this.width = width;
        this.height = height;
    }

    public Optional<Texture> getTexture() {
        return Optional.ofNullable(texture);
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    public Vector2f[] getTexCoords() {
        return texCoords;
    }

    public void setTexCoords(Vector2f[] texCoords) {
        this.texCoords = texCoords;
    }

    public int getTextureId() {
        return texture == null ? -1 : this.texture.getTextureId();
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}

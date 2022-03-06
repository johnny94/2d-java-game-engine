package components;

import java.util.Optional;

import org.joml.Vector2f;

import renderer.Texture;

public class Sprite {
    private Texture texture;
    private Vector2f[] texCoords;

    public Sprite(Texture texture) {
        this.texture = texture;
        this.texCoords = new Vector2f[]{
                new Vector2f(1, 1),
                new Vector2f(1, 0),
                new Vector2f(0, 0),
                new Vector2f(0, 1)
        };
    }

    public Sprite(Texture texture, Vector2f[] texCoord) {
        this.texture = texture;
        this.texCoords = texCoord;
    }

    public Optional<Texture> getTexture() {
        return Optional.ofNullable(texture);
    }

    public Vector2f[] getTexCoords() {
        return texCoords;
    }
}

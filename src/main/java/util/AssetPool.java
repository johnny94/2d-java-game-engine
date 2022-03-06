package util;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import components.SpriteSheet;
import renderer.Shader;
import renderer.Texture;

public final class AssetPool {
    private static final Map<String, Shader> shaders = new HashMap<>();
    private static final Map<String, Texture> textures = new HashMap<>();
    private static final Map<String, SpriteSheet> spriteSheets = new HashMap<>();

    private AssetPool() { }

    public static Shader loadShader(String resourcePath) {
        File f = new File(resourcePath);
        if (shaders.containsKey(f.getAbsolutePath())) {
            return shaders.get(f.getAbsolutePath());
        } else {
            Shader s = new Shader(resourcePath);
            s.compile();
            shaders.put(f.getAbsolutePath(), s);
            return s;
        }
    }

    public static Texture loadTexture(String resourcePath) {
        File f = new File(resourcePath);
        if (textures.containsKey(f.getAbsolutePath())) {
            return textures.get(f.getAbsolutePath());
        } else {
            Texture t = new Texture(resourcePath);
            textures.put(f.getAbsolutePath(), t);
            return t;
        }
    }

    public static void loadSpriteSheet(String resourcePath, SpriteSheet spriteSheet) {
        File f = new File(resourcePath);
        if (!spriteSheets.containsKey(f.getAbsolutePath())) {
            System.out.println("Load sprite sheet " + resourcePath);
            spriteSheets.put(f.getAbsolutePath(), spriteSheet);
        }
    }

    public static SpriteSheet getSpriteSheet(String resourcePath) {
        File f = new File(resourcePath);
        if (!spriteSheets.containsKey(f.getAbsolutePath())) {
            assert false : "Error: Tried to access spritesheet '" + resourcePath + "' and it has not been added to asset pool.";
        }

        return spriteSheets.getOrDefault(f.getAbsolutePath(), null);
    }
}

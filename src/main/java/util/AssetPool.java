package util;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import components.SpriteSheet;
import jade.Sound;
import renderer.Shader;
import renderer.Texture;

public final class AssetPool {
    private static final Map<String, Shader> shaders = new HashMap<>();
    private static final Map<String, Texture> textures = new HashMap<>();
    private static final Map<String, SpriteSheet> spriteSheets = new HashMap<>();
    private static final Map<Path, Sound> sounds = new HashMap<>();

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
            Texture t = new Texture();
            t.init(resourcePath);
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

    public static Collection<Sound> getAllSounds() {
        return sounds.values();
    }

    public static Sound getSound(String resourcePath) {
        Path path = Paths.get(resourcePath).toAbsolutePath();
        Sound sound = sounds.get(path);
        if (sound == null) {
            assert false : "Sound file not added '" + resourcePath + "' and it has not been added to asset pool.";
        }

        return sound;
    }

    public static Sound addSound(String resourcePath, boolean loops) {
        Path path = Paths.get(resourcePath).toAbsolutePath();
        Sound sound = sounds.get(path);
        if (sound == null) {
            return sounds.put(path, new Sound(resourcePath, loops));
        }

        return sound;
    }
}

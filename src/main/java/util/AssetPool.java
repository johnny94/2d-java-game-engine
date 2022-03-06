package util;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import renderer.Shader;
import renderer.Texture;

public final class AssetPool {
    private static final Map<String, Shader> shaders = new HashMap<>();
    private static final Map<String, Texture> textures = new HashMap<>();

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
}

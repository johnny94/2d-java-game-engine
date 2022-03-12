package util;

import com.google.gson.Gson;

import components.Component;
import components.ComponentDeserializer;
import jade.GameObject;
import jade.GameObjectDeserializer;

public final class GsonUtils {
    private GsonUtils() { }

    public static final Gson DEFAULT_GSON = new Gson()
            .newBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(Component.class, new ComponentDeserializer())
            .registerTypeAdapter(GameObject.class, new GameObjectDeserializer())
            .create();
}

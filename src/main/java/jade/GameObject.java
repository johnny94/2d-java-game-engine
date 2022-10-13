package jade;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.google.gson.Gson;

import components.Component;
import components.ComponentDeserializer;
import components.SpriteRenderer;
import components.Transform;
import imgui.ImGui;
import renderer.Texture;
import util.AssetPool;

public class GameObject {
    private static int ID_COUNTER;
    private int uid = -1;

    public String name;
    private final List<Component> components = new ArrayList<>();
    public transient Transform transform;
    private boolean doSerialization = true;

    private boolean isDead;

    public GameObject(String name) {
        this.name = name;

        this.uid = ID_COUNTER;
        ID_COUNTER++;
    }

    public static void init(int maxId) {
        ID_COUNTER = maxId;
    }

    public void start() {
        /*for (Component c : components) {
            c.start();
        }*/

        // TODO: WTF, why this works but the code above got a ConcurrentModificationException?
        // Current known reason is because GizmoManager is adding components when levelEditorObject is iterating
        // its components in start() (GizmoManager is also a component in levelEditorObject)
        for(int i = 0; i < components.size(); i++) {
            components.get(i).start();
        }
    }

    public void editorUpdate(float deltaTime) {
        for (Component c : components) {
            c.editorUpdate(deltaTime);
        }
    }

    public void update(float deltaTime) {
        for (Component c : components) {
             c.update(deltaTime);
        }
    }

    public <T extends Component> Optional<T> getComponent(Class<T> clazz) {
        for (Component c : components) {
            if (clazz.isAssignableFrom(c.getClass())) {
                return Optional.of(clazz.cast(c));
            }
        }

        return Optional.empty();
    }

    public <T extends Component> void removeComponent(Class<T> clazz) {
        for (int i = 0; i < components.size(); i++) {
            if (clazz.isAssignableFrom(components.get(0).getClass())) {
                components.remove(i);
                return;
            }
        }
    }

    public void addComponent(Component component) {
        component.generateId();
        this.components.add(component);
        component.setGameObject(this);
    }

    public void imGui() {
        for (Component c : components) {
            if (ImGui.collapsingHeader(c.getClass().getSimpleName())) {
                c.imGui();
            }
        }
    }

    public int getUid() {
        return this.uid;
    }

    public List<Component> getComponents() {
        return this.components;
    }

    public void setNoSerialize() {
        this.doSerialization = false;
    }

    public boolean isDoSerialization() {
        return doSerialization;
    }

    public GameObject copy() {
        // TODO: Come up with a cleaner solution
        Gson gson = new Gson()
                .newBuilder()
                .registerTypeAdapter(Component.class, new ComponentDeserializer())
                .registerTypeAdapter(GameObject.class, new GameObjectDeserializer())
                .create();
        String json = gson.toJson(this);
        GameObject gameObject = gson.fromJson(json, GameObject.class);
        gameObject.generateUid();
        for(Component c : gameObject.getComponents()) {
            c.generateId();
        }

        Optional<SpriteRenderer> maybeRenderer = gameObject.getComponent(SpriteRenderer.class);
        if (maybeRenderer.isPresent()) {
            Optional<Texture> maybeTexture = maybeRenderer.get().getTexture();
            if (maybeTexture.isPresent()) {
                maybeRenderer.get().setTexture(AssetPool.loadTexture(maybeTexture.get().getFilepath()));
            }
        }

        return gameObject;
    }

    public void generateUid() {
        this.uid = ID_COUNTER++;
    }

    public void destroy() {
        isDead = true;
        for(Component c : components) {
            c.destroy();
        }
    }

    public boolean isDead() {
        return isDead;
    }
}

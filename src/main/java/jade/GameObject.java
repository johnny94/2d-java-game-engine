package jade;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import components.Component;

public class GameObject {
    private static int ID_COUNTER;
    private int uid = -1;

    private String name;
    private List<Component> components = new ArrayList<>();
    private int zIndex;

    public Transform transform;
    private boolean doSerialization = true;

    public GameObject(String name) {
        this(name, new Transform(), 0);
    }

    public GameObject(String name, Transform transform, int zIndex) {
        this.name = name;
        this.transform = transform;
        this.zIndex = zIndex;

        this.uid = ID_COUNTER;
        ID_COUNTER++;
    }

    public static void init(int maxId) {
        ID_COUNTER = maxId;
    }

    public void start() {
        for (Component c : components) {
            c.start();
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
            c.imGui();
        }
    }

    public int zIndex() {
        return this.zIndex;
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
}

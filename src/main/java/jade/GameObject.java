package jade;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import components.Component;

public class GameObject {
    private String name;
    private List<Component> components = new ArrayList<>();

    public Transform transform;

    public GameObject(String name) {
        this(name, new Transform());
    }

    public GameObject(String name, Transform transform) {
        this.name = name;
        this.transform = transform;
    }

    public void start() {
        for (Component c : components) {
            c.start();
        }
    }

    public void update(double deltaTime) {
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
        this.components.add(component);
        component.setGameObject(this);
    }
}

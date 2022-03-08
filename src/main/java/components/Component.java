package components;

import jade.GameObject;

public abstract class Component {
    public transient GameObject gameObject;

    public void start() { }
    public abstract void update(double deltaTime);

    public void setGameObject(GameObject gameObject) {
        this.gameObject = gameObject;
    }

    // Note: Should this implement as an interface?
    public void imGui() {
    }
}

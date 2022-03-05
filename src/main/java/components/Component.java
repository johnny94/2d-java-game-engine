package components;

import jade.GameObject;

public abstract class Component {
    private GameObject gameObject;

    public void start() { }
    public abstract void update(double deltaTime);

    public void setGameObject(GameObject gameObject) {
        this.gameObject = gameObject;
    }

    public GameObject getGameObject() {
        return gameObject;
    }
}

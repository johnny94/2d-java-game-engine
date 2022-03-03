package jade;

public abstract class Scene {
    protected Camera camera;

    public void init() { }
    public abstract void update(double deltaTime);
}

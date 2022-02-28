package jade;

public class LevelEditorScene extends Scene {
    public LevelEditorScene() {
        System.out.println("In LevelEditorScene");
    }

    @Override
    public void update(double deltaTime) {
        System.out.println("FPS: " + (1.0/deltaTime));

    }
}

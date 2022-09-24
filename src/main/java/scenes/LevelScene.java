package scenes;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;

import jade.KeyListener;
import jade.Window;

public class LevelScene extends Scene {
    public LevelScene() {
        System.out.println("In LevelScene");
    }

    @Override
    public void update(double deltaTime) {
        if(KeyListener.getInstance().isKeyPressed(GLFW_KEY_SPACE)) {
            Window.get().changeScene(1);
        }
    }

    @Override
    public void render() {

    }
}

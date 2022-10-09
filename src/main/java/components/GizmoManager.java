package components;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_T;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_Y;

import jade.KeyListener;
import jade.Window;

public class GizmoManager extends Component {
    private enum GizmoType {
        TRANSLATE, SCALE
    }

    private SpriteSheet gizmoSpriteSheet;
    private GizmoType usingGizmo = GizmoType.TRANSLATE;

    public GizmoManager(SpriteSheet gizmoSpriteSheet) {
        this.gizmoSpriteSheet = gizmoSpriteSheet;
    }

    @Override
    public void start() {
        gameObject.addComponent(new TranslateGizmo(gizmoSpriteSheet.getSprite(1),
                                                   Window.get().getImGuiLayer().getPropertiesWindow()));
        gameObject.addComponent(new ScaleGizmo(gizmoSpriteSheet.getSprite(2),
                                               Window.get().getImGuiLayer().getPropertiesWindow()));
    }

    @Override
    public void editorUpdate(float deltaTime) {
        if (usingGizmo == GizmoType.TRANSLATE) {
            gameObject.getComponent(TranslateGizmo.class).get().setUsing();
            gameObject.getComponent(ScaleGizmo.class).get().setNotUsing();
        } else if (usingGizmo == GizmoType.SCALE){
            gameObject.getComponent(TranslateGizmo.class).get().setNotUsing();
            gameObject.getComponent(ScaleGizmo.class).get().setUsing();
        }

        if (KeyListener.getInstance().isKeyPressed(GLFW_KEY_T)) {
            usingGizmo = GizmoType.TRANSLATE;
        } else if (KeyListener.getInstance().isKeyPressed(GLFW_KEY_Y)) {
            usingGizmo = GizmoType.SCALE;
        }
    }
}

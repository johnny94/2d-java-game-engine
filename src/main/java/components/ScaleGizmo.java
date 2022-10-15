package components;

import editor.PropertiesWindow;
import jade.MouseListener;

public class ScaleGizmo extends Gizmo {
    public ScaleGizmo(Sprite scaleSprite, PropertiesWindow propertiesWindow) {
        super(scaleSprite, propertiesWindow);
    }

    @Override
    public void editorUpdate(float deltaTime) {
        if (activeGameObject != null) {
            if (xAxisActive && !yAxisActive) {
                activeGameObject.transform.scale.x -= MouseListener.getInstance().getWorldX();
            } else if (yAxisActive) {
                activeGameObject.transform.scale.y -= MouseListener.getInstance().getWorldY();
            }
        }

        super.editorUpdate(deltaTime);
    }
}

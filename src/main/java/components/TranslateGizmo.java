package components;

import editor.PropertiesWindow;
import jade.MouseListener;

public class TranslateGizmo extends Gizmo {

    public TranslateGizmo(Sprite arrowSprite, PropertiesWindow propertiesWindow) {
        super(arrowSprite, propertiesWindow);
    }

    @Override
    public void editorUpdate(float deltaTime) {
        if (activeGameObject != null) {
            if (xAxisActive && !yAxisActive) {
                activeGameObject.transform.position.x -= MouseListener.getInstance().getWorldX();
            } else if (yAxisActive) {
                activeGameObject.transform.position.y -= MouseListener.getInstance().getWorldY();
            }
        }

        super.editorUpdate(deltaTime);
    }
}

package components;

import org.joml.Vector2f;
import org.joml.Vector4f;

import editor.PropertiesWindow;
import jade.GameObject;
import jade.Prefabs;
import jade.Window;

public class TranslateGizmo extends Component {
    private Vector4f xAxisColor = new Vector4f(1, 0, 0, 1);
    private Vector4f xAxisColorHover = new Vector4f();
    private Vector4f yAxisColor = new Vector4f(0, 1, 0, 1);
    private Vector4f yAxisColorHover = new Vector4f();

    private GameObject xAxisObject;
    private GameObject yAxisObject;
    private SpriteRenderer xAxisSprite;
    private SpriteRenderer yAxisSprite;
    private GameObject activeGameObject;

    private Vector2f xAxisOffset = new Vector2f(64, -5);
    private Vector2f yAxisOffset = new Vector2f(16, 61);

    private PropertiesWindow propertiesWindow;

    public TranslateGizmo(Sprite arrowSprite, PropertiesWindow propertiesWindow) {
        this.xAxisObject = Prefabs.generateSpriteObject(arrowSprite, 16, 48);
        this.yAxisObject = Prefabs.generateSpriteObject(arrowSprite, 16, 48);
        this.xAxisSprite = this.xAxisObject.getComponent(SpriteRenderer.class).get();
        this.yAxisSprite = this.yAxisObject.getComponent(SpriteRenderer.class).get();

        this.propertiesWindow = propertiesWindow;

        Window.get().getCurrentScene().addGameObject(xAxisObject);
        Window.get().getCurrentScene().addGameObject(yAxisObject);
    }

    @Override
    public void start() {
        this.xAxisObject.transform.rotation = 90;
        this.yAxisObject.transform.rotation = 180;
        this.xAxisObject.setNoSerialize();
        this.yAxisObject.setNoSerialize();
    }

    @Override
    public void update(float deltaTime) {
        if (this.activeGameObject != null) {
            this.xAxisObject.transform.position.set(this.activeGameObject.transform.position);
            this.xAxisObject.transform.position.add(xAxisOffset);

            this.yAxisObject.transform.position.set(this.activeGameObject.transform.position);
            this.yAxisObject.transform.position.add(yAxisOffset);
        }

        if (this.propertiesWindow.getActiveGameObject().isPresent()) {
            this.activeGameObject = this.propertiesWindow.getActiveGameObject().get();
            setActive();
        } else {
            setInactive();
        }
    }

    private void setActive() {
        this.xAxisSprite.setColor(xAxisColor);
        this.yAxisSprite.setColor(yAxisColor);
    }

    private void setInactive() {
        this.activeGameObject = null;
        this.xAxisSprite.setColor(new Vector4f(0,0,0,0));
        this.yAxisSprite.setColor(new Vector4f(0,0,0,0));
    }
}

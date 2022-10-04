package components;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

import org.joml.Vector2f;
import org.joml.Vector4f;

import editor.PropertiesWindow;
import jade.GameObject;
import jade.MouseListener;
import jade.Prefabs;
import jade.Window;

public class Gizmo extends Component {
    private Vector4f xAxisColor = new Vector4f(1, 0.3f, 0.3f, 1);
    private Vector4f xAxisColorHover = new Vector4f(1, 0, 0, 1);
    private Vector4f yAxisColor = new Vector4f(0.3f, 1, 0.3f, 1);
    private Vector4f yAxisColorHover = new Vector4f(0, 1, 0, 1);

    private GameObject xAxisObject;
    private GameObject yAxisObject;
    private SpriteRenderer xAxisSprite;
    private SpriteRenderer yAxisSprite;

    private int gizmoWidth = 16;
    private int gizmoHeight = 48;

    private boolean using;

    protected GameObject activeGameObject;
    protected boolean xAxisActive;
    protected boolean yAxisActive;

    private Vector2f xAxisOffset = new Vector2f(64, -5);
    private Vector2f yAxisOffset = new Vector2f(16, 61);

    private PropertiesWindow propertiesWindow;

    public Gizmo(Sprite arrowSprite, PropertiesWindow propertiesWindow) {
        this.xAxisObject = Prefabs.generateSpriteObject(arrowSprite, 16, 48);
        this.yAxisObject = Prefabs.generateSpriteObject(arrowSprite, 16, 48);
        this.xAxisObject.addComponent(new NonPickable());
        this.yAxisObject.addComponent(new NonPickable());

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
        if (!using) {
            return;
        }

        if (this.propertiesWindow.getActiveGameObject().isPresent()) {
            this.activeGameObject = this.propertiesWindow.getActiveGameObject().get();
            setActive();
        } else {
            setInactive();
        }

        boolean xAxisHot = checkXHoverState();
        boolean yAxisHot = checkYHoverState();

        MouseListener mouseListener = MouseListener.getInstance();
        if ((xAxisHot || xAxisActive) && mouseListener.isDragging() &&
            mouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)) {
            xAxisActive = true;
            yAxisActive = false;
        } else if ((yAxisHot || yAxisActive) && mouseListener.isDragging() &&
                   mouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)) {
            xAxisActive = false;
            yAxisActive = true;
        } else {
            xAxisActive = false;
            yAxisActive = false;
        }

        if (this.activeGameObject != null) {
            this.xAxisObject.transform.position.set(this.activeGameObject.transform.position);
            this.xAxisObject.transform.position.add(xAxisOffset);
            this.yAxisObject.transform.position.set(this.activeGameObject.transform.position);
            this.yAxisObject.transform.position.add(yAxisOffset);
        }
    }

    public void setUsing() {
        using = true;
    }

    public void setNotUsing() {
        using = false;
        setInactive();
    }

    private boolean checkXHoverState() {
        Vector2f mousePos = new Vector2f(MouseListener.getInstance().getOrthoX(),
                                         MouseListener.getInstance().getOrthoY());

        if (mousePos.x <= xAxisObject.transform.position.x &&
            mousePos.x >= xAxisObject.transform.position.x - gizmoHeight &&
            mousePos.y >= xAxisObject.transform.position.y &&
            mousePos.y <= xAxisObject.transform.position.y + gizmoWidth) {
            xAxisSprite.setColor(xAxisColorHover);
            return true;
        }

        xAxisSprite.setColor(xAxisColor);
        return false;
    }

    private boolean checkYHoverState() {
        Vector2f mousePos = new Vector2f(MouseListener.getInstance().getOrthoX(),
                                         MouseListener.getInstance().getOrthoY());

        if (mousePos.x <= yAxisObject.transform.position.x &&
            mousePos.x >= yAxisObject.transform.position.x - gizmoWidth &&
            mousePos.y <= yAxisObject.transform.position.y &&
            mousePos.y >= yAxisObject.transform.position.y - gizmoHeight) {
            yAxisSprite.setColor(yAxisColorHover);
            return true;
        }

        yAxisSprite.setColor(yAxisColor);
        return false;
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

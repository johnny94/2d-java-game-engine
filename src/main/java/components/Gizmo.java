package components;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_K;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_CONTROL;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

import org.joml.Vector2f;
import org.joml.Vector4f;

import editor.PropertiesWindow;
import jade.GameObject;
import jade.KeyListener;
import jade.MouseListener;
import jade.Prefabs;
import jade.Window;

public class Gizmo extends Component {
    private static final float gizmoWidth = 16f / 80f;
    private static final float gizmoHeight = 48f / 80f;

    private Vector4f xAxisColor = new Vector4f(1, 0.3f, 0.3f, 1);
    private Vector4f xAxisColorHover = new Vector4f(1, 0, 0, 1);
    private Vector4f yAxisColor = new Vector4f(0.3f, 1, 0.3f, 1);
    private Vector4f yAxisColorHover = new Vector4f(0, 1, 0, 1);

    private GameObject xAxisObject;
    private GameObject yAxisObject;
    private SpriteRenderer xAxisSprite;
    private SpriteRenderer yAxisSprite;

    private boolean using;

    protected GameObject activeGameObject;
    protected boolean xAxisActive;
    protected boolean yAxisActive;

    private Vector2f xAxisOffset = new Vector2f(24f / 80f, -6f / 80f);
    private Vector2f yAxisOffset = new Vector2f(-7f / 80f, 21f / 80f);

    private PropertiesWindow propertiesWindow;

    public Gizmo(Sprite arrowSprite, PropertiesWindow propertiesWindow) {
        this.xAxisObject = Prefabs.generateSpriteObject(arrowSprite, gizmoWidth, gizmoHeight);
        this.yAxisObject = Prefabs.generateSpriteObject(arrowSprite, gizmoWidth, gizmoHeight);
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
        this.xAxisObject.transform.zIndex = 100;
        this.yAxisObject.transform.zIndex = 100;

        this.xAxisObject.setNoSerialize();
        this.yAxisObject.setNoSerialize();
    }

    @Override
    public void update(float deltaTime) {
        if (using) {
            setInactive();
        }

        xAxisObject.getComponent(SpriteRenderer.class).ifPresent(spr -> spr.setColor(new Vector4f(0,0,0,0)));
        yAxisObject.getComponent(SpriteRenderer.class).ifPresent(spr -> spr.setColor(new Vector4f(0,0,0,0)));
    }

    @Override
    public void editorUpdate(float deltaTime) {
        if (!using) {
            return;
        }

        if (this.propertiesWindow.getActiveGameObject().isPresent()) {
            this.activeGameObject = this.propertiesWindow.getActiveGameObject().get();
            setActive();

            // TODO: Move this its own keyEditorBinding component class
            KeyListener keyListener = KeyListener.getInstance();
            if (keyListener.isKeyPressed(GLFW_KEY_LEFT_CONTROL) &&
                keyListener.keyBeginPress(GLFW_KEY_D)) {
                GameObject newObject = activeGameObject.copy();
                Window.get().getCurrentScene().addGameObject(newObject);
                newObject.transform.position.add(0.1f, 0.1f);
                this.propertiesWindow.setActiveGameObject(newObject);
                return;
            } else if (keyListener.isKeyPressed(GLFW_KEY_K)) {
                activeGameObject.destroy();
                this.setInactive();
                propertiesWindow.setActiveGameObject(null);
                return;
            }
        } else {
            setInactive();
            return;
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
        Vector2f mousePos = MouseListener.getInstance().getWorld();

        if (mousePos.x <= xAxisObject.transform.position.x + (gizmoHeight / 2.0f) &&
            mousePos.x >= xAxisObject.transform.position.x - (gizmoWidth /2.0f) &&
            mousePos.y >= xAxisObject.transform.position.y - (gizmoHeight / 2.0f) &&
            mousePos.y <= xAxisObject.transform.position.y + (gizmoWidth /2.0f)) {
            xAxisSprite.setColor(xAxisColorHover);
            return true;
        }

        xAxisSprite.setColor(xAxisColor);
        return false;
    }

    private boolean checkYHoverState() {
        Vector2f mousePos = MouseListener.getInstance().getWorld();

        if (mousePos.x <= yAxisObject.transform.position.x + (gizmoWidth /2.0f)&&
            mousePos.x >= yAxisObject.transform.position.x - (gizmoWidth /2.0f) &&
            mousePos.y <= yAxisObject.transform.position.y + (gizmoHeight / 2.0f) &&
            mousePos.y >= yAxisObject.transform.position.y - (gizmoHeight / 2.0f)) {
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

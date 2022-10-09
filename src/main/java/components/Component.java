package components;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import editor.JImGui;
import imgui.ImGui;
import jade.GameObject;

public abstract class Component {
    private static int ID_COUNTER;
    private int uid = -1;

    public transient GameObject gameObject;

    public void start() { }

    // Maybe we should separate this to different interface?
    public void update(float deltaTime) { }
    public void editorUpdate(float deltaTime) { }

    public void setGameObject(GameObject gameObject) {
        this.gameObject = gameObject;
    }

    // Note: Should this implement as an interface?
    public void imGui() {
        try {
            Field[] fields = getClass().getDeclaredFields();
            for (Field field :  fields) {
                boolean isTransient = Modifier.isTransient(field.getModifiers());
                if (isTransient) {
                    continue;
                }

                boolean isPrivate = Modifier.isPrivate(field.getModifiers());
                if (isPrivate) {
                    field.setAccessible(true);
                }

                Class<?> type = field.getType();
                Object value = field.get(this);
                String name = field.getName();

                if (type.equals(int.class)) {
                    int val = (int)value;
                    field.set(this, JImGui.drawInt(name, val));
                } else if (type.equals(float.class)) {
                    float val = (float)value;
                    field.set(this, JImGui.drawFloat(name, val));
                } else if (type.equals(boolean.class)) {
                    boolean val = (boolean)value;
                    if (ImGui.checkbox(name + ": ", val)) {
                        field.set(this, !val);
                    }
                } else if (type.equals(Vector2f.class)) {
                    Vector2f val = (Vector2f)value;
                    JImGui.drawVec2Control(name, val);
                } else if (type.equals(Vector3f.class)) {
                    Vector3f val = (Vector3f)value;
                    float[] imVec = { val.x, val.y, val.z };
                    if (ImGui.dragFloat3(name + ": ", imVec)) {
                        val.set(imVec);
                    }
                } else if (type.equals(Vector4f.class)) {
                    Vector4f val = (Vector4f)value;
                    float[] imVec = { val.x, val.y, val.z, val.w };
                    if (ImGui.dragFloat3(name + ": ", imVec)) {
                        val.set(imVec);
                    }
                }

                if (isPrivate) {
                    field.setAccessible(false);
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void generateId() {
        if (this.uid == -1) {
            this.uid = ID_COUNTER;
            ID_COUNTER++;
        }
    }

    public int getUid() {
        return this.uid;
    }

    public static void init(int maxId) {
        ID_COUNTER = maxId;
    }

    public void destroy() {
        // Do nothing
    }
}

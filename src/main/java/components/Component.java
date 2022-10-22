package components;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import editor.JImGui;
import imgui.ImGui;
import imgui.type.ImInt;
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

                // TODO: Want to fix raw type
                Class type = field.getType();
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
                } else if (type.isEnum()) {
                    String[] enumValues = getEnumValues(type);
                    String enumType = ((Enum<?>)value).name();

                    ImInt index = new ImInt(indexOf(enumType, enumValues));
                    if (ImGui.combo(field.getName(), index, enumValues, enumValues.length)) {
                        field.set(this, type.getEnumConstants()[index.get()]);
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

    public void beginCollision(GameObject collidingObject, Contact contact, Vector2f hitNormal) {

    }

    public void endCollision(GameObject collidingObject, Contact contact, Vector2f hitNormal) {

    }

    public void preSolve(GameObject collidingObject, Contact contact, Vector2f hitNormal) {

    }

    public void postSolve(GameObject collidingObject, Contact contact, Vector2f hitNormal) {

    }

    public static void init(int maxId) {
        ID_COUNTER = maxId;
    }

    private <T extends Enum<T>> String[] getEnumValues(Class<T> enumType) {
        String[] enumValues = new String[enumType.getEnumConstants().length];
        for (int i = 0; i < enumType.getEnumConstants().length; i++) {
            enumValues[i] = enumType.getEnumConstants()[i].name();
        }
        return enumValues;
    }

    private int indexOf(String str, String[] arr) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i].equals(str)) {
                return i;
            }
        }

        return -1;
    }

    public void destroy() {
        // Do nothing
    }
}

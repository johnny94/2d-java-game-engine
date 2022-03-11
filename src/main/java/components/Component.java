package components;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.joml.Vector3f;
import org.joml.Vector4f;

import imgui.ImGui;
import jade.GameObject;

public abstract class Component {
    public transient GameObject gameObject;

    public void start() { }
    public abstract void update(double deltaTime);

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
                    int[] imInt = { val };
                    if (ImGui.dragInt(name + ": ", imInt)) {
                        field.set(this, imInt[0]);
                    }
                } else if (type.equals(float.class)) {
                    float val = (float)value;
                    float[] imFloat = { val };
                    if (ImGui.dragFloat(name + ": ", imFloat)) {
                        field.set(this, imFloat[0]);
                    }
                } else if (type.equals(boolean.class)) {
                    boolean val = (boolean)value;
                    if (ImGui.checkbox(name + ": ", val)) {
                        field.set(this, !val);
                    }
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
}

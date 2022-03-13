package renderer;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_LINES;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glLineWidth;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glBufferSubData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import jade.Window;
import util.AssetPool;

public final class DebugDraw {
    private static final int MAX_LINE = 500;
    private static final int POS_SIZE = 3; // x y z
    private static final int COLOR_SIZE = 3; // r g b
    private static final int VERTEX_SIZE = POS_SIZE + COLOR_SIZE;

    private static List<Line2D> lines = new ArrayList<>();

    // Each line has 2 vertex (from and to)
    private static float[] vertexArray = new float[MAX_LINE * VERTEX_SIZE * 2];
    private static Shader shader = AssetPool.loadShader("assets/shaders/debugLine2D.glsl");

    private static int vaoId;
    private static int vboId;

    private static boolean started;

    private DebugDraw() { }

    public static void start() {
        vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);

        vboId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, (long)vertexArray.length * Float.BYTES, GL_DYNAMIC_DRAW);

        glVertexAttribPointer(0, 3, GL_FLOAT, false,
                              VERTEX_SIZE * Float.BYTES, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, 3, GL_FLOAT, false,
                              VERTEX_SIZE * Float.BYTES, POS_SIZE * Float.BYTES);
        glEnableVertexAttribArray(1);
        glLineWidth(20.0f);
    }

    public static void draw() {
        beginFrame();

        if (lines.isEmpty()) {
            return;
        }

        int index = 0;
        for (Line2D line : lines) {
            // from and to
            for (int i = 0; i < 2; i++) {
                Vector2f position = i == 0 ? line.getFrom() : line.getTo();
                Vector3f color = line.getColor();

                // Position
                vertexArray[index] = position.x;
                vertexArray[index + 1] = position.y;
                vertexArray[index + 2] = -10.0f; // Doesn't matter in 2d

                // Color
                vertexArray[index + 3] = color.x;
                vertexArray[index + 4] = color.y;
                vertexArray[index + 5] = color.z;
                index += 6;
            }
        }

        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferSubData(GL_ARRAY_BUFFER, 0,
                        Arrays.copyOfRange(vertexArray, 0, totalVertex()));

        beginDraw();

        glDrawArrays(GL_LINES, 0, totalVertex());

        endDraw();

        shader.detach();
    }

    private static void beginFrame() {
        if (!started) {
            start();
            started = true;
        }

        lines.removeIf(line -> line.beginFrame() < 0);
    }

    private static void beginDraw() {
        shader.use();
        shader.setMat4f("uProjection", getProjectionMatrix());
        shader.setMat4f("uView", getViewMatrix());

        glBindVertexArray(vaoId);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
    }

    private static void endDraw() {
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);
        shader.detach();
    }

    private static int totalVertex() {
        return lines.size() * VERTEX_SIZE * 2;
    }

    private static Matrix4f getProjectionMatrix() {
        return Window.get().getCurrentScene().getCamera().getProjectionMatrix();
    }

    private static Matrix4f getViewMatrix() {
        return Window.get().getCurrentScene().getCamera().getViewMatrix();
    }

    // =========================
    // 2D Line
    // =========================
    public static void drawLine(Vector2f from, Vector2f to) {
        drawLine(from, to, new Vector3f(0, 1, 0));
    }

    public static void drawLine(Vector2f from, Vector2f to, Vector3f color) {
        drawLine(from, to, color, 1);
    }

    public static void drawLine(Vector2f from, Vector2f to, Vector3f color, int lifeTime) {
        if (lines.size() >= MAX_LINE) {
            System.out.println("The number of lines exceed the limit: " + MAX_LINE);
            return;
        }

        lines.add(new Line2D(from, to, color, lifeTime));
    }
}

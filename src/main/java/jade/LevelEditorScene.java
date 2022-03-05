package jade;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.joml.Vector2f;
import org.lwjgl.BufferUtils;

import renderer.Shader;
import renderer.Texture;
import util.Time;

public class LevelEditorScene extends Scene {

    private float[] vertexArray = {
            // Position             // Color                    // Texture coords
            100.5f, 0.5f, 0.0f,     1.0f, 0.0f, 0.0f, 1.0f,     1, 0,   // Bottom right 0
            0.5f, 100.5f, 0.0f,     0.0f, 1.0f, 0.0f, 1.0f,     0, 1,   // Top left     1
            100.5f, 100.5f, 0.0f,   0.0f, 0.0f, 1.0f, 1.0f,     1, 1,   // Top right    2
            -0.5f, -0.5f, 0.0f,     1.0f, 1.0f, 0.0f, 1.0f,     0, 0    // Bottom left  3
    };

    // Note: This should be counter-clockwise order
    private int[] elementArray = {
            /*
                *       *


                *       *
             */
            2, 1, 0, // Top right triangle
            0, 1, 3  // Bottom left triangle
    };

    private int vaoId;
    private int vboId;
    private int eboId;

    private Shader defaultShader;
    private Texture testTexture;

    @Override
    public void init() {
        this.camera = new Camera(new Vector2f());
        this.testTexture = new Texture("assets/images/awesomeface.png");

        defaultShader = new Shader("assets/shaders/default.glsl");
        defaultShader.compile();

        // Generate VAO, VBO, EBO and send to GPU
        vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);

        // Create VBO
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertexArray.length);
        vertexBuffer.put(vertexArray).flip();

        vboId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

        // Create EBO
        IntBuffer elementBuffer = BufferUtils.createIntBuffer(elementArray.length);
        elementBuffer.put(elementArray).flip();

        eboId = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW);

        int positionSize = 3; // x y z
        int colorSize = 4;    // r g b a
        int uvSize = 2;
        int vertexSizeBytes = (positionSize + colorSize + uvSize) * Float.BYTES;
        glVertexAttribPointer(0, positionSize, GL_FLOAT, false, vertexSizeBytes, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, colorSize, GL_FLOAT, false, vertexSizeBytes,
                              positionSize * Float.BYTES);
        glEnableVertexAttribArray(1);

        glVertexAttribPointer(2, uvSize, GL_FLOAT, false, vertexSizeBytes,
                              (positionSize + colorSize) * Float.BYTES);
        glEnableVertexAttribArray(2);
    }

    @Override
    public void update(double deltaTime) {
        // Use shader
        camera.position.x -= deltaTime * 50f;
        camera.position.y -= deltaTime * 20f;

        defaultShader.use();

        defaultShader.setTexture("TEX_SAMPLER", 0);
        glActiveTexture(GL_TEXTURE0);
        testTexture.bind();

        defaultShader.setMat4f("uProjection", camera.getProjectionMatrix());
        defaultShader.setMat4f("uView", camera.getViewMatrix());
        defaultShader.setFloat("uTime", (float)Time.getTime());

        // Bind VAO we want to use
        glBindVertexArray(vaoId);

        // Enable vertex attributes
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        // Draw
        glDrawElements(GL_TRIANGLES, elementArray.length, GL_UNSIGNED_INT, 0);

        // Unbind everything
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);
        defaultShader.detach();
    }
}

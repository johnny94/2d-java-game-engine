package renderer;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glBufferSubData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

import org.joml.Vector4f;

import components.SpriteRenderer;
import jade.Window;
import util.AssetPool;

public class RenderBatch {
    // Vertex Information
    // ======
    // Pos              Color
    // float, float     float, float, float, float

    private static final int POS_SIZE = 2;
    private static final int COLOR_SIZE = 4;

    // In bytes
    private static final int POS_OFFSET = 0;
    private static final int COLOR_OFFSET = POS_OFFSET + POS_SIZE * Float.BYTES;

    private static final int VERTEX_SIZE = POS_SIZE + COLOR_SIZE;
    private static final int VERTEX_SIZE_BYTES = VERTEX_SIZE * Float.BYTES;

    private static final int VERTEX_PER_QUAD = 4;

    private SpriteRenderer[] spriteRenderers;
    private int numSprites;
    private boolean hasRoom;
    private float[] vertices;

    private int vaoId;
    private int vboId;
    private int maxBatchSize;
    private Shader shader;

    public RenderBatch(int maxBatchSize) {
        this.maxBatchSize = maxBatchSize;
        this.spriteRenderers = new SpriteRenderer[maxBatchSize];
        this.shader = AssetPool.loadShader("assets/shaders/default.glsl");
        this.shader.compile();

        this.vertices = new float[maxBatchSize * VERTEX_PER_QUAD * VERTEX_SIZE];

        this.numSprites = 0;
        this.hasRoom = true;
    }

    public void start() {
        this.vaoId = glGenVertexArrays();
        glBindVertexArray(this.vaoId);

        // Allocate space for all vertices
        this.vboId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, (long)this.vertices.length * Float.BYTES, GL_DYNAMIC_DRAW);

        // Create Index buffer
        int ebo = glGenBuffers();
        int[] indices = generateIndices();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        glVertexAttribPointer(0, POS_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, POS_OFFSET);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, COLOR_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, COLOR_OFFSET);
        glEnableVertexAttribArray(1);
    }

    public void addSprite(SpriteRenderer spriteRenderer) {
        int index = this.numSprites;
        this.spriteRenderers[index] = spriteRenderer;
        this.numSprites++;

        loadVertexProperties(index);

        if (this.numSprites >= maxBatchSize) {
            this.hasRoom = false;
        }
    }

    public boolean hasRoom() {
        return hasRoom;
    }

    private void loadVertexProperties(int index) {
        SpriteRenderer spr = spriteRenderers[index];
        int offset = index * VERTEX_PER_QUAD * VERTEX_SIZE;

        Vector4f color = spr.getColor();

        // Add vertices with appropriate properties
        // The algorithm here is to generate a quad (4 vertices) with a given position
        // It will generate vertices in this order: top-left, bottom-left, bottom-right, top-left
        //
        // Let's say give position is (0, 0) and scale is (1, 1)
        // Then the first 4 element of vertices array will be (1, 1), (1, 0), (0, 0), (0, 1)
        float xAdd = 1.0f;
        float yAdd = 1.0f;
        for (int i = 0; i < 4; i++) {
            if (i == 1) {
                yAdd = 0.0f;
            } else if (i == 2) {
                xAdd = 0.0f;
            } else if (i == 3) {
                yAdd = 1.0f;
            }

            // Load position
            vertices[offset] = spr.getGameObject().transform.position.x +
                               (xAdd * spr.getGameObject().transform.scale.x);
            vertices[offset + 1] = spr.getGameObject().transform.position.y +
                               (yAdd * spr.getGameObject().transform.scale.y);

            // Set color
            vertices[offset + 2] = color.x;
            vertices[offset + 3] = color.y;
            vertices[offset + 4] = color.z;
            vertices[offset + 5] = color.w;

            offset += VERTEX_SIZE;
        }
    }

    public void render() {
        // For now, we will re-buffer all data every frame
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);

        shader.use();
        shader.setMat4f("uProjection", Window.get().getCurrentScene().getCamera().getProjectionMatrix());
        shader.setMat4f("uView", Window.get().getCurrentScene().getCamera().getViewMatrix());

        glBindVertexArray(vaoId);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES, 6 * numSprites, GL_UNSIGNED_INT, 0);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);

        shader.detach();
    }

    private int[] generateIndices() {
        // There are 6 indices per quad (3 per triangle)
        int[] indices = new int[maxBatchSize * 6];
        for (int i = 0; i < maxBatchSize; i++) {
            loadElementIndices(indices, i);
        }

        return indices;
    }

    private void loadElementIndices(int[] indices, int curSprite) {
        int offsetArrayIndex = 6 * curSprite; // Each quad needs 6 indices
        int offset = VERTEX_PER_QUAD * curSprite;

        // Index will be
        // First quad                   Second quad
        // Element in indices:
        // 3, 2, 0, 0, 2, 1,            7, 6, 4, 4, 6, 5, ...
        //
        // offsetArrayIndex:
        // 0  1  2  3  4  5             6 (which is 6 * 1 + 0)... and so on
        //
        // offset:
        // 0+3, 0+2, 0, 0, 0+2, 0+1     (4 * 1) + 3... and so on

        indices[offsetArrayIndex] = offset + 3;
        indices[offsetArrayIndex + 1] = offset + 2;
        indices[offsetArrayIndex + 2] = offset;

        indices[offsetArrayIndex + 3] = offset;
        indices[offsetArrayIndex + 4] = offset + 2;
        indices[offsetArrayIndex + 5] = offset + 1;
    }
}

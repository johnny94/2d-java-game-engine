package renderer;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;

import components.SpriteRenderer;
import jade.Window;

public class RenderBatch implements Comparable<RenderBatch> {
    // Vertex Information
    // ======
    // Pos              Color                           tex coord      texIndex
    // float, float     float, float, float, float      float, float   float

    private static final int POS_SIZE = 2;
    private static final int COLOR_SIZE = 4;
    private static final int TEX_COORD_SIZE = 2;
    private static final int TEX_INDEX_SIZE = 1;
    private static final int ENTITY_ID_INDEX_SIZE = 1;

    // In bytes
    private static final int POS_OFFSET = 0;
    private static final int COLOR_OFFSET = POS_OFFSET + POS_SIZE * Float.BYTES;
    private static final int TEX_COORD_OFFSET = COLOR_OFFSET + COLOR_SIZE * Float.BYTES;
    private static final int TEX_INDEX_OFFSET = TEX_COORD_OFFSET + TEX_COORD_SIZE * Float.BYTES;
    private static final int ENTITY_ID_INDEX_OFFSET = TEX_INDEX_OFFSET + TEX_INDEX_SIZE * Float.BYTES;

    private static final int VERTEX_SIZE = POS_SIZE + COLOR_SIZE + TEX_COORD_SIZE +
                                           TEX_INDEX_SIZE + ENTITY_ID_INDEX_SIZE;
    private static final int VERTEX_SIZE_BYTES = VERTEX_SIZE * Float.BYTES;

    private static final int VERTEX_PER_QUAD = 4;

    private static final int MAX_NUM_TEXTURE = 8;

    private SpriteRenderer[] spriteRenderers;
    private List<Texture> textures; // TODO: This can be changed to Set

    private int numSprites;
    private boolean hasRoom;
    private float[] vertices;

    // We will use the first element for no texture sprite
    private int[] texSlots = IntStream.range(0, 8).toArray();

    private int vaoId;
    private int vboId;
    private int maxBatchSize;
    private int zIndex;

    public RenderBatch(int maxBatchSize, int zIndex) {
        this.maxBatchSize = maxBatchSize;
        this.zIndex = zIndex;
        this.spriteRenderers = new SpriteRenderer[maxBatchSize];
        this.textures = new ArrayList<>();
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

        glVertexAttribPointer(2, TEX_COORD_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, TEX_COORD_OFFSET);
        glEnableVertexAttribArray(2);

        glVertexAttribPointer(3, TEX_INDEX_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, TEX_INDEX_OFFSET);
        glEnableVertexAttribArray(3);

        glVertexAttribPointer(4, ENTITY_ID_INDEX_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, ENTITY_ID_INDEX_OFFSET);
        glEnableVertexAttribArray(4);
    }

    public void addSpriteRenderer(SpriteRenderer spriteRenderer) {
        int index = this.numSprites;
        this.spriteRenderers[index] = spriteRenderer;
        this.numSprites++;

        if (spriteRenderer.getTexture().isPresent()) {
            Texture t = spriteRenderer.getTexture().get();
            if (!textures.contains(t)) {
                textures.add(t);
            }
        }

        loadVertexProperties(index);

        if (this.numSprites >= maxBatchSize) {
            this.hasRoom = false;
        }
    }

    public boolean hasRoom() {
        return hasRoom;
    }

    public boolean hasTextureRoom() {
        return this.textures.size() < MAX_NUM_TEXTURE;
    }

    public boolean hasTexture(Texture tex) {
        return this.textures.contains(tex);
    }

    private void loadVertexProperties(int sprIndex) {
        SpriteRenderer spr = spriteRenderers[sprIndex];
        int offset = sprIndex * VERTEX_PER_QUAD * VERTEX_SIZE;

        Vector4f color = spr.getColor();
        Vector2f[] texCoords = spr.getTextCoords();

        int slotIndex = -1;
        if (spr.getTexture().isPresent()) {
            Texture t = spr.getTexture().get();
            for (int i = 0; i < textures.size(); i++) {
                if (textures.get(i).equals(t)) {
                    slotIndex = i;
                    break;
                }
            }
        }

        boolean isRotated = spr.gameObject.transform.rotation != 0.0f;
        Matrix4f transformMatrix = new Matrix4f().identity();
        if (isRotated) {
            transformMatrix.translate(spr.gameObject.transform.position.x,
                                      spr.gameObject.transform.position.y, 0);
            transformMatrix.rotate((float)Math.toRadians(spr.gameObject.transform.rotation), 0, 0, 1);
            transformMatrix.scale(spr.gameObject.transform.scale.x, spr.gameObject.transform.scale.y, 1);
        }

        // Add vertices with appropriate properties
        // The algorithm here is to generate a quad (4 vertices) with a given position
        // It will generate vertices in this order: top-left, bottom-left, bottom-right, top-left
        //
        // Let's say give position is (0, 0) and scale is (1, 1)
        // Then the first 4 element of vertices array will be (1, 1), (1, 0), (0, 0), (0, 1)
        float xAdd = 1.0f;
        float yAdd = 1.0f;
        for (int i = 0; i < VERTEX_PER_QUAD; i++) {
            if (i == 1) {
                yAdd = 0.0f;
            } else if (i == 2) {
                xAdd = 0.0f;
            } else if (i == 3) {
                yAdd = 1.0f;
            }

            Vector4f currentPos = new Vector4f(spr.gameObject.transform.position.x +
                                               (xAdd * spr.gameObject.transform.scale.x),
                                               spr.gameObject.transform.position.y +
                                               (yAdd * spr.gameObject.transform.scale.y), 0, 1);
            if (isRotated) {
                currentPos = new Vector4f(xAdd, yAdd, 0, 1).mul(transformMatrix);
            }

            // Set position
            vertices[offset] = currentPos.x;
            vertices[offset + 1] = currentPos.y;

            // Set color
            vertices[offset + 2] = color.x;
            vertices[offset + 3] = color.y;
            vertices[offset + 4] = color.z;
            vertices[offset + 5] = color.w;

            // Set tex coords
            vertices[offset + 6] = texCoords[i].x;
            vertices[offset + 7] = texCoords[i].y;

            // Set tex index
            vertices[offset + 8] = slotIndex;

            // Set entity id
            // Plus 1 because we will minus 1 after getting the value (See PickingTexture#readPixel)
            vertices[offset + 9] = spr.gameObject.getUid() + 1;

            offset += VERTEX_SIZE;
        }
    }

    public void render() {
        boolean rebufferData = false;
        for (int i = 0; i < numSprites; i++) {
            SpriteRenderer spr = spriteRenderers[i];
            if (spr.isDirty()) {
                loadVertexProperties(i);
                spr.setClean();
                rebufferData = true;
            }
        }

        if (rebufferData) {
            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);
        }

        Shader shader = Renderer.getBoundShader();
        shader.setMat4f("uProjection", Window.get().getCurrentScene().getCamera().getProjectionMatrix());
        shader.setMat4f("uView", Window.get().getCurrentScene().getCamera().getViewMatrix());

        for (int i = 0; i < textures.size(); i++) {
            glActiveTexture(GL_TEXTURE0 + i); // Remember that we reserve 0 for no texture
            textures.get(i).bind();
        }
        shader.setIntArray("uTextures", texSlots);


        glBindVertexArray(vaoId);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES, 6 * numSprites, GL_UNSIGNED_INT, 0);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);

        for (int i = 0; i < textures.size(); i++) {
            textures.get(i).unbind();
        }

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

    public int zIndex() {
        return this.zIndex;
    }

    @Override
    public int compareTo(RenderBatch o) {
        return Integer.compare(this.zIndex, o.zIndex);
    }

    public boolean destroyIfExist(SpriteRenderer spriteRenderer) {
        for (int i = 0; i < numSprites; i++) {
            if (spriteRenderers[i] == spriteRenderer) {

                // [1, 2, 3, 4, 5]
                // If 3 is remove target then the array will be
                // [1, 2, 4, 5]
                // TODO: Can we re-implement this with List?
                for (int j = i; j < numSprites - 1; j++) {
                    spriteRenderers[j] = spriteRenderers[j + 1];
                    spriteRenderers[j].setDirty();
                }
                numSprites--;
                return true;
            }
        }

        return false;
    }
}

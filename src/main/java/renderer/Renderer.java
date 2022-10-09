package renderer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import components.SpriteRenderer;
import jade.GameObject;

public class Renderer {
    private static final int MAX_BATCH_SIZE = 1000;
    private final List<RenderBatch> renderBatchs = new ArrayList<>();

    // This is a global state
    private static Shader currentShader;

    // TODO: Maybe SpriteRenderer will be better
    public void add(GameObject gameObject) {
        Optional<SpriteRenderer> spr = gameObject.getComponent(SpriteRenderer.class);
        if (spr.isPresent()) {
            add(spr.get());
        }
    }

    private void add(SpriteRenderer renderer) {
        boolean added = false;
        for (RenderBatch rb : renderBatchs) {
            if (rb.hasRoom() && rb.zIndex() == renderer.gameObject.transform.zIndex) {
                Optional<Texture> tex = renderer.getTexture();
                if (!tex.isPresent() || rb.hasTexture(tex.get()) || rb.hasTextureRoom()) {
                    rb.addSpriteRenderer(renderer);
                    added = true;
                    break;
                }

            }
        }

        if (!added) {
            RenderBatch rb = new RenderBatch(MAX_BATCH_SIZE, renderer.gameObject.transform.zIndex, this);
            rb.start();
            renderBatchs.add(rb);
            rb.addSpriteRenderer(renderer);
            Collections.sort(renderBatchs);
        }
    }

    public static void bindShader(Shader shader) {
        currentShader = shader;
    }

    public static Shader getBoundShader() {
        return currentShader;
    }

    public void render() {
        currentShader.use();
        for (int i = 0; i < renderBatchs.size(); i++) {
            renderBatchs.get(i).render();
        }
    }

    public void destroyGameObject(GameObject gameObject) {
        Optional<SpriteRenderer> maybeSpriteRenderer = gameObject.getComponent(SpriteRenderer.class);
        if (!maybeSpriteRenderer.isPresent()) {
            return;
        }

        for (RenderBatch renderBatch : renderBatchs) {
            if (renderBatch.destroyIfExist(maybeSpriteRenderer.get())) {
                return;
            }
        }
    }
}

package renderer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import components.SpriteRenderer;
import jade.GameObject;

public class Renderer {
    private static final int MAX_BATCH_SIZE = 1000;
    private final List<RenderBatch> renderBatchs = new ArrayList<>();

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
            if (rb.hasRoom()) {
                Optional<Texture> tex = renderer.getTexture();
                if (!tex.isPresent() || rb.hasTexture(tex.get()) || rb.hasTextureRoom()) {
                    rb.addSprite(renderer);
                    added = true;
                    break;
                }

            }
        }

        if (!added) {
            RenderBatch rb = new RenderBatch(MAX_BATCH_SIZE);
            rb.start();
            renderBatchs.add(rb);
            rb.addSprite(renderer);
        }
    }

    public void render() {
        for(RenderBatch rb : renderBatchs) {
            rb.render();
        }
    }
}

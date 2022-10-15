package components.animation;

import java.util.ArrayList;
import java.util.List;

import components.Sprite;
import util.AssetPool;

public class AnimationState { // Animation?
    private static final Sprite defaultSprite = new Sprite();

    public String title;
    public List<Frame> animationFrames = new ArrayList<>();

    private transient float timeTracker = 0.0f;
    private transient int currentFrame = 0; // currentFrame?
    public boolean doesLoop;

    public AnimationState(String title) {
        this.title = title;
    }

    public void addFrame(Sprite sprite, float frameTime) {
        animationFrames.add(new Frame(sprite, frameTime));
    }

    public void setLoop(boolean doesLoop) {
        this.doesLoop = doesLoop;
    }

    public void update(float deltaTime) {
        if (currentFrame < animationFrames.size()) {
            timeTracker -= deltaTime;
            if (timeTracker <= 0) {
                if (currentFrame != animationFrames.size() - 1 || doesLoop) {
                    currentFrame = (currentFrame + 1) % animationFrames.size();
                }

                timeTracker = animationFrames.get(currentFrame).frameTime;
            }
        }
    }

    public Sprite getCurrentSprite() {
        if (currentFrame < animationFrames.size()) {
            return animationFrames.get(currentFrame).sprite;
        }

        return defaultSprite;
    }

    public void refreshTextures() {
        for (Frame frame : animationFrames) {
            frame.sprite.setTexture(AssetPool.loadTexture(frame.sprite.getTexture().get().getFilepath()));
        }
    }
}

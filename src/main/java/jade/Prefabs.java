package jade;

import components.Sprite;
import components.SpriteRenderer;
import components.SpriteSheet;
import components.animation.AnimationState;
import components.animation.StateMachine;
import util.AssetPool;

public class Prefabs {
    public static GameObject generateSpriteObject(Sprite sprite, float scaleX, float scaleY) {
        GameObject block = Window.get().getCurrentScene().createGameObject("Sprite_Object_Gen");
        block.transform.scale.x = scaleX;
        block.transform.scale.y = scaleY;
        block.addComponent(new SpriteRenderer(sprite));

        return block;
    }

    public static GameObject generateMario() {
        SpriteSheet player = AssetPool.getSpriteSheet("assets/images/spritesheets/spritesheet.png");
        GameObject mario = generateSpriteObject(player.getSprite(0), 0.25f, 0.25f);

        AnimationState run = new AnimationState("Run");
        float defaultFrameTime = 0.23f;
        run.addFrame(player.getSprite(0), defaultFrameTime);
        run.addFrame(player.getSprite(2), defaultFrameTime);
        run.addFrame(player.getSprite(3), defaultFrameTime);
        run.addFrame(player.getSprite(2), defaultFrameTime);
        run.setLoop(true);

        StateMachine stateMachine = new StateMachine();
        stateMachine.addState(run);
        stateMachine.setDefaultState(run.title);

        mario.addComponent(stateMachine);

        return mario;
    }

    public static GameObject generateQuestionBlock() {
        SpriteSheet items = AssetPool.getSpriteSheet("assets/images/spritesheets/items.png");
        GameObject questionBlock = generateSpriteObject(items.getSprite(0), 0.25f, 0.25f);

        AnimationState run = new AnimationState("Flicker");
        float defaultFrameTime = 0.23f;
        run.addFrame(items.getSprite(0), 0.57f);
        run.addFrame(items.getSprite(1), defaultFrameTime);
        run.addFrame(items.getSprite(2), defaultFrameTime);
        run.setLoop(true);

        StateMachine stateMachine = new StateMachine();
        stateMachine.addState(run);
        stateMachine.setDefaultState(run.title);

        questionBlock.addComponent(stateMachine);

        return questionBlock;
    }

}

package jade;

import components.PlayerController;
import components.Sprite;
import components.SpriteRenderer;
import components.SpriteSheet;
import components.animation.AnimationState;
import components.animation.StateMachine;
import physics2d.components.PillBoxCollider;
import physics2d.components.RigidBody2D;
import physics2d.enums.BodyType;
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
        SpriteSheet playerSprites = AssetPool.getSpriteSheet("assets/images/spritesheets/spritesheet.png");
        SpriteSheet bigPlayerSprites = AssetPool.getSpriteSheet("assets/images/spritesheets/bigSpritesheet.png");
        GameObject mario = generateSpriteObject(playerSprites.getSprite(0), 0.25f, 0.25f);

        AnimationState run = new AnimationState("Run");
        float defaultFrameTime = 0.2f;
        run.addFrame(playerSprites.getSprite(0), defaultFrameTime);
        run.addFrame(playerSprites.getSprite(2), defaultFrameTime);
        run.addFrame(playerSprites.getSprite(3), defaultFrameTime);
        run.addFrame(playerSprites.getSprite(2), defaultFrameTime);
        run.setLoop(true);

        AnimationState switchDirection = new AnimationState("Switch Direction");
        switchDirection.addFrame(playerSprites.getSprite(4), 0.1f);
        switchDirection.setLoop(false);

        AnimationState idle = new AnimationState("Idle");
        idle.addFrame(playerSprites.getSprite(0), 0.1f);
        idle.setLoop(false);

        AnimationState jump = new AnimationState("Jump");
        jump.addFrame(playerSprites.getSprite(5), 0.1f);
        jump.setLoop(false);

        // Big mario animations
        AnimationState bigRun = new AnimationState("BigRun");
        bigRun.addFrame(bigPlayerSprites.getSprite(0), defaultFrameTime);
        bigRun.addFrame(bigPlayerSprites.getSprite(1), defaultFrameTime);
        bigRun.addFrame(bigPlayerSprites.getSprite(2), defaultFrameTime);
        bigRun.addFrame(bigPlayerSprites.getSprite(3), defaultFrameTime);
        bigRun.addFrame(bigPlayerSprites.getSprite(2), defaultFrameTime);
        bigRun.addFrame(bigPlayerSprites.getSprite(1), defaultFrameTime);
        bigRun.setLoop(true);

        AnimationState bigSwitchDirection = new AnimationState("Big Switch Direction");
        bigSwitchDirection.addFrame(bigPlayerSprites.getSprite(4), 0.1f);
        bigSwitchDirection.setLoop(false);

        AnimationState bigIdle = new AnimationState("BigIdle");
        bigIdle.addFrame(bigPlayerSprites.getSprite(0), 0.1f);
        bigIdle.setLoop(false);

        AnimationState bigJump = new AnimationState("BigJump");
        bigJump.addFrame(bigPlayerSprites.getSprite(5), 0.1f);
        bigJump.setLoop(false);

        // Fire mario animations
        int fireOffset = 21;
        AnimationState fireRun = new AnimationState("FireRun");
        fireRun.addFrame(bigPlayerSprites.getSprite(fireOffset + 0), defaultFrameTime);
        fireRun.addFrame(bigPlayerSprites.getSprite(fireOffset + 1), defaultFrameTime);
        fireRun.addFrame(bigPlayerSprites.getSprite(fireOffset + 2), defaultFrameTime);
        fireRun.addFrame(bigPlayerSprites.getSprite(fireOffset + 3), defaultFrameTime);
        fireRun.addFrame(bigPlayerSprites.getSprite(fireOffset + 2), defaultFrameTime);
        fireRun.addFrame(bigPlayerSprites.getSprite(fireOffset + 1), defaultFrameTime);
        fireRun.setLoop(true);

        AnimationState fireSwitchDirection = new AnimationState("Fire Switch Direction");
        fireSwitchDirection.addFrame(bigPlayerSprites.getSprite(fireOffset + 4), 0.1f);
        fireSwitchDirection.setLoop(false);

        AnimationState fireIdle = new AnimationState("FireIdle");
        fireIdle.addFrame(bigPlayerSprites.getSprite(fireOffset + 0), 0.1f);
        fireIdle.setLoop(false);

        AnimationState fireJump = new AnimationState("FireJump");
        fireJump.addFrame(bigPlayerSprites.getSprite(fireOffset + 5), 0.1f);
        fireJump.setLoop(false);

        AnimationState die = new AnimationState("Die");
        die.addFrame(playerSprites.getSprite(6), 0.1f);
        die.setLoop(false);

        StateMachine stateMachine = new StateMachine();
        stateMachine.addState(run);
        stateMachine.addState(idle);
        stateMachine.addState(switchDirection);
        stateMachine.addState(jump);
        stateMachine.addState(die);

        stateMachine.addState(bigRun);
        stateMachine.addState(bigIdle);
        stateMachine.addState(bigSwitchDirection);
        stateMachine.addState(bigJump);

        stateMachine.addState(fireRun);
        stateMachine.addState(fireIdle);
        stateMachine.addState(fireSwitchDirection);
        stateMachine.addState(fireJump);

        stateMachine.setDefaultState(idle.title);
        stateMachine.addTrigger(run.title, switchDirection.title, "switchDirection");
        stateMachine.addTrigger(run.title, idle.title, "stopRunning");
        stateMachine.addTrigger(run.title, jump.title, "jump");
        stateMachine.addTrigger(switchDirection.title, idle.title, "stopRunning");
        stateMachine.addTrigger(switchDirection.title, run.title, "startRunning");
        stateMachine.addTrigger(switchDirection.title, jump.title, "jump");
        stateMachine.addTrigger(idle.title, run.title, "startRunning");
        stateMachine.addTrigger(idle.title, jump.title, "jump");
        stateMachine.addTrigger(jump.title, idle.title, "stopJumping");

        stateMachine.addTrigger(bigRun.title, bigSwitchDirection.title, "switchDirection");
        stateMachine.addTrigger(bigRun.title, bigIdle.title, "stopRunning");
        stateMachine.addTrigger(bigRun.title, bigJump.title, "jump");
        stateMachine.addTrigger(bigSwitchDirection.title, bigIdle.title, "stopRunning");
        stateMachine.addTrigger(bigSwitchDirection.title, bigRun.title, "startRunning");
        stateMachine.addTrigger(bigSwitchDirection.title, bigJump.title, "jump");
        stateMachine.addTrigger(bigIdle.title, bigRun.title, "startRunning");
        stateMachine.addTrigger(bigIdle.title, bigJump.title, "jump");
        stateMachine.addTrigger(bigJump.title, bigIdle.title, "stopJumping");

        stateMachine.addTrigger(fireRun.title, fireSwitchDirection.title, "switchDirection");
        stateMachine.addTrigger(fireRun.title, fireIdle.title, "stopRunning");
        stateMachine.addTrigger(fireRun.title, fireJump.title, "jump");
        stateMachine.addTrigger(fireSwitchDirection.title, fireIdle.title, "stopRunning");
        stateMachine.addTrigger(fireSwitchDirection.title, fireRun.title, "startRunning");
        stateMachine.addTrigger(fireSwitchDirection.title, fireJump.title, "jump");
        stateMachine.addTrigger(fireIdle.title, fireRun.title, "startRunning");
        stateMachine.addTrigger(fireIdle.title, fireJump.title, "jump");
        stateMachine.addTrigger(fireJump.title, fireIdle.title, "stopJumping");

        stateMachine.addTrigger(run.title, bigRun.title, "powerup");
        stateMachine.addTrigger(idle.title, bigIdle.title, "powerup");
        stateMachine.addTrigger(switchDirection.title, bigSwitchDirection.title, "powerup");
        stateMachine.addTrigger(jump.title, bigJump.title, "powerup");
        stateMachine.addTrigger(bigRun.title, fireRun.title, "powerup");
        stateMachine.addTrigger(bigIdle.title, fireIdle.title, "powerup");
        stateMachine.addTrigger(bigSwitchDirection.title, fireSwitchDirection.title, "powerup");
        stateMachine.addTrigger(bigJump.title, fireJump.title, "powerup");

        stateMachine.addTrigger(bigRun.title, run.title, "damage");
        stateMachine.addTrigger(bigIdle.title, idle.title, "damage");
        stateMachine.addTrigger(bigSwitchDirection.title, switchDirection.title, "damage");
        stateMachine.addTrigger(bigJump.title, jump.title, "damage");
        stateMachine.addTrigger(fireRun.title, bigRun.title, "damage");
        stateMachine.addTrigger(fireIdle.title, bigIdle.title, "damage");
        stateMachine.addTrigger(fireSwitchDirection.title, bigSwitchDirection.title, "damage");
        stateMachine.addTrigger(fireJump.title, bigJump.title, "damage");

        stateMachine.addTrigger(run.title, die.title, "die");
        stateMachine.addTrigger(switchDirection.title, die.title, "die");
        stateMachine.addTrigger(idle.title, die.title, "die");
        stateMachine.addTrigger(jump.title, die.title, "die");
        stateMachine.addTrigger(bigRun.title, run.title, "die");
        stateMachine.addTrigger(bigSwitchDirection.title, switchDirection.title, "die");
        stateMachine.addTrigger(bigIdle.title, idle.title, "die");
        stateMachine.addTrigger(bigJump.title, jump.title, "die");
        stateMachine.addTrigger(fireRun.title, bigRun.title, "die");
        stateMachine.addTrigger(fireSwitchDirection.title, bigSwitchDirection.title, "die");
        stateMachine.addTrigger(fireIdle.title, bigIdle.title, "die");
        stateMachine.addTrigger(fireJump.title, bigJump.title, "die");
        mario.addComponent(stateMachine);

        PillBoxCollider pillBoxCollider = new PillBoxCollider();
        pillBoxCollider.setWidth(0.39f);
        pillBoxCollider.setHeight(0.31f);

        RigidBody2D rb = new RigidBody2D();
        rb.setBodyType(BodyType.DYNAMIC);
        rb.setContinuousCollision(false);
        rb.setFixedRotation(true);
        rb.setMass(25.0f);

        mario.addComponent(rb);
        mario.addComponent(pillBoxCollider);
        mario.addComponent(new PlayerController());

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

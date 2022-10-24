package jade;

import org.joml.Vector2f;

import components.PlayerController;
import components.Sprite;
import components.SpriteRenderer;
import components.SpriteSheet;
import components.animation.AnimationState;
import components.animation.StateMachine;
import components.game.BlockCoin;
import components.game.Flower;
import components.game.GoombaAI;
import components.game.Ground;
import components.game.MushroomAI;
import components.game.QuestionBlock;
import physics2d.components.Box2DCollider;
import physics2d.components.CircleCollider;
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

        AnimationState flicker = new AnimationState("Flicker");
        float defaultFrameTime = 0.23f;
        flicker.addFrame(items.getSprite(0), 0.57f);
        flicker.addFrame(items.getSprite(1), defaultFrameTime);
        flicker.addFrame(items.getSprite(2), defaultFrameTime);
        flicker.setLoop(true);

        AnimationState inActive = new AnimationState("Inactive");
        inActive.addFrame(items.getSprite(3), 0.1f);
        inActive.setLoop(false);

        StateMachine stateMachine = new StateMachine();
        stateMachine.addState(flicker);
        stateMachine.addState(inActive);
        stateMachine.addTrigger(flicker.title, inActive.title, "setInactive");
        stateMachine.setDefaultState(flicker.title);

        questionBlock.addComponent(stateMachine);
        questionBlock.addComponent(new QuestionBlock());

        RigidBody2D rigidBody2D = new RigidBody2D();
        rigidBody2D.setBodyType(BodyType.STATIC);
        questionBlock.addComponent(rigidBody2D);

        Box2DCollider box2DCollider = new Box2DCollider();
        box2DCollider.setHalfSize(new Vector2f(0.25f, 0.25f));
        questionBlock.addComponent(box2DCollider);

        questionBlock.addComponent(new Ground());

        return questionBlock;
    }

    public static GameObject generateBlockCoin() {
        SpriteSheet items = AssetPool.getSpriteSheet("assets/images/spritesheets/items.png");
        GameObject coin = generateSpriteObject(items.getSprite(0), 0.25f, 0.25f);

        AnimationState coinFlip = new AnimationState("CoinFlip");
        float defaultFrameTime = 0.23f;
        coinFlip.addFrame(items.getSprite(7), 0.57f);
        coinFlip.addFrame(items.getSprite(8), defaultFrameTime);
        coinFlip.addFrame(items.getSprite(9), defaultFrameTime);
        coinFlip.setLoop(true);

        StateMachine stateMachine = new StateMachine();
        stateMachine.addState(coinFlip);
        stateMachine.setDefaultState(coinFlip.title);

        coin.addComponent(stateMachine);
        coin.addComponent(new BlockCoin());

        return coin;
    }

    public static GameObject generateGoomba() {
        SpriteSheet sprite = AssetPool.getSpriteSheet("assets/images/spritesheets/spritesheet.png");
        GameObject goomba = generateSpriteObject(sprite.getSprite(14), 0.25f, 0.25f);

        AnimationState walk = new AnimationState("Walk");
        float defaultFrameTime = 0.23f;
        walk.addFrame(sprite.getSprite(14), defaultFrameTime);
        walk.addFrame(sprite.getSprite(15), defaultFrameTime);
        walk.setLoop(true);

        AnimationState squashed = new AnimationState("Squashed");
        squashed.addFrame(sprite.getSprite(16), 0.1f);
        squashed.setLoop(false);

        StateMachine stateMachine = new StateMachine();
        stateMachine.addState(walk);
        stateMachine.addState(squashed);
        stateMachine.addTrigger(walk.title, squashed.title, "squashMe");
        stateMachine.setDefaultState(walk.title);
        goomba.addComponent(stateMachine);

        RigidBody2D rigidBody2D = new RigidBody2D();
        rigidBody2D.setBodyType(BodyType.DYNAMIC);
        rigidBody2D.setFixedRotation(true);
        rigidBody2D.setMass(0.1f);
        goomba.addComponent(rigidBody2D);

        CircleCollider circleCollider = new CircleCollider();
        circleCollider.setRadius(0.12f);
        goomba.addComponent(circleCollider);

        goomba.addComponent(new GoombaAI());

        return goomba;
    }

    public static GameObject generateMushroom() {
        SpriteSheet items = AssetPool.getSpriteSheet("assets/images/spritesheets/items.png");
        GameObject mushroom = generateSpriteObject(items.getSprite(10), 0.25f, 0.25f);

        RigidBody2D rigidBody2D = new RigidBody2D();
        rigidBody2D.setBodyType(BodyType.DYNAMIC);
        rigidBody2D.setFixedRotation(true);
        rigidBody2D.setContinuousCollision(false);
        mushroom.addComponent(rigidBody2D);

        CircleCollider circleCollider = new CircleCollider();
        circleCollider.setRadius(0.14f);
        mushroom.addComponent(circleCollider);

        mushroom.addComponent(new MushroomAI());

        return mushroom;
    }

    public static GameObject generateFlower() {
        SpriteSheet items = AssetPool.getSpriteSheet("assets/images/spritesheets/items.png");
        GameObject flower = generateSpriteObject(items.getSprite(20), 0.25f, 0.25f);

        RigidBody2D rigidBody2D = new RigidBody2D();
        rigidBody2D.setBodyType(BodyType.STATIC);
        rigidBody2D.setFixedRotation(true);
        rigidBody2D.setContinuousCollision(false);
        flower.addComponent(rigidBody2D);

        CircleCollider circleCollider = new CircleCollider();
        circleCollider.setRadius(0.14f);
        flower.addComponent(circleCollider);

        flower.addComponent(new Flower());

        return flower;
    }
}

package components.game;

import components.PlayerController;
import components.animation.StateMachine;
import jade.GameObject;
import jade.Prefabs;
import jade.Window;

public class QuestionBlock extends Block {
    private enum BlockType {
        Coin,
        PowerUp,
        Invincibility
    }

    private BlockType blockType = BlockType.Coin;

    @Override
    void playHit(PlayerController playerController) {
        switch (blockType) {
            case Coin:
                doCoin(playerController);
                break;
            case PowerUp:
                doPowerUp(playerController);
                break;
            case Invincibility:
                doInvincibility(playerController);
                break;
        }

        gameObject.getComponent(StateMachine.class).ifPresent(stateMachine -> {
            stateMachine.trigger("setInactive");
            setInactive();
        });
    }

    private void doCoin(PlayerController playerController) {
        GameObject coin = Prefabs.generateBlockCoin();
        coin.transform.position.set(gameObject.transform.position).add(0, 0.25f);
        Window.get().getCurrentScene().addGameObject(coin);
    }

    private void doPowerUp(PlayerController playerController) {

    }

    private void doInvincibility(PlayerController playerController) {
    }
}

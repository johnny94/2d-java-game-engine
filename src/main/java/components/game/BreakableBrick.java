package components.game;

import components.PlayerController;
import util.AssetPool;

public class BreakableBrick extends Block {
    @Override
    void playHit(PlayerController playerController) {
        if (!playerController.isSmall()) {
            AssetPool.getSound("assets/sounds/break_block.ogg").play();
            gameObject.destroy();
        }
    }
}

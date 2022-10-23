package components.game;

import components.Component;
import util.AssetPool;

public class BlockCoin extends Component {
    private float topY;
    private float coinSpeed = 1.4f;

    @Override
    public void start() {
        topY = gameObject.transform.position.y + 0.5f;
        AssetPool.getSound("assets/sounds/coin.ogg").play();
    }

    @Override
    public void update(float deltaTime) {
        if (gameObject.transform.position.y < topY) {
            gameObject.transform.position.y += coinSpeed * deltaTime;
            gameObject.transform.scale.x -= (0.5f * deltaTime) % -1.0f;
        } else {
            gameObject.destroy();
        }
    }
}

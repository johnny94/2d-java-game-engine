package components;

import org.joml.Vector4f;

import jade.Camera;
import jade.GameObject;
import jade.Window;

public class GameCamera extends Component {
    private static final Vector4f skyColor = new Vector4f(92, 148, 252, 255).div(255f);
    private static final Vector4f undergroundColor = new Vector4f(0, 0, 0, 1);

    private transient GameObject player;
    private transient Camera gameCamera;
    private transient float highestX = Float.MIN_VALUE;
    private transient float undergroundYLevel = 0.0f;
    private transient float cameraBuffer = 1.5f;
    private transient float playerBuffer = 0.25f;

    public GameCamera(Camera camera) {
        this.gameCamera = camera;
    }

    @Override
    public void start() {
        this.player = Window.get().getCurrentScene().getGameObjectWith(PlayerController.class).get();

        this.gameCamera.clearColor.set(skyColor);
        this.undergroundYLevel = this.gameCamera.position.y -
                                 this.gameCamera.getProjectionSize().y - this.cameraBuffer;
    }

    @Override
    public void update(float deltaTime) {
        if (!player.getComponent(PlayerController.class).get().hasWon()) {
            this.gameCamera.position.x = Math.max(player.transform.position.x - 2.5f, highestX);
            highestX = Math.max(highestX, gameCamera.position.x);

            if (player.transform.position.y < -playerBuffer) {
                this.gameCamera.position.y = undergroundYLevel;
                this.gameCamera.clearColor.set(undergroundColor);
            } else if (player.transform.position.y >= 0) {
                this.gameCamera.position.y = 0;
                this.gameCamera.clearColor.set(skyColor);
            }
        }
    }
}

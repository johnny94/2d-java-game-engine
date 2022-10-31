package components.game;

import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;

import components.Component;
import components.PlayerController;
import jade.GameObject;

public class Flagpole extends Component {
    private boolean isTop;

    public Flagpole(boolean isTop) {
        this.isTop = isTop;
    }

    @Override
    public void beginCollision(GameObject collidingObject, Contact contact, Vector2f hitNormal) {
        collidingObject.getComponent(PlayerController.class).ifPresent(playerController -> {
            playerController.playWinAnimation(this.gameObject);
        });
    }
}

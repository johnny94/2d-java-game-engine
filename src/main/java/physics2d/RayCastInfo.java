package physics2d;

import org.jbox2d.callbacks.RayCastCallback;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Fixture;
import org.joml.Vector2f;

import jade.GameObject;

public class RayCastInfo implements RayCastCallback {

    public Fixture fixture;
    public Vector2f point = new Vector2f();
    public Vector2f normal = new Vector2f();
    public float fraction;
    public boolean hit;
    public GameObject hitObject;
    private GameObject requestingObject;


    public RayCastInfo(GameObject requestingObject) {
        this.requestingObject = requestingObject;
    }

    // Can check Box2d doc for more information
    @Override
    public float reportFixture(Fixture fixture, Vec2 hitPoint, Vec2 normal, float fraction) {
        if (fixture.getUserData() == requestingObject) {
            return 1;
        }

        this.fixture = fixture;
        this.point = new Vector2f(hitPoint.x, hitPoint.y);
        this.normal = new Vector2f(normal.x, normal.y);
        this.fraction = fraction;
        this.hit = fraction != 0;
        this.hitObject = (GameObject) fixture.getUserData();

        return fraction;
    }
}

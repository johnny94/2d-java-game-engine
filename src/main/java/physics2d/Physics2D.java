package physics2d;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;

public class Physics2D {
    private Vec2 gravity = new Vec2(0, -10.0f);
    private World world = new World(gravity);

    private float physicsTime = 0.0f;
    private float physicsTimeStep = 1.0f / 60.0f;
    private int velocityIteration = 8;
    private int positionIteration = 3;

    public void update(float deltaTime) {
        physicsTime += deltaTime;

        // Make sure the world updates every 1/60 sec
        if (physicsTime > 0) {
            physicsTime -= physicsTimeStep;
            world.step(physicsTimeStep, velocityIteration, positionIteration);
        }
    }
}

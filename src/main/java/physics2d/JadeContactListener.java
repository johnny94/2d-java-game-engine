package physics2d;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.WorldManifold;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;

import components.Component;
import jade.GameObject;

public class JadeContactListener implements ContactListener {
    @Override
    public void beginContact(Contact contact) {
        GameObject gameObjectA = (GameObject) contact.getFixtureA().getUserData();
        GameObject gameObjectB = (GameObject) contact.getFixtureB().getUserData();

        WorldManifold worldManifold = new WorldManifold();
        contact.getWorldManifold(worldManifold);
        Vector2f aNormal = new Vector2f(worldManifold.normal.x, worldManifold.normal.y);
        Vector2f bNormal = new Vector2f(aNormal).negate();

        for (Component component : gameObjectA.getComponents()) {
            component.beginCollision(gameObjectB, contact, aNormal);
        }

        for (Component component : gameObjectB.getComponents()) {
            component.beginCollision(gameObjectA, contact, bNormal);
        }
    }

    @Override
    public void endContact(Contact contact) {
        GameObject gameObjectA = (GameObject) contact.getFixtureA().getUserData();
        GameObject gameObjectB = (GameObject) contact.getFixtureB().getUserData();

        WorldManifold worldManifold = new WorldManifold();
        contact.getWorldManifold(worldManifold);
        Vector2f aNormal = new Vector2f(worldManifold.normal.x, worldManifold.normal.y);
        Vector2f bNormal = new Vector2f(aNormal).negate();

        for (Component component : gameObjectA.getComponents()) {
            component.endCollision(gameObjectB, contact, aNormal);
        }

        for (Component component : gameObjectB.getComponents()) {
            component.endCollision(gameObjectA, contact, bNormal);
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold manifold) {
        GameObject gameObjectA = (GameObject) contact.getFixtureA().getUserData();
        GameObject gameObjectB = (GameObject) contact.getFixtureB().getUserData();

        WorldManifold worldManifold = new WorldManifold();
        contact.getWorldManifold(worldManifold);
        Vector2f aNormal = new Vector2f(worldManifold.normal.x, worldManifold.normal.y);
        Vector2f bNormal = new Vector2f(aNormal).negate();

        for (Component component : gameObjectA.getComponents()) {
            component.preSolve(gameObjectB, contact, aNormal);
        }

        for (Component component : gameObjectB.getComponents()) {
            component.preSolve(gameObjectA, contact, bNormal);
        }
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse contactImpulse) {
        GameObject gameObjectA = (GameObject) contact.getFixtureA().getUserData();
        GameObject gameObjectB = (GameObject) contact.getFixtureB().getUserData();

        WorldManifold worldManifold = new WorldManifold();
        contact.getWorldManifold(worldManifold);
        Vector2f aNormal = new Vector2f(worldManifold.normal.x, worldManifold.normal.y);
        Vector2f bNormal = new Vector2f(aNormal).negate();

        for (Component component : gameObjectA.getComponents()) {
            component.postSolve(gameObjectB, contact, aNormal);
        }

        for (Component component : gameObjectB.getComponents()) {
            component.postSolve(gameObjectA, contact, bNormal);
        }
    }
}

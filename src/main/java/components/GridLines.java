package components;

import org.joml.Vector2f;
import org.joml.Vector3f;

import jade.Window;
import renderer.DebugDraw;
import util.Settings;

public class GridLines extends Component {
    private static final Vector3f defaultColor = new Vector3f(0.2f, 0.2f, 0.2f);

    @Override
    public void update(double deltaTime) {
        Vector2f cameraPos = Window.get().getCurrentScene().getCamera().position;
        Vector2f projectionSize = Window.get().getCurrentScene().getCamera().getProjectionSize();

        // -1 means we will move the whole grid bottom left 1 grid unit
        int firstX = ((int)(cameraPos.x / Settings.GRID_WIDTH) - 1) * Settings.GRID_WIDTH;
        int firstY = ((int)(cameraPos.y / Settings.GRID_HEIGHT) - 1) * Settings.GRID_HEIGHT;

        int numVtLines = (int)projectionSize.x / Settings.GRID_WIDTH + 2;
        int numHzLines = (int)projectionSize.y / Settings.GRID_HEIGHT + 2;

        int width = (int)projectionSize.x + Settings.GRID_WIDTH * 2;
        int height = (int)projectionSize.y + + Settings.GRID_HEIGHT * 2;

        int maxLines = Math.max(numVtLines, numHzLines);
        for (int i = 0; i < maxLines; i++) {
            int x = firstX + Settings.GRID_WIDTH * i;
            int y = firstY + Settings.GRID_HEIGHT * i;

            if (i < numVtLines) {
                DebugDraw.drawLine(new Vector2f(x, firstY), new Vector2f(x, firstY + height), defaultColor);
            }

            if (i < numHzLines) {
                DebugDraw.drawLine(new Vector2f(firstX, y), new Vector2f(firstX + width, y), defaultColor);
            }
        }
    }
}

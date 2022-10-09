package components;

import org.joml.Vector2f;
import org.joml.Vector3f;

import jade.Camera;
import jade.Window;
import renderer.DebugDraw;
import util.Settings;

public class GridLines extends Component {
    private static final Vector3f defaultColor = new Vector3f(0.2f, 0.2f, 0.2f);

    @Override
    public void editorUpdate(float deltaTime) {
        Camera camera = Window.get().getCurrentScene().getCamera();
        Vector2f cameraPos = camera.position;
        Vector2f projectionSize = camera.getProjectionSize();

        // -1 means we will move the whole grid bottom left 1 grid unit
        float firstX = ((int)(cameraPos.x / Settings.GRID_WIDTH) - 1) * Settings.GRID_WIDTH;
        float firstY = ((int)(cameraPos.y / Settings.GRID_HEIGHT) - 1) * Settings.GRID_HEIGHT;

        int numVtLines = (int)(projectionSize.x * camera.getZoom() / Settings.GRID_WIDTH) + 2;
        int numHzLines = (int)(projectionSize.y * camera.getZoom() / Settings.GRID_HEIGHT) + 2;

        float width = (int)(projectionSize.x * camera.getZoom()) + Settings.GRID_WIDTH * 2;
        float height = (int)(projectionSize.y * camera.getZoom()) + Settings.GRID_HEIGHT * 2;

        int maxLines = Math.max(numVtLines, numHzLines);
        for (int i = 0; i < maxLines; i++) {
            float x = firstX + Settings.GRID_WIDTH * i;
            float y = firstY + Settings.GRID_HEIGHT * i;

            if (i < numVtLines) {
                DebugDraw.drawLine(new Vector2f(x, firstY), new Vector2f(x, firstY + height), defaultColor);
            }

            if (i < numHzLines) {
                DebugDraw.drawLine(new Vector2f(firstX, y), new Vector2f(firstX + width, y), defaultColor);
            }
        }
    }
}

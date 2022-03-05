package components;

public class SpriteRenderer extends Component {

    private boolean isFirstTime = true;

    @Override
    public void start() {
        System.out.println(getClass().getSimpleName() + "component is starting...");
    }

    @Override
    public void update(double deltaTime) {
        if (isFirstTime) {
            System.out.println(getClass().getSimpleName() + "component is updating...");
            isFirstTime = false;
        }
    }
}

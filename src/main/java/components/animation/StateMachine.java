package components.animation;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import components.Component;
import components.SpriteRenderer;
import imgui.ImGui;
import imgui.type.ImBoolean;
import imgui.type.ImString;

public class StateMachine extends Component {
    private class StateTrigger {
        public String state;
        public String trigger;

        StateTrigger(String state, String trigger) {
            this.state = state;
            this.trigger = trigger;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) { return true; }
            if (o == null || getClass() != o.getClass()) { return false; }
            final StateTrigger that = (StateTrigger) o;
            return Objects.equals(state, that.state) && Objects.equals(trigger, that.trigger);
        }

        @Override
        public int hashCode() {
            return Objects.hash(state, trigger);
        }
    }

    private Map<StateTrigger, String> stateTransfers = new HashMap<>();
    private Map<String, AnimationState> states = new HashMap<>();
    private transient AnimationState currentState;
    private String defaultStateTitle = "";

    // I think this kind of stuff should be hidden in a lower level
    public void refreshTexture() {
        states.forEach((k, v) -> v.refreshTextures());
    }

    public void addTrigger(String from, String to, String onTrigger) {
        // I think this is not good.
        // It should use onTrigger as kay, (from, to) as value
        stateTransfers.put(new StateTrigger(from, onTrigger), to);
    }

    public void addState(AnimationState state) {
        states.put(state.title, state);
    }

    public void setDefaultState(String title) {
        AnimationState state = states.get(title);
        if (state != null) {
            defaultStateTitle = title;
            if (currentState == null) {
                this.currentState = state;
            }
        } else {
            System.out.println("Unable to find state: " + title);
        }
    }

    public void trigger(String trigger) {
        StateTrigger stateTrigger = new StateTrigger(currentState.title, trigger);
        String to = stateTransfers.get(stateTrigger);
        if (to != null) {
            AnimationState state = states.get(to);
            if (state != null) {
                this.currentState = state;
            }
        }

        System.out.println("Unable to find the trigger: " + trigger);
    }

    @Override
    public void start() {
        this.currentState = states.get(defaultStateTitle);
    }

    @Override
    public void update(float deltaTime) {
        if (currentState != null) {
            currentState.update(deltaTime);
            Optional<SpriteRenderer> maybeRenderer = gameObject.getComponent(SpriteRenderer.class);
            if (maybeRenderer.isPresent()) {
                maybeRenderer.get().setSprite(currentState.getCurrentSprite());
            }
        }
    }

    @Override
    public void editorUpdate(float deltaTime) {
        if (currentState != null) {
            currentState.update(deltaTime);
            Optional<SpriteRenderer> maybeRenderer = gameObject.getComponent(SpriteRenderer.class);
            if (maybeRenderer.isPresent()) {
                maybeRenderer.get().setSprite(currentState.getCurrentSprite());
            }
        }
    }

    @Override
    public void imGui() {
        for (Map.Entry<String, AnimationState> item : states.entrySet()) {
            AnimationState animationState = item.getValue();

            ImString title = new ImString(animationState.title);
            ImGui.inputText("State: ", title);
            animationState.title = title.get();

            ImBoolean doesLoop = new ImBoolean(animationState.doesLoop);
            ImGui.checkbox("Does Loop?", doesLoop);
            animationState.doesLoop = doesLoop.get();

            for (int i = 0; i < animationState.animationFrames.size(); i++) {
                Frame frame = animationState.animationFrames.get(i);
                float[] val = new float[1];
                val[0] = frame.frameTime;
                ImGui.dragFloat(String.format("Frame(%d) Time: ", i), val, 0.01f);
                frame.frameTime = val[0];
            }
        }
    }
}

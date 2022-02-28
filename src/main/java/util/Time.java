package util;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

public final class Time {
    private static final double startedTime = glfwGetTime();

    private Time() {}

    public static double getTime() {
        return glfwGetTime() - startedTime;
    }
}

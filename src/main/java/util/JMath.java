package util;

import org.joml.Vector2f;

public final class JMath {
    private JMath() {}

    public static void rotate(Vector2f vec, float angleDeg, Vector2f origin) {
        float x = vec.x - origin.x;
        float y = vec.y - origin.y;

        float cos = (float) Math.cos(Math.toRadians(angleDeg));
        float sin = (float) Math.sin(Math.toRadians(angleDeg));

        float xPrime = (x * cos) - (y * sin);
        float yPrime = (x * sin) + (y * cos);

        xPrime += origin.x;
        yPrime += origin.y;

        vec.x = xPrime;
        vec.y = yPrime;
    }

    public static <T extends Number & Comparable<T>> T clamp(T value, T minValue, T maxValue) {
        if (value.compareTo(minValue) < 0) {
            return minValue;
        }

        if (value.compareTo(maxValue) > 0) {
            return maxValue;
        }

        return value;
    }
}

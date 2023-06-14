package ca.fxco.moreculling.utils;

import net.minecraft.util.math.Vec3d;

public class MathUtils {

    public static final double ONE_SIGN_ROTATION = 0.3926991D; // 22.5 degrees
    public static final double PI2 = Math.PI * 2; // 360 degrees

    // Angle in Radians
    public static boolean isBehindLine(double angle, Vec3d linePos, Vec3d pos) {
        double rad = Math.atan2(pos.getZ() - linePos.getZ(), pos.getX() - linePos.getX());
        rad += Math.ceil(-rad / PI2) * PI2;
        return rad - angle > Math.PI; // Simplified so it's faster for this use case
    }

    // In Radians
    public static boolean isAngleBetween(double angle, double start, double end) {
        end -= start;
        angle -= start;
        return (angle + PI2) % PI2 > (end + PI2) % PI2;
    }
}

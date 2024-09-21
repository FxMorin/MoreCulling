package ca.fxco.moreculling.api.data;

public class QuadBounds {

    private final int minX;
    private final int minY;
    private final int maxX;
    private final int maxY;

    public QuadBounds(int minX, int minY, int maxX, int maxY) {
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
    }

    public int getMinX() {
        return this.minX;
    }

    public int getMinY() {
        return this.minY;
    }

    public int getMaxX() {
        return this.maxX;
    }

    public int getMaxY() {
        return this.maxY;
    }
}

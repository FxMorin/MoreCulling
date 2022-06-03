package ca.fxco.moreculling.patches;

public interface BakedTransparency {

    // When true, block will be treated like a normal block
    default boolean hasTransparency() {
        return true;
    }
}

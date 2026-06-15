package ca.fxco.moreculling.api.renderers;

import net.minecraft.client.renderer.culling.Frustum;

public interface ExtendedLevelExtractor {

    default Frustum moreculling$getFrustum() {
        return null;
    }
}

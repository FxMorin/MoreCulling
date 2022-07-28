package ca.fxco.moreculling.api.model;

import net.minecraft.block.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * BakedOpacity is an interface that should be used on classes that extend BakedModel
 * It allows your custom model to take full advantage of MoreCulling's culling techniques.
 */

public interface BakedOpacity {

    /**
     * States if any of the textures of the model that are on a face of the block are translucent.
     * If they are not translucent, MoreCulling will be able to provide faster culling for its states.
     *
     * Some baked models will require a blockstate in order to provide more accurate translucency checks,
     * usually if no blockstate is passed it will work fine, although some baked models will always return true.
     * If possible, the default state of the block will be used.
     */
    default boolean hasTextureTranslucency(@Nullable BlockState state) {
        return true;
    }

    /**
     * This just acts like hasTextureTranslucency(null)
     * it should not be used anymore unless you are sure that your model is not a container or wrapper with a container
     * for multiple block models. Since they will always return true is null is passed.
     */
    default boolean hasTextureTranslucency() {
        return hasTextureTranslucency(null);
    }

    /**
     * When called this method will reset the translucency cache of the model.
     * This should be called if the texture of the model is ever changed!
     */
    default void resetTranslucencyCache() {}
}

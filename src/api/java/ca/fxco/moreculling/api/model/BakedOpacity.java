package ca.fxco.moreculling.api.model;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * BakedOpacity is an interface that should be used on classes that extend BakedModel
 * It allows your custom model to take full advantage of MoreCulling's culling techniques.
 * @since 0.3.0
 */

public interface BakedOpacity {

    /**
     * States if any of the textures of the model that are on a face of the block are translucent.
     * If they are not translucent, MoreCulling will be able to provide faster culling for its states.
     *
     * Some baked models will require a blockstate in order to provide more accurate translucency checks,
     * usually if no blockstate is passed it will work fine, although some baked models will always return true.
     * If possible, the default state of the block will be used.
     * @since 0.8.0
     */
    default boolean hasTextureTranslucency(@Nullable BlockState state) {
        return true;
    }

    /**
     * This just acts like hasTextureTranslucency(null)
     * it should not be used anymore unless you are sure that your model is not a container or wrapper with a container
     * for multiple block models. Since they will always return true is null is passed.
     * @since 0.3.0
     */
    default boolean hasTextureTranslucency() {
        return hasTextureTranslucency(null);
    }

    /**
     * When called this method will reset the translucency cache of the model.
     * This should be called if the texture of the model is ever changed!
     * @since 0.7.0
     */
    default void resetTranslucencyCache() {}

    /**
     * This method will return a list of all models contained within this model.
     * Currently, this is only used for debugging and development. Although it may be used in the future!
     * @since 0.8.0
     */
    default @Nullable List<BakedModel> getModels() {
        return null;
    }
}

package ca.fxco.moreculling.utils;

import ca.fxco.moreculling.MoreCulling;
import ca.fxco.moreculling.api.model.BakedOpacity;
import ca.fxco.moreculling.mixin.accessors.BlockModelShaperAccessor;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;

import static ca.fxco.moreculling.MoreCulling.blockRenderManager;
import static ca.fxco.moreculling.utils.CompatUtils.IS_MODERNFIX_LOADED;

public class CacheUtils {

    /**
     * Resets all cache used for MoreCulling
     */
    public static void resetAllCache() {
        // Not really needed as ModernFix automatically caches and releases models, which is good enough.
        if (IS_MODERNFIX_LOADED) {
            return;
        }
        // Reset all model translucency cache
        Map<BlockState, BakedModel> allModels = ((BlockModelShaperAccessor) blockRenderManager.getBlockModelShaper()).getModels();
        allModels.forEach((state, model) -> {
            if (!state.canOcclude()) {
                ((BakedOpacity) model).moreculling$resetTranslucencyCache(state);
            }
        });
        //TODO: Reset quad cache
        MoreCulling.LOGGER.info(allModels.size() + " cache(s) where cleared!");
    }
}

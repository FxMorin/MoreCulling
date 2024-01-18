package ca.fxco.moreculling.utils;

import ca.fxco.moreculling.MoreCulling;
import ca.fxco.moreculling.api.model.BakedOpacity;
import ca.fxco.moreculling.mixin.accessors.BlockModelsAccessor;
import net.minecraft.client.render.model.BakedModel;

import java.util.Collection;

import static ca.fxco.moreculling.MoreCulling.blockRenderManager;

public class CacheUtils {

    /**
     * Resets all cache used for MoreCulling
     */
    public static void resetAllCache() {
        // Reset all model translucency cache
        Collection<BakedModel> allModels = ((BlockModelsAccessor) blockRenderManager.getModels()).getModels().values();
        for (BakedModel model : allModels) {
            ((BakedOpacity) model).resetTranslucencyCache();
        }
        //TODO: Reset quad cache
        MoreCulling.LOGGER.info(allModels.size() + " cache(s) where cleared!");
    }
}

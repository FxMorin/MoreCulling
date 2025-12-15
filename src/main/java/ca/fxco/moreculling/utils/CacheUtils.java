package ca.fxco.moreculling.utils;

import ca.fxco.moreculling.MoreCulling;
import ca.fxco.moreculling.api.model.BakedOpacity;
import ca.fxco.moreculling.mixin.accessors.BlockModelsAccessor;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import org.embeddedt.modernfix.core.ModernFixMixinPlugin;

import java.util.Map;

import static ca.fxco.moreculling.MoreCulling.blockRenderManager;
import static ca.fxco.moreculling.utils.CompatUtils.IS_MODERNFIX_LOADED;

public class CacheUtils {

    /**
     * Resets all cache used for MoreCulling
     */
    public static void resetAllCache() {
        // Not really needed as ModernFix automatically caches and releases models, which is good enough.
        if (IS_MODERNFIX_LOADED && ModernFixMixinPlugin.instance.isOptionEnabled("perf.dynamic_resources")) {
            return;
        }
        // Reset all model translucency cache

        Map<BlockState, BakedModel> map = ((BlockModelsAccessor) blockRenderManager.getModels()).getModels();
        for (Map.Entry<BlockState, BakedModel> entry : map.entrySet()) {
            if (!entry.getKey().isOpaque()) {
                ((BakedOpacity) entry.getValue()).resetTranslucencyCache(entry.getKey());
            }
        }
        //TODO: Reset quad cache
        MoreCulling.LOGGER.info(map.values().size() + " cache(s) where cleared!");
    }
}

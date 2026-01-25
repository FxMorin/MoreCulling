package ca.fxco.moreculling.utils;

import ca.fxco.moreculling.MoreCulling;
import ca.fxco.moreculling.api.blockstate.StateCullingShapeCache;
import ca.fxco.moreculling.api.model.BakedOpacity;
import ca.fxco.moreculling.mixin.accessors.BlockModelShaperAccessor;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
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
        Block.BLOCK_STATE_REGISTRY.forEach(state -> ((StateCullingShapeCache) state).moreculling$initCustomCullingShape());
        Map<BlockState, BlockStateModel> allModels = ((BlockModelShaperAccessor) blockRenderManager.getBlockModelShaper()).getModels();
        allModels.forEach((state, model) -> {
            if (!state.canOcclude()) {
                ((BakedOpacity) model).moreculling$resetTranslucencyCache(state);
            }
        });
        //TODO: Reset quad cache
        MoreCulling.LOGGER.info(allModels.size() + " cache(s) were cleared!");
    }
}

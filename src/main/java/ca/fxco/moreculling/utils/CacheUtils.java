package ca.fxco.moreculling.utils;

import ca.fxco.moreculling.MoreCulling;
import ca.fxco.moreculling.api.model.BakedOpacity;
import ca.fxco.moreculling.mixin.accessors.BlockModelsAccessor;
import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.function.Supplier;

import static ca.fxco.moreculling.MoreCulling.blockRenderManager;

public class CacheUtils {

    private final static @NotNull Supplier<Object2IntLinkedOpenHashMap<BakedQuad>> BakedQuadColorCacheSupplier = () -> {
        Object2IntLinkedOpenHashMap<BakedQuad> initialBakedQuadColorCache = new Object2IntLinkedOpenHashMap<>(256, 0.25F) {
            @Override
            protected void rehash(int newN) {
            }
        };
        initialBakedQuadColorCache.defaultReturnValue(Integer.MAX_VALUE);
        return initialBakedQuadColorCache;
    };

    /**
     * Should only be used for item frame baked quad colors
     */
    public static ThreadLocal<Object2IntLinkedOpenHashMap<BakedQuad>> BAKED_QUAD_COLOR_CACHE = ImprovedThreadLocal.withInitial(BakedQuadColorCacheSupplier);

    /**
     * Resets all cache used for MoreCulling
     */
    public static void resetAllCache() {
        // ThreadLocal needs to be reset to clear all threads correctly
        BAKED_QUAD_COLOR_CACHE = ImprovedThreadLocal.withInitial(BakedQuadColorCacheSupplier);
        // Reset all model translucency cache
        Collection<BakedModel> allModels = ((BlockModelsAccessor) blockRenderManager.getModels()).getModels().values();
        for (BakedModel model : allModels) {
            ((BakedOpacity) model).resetTranslucencyCache();
        }
        //TODO: Reset quad cache
        MoreCulling.LOGGER.info((allModels.size() + 1) + " cache(s) where cleared!");
    }
}

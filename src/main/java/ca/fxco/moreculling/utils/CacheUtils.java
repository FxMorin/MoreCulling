package ca.fxco.moreculling.utils;

import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import net.minecraft.client.render.model.BakedQuad;

public class CacheUtils {

    /**
     * Should only be used for item frame baked quad colors
     */
    public static final ThreadLocal<Object2IntLinkedOpenHashMap<BakedQuad>> BAKED_QUAD_COLOR_CACHE = ThreadLocal.withInitial(() -> {
        Object2IntLinkedOpenHashMap<BakedQuad> initialBakedQuadColorCache = new Object2IntLinkedOpenHashMap<>(256, 0.25F) {
            @Override
            protected void rehash(int newN) {}
        };
        initialBakedQuadColorCache.defaultReturnValue(Integer.MAX_VALUE);
        return initialBakedQuadColorCache;
    });
}

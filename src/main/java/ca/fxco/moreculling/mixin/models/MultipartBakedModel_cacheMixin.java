package ca.fxco.moreculling.mixin.models;

import ca.fxco.moreculling.api.model.BakedOpacity;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.MultipartBakedModel;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.*;
import java.util.function.Predicate;

@Mixin(MultipartBakedModel.class)
public abstract class MultipartBakedModel_cacheMixin implements BakedOpacity {

    /*
    I'm not the biggest fan of all Multipart models containing a HashMap cache, it's excessive.
    Although I may be able to remove it if I can get the first to-do in BakedModel_extendsMixin working
    Some calls to hasTextureTranslucency() also don't currently have blockstate, they would not work correctly anyways
     */

    @Shadow
    @Final
    private List<Pair<Predicate<BlockState>, BakedModel>> components;

    @Unique
    private final Object2BooleanOpenHashMap<BlockState> stateTranslucencyCache = new Object2BooleanOpenHashMap<>();

    private final BakedModel self = (BakedModel)this;

    @Override
    public boolean hasTextureTranslucency(@Nullable BlockState blockState) {
        if (blockState == null) return true;
        // Don't want to test all possible blockstates so we have it be dynamic with a cache.
        // Which should be better for performance also.
        return stateTranslucencyCache.computeIfAbsent(blockState,(s) -> {
            BlockState state = (BlockState)s;
            for (Pair<Predicate<BlockState>, BakedModel> pair : components) { // For models within multipart model
                BakedModel model = pair.getRight();
                if (model != self && pair.getLeft().test(state)) // If valid state for model
                    return ((BakedOpacity) model).hasTextureTranslucency(state);
            }
            return false;
        });
    }

    @Override
    public void resetTranslucencyCache() {
        stateTranslucencyCache.clear(); // Clear cache
    }
}

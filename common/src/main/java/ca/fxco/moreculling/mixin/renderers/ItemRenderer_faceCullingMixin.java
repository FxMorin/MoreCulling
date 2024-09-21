package ca.fxco.moreculling.mixin.renderers;

import ca.fxco.moreculling.states.ItemRendererStates;
import ca.fxco.moreculling.utils.DirectionUtils;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Mixin(value = ItemRenderer.class, priority = 1100)
public class ItemRenderer_faceCullingMixin {

    // Doesn't work in 1.21, too stupid to figure it out.
    /*@Redirect(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemDisplayContext;firstPerson()Z",
                    ordinal = 0
            )
    )
    private boolean moreculling$skipSlowTransparencyChecks(ItemDisplayContext renderMode) {
        return ItemRendererStates.ITEM_FRAME != null;
    }*/

    @Redirect(
            method = "renderModelLists",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/core/Direction;values()[Lnet/minecraft/core/Direction;"
            )
    )
    private Direction[] moreculling$modifyDirections() {
        return ItemRendererStates.DIRECTIONS == null ? DirectionUtils.DIRECTIONS : ItemRendererStates.DIRECTIONS;
    }

    @WrapOperation(
            method = "renderModelLists",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/resources/model/BakedModel;getQuads(" +
                            "Lnet/minecraft/world/level/block/state/BlockState;" +
                            "Lnet/minecraft/core/Direction;Lnet/minecraft/util/RandomSource;)Ljava/util/List;"
            )
    )
    private List<BakedQuad> moreculling$onlySomeFaces$Vanilla(BakedModel instance, BlockState blockState,
                                                              Direction direction, RandomSource random,
                                                              Operation<List<BakedQuad>> original) {
        if (ItemRendererStates.DIRECTIONS != null) {
            List<BakedQuad> bakedQuads = new ArrayList<>(original.call(instance, blockState, direction, random));
            Iterator<BakedQuad> iterator = bakedQuads.iterator();
            quads:
            while (iterator.hasNext()) {
                BakedQuad bakedQuad = iterator.next();
                Direction face = bakedQuad.getDirection();
                for (Direction dir : ItemRendererStates.DIRECTIONS) {
                    if (face == dir) {
                        continue quads;
                    }
                }
                iterator.remove();
            }
            return bakedQuads;
        }
        return original.call(instance, blockState, direction, random);
    }
}

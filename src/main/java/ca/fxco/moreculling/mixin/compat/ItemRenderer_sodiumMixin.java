package ca.fxco.moreculling.mixin.compat;

import ca.fxco.moreculling.states.ItemRendererStates;
import ca.fxco.moreculling.utils.DirectionUtils;
import com.bawnorton.mixinsquared.TargetHandler;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Restriction(require = @Condition("sodium"))
@Mixin(value = ItemRenderer.class, priority = 1200)
public class ItemRenderer_sodiumMixin {

    // Sodium cancels the entire method... again

    @TargetHandler(
            mixin = "me.jellysquid.mods.sodium.mixin.features.render.model.item.ItemRendererMixin",
            name = "renderModelFast"
    )
    @WrapOperation(
            method = "@MixinSquared:Handler",
            at = @At(
                    value = "FIELD",
                    target = "Lme/jellysquid/mods/sodium/client/util/DirectionUtil;" +
                            "ALL_DIRECTIONS:[Lnet/minecraft/util/math/Direction;",
                    opcode = Opcodes.GETSTATIC
            )
    )
    private Direction[] moreculling$modifyDirections$Sodium(Operation<Direction[]> original) {
        return ItemRendererStates.DIRECTIONS == null ? original.call() : ItemRendererStates.DIRECTIONS;
    }

    @TargetHandler(
            mixin = "me.jellysquid.mods.sodium.mixin.features.render.model.item.ItemRendererMixin",
            name = "renderModelFast"
    )
    @WrapOperation(
            method = "@MixinSquared:Handler",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/model/BakedModel;getQuads(" +
                            "Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/Direction;" +
                            "Lnet/minecraft/util/math/random/Random;)Ljava/util/List;"
            )
    )
    private List<BakedQuad> moreculling$onlySomeFaces$Sodium(BakedModel instance, BlockState blockState,
                                                             Direction direction, Random random,
                                                             Operation<List<BakedQuad>> original) {
        if (ItemRendererStates.DIRECTIONS != null) {
            List<BakedQuad> bakedQuads = new ArrayList<>(original.call(instance, blockState, direction, random));
            Iterator<BakedQuad> iterator = bakedQuads.iterator();
            quads:
            while (iterator.hasNext()) {
                BakedQuad bakedQuad = iterator.next();
                Direction face = bakedQuad.getFace();
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

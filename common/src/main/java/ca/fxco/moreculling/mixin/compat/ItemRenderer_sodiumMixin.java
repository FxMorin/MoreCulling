package ca.fxco.moreculling.mixin.compat;

import ca.fxco.moreculling.states.ItemRendererStates;
import ca.fxco.moreculling.utils.DirectionUtils;
import com.bawnorton.mixinsquared.TargetHandler;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
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

@Restriction(require = @Condition("sodium"))
@Mixin(value = ItemRenderer.class, priority = 1200)
public class ItemRenderer_sodiumMixin {

    @TargetHandler(
            mixin = "net.caffeinemc.mods.sodium.mixin.features.render.model.item.ItemRendererMixin",
            name = "renderModelFastDirections"
    )
    @Redirect(
            method = "@MixinSquared:Handler",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/caffeinemc/mods/sodium/client/util/DirectionUtil;" +
                            "ALL_DIRECTIONS:[Lnet/minecraft/core/Direction;",
                    opcode = 178 //Opcodes.GETSTATIC
            )
    )
    private static Direction[] moreculling$modifyDirections$Sodium() {
        return ItemRendererStates.DIRECTIONS == null ? DirectionUtils.DIRECTIONS : ItemRendererStates.DIRECTIONS;
    }
}

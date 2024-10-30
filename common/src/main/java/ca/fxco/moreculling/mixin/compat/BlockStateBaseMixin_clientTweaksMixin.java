package ca.fxco.moreculling.mixin.compat;

import com.bawnorton.mixinsquared.TargetHandler;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Restriction(require = @Condition("clienttweaks"))
@Mixin(value = BlockBehaviour.BlockStateBase.class, priority = 1200)
public abstract class BlockStateBaseMixin_clientTweaksMixin {
    @Shadow private VoxelShape occlusionShape;

    @TargetHandler(
            mixin = "net.blay09.mods.clienttweaks.mixin.BlockStateBaseMixin",
            name = "getShape"
    )
    @Inject(
            method = "@MixinSquared:Handler",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/phys/shapes/Shapes;create(Lnet/minecraft/world/phys/AABB;)Lnet/minecraft/world/phys/shapes/VoxelShape;"
            )
    )
    private void moreculling$cacheOriginalShape$ClientTweaks(BlockGetter blockGetter,
                                                             BlockPos pos, CollisionContext context,
                                                             CallbackInfoReturnable<VoxelShape> callbackInfo,
                                                             CallbackInfo ci) {
        occlusionShape = callbackInfo.getReturnValue();
    }
}

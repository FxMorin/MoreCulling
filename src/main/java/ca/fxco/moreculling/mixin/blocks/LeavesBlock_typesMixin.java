package ca.fxco.moreculling.mixin.blocks;

import ca.fxco.moreculling.MoreCulling;
import ca.fxco.moreculling.api.block.MoreBlockCulling;
import ca.fxco.moreculling.config.option.LeavesCullingMode;
import ca.fxco.moreculling.utils.CullingUtils;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Optional;

@Restriction(conflict = @Condition("cull-less-leaves"))
@Mixin(value = LeavesBlock.class, priority = 1220)
public class LeavesBlock_typesMixin implements MoreBlockCulling {

    @Shadow
    @Final
    public static IntProperty DISTANCE;

    @Override
    public boolean usesCustomShouldDrawFace(BlockState state) {
        return MoreCulling.CONFIG.leavesCullingMode != LeavesCullingMode.DEFAULT; //Fast will skip this call
    }

    @Override
    public Optional<Boolean> customShouldDrawFace(BlockView view, BlockState thisState, BlockState sideState,
                                                  BlockPos thisPos, BlockPos sidePos, Direction side) {
        return switch (MoreCulling.CONFIG.leavesCullingMode) {
            case STATE -> Optional.of(!(sideState.getBlock() instanceof LeavesBlock && sideState.get(DISTANCE) % 3 != 1));
            case CHECK -> CullingUtils.shouldDrawFaceCheck(view, sideState, thisPos, sidePos, side);
            case DEPTH -> CullingUtils.shouldDrawFaceDepth(view, sideState, sidePos, side);
            default -> Optional.empty();
        };
    }
}

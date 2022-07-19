package ca.fxco.moreculling.mixin.blocks;

import ca.fxco.moreculling.MoreCulling;
import ca.fxco.moreculling.api.block.MoreBlockCulling;
import ca.fxco.moreculling.config.option.LeavesCullingMode;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Optional;

import static net.minecraft.block.LeavesBlock.DISTANCE;

@Restriction(conflict = @Condition("cull-less-leaves"))
@Mixin(value = LeavesBlock.class, priority = 1220)
public class LeavesBlock_typesMixin implements MoreBlockCulling {

    @Shadow @Final public static IntProperty DISTANCE;
    private static final Direction[] DIRECTIONS = Direction.values();

    @Override
    public boolean usesCustomShouldDrawFace(BlockState state) {
        return MoreCulling.CONFIG.leavesCullingMode != LeavesCullingMode.DEFAULT; //Fast will skip this call
    }

    @Override
    public Optional<Boolean> customShouldDrawFace(BlockView view, BlockState thisState, BlockState sideState, BlockPos thisPos, BlockPos sidePos, Direction side) {
        return switch (MoreCulling.CONFIG.leavesCullingMode) {
            case STATE -> Optional.of(!(sideState.getBlock() instanceof LeavesBlock && sideState.get(DISTANCE) % 3 != 1));
            case CHECK -> shouldDrawFaceCheck(view, sideState, thisPos, side);
            case DEPTH -> shouldDrawFaceDepth(view, sideState, sidePos, side);
            default -> Optional.empty();
        };
    }

    private Optional<Boolean> shouldDrawFaceCheck(BlockView view, BlockState sideState, BlockPos thisPos, Direction side) {
        if (sideState.isOpaque() || sideState.getBlock() instanceof LeavesBlock) {
            boolean isSurrounded = true;
            for (Direction dir : DIRECTIONS) {
                if (dir != side) {
                    BlockState state = view.getBlockState(thisPos.offset(dir));
                    isSurrounded &= state.isOpaque() || state.getBlock() instanceof LeavesBlock;
                }
            }
            return Optional.of(!isSurrounded);
        }
        return Optional.of(true);
    }

    private Optional<Boolean> shouldDrawFaceDepth(BlockView view, BlockState sideState, BlockPos sidePos, Direction side) {
        if (sideState.isOpaque() || sideState.getBlock() instanceof LeavesBlock) {
            for (int i = 1; i < MoreCulling.CONFIG.leavesCullingDepth; i++) {
                BlockState state = view.getBlockState(sidePos.offset(side, i));
                if (state == null || !(state.isOpaque() || state.getBlock() instanceof LeavesBlock))
                    return Optional.of(false);
            }
        }
        return Optional.of(true);
    }
}

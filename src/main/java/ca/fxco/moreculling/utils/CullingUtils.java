package ca.fxco.moreculling.utils;

import ca.fxco.moreculling.MoreCulling;
import ca.fxco.moreculling.api.model.BakedOpacity;
import ca.fxco.moreculling.mixin.accessors.AbstractBlockAccessor;
import ca.fxco.moreculling.api.blockstate.MoreStateCulling;
import it.unimi.dsi.fastutil.objects.Object2ByteLinkedOpenHashMap;
import me.jellysquid.mods.sodium.client.SodiumClientMod;
import net.minecraft.block.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GraphicsMode;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

import java.util.Optional;

import static ca.fxco.moreculling.MoreCulling.blockRenderManager;
import static net.minecraft.block.Block.FACE_CULL_MAP;

public class CullingUtils {

    private static final Direction[] DIRECTIONS = Direction.values();

    /**
     * Replaces the default vanilla culling with a custom implementation
     */
    public static boolean shouldDrawSideCulling(BlockState thisState,
                                                BlockView world, BlockPos thisPos, Direction side,
                                                BlockPos sidePos) {
        BlockState sideState = world.getBlockState(sidePos);
        if (thisState.isSideInvisible(sideState, side)) return false;
        if (((MoreStateCulling)thisState).usesCustomShouldDrawFace()) {
            Optional<Boolean> shouldDrawFace = ((MoreStateCulling) thisState).customShouldDrawFace(
                    world, sideState, thisPos, sidePos, side
            );
            if (shouldDrawFace.isPresent()) return shouldDrawFace.get();
        }
        if (sideState.isOpaque() || (((AbstractBlockAccessor)sideState.getBlock()).getCollidable() &&
                !sideState.getRenderType().equals(BlockRenderType.INVISIBLE) &&
                ((MoreStateCulling) thisState).shouldAttemptToCull() &&
                ((MoreStateCulling) sideState).shouldAttemptToCull())) {
            return shouldDrawFace(world, thisState, sideState, thisPos, sidePos, side);
        }
        return true;
    }

    /**
     * Just the logic used to compare 2 block faces once we know that they are both solid textures
     */
    private static boolean shouldDrawFace(BlockView world, BlockState thisState, BlockState sideState,
                                          BlockPos thisPos, BlockPos sidePos, Direction side) {
        if (((MoreStateCulling)sideState).cantCullAgainst()) return true; // Check if we can cull against this block
        Block.NeighborGroup neighborGroup = new Block.NeighborGroup(thisState, sideState, side);
        Object2ByteLinkedOpenHashMap<Block.NeighborGroup> object2ByteLinkedOpenHashMap = FACE_CULL_MAP.get();
        byte b = object2ByteLinkedOpenHashMap.getAndMoveToFirst(neighborGroup);
        if (b != 127) return b != 0;
        Direction opposite = side.getOpposite();
        VoxelShape thisShape = thisState.getCullingFace(world, thisPos, side);
        VoxelShape sideShape; // Culling face may not be required, so we can save performance by skipping it
        if (thisShape.isEmpty()) { // It this shape is empty
            if (!sideState.isSideSolidFullSquare(world, sidePos, opposite) ||
                    (sideShape = sideState.getCullingFace(world, sidePos, opposite)).isEmpty()) {
                return true; // Face should be drawn if the side face is not a full square or its empty
            }
        } else {
            sideShape = sideState.getCullingFace(world, sidePos, opposite);
        }
        boolean bl = VoxelShapes.matchesAnywhere(thisShape, sideShape, BooleanBiFunction.ONLY_FIRST);
        if (object2ByteLinkedOpenHashMap.size() == 2048) object2ByteLinkedOpenHashMap.removeLastByte();
        object2ByteLinkedOpenHashMap.putAndMoveToFirst(neighborGroup, (byte)(bl ? 1 : 0));
        return bl;
    }

    public static boolean areLeavesOpaque() {
        GraphicsMode mode = MinecraftClient.getInstance().options.graphicsMode;
        return CompatUtils.IS_SODIUM_LOADED ?
                !SodiumClientMod.options().quality.leavesQuality.isFancy(mode) :
                mode.getId() < GraphicsMode.FANCY.getId();
    }

    public static Optional<Boolean> shouldDrawFaceCheck(BlockView view, BlockState sideState,
                                                        BlockPos thisPos, BlockPos sidePos, Direction side) {
        if (sideState.getBlock() instanceof LeavesBlock ||
                (sideState.isOpaque() && sideState.isSideSolidFullSquare(view, sidePos, side))) {
            boolean isSurrounded = true;
            for (Direction dir : DIRECTIONS) {
                if (dir != side) {
                    BlockPos pos = thisPos.offset(dir);
                    BlockState state = view.getBlockState(pos);
                    isSurrounded &= state.getBlock() instanceof LeavesBlock ||
                            (sideState.isOpaque() && state.isSideSolidFullSquare(view, pos, dir));
                }
            }
            return isSurrounded ? Optional.of(false) : Optional.empty();
        }
        return Optional.of(true);
    }

    public static Optional<Boolean> shouldDrawFaceDepth(BlockView view, BlockState sideState,
                                                        BlockPos sidePos, Direction side) {
        if (sideState.getBlock() instanceof LeavesBlock ||
                (sideState.isOpaque() && sideState.isSideSolidFullSquare(view, sidePos, side))) {
            for (int i = 1; i < MoreCulling.CONFIG.leavesCullingDepth + 1; i++) {
                BlockPos pos = sidePos.offset(side, i);
                BlockState state = view.getBlockState(pos);
                if (state == null || !(state.getBlock() instanceof LeavesBlock ||
                        (state.isOpaque() && state.isSideSolidFullSquare(view, pos, side))))
                    return Optional.of(false);
            }
        }
        return Optional.of(true);
    }

    public static boolean shouldCullBack(ItemFrameEntity frame) {
        Direction dir = frame.getHorizontalFacing();
        BlockPos posBehind = frame.getDecorationBlockPos().offset(dir.getOpposite());
        BlockState blockState = frame.world.getBlockState(posBehind);
        return blockState.isOpaque() && blockState.isSideSolidFullSquare(frame.world, posBehind, dir);
    }

    public static BakedModel getBakedModel(BlockState state) {
        return blockRenderManager.getModel(state);
    }

    public static BakedOpacity getBakedOpacity(BlockState state) {
        return (BakedOpacity)getBakedModel(state);
    }
}

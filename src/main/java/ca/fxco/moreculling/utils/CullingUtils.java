package ca.fxco.moreculling.utils;

import ca.fxco.moreculling.api.model.BakedOpacity;
import ca.fxco.moreculling.mixin.accessors.AbstractBlockAccessor;
import ca.fxco.moreculling.patches.MoreStateCulling;
import it.unimi.dsi.fastutil.objects.Object2ByteLinkedOpenHashMap;
import net.minecraft.block.*;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

import java.util.Optional;

import static ca.fxco.moreculling.MoreCulling.DONT_CULL;
import static ca.fxco.moreculling.MoreCulling.blockRenderManager;
import static net.minecraft.block.Block.FACE_CULL_MAP;

public class CullingUtils {

    /**
     * Replaces the default vanilla culling with a custom implementation
     */
    public static boolean shouldDrawSideCulling(BlockState thisState,
                                                BlockView world, BlockPos thisPos, Direction side,
                                                BlockPos sidePos) {
        BlockState sideState = world.getBlockState(sidePos);
        if (thisState.isSideInvisible(sideState, side)) return false;
        if (((MoreStateCulling)thisState).usesCustomShouldDrawFace()) {
            Optional<Boolean> shouldDrawFace = ((MoreStateCulling) thisState).customShouldDrawFace(world, sideState, thisPos, sidePos, side);
            if (shouldDrawFace.isPresent()) return shouldDrawFace.get();
        }
        if (((MoreStateCulling)sideState).usesCustomShouldDrawFace()) {
            Optional<Boolean> shouldDrawFace = ((MoreStateCulling) sideState).customShouldDrawFace(world, sideState, thisPos, sidePos, side);
            if (shouldDrawFace.isPresent()) return shouldDrawFace.get();
        }
        Block block = sideState.getBlock();
        if (sideState.isOpaque() || (((AbstractBlockAccessor)block).getCollidable() &&
                !((BakedOpacity)blockRenderManager.getModel(thisState)).hasTextureTranslucency() &&
                !((BakedOpacity)blockRenderManager.getModel(sideState)).hasTextureTranslucency())) {
            return shouldDrawFace(world, thisState, sideState, thisPos, sidePos, side);
        }
        return true;
    }

    /**
     * Same as above except, this should be used if the model is provided since its faster
     */
    public static boolean shouldDrawSideCulling(BlockState thisState,
                                                BlockView world, BlockPos thisPos, Direction side,
                                                BlockPos sidePos, boolean hasTransparency) {
        BlockState sideState = world.getBlockState(sidePos);
        if (thisState.isSideInvisible(sideState, side)) return false;
        if (((MoreStateCulling)thisState).usesCustomShouldDrawFace()) {
            Optional<Boolean> shouldDrawFace = ((MoreStateCulling) thisState).customShouldDrawFace(world, sideState, thisPos, sidePos, side);
            if (shouldDrawFace.isPresent()) return shouldDrawFace.get();
        }
        if (((MoreStateCulling)sideState).usesCustomShouldDrawFace()) {
            Optional<Boolean> shouldDrawFace = ((MoreStateCulling) sideState).customShouldDrawFace(world, sideState, thisPos, sidePos, side);
            if (shouldDrawFace.isPresent()) return shouldDrawFace.get();
        }
        Block block = sideState.getBlock();
        if (sideState.isOpaque() || (!hasTransparency && ((AbstractBlockAccessor)block).getCollidable() &&
                !((BakedOpacity)blockRenderManager.getModel(sideState)).hasTextureTranslucency())) {
            return shouldDrawFace(world, thisState, sideState, thisPos, sidePos, side);
        }
        return true;
    }

    /**
     * Just the logic used to compare 2 block faces once we know that they are both solid textures
     */
    private static boolean shouldDrawFace(BlockView world, BlockState thisState, BlockState sideState,
                                          BlockPos thisPos, BlockPos sidePos, Direction side) {
        if (sideState.isIn(DONT_CULL)) return true; // Some states are special, so we use the dont_cull blockTag
        Block.NeighborGroup neighborGroup = new Block.NeighborGroup(thisState, sideState, side);
        Object2ByteLinkedOpenHashMap<Block.NeighborGroup> object2ByteLinkedOpenHashMap = FACE_CULL_MAP.get();
        byte b = object2ByteLinkedOpenHashMap.getAndMoveToFirst(neighborGroup);
        if (b != 127) return b != 0;
        VoxelShape thisShape = thisState.getCullingFace(world, thisPos, side);
        if (thisShape.isEmpty()) return true;
        VoxelShape sideShape = sideState.getCullingFace(world, sidePos, side.getOpposite());
        boolean bl = VoxelShapes.matchesAnywhere(thisShape, sideShape, BooleanBiFunction.ONLY_FIRST);
        if (object2ByteLinkedOpenHashMap.size() == 2048) object2ByteLinkedOpenHashMap.removeLastByte();
        object2ByteLinkedOpenHashMap.putAndMoveToFirst(neighborGroup, (byte)(bl ? 1 : 0));
        return bl;
    }
}

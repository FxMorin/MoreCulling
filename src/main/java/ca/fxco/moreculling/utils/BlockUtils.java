package ca.fxco.moreculling.utils;

import ca.fxco.moreculling.api.model.BakedTransparency;
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

import static ca.fxco.moreculling.MoreCulling.DONT_CULL;
import static ca.fxco.moreculling.MoreCulling.blockRenderManager;
import static net.minecraft.block.Block.FACE_CULL_MAP;

public class BlockUtils {

    public static boolean shouldDrawSideTransparency(BlockState thisState,
                                                     BlockView world, BlockPos thisPos, Direction side,
                                                     BlockPos sidePos, boolean hasTransparency) {
        BlockState sideState = world.getBlockState(sidePos);
        if (((MoreStateCulling)thisState).isSideInvisibleAtPos(sideState, side, sidePos)) return false;
        Block block = sideState.getBlock();
        if (sideState.isOpaque() || (!hasTransparency && ((AbstractBlockAccessor)block).getCollidable() &&
                !((BakedTransparency)blockRenderManager.getModel(sideState)).hasTextureTransparency())) {
            //if (block instanceof LeavesBlock || block instanceof DoorBlock) return true; // Replace with a blockTag
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
        return true;
    }
}

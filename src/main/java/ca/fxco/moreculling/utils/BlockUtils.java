package ca.fxco.moreculling.utils;

import ca.fxco.moreculling.mixin.accessors.AbstractBlockAccessor;
import ca.fxco.moreculling.patches.BakedTransparency;
import it.unimi.dsi.fastutil.objects.Object2ByteLinkedOpenHashMap;
import net.minecraft.block.*;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

import static net.minecraft.block.Block.FACE_CULL_MAP;

public class BlockUtils {

    public static boolean shouldDrawSideTransparency(BlockRenderManager blockRenderManager, BlockState state,
                                                     BlockView world, BlockPos pos, Direction side,
                                                     BlockPos otherPos, boolean hasTransparency) {
        BlockState blockState = world.getBlockState(otherPos);
        if (state.isSideInvisible(blockState, side)) return false;
        Block block = blockState.getBlock();
        if (blockState.isOpaque() || (!hasTransparency && ((AbstractBlockAccessor)block).getCollidable() &&
                !((BakedTransparency)blockRenderManager.getModel(blockState)).hasTransparency())) {
            if (block instanceof LeavesBlock || block instanceof DoorBlock) return true; // Replace to a blockTag instead
            Block.NeighborGroup neighborGroup = new Block.NeighborGroup(state, blockState, side);
            Object2ByteLinkedOpenHashMap<Block.NeighborGroup> object2ByteLinkedOpenHashMap = FACE_CULL_MAP.get();
            byte b = object2ByteLinkedOpenHashMap.getAndMoveToFirst(neighborGroup);
            if (b != 127) return b != 0;
            VoxelShape voxelShape = state.getCullingFace(world, pos, side);
            if (voxelShape.isEmpty()) return true;
            VoxelShape voxelShape2 = blockState.getCullingFace(world, otherPos, side.getOpposite());
            boolean bl = VoxelShapes.matchesAnywhere(voxelShape, voxelShape2, BooleanBiFunction.ONLY_FIRST);
            if (object2ByteLinkedOpenHashMap.size() == 2048) object2ByteLinkedOpenHashMap.removeLastByte();
            object2ByteLinkedOpenHashMap.putAndMoveToFirst(neighborGroup, (byte)(bl ? 1 : 0));
            return bl;
        }
        return true;
    }
}

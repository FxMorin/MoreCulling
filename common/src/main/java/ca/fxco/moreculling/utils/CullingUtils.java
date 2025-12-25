package ca.fxco.moreculling.utils;

import ca.fxco.moreculling.MoreCulling;
import ca.fxco.moreculling.api.block.LeavesCulling;
import ca.fxco.moreculling.api.blockstate.MoreStateCulling;
import ca.fxco.moreculling.api.blockstate.StateCullingShapeCache;
import it.unimi.dsi.fastutil.objects.Object2ByteLinkedOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.state.ItemFrameRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Optional;

public class CullingUtils {

    public static final RandomSource RANDOM = RandomSource.createNewThreadLocalInstance();

    /**
     * Replaces the default vanilla culling with a custom implementation
     */
    public static boolean shouldDrawSideCulling(BlockState thisState, BlockState sideState,
                                                BlockGetter world, BlockPos thisPos, Direction side,
                                                BlockPos sidePos) {
        if (thisState.skipRendering(sideState, side)) {
            return false;
        }
        if (((MoreStateCulling) thisState).moreculling$usesCustomShouldDrawFace()) {
            Optional<Boolean> shouldDrawFace = ((MoreStateCulling) thisState).moreculling$customShouldDrawFace(
                    world, sideState, thisPos, sidePos, side
            );
            if (shouldDrawFace.isPresent()) {
                return shouldDrawFace.get();
            }
        }

        if (((MoreStateCulling) sideState).moreculling$canCull() &&
                (sideState.canOcclude() || !sideState.getRenderShape().equals(RenderShape.INVISIBLE) &&
                ((MoreStateCulling) thisState).moreculling$shouldAttemptToCull(side, world, thisPos) &&
                ((MoreStateCulling) sideState).moreculling$shouldAttemptToCull(side.getOpposite(), world, sidePos) &&
                ((MoreStateCulling) sideState).moreculling$shouldAttemptToCullAgainst(null, world, sidePos))) {
            return shouldDrawFace(world, thisState, sideState, thisPos, sidePos, side);
        }

        return true;
    }

    /**
     * Just the logic used to compare 2 block faces once we know that they are both solid textures
     */
    private static boolean shouldDrawFace(BlockGetter world, BlockState thisState, BlockState sideState,
                                          BlockPos thisPos, BlockPos sidePos, Direction side) {
        if (((MoreStateCulling) sideState).moreculling$cantCullAgainst(side)) {
            return true; // Check if we can cull against this block
        }
        Direction opposite = side.getOpposite();
        VoxelShape sideShape = ((StateCullingShapeCache) sideState).moreculling$getFaceCullingShape(opposite);

        if (sideShape == Shapes.block()) {
            return false;
        }

        VoxelShape thisShape = ((StateCullingShapeCache) thisState).moreculling$getFaceCullingShape(side);
        if (thisShape == Shapes.empty() || sideShape == Shapes.empty()) { // It this shape is empty
            return true; // Face should be drawn if the side face is empty
        }

        Block.ShapePairKey shapePairKey = new Block.ShapePairKey(
                thisShape,
                sideShape
        );
        Object2ByteLinkedOpenHashMap<Block.ShapePairKey> object2ByteLinkedOpenHashMap = Block.OCCLUSION_CACHE.get();
        byte b = object2ByteLinkedOpenHashMap.getAndMoveToFirst(shapePairKey);
        if (b != 127) {
            return b != 0;
        }
        boolean bl = Shapes.joinIsNotEmpty(thisShape, sideShape, BooleanOp.ONLY_FIRST);
        if (object2ByteLinkedOpenHashMap.size() == 256) {
            object2ByteLinkedOpenHashMap.removeLastByte();
        }
        object2ByteLinkedOpenHashMap.putAndMoveToFirst(shapePairKey, (byte) (bl ? 1 : 0));
        return bl;
    }

    public static boolean areLeavesOpaque() {
        return !Minecraft.getInstance().options.cutoutLeaves().get();
    }

    public static Optional<Boolean> shouldDrawFaceCheck(BlockGetter view, BlockState sideState,
                                                        BlockPos thisPos, BlockPos sidePos, Direction side) {
        if (sideState.getBlock() instanceof LeavesCulling ||
                (sideState.canOcclude() && ((StateCullingShapeCache) sideState)
                        .moreculling$getFaceCullingShape(side.getOpposite()) == Shapes.block())) {
            boolean isSurrounded = true;
            for (Direction dir : DirectionUtils.DIRECTIONS) {
                if (dir != side) {
                    BlockPos pos = thisPos.relative(dir);
                    BlockState state = view.getBlockState(pos);
                    isSurrounded &= state.getBlock() instanceof LeavesCulling ||
                            (state.canOcclude() && ((StateCullingShapeCache) state)
                                    .moreculling$getFaceCullingShape(dir.getOpposite()) == Shapes.block());
                }
            }
            return isSurrounded ? Optional.of(false) : Optional.empty();
        }
        return Optional.of(true);
    }

    public static Optional<Boolean> shouldDrawFaceGap(BlockGetter view, BlockState sideState,
                                                      BlockPos sidePos, Direction side) {
        Direction oppositeSide = side.getOpposite();
        if (sideState.getBlock() instanceof LeavesCulling ||
                (sideState.canOcclude() && ((StateCullingShapeCache) sideState)
                        .moreculling$getFaceCullingShape(oppositeSide) == Shapes.block())) {
            for (int i = 1; i < (5 - MoreCulling.CONFIG.leavesCullingAmount); i++) {
                BlockPos pos = sidePos.relative(side, i);
                BlockState state = view.getBlockState(pos);
                if (state == null || !(state.getBlock() instanceof LeavesCulling ||
                        (state.canOcclude() && ((StateCullingShapeCache) state)
                                .moreculling$getFaceCullingShape(oppositeSide) == Shapes.block()))) {
                    return Optional.of(false);
                }
            }
        }
        return Optional.of(true);
    }

    public static Optional<Boolean> shouldDrawFaceDepth(BlockGetter view, BlockState sideState,
                                                        BlockPos sidePos, Direction side) {
        if (sideState.getBlock() instanceof LeavesCulling ||
                (sideState.canOcclude() && ((StateCullingShapeCache) sideState)
                        .moreculling$getFaceCullingShape(side.getOpposite()) == Shapes.block())) {
            for (int i = 1; i < MoreCulling.CONFIG.leavesCullingAmount + 1; i++) {
                BlockState state = view.getBlockState(sidePos.relative(side, i));
                if (state == null || state.isAir()) {
                    return Optional.of(true);
                }
            }
            return Optional.of(false);
        }
        return Optional.of(true);
    }

    public static Optional<Boolean> shouldDrawFaceRandom(BlockGetter view, BlockState sideState,
                                                         BlockPos sidePos, Direction side) {
        if (sideState.getBlock() instanceof LeavesCulling ||
                (sideState.canOcclude() && ((StateCullingShapeCache) sideState)
                        .moreculling$getFaceCullingShape(side.getOpposite()) == Shapes.block())) {
            if (RANDOM.nextIntBetweenInclusive(1, MoreCulling.CONFIG.leavesCullingAmount + 1) == 1) {
                return Optional.of(false);
            }
        }
        return Optional.of(true);
    }

    public static boolean shouldCullBack(ItemFrameRenderState frame) {
        Direction dir = frame.direction;
        BlockPos posBehind = new BlockPos((int) Math.floor(frame.x), (int) Math.floor(frame.y), (int) Math.floor(frame.z)).relative(dir.getOpposite());
        BlockState blockState = Minecraft.getInstance().level.getBlockState(posBehind);
        return blockState.canOcclude() && ((StateCullingShapeCache) blockState)
                .moreculling$getFaceCullingShape(dir) == Shapes.block();
    }

    public static boolean shouldShowMapFace(Direction facingDir, ItemFrameRenderState framePos, Vec3 cameraPos) {
        if (MoreCulling.CONFIG.itemFrameMapCulling) {
            return switch (facingDir) {
                case DOWN -> cameraPos.y <= framePos.y;
                case UP -> cameraPos.y >= framePos.y;
                case NORTH -> cameraPos.z <= framePos.z;
                case SOUTH -> cameraPos.z >= framePos.z;
                case WEST -> cameraPos.x <= framePos.x;
                case EAST -> cameraPos.x >= framePos.x;
            };
        }
        return true;
    }

    public static boolean shouldHideWallSignText(Direction facingDir, Vec3 framePos, Vec3 cameraPos) {
        return switch (facingDir) {
            case NORTH -> cameraPos.z > framePos.z;
            case SOUTH -> cameraPos.z < framePos.z;
            case WEST -> cameraPos.x > framePos.x;
            case EAST -> cameraPos.x < framePos.x;
            default -> false;
        };
    }

    public static boolean shouldCullPaintingBack(BlockPos paintingPos, Direction oppositeDir) {
        BlockPos posBehind = paintingPos.relative(oppositeDir, 1);
        BlockState blockState = Minecraft.getInstance().level.getBlockState(posBehind);
        return blockState.canOcclude() && ((StateCullingShapeCache) blockState)
                .moreculling$getFaceCullingShape(oppositeDir) == Shapes.block();
    }
}

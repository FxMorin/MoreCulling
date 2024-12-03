package ca.fxco.moreculling.utils;

import ca.fxco.moreculling.MoreCulling;
import ca.fxco.moreculling.api.blockstate.MoreStateCulling;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2ByteLinkedOpenHashMap;
import net.caffeinemc.mods.sodium.client.SodiumClientMod;
import net.minecraft.client.GraphicsStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.state.ItemFrameRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Optional;

public class CullingUtils {

    public static final RandomSource random = RandomSource.createNewThreadLocalInstance();

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
                (sideState.canOcclude() || (!sideState.getRenderShape().equals(RenderShape.INVISIBLE) &&
                ((MoreStateCulling) thisState).moreculling$shouldAttemptToCull(side) &&
                ((MoreStateCulling) sideState).moreculling$shouldAttemptToCull(side.getOpposite())))) {
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
        VoxelShape thisShape = thisState.getFaceOcclusionShape(side);
        if (thisShape.isEmpty()) //vanilla 1.21.2 will just return empty if block cant occlude instead of its shape
            thisShape = thisState.getBlock().getOcclusionShape(thisState).getFaceShape(side);
        VoxelShape sideShape = sideState.getFaceOcclusionShape(opposite);
        if (sideShape.isEmpty())
            sideShape = sideState.getBlock().getOcclusionShape(sideState).getFaceShape(opposite);

        Block.ShapePairKey shapePairKey = new Block.ShapePairKey(
                thisShape,
                sideShape
                );
        Object2ByteLinkedOpenHashMap<Block.ShapePairKey> object2ByteLinkedOpenHashMap = Block.OCCLUSION_CACHE.get();
        byte b = object2ByteLinkedOpenHashMap.getAndMoveToFirst(shapePairKey);
        if (b != 127) {
            return b != 0;
        }
        if (thisShape.isEmpty()) { // It this shape is empty
            if (!sideState.isFaceSturdy(world, sidePos, opposite) ||
                    sideShape.isEmpty()) {
                return true; // Face should be drawn if the side face is not a full square or its empty
            }
        }
        boolean bl = Shapes.joinIsNotEmpty(thisShape, sideShape, BooleanOp.ONLY_FIRST);
        if (object2ByteLinkedOpenHashMap.size() == 256) {
            object2ByteLinkedOpenHashMap.removeLastByte();
        }
        object2ByteLinkedOpenHashMap.putAndMoveToFirst(shapePairKey, (byte) (bl ? 1 : 0));
        return bl;
    }

    public static boolean areLeavesOpaque() {
        GraphicsStatus mode = Minecraft.getInstance().options.graphicsMode().get();
        return CompatUtils.IS_SODIUM_LOADED ?
                !SodiumClientMod.options().quality.leavesQuality.isFancy(mode) :
                mode.getId() < GraphicsStatus.FANCY.getId();
    }

    public static Optional<Boolean> shouldDrawFaceCheck(BlockGetter view, BlockState sideState,
                                                        BlockPos thisPos, BlockPos sidePos, Direction side) {
        if (sideState.getBlock() instanceof LeavesBlock ||
                (sideState.canOcclude() && sideState.isFaceSturdy(view, sidePos, side.getOpposite()))) {
            boolean isSurrounded = true;
            for (Direction dir : DirectionUtils.DIRECTIONS) {
                if (dir != side) {
                    BlockPos pos = thisPos.relative(dir);
                    BlockState state = view.getBlockState(pos);
                    isSurrounded &= state.getBlock() instanceof LeavesBlock ||
                            (state.canOcclude() && state.isFaceSturdy(view, pos, dir.getOpposite()));
                }
            }
            return isSurrounded ? Optional.of(false) : Optional.empty();
        }
        return Optional.of(true);
    }

    public static Optional<Boolean> shouldDrawFaceGap(BlockGetter view, BlockState sideState,
                                                      BlockPos sidePos, Direction side) {
        Direction oppositeSide = side.getOpposite();
        if (sideState.getBlock() instanceof LeavesBlock ||
                (sideState.canOcclude() && sideState.isFaceSturdy(view, sidePos, oppositeSide))) {
            for (int i = 1; i < (5 - MoreCulling.CONFIG.leavesCullingAmount); i++) {
                BlockPos pos = sidePos.relative(side, i);
                BlockState state = view.getBlockState(pos);
                if (state == null || !(state.getBlock() instanceof LeavesBlock ||
                        (state.canOcclude() && state.isFaceSturdy(view, pos, oppositeSide)))) {
                    return Optional.of(false);
                }
            }
        }
        return Optional.of(true);
    }

    public static Optional<Boolean> shouldDrawFaceDepth(BlockGetter view, BlockState sideState,
                                                        BlockPos sidePos, Direction side) {
        if (sideState.getBlock() instanceof LeavesBlock ||
                (sideState.canOcclude() && sideState.isFaceSturdy(view, sidePos, side.getOpposite()))) {
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
        if (sideState.getBlock() instanceof LeavesBlock ||
                (sideState.canOcclude() && sideState.isFaceSturdy(view, sidePos, side.getOpposite()))) {
            if (random.nextIntBetweenInclusive(1, MoreCulling.CONFIG.leavesCullingAmount + 1) == 1) {
                return Optional.of(false);
            }
        }
        return Optional.of(true);
    }

    public static boolean shouldCullBack(ItemFrameRenderState frame) {
        Direction dir = frame.direction;
        BlockPos posBehind = new BlockPos((int) Math.floor(frame.x), (int) Math.floor(frame.y), (int) Math.floor(frame.z)).relative(dir.getOpposite());
        BlockState blockState = Minecraft.getInstance().level.getBlockState(posBehind);
        return blockState.canOcclude() && blockState.isFaceSturdy(Minecraft.getInstance().level, posBehind, dir);
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
}

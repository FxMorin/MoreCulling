package ca.fxco.moreculling.mixin.models.cullshape;

import ca.fxco.moreculling.api.model.BakedOpacity;
import ca.fxco.moreculling.api.model.CullShapeElement;
import ca.fxco.moreculling.api.model.ExtendedUnbakedModel;
import ca.fxco.moreculling.utils.ShapeUtils;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.logging.LogUtils;
import com.mojang.math.Transformation;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.ResolvedModel;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(SimpleModelWrapper.class)
public abstract class SimpleModelWrapper_cullShapeMixin implements BakedOpacity {
    @Unique
    private @Nullable VoxelShape moreculling$cullVoxelShape;
    @Unique
    private boolean moreculling$wasShapeOptimized = false;

    @Inject(
            method = "bake",
            at = @At(
                    value = "RETURN"
            )
    )
    private static void moreculling$onBake(ModelBaker modelBaker, ResourceLocation resourceLocation,
                                           ModelState settings, CallbackInfoReturnable<SimpleModelWrapper> cir,
                                           @Local ResolvedModel model) {
        UnbakedModel unbakedModel = model.wrapped();
        BlockModelPart bakedModel = cir.getReturnValue();
        if (bakedModel == null) {
            return;
        }
        BakedOpacity bakedOpacity = (BakedOpacity) bakedModel;
        if (!bakedOpacity.moreculling$canSetCullingShape()) {
            return;
        }
        ResourceLocation id = unbakedModel.parent();
        if (((ExtendedUnbakedModel) unbakedModel).moreculling$getUseModelShape(id)) {
            if (model.getTopGeometry() instanceof SimpleUnbakedGeometry(List<BlockElement> elements)) {
                if (elements != null && !elements.isEmpty()) {
                    VoxelShape voxelShape = Shapes.empty();
                    for (BlockElement e : elements) {
                        if ((e.rotation() == null || e.rotation().angle() == 0) &&
                                e.from().x() <= e.to().x()
                                && e.from().y() <= e.to().y()
                                && e.from().z() <= e.to().z()) {
                            VoxelShape shape = Block.box(
                                    e.from().x(), e.from().y(), e.from().z(), e.to().x(), e.to().y(), e.to().z()
                            );
                            voxelShape = ShapeUtils.orUnoptimized(voxelShape, shape);
                        }
                    }

                    if (settings.transformation() != Transformation.identity()) {
                        ;
                        Direction direction = Direction.rotate(settings.transformation().getMatrix(), Direction.NORTH);
                        if (direction.getAxis() != Direction.Axis.Y) {
                            voxelShape = ShapeUtils.rotateShapeUnoptimizedAroundY(Direction.NORTH, direction, voxelShape);
                        } else {
                            voxelShape = null;
                        /*direction = Direction.rotate(settings.getRotation().getMatrix(), Direction.UP); TODO rotation for non horizontal directions
                        if (direction.getAxis() != Direction.Axis.X) {
                            voxelShape = ShapeUtils.rotateShapeUnoptimizedAroundX(Direction.UP, direction, voxelShape);
                        } else {
                            voxelShape = ShapeUtils.rotateShapeUnoptimizedAroundZ(Direction.UP, direction, voxelShape);
                        }*/
                        }
                    }
                    bakedOpacity.moreculling$setCullingShape(voxelShape);
                }
            }
        } else {
            List<CullShapeElement> cullShapeElementList = ((ExtendedUnbakedModel) unbakedModel)
                    .moreculling$getCullShapeElements(model.parent());
            if (cullShapeElementList != null && !cullShapeElementList.isEmpty()) {
                VoxelShape voxelShape = Shapes.empty();
                for (CullShapeElement e : cullShapeElementList) {
                    VoxelShape shape = Block.box(e.from.x, e.from.y, e.from.z, e.to.x, e.to.y, e.to.z);
                    voxelShape = ShapeUtils.orUnoptimized(voxelShape, shape);
                }
                bakedOpacity.moreculling$setCullingShape(voxelShape);
            }
        }
    }

    @Override
    public @Nullable VoxelShape moreculling$getCullingShape(BlockState state) {
        if (!this.moreculling$wasShapeOptimized) {
            if (this.moreculling$cullVoxelShape != null) {
                this.moreculling$cullVoxelShape = moreculling$cullVoxelShape.optimize();
            }
            this.moreculling$wasShapeOptimized = true;
        }

        return this.moreculling$cullVoxelShape;
    }

    @Override
    public void moreculling$setCullingShape(VoxelShape cullingShape) {
        this.moreculling$cullVoxelShape = cullingShape;
    }

    @Override
    public boolean moreculling$canSetCullingShape() {
        return true;
    }
}

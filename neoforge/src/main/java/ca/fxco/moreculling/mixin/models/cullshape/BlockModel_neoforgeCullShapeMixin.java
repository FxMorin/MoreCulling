package ca.fxco.moreculling.mixin.models.cullshape;

import ca.fxco.moreculling.api.model.BakedOpacity;
import ca.fxco.moreculling.api.model.CullShapeElement;
import ca.fxco.moreculling.api.model.ExtendedUnbakedModel;
import ca.fxco.moreculling.utils.ShapeUtils;
import com.mojang.math.Transformation;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.block.model.TextureSlots;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(BlockModel.class)
public abstract class BlockModel_neoforgeCullShapeMixin implements ExtendedUnbakedModel {

    @Shadow
    @Nullable
    private UnbakedModel parent;

    @Shadow @Nullable abstract List<BlockElement> getElements();

    @Inject(
            method = "bake",
            at = @At(
                    value = "RETURN"
            )
    )
    private void moreculling$onBake(TextureSlots p_387258_, ModelBaker p_388168_, ModelState settings,
                                    boolean p_111455_, boolean p_387632_, ItemTransforms p_386577_,
                                    ContextMap additionalProperties,
                                    CallbackInfoReturnable<BakedModel> cir) {
        BakedModel bakedModel = cir.getReturnValue();
        if (bakedModel == null) {
            return;
        }
        BakedOpacity bakedOpacity = (BakedOpacity) bakedModel;
        if (!bakedOpacity.moreculling$canSetCullingShape()) {
            return;
        }
        ResourceLocation id = bakedModel instanceof BlockModel model ? model.parentLocation : null;
        List<CullShapeElement> cullShapeElementList = moreculling$getCullShapeElements(id);
        if (cullShapeElementList != null && !cullShapeElementList.isEmpty()) {
            VoxelShape voxelShape = Shapes.empty();
            for (CullShapeElement e : cullShapeElementList) {
                VoxelShape shape = Block.box(e.from.x, e.from.y, e.from.z, e.to.x, e.to.y, e.to.z);
                voxelShape = ShapeUtils.orUnoptimized(voxelShape, shape);
            }
            bakedOpacity.moreculling$setCullingShape(voxelShape);
            bakedOpacity.moreculling$setHasAutoModelShape(false);
        } else if (moreculling$getUseModelShape(id)) {
            List<BlockElement> modelElementList = this.getElements();
            if (modelElementList != null && !modelElementList.isEmpty()) {
                VoxelShape voxelShape = Shapes.empty();
                for (BlockElement e : modelElementList) {
                    if ((e.rotation == null || e.rotation.angle() == 0) &&
                            e.from.x <= e.to.x && e.from.y <= e.to.y && e.from.z <= e.to.z) {
                        VoxelShape shape = Block.box(
                                e.from.x, e.from.y, e.from.z, e.to.x, e.to.y, e.to.z
                        );
                        voxelShape = ShapeUtils.orUnoptimized(voxelShape, shape);
                    }
                }

                if (settings.getRotation() != Transformation.identity()) {;
                    Direction direction = Direction.rotate(settings.getRotation().getMatrix(), Direction.NORTH);
                    if (direction.getAxis() != Direction.Axis.Y) {
                        voxelShape = ShapeUtils.rotateShapeUnoptimizedAroundY(Direction.NORTH, direction, voxelShape);
                    } else {
                        voxelShape = null;
                        /*direction = Direction.rotate(settings.getRotation().getMatrix(), Direction.UP); TODO
                        if (direction.getAxis() != Direction.Axis.X) {
                            voxelShape = ShapeUtils.rotateShapeUnoptimizedAroundX(Direction.UP, direction, voxelShape);
                        } else {
                            voxelShape = ShapeUtils.rotateShapeUnoptimizedAroundZ(Direction.UP, direction, voxelShape);
                        }*/
                    }
                }
                bakedOpacity.moreculling$setCullingShape(voxelShape);
                bakedOpacity.moreculling$setHasAutoModelShape(moreculling$getHasAutoModelShape());
            }
        }
    }
}

package ca.fxco.moreculling.mixin.models.cullshape;

import ca.fxco.moreculling.api.model.BakedOpacity;
import ca.fxco.moreculling.api.model.CullShapeElement;
import ca.fxco.moreculling.api.model.ExtendedUnbakedModel;
import ca.fxco.moreculling.utils.ShapeUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.logging.LogUtils;
import net.minecraft.block.Block;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.Baker;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.ModelElement;
import net.minecraft.client.render.model.json.ModelElementFace;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.AffineTransformation;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.DirectionTransformation;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Mixin(JsonUnbakedModel.class)
public abstract class JsonUnbakedModel_cullShapeMixin implements ExtendedUnbakedModel {

    @Shadow
    @Nullable
    protected JsonUnbakedModel parent;

    @Shadow
    public abstract List<ModelElement> getElements();

    @Shadow
    public String id;
    @Unique
    @Nullable
    private List<CullShapeElement> cullShapeElements = null;

    @Unique
    private boolean useModelShape = false;

    @Override
    public void setCullShapeElements(@Nullable List<CullShapeElement> cullShapeElements) {
        this.cullShapeElements = cullShapeElements;
    }

    @Override
    public @Nullable List<CullShapeElement> getCullShapeElements(Identifier id) {
        if (this.cullShapeElements == null) {
            return this.parent != null ? ((ExtendedUnbakedModel) this.parent).getCullShapeElements(id) : null;
        }
        return cullShapeElements;
    }

    @Override
    public void setUseModelShape(boolean useModelShape) {
        this.useModelShape = useModelShape;
    }

    @Override
    public boolean getUseModelShape(Identifier id) {
        return this.useModelShape;
    }

    @Override
    public ModelElementFace modifyElementFace(ModelElementFace elementFace) {
        return elementFace;
    }

    @WrapOperation(
            method = "<clinit>",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/google/gson/GsonBuilder;create()Lcom/google/gson/Gson;"
            )
    )
    private static Gson registerCustomTypeAdapter(GsonBuilder instance, Operation<Gson> original) {
        return original.call(instance.registerTypeAdapter(CullShapeElement.class, new CullShapeElement.Deserializer()));
    }

    @WrapOperation(
            method = "bake(Lnet/minecraft/client/render/model/Baker;" +
                    "Lnet/minecraft/client/render/model/json/JsonUnbakedModel;Ljava/util/function/Function;" +
                    "Lnet/minecraft/client/render/model/ModelBakeSettings;Lnet/minecraft/util/Identifier;Z)" +
                    "Lnet/minecraft/client/render/model/BakedModel;",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;"
            )
    )
    private Object overrideFaceData(Map<Direction, ModelElementFace> map, Object direction, Operation<ModelElementFace> original) {
        return modifyElementFace(original.call(map, direction));
    }

    @Inject(
            method = "bake(Lnet/minecraft/client/render/model/Baker;" +
                    "Lnet/minecraft/client/render/model/json/JsonUnbakedModel;Ljava/util/function/Function;" +
                    "Lnet/minecraft/client/render/model/ModelBakeSettings;Lnet/minecraft/util/Identifier;Z)" +
                    "Lnet/minecraft/client/render/model/BakedModel;",
            at = @At(
                    value = "RETURN"
            )
    )
    private void onBake(Baker baker, JsonUnbakedModel parent, Function<SpriteIdentifier, Sprite> textureGetter,
                        ModelBakeSettings settings, Identifier id, boolean hasDepth,
                        CallbackInfoReturnable<BakedModel> cir) {
        BakedModel bakedModel = cir.getReturnValue();
        if (bakedModel == null) {
            return;
        }
        BakedOpacity bakedOpacity = (BakedOpacity) bakedModel;
        if (!bakedOpacity.canSetCullingShape()) {
            return;
        }
        if (getUseModelShape(id)) {
            LogUtils.getLogger().warn("usedfo " + id);
            List<ModelElement> modelElementList = this.getElements();
            if (modelElementList != null && !modelElementList.isEmpty()) {
                VoxelShape voxelShape = VoxelShapes.empty();
                for (ModelElement e : modelElementList) {
                    if ((e.rotation == null || e.rotation.angle() == 0)
                            && e.from.x <= e.to.x
                            && e.from.y <= e.to.y
                            && e.from.z <= e.to.z) {
                        VoxelShape shape = Block.createCuboidShape(e.from.x, e.from.y, e.from.z, e.to.x, e.to.y, e.to.z);
                        voxelShape = ShapeUtils.orUnoptimized(voxelShape, shape);
                    }
                }

                if (settings.getRotation() != AffineTransformation.identity()) {
                    DirectionTransformation group = ShapeUtils.MATRIX_TO_OCTAHEDRAL
                            .get(settings.getRotation().getMatrix());
                    if (group != null) {
                        voxelShape = ShapeUtils.rotate(voxelShape, group);
                    } else {
                        LogUtils.getLogger().warn(id + "  " + settings.getClass().toString());
                    }
                }
                bakedOpacity.setCullingShape(voxelShape.simplify());
                return;
            }
        } else {
            List<CullShapeElement> cullShapeElementList = getCullShapeElements(id);
            if (cullShapeElementList != null && !cullShapeElementList.isEmpty()) {
                VoxelShape voxelShape = VoxelShapes.empty();
                for (CullShapeElement e : cullShapeElementList) {
                    VoxelShape shape = Block.createCuboidShape(e.from.x, e.from.y, e.from.z, e.to.x, e.to.y, e.to.z);
                    voxelShape = ShapeUtils.orUnoptimized(voxelShape, shape);
                }
                bakedOpacity.setCullingShape(voxelShape.simplify());
                return;
            }
        }
        bakedOpacity.setCullingShape(null);
    }
}

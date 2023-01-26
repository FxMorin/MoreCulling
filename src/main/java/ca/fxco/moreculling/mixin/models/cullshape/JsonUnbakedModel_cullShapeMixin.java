package ca.fxco.moreculling.mixin.models.cullshape;

import ca.fxco.moreculling.api.model.CullShapeElement;
import ca.fxco.moreculling.api.model.ExtendedUnbakedModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.block.Block;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.Baker;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.ModelElement;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.function.Function;

import static ca.fxco.moreculling.utils.CullingUtils.VOXEL_SHAPE_STORE;

@Mixin(JsonUnbakedModel.class)
public abstract class JsonUnbakedModel_cullShapeMixin implements ExtendedUnbakedModel {

    @Shadow @Nullable
    protected JsonUnbakedModel parent;

    @Shadow public abstract List<ModelElement> getElements();

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
            return this.parent != null ? ((ExtendedUnbakedModel)this.parent).getCullShapeElements(id) : null;
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

    @Redirect(
            method = "<clinit>",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/google/gson/GsonBuilder;create()Lcom/google/gson/Gson;"
            )
    )
    private static Gson registerCustomTypeAdapter(GsonBuilder builder) {
        return builder.registerTypeAdapter(CullShapeElement.class, new CullShapeElement.Deserializer()).create();
    }

    @Inject(
            method = "bake(Lnet/minecraft/client/render/model/Baker;" +
                    "Lnet/minecraft/client/render/model/json/JsonUnbakedModel;Ljava/util/function/Function;" +
                    "Lnet/minecraft/client/render/model/ModelBakeSettings;Lnet/minecraft/util/Identifier;Z)" +
                    "Lnet/minecraft/client/render/model/BakedModel;",
            at = @At(
                    value = "RETURN",
                    shift = At.Shift.BEFORE
            )
    )
    private void onBake(Baker baker, JsonUnbakedModel parent, Function<SpriteIdentifier, Sprite> textureGetter,
                        ModelBakeSettings settings, Identifier id, boolean hasDepth,
                        CallbackInfoReturnable<BakedModel> cir) {
        if (getUseModelShape(id)) {
            List<ModelElement> modelElementList = this.getElements();
            if (modelElementList != null && !modelElementList.isEmpty()) {
                VoxelShape voxelShape = VoxelShapes.empty();
                for (ModelElement e : modelElementList) {
                    VoxelShape shape = Block.createCuboidShape(e.from.x, e.from.y, e.from.z, e.to.x, e.to.y, e.to.z);
                    voxelShape = VoxelShapes.union(voxelShape, shape);
                }
                VOXEL_SHAPE_STORE.set(voxelShape);
                return;
            }
        } else {
            List<CullShapeElement> cullShapeElementList = getCullShapeElements(id);
            if (cullShapeElementList != null && !cullShapeElementList.isEmpty()) {
                VoxelShape voxelShape = VoxelShapes.empty();
                for (CullShapeElement e : cullShapeElementList) {
                    VoxelShape shape = Block.createCuboidShape(e.from.x, e.from.y, e.from.z, e.to.x, e.to.y, e.to.z);
                    voxelShape = VoxelShapes.union(voxelShape, shape);
                }
                VOXEL_SHAPE_STORE.set(voxelShape);
                return;
            }
        }
        VOXEL_SHAPE_STORE.set(null);
    }
}

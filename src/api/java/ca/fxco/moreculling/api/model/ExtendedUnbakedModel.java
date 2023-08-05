package ca.fxco.moreculling.api.model;

import net.minecraft.client.render.model.json.ModelElementFace;
import net.minecraft.util.Identifier;

import java.util.List;

/**
 * ExtendedUnbakedModel is an interface that should be used on classes that extend JsonUnbakedModel
 * It allows unbaked models to use custom cullshapes & modify some inner workings
 *
 * @since 0.15.0
 */
public interface ExtendedUnbakedModel {

    /**
     * Sets the CullShapeElement for the extended unbaked model
     *
     * @since 0.15.0
     */
    void setCullShapeElements(List<CullShapeElement> cullShapeElements);

    /**
     * Gets the CullShapeElement for the extended unbaked model
     *
     * @since 0.15.0
     */
    List<CullShapeElement> getCullShapeElements(Identifier id);

    /**
     * When set to true, the cull shape is completely ignored and instead the model will be used as the cull shape
     *
     * @since 0.15.0
     */
    void setUseModelShape(boolean useModelShape);

    /**
     * Returns if it should use the model shape instead of the cull shape
     *
     * @since 0.15.0
     */
    boolean getUseModelShape(Identifier id);

    /**
     * ModelElementFace is just final's, that should not change. However you can create a new ModelElementFace or wrap
     * it. Do this to modify the element face data.
     *
     * @since 0.15.0
     */
    ModelElementFace modifyElementFace(ModelElementFace elementFace);
}

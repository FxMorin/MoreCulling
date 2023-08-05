package ca.fxco.moreculling.utils;

import net.minecraft.client.render.model.json.Transformation;

public class TransformationUtils {

    /*
    I need to put a lot more work into transformation logic. A lot of performance gains are possible if we can fully
    understand the limits of the transformations and how items get displayed with them. Proper equations that can
    correctly check the culling at really fast speeds could drastically help here. Would also make it possible to do
    stuff like 3-face item culling.
     */

    public static boolean canCullTransformation(Transformation transform) { // FRONT = SOUTH
        if (transform.scale.x() > 1.0F || transform.scale.y() > 1.0F || transform.scale.z() > 1.0F) {
            return false; //TODO: Maybe Allow Z axis
        }
        if (transform.rotation.x() % 90 != 0 || transform.rotation.z() % 90 != 0 || transform.rotation.y() % 90 != 0) {
            return false; //TODO: Maybe Allow Y axis, see if the face is correct
        }
        if (transform.translation.x() != 0 || transform.translation.y() != 0 || transform.translation.z() != 0) {
            return false; //TODO: Maybe allow Z axis, although would require checking scale also
        }
        return true;
    }
}

package ca.fxco.moreculling.utils;

import ca.fxco.moreculling.MoreCulling;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class LoaderUtils {

    private static Object loaderInstance = null;
    private static Method isModLoaded = null;

    // Preparing for when they eventually split or Quilt uses new functionality (maybe forge)
    private static Class<?> getLoaderClass() throws ReflectiveOperationException {
        Method instance;
        try {
            instance = Class.forName("net.fabricmc.loader.api.FabricLoader").getDeclaredMethod("getInstance");
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            instance = Class.forName("org.quiltmc.loader.api.QuiltLoader").getDeclaredMethod("impl");
        }
        instance.setAccessible(true);
        return instance.invoke(null).getClass();
    }

    public static boolean isModLoaded(String modid) {
        try {
            if (isModLoaded == null) {
                Class<?> loaderClass = getLoaderClass();
                isModLoaded = loaderClass.getDeclaredMethod("isModLoaded", String.class);
                isModLoaded.setAccessible(true);
                Constructor<?> loaderConstructor = loaderClass.getDeclaredConstructor();
                loaderConstructor.setAccessible(true);
                loaderInstance = loaderConstructor.newInstance();
            }
            return (boolean)isModLoaded.invoke(loaderInstance, modid);
        } catch (ReflectiveOperationException e) {
            MoreCulling.LOGGER.error("Unable to call `isModLoaded`", e);
        }
        return false;
    }
}

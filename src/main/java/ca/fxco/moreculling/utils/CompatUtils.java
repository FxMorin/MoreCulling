package ca.fxco.moreculling.utils;

import net.fabricmc.loader.api.FabricLoader;

public class CompatUtils {
    public static final boolean IS_SODIUM_LOADED = FabricLoader.getInstance().isModLoaded("sodium");
    public static final boolean IS_CULLLESSLEAVES_LOADED = FabricLoader.getInstance().isModLoaded("cull-less-leaves");
}

package ca.fxco.moreculling.utils;

import net.fabricmc.loader.api.FabricLoader;

public class CompatUtils {
    public static final boolean IS_SODIUM_LOADED = FabricLoader.getInstance().isModLoaded("sodium");
}

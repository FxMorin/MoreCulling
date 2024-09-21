package ca.fxco.moreculling.utils;

import ca.fxco.moreculling.platform.Services;

public class CompatUtils {
    public static boolean IS_SODIUM_LOADED = Services.PLATFORM.isModLoaded("sodium");
    public static boolean IS_MODERNFIX_LOADED = Services.PLATFORM.isModLoaded("modernfix");
}

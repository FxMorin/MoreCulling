package ca.fxco.moreculling.config;

import ca.fxco.moreculling.config.option.LeavesCullingMode;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

@Config(name = "moreculling")
public class MoreCullingConfig implements ConfigData {

    // You can disable the sodium menu in the config
    public boolean enableSodiumMenu = true;

    public boolean cloudCulling = true;

    public boolean signTextCulling = true;

    public boolean useBlockStateCulling = true;

    public boolean useCustomItemFrameRenderer = true;

    public boolean itemFrameMapCulling = true;

    public boolean useItemFrameLOD = true;

    public int itemFrameLODRange = 384; // default 384 (128 blocks) away

    public boolean useItemFrame3FaceCulling = true;

    public float itemFrame3FaceCullingRange = 12.0F; // default 12 (4 blocks) away

    public LeavesCullingMode leavesCullingMode = LeavesCullingMode.DEFAULT;

    public int leavesCullingAmount = 2;

    public boolean includeMangroveRoots = false;

    public boolean powderSnowCulling = true;

    public boolean entityModelCulling = false;

    public boolean useOnModdedBlocksByDefault = true;

    public Object2BooleanOpenHashMap<String> modCompatibility = new Object2BooleanOpenHashMap<>() {{
        put("minecraft", true);
    }};

    // Example to add rule using the MoreCulling Config API
    /*static {
        ConfigAdditions.addOption("general", new ConfigBooleanOption(
                "moreculling.config.option.signTextCulling",
                s -> MoreCulling.CONFIG.signTextCulling = s,
                () -> MoreCulling.CONFIG.signTextCulling,
                null,
                true,
                ConfigOptionImpact.HIGH,
                null
                )
        );
    }*/
}

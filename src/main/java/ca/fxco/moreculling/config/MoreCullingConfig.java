package ca.fxco.moreculling.config;

import ca.fxco.moreculling.config.option.LeavesCullingMode;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

@Config(name = "moreculling")
public class MoreCullingConfig implements ConfigData {

    // You can disable the sodium menu in the config
    public boolean enableSodiumMenu = true;

    public boolean useBlockStateCulling = true;

    public boolean useCustomItemFrameRenderer = true;

    public boolean useItemFrameLOD = true;

    public int itemFrameLODRange = 384; // default 384 (128 blocks) away

    public boolean useItemFrame3FaceCulling = true;

    public float itemFrame3FaceCullingRange = 12.0F; // default 12 (4 blocks) away

    public LeavesCullingMode leavesCullingMode = LeavesCullingMode.DEFAULT;

    public int leavesCullingDepth = 2;

    public boolean powderSnowCulling = true;
}

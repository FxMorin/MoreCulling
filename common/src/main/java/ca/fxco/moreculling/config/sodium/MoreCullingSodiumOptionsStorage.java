package ca.fxco.moreculling.config.sodium;

import ca.fxco.moreculling.MoreCulling;
import ca.fxco.moreculling.config.MoreCullingConfig;
import ca.fxco.moreculling.config.option.LeavesCullingMode;
import me.shedaniel.autoconfig.AutoConfig;

public class MoreCullingSodiumOptionsStorage {

    public MoreCullingConfig getData() {
        return MoreCulling.CONFIG;
    }

    public void save() {
        AutoConfig.getConfigHolder(MoreCullingConfig.class).save();
    }

    public void setCloudCulling(boolean cloudCulling) {
        getData().cloudCulling = cloudCulling;
    }

    public boolean getCloudCulling() {
        return getData().cloudCulling;
    }

    public void setSignTextCulling(boolean cloudCulling) {
        getData().signTextCulling = cloudCulling;
    }

    public boolean getSignTextCulling() {
        return getData().signTextCulling;
    }

    public void setRainCulling(boolean cloudCulling) {
        getData().rainCulling = cloudCulling;
    }

    public boolean getRainCulling() {
        return getData().rainCulling;
    }

    public void setBeaconBeamCulling(boolean value) {
        getData().beaconBeamCulling = value;
    }

    public boolean getBeaconBeamCulling() {
        return getData().beaconBeamCulling;
    }

    public void setIncludeMangroveRoots(boolean value) {
        getData().includeMangroveRoots = value;

    }

    public boolean getIncludeMangroveRoots() {
        return getData().includeMangroveRoots;
    }

    public void setEndGatewayCulling(boolean value) {
        getData().endGatewayCulling = value;

    }
    public boolean getEndGatewayCulling() {
        return getData().endGatewayCulling;
    }

    public void setUseBlockStateCulling(boolean value) {
        getData().useBlockStateCulling = value;

    }
    public boolean getUseBlockStateCulling() {
        return getData().useBlockStateCulling;
    }

    public void setLeavesCullingAmount(int value) {
        getData().leavesCullingAmount = value;
    }

    public int getLeavesCullingAmount() {
        return getData().leavesCullingAmount;
    }

    public void setLeavesCullingMode(LeavesCullingMode value) {
        getData().leavesCullingMode = value;
    }

    public LeavesCullingMode getLeavesCullingMode() {
        return getData().leavesCullingMode;
    }
}

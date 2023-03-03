package de.florianmichael.viafabricplus.settings.groups;

import de.florianmichael.viafabricplus.settings.SettingGroup;
import de.florianmichael.viafabricplus.settings.impl.BooleanSetting;

public class BridgeSettings extends SettingGroup {
    private final static BridgeSettings self = new BridgeSettings();

    public final BooleanSetting optionsButtonInGameOptions = new BooleanSetting(this, "Options button in game options", true);
    public final BooleanSetting showExtraInformationInDebugHud = new BooleanSetting(this, "Show extra information in Debug Hud", true);
    public final BooleanSetting showClassicLoadingProgressInConnectScreen = new BooleanSetting(this, "Show classic loading progress in connect screen", true);

    public BridgeSettings() {
        super("Bridge");
    }

    public static BridgeSettings getClassWrapper() {
        return BridgeSettings.self;
    }
}

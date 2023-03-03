package de.florianmichael.viafabricplus.settings.groups;

import de.florianmichael.viafabricplus.settings.SettingGroup;
import de.florianmichael.viafabricplus.settings.impl.BooleanSetting;

public class MPPassSettings extends SettingGroup {
    private final static MPPassSettings self = new MPPassSettings();

    public final BooleanSetting useBetaCraftAuthentication = new BooleanSetting(this, "Use BetaCraft authentication", true);
    public final BooleanSetting allowViaLegacyToCallJoinServerToVerifySession = new BooleanSetting(this, "Allow ViaLegacy to call joinServer() to verify session", true);
    public final BooleanSetting disconnectIfJoinServerCallFails = new BooleanSetting(this, "Disconnect if joinServer() call fails", true);

    public MPPassSettings() {
        super("MP Pass");
    }

    public static MPPassSettings getClassWrapper() {
        return MPPassSettings.self;
    }
}

package de.florianmichael.viafabricplus.setting.groups;

import de.florianmichael.viafabricplus.setting.SettingGroup;
import de.florianmichael.viafabricplus.setting.impl.BooleanSetting;
import de.florianmichael.viafabricplus.setting.impl.ModeSetting;

public class GeneralSettings extends SettingGroup {
    private final static GeneralSettings self = new GeneralSettings();

    public final ModeSetting mainButtonOrientation = new ModeSetting(this, "Main button orientation", "Left; Top", "Right; Top", "Left; Bottom", "Right: Bottom");
    public final BooleanSetting removeNotAvailableItemsFromCreativeTab = new BooleanSetting(this, "Remove not available items from creative tab", true);
    public final BooleanSetting automaticallyChangeValuesBasedOnTheCurrentVersion = new BooleanSetting(this, "Automatically change Settings based on the current version", true);
    public final BooleanSetting useBetaCraftAuthentication = new BooleanSetting(this, "Use BetaCraft authentication", true);

    public GeneralSettings() {
        super("General");
    }

    public static GeneralSettings getClassWrapper() {
        return GeneralSettings.self;
    }
}

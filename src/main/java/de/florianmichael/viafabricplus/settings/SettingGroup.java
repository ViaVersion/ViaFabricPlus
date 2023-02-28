package de.florianmichael.viafabricplus.settings;

import java.util.ArrayList;
import java.util.List;

public class SettingGroup {
    private final List<AbstractSetting<?>> settings = new ArrayList<>();
    private final String name;

    public SettingGroup(String name) {
        this.name = name;
    }

    public List<AbstractSetting<?>> getSettings() {
        return settings;
    }

    public String getName() {
        return name;
    }
}

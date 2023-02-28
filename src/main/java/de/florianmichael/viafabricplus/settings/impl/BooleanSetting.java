package de.florianmichael.viafabricplus.settings.impl;

import com.google.gson.JsonObject;
import de.florianmichael.viafabricplus.settings.AbstractSetting;
import de.florianmichael.viafabricplus.settings.SettingGroup;

public class BooleanSetting extends AbstractSetting<Boolean> {

    public BooleanSetting(SettingGroup parent, String name, Boolean defaultValue) {
        super(parent, name, defaultValue);
    }

    @Override
    public void write(JsonObject object) {
        object.addProperty(getName(), getValue());
    }

    @Override
    public void read(JsonObject object) {
        if (!object.has(getName())) return;

        setValue(object.get(getName()).getAsBoolean());
    }
}

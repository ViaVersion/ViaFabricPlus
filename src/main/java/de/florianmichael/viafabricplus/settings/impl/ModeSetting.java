package de.florianmichael.viafabricplus.settings.impl;

import com.google.gson.JsonObject;
import de.florianmichael.viafabricplus.settings.AbstractSetting;
import de.florianmichael.viafabricplus.settings.SettingGroup;

import java.util.Arrays;

public class ModeSetting extends AbstractSetting<String> {
    private final String[] options;

    public ModeSetting(SettingGroup parent, String name, String... options) {
        super(parent, name, options[0]);
        this.options = options;
    }

    @Override
    public void write(JsonObject object) {
        object.addProperty(getName(), getValue());
    }

    @Override
    public void read(JsonObject object) {
        if (!object.has(getName())) return;

        setValue(object.get(getName()).getAsString());
    }

    public void setValue(int index) {
        super.setValue(options[index]);
    }

    public int getIndex() {
        return Arrays.stream(options).toList().indexOf(getValue());
    }

    public String[] getOptions() {
        return options;
    }
}

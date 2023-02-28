package de.florianmichael.viafabricplus.settings;

import com.google.gson.JsonObject;

public abstract class AbstractSetting<T> {
    private final String name;
    private final T defaultValue;

    public T value;

    public AbstractSetting(final SettingGroup parent, final String name, final T defaultValue) {
        this.name = name;
        this.defaultValue = defaultValue;

        this.value = defaultValue;

        parent.getSettings().add(this);
    }

    public abstract void write(final JsonObject object);
    public abstract void read(final JsonObject object);

    public String getName() {
        return name;
    }

    public T getDefaultValue() {
        return defaultValue;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }
}

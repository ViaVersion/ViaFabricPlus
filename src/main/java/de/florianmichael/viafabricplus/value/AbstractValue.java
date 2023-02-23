package de.florianmichael.viafabricplus.value;

import com.google.gson.JsonObject;

public abstract class AbstractValue<T> {
    private final String name;
    private final T defaultValue;

    private T value;

    public AbstractValue(final String name, final T defaultValue) {
        this.name = name;
        this.defaultValue = defaultValue;

        this.value = defaultValue;

        ValueHolder.values.add(this);
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

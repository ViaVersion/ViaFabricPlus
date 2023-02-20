package de.florianmichael.viafabricplus.value;

public abstract class AbstractValue<T> {
    private final String name;
    private final T defaultValue;

    private T value;

    public AbstractValue(String name, T defaultValue) {
        this.name = name;
        this.defaultValue = defaultValue;

        this.value = defaultValue;
    }

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

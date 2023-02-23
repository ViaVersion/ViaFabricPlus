package de.florianmichael.viafabricplus.value.impl;

import com.google.gson.JsonObject;
import de.florianmichael.viafabricplus.value.AbstractValue;

public class BooleanValue extends AbstractValue<Boolean> {

    public BooleanValue(String name, Boolean defaultValue) {
        super(name, defaultValue);
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

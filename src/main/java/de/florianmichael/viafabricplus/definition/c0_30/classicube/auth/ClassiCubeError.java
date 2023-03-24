package de.florianmichael.viafabricplus.definition.c0_30.classicube.auth;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.Arrays;

public enum ClassiCubeError implements JsonDeserializer<ClassiCubeError> {
    TOKEN("Incorrect token. Is your ViaFabricPlus out of date?"),
    USERNAME("Invalid username."),
    PASSWORD("Invalid password."),
    VERIFICATION("User hasn't verified their E-mail address yet.", false),
    LOGIN_CODE("Multi-factor authentication requested. Please check your E-mail.");

    public final String description;
    public final boolean fatal;

    ClassiCubeError(String description) {
        this(description, true);
    }

    ClassiCubeError(String description, boolean fatal) {
        this.description = description;
        this.fatal = fatal;
    }

    @Override
    public ClassiCubeError deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return Arrays.stream(ClassiCubeError.values())
                .filter(e -> e.name().toLowerCase().equals(json.getAsString()))
                .findFirst()
                .orElse(null);
    }
}

package de.florianmichael.viafabricplus.definition.c0_30.classicube.response;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public abstract class ClassiCubeResponse {
    protected final static Gson GSON = new GsonBuilder()
            .serializeNulls()
            .create();
}

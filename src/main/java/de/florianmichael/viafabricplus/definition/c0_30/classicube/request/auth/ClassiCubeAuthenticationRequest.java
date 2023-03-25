package de.florianmichael.viafabricplus.definition.c0_30.classicube.request.auth;

import de.florianmichael.viafabricplus.definition.c0_30.classicube.auth.ClassiCubeAccount;
import de.florianmichael.viafabricplus.definition.c0_30.classicube.request.ClassiCubeRequest;
import de.florianmichael.viafabricplus.definition.c0_30.classicube.response.auth.ClassiCubeAuthenticationResponse;

import java.util.concurrent.CompletableFuture;

public abstract class ClassiCubeAuthenticationRequest extends ClassiCubeRequest {

    protected ClassiCubeAuthenticationRequest(ClassiCubeAccount account) {
        super(account);
    }

    public abstract CompletableFuture<ClassiCubeAuthenticationResponse> send();
}

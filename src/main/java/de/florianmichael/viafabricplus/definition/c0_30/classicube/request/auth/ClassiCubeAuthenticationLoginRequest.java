package de.florianmichael.viafabricplus.definition.c0_30.classicube.request.auth;

import de.florianmichael.viafabricplus.definition.c0_30.classicube.auth.ClassiCubeAuthenticationData;
import de.florianmichael.viafabricplus.definition.c0_30.classicube.request.ClassiCubeRequest;
import de.florianmichael.viafabricplus.definition.c0_30.classicube.response.auth.ClassiCubeAuthenticationResponse;

import javax.annotation.Nullable;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public class ClassiCubeAuthenticationLoginRequest extends ClassiCubeAuthenticationRequest {
    private final ClassiCubeAuthenticationData authenticationData;

    public ClassiCubeAuthenticationLoginRequest(ClassiCubeAuthenticationResponse previousResponse, String username, String password) {
        this(previousResponse, username, password, null);
    }

    public ClassiCubeAuthenticationLoginRequest(ClassiCubeAuthenticationResponse previousResponse, String username, String password, @Nullable String loginCode) {
        this.authenticationData = new ClassiCubeAuthenticationData(username, password, previousResponse.token, loginCode);
    }

    @Override
    public CompletableFuture<ClassiCubeAuthenticationResponse> send() {
        return CompletableFuture.supplyAsync(() -> {
            final String requestBody = authenticationData.getRequestBody();
            final HttpRequest request = HttpRequest.newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .uri(ClassiCubeRequest.AUTHENTICATION_URI)
                    .build();

            final HttpResponse<String> response = HTTP_CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .join();

            final String responseBody = response.body();
            return ClassiCubeAuthenticationResponse.fromJson(responseBody);
        });
    }
}

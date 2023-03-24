package de.florianmichael.viafabricplus.definition.c0_30.classicube.request.auth;

import de.florianmichael.viafabricplus.definition.c0_30.classicube.request.ClassiCubeRequest;
import de.florianmichael.viafabricplus.definition.c0_30.classicube.response.auth.ClassiCubeAuthenticationResponse;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public class ClassiCubeAuthenticationTokenRequest extends ClassiCubeAuthenticationRequest {
    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();

    @Override
    public CompletableFuture<ClassiCubeAuthenticationResponse> send() {
        return CompletableFuture.supplyAsync(() -> {
            final HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(ClassiCubeRequest.AUTHENTICATION_URI)
                    .build();

            final HttpResponse<String> response = HTTP_CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .join();

            final String responseBody = response.body();
            return ClassiCubeAuthenticationResponse.fromJson(responseBody);
        });
    }
}

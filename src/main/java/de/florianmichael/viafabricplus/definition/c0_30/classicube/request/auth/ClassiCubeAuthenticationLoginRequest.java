package de.florianmichael.viafabricplus.definition.c0_30.classicube.request.auth;

import de.florianmichael.viafabricplus.definition.c0_30.classicube.auth.ClassiCubeAccount;
import de.florianmichael.viafabricplus.definition.c0_30.classicube.auth.ClassiCubeAuthenticationData;
import de.florianmichael.viafabricplus.definition.c0_30.classicube.request.ClassiCubeRequest;
import de.florianmichael.viafabricplus.definition.c0_30.classicube.response.auth.ClassiCubeAuthenticationResponse;

import javax.annotation.Nullable;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public class ClassiCubeAuthenticationLoginRequest extends ClassiCubeAuthenticationRequest {
    private final ClassiCubeAuthenticationData authenticationData;
    private final ClassiCubeAccount account;

    public ClassiCubeAuthenticationLoginRequest(ClassiCubeAuthenticationResponse previousResponse, ClassiCubeAccount account) {
        this(previousResponse, account, null);
    }

    public ClassiCubeAuthenticationLoginRequest(ClassiCubeAuthenticationResponse previousResponse, ClassiCubeAccount account, @Nullable String loginCode) {
        super(account);
        this.authenticationData = new ClassiCubeAuthenticationData(account.username, account.password, previousResponse.token, loginCode);
        this.account = account;
    }

    @Override
    public CompletableFuture<ClassiCubeAuthenticationResponse> send() {
        return CompletableFuture.supplyAsync(() -> {
            final String requestBody = authenticationData.getRequestBody();

            final HttpRequest request = this.buildWithCookies(HttpRequest.newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .uri(ClassiCubeRequest.AUTHENTICATION_URI)
                    .header("content-type", "application/x-www-form-urlencoded"));

            final HttpResponse<String> response = HTTP_CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .join();

            this.updateCookies(response);

            final String responseBody = response.body();
            return ClassiCubeAuthenticationResponse.fromJson(responseBody);
        });
    }
}

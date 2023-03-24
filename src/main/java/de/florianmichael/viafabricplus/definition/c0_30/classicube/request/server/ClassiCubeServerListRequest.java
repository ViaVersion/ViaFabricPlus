package de.florianmichael.viafabricplus.definition.c0_30.classicube.request.server;

import de.florianmichael.viafabricplus.definition.c0_30.classicube.request.ClassiCubeRequest;
import de.florianmichael.viafabricplus.definition.c0_30.classicube.response.server.ClassiCubeServerInfoResponse;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public class ClassiCubeServerListRequest extends ClassiCubeRequest {
    public CompletableFuture<ClassiCubeServerInfoResponse> send() {
        return CompletableFuture.supplyAsync(() -> {
            final HttpRequest request = buildWithTokenHeader(HttpRequest.newBuilder()
                    .GET()
                    .uri(SERVER_LIST_INFO_URI));
            final HttpResponse<String> response = HTTP_CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .join();

            final String body = response.body();

            return ClassiCubeServerInfoResponse.fromJson(body);
        });
    }
}

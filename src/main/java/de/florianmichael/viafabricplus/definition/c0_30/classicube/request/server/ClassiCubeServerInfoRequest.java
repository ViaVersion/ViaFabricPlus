package de.florianmichael.viafabricplus.definition.c0_30.classicube.request.server;

import de.florianmichael.viafabricplus.definition.c0_30.classicube.auth.ClassiCubeAccount;
import de.florianmichael.viafabricplus.definition.c0_30.classicube.request.ClassiCubeRequest;
import de.florianmichael.viafabricplus.definition.c0_30.classicube.response.server.ClassiCubeServerInfoResponse;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class ClassiCubeServerInfoRequest extends ClassiCubeRequest {
    private final Set<String> serverHashes;

    public ClassiCubeServerInfoRequest(final ClassiCubeAccount account, final String serverHash) {
        this(account, Set.of(serverHash));
    }

    public ClassiCubeServerInfoRequest(final ClassiCubeAccount account, final Set<String> serverHashes) {
        super(account);
        this.serverHashes = serverHashes;
    }

    private URI generateUri() {
        final String joined = String.join(",", serverHashes);

        return SERVER_INFO_URI.resolve(joined);
    }

    public CompletableFuture<ClassiCubeServerInfoResponse> send() {
        return CompletableFuture.supplyAsync(() -> {
            final URI uri = this.generateUri();
            final HttpRequest request = this.buildWithCookies(HttpRequest.newBuilder()
                    .GET()
                    .uri(uri));
            final HttpResponse<String> response = HTTP_CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .join();

            updateCookies(response);

            final String body = response.body();

            return ClassiCubeServerInfoResponse.fromJson(body);
        });
    }
}

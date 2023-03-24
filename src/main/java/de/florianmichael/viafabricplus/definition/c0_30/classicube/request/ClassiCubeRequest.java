package de.florianmichael.viafabricplus.definition.c0_30.classicube.request;

import de.florianmichael.viafabricplus.definition.c0_30.classicube.ClassiCubeAccountHandler;
import de.florianmichael.viafabricplus.definition.c0_30.classicube.auth.ClassiCubeAccount;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;

public abstract class ClassiCubeRequest {

    private final static URI CLASSICUBE_ROOT_URI = URI.create("https://www.classicube.net");

    protected final static URI AUTHENTICATION_URI = CLASSICUBE_ROOT_URI.resolve("/api/login/");
    protected final static URI SERVER_INFO_URI = CLASSICUBE_ROOT_URI.resolve("/api/server/");
    protected final static URI SERVER_LIST_INFO_URI = CLASSICUBE_ROOT_URI.resolve("/api/servers/");
    protected final static HttpClient HTTP_CLIENT = HttpClient.newHttpClient();

    public HttpRequest buildWithTokenHeader(final HttpRequest.Builder builder) {
        final ClassiCubeAccountHandler accountHandler = ClassiCubeAccountHandler.INSTANCE;
        final ClassiCubeAccount account = accountHandler.getAccountClone();

        builder.header("Cookie", "session=" + account.token);

        return builder.build();
    }
}

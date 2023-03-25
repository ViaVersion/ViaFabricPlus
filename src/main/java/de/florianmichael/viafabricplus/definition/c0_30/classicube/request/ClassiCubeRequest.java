package de.florianmichael.viafabricplus.definition.c0_30.classicube.request;

import de.florianmichael.viafabricplus.definition.c0_30.classicube.auth.ClassiCubeAccount;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public abstract class ClassiCubeRequest {
    private final static URI CLASSICUBE_ROOT_URI = URI.create("https://www.classicube.net");

    protected final static URI AUTHENTICATION_URI = CLASSICUBE_ROOT_URI.resolve("/api/login/");
    protected final static URI SERVER_INFO_URI = CLASSICUBE_ROOT_URI.resolve("/api/server/");
    protected final static URI SERVER_LIST_INFO_URI = CLASSICUBE_ROOT_URI.resolve("/api/servers/");
    protected final static HttpClient HTTP_CLIENT = HttpClient.newHttpClient();
    protected final ClassiCubeAccount account;

    protected ClassiCubeRequest(ClassiCubeAccount account) {
        this.account = account;
    }

    protected HttpRequest buildWithCookies(final HttpRequest.Builder builder) {
        return this.account.cookieStore.appendCookies(builder)
                .build();
    }

    protected void updateCookies(final HttpResponse<?> response) {
        this.account.cookieStore.mergeFromResponse(response);
    }
}

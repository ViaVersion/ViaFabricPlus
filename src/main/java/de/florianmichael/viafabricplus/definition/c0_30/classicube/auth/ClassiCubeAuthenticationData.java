package de.florianmichael.viafabricplus.definition.c0_30.classicube.auth;

import javax.annotation.Nullable;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Contains the authentication data that will be used in the URL parameters of the /api/login request.
 * Taken from <a href="https://www.classicube.net/api/">the official ClassiCube API documentation</a>
 */
public class ClassiCubeAuthenticationData {
    private final String username;
    private final String password;
    private final String previousToken;
    @Nullable private final String loginCode;

    public ClassiCubeAuthenticationData(String username, String password, String previousToken) {
        this(username, password, previousToken, null);
    }

    public ClassiCubeAuthenticationData(String username, String password, String previousToken, @Nullable String loginCode) {
        this.username = username;
        this.password = password;
        this.previousToken = previousToken;
        this.loginCode = loginCode;
    }

    public ClassiCubeAuthenticationData getWithLoginToken(final String loginCode) {
        return new ClassiCubeAuthenticationData(this.username, this.password, this.previousToken, loginCode);
    }

    public String getRequestBody() {
        final StringBuilder builder = new StringBuilder("username=").append(URLEncoder.encode(username, StandardCharsets.UTF_8));

        builder.append("&password=");
        builder.append(URLEncoder.encode(password, StandardCharsets.UTF_8));
        builder.append("&token=");
        builder.append(URLEncoder.encode(previousToken, StandardCharsets.UTF_8));

        if (loginCode != null) {
            builder.append("&login_code=");
            builder.append(URLEncoder.encode(loginCode, StandardCharsets.UTF_8));
        }

        return builder.toString();
    }
}

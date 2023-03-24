package de.florianmichael.viafabricplus.definition.c0_30.classicube.auth;

import javax.annotation.Nullable;

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
        final StringBuilder builder = new StringBuilder("username=").append(username);

        builder.append("&password=");
        builder.append(password);
        builder.append("&token=");
        builder.append(previousToken);

        if (loginCode != null) {
            builder.append("&login_code=");
            builder.append(loginCode);
        }

        return builder.toString();
    }
}

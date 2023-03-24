package de.florianmichael.viafabricplus.definition.c0_30.classicube.auth;

import com.google.gson.JsonObject;
import de.florianmichael.viafabricplus.definition.c0_30.classicube.auth.process.ILoginProcessHandler;
import de.florianmichael.viafabricplus.definition.c0_30.classicube.request.auth.ClassiCubeAuthenticationLoginRequest;
import de.florianmichael.viafabricplus.definition.c0_30.classicube.request.auth.ClassiCubeAuthenticationTokenRequest;
import de.florianmichael.viafabricplus.definition.c0_30.classicube.response.auth.ClassiCubeAuthenticationResponse;

import javax.security.auth.login.LoginException;

public class ClassiCubeAccount {
    public String token;
    public String username;
    public String password;

    public ClassiCubeAccount(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public ClassiCubeAccount(String token, String username, String password) {
        this(username, password);
        this.token = token;
    }

    public JsonObject toJson() {
        final JsonObject object = new JsonObject();

        object.addProperty("token", this.token);
        object.addProperty("username", this.username);
        object.addProperty("password", this.password);

        return object;
    }

    public static ClassiCubeAccount fromJson(JsonObject jsonObject) {
        final String token = jsonObject.getAsString();
        final String username = jsonObject.getAsString();
        final String password = jsonObject.getAsString();

        return new ClassiCubeAccount(token, username, password);
    }

    public void login(ILoginProcessHandler processHandler) throws LoginException {
        final ClassiCubeAuthenticationTokenRequest initialTokenRequest = new ClassiCubeAuthenticationTokenRequest();
        final ClassiCubeAuthenticationResponse initialTokenResponse = initialTokenRequest.send()
                .join();

        // There should NEVER be any errors on the initial token response!
        if (initialTokenResponse.shouldError()) {
            final String errorDisplay = initialTokenResponse.getErrorDisplay();

            throw new LoginException(errorDisplay);
        }

        final ClassiCubeAuthenticationLoginRequest loginRequest = new ClassiCubeAuthenticationLoginRequest(initialTokenResponse, this.username, this.password);
        final ClassiCubeAuthenticationResponse loginResponse = loginRequest.send()
                .join();

        if (loginResponse.shouldError()) {
            final String errorDisplay = loginResponse.getErrorDisplay();

            throw new LoginException(errorDisplay);
        }

        this.token = loginResponse.token;

        if (initialTokenResponse.mfaRequired()) {
            processHandler.handleMfa(this);
            return;
        }

        processHandler.handleSuccessfulLogin(this);
    }
}

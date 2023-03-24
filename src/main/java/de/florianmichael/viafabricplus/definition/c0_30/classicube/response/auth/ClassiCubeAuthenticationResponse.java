package de.florianmichael.viafabricplus.definition.c0_30.classicube.response.auth;

import de.florianmichael.viafabricplus.definition.c0_30.classicube.auth.ClassiCubeError;
import de.florianmichael.viafabricplus.definition.c0_30.classicube.response.ClassiCubeResponse;

import javax.annotation.Nullable;
import java.util.Set;

/**
 * The class containing the response from the ClassiCube authentication service.
 * Most fields, except for authenticated and errors, are null in the first request.
 * As such, they are annotated as {@link Nullable}.
 */
public class ClassiCubeAuthenticationResponse extends ClassiCubeResponse {
    @Nullable public final String token;
    @Nullable public final String username;
    public final boolean authenticated;

    public ClassiCubeAuthenticationResponse(@Nullable String token, @Nullable String username, boolean authenticated, Set<ClassiCubeError> errors) {
        this.token = token;
        this.username = username;
        this.authenticated = authenticated;
        this.errors = errors;
    }

    public final Set<ClassiCubeError> errors;

    public boolean shouldError() {
        return errors.size() > 0 &&
                errors.stream().anyMatch(e -> e.fatal);
    }

    public String getErrorDisplay() {
        final StringBuilder builder = new StringBuilder();

        for (ClassiCubeError error : this.errors) {
            builder.append(error.description)
                    .append("\n");
        }

        return builder.toString()
                .trim();
    }

    public boolean mfaRequired() {
        return this.errors.stream().anyMatch(e -> e == ClassiCubeError.LOGIN_CODE);
    }

    public boolean isJustMfaError() {
        return mfaRequired() &&
                this.errors.stream().anyMatch(e -> e != ClassiCubeError.LOGIN_CODE);
    }

    public static ClassiCubeAuthenticationResponse fromJson(final String json) {
        return GSON.fromJson(json, ClassiCubeAuthenticationResponse.class);
    }
}

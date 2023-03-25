package de.florianmichael.viafabricplus.definition.c0_30.classicube.response.auth;

import de.florianmichael.viafabricplus.definition.c0_30.classicube.auth.ClassiCubeError;
import de.florianmichael.viafabricplus.definition.c0_30.classicube.response.ClassiCubeResponse;

import javax.annotation.Nullable;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The class containing the response from the ClassiCube authentication service.
 * Most fields, except for authenticated and errors, are null in the first request.
 * As such, they are annotated as {@link Nullable}.
 */
public class ClassiCubeAuthenticationResponse extends ClassiCubeResponse {
    @Nullable public final String token;
    @Nullable public final String username;
    public final boolean authenticated;
    public final Set<String> errors;

    public ClassiCubeAuthenticationResponse(@Nullable String token, @Nullable String username, boolean authenticated, Set<String> errors) {
        this.token = token;
        this.username = username;
        this.authenticated = authenticated;
        this.errors = errors;
    }

    public boolean shouldError() {
        return errors.size() > 0;
    }

    public String getErrorDisplay() {
        final StringBuilder builder = new StringBuilder();

        for (String error : this.errors) {
            builder.append(ClassiCubeError.valueOf(error.toUpperCase()).description.getString()).append("\n");
        }

        return builder.toString()
                .trim();
    }

    public Set<ClassiCubeError> errors() {
        return this.errors.stream().map(s -> ClassiCubeError.valueOf(s.toUpperCase(Locale.ROOT))).collect(Collectors.toSet());
    }

    public boolean mfaRequired() {
        return this.errors().stream().anyMatch(e -> e == ClassiCubeError.LOGIN_CODE);
    }

    public static ClassiCubeAuthenticationResponse fromJson(final String json) {
        return GSON.fromJson(json, ClassiCubeAuthenticationResponse.class);
    }
}

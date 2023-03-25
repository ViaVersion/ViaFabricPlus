package de.florianmichael.viafabricplus.definition.c0_30.classicube.auth;

import net.minecraft.text.Text;

public enum ClassiCubeError {
    TOKEN(Text.translatable("classicube.viafabricplus.error.token")),
    USERNAME(Text.translatable("classicube.viafabricplus.error.username")),
    PASSWORD(Text.translatable("classicube.viafabricplus.error.password")),
    VERIFICATION(Text.translatable("classicube.viafabricplus.error.verification"), false),
    LOGIN_CODE(Text.translatable("classicube.viafabricplus.error.logincode"));

    public final Text description;
    public final boolean fatal;

    ClassiCubeError(Text description) {
        this(description, true);
    }

    ClassiCubeError(Text description, boolean fatal) {
        this.description = description;
        this.fatal = fatal;
    }
}

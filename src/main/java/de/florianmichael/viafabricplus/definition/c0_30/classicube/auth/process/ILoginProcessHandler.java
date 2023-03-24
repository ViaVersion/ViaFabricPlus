package de.florianmichael.viafabricplus.definition.c0_30.classicube.auth.process;

import de.florianmichael.viafabricplus.definition.c0_30.classicube.auth.ClassiCubeAccount;

public interface ILoginProcessHandler {

    void handleMfa(final ClassiCubeAccount account);
    void handleSuccessfulLogin(final ClassiCubeAccount account);
}

package de.florianmichael.viafabricplus.injection.access;

public interface IMinecraftClient {

    void protocolhack_trackKeyboardInteraction(final Runnable interaction);
    void protocolhack_trackMouseInteraction(final Runnable interaction);
}

package de.florianmichael.viafabricplus.definition.v1_8_x;

public class IdlePacketExecutor {
    private static Runnable skipIdlePacketExecute;

    public static void skipIdlePacket() {
        if (skipIdlePacketExecute != null) {
            skipIdlePacketExecute.run();
        }
    }

    public static void registerIdlePacketSkipExecute(final Runnable runnable) {
        skipIdlePacketExecute = runnable;
    }
}

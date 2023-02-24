package de.florianmichael.viafabricplus.definition.v1_12_2;

import java.util.concurrent.ConcurrentLinkedDeque;

public class SyncInputExecutor {
    private final static ConcurrentLinkedDeque<Runnable> keyboardInteractions = new ConcurrentLinkedDeque<>();
    private final static ConcurrentLinkedDeque<Runnable> mouseInteractions = new ConcurrentLinkedDeque<>();

    public static void callback() {
        while (!mouseInteractions.isEmpty()) {
            mouseInteractions.poll().run();
        }
        while (!keyboardInteractions.isEmpty()) {
            keyboardInteractions.poll().run();
        }
    }

    public static void trackKeyboardInteraction(Runnable interaction) {
        keyboardInteractions.add(interaction);
    }

    public static void trackMouseInteraction(Runnable interaction) {
        mouseInteractions.add(interaction);
    }
}

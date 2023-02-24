package de.florianmichael.viafabricplus.definition.v1_14_4;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

public class SoulSandVelocityHandler {

    public static void handleVelocity(final Entity entity) {
        final Vec3d velocity = entity.getVelocity();

        double multiplier = 0.4D;
        entity.setVelocity(velocity.getX() * multiplier, velocity.getY(), velocity.getZ() * multiplier);
    }
}

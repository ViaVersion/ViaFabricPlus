package com.viaversion.viafabricplus.injection.mixin.features.movement.constants.infinity;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.viaversion.viafabricplus.settings.impl.GeneralSettings;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.border.BorderChangeListener;
import net.minecraft.world.level.border.BorderStatus;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(WorldBorder.class)
public abstract class MixinWorldBorder {
    @Shadow
    int absoluteMaxSize;

    @WrapMethod(method = "isWithinBounds(DDD)Z")
    private boolean isInBounds(final double x, final double y, final double z, final Operation<Boolean> original) {
        return original.call(x, y, z) || GeneralSettings.INSTANCE.experimentalFarlandsDistance.getValue();
    }

    @WrapOperation(method = "clampVec3ToBound(DDD)Lnet/minecraft/world/phys/Vec3;", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;clamp(DDD)D"))
    private double dontClampVec3(final double value, final double min, final double max, final Operation<Double> original) {
        if (GeneralSettings.INSTANCE.experimentalFarlandsDistance.getValue()) {
            return value;
        } else {
            return original.call(value, min, max);
        }
    }

    @WrapMethod(method = "getCollisionShape")
    private VoxelShape noCollision(final Operation<VoxelShape> original) {
        if (GeneralSettings.INSTANCE.experimentalFarlandsDistance.getValue()) {
            return Shapes.empty();
        } else {
            return original.call();
        }
    }

    @WrapMethod(method = "isInsideCloseToBorder")
    private boolean disableInsideClose(final Entity entity, final AABB aABB, final Operation<Boolean> original) {
        if (GeneralSettings.INSTANCE.experimentalFarlandsDistance.getValue()) {
            return false;
        } else {
            return original.call(entity, aABB);
        }
    }

    @WrapMethod(method = "getStatus")
    private BorderStatus noStatus(final Operation<BorderStatus> original) {
        if (GeneralSettings.INSTANCE.experimentalFarlandsDistance.getValue()) {
            return BorderStatus.STATIONARY;
        } else {
            return original.call();
        }
    }

    @WrapMethod(method = "getMinX(F)D")
    private double replaceMinX(final float tickDelta, final Operation<Double> original) {
        if (GeneralSettings.INSTANCE.experimentalFarlandsDistance.getValue()) {
            return Double.MIN_VALUE;
        } else {
            return original.call(tickDelta);
        }
    }

    @WrapMethod(method = "getMinZ(F)D")
    private double replaceMinZ(final float tickDelta, final Operation<Double> original) {
        if (GeneralSettings.INSTANCE.experimentalFarlandsDistance.getValue()) {
            return Double.MIN_VALUE;
        } else {
            return original.call(tickDelta);
        }
    }

    @WrapMethod(method = "getMaxX(F)D")
    private double replaceMaxX(final float tickDelta, final Operation<Double> original) {
        if (GeneralSettings.INSTANCE.experimentalFarlandsDistance.getValue()) {
            return Double.MAX_VALUE;
        } else {
            return original.call(tickDelta);
        }
    }

    @WrapMethod(method = "getMaxZ(F)D")
    private double replaceMaxZ(final float tickDelta, final Operation<Double> original) {
        if (GeneralSettings.INSTANCE.experimentalFarlandsDistance.getValue()) {
            return Double.MAX_VALUE;
        } else {
            return original.call(tickDelta);
        }
    }

    @WrapMethod(method = "setCenter")
    private void setCenter(final double x, final double z, final Operation<Void> original) {
        if (!GeneralSettings.INSTANCE.experimentalFarlandsDistance.getValue()) {
            original.call(x, z);
        }
    }

    @WrapMethod(method = "getSize")
    private double replaceSize(final Operation<Double> original) {
        if (GeneralSettings.INSTANCE.experimentalFarlandsDistance.getValue()) {
            return Double.MAX_VALUE;
        } else {
            return original.call();
        }
    }

    @WrapMethod(method = "setSize")
    private void setSize(final double size, final Operation<Void> original) {
        if (!GeneralSettings.INSTANCE.experimentalFarlandsDistance.getValue()) {
            original.call(size);
        }
    }

    @WrapMethod(method = "lerpSizeBetween")
    private void noLerp(final double d, final double e, final long l, final long m, final Operation<Void> original) {
        if (!GeneralSettings.INSTANCE.experimentalFarlandsDistance.getValue()) {
            original.call(d, e, l, m);
        }
    }

    @WrapMethod(method = "addListener")
    private void noListenerAdd(final BorderChangeListener borderChangeListener, final Operation<Void> original) {
        if (!GeneralSettings.INSTANCE.experimentalFarlandsDistance.getValue()) {
            original.call(borderChangeListener);
        }
    }

    @WrapMethod(method = "removeListener")
    private void noListenerRemove(final BorderChangeListener borderChangeListener, final Operation<Void> original) {
        if (!GeneralSettings.INSTANCE.experimentalFarlandsDistance.getValue()) {
            original.call(borderChangeListener);
        }
    }

    @WrapMethod(method = "setAbsoluteMaxSize")
    private void noAbsoluteSize(final int absoluteMaxSize, final Operation<Void> original) {
        if (GeneralSettings.INSTANCE.experimentalFarlandsDistance.getValue()) {
            this.absoluteMaxSize = Integer.MAX_VALUE;
        } else {
            original.call(absoluteMaxSize);
        }
    }

    @WrapMethod(method = "tick")
    private void noTick(final Operation<Void> original) {
        if (!GeneralSettings.INSTANCE.experimentalFarlandsDistance.getValue()) {
            original.call();
        }
    }

    @WrapMethod(method = "applyInitialSettings")
    private void noSettings(final long l, final Operation<Void> original) {
        if (!GeneralSettings.INSTANCE.experimentalFarlandsDistance.getValue()) {
            original.call(l);
        }
    }
}

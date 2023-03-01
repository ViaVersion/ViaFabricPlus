package de.florianmichael.viafabricplus.injection.mixin.fixes.entity;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("ConstantValue")
@Mixin(Entity.class)
public abstract class MixinEntity {

    @Shadow
    public World world;
    @Shadow
    protected Object2DoubleMap<TagKey<Fluid>> fluidHeight;
    @Shadow
    private Vec3d pos;

    @Shadow
    public abstract Box getBoundingBox();

    @Shadow
    public abstract Vec3d getVelocity();

    @Shadow
    public abstract void setVelocity(Vec3d velocity);


    @ModifyConstant(method = "movementInputToVelocity", constant = @Constant(doubleValue = 1E-7))
    private static double injectMovementInputToVelocity(double epsilon) {
        if (ViaLoadingBase.getClassWrapper().getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_13_2)) {
            return 1E-4;
        }
        return epsilon;
    }

    @Inject(method = "getVelocityAffectingPos", at = @At("HEAD"), cancellable = true)
    public void injectGetVelocityAffectingPos(CallbackInfoReturnable<BlockPos> cir) {
        if (ViaLoadingBase.getClassWrapper().getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_14_4)) {
            cir.setReturnValue(new BlockPos(pos.x, getBoundingBox().minY - 1, pos.z));
        }
    }

    @Inject(method = "getRotationVector(FF)Lnet/minecraft/util/math/Vec3d;", at = @At("HEAD"), cancellable = true)
    public void onGetRotationVector(float pitch, float yaw, CallbackInfoReturnable<Vec3d> cir) {
        if (ViaLoadingBase.getClassWrapper().getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_12_2)) {
            cir.setReturnValue(Vec3d.fromPolar(pitch, yaw));
        }
    }

    @Inject(method = "setSwimming", at = @At("HEAD"), cancellable = true)
    private void onSetSwimming(boolean swimming, CallbackInfo ci) {
        if (ViaLoadingBase.getClassWrapper().getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_12_2) && swimming) {
            ci.cancel();
        }
    }

    @SuppressWarnings("deprecation")
    @Inject(method = "updateMovementInFluid", at = @At("HEAD"), cancellable = true)
    private void modifyFluidMovementBoundingBox(TagKey<Fluid> fluidTag, double d, CallbackInfoReturnable<Boolean> ci) {
        if (ViaLoadingBase.getClassWrapper().getTargetVersion().isNewerThan(ProtocolVersion.v1_12_2)) {
            return;
        }

        Box box = getBoundingBox().expand(0, -0.4, 0).contract(0.001);
        int minX = MathHelper.floor(box.minX);
        int maxX = MathHelper.ceil(box.maxX);
        int minY = MathHelper.floor(box.minY);
        int maxY = MathHelper.ceil(box.maxY);
        int minZ = MathHelper.floor(box.minZ);
        int maxZ = MathHelper.ceil(box.maxZ);

        if (!world.isRegionLoaded(minX, minY, minZ, maxX, maxY, maxZ))
            ci.setReturnValue(false);

        double waterHeight = 0;
        boolean foundFluid = false;
        Vec3d pushVec = Vec3d.ZERO;

        BlockPos.Mutable mutable = new BlockPos.Mutable();

        for (int x = minX; x < maxX; x++) {
            for (int y = minY - 1; y < maxY; y++) {
                for (int z = minZ; z < maxZ; z++) {
                    mutable.set(x, y, z);
                    FluidState state = world.getFluidState(mutable);
                    if (state.isIn(fluidTag)) {
                        double height = y + state.getHeight(world, mutable);
                        if (height >= box.minY - 0.4)
                            waterHeight = Math.max(height - box.minY + 0.4, waterHeight);
                        if (y >= minY && maxY >= height) {
                            foundFluid = true;
                            pushVec = pushVec.add(state.getVelocity(world, mutable));
                        }
                    }
                }
            }
        }

        if (pushVec.length() > 0) {
            pushVec = pushVec.normalize().multiply(0.014);
            setVelocity(getVelocity().add(pushVec));
        }

        this.fluidHeight.put(fluidTag, waterHeight);
        ci.setReturnValue(foundFluid);
    }

    @Inject(method = "getTargetingMargin", at = @At("HEAD"), cancellable = true)
    public void expandHitBox(CallbackInfoReturnable<Float> cir) {
        if (ViaLoadingBase.getClassWrapper().getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_8)) {
            cir.setReturnValue(0.1F);
        }
    }

    @Redirect(method = {"setYaw", "setPitch"}, at = @At(value = "INVOKE", target = "Ljava/lang/Float;isFinite(F)Z"))
    public boolean modifyIsFinite(float f) {
        return Float.isFinite(f) || ((Object) this instanceof ClientPlayerEntity && ViaLoadingBase.getClassWrapper().getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_12_2));
    }

    @ModifyConstant(method = "checkBlockCollision", constant = @Constant(doubleValue = 1.0E-7))
    public double changeBlockCollisionConstant(double constant) {
        if (ViaLoadingBase.getClassWrapper().getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_19_1)) {
            return 0.001;
        }
        return constant;
    }

    // Not relevant for GamePlay
    @Redirect(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;onLanding()V"))
    public void revertOnLanding(Entity instance) {
        if (ViaLoadingBase.getClassWrapper().getTargetVersion().isNewerThanOrEqualTo(ProtocolVersion.v1_19)) {
            instance.onLanding();
        }
    }
}

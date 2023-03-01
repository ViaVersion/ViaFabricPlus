package de.florianmichael.viafabricplus.injection.mixin.fixes.entity;

import com.mojang.authlib.GameProfile;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.viafabricplus.definition.v1_8_x.IdlePacketExecutor;
import de.florianmichael.viafabricplus.injection.access.IClientPlayerEntity;
import de.florianmichael.viafabricplus.settings.groups.DebugSettings;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("ConstantValue")
@Mixin(value = ClientPlayerEntity.class, priority = 2000)
public abstract class MixinClientPlayerEntity extends AbstractClientPlayerEntity implements IClientPlayerEntity {

    @Shadow
    public Input input;
    @Shadow
    private float lastYaw;
    @Shadow
    private float lastPitch;
    @Shadow
    @Final
    public ClientPlayNetworkHandler networkHandler;
    @Shadow
    private boolean autoJumpEnabled;
    @Shadow
    @Final
    protected MinecraftClient client;
    @Shadow
    private boolean lastOnGround;
    @Shadow
    private int ticksSinceLastPositionPacketSent;
    @Shadow
    private double lastX;
    @Shadow
    private double lastBaseY;
    @Shadow
    private double lastZ;
    @Unique
    private boolean protocolhack_areSwingCanceledThisTick = false;

    public MixinClientPlayerEntity(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    @Shadow
    protected abstract boolean isCamera();

    @Shadow protected abstract void sendSprintingPacket();

    @Redirect(method = "sendMovementPackets", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isCamera()Z"))
    public boolean fixMovement(ClientPlayerEntity instance) {
        if (this.isCamera()) {
            boolean bl4;
            double d = this.getX() - this.lastX;
            double e = this.getY() - this.lastBaseY;
            double f = this.getZ() - this.lastZ;
            double g = this.getYaw() - this.lastYaw;
            double h = this.getPitch() - this.lastPitch;
            if (ViaLoadingBase.getClassWrapper().getTargetVersion().isNewerThan(ProtocolVersion.v1_8)) {
                ++this.ticksSinceLastPositionPacketSent;
            }
            double n = MathHelper.square(2.05E-4);
            if (ViaLoadingBase.getClassWrapper().getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_18_2)) {
                n = 9.0E-4D;
            }
            boolean bl3 = MathHelper.squaredMagnitude(d, e, f) > n || this.ticksSinceLastPositionPacketSent >= 20;
            bl4 = g != 0.0 || h != 0.0;
            if (this.hasVehicle()) {
                Vec3d vec3d = this.getVelocity();
                this.networkHandler.sendPacket(new PlayerMoveC2SPacket.Full(vec3d.x, -999.0, vec3d.z, this.getYaw(), this.getPitch(), this.onGround));
                bl3 = false;
            } else if (bl3 && bl4) {
                this.networkHandler.sendPacket(new PlayerMoveC2SPacket.Full(this.getX(), this.getY(), this.getZ(), this.getYaw(), this.getPitch(), this.onGround));
            } else if (bl3) {
                this.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(this.getX(), this.getY(), this.getZ(), this.onGround));
            } else if (bl4) {
                this.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(this.getYaw(), this.getPitch(), this.onGround));
            } else if (this.lastOnGround != this.onGround || DebugSettings.getClassWrapper().sendIdlePacket.getValue()) {
                this.networkHandler.sendPacket(new PlayerMoveC2SPacket.OnGroundOnly(this.onGround));
            } else {
                IdlePacketExecutor.skipIdlePacket();
            }
            if (ViaLoadingBase.getClassWrapper().getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_8)) {
                ++this.ticksSinceLastPositionPacketSent;
            }

            if (bl3) {
                this.lastX = this.getX();
                this.lastBaseY = this.getY();
                this.lastZ = this.getZ();
                this.ticksSinceLastPositionPacketSent = 0;
            }
            if (bl4) {
                this.lastYaw = this.getYaw();
                this.lastPitch = this.getPitch();
            }
            this.lastOnGround = this.onGround;
            this.autoJumpEnabled = this.client.options.getAutoJump().getValue();
        }
        return false;
    }

    @Inject(method = "swingHand", at = @At("HEAD"), cancellable = true)
    public void injectSwingHand(Hand hand, CallbackInfo ci) {
        if (ViaLoadingBase.getClassWrapper().getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_8) && protocolhack_areSwingCanceledThisTick) {
            ci.cancel();
        }

        protocolhack_areSwingCanceledThisTick = false;
    }

    @Inject(
            method = "tickMovement()V",
            slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isCamera()Z")),
            at = @At(value = "FIELD", target = "Lnet/minecraft/client/input/Input;sneaking:Z", ordinal = 0)
    )
    private void injectTickMovement(CallbackInfo ci) {
        if (ViaLoadingBase.getClassWrapper().getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_14_4)) {
            if (this.input.sneaking) {
                this.input.movementSideways = (float) ((double) this.input.movementSideways / 0.3D);
                this.input.movementForward = (float) ((double) this.input.movementForward / 0.3D);
            }
        }
    }

    @Redirect(method = "tickMovement",
            slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isWalking()Z")),
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isSwimming()Z", ordinal = 0))
    public boolean redirectIsSneakingWhileSwimming(ClientPlayerEntity _this) {
        if (ViaLoadingBase.getClassWrapper().getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_14_1)) {
            return false;
        } else {
            return _this.isSwimming();
        }
    }

    @Redirect(method = "isWalking", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isSubmergedInWater()Z"))
    public boolean easierUnderwaterSprinting(ClientPlayerEntity instance) {
        if (ViaLoadingBase.getClassWrapper().getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_14_1)) {
            return false;
        }
        return instance.isSubmergedInWater();
    }

    @Redirect(method = "tickMovement()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/input/Input;hasForwardMovement()Z", ordinal = 0))
    private boolean disableSprintSneak(Input input) {
        if (ViaLoadingBase.getClassWrapper().getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_14_1)) {
            return input.movementForward >= 0.8F;
        }

        return input.hasForwardMovement();
    }

    @Redirect(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isTouchingWater()Z"))
    private boolean redirectTickMovement(ClientPlayerEntity self) {
        if (ViaLoadingBase.getClassWrapper().getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_12_2)) {
            return false; // Disable all water related movement
        }

        return self.isTouchingWater();
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;sendSprintingPacket()V"))
    public void removeSprintingPacket(ClientPlayerEntity instance) {
        if (ViaLoadingBase.getClassWrapper().getTargetVersion().isNewerThanOrEqualTo(ProtocolVersion.v1_19_3)) {
            sendSprintingPacket();
        }
    }

    @Override
    public void viafabricplus_cancelSwingOnce() {
        protocolhack_areSwingCanceledThisTick = true;
    }
}

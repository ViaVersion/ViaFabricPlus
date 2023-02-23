package de.florianmichael.viafabricplus.injection.mixin.fixes.entity;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.SkeletonHorseEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("ConstantValue")
@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity extends Entity {

    @Shadow
    protected boolean jumping;

    public MixinLivingEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "getPreferredEquipmentSlot", at = @At("HEAD"), cancellable = true)
    private static void removeShieldSlotPreference(ItemStack stack, CallbackInfoReturnable<EquipmentSlot> cir) {
        if (ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_9_3) && stack.isOf(Items.SHIELD)) {
            cir.setReturnValue(EquipmentSlot.MAINHAND);
        }
    }

    @Shadow
    protected abstract float getBaseMovementSpeedMultiplier();

    @Redirect(method = "applyMovementInput", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/LivingEntity;jumping:Z"))
    private boolean disableJumpOnLadder(LivingEntity self) {
        if (ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_13_2)) {
            return false;
        }

        return jumping;
    }

    @Redirect(method = "travel",
            slice = @Slice(from = @At(value = "FIELD", target = "Lnet/minecraft/entity/effect/StatusEffects;DOLPHINS_GRACE:Lnet/minecraft/entity/effect/StatusEffect;")),
            at = @At(value = "FIELD", target = "Lnet/minecraft/entity/LivingEntity;horizontalCollision:Z", ordinal = 0))
    private boolean disableClimbing(LivingEntity self) {
        if (ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_13_2)) {
            return false;
        }

        return horizontalCollision;
    }

    @ModifyVariable(method = "applyFluidMovingSpeed", ordinal = 0, at = @At("HEAD"), argsOnly = true)
    private boolean modifyMovingDown(boolean movingDown) {
        if (ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_13_2)) {
            return true;
        }

        return movingDown;
    }

    @Redirect(method = "travel",
            slice = @Slice(from = @At(value = "FIELD", target = "Lnet/minecraft/entity/effect/StatusEffects;LEVITATION:Lnet/minecraft/entity/effect/StatusEffect;", ordinal = 0)),
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;onLanding()V", ordinal = 0))
    private void dontResetLevitationFallDistance(LivingEntity instance) {
        if (ViaLoadingBase.getTargetVersion().isNewerThan(ProtocolVersion.v1_12_2)) {
            instance.onLanding();
        }
    }

    @Redirect(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;isSprinting()Z", ordinal = 0))
    private boolean modifySwimSprintSpeed(LivingEntity self) {
        if (ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_12_2)) {
            return false;
        }
        return self.isSprinting();
    }

    @Redirect(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getFluidHeight(Lnet/minecraft/registry/tag/TagKey;)D"))
    private double redirectFluidHeight(LivingEntity instance, TagKey<Fluid> tagKey) {
        if (ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_12_2) && tagKey == FluidTags.WATER) {
            if (instance.getFluidHeight(tagKey) > 0) {
                return 1;
            }
        }
        return instance.getFluidHeight(tagKey);
    }

    @Inject(method = "applyFluidMovingSpeed", at = @At("HEAD"), cancellable = true)
    private void modifySwimSprintFallSpeed(double gravity, boolean movingDown, Vec3d velocity, CallbackInfoReturnable<Vec3d> ci) {
        if (ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_12_2) && !hasNoGravity()) {
            ci.setReturnValue(new Vec3d(velocity.x, velocity.y - 0.02, velocity.z));
        }
    }

    @ModifyConstant(method = "tickMovement", constant = @Constant(doubleValue = 0.003D))
    public double modifyVelocityZero(final double constant) {
        if (ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_8)) {
            return 0.005D;
        }
        return constant;
    }

    @Inject(method = "canEnterTrapdoor", at = @At("HEAD"), cancellable = true)
    private void onCanEnterTrapdoor(CallbackInfoReturnable<Boolean> ci) {
        if (ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_8)) {
            ci.setReturnValue(false);
        }
    }

    @ModifyConstant(method = "travel", constant = @Constant(floatValue = 0.9F))
    private float changeEntitySpeed(float constant) {
        if (ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_12_2)) {
            //noinspection ConstantConditions
            if ((Entity) this instanceof SkeletonHorseEntity) {
                return this.getBaseMovementSpeedMultiplier(); // 0.96F
            }
            return 0.8F;
        }
        return constant;
    }

    @Redirect(method = "travel", at = @At(value = "INVOKE", target = "Ljava/lang/Math;cos(D)D"))
    public double fixCosTable(double a) {
        if (ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_18_2)) {
            return MathHelper.cos((float) a);
        }
        return Math.cos(a);
    }

    @Redirect(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getFluidHeight(Lnet/minecraft/registry/tag/TagKey;)D"))
    public double fixLavaMovement(LivingEntity instance, TagKey<Fluid> tagKey) {
        double height = instance.getFluidHeight(tagKey);

        if (ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_15_2)) {
            height += getSwimHeight() + 4;
        }
        return height;
    }

    @ModifyConstant(method = "isBlocking", constant = @Constant(intValue = 5))
    public int shieldBlockCounter(int constant) {
        if(ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_8)) {
            return 0;
        }
        return constant;
    }

    @Redirect(method = "tickCramming", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;isClient()Z"))
    public boolean revertOnlyPlayerCramming(World instance) {
        if (ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_19_1)) {
            return false;
        }
        return instance.isClient();
    }
}

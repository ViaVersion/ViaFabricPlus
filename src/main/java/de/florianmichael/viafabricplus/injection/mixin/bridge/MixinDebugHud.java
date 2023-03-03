package de.florianmichael.viafabricplus.injection.mixin.bridge;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.viafabricplus.ViaFabricPlus;
import de.florianmichael.viafabricplus.settings.groups.BridgeSettings;
import de.florianmichael.viafabricplus.util.ScreenUtil;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.DebugHud;
import net.minecraft.util.Formatting;
import net.raphimc.vialegacy.api.LegacyProtocolVersion;
import net.raphimc.vialegacy.protocols.release.protocol1_2_1_3to1_1.storage.SeedStorage;
import net.raphimc.vialegacy.protocols.release.protocol1_8to1_7_6_10.storage.EntityTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Mixin(DebugHud.class)
public class MixinDebugHud {

    @Inject(method = "getLeftText", at = @At("RETURN"))
    public void addViaFabricPlusInformation(CallbackInfoReturnable<List<String>> cir) {
        if (MinecraftClient.getInstance().isInSingleplayer() || !BridgeSettings.getClassWrapper().showExtraInformationInDebugHud.getValue()) return;

        final List<String> information = new ArrayList<>();
        if (MinecraftClient.getInstance().getNetworkHandler() != null) {
            final UserConnection userConnection = MinecraftClient.getInstance().getNetworkHandler().getConnection().channel.attr(ViaFabricPlus.LOCAL_VIA_CONNECTION).get();

            information.add("Pipeline count: " + userConnection.getProtocolInfo().getPipeline().pipes().size());
            information.add("Target version: " + ViaLoadingBase.getClassWrapper().getTargetVersion().getName() + " (" + ViaLoadingBase.getClassWrapper().getTargetVersion().getVersion() + ")");

            if (ViaLoadingBase.getClassWrapper().getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_7_6)) {
                final EntityTracker entityTracker1_7_10 = userConnection.get(EntityTracker.class);
                if (entityTracker1_7_10 != null) {
                    information.add("Entity Tracker (" + ProtocolVersion.v1_7_6.getName() + "): " + entityTracker1_7_10.getTrackedEntities().size());
                }
                if (ViaLoadingBase.getClassWrapper().getTargetVersion().isOlderThanOrEqualTo(LegacyProtocolVersion.r1_5_2)) {
                    final net.raphimc.vialegacy.protocols.release.protocol1_6_1to1_5_2.storage.EntityTracker entityTracker1_5_2 = userConnection.get(net.raphimc.vialegacy.protocols.release.protocol1_6_1to1_5_2.storage.EntityTracker.class);
                    if (entityTracker1_5_2 != null) {
                        information.add("Entity Tracker (" + LegacyProtocolVersion.r1_5_2.getName() + "): " + entityTracker1_5_2.getTrackedEntities().size());
                    }
                    if (ViaLoadingBase.getClassWrapper().getTargetVersion().isOlderThanOrEqualTo(LegacyProtocolVersion.r1_2_4tor1_2_5)) {
                        final net.raphimc.vialegacy.protocols.release.protocol1_3_1_2to1_2_4_5.storage.EntityTracker entityTracker1_2_4_5 = userConnection.get(net.raphimc.vialegacy.protocols.release.protocol1_3_1_2to1_2_4_5.storage.EntityTracker.class);
                        if (entityTracker1_2_4_5 != null) {
                            information.add("Entity Tracker (" + LegacyProtocolVersion.r1_2_4tor1_2_5.getName() + "): " + entityTracker1_2_4_5.getTrackedEntities().size());
                        }
                        if (ViaLoadingBase.getClassWrapper().getTargetVersion().isOlderThanOrEqualTo(LegacyProtocolVersion.r1_1)) {
                            final SeedStorage seedStorage = userConnection.get(SeedStorage.class);
                            if (seedStorage != null) {
                                information.add("World Seed (" + LegacyProtocolVersion.r1_1.getName() + "): " + seedStorage.seed);
                            }
                        }
                    }
                }
            }
        }
        cir.getReturnValue().addAll(information.stream().map(ScreenUtil::prefixedMessage).toList());
    }
}

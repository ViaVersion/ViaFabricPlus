package de.florianmichael.viafabricplus;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.providers.HandItemProvider;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.providers.MovementTransmitterProvider;
import de.florianmichael.viafabricplus.definition.ChatLengthDefinition;
import de.florianmichael.viafabricplus.definition.ItemReleaseVersionDefinition;
import de.florianmichael.viafabricplus.definition.PackFormatsDefinition;
import de.florianmichael.viafabricplus.definition.c0_30.ClassicItemSelectionScreen;
import de.florianmichael.viafabricplus.definition.v1_19_0.provider.CommandArgumentsProvider;
import de.florianmichael.viafabricplus.definition.v1_8_x.ArmorPointsDefinition;
import de.florianmichael.viafabricplus.platform.ViaAprilFoolsPlatformImpl;
import de.florianmichael.viafabricplus.platform.ViaLegacyPlatformImpl;
import de.florianmichael.viafabricplus.provider.*;
import de.florianmichael.viafabricplus.settings.SettingGroup;
import de.florianmichael.viafabricplus.settings.groups.*;
import de.florianmichael.viafabricplus.util.SettingsSave;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import de.florianmichael.vialoadingbase.platform.InternalProtocolList;
import de.florianmichael.vialoadingbase.platform.SubPlatform;
import io.netty.channel.DefaultEventLoop;
import io.netty.util.AttributeKey;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.ClientConnection;
import net.raphimc.viaaprilfools.api.AprilFoolsProtocolVersion;
import net.raphimc.vialegacy.api.LegacyProtocolVersion;
import net.raphimc.vialegacy.protocols.classic.protocola1_0_15toc0_28_30.providers.ClassicMPPassProvider;
import net.raphimc.vialegacy.protocols.classic.protocola1_0_15toc0_28_30.providers.ClassicWorldHeightProvider;
import net.raphimc.vialegacy.protocols.release.protocol1_3_1_2to1_2_4_5.providers.OldAuthProvider;
import net.raphimc.vialegacy.protocols.release.protocol1_7_2_5to1_6_4.providers.EncryptionProvider;
import net.raphimc.vialegacy.protocols.release.protocol1_8to1_7_6_10.providers.GameProfileFetcher;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ViaFabricPlus {
    public final static File RUN_DIRECTORY = new File("ViaFabricPlus");
    public final static AttributeKey<UserConnection> LOCAL_VIA_CONNECTION = AttributeKey.newInstance("viafabricplus-via-connection");
    public final static AttributeKey<ClientConnection> LOCAL_MINECRAFT_CONNECTION = AttributeKey.newInstance("viafabricplus-minecraft-connection");

    private final static ViaFabricPlus self = new ViaFabricPlus();

    private final List<SettingGroup> settingGroups = new ArrayList<>();

    private final SubPlatform SUB_PLATFORM_VIA_LEGACY = new SubPlatform("ViaLegacy", () -> true, ViaLegacyPlatformImpl::new, protocolVersions -> {
        final List<ProtocolVersion> legacyProtocols = new ArrayList<>(LegacyProtocolVersion.PROTOCOLS);
        Collections.reverse(legacyProtocols);

        legacyProtocols.remove(LegacyProtocolVersion.c0_30cpe);
        legacyProtocols.add(legacyProtocols.indexOf(LegacyProtocolVersion.c0_28toc0_30) + 1, LegacyProtocolVersion.c0_30cpe);

        protocolVersions.addAll(legacyProtocols);
    });
    private final SubPlatform SUB_PLATFORM_VIA_APRIL_FOOLS = new SubPlatform("ViaAprilFools", () -> true, ViaAprilFoolsPlatformImpl::new, protocolVersions -> {
        protocolVersions.add(protocolVersions.indexOf(ProtocolVersion.v1_14) + 1,AprilFoolsProtocolVersion.s3d_shareware);
        protocolVersions.add(protocolVersions.indexOf(ProtocolVersion.v1_16) + 1, AprilFoolsProtocolVersion.s20w14infinite);
        protocolVersions.add(protocolVersions.indexOf(ProtocolVersion.v1_16_2) + 1, AprilFoolsProtocolVersion.sCombatTest8c);
    });

    public void preLoad() {
        ViaLoadingBase.ViaLoadingBaseBuilder builder = ViaLoadingBase.ViaLoadingBaseBuilder.create();

        builder = builder.subPlatform(SUB_PLATFORM_VIA_LEGACY);
        builder = builder.subPlatform(SUB_PLATFORM_VIA_APRIL_FOOLS);

        builder = builder.runDirectory(RUN_DIRECTORY);
        builder = builder.nativeVersion(SharedConstants.getProtocolVersion());
        builder = builder.forceNativeVersionCondition(() -> {
            if (MinecraftClient.getInstance() == null) return true;

            return MinecraftClient.getInstance().isInSingleplayer();
        });
        builder = builder.eventLoop(new DefaultEventLoop());
        builder = builder.providers(providers -> {
            providers.use(MovementTransmitterProvider.class, new ViaFabricPlusMovementTransmitterProvider());
            providers.use(HandItemProvider.class, new ViaFabricPlusHandItemProvider());

            providers.use(CommandArgumentsProvider.class, new ViaFabricPlusCommandArgumentsProvider());

            providers.use(OldAuthProvider.class, new ViaFabricPlusOldAuthProvider());
            providers.use(ClassicWorldHeightProvider.class, new ViaFabricPlusClassicWorldHeightProvider());
            providers.use(EncryptionProvider.class, new ViaFabricPlusEncryptionProvider());
            providers.use(GameProfileFetcher.class, new ViaFabricPlusGameProfileFetcher());
            providers.use(ClassicMPPassProvider.class, new ViaFabricPlusClassicMPPassProvider());

            Via.getManager().setDebug(true);
        });
        builder = builder.onProtocolReload(protocolVersion -> {
            FabricLoader.getInstance().getEntrypoints("viafabricplus", ViaFabricPlusAddon.class).forEach(viaFabricPlusAddon -> viaFabricPlusAddon.onChangeVersion(protocolVersion));
            ItemReleaseVersionDefinition.reload(protocolVersion);
            ChatLengthDefinition.reload(protocolVersion);
            if (protocolVersion.isOlderThanOrEqualTo(LegacyProtocolVersion.c0_28toc0_30)) {
                ClassicItemSelectionScreen.INSTANCE.reload(protocolVersion);
            }
        });
        builder.build();
    }

    public void postLoad() throws Exception {
        FabricLoader.getInstance().getEntrypoints("viafabricplus", ViaFabricPlusAddon.class).forEach(ViaFabricPlusAddon::onLoad);

        loadGroup(
                GeneralSettings.getClassWrapper(),
                BridgeSettings.getClassWrapper(),
                MPPassSettings.getClassWrapper(),
                VisualSettings.getClassWrapper(),
                DebugSettings.getClassWrapper()
        );

        SettingsSave.load(this);

        PackFormatsDefinition.load();
        ItemReleaseVersionDefinition.load();
        ArmorPointsDefinition.load();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                SettingsSave.save(this);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }));
    }

    public void loadGroup(final SettingGroup... groups) {
        this.settingGroups.addAll(Arrays.asList(groups));
    }

    public List<SettingGroup> getSettingGroups() {
        return settingGroups;
    }

    public static ViaFabricPlus getClassWrapper() {
        return ViaFabricPlus.self;
    }
}

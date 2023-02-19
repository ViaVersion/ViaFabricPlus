package de.florianmichael.everyprotocol;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.libs.gson.JsonArray;
import com.viaversion.viaversion.libs.gson.JsonObject;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.providers.HandItemProvider;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.providers.MovementTransmitterProvider;
import de.florianmichael.everyprotocol.platform.ViaAprilFoolsPlatformImpl;
import de.florianmichael.everyprotocol.platform.ViaLegacyPlatformImpl;
import de.florianmichael.everyprotocol.provider.EveryProtocolHandItemProvider;
import de.florianmichael.everyprotocol.provider.EveryProtocolMovementTransmitterProvider;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import de.florianmichael.vialoadingbase.api.SubPlatform;
import io.netty.channel.DefaultEventLoop;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.Person;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.raphimc.viaaprilfools.api.AprilFoolsProtocolVersion;
import net.raphimc.vialegacy.api.LegacyProtocolVersions;

import java.util.List;

public class EveryProtocol {
    private final static EveryProtocol self = new EveryProtocol();

    private final SubPlatform SUB_PLATFORM_VIA_LEGACY = new SubPlatform("ViaLegacy", () -> true, ViaLegacyPlatformImpl::new, protocolVersions -> protocolVersions.addAll(LegacyProtocolVersions.PROTOCOLS));
    private final SubPlatform SUB_PLATFORM_VIA_APRIL_FOOLS = new SubPlatform("ViaAprilFools", () -> true, ViaAprilFoolsPlatformImpl::new, this::invokeAprilFoolsProtocols);

    protected void invokeAprilFoolsProtocols(List<ProtocolVersion> origin) {
        final int v1_14Index = origin.indexOf(ProtocolVersion.v1_14);
        final int v1_16Index = origin.indexOf(ProtocolVersion.v1_16);
        final int v1_16_2Index = origin.indexOf(ProtocolVersion.v1_16_2);

        origin.add(v1_14Index - 1,AprilFoolsProtocolVersion.s3d_shareware);
        origin.add(v1_16Index - 1, AprilFoolsProtocolVersion.s20w14infinite);
        origin.add(v1_16_2Index - 1, AprilFoolsProtocolVersion.sCombatTest8c);
    }

    public void create() {
        ViaLoadingBase.ViaLoadingBaseBuilder builder = ViaLoadingBase.ViaLoadingBaseBuilder.create();

        builder = builder.subPlatform(SUB_PLATFORM_VIA_LEGACY);
        builder = builder.subPlatform(SUB_PLATFORM_VIA_APRIL_FOOLS);

        builder = builder.runDirectory(MinecraftClient.getInstance().runDirectory);
        builder = builder.nativeVersion(SharedConstants.getProtocolVersion());
        builder = builder.singlePlayerProvider(() -> MinecraftClient.getInstance().isInSingleplayer());
        builder = builder.eventLoop(new DefaultEventLoop());
        builder = builder.dumpCreator(() -> {
            final JsonObject parentNode = new JsonObject();
            final JsonArray modsNode = new JsonArray();
            for (ModContainer mod : FabricLoader.getInstance().getAllMods()) {
                final JsonObject modNode = new JsonObject();
                modNode.addProperty("id", mod.getMetadata().getId());
                modNode.addProperty("name", mod.getMetadata().getName());
                modNode.addProperty("version", mod.getMetadata().getVersion().getFriendlyString());

                final JsonArray authorsNode = new JsonArray();
                for (Person author : mod.getMetadata().getAuthors()) {
                    final JsonObject infoNode = new JsonObject();

                    final JsonObject contactNode = new JsonObject();
                    author.getContact().asMap().forEach(contactNode::addProperty);
                    if (contactNode.size() != 0) {
                        infoNode.add("contact", contactNode);
                    }
                    infoNode.addProperty("name", author.getName());

                    authorsNode.add(infoNode);
                }
                modNode.add("author", authorsNode);
                modsNode.add(modNode);
            }
            parentNode.add("mods", modsNode);
            parentNode.addProperty("native version", SharedConstants.getProtocolVersion());
            return parentNode;
        });
        builder = builder.viaProviderCreator(providers -> {
            providers.use(MovementTransmitterProvider.class, new EveryProtocolMovementTransmitterProvider());
            providers.use(HandItemProvider.class, new EveryProtocolHandItemProvider());
        });

        builder.build();
    }

    public static EveryProtocol getClassWrapper() {
        return EveryProtocol.self;
    }
}

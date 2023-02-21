/**
 * --FLORIAN MICHAEL PRIVATE LICENCE v1.2--
 *
 * This file / project is protected and is the intellectual property of Florian Michael (aka. EnZaXD),
 * any use (be it private or public, be it copying or using for own use, be it publishing or modifying) of this
 * file / project is prohibited. It requires in that use a written permission with official signature of the owner
 * "Florian Michael". "Florian Michael" receives the right to control and manage this file / project. This right is not
 * cancelled by copying or removing the license and in case of violation a criminal consequence is to be expected.
 * The owner "Florian Michael" is free to change this license. The creator assumes no responsibility for any infringements
 * that have arisen, are arising or will arise from this project / file. If this licence is used anywhere,
 * the latest version published by the author Florian Michael (aka EnZaXD) always applies automatically.
 *
 * Changelog:
 *     v1.0:
 *         Added License
 *     v1.1:
 *         Ownership withdrawn
 *     v1.2:
 *         Version-independent validity and automatic renewal
 */

package de.florianmichael.viafabricplus;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.libs.gson.JsonArray;
import com.viaversion.viaversion.libs.gson.JsonObject;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.providers.HandItemProvider;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.providers.MovementTransmitterProvider;
import de.florianmichael.viafabricplus.definition.ItemReleaseVersionDefinition;
import de.florianmichael.viafabricplus.definition.PackFormatsDefinition;
import de.florianmichael.viafabricplus.platform.ViaAprilFoolsPlatformImpl;
import de.florianmichael.viafabricplus.platform.ViaLegacyPlatformImpl;
import de.florianmichael.viafabricplus.provider.ViaFabricPlusHandItemProvider;
import de.florianmichael.viafabricplus.provider.ViaFabricPlusMovementTransmitterProvider;
import de.florianmichael.viafabricplus.value.ValueHolder;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import de.florianmichael.vialoadingbase.api.SubPlatform;
import io.netty.channel.DefaultEventLoop;
import io.netty.util.AttributeKey;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.Person;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.raphimc.viaaprilfools.api.AprilFoolsProtocolVersion;
import net.raphimc.vialegacy.api.LegacyProtocolVersion;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO
 *  - Add ViaFabricPlus-Visual
 *  - Add ViaFabricPlus-Emulation
 */
public class ViaFabricPlus {
    public final static File RUN_DIRECTORY = new File("ViaFabricPlus");
    public final static AttributeKey<UserConnection> LOCAL_USER_CONNECTION = AttributeKey.newInstance("viafabricplus-via-connection");

    private final static ViaFabricPlus self = new ViaFabricPlus();

    private final SubPlatform SUB_PLATFORM_VIA_LEGACY = new SubPlatform("ViaLegacy", () -> true, ViaLegacyPlatformImpl::new, protocolVersions -> protocolVersions.addAll(LegacyProtocolVersion.PROTOCOLS));
    private final SubPlatform SUB_PLATFORM_VIA_APRIL_FOOLS = new SubPlatform("ViaAprilFools", () -> true, ViaAprilFoolsPlatformImpl::new, this::invokeAprilFoolsProtocols);

    private final List<Item> availableItemsInTargetVersion = new ArrayList<>();

    protected void invokeAprilFoolsProtocols(List<ProtocolVersion> origin) {
        final int v1_14Index = origin.indexOf(ProtocolVersion.v1_14);
        final int v1_16Index = origin.indexOf(ProtocolVersion.v1_16);
        final int v1_16_2Index = origin.indexOf(ProtocolVersion.v1_16_2);

        origin.add(v1_14Index - 1,AprilFoolsProtocolVersion.s3d_shareware);
        origin.add(v1_16Index - 1, AprilFoolsProtocolVersion.s20w14infinite);
        origin.add(v1_16_2Index - 1, AprilFoolsProtocolVersion.sCombatTest8c);
    }

    public void preLoad() {
        ViaLoadingBase.ViaLoadingBaseBuilder builder = ViaLoadingBase.ViaLoadingBaseBuilder.create();

        builder = builder.subPlatform(SUB_PLATFORM_VIA_LEGACY);
        builder = builder.subPlatform(SUB_PLATFORM_VIA_APRIL_FOOLS);

        builder = builder.runDirectory(RUN_DIRECTORY);
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
            providers.use(MovementTransmitterProvider.class, new ViaFabricPlusMovementTransmitterProvider());
            providers.use(HandItemProvider.class, new ViaFabricPlusHandItemProvider());
        });
        builder = builder.protocolReloader(protocolVersion -> {
            availableItemsInTargetVersion.clear();
            availableItemsInTargetVersion.addAll(Registries.ITEM.stream().filter(item -> ItemReleaseVersionDefinition.contains(item, protocolVersion)).toList());
        });
        builder.build();
    }

    public void postLoad() throws Exception {
        ValueHolder.setup();

        PackFormatsDefinition.load();
        ItemReleaseVersionDefinition.load();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                this.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }));
    }

    public void close() throws Exception {
        ValueHolder.save();
    }

    public List<Item> getAvailableItemsInTargetVersion() {
        return availableItemsInTargetVersion;
    }

    public static ViaFabricPlus getClassWrapper() {
        return ViaFabricPlus.self;
    }
}

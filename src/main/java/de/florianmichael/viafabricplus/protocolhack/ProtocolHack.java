/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2023 FlorianMichael/EnZaXD and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.florianmichael.viafabricplus.protocolhack;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.providers.HandItemProvider;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.providers.MovementTransmitterProvider;
import de.florianmichael.viafabricplus.ViaFabricPlus;
import de.florianmichael.viafabricplus.definition.v1_19_0.provider.CommandArgumentsProvider;
import de.florianmichael.viafabricplus.event.ChangeProtocolVersionCallback;
import de.florianmichael.viafabricplus.event.FinishViaLoadingBaseStartupCallback;
import de.florianmichael.viafabricplus.protocolhack.platform.ViaAprilFoolsPlatformImpl;
import de.florianmichael.viafabricplus.protocolhack.platform.ViaBedrockPlatformImpl;
import de.florianmichael.viafabricplus.protocolhack.platform.ViaLegacyPlatformImpl;
import de.florianmichael.viafabricplus.protocolhack.provider.*;
import de.florianmichael.viafabricplus.protocolhack.provider.viabedrock.ViaFabricPlusNettyPipelineProvider;
import de.florianmichael.viafabricplus.protocolhack.provider.vialegacy.*;
import de.florianmichael.viafabricplus.protocolhack.provider.viaversion.ViaFabricPlusHandItemProvider;
import de.florianmichael.viafabricplus.protocolhack.provider.viaversion.ViaFabricPlusMovementTransmitterProvider;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import de.florianmichael.vialoadingbase.platform.SubPlatform;
import io.netty.util.AttributeKey;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.ClientConnection;
import net.raphimc.viaaprilfools.api.AprilFoolsProtocolVersion;
import net.raphimc.viabedrock.api.BedrockProtocolVersion;
import net.raphimc.viabedrock.protocol.providers.NettyPipelineProvider;
import net.raphimc.vialegacy.api.LegacyProtocolVersion;
import net.raphimc.vialegacy.protocols.classic.protocola1_0_15toc0_28_30.providers.ClassicCustomCommandProvider;
import net.raphimc.vialegacy.protocols.classic.protocola1_0_15toc0_28_30.providers.ClassicMPPassProvider;
import net.raphimc.vialegacy.protocols.classic.protocola1_0_15toc0_28_30.providers.ClassicWorldHeightProvider;
import net.raphimc.vialegacy.protocols.release.protocol1_3_1_2to1_2_4_5.providers.OldAuthProvider;
import net.raphimc.vialegacy.protocols.release.protocol1_7_2_5to1_6_4.providers.EncryptionProvider;
import net.raphimc.vialegacy.protocols.release.protocol1_8to1_7_6_10.providers.GameProfileFetcher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProtocolHack {
    public final static AttributeKey<UserConnection> LOCAL_VIA_CONNECTION = AttributeKey.newInstance("viafabricplus-via-connection");
    public final static AttributeKey<ClientConnection> LOCAL_MINECRAFT_CONNECTION = AttributeKey.newInstance("viafabricplus-minecraft-connection");

    public ProtocolHack() {
        ViaLoadingBase.ViaLoadingBaseBuilder builder = ViaLoadingBase.ViaLoadingBaseBuilder.create();

        builder = builder.subPlatform(new SubPlatform("ViaBedrock", () -> true, ViaBedrockPlatformImpl::new, protocolVersions -> protocolVersions.add(BedrockProtocolVersion.bedrockLatest)), 0);
        builder = builder.subPlatform(new SubPlatform("ViaLegacy", () -> true, ViaLegacyPlatformImpl::new, protocolVersions -> {
            final List<ProtocolVersion> legacyProtocols = new ArrayList<>(LegacyProtocolVersion.PROTOCOLS);
            Collections.reverse(legacyProtocols);

            legacyProtocols.remove(LegacyProtocolVersion.c0_30cpe);
            legacyProtocols.add(legacyProtocols.indexOf(LegacyProtocolVersion.c0_28toc0_30) + 1, LegacyProtocolVersion.c0_30cpe);

            protocolVersions.addAll(legacyProtocols);
        }));
        builder = builder.subPlatform(new SubPlatform("ViaAprilFools", () -> true, ViaAprilFoolsPlatformImpl::new, protocolVersions -> {
            protocolVersions.add(protocolVersions.indexOf(ProtocolVersion.v1_14) + 1, AprilFoolsProtocolVersion.s3d_shareware);
            protocolVersions.add(protocolVersions.indexOf(ProtocolVersion.v1_16) + 1, AprilFoolsProtocolVersion.s20w14infinite);
            protocolVersions.add(protocolVersions.indexOf(ProtocolVersion.v1_16_2) + 1, AprilFoolsProtocolVersion.sCombatTest8c);
        }));

        builder = builder.runDirectory(ViaFabricPlus.RUN_DIRECTORY);
        builder = builder.nativeVersion(SharedConstants.getProtocolVersion());
        builder = builder.forceNativeVersionCondition(() -> {
            if (MinecraftClient.getInstance() == null) return true;

            return MinecraftClient.getInstance().isInSingleplayer();
        });
        builder = builder.providers(providers -> {
            providers.use(MovementTransmitterProvider.class, new ViaFabricPlusMovementTransmitterProvider());
            providers.use(HandItemProvider.class, new ViaFabricPlusHandItemProvider());

            providers.use(CommandArgumentsProvider.class, new ViaFabricPlusCommandArgumentsProvider());

            providers.use(OldAuthProvider.class, new ViaFabricPlusOldAuthProvider());
            providers.use(ClassicWorldHeightProvider.class, new ViaFabricPlusClassicWorldHeightProvider());
            providers.use(EncryptionProvider.class, new ViaFabricPlusEncryptionProvider());
            providers.use(GameProfileFetcher.class, new ViaFabricPlusGameProfileFetcher());
            providers.use(ClassicMPPassProvider.class, new ViaFabricPlusClassicMPPassProvider());
            providers.use(ClassicCustomCommandProvider.class, new ViaFabricPlusClassicCustomCommandProvider());
            providers.use(NettyPipelineProvider.class, new ViaFabricPlusNettyPipelineProvider());
        });
        builder = builder.onProtocolReload(protocolVersion -> ChangeProtocolVersionCallback.EVENT.invoker().onChangeProtocolVersion(protocolVersion));
        builder.build();

        FinishViaLoadingBaseStartupCallback.EVENT.invoker().onFinishViaLoadingBaseStartup();
    }
}

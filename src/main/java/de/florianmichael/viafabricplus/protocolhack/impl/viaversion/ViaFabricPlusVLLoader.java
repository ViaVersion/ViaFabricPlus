/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2024 FlorianMichael/EnZaXD
 * Copyright (C) 2021-2024 RK_01/RaphiMC and contributors
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

package de.florianmichael.viafabricplus.protocolhack.impl.viaversion;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.minecraft.signature.SignableCommandArgumentsProvider;
import com.viaversion.viaversion.api.platform.providers.ViaProviders;
import com.viaversion.viaversion.api.protocol.version.VersionProvider;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.providers.PlayerLookTargetProvider;
import com.viaversion.viaversion.protocols.protocol1_16to1_15_2.provider.PlayerAbilitiesProvider;
import com.viaversion.viaversion.protocols.protocol1_19to1_18_2.provider.AckSequenceProvider;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.providers.HandItemProvider;
import de.florianmichael.viafabricplus.protocolhack.impl.provider.viabedrock.ViaFabricPlusNettyPipelineProvider;
import de.florianmichael.viafabricplus.protocolhack.impl.provider.viabedrock.ViaFabricPlusTransferProvider;
import de.florianmichael.viafabricplus.protocolhack.impl.provider.vialegacy.*;
import de.florianmichael.viafabricplus.protocolhack.impl.provider.viaversion.*;
import de.florianmichael.viafabricplus.settings.impl.GeneralSettings;
import net.raphimc.viabedrock.protocol.providers.NettyPipelineProvider;
import net.raphimc.viabedrock.protocol.providers.TransferProvider;
import net.raphimc.vialegacy.protocols.alpha.protocolb1_0_1_1_1toa1_2_3_5_1_2_6.providers.AlphaInventoryProvider;
import net.raphimc.vialegacy.protocols.classic.protocola1_0_15toc0_28_30.providers.ClassicMPPassProvider;
import net.raphimc.vialegacy.protocols.classic.protocola1_0_15toc0_28_30.providers.ClassicWorldHeightProvider;
import net.raphimc.vialegacy.protocols.release.protocol1_3_1_2to1_2_4_5.providers.OldAuthProvider;
import net.raphimc.vialegacy.protocols.release.protocol1_7_2_5to1_6_4.providers.EncryptionProvider;
import net.raphimc.vialegacy.protocols.release.protocol1_8to1_7_6_10.providers.GameProfileFetcher;
import net.raphimc.vialoader.impl.viaversion.VLLoader;

public class ViaFabricPlusVLLoader extends VLLoader {

    @Override
    public void load() {
        super.load();

        final ViaProviders providers = Via.getManager().getProviders();

        providers.use(VersionProvider.class, new ViaFabricPlusBaseVersionProvider());

        providers.use(HandItemProvider.class, new ViaFabricPlusHandItemProvider());
        providers.use(PlayerLookTargetProvider.class, new ViaFabricPlusPlayerLookTargetProvider());
        providers.use(PlayerAbilitiesProvider.class, new ViaFabricPlusPlayerAbilitiesProvider());
        providers.use(SignableCommandArgumentsProvider.class, new ViaFabricPlusCommandArgumentsProvider());
        providers.use(AckSequenceProvider.class, new ViaFabricPlusAckSequenceProvider());

        providers.use(OldAuthProvider.class, new ViaFabricPlusOldAuthProvider());
        providers.use(ClassicWorldHeightProvider.class, new ViaFabricPlusClassicWorldHeightProvider());
        providers.use(EncryptionProvider.class, new ViaFabricPlusEncryptionProvider());
        providers.use(GameProfileFetcher.class, new ViaFabricPlusGameProfileFetcher());
        providers.use(ClassicMPPassProvider.class, new ViaFabricPlusClassicMPPassProvider());
        if (GeneralSettings.global().emulateInventoryActionsInAlphaVersions.getValue()) {
            providers.use(AlphaInventoryProvider.class, new ViaFabricPlusAlphaInventoryProvider());
        }

        providers.use(NettyPipelineProvider.class, new ViaFabricPlusNettyPipelineProvider());
        providers.use(TransferProvider.class, new ViaFabricPlusTransferProvider());
    }

}

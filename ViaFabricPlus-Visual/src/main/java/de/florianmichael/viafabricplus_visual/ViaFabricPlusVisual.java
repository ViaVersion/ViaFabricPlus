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

package de.florianmichael.viafabricplus_visual;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.viafabricplus.platform.ProtocolRange;
import de.florianmichael.viafabricplus.value.impl.ProtocolSyncBooleanValue;
import de.florianmichael.viafabricplus_visual.definition.ArmorPointsDefinition;
import net.fabricmc.api.ClientModInitializer;
import net.raphimc.vialegacy.api.LegacyProtocolVersion;

public class ViaFabricPlusVisual implements ClientModInitializer {

    // 1.19.2 -> 1.19
    public final static ProtocolSyncBooleanValue disableSecureChatWarning = new ProtocolSyncBooleanValue("Disable secure chat  warning", ProtocolRange.andOlder(ProtocolVersion.v1_19));

    // 1.19 -> 1.18.2
    public final static ProtocolSyncBooleanValue hideSignatureIndicator = new ProtocolSyncBooleanValue("Hide signature indicator", ProtocolRange.andOlder(ProtocolVersion.v1_18_2));

    // 1.13 -> 1.12.2
    public final static ProtocolSyncBooleanValue replacePetrifiedOakSlab = new ProtocolSyncBooleanValue("Replace petrified oak slab", new ProtocolRange(ProtocolVersion.v1_12_2, LegacyProtocolVersion.r1_3_1tor1_3_2));

    // 1.9 -> 1.8.x
    public final static ProtocolSyncBooleanValue emulateArmorHud = new ProtocolSyncBooleanValue("Emulate Armor hud", ProtocolRange.andOlder(ProtocolVersion.v1_8));

    @Override
    public void onInitializeClient() {
        ArmorPointsDefinition.load();
    }
}

package de.florianmichael.viafabricplus.provider;

import com.viaversion.viaversion.util.Pair;
import de.florianmichael.viafabricplus.definition.v1_19_0.provider.CommandArgumentsProvider;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.command.argument.SignedArgumentList;

import java.util.List;

public class ViaFabricPlusCommandArgumentsProvider extends CommandArgumentsProvider {

    @Override
    public List<Pair<String, String>> getSignedArguments(String command) {
        final ClientPlayNetworkHandler clientPlayNetworkHandler = MinecraftClient.getInstance().getNetworkHandler();

        if (clientPlayNetworkHandler != null) {
            return SignedArgumentList.of(clientPlayNetworkHandler.getCommandDispatcher().parse(command, clientPlayNetworkHandler.getCommandSource())).arguments().stream().map(function -> new Pair<>(function.getNodeName(), function.value())).toList();
        }
        return super.getSignedArguments(command);
    }
}

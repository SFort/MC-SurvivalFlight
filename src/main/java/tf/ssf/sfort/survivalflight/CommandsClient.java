package tf.ssf.sfort.survivalflight;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;

public class CommandsClient {
    public static void registerClient(){
        try {
            LiteralArgumentBuilder<FabricClientCommandSource> command = LiteralArgumentBuilder.literal("survivalflight:client");
            command.then(LiteralArgumentBuilder.<FabricClientCommandSource>literal("ui").executes(c -> {
                MinecraftClient.getInstance().send(()->MinecraftClient.getInstance().setScreen(new ConfigScreen(null)));
                return 1;
            }));
            ClientCommandManager.getActiveDispatcher().register(command);
        }catch (Exception igore){
            System.out.println("\n\n\n"+igore);
        }
    }

}

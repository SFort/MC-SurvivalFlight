package tf.ssf.sfort.survivalflight;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.command.ServerCommandSource;

public class Commands {
    public static void register(){
        try {
            CommandRegistrationCallback.EVENT.register((dispatcher, dedi) -> {
                LiteralArgumentBuilder<ServerCommandSource> ssf = LiteralArgumentBuilder.literal("survivalflight");
                dispatcher.register(ssf.then(LiteralArgumentBuilder.<ServerCommandSource>literal("reload")
                                        .requires(pred -> pred.hasPermissionLevel(2))
                                        .executes((c) -> {
                                            Config.resetsettings();
                                            Config.reload_settings();
                                            return 1;
                                        })));
            });
        }catch (Exception ignore){}
    }
    public static void registerClient(){
        try {
            LiteralArgumentBuilder<FabricClientCommandSource> command = LiteralArgumentBuilder.literal("survivalflight:client");
            command.then(LiteralArgumentBuilder.<FabricClientCommandSource>literal("ui").executes(c -> {
                MinecraftClient.getInstance().send(()->MinecraftClient.getInstance().openScreen(new ConfigScreen(null)));
                return 1;
            }));
            ClientCommandManager.DISPATCHER.register(command);
        }catch (Exception igore){
            System.out.println("\n\n\n"+igore);
        }
    }
}

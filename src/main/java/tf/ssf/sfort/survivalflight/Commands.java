package tf.ssf.sfort.survivalflight;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
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

}

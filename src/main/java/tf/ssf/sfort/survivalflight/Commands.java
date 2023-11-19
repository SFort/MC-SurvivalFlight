package tf.ssf.sfort.survivalflight;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;

public class Commands {
    public static void register(){
        try {
            LiteralArgumentBuilder<ServerCommandSource> ssf = LiteralArgumentBuilder.literal("survivalflight");
            ssf.requires(s->s.hasPermissionLevel(2));
            ssf.then(LiteralArgumentBuilder.<ServerCommandSource>literal("reload")
                    .executes((c) -> {
                        Config.resetsettings();
                        Config.reload_settings();
                        return 1;
                    }));
            if (Config.allowWritingServerConfigs) {
                LiteralArgumentBuilder<ServerCommandSource> script = LiteralArgumentBuilder.literal("script");
                LiteralArgumentBuilder<ServerCommandSource> key = LiteralArgumentBuilder.<ServerCommandSource>literal("flight");
                key.then(RequiredArgumentBuilder.<ServerCommandSource, String>argument("script", StringArgumentType.string())
                        .executes((c) -> {
                            Config.writeFly(c.getArgument("script", String.class));
                            Config.resetsettings();
                            Config.reload_settings();
                            return 1;
                        }));
                script.then(key);
                key = LiteralArgumentBuilder.<ServerCommandSource>literal("elytra");
                key.then(RequiredArgumentBuilder.<ServerCommandSource, String>argument("script", StringArgumentType.string())
                        .executes((c) -> {
                            Config.writeElytra(c.getArgument("script", String.class));
                            Config.resetsettings();
                            Config.reload_settings();
                            return 1;
                        }));
                script.then(key);
                key = LiteralArgumentBuilder.<ServerCommandSource>literal("elytra_boost");
                key.then(RequiredArgumentBuilder.<ServerCommandSource, String>argument("script", StringArgumentType.string())
                        .executes((c) -> {
                            Config.writeElytraBoost(c.getArgument("script", String.class));
                            Config.resetsettings();
                            Config.reload_settings();
                            return 1;
                        }));
                script.then(key);
                ssf.then(script);
            }
            CommandRegistrationCallback.EVENT.register((dispatcher, dedi, environment) -> {
                dispatcher.register(ssf);
            });
        }catch (Exception ignore){}
    }

}

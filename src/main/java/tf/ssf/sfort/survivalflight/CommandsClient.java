package tf.ssf.sfort.survivalflight;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class CommandsClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        if (FabricLoader.getInstance().isModLoaded("fabric-command-api-v1")){
            CommandsClientRegister.registerClient();
        }
    }
}

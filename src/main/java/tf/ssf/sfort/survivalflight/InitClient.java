package tf.ssf.sfort.survivalflight;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class InitClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        if (FabricLoader.getInstance().isModLoaded("fabric-command-api-v1")){
            CommandsClient.registerClient();
        }
    }
}

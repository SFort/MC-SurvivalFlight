package tf.ssf.sfort.survivalflight;

import net.fabricmc.api.ModInitializer;
import tf.ssf.sfort.script.Default;

public class InitFScript implements ModInitializer {

    @Override
    public void onInitialize() {
        Default.SERVER_PLAYER_ENTITY.addProvider(FlightScript.INSTANCE);
    }
}

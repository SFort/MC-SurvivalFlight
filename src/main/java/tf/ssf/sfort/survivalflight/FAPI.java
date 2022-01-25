package tf.ssf.sfort.survivalflight;

import net.fabricmc.fabric.api.entity.event.v1.EntityElytraEvents;
import net.minecraft.server.network.ServerPlayerEntity;

public class FAPI {
    public static void register(){
        EntityElytraEvents.CUSTOM.register((entity, _a) -> {
            if (entity instanceof ServerPlayerEntity) {
                if (Config.canElytraFly != null && Config.canElytraFly.test((ServerPlayerEntity) entity)) return true;
                else if (entity.isFallFlying()) Config.exitElytra.accept((ServerPlayerEntity) entity);
                return false;
            }
            return true;
        });
    }
}

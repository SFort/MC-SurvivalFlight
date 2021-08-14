package tf.ssf.sfort.survivalflight.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tf.ssf.sfort.survivalflight.Config;

@Mixin(LivingEntity.class)
public abstract class Elytra extends Entity {
    public Elytra(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(at=@At("HEAD"), method= "tickFallFlying()V", cancellable = true)
    private void scriptCheck(CallbackInfo ci) {
        if (((Object)this) instanceof ServerPlayerEntity && this.getFlag(7) && Config.cantElytraFly.test((ServerPlayerEntity) (Object) this)) {
            this.setFlag(7, false);
            Config.exitElytra.accept((ServerPlayerEntity) (Object) this);
            ci.cancel();
        }
    }
}

package tf.ssf.sfort.survivalflight.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tf.ssf.sfort.survivalflight.Config;

@Mixin(LivingEntity.class)
public abstract class FallFlyTick extends Entity {
    @Shadow protected int roll;

    @Shadow public abstract boolean hasStatusEffect(StatusEffect effect);

    public FallFlyTick(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(at=@At("HEAD"), method="tickFallFlying()V", cancellable=true)
    private void scriptCheck(CallbackInfo ci) {
        if (((Object)this) instanceof ServerPlayerEntity && Config.canElytraFly != null) {
            boolean bl = this.getFlag(7);
            if (bl && !this.isOnGround() && !this.hasVehicle() && !this.hasStatusEffect(StatusEffects.LEVITATION) && Config.canElytraFly.test((ServerPlayerEntity) (Object) this)){
                if ((this.roll + 1) % 10 == 0)
                    this.emitGameEvent(GameEvent.ELYTRA_GLIDE);

            }else {
                if(bl) Config.exitElytra.accept((ServerPlayerEntity) (Object) this);
                this.setFlag(7, false);
            }
            ci.cancel();
        }
    }
}

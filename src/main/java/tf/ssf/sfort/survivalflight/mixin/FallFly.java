package tf.ssf.sfort.survivalflight.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tf.ssf.sfort.survivalflight.Config;

@Mixin(PlayerEntity.class)
public abstract class FallFly extends LivingEntity {

    @Shadow public abstract void startFallFlying();

    protected FallFly(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(at=@At(value="FIELD", target="Lnet/minecraft/entity/effect/StatusEffects;LEVITATION:Lnet/minecraft/entity/effect/StatusEffect;"),
            method="checkFallFlying()Z", cancellable=true)
    private void scriptCheck(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(true);
        if (Config.canElytraFly != null && ((Object)this) instanceof ServerPlayerEntity && Config.canElytraFly.test((ServerPlayerEntity) (Object) this)) {
            startFallFlying();
        }
    }
}

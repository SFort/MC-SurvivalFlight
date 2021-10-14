package tf.ssf.sfort.survivalflight.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FireworkRocketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tf.ssf.sfort.survivalflight.Config;
import tf.ssf.sfort.survivalflight.SPEA;

@Mixin(FireworkRocketItem.class)
public abstract class ElytraBoost {

    @Inject(at=@At("HEAD"), method= "use(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/TypedActionResult;",
            cancellable = true)
    private void scriptCheck(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
        if (user instanceof ServerPlayerEntity
                && ((SPEA)user).bf$isSurvivalLike()
                && Config.canElytraBoost != null
                && !Config.canElytraBoost.test((ServerPlayerEntity)user)
        )
            cir.setReturnValue(TypedActionResult.pass(user.getStackInHand(hand)));
    }
}

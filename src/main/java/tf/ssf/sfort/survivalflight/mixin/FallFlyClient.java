package tf.ssf.sfort.survivalflight.mixin;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ClientPlayerEntity.class)
public abstract class FallFlyClient {

    @ModifyVariable(at=@At("STORE"), method="tickMovement()V", ordinal = 0)
    private ItemStack tickMovement(ItemStack old) {
        return Items.ELYTRA.getDefaultStack();
    }
}

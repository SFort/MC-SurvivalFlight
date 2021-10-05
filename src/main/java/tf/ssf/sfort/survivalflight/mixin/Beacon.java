package tf.ssf.sfort.survivalflight.mixin;

import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import tf.ssf.sfort.survivalflight.Config;
import tf.ssf.sfort.survivalflight.SPEA;

import java.util.List;

@Mixin(BeaconBlockEntity.class)
public class Beacon {
    @Shadow private int level;

    @Inject(method = "applyPlayerEffects",
            at = @At(value = "TAIL"
            ), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private void onApplyPlayerEffects(CallbackInfo ci, double d, int y, int duration, Box bb, List<PlayerEntity> list) {
        if (level>= Config.beaconLevel)
        for (PlayerEntity player : list)
            if (player instanceof ServerPlayerEntity)
                ((SPEA) player).bf$beaconPing(bb, duration);
    }
}

package tf.ssf.sfort.survivalflight.mixin;

import net.minecraft.block.entity.ConduitBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import tf.ssf.sfort.survivalflight.Config;
import tf.ssf.sfort.survivalflight.SPEA;

import java.util.List;

@Mixin(ConduitBlockEntity.class)
public class Conduit {
    @Inject(method = "givePlayersEffects",
            at = @At(value = "TAIL"
            ), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private static void onApplyPlayerEffects(World world, BlockPos pos, List<BlockPos> activatingBlocks, CallbackInfo ci, int i, int j, int k, int l, int m, Box box, List<PlayerEntity> list) {
        if (Config.hasConduitCondition)
        for (PlayerEntity player : list)
            if (player instanceof ServerPlayerEntity)
                ((SPEA) player).bf$conduitPing(pos, j);
    }
}

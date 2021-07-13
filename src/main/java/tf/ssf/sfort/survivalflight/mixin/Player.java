package tf.ssf.sfort.survivalflight.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tf.ssf.sfort.survivalflight.Config;
import tf.ssf.sfort.survivalflight.SPEA;

@Mixin(ServerPlayerEntity.class)
public abstract class Player extends PlayerEntity implements SPEA {
    @Shadow
    @Final
    public ServerPlayerInteractionManager interactionManager;

    public Player(World world, BlockPos blockPos, float f, GameProfile gameProfile) {
        super(world, blockPos, f, gameProfile);
    }

    protected int bf$ticksLeft = 0;
    protected float bf$ticksXp = 0;
    protected Box bf$ping;
    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo info) {
        Config.tick.accept((ServerPlayerEntity)(Object)this);
    }

    @Shadow
    public void sendAbilitiesUpdate() {
    }

    @Override
    public void bf$beaconPing(Box box, int duration) {
        if (bf$ping == null || box.getCenter().distanceTo(this.getPos()) < bf$ping.getCenter().distanceTo(this.getPos()))
            bf$ping = box;
        bf$ticksLeft = duration;
    }

    @Override
    public boolean bf$isSurvivalLike() {
        return this.interactionManager.getGameMode().isSurvivalLike();
    }
    @Override
    public boolean bf$hasBeacon(){
        return bf$ticksLeft > 0 && bf$ping != null && bf$ping.contains(getPos());
    }
    @Override
    public void bf$fly() {
        PlayerAbilities abilities = getAbilities();
        abilities.allowFlying = true;
        sendAbilitiesUpdate();
    }
    @Override
    public void bf$fall() {
        PlayerAbilities abilities = getAbilities();
        if (abilities.flying)
            Config.exit.accept((ServerPlayerEntity) (Object)this);
        abilities.allowFlying = false;
        abilities.flying = false;
        sendAbilitiesUpdate();
    }
    @Override
    public void bf$tickXP(){
        if ((totalExperience > 0 || experienceLevel > 0) && !getAbilities().allowFlying)
            bf$fly();
        if (getAbilities().flying) {
            if (Config.ticksPerXP != 0) {
                bf$ticksXp++;
                if (bf$ticksXp >= Config.ticksPerXP) {
                    addExperience(-1);
                    bf$ticksXp = 0;
                }
            }
            if (Config.xpPerTick != 0)
                addExperience(-Config.xpPerTick);
            if (totalExperience == 0 && experienceLevel == 0)
                bf$fall();
        }
    }
    @Override
    public void bf$tickBeacon(){
        if (bf$ticksLeft>0)bf$ticksLeft--;
    }
    @Override
    public void bf$checkBeacon(){
        if (bf$ticksLeft == 0 && getAbilities().allowFlying)
            bf$fall();
    }
}

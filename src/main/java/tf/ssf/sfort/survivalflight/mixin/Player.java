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

//TODO use config plugin to reduce unused code
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
    protected int bf$pingLevel = 0;
    protected Box bf$ping;

    @Override
    public void bf$beaconPing(Box box, int duration, int level) {
        if (bf$ping == null || box.getCenter().distanceTo(this.getPos()) < bf$ping.getCenter().distanceTo(this.getPos())) {
            bf$ping = box;
            bf$pingLevel = level;
        }
        bf$ticksLeft = duration;
    }

    @Override
    public boolean bf$isSurvivalLike() {
        return this.interactionManager.getGameMode().isSurvivalLike();
    }
    @Override
    public int bf$highestLevel(){
        return bf$pingLevel;
    }

    public void bf$fly() {
        PlayerAbilities abilities = getAbilities();
        abilities.allowFlying = true;
        sendAbilitiesUpdate();
    }

    public void bf$fall() {
        PlayerAbilities abilities = getAbilities();
        if (abilities.flying)
            Config.exit.accept((ServerPlayerEntity) (Object)this);
        abilities.allowFlying = false;
        abilities.flying = false;
        sendAbilitiesUpdate();
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo info) {
        if (Config.hasBeaconCondition) {
            if (bf$ticksLeft > 0)
                if ((bf$ping != null && bf$ping.contains(getPos())))
                    if (Config.canFly.test((ServerPlayerEntity) (Object) this)) {
                        if (Config.hasExperianceCondition) {
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
                        } else if (!getAbilities().allowFlying)
                            bf$fly();
                        bf$ticksLeft--;
                        if (bf$ticksLeft == 0 && getAbilities().allowFlying)
                            bf$fall();
                    } else if (bf$isSurvivalLike() && getAbilities().allowFlying) {
                        bf$ticksLeft = 0;
                        bf$fall();
                    }
        }else{
            if (Config.canFly.test((ServerPlayerEntity) (Object) this)) {
                if (Config.hasExperianceCondition) {
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
                } else if (!getAbilities().allowFlying)
                    bf$fly();
            } else if (bf$isSurvivalLike() && getAbilities().allowFlying) {
                bf$fall();
            }
        }
    }

    @Shadow
    public void sendAbilitiesUpdate() {
    }
}

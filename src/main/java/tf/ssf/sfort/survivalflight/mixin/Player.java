package tf.ssf.sfort.survivalflight.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
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

@Mixin(value = ServerPlayerEntity.class, priority = 1999)
public abstract class Player extends PlayerEntity implements SPEA {
	@Shadow
	@Final
	public ServerPlayerInteractionManager interactionManager;

	public Player(World world, BlockPos blockPos, float f, GameProfile gameProfile) {
		super(world, blockPos, f, gameProfile);
	}

	protected int bf$ticksLeft = 0;
	protected int bf$ticksXp = 0;
	protected int bf$timed = 0;
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
		if (bf$ping == null || !bf$ping.contains(this.getPos()) || box.getCenter().distanceTo(this.getPos()) < bf$ping.getCenter().distanceTo(this.getPos()))
			bf$ping = box;
		bf$ticksLeft = duration;
	}

	@Override
	public boolean bf$isSurvivalLike() {
		return this.interactionManager.getGameMode().isSurvivalLike();
	}
	@Override
	public boolean bf$hasBeaconTicks(){
		return bf$ticksLeft>0;
	}
	@Override
	public boolean bf$hasBeaconPing(){
		return bf$ping != null && bf$ping.contains(this.getPos());
	}
	@Override
	public void bf$fly() {
		getAbilities().allowFlying = true;
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
		if (Config.ticksPerXP != 0) {
				bf$ticksXp++;
				if (bf$ticksXp >= Config.ticksPerXP) {
					addExperience(-1);
					bf$ticksXp = 0;
				}
			}
		if (Config.xpPerTick != 0)
			addExperience(-Config.xpPerTick);
	}
	@Override
	public void bf$tickBeacon(){
		if (bf$ticksLeft>0)bf$ticksLeft--;
	}
	@Override
	public boolean bf$tickTimed(){
		if (bf$timed >= 0){
			if(bf$timed > Config.duration){
				bf$timed = Config.cooldown;
				return false;
			}
			if(getAbilities().flying) bf$timed++;
			else if (bf$timed != 0) bf$timed--;
			return true;
		}
		bf$timed++;
		return false;
	}

	@Inject(method = "readCustomDataFromNbt(Lnet/minecraft/nbt/NbtCompound;)V", at = @At("HEAD"))
	public void load(NbtCompound nbt, CallbackInfo ci){
		NbtCompound tag = nbt.getCompound("SurvivalFlight");
		if (tag!=null){
			bf$ticksLeft = tag.getInt("ticksLeft");
			bf$ticksXp = tag.getInt("ticksXp");
			bf$timed = tag.getInt("timed");
			if(tag.contains("ping$sx") && tag.contains("ping$bx")
			&& tag.contains("ping$sy") && tag.contains("ping$by")
			&& tag.contains("ping$sz") && tag.contains("ping$bz")){
				bf$ping = new Box(
						tag.getDouble("ping$sx"), tag.getDouble("ping$sy"), tag.getDouble("ping$sz"),
						tag.getDouble("ping$bx"), tag.getDouble("ping$by"), tag.getDouble("ping#bz")
				);
			}
		}
	}
	@Inject(method = "writeCustomDataToNbt(Lnet/minecraft/nbt/NbtCompound;)V", at = @At("TAIL"))
	public void save(NbtCompound nbt, CallbackInfo ci){
		NbtCompound tag = new NbtCompound();
		tag.putInt("ticksLeft", bf$ticksLeft);
		tag.putInt("ticksXp", bf$ticksXp);
		tag.putInt("timed", bf$timed);
		if (bf$ping!=null) {
			tag.putDouble("ping$sx", bf$ping.minX);
			tag.putDouble("ping$bx", bf$ping.maxX);
			tag.putDouble("ping$sy", bf$ping.minY);
			tag.putDouble("ping$by", bf$ping.maxY);
			tag.putDouble("ping$sz", bf$ping.minZ);
			tag.putDouble("ping$bz", bf$ping.maxZ);
		}
		nbt.put("SurvivalFlight", tag);
	}
}

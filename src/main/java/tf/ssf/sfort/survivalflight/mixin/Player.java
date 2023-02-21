package tf.ssf.sfort.survivalflight.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tf.ssf.sfort.survivalflight.BeaconPingDOSLList;
import tf.ssf.sfort.survivalflight.Config;
import tf.ssf.sfort.survivalflight.SPEA;

@Mixin(value = ServerPlayerEntity.class, priority = 1999)
public abstract class Player extends PlayerEntity implements SPEA {
	@Shadow
	@Final
	public ServerPlayerInteractionManager interactionManager;
	protected int bf$ticksXp = 0;
	protected int bf$timed = 0;
	protected BeaconPingDOSLList<Box> bf$ping = new BeaconPingDOSLList<>();
	protected BlockPos bf$cping;
	protected int bf$cdist = 0;
	protected int bf$cticksLeft = 0;

	public Player(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
		super(world, pos, yaw, gameProfile);
	}

	@Inject(method = "tick", at = @At("HEAD"))
	private void onTick(CallbackInfo info) {
		Config.tick.accept((ServerPlayerEntity)(Object)this);
	}

	@Shadow
	public void sendAbilitiesUpdate() {
	}

	@Override
	public void bf$beaconPing(Box box, int duration) {
		bf$ping.add(box, duration);
	}

	@Override
	public void bf$conduitPing(BlockPos box, int dist) {
		if (bf$cping == null || !bf$cping.isWithinDistance(this.getBlockPos(), dist) || dist-box.getSquaredDistance(this.getBlockPos()) > bf$cdist-bf$cping.getSquaredDistance(this.getBlockPos())) {
			bf$cdist = dist;
			bf$cping = box;
		}
		bf$cticksLeft = 260;
	}

	@Override
	public boolean bf$isSurvivalLike() {
		return this.interactionManager.getGameMode().isSurvivalLike();
	}
	@Override
	public boolean bf$hasBeaconTicks(){
		return bf$ping.first != null;
	}
	@Override
	public boolean bf$hasBeaconPing(){
		Vec3d pos = this.getPos();
		return bf$ping.anyMatch(b->b.contains(pos));
	}
	@Override
	public boolean bf$hasConduitTicks(){
		return bf$cticksLeft>0;
	}
	@Override
	public boolean bf$hasConduitPing(){
		return bf$cping != null && bf$cping.isWithinDistance(this.getPos(), bf$cdist);
	}
	@Override
	public void bf$fly() {
		getAbilities().allowFlying = true;
		sendAbilitiesUpdate();
	}
	@Override
	public void bf$fall() {
		PlayerAbilities abilities = getAbilities();
		if (abilities.flying) bf$exit();
		abilities.allowFlying = false;
		abilities.flying = false;
		sendAbilitiesUpdate();
	}
	@Override
	public void bf$exit() {
		Config.exit.accept((ServerPlayerEntity) (Object)this);
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
		bf$ping.tick();
	}
	@Override
	public void bf$tickConduit(){
		if (bf$cticksLeft>0)bf$cticksLeft--;
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
			bf$ticksXp = tag.getInt("ticksXp");
			bf$timed = tag.getInt("timed");
			bf$cdist = tag.getInt("cdist");
			bf$cticksLeft = tag.getInt("cticksleft");
			if (tag.contains("cping$x") && tag.contains("cping$y") && tag.contains("cping$z")) {
				bf$cping = new BlockPos(tag.getInt("cping$x"), tag.getInt("cping$y"), tag.getInt("cping$z"));
			} else {
				bf$cping = null;
			}
			if (tag.contains("pings")) {
				bf$ping.first = null;
				NbtCompound pin = tag.getCompound("pings");
				int i = 0;
				NbtCompound bb = pin.getCompound(Integer.toString(i++));
				BeaconPingDOSLList.Node<Box> next = null;
				while (bb.contains("sx") && bb.contains("bx")
						&& bb.contains("sy") && bb.contains("by")
						&& bb.contains("sz") && bb.contains("bz")) {
					BeaconPingDOSLList.Node<Box> a = new BeaconPingDOSLList.Node<>(new Box(
							tag.getDouble("sx"), tag.getDouble("sy"), tag.getDouble("sz"),
							tag.getDouble("bx"), tag.getDouble("by"), tag.getDouble("bz")
					), tag.getInt("delay"), null);
					if (next == null) next = bf$ping.first = a;
					else next = (next.next = a);
					bb = pin.getCompound(Integer.toString(i++));
				}

			} else if(tag.contains("ping$sx") && tag.contains("ping$bx")
			&& tag.contains("ping$sy") && tag.contains("ping$by")
			&& tag.contains("ping$sz") && tag.contains("ping$bz")){
				bf$ping.first = new BeaconPingDOSLList.Node<>(new Box(
						tag.getDouble("ping$sx"), tag.getDouble("ping$sy"), tag.getDouble("ping$sz"),
						tag.getDouble("ping$bx"), tag.getDouble("ping$by"), tag.getDouble("ping$bz")
				), tag.getInt("ticksLeft"), null);
			}
		}
	}
	@Inject(method = "writeCustomDataToNbt(Lnet/minecraft/nbt/NbtCompound;)V", at = @At("TAIL"))
	public void save(NbtCompound nbt, CallbackInfo ci){
		NbtCompound tag = new NbtCompound();
		tag.putInt("ticksXp", bf$ticksXp);
		tag.putInt("timed", bf$timed);
		tag.putInt("cdist", bf$cdist);
		tag.putInt("cticksleft", bf$cticksLeft);
		if (bf$cping != null) {
			tag.putInt("cping$x", bf$cping.getX());
			tag.putInt("cping$y", bf$cping.getY());
			tag.putInt("cping$z", bf$cping.getZ());
		}
		if (bf$ping.first != null) {
			NbtCompound pin = new NbtCompound();
			int i = 0;
			for (BeaconPingDOSLList.Node<Box> n = bf$ping.first; n!=null; n = n.next) {
				NbtCompound bb = new NbtCompound();
				Box b = n.obj;
				bb.putDouble("sx", b.minX);
				bb.putDouble("bx", b.maxX);
				bb.putDouble("sy", b.minY);
				bb.putDouble("by", b.maxY);
				bb.putDouble("sz", b.minZ);
				bb.putDouble("bz", b.maxZ);
				bb.putInt("delay", n.i);
				pin.put(Integer.toString(i++), bb);
			}
			tag.put("pings", pin);
		}
		nbt.put("SurvivalFlight", tag);
	}
}

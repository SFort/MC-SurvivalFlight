package tf.ssf.sfort.survivalflight.script;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import tf.ssf.sfort.survivalflight.script.instance.*;

public class Default {
    public static final EntityScript<Entity> ENTITY = new EntityScript<>();
    public static final LivingEntityScript<LivingEntity> LIVING_ENTITY = new LivingEntityScript<>();
    public static final PlayerEntityScript<PlayerEntity> PLAYER_ENTITY = new PlayerEntityScript<>();
    public static final ServerPlayerEntityScript<ServerPlayerEntity> SERVER_PLAYER_ENTITY = new ServerPlayerEntityScript<>();
    public static final DimensionTypeScript DIMENSION_TYPE = new DimensionTypeScript();
    public static final WorldScript WORLD = new WorldScript();
    public static final BiomeScript BIOME = new BiomeScript();
}

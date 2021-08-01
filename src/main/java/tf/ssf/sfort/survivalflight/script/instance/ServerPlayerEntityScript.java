package tf.ssf.sfort.survivalflight.script.instance;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import tf.ssf.sfort.survivalflight.script.Default;
import tf.ssf.sfort.survivalflight.script.Help;
import tf.ssf.sfort.survivalflight.script.PredicateProvider;
import tf.ssf.sfort.survivalflight.script.Type;

import java.util.Set;
import java.util.function.Predicate;

public class ServerPlayerEntityScript implements PredicateProvider<ServerPlayerEntity>, Type, Help {
    public Predicate<ServerPlayerEntity> getLP(String in){
        return null;
    }
    public Predicate<ServerPlayerEntity> getLP(String in, String val){
        return switch (in){
            case "respawn_distance" ->{
                double arg = Double.parseDouble(val);
                yield player -> {
                    BlockPos pos = player.getSpawnPointPosition();
                    ServerWorld world = player.getServerWorld();
                    RegistryKey<World> dim = player.getSpawnPointDimension();
                    if (pos == null || world == null) return false;
                    return dim.equals(world.getRegistryKey()) && pos.isWithinDistance(player.getPos(), arg);
                };
            }
            case "advancement" -> {
                Identifier arg = new Identifier(val);
                yield player -> {
                    MinecraftServer server = player.getServer();
                    if (server == null) return false;
                    return player.getAdvancementTracker().getProgress(server.getAdvancementLoader().get(arg)).isDone();
                };
            }
            default -> null;
        };
    }
    @Override
    public Predicate<ServerPlayerEntity> getPredicate(String in, String val, Set<String> dejavu){
        {
            Predicate<ServerPlayerEntity> out = getLP(in, val);
            if (out != null) return out;
        }
        if (dejavu.add(Default.PLAYER_ENTITY.getType())){
            Predicate<PlayerEntity> out = Default.PLAYER_ENTITY.getPredicate(in, val, dejavu);
            if (out !=null) return out::test;
        }
        return null;
    }


    @Override
    public Predicate<ServerPlayerEntity> getPredicate(String in, Set<String> dejavu){
        {
            Predicate<ServerPlayerEntity> out = getLP(in);
            if (out != null) return out;
        }
        if (dejavu.add(Default.PLAYER_ENTITY.getType())){
            Predicate<PlayerEntity> out = Default.PLAYER_ENTITY.getPredicate(in, dejavu);
            if (out !=null) return out::test;
        }
        return null;
    }
    @Override
    public String getHelp(){
        return
                String.format("\t%-20s%-40s%s%n","advancement","- Require advancement unlocked","AdvancementID")+
                String.format("\t%-20s%-40s%s%n","respawn_distance","- Require player to be nearby their respawn (usually a bed)","double")
        ;
    }
    @Override
    public String getAllHelp(Set<String> dejavu){
        return dejavu.add(Default.PLAYER_ENTITY.getType())?Default.PLAYER_ENTITY.getAllHelp(dejavu):""+getHelp();
    }
}

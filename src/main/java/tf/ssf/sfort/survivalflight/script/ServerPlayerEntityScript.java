package tf.ssf.sfort.survivalflight.script;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

import java.util.function.Predicate;

public class ServerPlayerEntityScript implements ScriptParser<ServerPlayerEntity>{
    public static final ServerPlayerEntityScript INSTANCE = new ServerPlayerEntityScript();
    public static Predicate<ServerPlayerEntity> getP(String in, String val){
        return INSTANCE.getPredicate(in, val);
    }
    public static Predicate<ServerPlayerEntity> getP(String in){
        return INSTANCE.getPredicate(in);
    }
    public static String getH(){
        return INSTANCE.getHelp();
    }
    @Override
    public Predicate<ServerPlayerEntity> getPredicate(String in, String val){
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
            default -> player -> PlayerEntityScript.getP(in, val).test(player);
        };
    }
    @Override
    public Predicate<ServerPlayerEntity> getPredicate(String in){
        return player -> PlayerEntityScript.getP(in).test(player);
    }
    public String getHelp(){
        return
                PlayerEntityScript.getH()+
                String.format("\t%-20s%-40s%s%n","advancement","- Require advancement unlocked","AdvancementID")+
                String.format("\t%-20s%-40s%s%n","respawn_distance","- Require player to be nearby their respawn (usually a bed)","double")
        ;
    }
}

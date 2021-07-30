package tf.ssf.sfort.survivalflight.script;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.function.Predicate;

public class ServerPlayerEntityScript<T extends ServerPlayerEntity> implements ScriptParser<T>{
    private final PlayerEntityScript<T> playerEntityScript = new PlayerEntityScript<>();
    @Override
    public Predicate<T> getPredicate(String in, String val){
        return switch (in){
            case "advancement" -> {
                Identifier arg = new Identifier(val);
                yield (player) -> {
                    MinecraftServer server = player.getServer();
                    if (server == null) return false;
                    return player.getAdvancementTracker().getProgress(server.getAdvancementLoader().get(arg)).isDone();
                };
            }
            default -> playerEntityScript.getPredicate(in, val);
        };
    }
    @Override
    public Predicate<T> getPredicate(String in){
        return playerEntityScript.getPredicate(in);
    }
    @Override
    public String getHelp(){
        return
                playerEntityScript.getHelp()+
                String.format("\t%-20s%-40s%s%n","advancement","- Require advancement unlocked","AdvancementID")
                ;
    }
    private static Item getItem(String id){
        return Registry.ITEM.get(new Identifier(id));
    }
    private static boolean eq(Item required, ItemStack current){
        return required != null && required == current.getItem();
    }
}

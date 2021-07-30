package tf.ssf.sfort.survivalflight.script;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.function.Predicate;

public class PlayerEntityScript<T extends PlayerEntity> implements ScriptParser<T>{
    private final LivingEntityScript<T> livingEntityScript = new LivingEntityScript<>();
    @Override
    public Predicate<T> getPredicate(String in, String val){
        return switch (in){
            case "level" -> {
                int arg = Integer.parseInt(val);
                yield (player) -> player.experienceLevel>=arg;
            }
            case "food" -> {
                float arg = Float.parseFloat(val);
                yield (player) -> player.getHungerManager().getFoodLevel()>=arg;
            }
            default -> livingEntityScript.getPredicate(in, val);
        };
    }
    @Override
    public Predicate<T> getPredicate(String in){
        return livingEntityScript.getPredicate(in);
    }
    @Override
    public String getHelp(){
        return
                livingEntityScript.getHelp()+
                String.format("\t%-20s%-40s%s%n","level","- Minimum required player level","int")+
                String.format("\t%-20s%-40s%s%n","food","- Minimum required food","float")
                ;
    }
}

package tf.ssf.sfort.survivalflight.script.instance;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import tf.ssf.sfort.survivalflight.script.Default;
import tf.ssf.sfort.survivalflight.script.Help;
import tf.ssf.sfort.survivalflight.script.PredicateProvider;
import tf.ssf.sfort.survivalflight.script.Type;

import java.util.Set;
import java.util.function.Predicate;

public class PlayerEntityScript implements PredicateProvider<PlayerEntity>, Type, Help {
    public Predicate<PlayerEntity> getLP(String in, String val){
        return switch (in){
            case "level" -> {
                int arg = Integer.parseInt(val);
                yield player -> player.experienceLevel>=arg;
            }
            case "food" -> {
                float arg = Float.parseFloat(val);
                yield player -> player.getHungerManager().getFoodLevel()>=arg;
            }
            default -> null;
        };
    }
    public Predicate<PlayerEntity> getLP(String in){
        return null;
    }
    @Override
    public Predicate<PlayerEntity> getPredicate(String in, String val, Set<String> dejavu){
        {
            Predicate<PlayerEntity> out = getLP(in, val);
            if (out != null) return out;
        }
        if (dejavu.add(Default.LIVING_ENTITY.getType())){
            Predicate<LivingEntity> out = Default.LIVING_ENTITY.getPredicate(in, val, dejavu);
            if (out !=null) return out::test;
        }
        return null;
    }
    @Override
    public Predicate<PlayerEntity> getPredicate(String in, Set<String> dejavu){
        {
            Predicate<PlayerEntity> out = getLP(in);
            if (out != null) return out;
        }
        if (dejavu.add(Default.LIVING_ENTITY.getType())){
            Predicate<LivingEntity> out = Default.LIVING_ENTITY.getPredicate(in, dejavu);
            if (out !=null) return out::test;
        }
        return null;
    }
    @Override
    public String getHelp(){
        return
                String.format("\t%-20s%-40s%s%n","level","- Minimum required player level","int")+
                String.format("\t%-20s%-40s%s%n","food","- Minimum required food","float")
                ;
    }
    @Override
    public String getAllHelp(Set<String> dejavu){
        return dejavu.add(Default.LIVING_ENTITY.getType())?Default.LIVING_ENTITY.getAllHelp(dejavu):""+getHelp();
    }
}

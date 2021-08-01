package tf.ssf.sfort.survivalflight.script.instance;

import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import tf.ssf.sfort.survivalflight.script.Default;
import tf.ssf.sfort.survivalflight.script.Help;
import tf.ssf.sfort.survivalflight.script.PredicateProvider;
import tf.ssf.sfort.survivalflight.script.Type;

import java.util.Set;
import java.util.function.Predicate;

public class WorldScript implements PredicateProvider<World>, Type, Help {
    public Predicate<World> getLP(String in){
        return switch (in){
            case "is_day" -> World::isDay;
            case "is_raining" -> World::isRaining;
            case "is_thundering" -> World::isThundering;
            default -> null;
        };
    }
    public Predicate<World> getLP(String in, String val){
        return null;
    }
    @Override
    public Predicate<World> getPredicate(String in, String val, Set<String> dejavu){
        {
            Predicate<World> out = getLP(in, val);
            if (out != null) return out;
        }
        if (dejavu.add(Default.DIMENSION_TYPE.getType())){
            Predicate<DimensionType> out = Default.DIMENSION_TYPE.getPredicate(in, val, dejavu);
            if (out !=null) return world -> out.test(world.getDimension());
        }
        return null;
    }
    @Override
    public Predicate<World> getPredicate(String in, Set<String> dejavu){
        {
            Predicate<World> out = getLP(in);
            if (out != null) return out;
        }
        if (dejavu.add(Default.DIMENSION_TYPE.getType())){
            Predicate<DimensionType> out = Default.DIMENSION_TYPE.getPredicate(in, dejavu);
            if (out !=null) return world -> out.test(world.getDimension());
        }
        return null;
    }
    @Override
    public String getHelp(){
        return
                String.format("\t%-20s%s%n","is_thundering","- Require thunder")+
                String.format("\t%-20s%s%n","is_raining","- Require rain")+
                String.format("\t%-20s%s%n","is_day","- Require daytime")
        ;
    }
    @Override
    public String getAllHelp(Set<String> dejavu){
        return dejavu.add(Default.DIMENSION_TYPE.getType())?Default.DIMENSION_TYPE.getAllHelp(dejavu):""+getHelp();
    }
}

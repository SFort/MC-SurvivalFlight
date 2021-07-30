package tf.ssf.sfort.survivalflight.script;

import net.minecraft.world.World;

import java.util.function.Predicate;

public class WorldScript implements ScriptParser<World>{
    public static final WorldScript INSTANCE = new WorldScript();
    public static Predicate<World> getP(String in, String val){
        return INSTANCE.getPredicate(in, val);
    }
    public static Predicate<World> getP(String in){
        return INSTANCE.getPredicate(in);
    }
    public static String getH(){
        return INSTANCE.getHelp();
    }
    @Override
    public Predicate<World> getPredicate(String in, String val){
        return world -> DimensionTypeScript.getP(in, val).test(world.getDimension());
    }
    @Override
    public Predicate<World> getPredicate(String in){
        return switch (in){
            case "is_day" -> World::isDay;
            case "is_raining" -> World::isRaining;
            case "is_thundering" -> World::isThundering;
            case "" -> world -> world.getDimension().isNatural();
            default -> world -> DimensionTypeScript.getP(in).test(world.getDimension());
        };
    }
    public String getHelp(){
        return
                DimensionTypeScript.getH()+
                String.format("\t%-20s%s%n","is_thundering","- Require thunder")+
                String.format("\t%-20s%s%n","is_raining","- Require rain")+
                String.format("\t%-20s%s%n","is_day","- Require daytime")
        ;
    }
}

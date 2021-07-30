package tf.ssf.sfort.survivalflight.script;

import net.minecraft.entity.player.PlayerEntity;

import java.util.function.Predicate;

public class PlayerEntityScript implements ScriptParser<PlayerEntity>{
    public static final PlayerEntityScript INSTANCE = new PlayerEntityScript();
    public static Predicate<PlayerEntity> getP(String in, String val){
        return INSTANCE.getPredicate(in, val);
    }
    public static Predicate<PlayerEntity> getP(String in){
        return INSTANCE.getPredicate(in);
    }
    public static String getH(){
        return INSTANCE.getHelp();
    }
    @Override
    public Predicate<PlayerEntity> getPredicate(String in, String val){
        return switch (in){
            case "level" -> {
                int arg = Integer.parseInt(val);
                yield player -> player.experienceLevel>=arg;
            }
            case "food" -> {
                float arg = Float.parseFloat(val);
                yield player -> player.getHungerManager().getFoodLevel()>=arg;
            }
            default -> player -> LivingEntityScript.getP(in, val).test(player);
        };
    }
    @Override
    public Predicate<PlayerEntity> getPredicate(String in){
        return player -> LivingEntityScript.getP(in).test(player);
    }
    public String getHelp(){
        return
                LivingEntityScript.getH()+
                String.format("\t%-20s%-40s%s%n","level","- Minimum required player level","int")+
                String.format("\t%-20s%-40s%s%n","food","- Minimum required food","float")
                ;
    }
}

package tf.ssf.sfort.survivalflight.script;

import net.minecraft.entity.Entity;

import java.util.function.Predicate;

public class EntityScript<T extends Entity> implements ScriptParser<T>{
    @Override
    public Predicate<T> getPredicate(String in, String val){
        return switch (in){
            case "height" -> {
                float arg = Float.parseFloat(val);
                yield (player) -> player.getPos().y>=arg;
            }
            default -> null;
        };
    }
    @Override
    public Predicate<T> getPredicate(String in){
        return switch (in) {
            case "sprinting" -> Entity::isSprinting;
            case "in_lava" -> Entity::isInLava;
            case "on_fire" -> Entity::isOnFire;
            case "wet" -> Entity::isWet;
            default -> null;
        };
    }
    @Override
    public String getHelp(){
        return
                String.format("\t%-20s%-40s%s%n","height","- Minimum required player y height","float")+
                String.format("\t%-20s%s%n","sprinting","- Require Sprinting")+
                String.format("\t%-20s%s%n","in_lava","- Require being in lava")+
                String.format("\t%-20s%s%n","on_fire","- Require being on fire")+
                String.format("\t%-20s%s%n","wet","- Require being wet")
                ;
    }
}

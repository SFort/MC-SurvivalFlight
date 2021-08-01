package tf.ssf.sfort.survivalflight.script.instance;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import tf.ssf.sfort.survivalflight.script.Default;
import tf.ssf.sfort.survivalflight.script.Help;
import tf.ssf.sfort.survivalflight.script.PredicateProvider;
import tf.ssf.sfort.survivalflight.script.Type;

import java.util.Set;
import java.util.function.Predicate;

public class EntityScript implements PredicateProvider<Entity>, Type, Help {
	public Predicate<Entity> getLP(String in, String val){
		return switch (in){
			case "height" -> {
				float arg = Float.parseFloat(val);
				yield (entity) -> entity.getPos().y>=arg;
			}
			case "age" -> {
				int arg = Integer.parseInt(val);
				yield (entity) -> entity.age>arg;
			}
			default -> null;
		};
	}
	public Predicate<Entity> getLP(String in){
		return switch (in) {
			case "sprinting" -> Entity::isSprinting;
			case "in_lava" -> Entity::isInLava;
			case "on_fire" -> Entity::isOnFire;
			case "wet" -> Entity::isWet;
			case "fire_immune" -> Entity::isFireImmune;
			case "freezing" -> Entity::isFreezing;
			case "glowing" -> Entity::isGlowing;
			case "explosion_immune" -> Entity::isImmuneToExplosion;
			case "invisible" -> Entity::isInvisible;
			default -> null;
		};
	}
	@Override
	public Predicate<Entity> getPredicate(String in, String val, Set<String> dejavu) {
		{
			Predicate<Entity> out = getLP(in, val);
			if (out != null) return out;
		}
		if (dejavu.add(Default.WORLD.getType())){
			Predicate<World> out = Default.WORLD.getPredicate(in, val, dejavu);
			if (out !=null) return entity -> out.test(entity.world);
		}
		return null;
	}
	@Override
	public Predicate<Entity> getPredicate(String in, Set<String> dejavu){
		{
			Predicate<Entity> out = getLP(in);
			if (out != null) return out;
		}
		if (dejavu.add(Default.WORLD.getType())){
			Predicate<World> out = Default.WORLD.getPredicate(in, dejavu);
			if (out !=null) return entity -> out.test(entity.world);
		}
		return null;
	}
	@Override
	public String getHelp(){
		return
			String.format("\t%-20s%-70s%s%n","age","- Minimum ticks the player must have existed","int")+
			String.format("\t%-20s%-70s%s%n","height","- Minimum required player y height","float")+
			String.format("\t%-20s%s%n","sprinting","- Require Sprinting")+
			String.format("\t%-20s%s%n","in_lava","- Require being in lava")+
			String.format("\t%-20s%s%n","on_fire","- Require being on fire")+
			String.format("\t%-20s%s%n","wet","- Require being wet")+
			String.format("\t%-20s%s%n","fire_immune","- Require being immune to fire")+
            String.format("\t%-20s%s%n","freezing","- Require to be freezing")+
            String.format("\t%-20s%s%n","glowing","- Require to be glowing")+
            String.format("\t%-20s%s%n","explosion_immune","- Require being immune to explosions")+
            String.format("\t%-20s%s%n","invisible","- Require being invisible")
		;
	}
	@Override
	public String getAllHelp(Set<String> dejavu){
		return (dejavu.add(Default.WORLD.getType())?Default.WORLD.getAllHelp(dejavu):"")+getHelp();
	}
}

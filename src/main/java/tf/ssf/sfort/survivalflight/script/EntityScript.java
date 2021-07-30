package tf.ssf.sfort.survivalflight.script;

import net.minecraft.entity.Entity;

import java.util.function.Predicate;

public class EntityScript implements ScriptParser<Entity>{
	public static final EntityScript INSTANCE = new EntityScript();
	public static Predicate<Entity> getP(String in, String val){
		return INSTANCE.getPredicate(in, val);
	}
	public static Predicate<Entity> getP(String in){
		return INSTANCE.getPredicate(in);
	}
	public static String getH(){
		return INSTANCE.getHelp();
	}
	@Override
	public Predicate<Entity> getPredicate(String in, String val){
		return switch (in){
			case "height" -> {
				float arg = Float.parseFloat(val);
				yield (entity) -> entity.getPos().y>=arg;
			}
			case "age" -> {
				int arg = Integer.parseInt(val);
				yield (entity) -> entity.age>arg;
			}
			default -> entity -> WorldScript.getP(in, val).test(entity.world);
		};
	}
	@Override
	public Predicate<Entity> getPredicate(String in){
		return switch (in) {
			case "sprinting" -> Entity::isSprinting;
			case "in_lava" -> Entity::isInLava;
			case "on_fire" -> Entity::isOnFire;
			case "wet" -> Entity::isWet;
			//NEW
			case "fire_immune" -> Entity::isFireImmune;
			case "freezing" -> Entity::isFreezing;
			case "glowing" -> Entity::isGlowing;
			case "explosion_immune" -> Entity::isImmuneToExplosion;
			case "invisible" -> Entity::isInvisible;
			default -> entity -> WorldScript.getP(in).test(entity.world);
		};
	}
	public String getHelp(){
		return
			WorldScript.getH()+
			String.format("\t%-20s%-40s%s%n","age","- Minimum ticks the player must have existed for","int")+
			String.format("\t%-20s%-40s%s%n","height","- Minimum required player y height","float")+
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
}

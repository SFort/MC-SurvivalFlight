package tf.ssf.sfort.survivalflight.script;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.SimpleRegistry;

import java.util.function.Predicate;

public class LivingEntityScript<T extends LivingEntity> implements ScriptParser<T>{
    private final EntityScript<T> entityScript= new EntityScript<>();
    @Override
    public Predicate<T> getPredicate(String in, String val){
        return switch (in){
            case "hand" -> {
                Item arg = getItem(val);
                yield (entity) -> eq(arg, entity.getMainHandStack());
            }
            case "offhand" -> {
                Item arg = getItem(val);
                yield (entity) -> eq(arg, entity.getOffHandStack());
            }
            case "helm" -> {
                Item arg = getItem(val);
                yield (entity) -> eq(arg, entity.getEquippedStack(EquipmentSlot.HEAD));
            }
            case "chest" -> {
                Item arg = getItem(val);
                yield (entity) -> eq(arg, entity.getEquippedStack(EquipmentSlot.CHEST));
            }
            case "legs" -> {
                Item arg = getItem(val);
                yield (entity) -> eq(arg, entity.getEquippedStack(EquipmentSlot.LEGS));
            }
            case "boots" -> {
                Item arg = getItem(val);
                yield (entity) -> eq(arg, entity.getEquippedStack(EquipmentSlot.FEET));
            }
            case "health" -> {
                float arg = Float.parseFloat(val);
                yield (entity) -> entity.getHealth()>=arg;
            }
            case "effect" -> {
                StatusEffect arg = SimpleRegistry.STATUS_EFFECT.get(new Identifier(val));
                yield (entity) -> entity.hasStatusEffect(arg);
            }
            default -> entityScript.getPredicate(in, val);
        };
    }
    @Override
    public Predicate<T> getPredicate(String in){
        return switch (in) {
            case "full_hp" -> (entity) -> entity.getHealth() == entity.getMaxHealth();
            case "blocking" -> LivingEntity::isBlocking;
            case "using" -> LivingEntity::isUsingItem;
            default -> entityScript.getPredicate(in);
        };
    }
    @Override
    public String getHelp(){
        return
                entityScript.getHelp()+
                String.format("\t%-20s%-40s%s%n","hand","- Require item in main hand","ItemID")+
                String.format("\t%-20s%-40s%s%n","offhand","- Require item in off hand","ItemID")+
                String.format("\t%-20s%-40s%s%n","helm","- Require item as helmet","ItemID")+
                String.format("\t%-20s%-40s%s%n","chest","- Require item as chestplate","ItemID")+
                String.format("\t%-20s%-40s%s%n","legs","- Require item as leggings","ItemID")+
                String.format("\t%-20s%-40s%s%n","boots","- Require item as boots","ItemID")+
                String.format("\t%-20s%-40s%s%n","effect","- Require potion effect","EffectID")+
                String.format("\t%-20s%-40s%s%n","health","- Minimum required heath","float")+
                String.format("\t%-20s%s%n","full_hp","- Require full health")+
                String.format("\t%-20s%s%n","sprinting","- Require Sprinting")+
                String.format("\t%-20s%s%n","blocking","- Require Blocking")+
                String.format("\t%-20s%s%n","using","- Require using items")
                ;
    }
    private static Item getItem(String id){
        return Registry.ITEM.get(new Identifier(id));
    }
    private static boolean eq(Item required, ItemStack current){
        return required != null && required == current.getItem();
    }
}

package tf.ssf.sfort.survivalflight.script;

import net.minecraft.world.dimension.DimensionType;

import java.util.function.Predicate;

public class DimensionTypeScript implements ScriptParser<DimensionType>{
    public static final DimensionTypeScript INSTANCE = new DimensionTypeScript();
    public static Predicate<DimensionType> getP(String in, String val){
        return INSTANCE.getPredicate(in, val);
    }
    public static Predicate<DimensionType> getP(String in){
        return INSTANCE.getPredicate(in);
    }
    public static String getH(){
        return INSTANCE.getHelp();
    }
    @Override
    public Predicate<DimensionType> getPredicate(String in, String val){
        return null;
    }
    @Override
    public Predicate<DimensionType> getPredicate(String in){
        return switch (in){
            case "dim_natural" -> DimensionType::isNatural;
            case "dim_ultrawarn" -> DimensionType::isUltrawarm;
            case "dim_piglin_safe" -> DimensionType::isPiglinSafe;
            case "dim_does_bed_work" -> DimensionType::isBedWorking;
            case "dim_does_anchor_work" -> DimensionType::isRespawnAnchorWorking;
            default -> null;
        };
    }
    public String getHelp(){
        return
                String.format("\t%-20s%s%n","dim_natural","- Require natural dimension")+
                String.format("\t%-20s%s%n","dim_ultrawarn","- Require ultra warm dimension")+
                String.format("\t%-20s%s%n","dim_piglin_safe","- Require piglin safe dimension")+
                String.format("\t%-20s%s%n","dim_does_bed_work","- Require dimension where beds don't blow")+
                String.format("\t%-20s%s%n","dim_does_anchor_work","- Require dimension where respawn anchors work")
        ;
    }
}

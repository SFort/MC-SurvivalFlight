package tf.ssf.sfort.survivalflight;

import net.minecraft.entity.player.PlayerEntity;
import org.apache.logging.log4j.Level;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

// Was so preoccupied with whether or not i could, didn't stop to think if i should
// (just don't want PlayerAbilityLib running while in a dev env, if anyone has a better solution please tell)
public class PlayerAbilityLibCompat {
    private static Object ID, FLIGHT;
    private static Method grants, grant, revoke;
    private static Class<?> source;
    private static Class<?> ability;

    static {
        try {
            ID = Class.forName("io.github.ladysnake.pal.Pal").getMethod("getAbilitySource", String.class, String.class).invoke(null, "survivalflight", "flight");
            source = Class.forName("io.github.ladysnake.pal.AbilitySource");
            ability = Class.forName("io.github.ladysnake.pal.PlayerAbility");
            FLIGHT = Class.forName("io.github.ladysnake.pal.VanillaAbilities").getField("ALLOW_FLYING").get(ability);
            grants = source.getMethod("grants", PlayerEntity.class, ability);
            grant = source.getMethod("grantTo", PlayerEntity.class, ability);
            revoke = source.getMethod("revokeFrom", PlayerEntity.class, ability);
        } catch (Exception e) {
            Config.borkedPlayerAbilityLib();
            Config.LOGGER.log(Level.WARN, "PlayerAbilityLib borked");
        }
    }
    public static void grant(PlayerEntity player) throws InvocationTargetException, IllegalAccessException {
        grant.invoke(source.cast(ID), player, ability.cast(FLIGHT));
    }
    public static boolean grants(PlayerEntity player) throws InvocationTargetException, IllegalAccessException {
        return (boolean) grants.invoke(source.cast(ID), player, ability.cast(FLIGHT));
    }
    public static void revoke(PlayerEntity player) throws InvocationTargetException, IllegalAccessException {
        revoke.invoke(source.cast(ID), player, ability.cast(FLIGHT));
    }
    public static void init(){
        Config.tick = splayer-> {
            SPEA player = (SPEA) splayer;
            try {
                if (Config.canFly.test(splayer)) {
                    if (!grants(splayer))
                        grant(splayer);
                } else if (player.bf$isSurvivalLike() && grants(splayer)) {
                    revoke(splayer);
                }
            }catch (Exception e){
                Config.borkedPlayerAbilityLib();
                Config.LOGGER.log(Level.WARN, "PlayerAbilityLib borked");
            }
        };
    }
    public static void addXp(){
        Config.tick = splayer-> {
            SPEA player = (SPEA) splayer;
            try {
                if (Config.canFly.test(splayer)) {
                    if ((splayer.totalExperience > 0 || splayer.experienceLevel > 0) && !grants(splayer))
                        grant(splayer);
                    if (splayer.abilities.flying) {
                        player.bf$tickXP();
                        if (splayer.totalExperience == 0 && splayer.experienceLevel == 0)
                            revoke(splayer);
                    }
                } else if (player.bf$isSurvivalLike() && grants(splayer)) {
                    revoke(splayer);
                }
            }catch (Exception e){
                Config.borkedPlayerAbilityLib();
                Config.LOGGER.log(Level.WARN, "PlayerAbilityLib borked");
            }
        };
    }
}

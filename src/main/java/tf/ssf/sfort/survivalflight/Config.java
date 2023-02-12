package tf.ssf.sfort.survivalflight;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Level;
import tf.ssf.sfort.ini.SFIni;
import tf.ssf.sfort.script.Default;
import tf.ssf.sfort.script.Help;
import tf.ssf.sfort.script.ScriptParser;
import tf.ssf.sfort.survivalflight.mixin.MixinConfig;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.function.Consumer;
import java.util.function.Predicate;

//TODO use mixin plugin
public class Config implements ModInitializer {


	public static int duration = 0;
	public static int cooldown = 0;
	public static int xpPerTick = 0;
	public static int beaconLevel = 0;
	public static int ticksPerXP = 0;
	public static boolean hasBeaconCondition = false;
	public static boolean hasConduitCondition = false;
	private static boolean registerCommands = true;
	private static boolean registerPlayerAbilityLib = true;
	public static boolean keepPlayerAbilityLib = true;
	public static final Predicate<ServerPlayerEntity> canFly_init = player -> ((SPEA)player).bf$isSurvivalLike();
	public static final Consumer<ServerPlayerEntity> tick_init = splayer-> {
		SPEA player = (SPEA) splayer;
		if (Config.canFly.test(splayer)) {
			if (!splayer.getAbilities().allowFlying)
				player.bf$fly();
		} else if (player.bf$isSurvivalLike() && splayer.getAbilities().allowFlying) {
			player.bf$fall();
		}
	};
	public static Predicate<ServerPlayerEntity> canFly = canFly_init;
	public static Predicate<ServerPlayerEntity> canElytraFly = null;
	public static Predicate<ServerPlayerEntity> canElytraBoost = null;
	public static Consumer<ServerPlayerEntity> exit = player -> {};
	public static Consumer<ServerPlayerEntity> exitElytra = player -> {};
	public static Consumer<ServerPlayerEntity> tick = tick_init;

	public static File scriptHelpFile = new File(ConfigLoader.confFolder.toFile(), "script_help.txt");
	public static File elytraScriptFile = new File(ConfigLoader.confFolder.toFile(), "elytra_flight.script");
	public static File boostScriptFile = new File(ConfigLoader.confFolder.toFile(), "elytra_boost.script");
	public static void write(File file, String in){
		try {
			FileUtils.writeStringToFile(file, in, StandardCharsets.UTF_8);
			resetsettings();
			reload_settings();
		} catch (Exception e) {
			MixinConfig.LOGGER.log(Level.ERROR, MixinConfig.MOD_ID +" failed to write script file\n"+e);
		}
	}
	public static void writeFly(String in){
		write(ConfigLoader.scriptFile, in);
	}
	public static void writeElytra(String in){
		write(elytraScriptFile, in);
	}
	public static void writeElytraBoost(String in){
		write(boostScriptFile, in);
	}
	public static String read(File file){
		try {
			return Files.readString(file.toPath()).replaceAll("\\s", "");
		} catch (Exception e) {
			MixinConfig.LOGGER.log(Level.ERROR, MixinConfig.MOD_ID +" failed to read script file\n"+e);
		}
		return "";
	}
	public static String readFly(){
		return read(ConfigLoader.scriptFile);
	}
	public static String readElytra(){
		return read(elytraScriptFile);
	}
	public static String readElytraBoost(){
		return read(boostScriptFile);
	}

	public static void resetsettings(){
		hasBeaconCondition = false;
		canFly = canFly_init;
		exit = player -> {};
		exitElytra = player -> {};
		tick = tick_init;
		canElytraBoost = null;
		canElytraFly = null;
	}
	@Override
	public void onInitialize() {
		reload_settings();
		if (FabricLoader.getInstance().isModLoaded("fabric-command-api-v2") && registerCommands){
			if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT)
				CommandsClient.registerClient();
			Commands.register();
		}
		if (MixinConfig.elytraFapi){
			FAPI.register();
		}
	}
	public static void borkedPlayerAbilityLib(){
		Config.keepPlayerAbilityLib = false;
		Config.resetsettings();
		Config.reload_settings();
	}
	public static void loadIni(SFIni ini) {
		try {
			//TODO add commands to generate these
			EnumTrueLudicrousFalse e = ini.getEnum("genScriptHelp", EnumTrueLudicrousFalse.class);
			if (e != EnumTrueLudicrousFalse.FALSE) try {
				Files.writeString(scriptHelpFile.toPath(), scriptHelp);
				if (e == EnumTrueLudicrousFalse.LUDICROUS) try {
					StringBuilder out = new StringBuilder();
					for (String h : Default.PARAMETERS.map.keySet()) {
						out.append("\n\n").append(h).append("\n")
								.append("======================================================================")
								.append(":\n").append(String.join("\n", Default.PARAMETERS.getParameters(h)));
					}
					Files.writeString(scriptHelpFile.toPath(), out.toString(), StandardOpenOption.APPEND);
				} catch (Exception err){
					MixinConfig.LOGGER.log(Level.WARN, MixinConfig.MOD_ID +" failed to write ludicrous help", err);
				}
			} catch (Exception err){
				MixinConfig.LOGGER.log(Level.WARN, MixinConfig.MOD_ID +" failed to write help", err);
			}
		} catch (IllegalArgumentException e) {
			MixinConfig.LOGGER.log(Level.WARN, MixinConfig.MOD_ID +" failed to load genScriptHelp", e);
		}
		try {
			xpPerTick = ini.getInt("xpPerTick");
		} catch (IllegalArgumentException e) {
			SFIni.Data data = ini.getLastData("xpPerTick");
			if (data != null) data.val = Integer.toString(xpPerTick);
			MixinConfig.LOGGER.log(Level.WARN, MixinConfig.MOD_ID +" failed to load xpPerTick, setting to last valid value", e);
		}
		try {
			String str = ini.getLast("ticksPerXp");
			int indx = str.indexOf(".");
			if(indx != -1)
				ticksPerXP = (int) Math.round(1/Double.parseDouble("0"+str.substring(indx)));
			else
				ticksPerXP = Integer.parseInt(str);
		} catch (IllegalArgumentException e) {
			SFIni.Data data = ini.getLastData("ticksPerXp");
			if (data != null) data.val = Integer.toString(xpPerTick);
			MixinConfig.LOGGER.log(Level.WARN, MixinConfig.MOD_ID +" failed to load ticksPerXp, setting to last valid value", e);
		}
		try {
			String str = ini.getLast("flightFailEffect");
			if (!str.isBlank()) {
				int indx = str.indexOf(";");
				int duration = Integer.parseInt(str.substring(indx + 1));
				StatusEffect exit_effect = Registries.STATUS_EFFECT.get(new Identifier(str.substring(0, indx)));
				if (exit_effect == null)
					throw new IllegalArgumentException("status effect not found: " + str.substring(0, indx));
				exit = exit.andThen((player) -> player.addStatusEffect(new StatusEffectInstance(exit_effect, duration)));
			}
		} catch (IllegalArgumentException e) {
			MixinConfig.LOGGER.log(Level.WARN, MixinConfig.MOD_ID +" failed to load flightFailEffect", e);
		}
		try {
			beaconLevel = ini.getInt("minBeaconLevel");
		} catch (IllegalArgumentException e) {
			SFIni.Data data = ini.getLastData("minBeaconLevel");
			if (data != null) data.val = Integer.toString(beaconLevel);
			MixinConfig.LOGGER.log(Level.WARN, MixinConfig.MOD_ID +" failed to load minBeaconLevel, setting to last valid value", e);
		}
		try {
			registerCommands = ini.getBoolean("addCommands");
		} catch (IllegalArgumentException e) {
			SFIni.Data data = ini.getLastData("addCommands");
			if (data != null) data.val = Boolean.toString(registerCommands);
			MixinConfig.LOGGER.log(Level.WARN, MixinConfig.MOD_ID +" failed to load addCommands, setting to last valid value", e);
		}
		try {
			duration = ini.getInt("flightDurationLimit");
		} catch (IllegalArgumentException e) {
			SFIni.Data data = ini.getLastData("flightDurationLimit");
			if (data != null) data.val = Integer.toString(duration);
			MixinConfig.LOGGER.log(Level.WARN, MixinConfig.MOD_ID +" failed to load flightDurationLimit, setting to last valid value", e);
		}
		try {
			cooldown = -ini.getInt("flightCooldown");
		} catch (IllegalArgumentException e) {
			SFIni.Data data = ini.getLastData("flightCooldown");
			if (data != null) data.val = Integer.toString(-cooldown);
			MixinConfig.LOGGER.log(Level.WARN, MixinConfig.MOD_ID +" failed to load flightCooldown, setting to last valid value", e);
		}
		try {
			String str = ini.getLast("elytraFlightFailEffect");
			if (!str.isBlank()) {
				int indx = str.indexOf(";");
				int duration = Integer.parseInt(str.substring(indx + 1));
				StatusEffect exit_effect = Registries.STATUS_EFFECT.get(new Identifier(str.substring(0, indx)));
				if (exit_effect == null)
					throw new IllegalArgumentException("status effect not found: " + str.substring(0, indx));
				exitElytra = exitElytra.andThen((player) -> player.addStatusEffect(new StatusEffectInstance(exit_effect, duration)));
			}
		} catch (IllegalArgumentException e) {
			MixinConfig.LOGGER.log(Level.WARN, MixinConfig.MOD_ID +" failed to load elytraFlightFailEffect", e);
		}
		try {
			ini.getEnum("allowGlideWithoutElytra", EnumTrueFalseFapi.class);
		} catch (IllegalArgumentException e) {
			SFIni.Data data = ini.getLastData("allowGlideWithoutElytra");
			if (data != null) data.val = "false";
			MixinConfig.LOGGER.log(Level.WARN, MixinConfig.MOD_ID +" failed to load allowGlideWithoutElytra, reset to default", e);
		}
		try {
			registerPlayerAbilityLib = ini.getBoolean("playerAbilityLibCompat");
		} catch (IllegalArgumentException e) {
			SFIni.Data data = ini.getLastData("playerAbilityLibCompat");
			if (data != null) data.val = Boolean.toString(registerPlayerAbilityLib);
			MixinConfig.LOGGER.log(Level.WARN, MixinConfig.MOD_ID +" failed to load playerAbilityLibCompat, setting to last valid value", e);
		}
		MixinConfig.LOGGER.log(Level.INFO, MixinConfig.MOD_ID +" successfully loaded config file");
	}

	public static void reload_settings(){
		ConfigLoader.getGeneral(Config::loadIni);

		try {
			if(ConfigLoader.scriptFile.createNewFile()) {
				FileUtils.writeStringToFile(ConfigLoader.scriptFile, "false", StandardCharsets.UTF_8);
			}
			Predicate<ServerPlayerEntity> out = Default.SERVER_PLAYER_ENTITY.parse(Files.readString(ConfigLoader.scriptFile.toPath()).replaceAll("\\s", ""));
			if (out != null)
				canFly = canFly.and(out);
			MixinConfig.LOGGER.log(Level.INFO, MixinConfig.MOD_ID + " successfully loaded flight script file");
		} catch (Exception e) {
			MixinConfig.LOGGER.log(Level.ERROR, MixinConfig.MOD_ID +" failed to load flight script file\n"+e);
		}
		try {
			if(elytraScriptFile.createNewFile()) {
				FileUtils.writeStringToFile(elytraScriptFile, "true", StandardCharsets.UTF_8);
			}
			Predicate<ServerPlayerEntity> out = Default.SERVER_PLAYER_ENTITY.parse(Files.readString(elytraScriptFile.toPath()).replaceAll("\\s", ""));
			canElytraFly = out;
			MixinConfig.LOGGER.log(Level.INFO, MixinConfig.MOD_ID + " successfully loaded elytra script file");
		} catch (Exception e) {
			MixinConfig.LOGGER.log(Level.ERROR, MixinConfig.MOD_ID +" failed to load elytra script file\n"+e);
		}
		try {
			if(boostScriptFile.createNewFile()) {
				FileUtils.writeStringToFile(boostScriptFile, "true", StandardCharsets.UTF_8);
			}
			Predicate<ServerPlayerEntity> out = Default.SERVER_PLAYER_ENTITY.parse(Files.readString(boostScriptFile.toPath()).replaceAll("\\s", ""));
			canElytraBoost = out;
			MixinConfig.LOGGER.log(Level.INFO, MixinConfig.MOD_ID + " successfully loaded elytra boost script file");
		} catch (Exception e) {
			MixinConfig.LOGGER.log(Level.ERROR, MixinConfig.MOD_ID +" failed to load elytra boost script file\n"+e);
		}
		if(duration != 0 || cooldown != 0)
			canFly = bit_and(canFly, player -> ((SPEA)player).bf$tickTimed());


		//TODO probably merge into canFly
		if (ticksPerXP != 0 || xpPerTick != 0){
			if (FabricLoader.getInstance().isModLoaded("playerabilitylib") && registerPlayerAbilityLib && keepPlayerAbilityLib) {
				PlayerAbilityLibCompat.addXp();
			}else{
				tick = splayer -> {
					SPEA player = (SPEA) splayer;
					if (Config.canFly.test(splayer)) {
						if ((splayer.totalExperience > 0 || splayer.experienceLevel > 0) && !splayer.getAbilities().allowFlying)
							player.bf$fly();
						if (splayer.getAbilities().flying) {
							player.bf$tickXP();
							if (splayer.totalExperience == 0 && splayer.experienceLevel == 0)
								player.bf$fall();
						}
					} else if (player.bf$isSurvivalLike() && splayer.getAbilities().allowFlying) {
						player.bf$fall();
					}
				};
			}
		}else if (FabricLoader.getInstance().isModLoaded("playerabilitylib") && registerPlayerAbilityLib && keepPlayerAbilityLib) {
			PlayerAbilityLibCompat.init();
		}
		if(hasBeaconCondition) {
			tick = ((Consumer<ServerPlayerEntity>)p -> ((SPEA) p).bf$tickBeacon()).andThen(tick);
		}
		if(hasConduitCondition) {
			tick = ((Consumer<ServerPlayerEntity>)p -> ((SPEA) p).bf$tickConduit()).andThen(tick);
		}
	}

	public static final String scriptHelp = """
				Lines are ignored.
				Available operations:
				"""+ ScriptParser.getHelp()+
			"\nAvailable Conditions:\n"+
			Help.formatHelp(Default.SERVER_PLAYER_ENTITY, null);
	;

	private static Predicate<ServerPlayerEntity> bit_and(Predicate<ServerPlayerEntity> p1, Predicate<ServerPlayerEntity> p2){
		return p -> p1.test(p) & p2.test(p);
	}
}

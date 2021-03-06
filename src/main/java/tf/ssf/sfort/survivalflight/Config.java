	package tf.ssf.sfort.survivalflight;

	import net.fabricmc.api.EnvType;
	import net.fabricmc.api.ModInitializer;
	import net.fabricmc.loader.api.FabricLoader;
	import net.minecraft.entity.damage.DamageSource;
	import net.minecraft.entity.effect.StatusEffect;
	import net.minecraft.entity.effect.StatusEffectInstance;
	import net.minecraft.server.network.ServerPlayerEntity;
	import net.minecraft.util.Identifier;
	import net.minecraft.util.registry.SimpleRegistry;
	import net.minecraft.world.GameMode;
	import org.apache.commons.io.FileUtils;
	import org.apache.logging.log4j.Level;
	import org.apache.logging.log4j.LogManager;
	import org.apache.logging.log4j.Logger;
	import tf.ssf.sfort.script.Default;
	import tf.ssf.sfort.script.Help;
	import tf.ssf.sfort.script.ScriptParser;
	import tf.ssf.sfort.script.StitchedPredicateProvider;
	import tf.ssf.sfort.survivalflight.mixin.MixinConfig;

	import java.io.File;
	import java.nio.charset.StandardCharsets;
	import java.nio.file.Files;
	import java.nio.file.Path;
	import java.nio.file.StandardOpenOption;
	import java.util.*;
	import java.util.function.Consumer;
	import java.util.function.Predicate;

	//TODO use mixin plugin
	public class Config implements ModInitializer {
		public static final String MOD_ID = "tf.ssf.sfort.survivalflight";
		public static final Logger LOGGER = LogManager.getLogger();


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

		static Path old_confFolder = FabricLoader.getInstance().getConfigDir();
		static File old_confFile = new File(old_confFolder.toFile(), "SurvivalFlight.conf");
		static File old_scriptFile = new File(old_confFolder.toFile(), "SurvivalFlightScript.conf");
		static File old_scriptHelpFile = new File(old_confFolder.toFile(), "SurvivalFlightScriptHelp.conf");
		public static Path confFolder = FabricLoader.getInstance().getConfigDir().resolve("SurvivalFlight");
		public static File confFile = new File(confFolder.toFile(), "general.conf");
		public static File scriptFile = new File(confFolder.toFile(), "creative_flight.script");
		public static File scriptHelpFile = new File(confFolder.toFile(), "script_help.txt");
		public static File elytraScriptFile = new File(confFolder.toFile(), "elytra_flight.script");
		public static File boostScriptFile = new File(confFolder.toFile(), "elytra_boost.script");
		public static void write(File file, String in){
			try {
				FileUtils.writeStringToFile(file, in, StandardCharsets.UTF_8);
				resetsettings();
				reload_settings();
			} catch (Exception e) {
				LOGGER.log(Level.ERROR, MOD_ID +" failed to write script file\n"+e);
			}
		}
		public static void writeFly(String in){
			write(scriptFile, in);
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
				LOGGER.log(Level.ERROR, MOD_ID +" failed to read script file\n"+e);
			}
			return "";
		}
		public static String readFly(){
			return read(scriptFile);
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
		public static void reload_settings(){
			// Configs

			if(!confFolder.toFile().isDirectory()){
				if(confFolder.toFile().mkdirs()){
					old_scriptHelpFile.delete();
					old_confFile.renameTo(confFile);
					old_scriptFile.renameTo(scriptFile);
				}
			}

			try {
				boolean existing = !confFile.createNewFile();
				List<String> la = Files.readAllLines(confFile.toPath());
				String[] ls = la.toArray(new String[Math.max(la.size(), defaultDesc.size() * 2)|1]);
				final int hash = Arrays.hashCode(ls);
				for (int i = 0; i<defaultDesc.size();++i)
					ls[i*2+1]= defaultDesc.get(i);
				int i = 0;
				//TODO add commands to generate these
				boolean generateScriptHelp = false;
				boolean generateLudicrousHelp = false;
				try {
					generateScriptHelp =ls[i].contains("true");
					generateLudicrousHelp =ls[i].contains("ludicrous");
				}catch (Exception ignored){}
				if (generateScriptHelp || generateLudicrousHelp)
					try {
						Files.writeString(scriptHelpFile.toPath(), scriptHelp);
					}catch (Exception e){ LOGGER.log(Level.WARN, MOD_ID +" #0\n"+e); }
				if (generateLudicrousHelp)
					try {
						StringBuilder out = new StringBuilder();
						for (String h : Default.PARAMETERS.map.keySet()) {
							out.append("\n\n").append(h).append("\n")
									.append("======================================================================")
									.append(":\n").append(String.join("\n", Default.PARAMETERS.getParameters(h)));
						}
						Files.writeString(scriptHelpFile.toPath(), out.toString(), StandardOpenOption.APPEND);
					}catch (Exception e){ LOGGER.log(Level.WARN, MOD_ID +" #0\n"+e); }
				ls[i]=generateLudicrousHelp? "ludicrous" :String.valueOf(generateScriptHelp);
				i+=2;
				try{
					xpPerTick = Integer.parseInt(ls[i]);
				}catch (Exception e){ if(existing)LOGGER.log(Level.WARN, MOD_ID +" #"+i+"\n"+e); }
				ls[i] = String.valueOf(xpPerTick);
				i+=2;

				try{
					int indx = ls[i].indexOf(".");
					if(indx != -1)
						ticksPerXP = (int) Math.round(1/Double.parseDouble("0"+ls[i].substring(indx)));
					else
						ticksPerXP = Integer.parseInt(ls[i]);
				}catch (Exception e){ if(existing)LOGGER.log(Level.WARN, MOD_ID +" #"+i+"\n"+e); }
				ls[i] = String.valueOf(ticksPerXP);
				i+=2;

				try{
					int indx = ls[i].indexOf(";");
					int duration = Integer.parseInt(ls[i].substring(indx+1));
					StatusEffect exit_effect = SimpleRegistry.STATUS_EFFECT.get(new Identifier(ls[i].substring(0,indx)));
					exit = exit.andThen((player) -> player.addStatusEffect(new StatusEffectInstance(exit_effect, duration)));
				}catch (Exception e){ if(existing)LOGGER.log(Level.WARN, MOD_ID +" #"+i+"\n"+e); }
				i+=2;
				try{
					beaconLevel = Integer.parseInt(ls[i]);
				}catch (Exception e){ if(existing)LOGGER.log(Level.WARN, MOD_ID +" #"+i+"\n"+e); }
				ls[i] = String.valueOf(beaconLevel);

				i+=2;
				try{
					registerCommands = !ls[i].contains("false");
				}catch (Exception e){ if(existing)LOGGER.log(Level.WARN, MOD_ID +" #"+i+"\n"+e); }
				ls[i] = String.valueOf(registerCommands);
				i+=2;

				try{
					duration = Integer.parseInt(ls[i]);
				}catch (Exception e){ if(existing)LOGGER.log(Level.WARN, MOD_ID +" #"+i+"\n"+e); }
				ls[i] = String.valueOf(duration);
				i+=2;

				try{
					cooldown = -Integer.parseInt(ls[i]);
				}catch (Exception e){ if(existing)LOGGER.log(Level.WARN, MOD_ID +" #"+i+"\n"+e); }
				ls[i] = String.valueOf(-cooldown);
				i+=2;

				try{
					int indx = ls[i].indexOf(";");
					int duration = Integer.parseInt(ls[i].substring(indx+1));
					StatusEffect exit_effect = SimpleRegistry.STATUS_EFFECT.get(new Identifier(ls[i].substring(0,indx)));
					exitElytra = exitElytra.andThen((player) -> player.addStatusEffect(new StatusEffectInstance(exit_effect, duration)));
				}catch (Exception e){ if(existing)LOGGER.log(Level.WARN, MOD_ID +" #"+i+"\n"+e); }
				i+=2;
				try{
					if (ls[i].contains("true")) ls[i] = "true";
					else if (ls[i].contains("fapi")) ls[i] = "fapi";
					else ls[i] = "false";
				}catch (Exception e){ if(existing)LOGGER.log(Level.WARN, MOD_ID +" #"+i+"\n"+e); }
				i+=2;

				try{
					registerPlayerAbilityLib = !ls[i].contains("false");
				}catch (Exception e){ if(existing)LOGGER.log(Level.WARN, MOD_ID +" #"+i+"\n"+e); }
				ls[i] = String.valueOf(registerPlayerAbilityLib);

				if (hash != Arrays.hashCode(ls))
					Files.write(confFile.toPath(), Arrays.asList(ls));
				LOGGER.log(Level.INFO, MOD_ID +" successfully loaded config file");
			} catch(Exception e) {
				LOGGER.log(Level.ERROR, MOD_ID +" failed to load config file, using defaults\n"+e);
			}

			try {
				if(scriptFile.createNewFile()) {
					FileUtils.writeStringToFile(scriptFile, "false", StandardCharsets.UTF_8);
				}
				Predicate<ServerPlayerEntity> out = Default.SERVER_PLAYER_ENTITY.parse(Files.readString(scriptFile.toPath()).replaceAll("\\s", ""));
				if (out != null)
					canFly = canFly.and(out);
				LOGGER.log(Level.INFO, MOD_ID + " successfully loaded flight script file");
			} catch (Exception e) {
				LOGGER.log(Level.ERROR, MOD_ID +" failed to load flight script file\n"+e);
			}
			try {
				if(elytraScriptFile.createNewFile()) {
					FileUtils.writeStringToFile(elytraScriptFile, "true", StandardCharsets.UTF_8);
				}
				Predicate<ServerPlayerEntity> out = Default.SERVER_PLAYER_ENTITY.parse(Files.readString(elytraScriptFile.toPath()).replaceAll("\\s", ""));
				canElytraFly = out;
				LOGGER.log(Level.INFO, MOD_ID + " successfully loaded elytra script file");
			} catch (Exception e) {
				LOGGER.log(Level.ERROR, MOD_ID +" failed to load elytra script file\n"+e);
			}
			try {
				if(boostScriptFile.createNewFile()) {
					FileUtils.writeStringToFile(boostScriptFile, "true", StandardCharsets.UTF_8);
				}
				Predicate<ServerPlayerEntity> out = Default.SERVER_PLAYER_ENTITY.parse(Files.readString(boostScriptFile.toPath()).replaceAll("\\s", ""));
				canElytraBoost = out;
				LOGGER.log(Level.INFO, MOD_ID + " successfully loaded elytra boost script file");
			} catch (Exception e) {
				LOGGER.log(Level.ERROR, MOD_ID +" failed to load elytra boost script file\n"+e);
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

        public static final List<String> defaultDesc = Arrays.asList(
				"^-Generate Script Help? [true] true | ludicrous | false //note that some values are only available while in a world you can get them by running /ssf load survivalflight then disabling this setting",
				"^-Xp consumed per tick [0] 0 - ..",
				"^-Consume 1XP per X ticks [0] 0 - .. // if you prefer decimals/tick putting in for e.g.: 0.2 xp/t will auto translate",
				"^-Apply effect to player on mid-flight condition failure [] EffectID;tick_duration //e.g. slow_falling;20",
				"^-Required beacon level for beacon setting [0] 1-4",
				"^-Add reload settings command [true] true | false // (changing setting needs game restart)",
				"^-Flight duration in ticks before cool-down starts [0]",
				"^-Flight cool-down in ticks [0]",
				"^-Apply effect to player on elytra mid-flight condition failure [] EffectID;tick_duration //e.g. slow_falling;20",
				"^-Allow gliding without an elytra [false] true | false | fapi //WARNING disables vanilla elytra and Requires mod to be installed on client and server. (changing setting needs game restart).",
				"^-Enable PlayerAbilityLib compatibility [true] true | false //change if you need survival flight to disable other mods flight implementations"
				);
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

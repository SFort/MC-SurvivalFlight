	package tf.ssf.sfort.survivalflight;

	import net.fabricmc.api.ModInitializer;
	import net.fabricmc.loader.api.FabricLoader;
	import net.minecraft.entity.effect.StatusEffect;
	import net.minecraft.entity.effect.StatusEffectInstance;
	import net.minecraft.server.network.ServerPlayerEntity;
	import net.minecraft.util.Identifier;
	import net.minecraft.util.registry.SimpleRegistry;
	import org.apache.commons.io.FileUtils;
	import org.apache.logging.log4j.Level;
	import org.apache.logging.log4j.LogManager;
	import org.apache.logging.log4j.Logger;
	import tf.ssf.sfort.script.ScriptParser;

	import java.io.File;
	import java.nio.charset.StandardCharsets;
	import java.nio.file.Files;
	import java.nio.file.Path;
	import java.util.Arrays;
	import java.util.List;
	import java.util.function.Consumer;
	import java.util.function.Predicate;

	//TODO use mixin plugin
	public class Config implements ModInitializer {
		private static final String MOD_ID = "tf.ssf.sfort.survivalflight";
		public static Logger LOGGER = LogManager.getLogger();


		public static int duration = 0;
		public static int cooldown = 0;
        public static int xpPerTick = 0;
		public static int beaconLevel = 0;
		public static int ticksPerXP = 0;
		public static boolean hasBeaconCondition = false;
        private static boolean registerCommands = true;
		public static final Predicate<ServerPlayerEntity> canFly_init = player -> ((SPEA)player).bf$isSurvivalLike();
		public static final Predicate<ServerPlayerEntity> cantEltytra_init = player -> ((SPEA)player).bf$isSurvivalLike();
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
		public static Predicate<ServerPlayerEntity> cantElytraFly = cantEltytra_init;
		public static Predicate<ServerPlayerEntity> cantElytraBoost = cantEltytra_init;
		public static Consumer<ServerPlayerEntity> exit = player -> {};
		public static Consumer<ServerPlayerEntity> exitElytra = player -> {};
		public static Consumer<ServerPlayerEntity> tick = tick_init;

		public static void resetsettings(){
			hasBeaconCondition = false;
			canFly = canFly_init;
			exit = player -> {};
			exitElytra = player -> {};
			tick = tick_init;
			cantElytraBoost = cantEltytra_init;
			cantElytraFly = cantEltytra_init;
		}
        @Override
		public void onInitialize() {
        	reload_settings();
			if (FabricLoader.getInstance().isModLoaded("fabric-command-api-v1") && registerCommands){
				Commands.register();
			}
		}
		public static void reload_settings(){
			// Configs
			Path confFolder = FabricLoader.getInstance().getConfigDir();
			File old_confFile = new File(confFolder.toFile(), "SurvivalFlight.conf");
			File old_scriptFile = new File(confFolder.toFile(), "SurvivalFlightScript.conf");
			File old_scriptHelpFile = new File(confFolder.toFile(), "SurvivalFlightScriptHelp.conf");
			confFolder = confFolder.resolve("SurvivalFlight");
			File confFile = new File(confFolder.toFile(), "general.conf");
			File scriptFile = new File(confFolder.toFile(), "creative_flight.script");
			File scriptHelpFile = new File(confFolder.toFile(), "script_help.txt");
			File elytraScriptFile = new File(confFolder.toFile(), "elytra_flight.script");
			File boostScriptFile = new File(confFolder.toFile(), "elytra_boost.script");
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
				boolean generateScriptHelp = false;
				try {
					generateScriptHelp =ls[i].contains("true");
				}catch (Exception ignored){}
				if (generateScriptHelp) writeScriptHelp(scriptHelpFile.toPath());
				ls[i]=String.valueOf(generateScriptHelp);
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
				Predicate<ServerPlayerEntity> out = new ScriptParser<ServerPlayerEntity>().ScriptParse(Files.readString(scriptFile.toPath()).replaceAll("\\s", ""), new FlightScript());
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
				Predicate<ServerPlayerEntity> out = new ScriptParser<ServerPlayerEntity>().ScriptParse(Files.readString(elytraScriptFile.toPath()).replaceAll("\\s", ""), new FlightScript());
				if (out != null)
					cantElytraFly = cantElytraFly.and(out.negate());
				LOGGER.log(Level.INFO, MOD_ID + " successfully loaded elytra script file");
			} catch (Exception e) {
				LOGGER.log(Level.ERROR, MOD_ID +" failed to load elytra script file\n"+e);
			}
			try {
				if(boostScriptFile.createNewFile()) {
					FileUtils.writeStringToFile(boostScriptFile, "true", StandardCharsets.UTF_8);
				}
				Predicate<ServerPlayerEntity> out = new ScriptParser<ServerPlayerEntity>().ScriptParse(Files.readString(boostScriptFile.toPath()).replaceAll("\\s", ""), new FlightScript());
				if (out != null)
					cantElytraBoost = cantElytraBoost.and(out.negate());
				LOGGER.log(Level.INFO, MOD_ID + " successfully loaded elytra boost script file");
			} catch (Exception e) {
				LOGGER.log(Level.ERROR, MOD_ID +" failed to load elytra boost script file\n"+e);
			}
			if(duration != 0 || cooldown != 0)
				canFly = bit_and(canFly, player -> ((SPEA)player).bf$tickTimed());

			//TODO probably merge into canFly
			if (ticksPerXP != 0 || xpPerTick != 0){
				tick = splayer-> {
					SPEA player = (SPEA) splayer;
					if (Config.canFly.test(splayer)) {
						player.bf$tickXP();
					} else if (player.bf$isSurvivalLike() && splayer.getAbilities().allowFlying) {
						player.bf$fall();
					}
				};
			}
			if(hasBeaconCondition) {
				tick = ((Consumer<ServerPlayerEntity>)p -> ((SPEA) p).bf$tickBeacon()).andThen(tick);
			}
		}

        public static final List<String> defaultDesc = Arrays.asList(
                "^-Generate Script Help? [true] true | false",
                "^-Xp consumed per tick [0] 0 - ..",
                "^-Consume 1XP per X ticks [0] 0 - .. // if you prefer decimals/tick putting in for e.g.: 0.2 xp/t will auto translate",
                "^-Apply effect to player on mid-flight condition failure [] EffectID;tick_duration //e.g. slow_falling;20",
				"^-Required beacon level for beacon setting [0] 1-4",
				"^-Add reload settings command",
				"^-Flight duration in ticks before cool-down starts [0]",
				"^-Flight cool-down in ticks [0]",
				"^-Apply effect to player on elytra mid-flight condition failure [] EffectID;tick_duration //e.g. slow_falling;20"
				);
		public static final String scriptHelp = """
				I decided to burn a day and make this mod stupidly configurable.
				Lines are ignored.
				Available operations:
				"""+ ScriptParser.getHelp()+
				"\nAvailable Conditions:\n"+
				FlightScript.getHelp()
				;
		private static void writeScriptHelp(Path path){
			try {
				Files.writeString(path, scriptHelp);
			}catch (Exception e){ LOGGER.log(Level.WARN, MOD_ID +" #0\n"+e); }
		}
		private static Predicate<ServerPlayerEntity> bit_and(Predicate<ServerPlayerEntity> p1, Predicate<ServerPlayerEntity> p2){
			return p -> p1.test(p) & p2.test(p);
		}
	}

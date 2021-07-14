	package tf.ssf.sfort.survivalflight;

	import net.fabricmc.api.ModInitializer;
	import net.fabricmc.loader.api.FabricLoader;
	import net.minecraft.entity.Entity;
	import net.minecraft.entity.EquipmentSlot;
	import net.minecraft.entity.LivingEntity;
	import net.minecraft.entity.effect.StatusEffect;
	import net.minecraft.entity.effect.StatusEffectInstance;
	import net.minecraft.item.Item;
	import net.minecraft.item.ItemStack;
	import net.minecraft.server.MinecraftServer;
	import net.minecraft.server.network.ServerPlayerEntity;
	import net.minecraft.util.Identifier;
	import net.minecraft.util.registry.Registry;
	import net.minecraft.util.registry.SimpleRegistry;
	import org.apache.logging.log4j.Level;
	import org.apache.logging.log4j.LogManager;
	import org.apache.logging.log4j.Logger;

	import java.io.File;
	import java.nio.file.Files;
	import java.nio.file.Path;
	import java.util.*;
	import java.util.function.Consumer;
	import java.util.function.Predicate;

	public class Config implements ModInitializer, ScriptParser<ServerPlayerEntity> {
		private static final String MOD_ID = "tf.ssf.sfort.survivalflight";
		public static Logger LOGGER = LogManager.getLogger();
		public static StatusEffect exit_effect = null;



        public static int xpPerTick = 0;
		public static int beaconLevel = 0;
		public static int ticksPerXP = 0;
		public static boolean hasBeaconCondition = false;
        public static boolean hasExperianceCondition = false;

		public static Predicate<ServerPlayerEntity> canFly = (player) -> ((SPEA)player).bf$isSurvivalLike();
		public static Consumer<ServerPlayerEntity> exit = (player) -> {};

		public static Consumer<ServerPlayerEntity> tick = (splayer)-> {
			SPEA player = (SPEA) splayer;
			if (Config.canFly.test(splayer)) {
				if (!splayer.getAbilities().allowFlying)
					player.bf$fly();
			} else if (player.bf$isSurvivalLike() && splayer.getAbilities().allowFlying) {
				player.bf$fall();
			}
		};

        @Override
		public void onInitialize() {
			// Configs
			File confFolder = FabricLoader.getInstance().getConfigDirectory();
			File confFile = new File(confFolder, "SurvivalFlight.conf");
			File scriptFile = new File(confFolder, "SurvivalFlightScript.conf");
			File scriptHelpFile = new File(confFolder, "SurvivalFlightScriptHelp.conf");
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
					exit_effect = SimpleRegistry.STATUS_EFFECT.get(new Identifier(ls[i].substring(0,indx)));
					exit = exit.andThen((player) -> player.addStatusEffect(new StatusEffectInstance(exit_effect, duration)));
				}catch (Exception e){ if(existing)LOGGER.log(Level.WARN, MOD_ID +" #"+i+"\n"+e); }
                i+=2;
				try{
					beaconLevel = Integer.parseInt(ls[i]);
				}catch (Exception e){ if(existing)LOGGER.log(Level.WARN, MOD_ID +" #"+i+"\n"+e); }
				ls[i] = String.valueOf(beaconLevel);

                if (hash != Arrays.hashCode(ls))
				    Files.write(confFile.toPath(), Arrays.asList(ls));
				LOGGER.log(Level.INFO, MOD_ID +" successfully loaded config file");
			} catch(Exception e) {
				LOGGER.log(Level.ERROR, MOD_ID +" failed to load config file, using defaults\n"+e);
			}


            try {
            	if(!scriptFile.createNewFile()) {
					Predicate<ServerPlayerEntity> out = ScriptParse(Files.readString(scriptFile.toPath()).replaceAll("\\s", ""));
					if (out != null)
						canFly = canFly.and(out);
					LOGGER.log(Level.INFO, MOD_ID + " successfully loaded script file");
				}
            } catch (Exception e) {
                LOGGER.log(Level.ERROR, MOD_ID +" failed to load script file\n"+e);
            }
            hasExperianceCondition = ticksPerXP != 0 || xpPerTick != 0;
            if (hasExperianceCondition && hasBeaconCondition){
            	tick = (splayer)->{
            		SPEA player = (SPEA) splayer;
					if (Config.canFly.test(splayer)) {
						player.bf$tickXP();
						player.bf$checkBeacon();
					} else if (player.bf$isSurvivalLike() && splayer.getAbilities().allowFlying) {
						player.bf$fall();
					}
					player.bf$tickBeacon();
				};
			}else if (hasExperianceCondition){
				tick = (splayer)-> {
					SPEA player = (SPEA) splayer;
					if (Config.canFly.test(splayer)) {
						player.bf$tickXP();
					} else if (player.bf$isSurvivalLike() && splayer.getAbilities().allowFlying) {
						player.bf$fall();
					}
				};
			}else if (hasBeaconCondition){
				tick = (splayer)-> {
					SPEA player = (SPEA) splayer;
					if (Config.canFly.test(splayer)) {
						if (!splayer.getAbilities().allowFlying)
							player.bf$fly();
						player.bf$checkBeacon();
					} else if (player.bf$isSurvivalLike() && splayer.getAbilities().allowFlying) {
						player.bf$fall();
					}
					player.bf$tickBeacon();
				};
			}
        }

        public static final List<String> defaultDesc = Arrays.asList(
                "^-Generate Script Help? [true] true | false",
                "^-Xp consumed per tick [0] 0 - ..",
                "^-Consume 1XP per X ticks [0] 0 - .. // if you prefer decimals/tick putting in for e.g.: 0.2 xp/t will auto translate",
                "^-Apply effect to player on mid-flight condition failure [] EffectID;tick_duration //e.g. slow_falling;20",
				"^-Required beacon level for beacon setting [0] 1-4"
		);
		public static final String scriptHelp = """
						I decided to burn a day and make this mod stupidly configurable.
						Lines are ignored.
						Available operations:
						"""+
				String.format("\t%-60s%s%n","!Condition:value","- NOT")+
				String.format("\t%-60s%s%n","(Condition; Condition:value; ..)","- OR")+
				String.format("\t%-60s%s%n","[Condition; Condition:value; ..]","- AND")+
				String.format("\t%-60s%s%n","{Condition; Condition:value; ..}","- XOR")+
						"""
						Available Conditions:
						"""+
				String.format("\t%-20s%-40s%s%n","level","- Minimum required player level","int")+
				String.format("\t%-20s%-40s%s%n","hand","- Require item in main hand","ItemID")+
				String.format("\t%-20s%-40s%s%n","offhand","- Require item in off hand","ItemID")+
				String.format("\t%-20s%-40s%s%n","helm","- Require item as helmet","ItemID")+
				String.format("\t%-20s%-40s%s%n","chest","- Require item as chestplate","ItemID")+
				String.format("\t%-20s%-40s%s%n","legs","- Require item as leggings","ItemID")+
				String.format("\t%-20s%-40s%s%n","boots","- Require item as boots","ItemID")+
				String.format("\t%-20s%-40s%s%n","advancement","- Require advancement unlocked","AdvancementID")+
				String.format("\t%-20s%-40s%s%n","effect","- Require potion effect","EffectID")+
				String.format("\t%-20s%-40s%s%n","food","- Minimum required food","float")+
				String.format("\t%-20s%-40s%s%n","health","- Minimum required heath","float")+
				String.format("\t%-20s%-40s%s%n","height","- Minimum required player y height","float")+
				String.format("\t%-20s%s%n","full_hp","- Require full health")+
				String.format("\t%-20s%s%n","sprinting","- Require Sprinting")+
				String.format("\t%-20s%s%n","blocking","- Require Blocking")+
				String.format("\t%-20s%s%n","in_lava","- Require being in lava")+
				String.format("\t%-20s%s%n","on_fire","- Require being on fire")+
				String.format("\t%-20s%s%n","using","- Require using items")+
				String.format("\t%-20s%s%n","wet","- Require being wet")+
				String.format("\t%-20s%s%n","beacon","- Require beacon")
				;
		private static void writeScriptHelp(Path path){
			try {
				Files.writeString(path, scriptHelp);
			}catch (Exception e){ LOGGER.log(Level.WARN, MOD_ID +" #0\n"+e); }
		}
		@Override
		public Predicate<ServerPlayerEntity> getPredicate(String in, String val){
			return switch (in){
				case "level" -> {
					int arg = Integer.parseInt(val);
					yield (player) -> player.experienceLevel>=arg;
				}
				case "hand" -> {
					Item arg = getItem(val);
					yield (player) -> eq(arg, player.getMainHandStack());
				}
				case "offhand" -> {
					Item arg = getItem(val);
					yield (player) -> eq(arg, player.getOffHandStack());
				}
				case "helm" -> {
					Item arg = getItem(val);
					yield (player) -> eq(arg, player.getEquippedStack(EquipmentSlot.HEAD));
				}
				case "chest" -> {
					Item arg = getItem(val);
					yield (player) -> eq(arg, player.getEquippedStack(EquipmentSlot.CHEST));
				}
				case "legs" -> {
					Item arg = getItem(val);
					yield (player) -> eq(arg, player.getEquippedStack(EquipmentSlot.LEGS));
				}
				case "boots" -> {
					Item arg = getItem(val);
					yield (player) -> eq(arg, player.getEquippedStack(EquipmentSlot.FEET));
				}
				case "food" -> {
					float arg = Float.parseFloat(val);
					yield (player) -> player.getHealth()>=arg;
				}
				case "health" -> {
					float arg = Float.parseFloat(val);
					yield (player) -> player.getHungerManager().getFoodLevel()>=arg;
				}
				case "height" -> {
					float arg = Float.parseFloat(val);
					yield (player) -> player.getPos().y>=arg;
				}
				case "advancement" -> {
					Identifier arg = new Identifier(val);
					yield (player) -> {
						MinecraftServer server = player.getServer();
						if (server == null) return false;
						return player.getAdvancementTracker().getProgress(server.getAdvancementLoader().get(arg)).isDone();
					};
				}
				case "effect" -> {
					StatusEffect arg = SimpleRegistry.STATUS_EFFECT.get(new Identifier(val));
					yield (player) -> player.hasStatusEffect(arg);
				}
				default -> throw new IllegalStateException("Unexpected value while parsing predicate: " + in);
			};
		}
		@Override
		public Predicate<ServerPlayerEntity> getPredicate(String in){
			return switch (in) {
				case "full_hp" -> (player) -> player.getHealth() == player.getMaxHealth();
				case "sprinting" -> Entity::isSprinting;
				case "blocking" -> LivingEntity::isBlocking;
				case "in_lava" -> Entity::isInLava;
				case "on_fire" -> Entity::isOnFire;
				case "wet" -> Entity::isWet;
				case "using" -> LivingEntity::isUsingItem;
				case "beacon" -> {
					hasBeaconCondition = true;
					yield (player) -> (((SPEA) player).bf$hasBeacon());
				}
				default -> throw new IllegalStateException("Unexpected value while parsing bool predicate: " + in);
			};
		}
		private static Item getItem(String id){
			return Registry.ITEM.get(new Identifier(id));
		}
		private static boolean eq(Item required, ItemStack current){
			return required != null && required == current.getItem();
		}
	}

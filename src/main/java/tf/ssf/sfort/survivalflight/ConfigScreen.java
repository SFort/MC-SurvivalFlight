package tf.ssf.sfort.survivalflight;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import tf.ssf.sfort.script.Default;
import tf.ssf.sfort.script.ScriptingScreen;

public class ConfigScreen extends Screen {
	final Screen screen;
	ConfigScreen(Screen parent) {
		super(Text.of("Survival Flight"));
		this.screen = parent;
	}
	@Override
	public void init(){
				addDrawableChild(ButtonWidget.builder(Text.of("Survival Flight Script"),
				p ->{
					client.setScreen(new ScriptingScreen(
							Text.of("Survival Flight Script"),
							this,
							new ScriptingScreen.Script(
									"§bSurvival Flight",
									Default.SERVER_PLAYER_ENTITY,
									Config::writeFly,
									null,
									Config::readFly,
									ScriptingScreen.getDefaultEmbed()
							)
					));
				}).size(150, 20).position(width/2-75, height/2-30).build());
		addDrawableChild(ButtonWidget.builder(Text.of("Elytra Flight Script"),
				p ->{
					client.setScreen(new ScriptingScreen(
							Text.of("Elytra Flight Script"),
							this,
							new ScriptingScreen.Script(
									"§bSurvival Flight - Elytra Flight",
									Default.SERVER_PLAYER_ENTITY,
									Config::writeElytra,
									null,
									Config::readElytra,
									ScriptingScreen.getDefaultEmbed()
							)
					));
				}).size(150, 20).position(width/2-75, height/2-10).build());

		addDrawableChild(ButtonWidget.builder(Text.of("Firework Boost Script"),
				p ->{
					client.setScreen(new ScriptingScreen(
							Text.of("Survival Flight Script"),
							this,
							new ScriptingScreen.Script(
									"§bSurvival Flight - Firework Boost",
									Default.SERVER_PLAYER_ENTITY,
									Config::writeElytraBoost,
									null,
									Config::readElytraBoost,
									ScriptingScreen.getDefaultEmbed()
							)
					));
				}).size(150, 20).position(width/2-75, height/2+10).build());
	}
	@Override
	public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {
		int x = width/2-130;
		int y = height/2-80;
		this.renderBackground(drawContext, mouseX, mouseY, delta);
		super.render(drawContext, mouseX, mouseY, delta);
		drawContext.drawTextWithShadow(textRenderer,"UI doesn't allow editing non-scripting options yet", width/2-123, height/2-70, -1);
		drawContext.drawTextWithShadow(textRenderer,".minecraft/config/SurvivalFlight/general.sf.ini", width/2-115, height/2-50, -1);
		drawContext.fill(x, y, x+260, y+1, -1);
		drawContext.fill(x, y, x+1, y+130, -1);
		drawContext.fill(x, y+130-1, x+260, y+130, -1);
		drawContext.fill(x+260-1, y, x+260, y+130, -1);
	}
	@Override
	public void close(){
		client.setScreen(screen);
	}
}

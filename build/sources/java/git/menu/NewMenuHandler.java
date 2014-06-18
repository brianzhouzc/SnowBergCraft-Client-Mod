package git.menu;

import java.io.IOException;

import net.minecraft.client.gui.GuiMainMenu;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class NewMenuHandler {
	public static NewMenuHandler instance = new NewMenuHandler();

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void openMainMenu(GuiOpenEvent event) throws IOException {
		if (event.gui instanceof GuiMainMenu) {
			event.gui = new NewMenu();
		}
	}
}

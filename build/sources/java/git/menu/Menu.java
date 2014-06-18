package git.menu;

import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@Mod(modid = "SnowBergCraft Client Mod")
public class Menu {
	@Mod.Instance("Menu")
	public static Menu instance;

	@Mod.EventHandler
	@SideOnly(Side.CLIENT)
	public void preInit(FMLPreInitializationEvent e) {
		MinecraftForge.EVENT_BUS.register(NewMenuHandler.instance);
	}
}

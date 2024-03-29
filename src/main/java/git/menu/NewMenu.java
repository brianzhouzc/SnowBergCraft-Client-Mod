package git.menu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiButtonLanguage;
import net.minecraft.client.gui.GuiConfirmOpenLink;
import net.minecraft.client.gui.GuiLanguage;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenOnlineServers;
import net.minecraft.client.gui.GuiSelectWorld;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.mco.ExceptionMcoService;
import net.minecraft.client.mco.ExceptionRetryCall;
import net.minecraft.client.mco.GuiScreenClientOutdated;
import net.minecraft.client.mco.McoClient;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Session;
import net.minecraft.world.demo.DemoWorldServer;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.WorldInfo;

import org.apache.commons.io.Charsets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Project;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import cpw.mods.fml.client.GuiModList;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class NewMenu extends GuiScreen {
	private static final AtomicInteger field_146973_f = new AtomicInteger(0);
	private static final Logger logger = LogManager.getLogger();
	/** The RNG used by the Main Menu Screen. */
	private static final Random rand = new Random();
	/** Counts the number of screen updates. */
	private float updateCounter;
	/** The splash message. */
	private String splashText;
	private GuiButton buttonResetDemo;
	/** Timer used to rotate the panorama, increases every tick. */
	private int panoramaTimer;
	/**
	 * Texture allocated for the current viewport of the main menu's panorama
	 * background.
	 */
	private DynamicTexture viewportTexture;
	private boolean field_96141_q = true;
	private static boolean field_96140_r;
	private static boolean field_96139_s;
	private final Object field_104025_t = new Object();
	private String field_92025_p;
	private String field_146972_A;
	private String field_104024_v;
	private static final ResourceLocation splashTexts = new ResourceLocation(
			"texts/splashes.txt");
	private static final ResourceLocation minecraftTitleTextures = new ResourceLocation(
			"textures/gui/title/minecraft.png");
	/** An array of all the paths to the panorama pictures. */
	private static final ResourceLocation[] titlePanoramaPaths = new ResourceLocation[] {
			new ResourceLocation("textures/gui/title/background/panorama_0.png"),
			new ResourceLocation("textures/gui/title/background/panorama_1.png"),
			new ResourceLocation("textures/gui/title/background/panorama_2.png"),
			new ResourceLocation("textures/gui/title/background/panorama_3.png"),
			new ResourceLocation("textures/gui/title/background/panorama_4.png"),
			new ResourceLocation("textures/gui/title/background/panorama_5.png") };
	public static final String field_96138_a = "Please click "
			+ EnumChatFormatting.UNDERLINE + "here" + EnumChatFormatting.RESET
			+ " for more information.";
	private int field_92024_r;
	private int field_92023_s;
	private int field_92022_t;
	private int field_92021_u;
	private int field_92020_v;
	private int field_92019_w;
	private ResourceLocation field_110351_G;
	private GuiButton minecraftRealmsButton;
	@SuppressWarnings("unused")
	private static final String __OBFID = "CL_00001154";

	private GuiButton fmlModButton = null;

	private static double version = 1.0;
	private static String updatemessage = getJsonString("Update.html");
	private static String announcement = getJsonString("Announcement.html");

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public NewMenu() {
		this.field_146972_A = field_96138_a;
		this.splashText = "missingno";
		BufferedReader bufferedreader = null;

		try {
			ArrayList arraylist = new ArrayList();
			bufferedreader = new BufferedReader(new InputStreamReader(Minecraft
					.getMinecraft().getResourceManager()
					.getResource(splashTexts).getInputStream(), Charsets.UTF_8));
			String s;

			while ((s = bufferedreader.readLine()) != null) {
				s = s.trim();

				if (!s.isEmpty()) {
					arraylist.add(s);
				}
			}

			if (!arraylist.isEmpty()) {
				do {
					this.splashText = (String) arraylist.get(rand
							.nextInt(arraylist.size()));
				} while (this.splashText.hashCode() == 125780783);
			}
		} catch (IOException ioexception1) {
			;
		} finally {
			if (bufferedreader != null) {
				try {
					bufferedreader.close();
				} catch (IOException ioexception) {
					;
				}
			}
		}

		this.updateCounter = rand.nextFloat();
		this.field_92025_p = "";

		if (isOutdate()) {
			this.field_92025_p = "§6您当前使用的雪漫世界客户端mod版本 v" + version + " 已过期";
			this.field_146972_A = "§6请点击此处获取最新的雪漫世界客户端mod";
			this.field_104024_v = "http://cn.snowbergcraft.com/client/clientupdate.html";
		}

	}

	/**
	 * Called from the main game loop to update the screen.
	 */
	public void updateScreen() {
		++this.panoramaTimer;
	}

	/**
	 * Returns true if this GUI should pause the game when it is displayed in
	 * single-player
	 */
	public boolean doesGuiPauseGame() {
		return false;
	}

	/**
	 * Fired when a key is typed. This is the equivalent of
	 * KeyListener.keyTyped(KeyEvent e).
	 */
	protected void keyTyped(char par1, int par2) {
	}

	/**
	 * Adds the buttons (and other controls) to the screen in question.
	 */
	@SuppressWarnings({ "unused", "unchecked" })
	public void initGui() {
		this.viewportTexture = new DynamicTexture(256, 256);
		this.field_110351_G = this.mc.getTextureManager()
				.getDynamicTextureLocation("background", this.viewportTexture);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());

		if (calendar.get(2) + 1 == 11 && calendar.get(5) == 9) {
			this.splashText = "Happy birthday, ez!";
		} else if (calendar.get(2) + 1 == 6 && calendar.get(5) == 1) {
			this.splashText = "Happy birthday, Notch!";
		} else if (calendar.get(2) + 1 == 12 && calendar.get(5) == 24) {
			this.splashText = "Merry X-mas!";
		} else if (calendar.get(2) + 1 == 1 && calendar.get(5) == 1) {
			this.splashText = "Happy new year!";
		} else if (calendar.get(2) + 1 == 10 && calendar.get(5) == 31) {
			this.splashText = "OOoooOOOoooo! Spooky!";
		} else if (calendar.get(2) + 1 == 5 && calendar.get(5) == 15) {
			this.splashText = "Happy Birthday, Brian!";
		}

		boolean flag = true;
		int i = this.height / 4 + 48;

		if (this.mc.isDemo()) {
			this.addDemoButtons(i, 24);
		} else {
			this.addSingleplayerMultiplayerButtons(i, 24);
		}

		this.func_130020_g();
		this.buttonList.add(new GuiButton(0, this.width / 2 - 100, i + 72 + 12,
				98, 20, I18n.format("menu.options", new Object[0])));
		this.buttonList.add(new GuiButton(4, this.width / 2 + 2, i + 72 + 12,
				98, 20, I18n.format("menu.quit", new Object[0])));
		this.buttonList.add(new GuiButtonLanguage(5, this.width / 2 - 124,
				i + 72 + 12));
		Object object = this.field_104025_t;

		synchronized (this.field_104025_t) {
			this.field_92023_s = this.fontRendererObj
					.getStringWidth(this.field_92025_p);
			this.field_92024_r = this.fontRendererObj
					.getStringWidth(this.field_146972_A);
			int j = Math.max(this.field_92023_s, this.field_92024_r);
			this.field_92022_t = (this.width - j) / 2;
			this.field_92021_u = ((GuiButton) this.buttonList.get(0)).yPosition - 24;
			this.field_92020_v = this.field_92022_t + j;
			this.field_92019_w = this.field_92021_u + 24;
		}
	}

	private void func_130020_g() {
		if (this.field_96141_q) {
			if (!field_96140_r) {
				field_96140_r = true;
				(new Thread("MCO Availability Checker #"
						+ field_146973_f.incrementAndGet()) {
					@SuppressWarnings("unused")
					private static final String __OBFID = "CL_00001155";

					public void run() {
						Session session = NewMenu.this.mc.getSession();
						McoClient mcoclient = new McoClient(
								session.getSessionID(), session.getUsername(),
								"1.7.2", Minecraft.getMinecraft().getProxy());
						boolean flag = false;

						for (int i = 0; i < 3; ++i) {
							try {
								Boolean obool = mcoclient.func_148687_b();

								if (obool.booleanValue()) {
									NewMenu.this.func_130022_h();
								}

								NewMenu.field_96139_s = obool.booleanValue();
							} catch (ExceptionRetryCall exceptionretrycall) {
								flag = true;
							} catch (ExceptionMcoService exceptionmcoservice) {
								NewMenu.logger
										.error("Couldn\'t connect to Realms");
							} catch (IOException ioexception) {
								NewMenu.logger
										.error("Couldn\'t parse response connecting to Realms");
							}

							if (!flag) {
								break;
							}

							try {
								Thread.sleep(10000L);
							} catch (InterruptedException interruptedexception) {
								Thread.currentThread().interrupt();
							}
						}
					}
				}).start();
			} else if (field_96139_s) {
				this.func_130022_h();
			}
		}
	}

	private void func_130022_h() {
		this.minecraftRealmsButton.visible = true;
		// fmlModButton.width = 98;
		fmlModButton.xPosition = this.width / 2 + 2;
	}

	/**
	 * Adds Singleplayer and Multiplayer buttons on Main Menu for players who
	 * have bought the game.
	 */
	@SuppressWarnings("unchecked")
	private void addSingleplayerMultiplayerButtons(int par1, int par2) {
		this.buttonList.add(new GuiButton(1, this.width / 2 + 2, par1, 98, 20,
				I18n.format("menu.singleplayer", new Object[0])));

		this.buttonList.add(new GuiButton(2, this.width / 2 + 2, par1 + par2
				* 1, 98, 20, I18n.format("menu.multiplayer", new Object[0])));
		// this.buttonList.add(new GuiButton(1, this.width / 2 - 100, par1,
		// I18n.format("menu.singleplayer", new Object[0])));
		// this.buttonList.add(new GuiButton(2, this.width / 2 - 100, par1 +
		// par2 * 1, I18n.format("menu.multiplayer", new Object[0])));
		// If Minecraft Realms is enabled, halve the size of both buttons and
		// set them next to eachother.
		fmlModButton = new GuiButton(6, this.width / 2 + 2, par1 + par2 * 2,
				98, 20, "Mods");
		this.buttonList.add(fmlModButton);

		// minecraftRealmsButton = new GuiButton(14, this.width / 2 - 100, par1
		// + par2 * 2, I18n.format("menu.online"));
		// minecraftRealmsButton.width = 98;
		// minecraftRealmsButton.xPosition = this.width / 2 - 100;
		// this.buttonList.add(minecraftRealmsButton);
		// this.minecraftRealmsButton.visible = false;
	}

	/**
	 * Adds Demo buttons on Main Menu for players who are playing Demo.
	 */
	@SuppressWarnings("unchecked")
	private void addDemoButtons(int par1, int par2) {
		this.buttonList.add(new GuiButton(11, this.width / 2 - 100, par1, I18n
				.format("menu.playdemo", new Object[0])));
		this.buttonList.add(this.buttonResetDemo = new GuiButton(12,
				this.width / 2 - 100, par1 + par2 * 1, I18n.format(
						"menu.resetdemo", new Object[0])));
		ISaveFormat isaveformat = this.mc.getSaveLoader();
		WorldInfo worldinfo = isaveformat.getWorldInfo("Demo_World");

		if (worldinfo == null) {
			this.buttonResetDemo.enabled = false;
		}
	}

	protected void actionPerformed(GuiButton p_146284_1_) {
		if (p_146284_1_.id == 0) {
			this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings));
		}

		if (p_146284_1_.id == 5) {
			this.mc.displayGuiScreen(new GuiLanguage(this,
					this.mc.gameSettings, this.mc.getLanguageManager()));
		}

		if (p_146284_1_.id == 1) {
			this.mc.displayGuiScreen(new GuiSelectWorld(this));
		}

		if (p_146284_1_.id == 2) {
			this.mc.displayGuiScreen(new GuiMultiplayer(this));
		}

		if (p_146284_1_.id == 14 && this.minecraftRealmsButton.visible) {
			this.func_140005_i();
		}

		if (p_146284_1_.id == 4) {
			this.mc.shutdown();
		}

		if (p_146284_1_.id == 6) {
			this.mc.displayGuiScreen(new GuiModList(this));
		}

		if (p_146284_1_.id == 11) {
			this.mc.launchIntegratedServer("Demo_World", "Demo_World",
					DemoWorldServer.demoWorldSettings);
		}

		if (p_146284_1_.id == 12) {
			ISaveFormat isaveformat = this.mc.getSaveLoader();
			WorldInfo worldinfo = isaveformat.getWorldInfo("Demo_World");

			if (worldinfo != null) {
				GuiYesNo guiyesno = GuiSelectWorld.func_146623_a(this,
						worldinfo.getWorldName(), 12);
				this.mc.displayGuiScreen(guiyesno);
			}
		}
	}

	private void func_140005_i() {
		Session session = this.mc.getSession();
		McoClient mcoclient = new McoClient(session.getSessionID(),
				session.getUsername(), "1.7.2", Minecraft.getMinecraft()
						.getProxy());

		try {
			if (mcoclient.func_148695_c().booleanValue()) {
				this.mc.displayGuiScreen(new GuiScreenClientOutdated(this));
			} else {
				this.mc.displayGuiScreen(new GuiScreenOnlineServers(this));
			}
		} catch (ExceptionMcoService exceptionmcoservice) {
			logger.error("Couldn\'t connect to realms");
		} catch (IOException ioexception) {
			logger.error("Couldn\'t connect to realms");
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void confirmClicked(boolean par1, int par2) {
		if (par1 && par2 == 12) {
			ISaveFormat isaveformat = this.mc.getSaveLoader();
			isaveformat.flushCache();
			isaveformat.deleteWorldDirectory("Demo_World");
			this.mc.displayGuiScreen(this);
		} else if (par2 == 13) {
			if (par1) {
				try {
					Class oclass = Class.forName("java.awt.Desktop");
					Object object = oclass
							.getMethod("getDesktop", new Class[0]).invoke(
									(Object) null, new Object[0]);
					oclass.getMethod("browse", new Class[] { URI.class })
							.invoke(object,
									new Object[] { new URI(this.field_104024_v) });
				} catch (Throwable throwable) {
					logger.error("Couldn\'t open link", throwable);
				}
			}

			this.mc.displayGuiScreen(this);
		}
	}

	/**
	 * Draws the main menu panorama
	 */
	private void drawPanorama(int par1, int par2, float par3) {
		Tessellator tessellator = Tessellator.instance;
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glPushMatrix();
		GL11.glLoadIdentity();
		Project.gluPerspective(120.0F, 1.0F, 0.05F, 10.0F);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glPushMatrix();
		GL11.glLoadIdentity();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
		GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glDepthMask(false);
		OpenGlHelper.glBlendFunc(770, 771, 1, 0);
		byte b0 = 8;

		for (int k = 0; k < b0 * b0; ++k) {
			GL11.glPushMatrix();
			float f1 = ((float) (k % b0) / (float) b0 - 0.5F) / 64.0F;
			float f2 = ((float) (k / b0) / (float) b0 - 0.5F) / 64.0F;
			float f3 = 0.0F;
			GL11.glTranslatef(f1, f2, f3);
			GL11.glRotatef(
					MathHelper
							.sin(((float) this.panoramaTimer + par3) / 400.0F) * 25.0F + 20.0F,
					1.0F, 0.0F, 0.0F);
			GL11.glRotatef(-((float) this.panoramaTimer + par3) * 0.1F, 0.0F,
					1.0F, 0.0F);

			for (int l = 0; l < 6; ++l) {
				GL11.glPushMatrix();

				if (l == 1) {
					GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
				}

				if (l == 2) {
					GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
				}

				if (l == 3) {
					GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
				}

				if (l == 4) {
					GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
				}

				if (l == 5) {
					GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
				}

				this.mc.getTextureManager().bindTexture(titlePanoramaPaths[l]);
				tessellator.startDrawingQuads();
				tessellator.setColorRGBA_I(16777215, 255 / (k + 1));
				float f4 = 0.0F;
				tessellator.addVertexWithUV(-1.0D, -1.0D, 1.0D,
						(double) (0.0F + f4), (double) (0.0F + f4));
				tessellator.addVertexWithUV(1.0D, -1.0D, 1.0D,
						(double) (1.0F - f4), (double) (0.0F + f4));
				tessellator.addVertexWithUV(1.0D, 1.0D, 1.0D,
						(double) (1.0F - f4), (double) (1.0F - f4));
				tessellator.addVertexWithUV(-1.0D, 1.0D, 1.0D,
						(double) (0.0F + f4), (double) (1.0F - f4));
				tessellator.draw();
				GL11.glPopMatrix();
			}

			GL11.glPopMatrix();
			GL11.glColorMask(true, true, true, false);
		}

		tessellator.setTranslation(0.0D, 0.0D, 0.0D);
		GL11.glColorMask(true, true, true, true);
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glPopMatrix();
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glPopMatrix();
		GL11.glDepthMask(true);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
	}

	/**
	 * Rotate and blurs the skybox view in the main menu
	 */
	private void rotateAndBlurSkybox(float par1) {
		this.mc.getTextureManager().bindTexture(this.field_110351_G);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER,
				GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER,
				GL11.GL_LINEAR);
		GL11.glCopyTexSubImage2D(GL11.GL_TEXTURE_2D, 0, 0, 0, 0, 0, 256, 256);
		GL11.glEnable(GL11.GL_BLEND);
		OpenGlHelper.glBlendFunc(770, 771, 1, 0);
		GL11.glColorMask(true, true, true, false);
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		byte b0 = 3;

		for (int i = 0; i < b0; ++i) {
			tessellator
					.setColorRGBA_F(1.0F, 1.0F, 1.0F, 1.0F / (float) (i + 1));
			int j = this.width;
			int k = this.height;
			float f1 = (float) (i - b0 / 2) / 256.0F;
			tessellator.addVertexWithUV((double) j, (double) k,
					(double) this.zLevel, (double) (0.0F + f1), 1.0D);
			tessellator.addVertexWithUV((double) j, 0.0D, (double) this.zLevel,
					(double) (1.0F + f1), 1.0D);
			tessellator.addVertexWithUV(0.0D, 0.0D, (double) this.zLevel,
					(double) (1.0F + f1), 0.0D);
			tessellator.addVertexWithUV(0.0D, (double) k, (double) this.zLevel,
					(double) (0.0F + f1), 0.0D);
		}

		tessellator.draw();
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glColorMask(true, true, true, true);
	}

	/**
	 * Renders the skybox in the main menu
	 */
	private void renderSkybox(int par1, int par2, float par3) {
		this.mc.getFramebuffer().unbindFramebuffer();
		GL11.glViewport(0, 0, 256, 256);
		this.drawPanorama(par1, par2, par3);
		this.rotateAndBlurSkybox(par3);
		this.rotateAndBlurSkybox(par3);
		this.rotateAndBlurSkybox(par3);
		this.rotateAndBlurSkybox(par3);
		this.rotateAndBlurSkybox(par3);
		this.rotateAndBlurSkybox(par3);
		this.rotateAndBlurSkybox(par3);
		this.mc.getFramebuffer().bindFramebuffer(true);
		GL11.glViewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		float f1 = this.width > this.height ? 120.0F / (float) this.width
				: 120.0F / (float) this.height;
		float f2 = (float) this.height * f1 / 256.0F;
		float f3 = (float) this.width * f1 / 256.0F;
		tessellator.setColorRGBA_F(1.0F, 1.0F, 1.0F, 1.0F);
		int k = this.width;
		int l = this.height;
		tessellator.addVertexWithUV(0.0D, (double) l, (double) this.zLevel,
				(double) (0.5F - f2), (double) (0.5F + f3));
		tessellator.addVertexWithUV((double) k, (double) l,
				(double) this.zLevel, (double) (0.5F - f2),
				(double) (0.5F - f3));
		tessellator.addVertexWithUV((double) k, 0.0D, (double) this.zLevel,
				(double) (0.5F + f2), (double) (0.5F - f3));
		tessellator.addVertexWithUV(0.0D, 0.0D, (double) this.zLevel,
				(double) (0.5F + f2), (double) (0.5F + f3));
		tessellator.draw();
	}

	/**
	 * Draws the screen and all the components in it.
	 */
	public void drawScreen(int par1, int par2, float par3) {
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		this.renderSkybox(par1, par2, par3);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		Tessellator tessellator = Tessellator.instance;
		short short1 = 274;
		int k = this.width / 2 - short1 / 2;
		byte b0 = 30;
		this.drawGradientRect(0, 0, this.width, this.height, -2130706433,
				16777215);
		this.drawGradientRect(0, 0, this.width, this.height, 0,
				Integer.MIN_VALUE);
		this.mc.getTextureManager().bindTexture(minecraftTitleTextures);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		if ((double) this.updateCounter < 1.0E-4D) {
			this.drawTexturedModalRect(k + 0, b0 + 0, 0, 0, 99, 44);
			this.drawTexturedModalRect(k + 99, b0 + 0, 129, 0, 27, 44);
			this.drawTexturedModalRect(k + 99 + 26, b0 + 0, 126, 0, 3, 44);
			this.drawTexturedModalRect(k + 99 + 26 + 3, b0 + 0, 99, 0, 26, 44);
			this.drawTexturedModalRect(k + 155, b0 + 0, 0, 45, 155, 44);
		} else {
			this.drawTexturedModalRect(k + 0, b0 + 0, 0, 0, 155, 44);
			this.drawTexturedModalRect(k + 155, b0 + 0, 0, 45, 155, 44);
		}

		tessellator.setColorOpaque_I(-1);
		GL11.glPushMatrix();
		GL11.glTranslatef((float) (this.width / 2 + 90), 70.0F, 0.0F);
		GL11.glRotatef(-20.0F, 0.0F, 0.0F, 1.0F);
		float f1 = 1.8F - MathHelper
				.abs(MathHelper.sin((float) (Minecraft.getSystemTime() % 1000L)
						/ 1000.0F * (float) Math.PI * 2.0F) * 0.1F);
		f1 = f1
				* 100.0F
				/ (float) (this.fontRendererObj.getStringWidth(this.splashText) + 32);
		GL11.glScalef(f1, f1, f1);
		this.drawCenteredString(this.fontRendererObj, this.splashText, 0, -8,
				-256);
		GL11.glPopMatrix();
		String s = "Minecraft 1.7.2";

		if (this.mc.isDemo()) {
			s = s + " Demo";
		}

		List<String> brandings = Lists.reverse(FMLCommonHandler.instance()
				.getBrandings(true));
		for (int i = 0; i < brandings.size(); i++) {
			String brd = brandings.get(i);
			if (!Strings.isNullOrEmpty(brd)) {
				this.drawString(this.fontRendererObj, brd, 2, this.height
						- (10 + i * (this.fontRendererObj.FONT_HEIGHT + 1)),
						16777215);
			}
		}
		// ForgeHooksClient.renderMainMenu(this, fontRendererObj, width,
		// height);
		String s1 = "Modded by SnowBergCraft™ Copyright Mojang AB";

		int varinfow = this.width / 2 - 119;
		int varinfoh = this.height / 4 + 48;
		if (!isOutdate()) {
			this.drawGradientRect(varinfow - 4, varinfoh - 7, varinfow + 110,
					varinfoh + 76, 1610612736, 1610612736);
		} else {
			this.drawGradientRect(varinfow - 4, varinfoh, varinfow + 110,
					varinfoh + 76, 1610612736, 1610612736);

		}

		this.drawString(this.fontRendererObj, s1, this.width
				- this.fontRendererObj.getStringWidth(s1) - 2,
				this.height - 10, -1);
		if (isOutdate()) {
			varinfoh = varinfoh + 3;
		}
		this.drawString(this.fontRendererObj, getText(1), varinfow, varinfoh,
				16777215);
		this.drawString(this.fontRendererObj, getText(2), varinfow,
				varinfoh + 12, 16777215);
		this.drawString(this.fontRendererObj, getText(3), varinfow,
				varinfoh + 24, 16777215);
		this.drawString(this.fontRendererObj, getText(4), varinfow,
				varinfoh + 36, 16777215);
		this.drawString(this.fontRendererObj, getText(5), varinfow,
				varinfoh + 48, 16777215);
		this.drawString(this.fontRendererObj, getText(6), varinfow,
				varinfoh + 60, 16777215);

		// drawRect(0,0,800,12,0x80000000);
		//
		// this.drawString(this.fontRendererObj, "2333333", 1, 2, -1);

		if (this.field_92025_p != null && this.field_92025_p.length() > 0) {
			drawRect(this.field_92022_t - 2, this.field_92021_u - 2,
					this.field_92020_v + 2, this.field_92019_w - 1, 1428160512);
			this.drawString(this.fontRendererObj, this.field_92025_p,
					this.field_92022_t, this.field_92021_u, -1);
			this.drawString(this.fontRendererObj, this.field_146972_A,
					(this.width - this.field_92024_r) / 2,
					((GuiButton) this.buttonList.get(0)).yPosition - 12, -1);
		}

		super.drawScreen(par1, par2, par3);
	}

	/**
	 * Called when the mouse is clicked.
	 */
	@SuppressWarnings("unused")
	protected void mouseClicked(int par1, int par2, int par3) {
		super.mouseClicked(par1, par2, par3);
		Object object = this.field_104025_t;

		synchronized (this.field_104025_t) {
			if (this.field_92025_p.length() > 0 && par1 >= this.field_92022_t
					&& par1 <= this.field_92020_v && par2 >= this.field_92021_u
					&& par2 <= this.field_92019_w) {
				GuiConfirmOpenLink guiconfirmopenlink = new GuiConfirmOpenLink(
						this, this.field_104024_v, 13, true);
				guiconfirmopenlink.func_146358_g();
				this.mc.displayGuiScreen(guiconfirmopenlink);
			}
		}
	}

	public static String getJsonString(String file) {
		URL url;
		String temp;
		StringBuffer sb = new StringBuffer();
		try {
			url = new URL("http://cn.snowbergcraft.com/client/" + file);
			BufferedReader in = new BufferedReader(new InputStreamReader(
					url.openStream(), "utf-8"));
			while ((temp = in.readLine()) != null) {
				sb.append(temp);
			}
			in.close();
		} catch (final MalformedURLException me) {
			me.getMessage();
			return "offline";
		} catch (final IOException e) {
			e.printStackTrace();
			return "offline";
		}
		return sb.toString();
	}

	public static String getText(int linenum) {
		if (announcement != "offline") {
			JSONObject jsonobj = new JSONObject(announcement);
			String text = jsonobj.getString("line" + linenum);
			return text;
		} else if (linenum == 1) {
			return "无法连接至互联网！";
		} else {
			return "";
		}
	}

	public static boolean isOutdate() {
		if (!updatemessage.equalsIgnoreCase("offline")) {
			JSONObject jsonobj = new JSONObject(updatemessage);
			double newversion = jsonobj.getDouble("Version");
			if (newversion > version) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
}
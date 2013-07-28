import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;
 
public class Game {
  
	/** time at last frame */
	long lastFrame;
 
	/** frames per second */
	int fps;
	/** last fps time */
	long lastFPS;
	
	private List<Entity> objects;
	private List<Tile> tiles;
 
	public void start() {
		try {
			Display.setDisplayMode(new DisplayMode(800, 600));
			Display.create();
		} catch (LWJGLException e) {
			e.printStackTrace();
			System.exit(0);
		}
 
		Actor player = new Actor();
		
		
		
		objects = new LinkedList<Entity>();
		
		 
		
		Texture texture = null;
		try {
			texture = TextureLoader.getTexture("JPG", ResourceLoader.getResourceAsStream("img002.jpg"));	
			System.out.println("Texture loaded: "+texture);
			System.out.println(">> Image width: "+texture.getImageWidth());
			System.out.println(">> Image height: "+texture.getImageHeight());
			System.out.println(">> Texture width: "+texture.getTextureWidth());
			System.out.println(">> Texture height: "+texture.getTextureHeight());
			System.out.println(">> Texture ID: "+texture.getTextureID());
		} catch (IOException e) {
			e.printStackTrace();
		}
		Ground ground = new Ground();
		player.addGround(ground);
		objects.add(ground);
		

		
		tiles = new LinkedList<Tile>();
		
		Random rand = new Random();
		Tile t;
		for(int i=0; i<=4; i++){
			for(int j=0; j<=4; j++){
				t = new Tile(rand, i, j, texture);
				//objects.add(t);
				tiles.add(t);
				t.addGround(ground);			
				t.addTiles(tiles);
			}
		}
		
		player.addTiles(tiles);
		objects.add(player);
		
		initGL(); // init OpenGL
		getDelta(); // call once before loop to initialise lastFrame
		lastFPS = getTime(); // call before loop to initialise fps timer
 
		while (!Display.isCloseRequested()) {
			int delta = getDelta();
 
			update(delta);
			renderGL();
 
			Display.update();
			Display.sync(60); // cap fps to 60fps
		}
 
		Display.destroy();
	}
 
	public void update(int delta) {
		for(Tile t : tiles){
			t.update(delta);
		}
		
		for(Entity object : objects){
			object.update(delta);
		}
		
		
		

		updateFPS(); // update FPS Counter
	}
 
	/** 
	 * Calculate how many milliseconds have passed 
	 * since last frame.
	 * 
	 * @return milliseconds passed since last frame 
	 */
	public int getDelta() {
	    long time = getTime();
	    int delta = (int) (time - lastFrame);
	    lastFrame = time;
 
	    return delta;
	}
 
	/**
	 * Get the accurate system time
	 * 
	 * @return The system time in milliseconds
	 */
	public long getTime() {
	    return (Sys.getTime() * 1000) / Sys.getTimerResolution();
	}
 
	/**
	 * Calculate the FPS and set it in the title bar
	 */
	public void updateFPS() {
		if (getTime() - lastFPS > 1000) {
			Display.setTitle("FPS: " + fps);
			fps = 0;
			lastFPS += 1000;
		}
		fps++;
	}
 
	public void initGL() {
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0, 800, 0, 600, 1, -1);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
	}
 
	public void renderGL() {
		// Clear The Screen And The Depth Buffer
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
 
		// R,G,B,A Set The Color To Blue One Time Only

		GL11.glPushMatrix();
 
		//GL11.glEnable(GL11.GL_TEXTURE_2D);   
		for(Tile t : tiles){
			t.renderGL();
		}
		//GL11.glDisable(GL11.GL_TEXTURE_2D);
		
		for(Entity object : objects){
			object.renderGL();
		}

		GL11.glPopMatrix();
	}
 
	public static void main(String[] argv) {
		System.out.println("start");
		
		Game game = new Game();
		game.start();
	}
}
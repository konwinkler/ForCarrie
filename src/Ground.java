
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;


public class Ground implements Entity {

	int height = 60;
	
	@Override
	public void update(int delta) {
		// TODO Auto-generated method stub

	}

	@Override
	public void renderGL() {
		//R, G, B, A
		GL11.glColor3f(1.0f, 0.5f, 0.5f);
	//	Color.white.bind();
		GL11.glBegin(GL11.GL_QUADS);
			GL11.glVertex2f(0, 0);
			GL11.glVertex2f(Display.getWidth(), 0);
			GL11.glVertex2f(Display.getWidth(), height);
			GL11.glVertex2f(0, height);
		GL11.glEnd();	
	}

	public float getHeight() {
		return this.height;
	}

}

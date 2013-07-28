import java.util.List;
import java.util.Random;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.Texture;

public class Tile implements Entity {

	int width = 64, height = 64;
	float x = 0, y = 600;

	Tile top, bot, left, right;

	int id_x, id_y;

	float wait;

	float y_velocity = 0;
	Boolean grounded = false;

	Ground ground;
	List<Tile> tiles;

	boolean carry = false;
	Actor owner;
	private Texture texture;

	private boolean g_top = false, g_bot = false, g_left = false,
			g_right = false;
	private boolean goal = false;

	private int borderDist = 1;

	Tile(Random rand, int i, int j, Texture texture) {
		x = rand.nextInt(Display.getWidth() / width - 2) + 1;
		x = x * width + width / 2;

		id_x = i;
		id_y = j;

		this.texture = texture;

		// width = (int) (texture.getImageWidth() / 8);
		// height = (int) (texture.getImageHeight() / 8);

		wait = i * 5 + j;
	}

	@Override
	public void update(int delta) {
		if (!grounded && !carry) {
			if (wait < 0) {
				for (Tile t : tiles) {
					if ((this != t) && !t.isCarried() && (t.isGrounded())) {
						if (y_velocity < 0f) {
							// if(y < (t.getY()+t.getHeight()/2)){
							if (x == t.getX()) {
								if (this.y < (t.getY() + height)) {
									this.y_velocity = 0;
									this.y = t.getY() + height;
									grounded = true;
									bot = t;
									t.removeTop();
									t.addTop(this);
									this.checkGoal();
									break;
								}
								// }
							}
						}
					}

				}

				if ((ground != null) && (!grounded)) {

					if (y > ground.getHeight()) {
						this.y_velocity -= 0.002f * delta;
					} else if (this.y <= ground.getHeight()) {
						this.y_velocity = 0;
						this.y = ground.getHeight();
						grounded = true;
						this.checkGoal();
					}

					y += y_velocity * delta;
				}
			} else {
				wait -= 0.02 * delta;
			}
		}

		if (carry) {
			x = owner.getX();
			y = owner.getY() + owner.getHeight() / 2;
		}

	}

	public void addTop(Tile t) {
		if (top == null) {
			this.top = t;
			t.addBot(this);
			this.checkGoal();
		}
	}

	public void addBot(Tile t) {
		if (bot == null) {
			this.bot = t;
			t.addTop(this);
			this.checkGoal();
		}
	}

	public void removeTop() {
		if (top != null) {
			Tile tmp = top;
			top = null;
			tmp.removeTop();
			tmp.removeBot();
			// grounded = false;
			// wait = 1;
		}
	}

	public void removeBot() {
		if (bot != null) {
			Tile tmp = bot;
			bot = null;
			tmp.removeTop();
			tmp.removeBot();
			grounded = false;
			wait = 1;
		}
	}

	@Override
	public void renderGL() {
		// R, G, B, A
		// top
		if (g_top) {
			GL11.glColor3f(0f, 1f, 0f);
		} else {
			GL11.glColor3f(1f, 0f, 0f);
		}
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glVertex2f(x - width / 2, y + height - borderDist);
		GL11.glVertex2f(x + width / 2, y + height - borderDist);
		GL11.glVertex2f(x + width / 2, y + height);
		GL11.glVertex2f(x - width / 2, y + height);
		GL11.glEnd();
		// top
		if (g_bot) {
			GL11.glColor3f(0f, 1f, 0f);
		} else {
			GL11.glColor3f(1f, 0f, 0f);
		}
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glVertex2f(x - width / 2, y);
		GL11.glVertex2f(x + width / 2, y);
		GL11.glVertex2f(x + width / 2, y + borderDist);
		GL11.glVertex2f(x - width / 2, y + borderDist);
		GL11.glEnd();
		// top
		if (g_left) {
			GL11.glColor3f(0f, 1f, 0f);
		} else {
			GL11.glColor3f(1f, 0f, 0f);
		}
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glVertex2f(x - width / 2, y);
		GL11.glVertex2f(x - width / 2 + borderDist, y);
		GL11.glVertex2f(x - width / 2 + borderDist, y + height);
		GL11.glVertex2f(x - width / 2, y + height);
		GL11.glEnd();
		// top
		if (g_right) {
			GL11.glColor3f(0f, 1f, 0f);
		} else {
			GL11.glColor3f(1f, 0f, 0f);
		}
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glVertex2f(x + width / 2 - borderDist, y);
		GL11.glVertex2f(x + width / 2, y);
		GL11.glVertex2f(x + width / 2, y + height);
		GL11.glVertex2f(x + width / 2 - borderDist, y + height);
		GL11.glEnd();

		GL11.glColor3f(1f, 1f, 1f);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glBegin(GL11.GL_QUADS);
		texture.bind();
		GL11.glTexCoord2f((float) id_x / 5, (float) id_y / 5);
		// GL11.glTexCoord2f(0, 0);
		GL11.glVertex2f(x - width / 2 + borderDist, y + height - borderDist);
		// GL11.glTexCoord2f(0, (float)1/5);
		GL11.glTexCoord2f((float) (id_x) / 5, (float) (id_y + 1) / 5);
		GL11.glVertex2f(x - width / 2 + borderDist, y + borderDist);
		// GL11.glTexCoord2f((float)1/5, (float)1/5);
		GL11.glTexCoord2f((float) (id_x + 1) / 5, (float) (id_y + 1) / 5);
		GL11.glVertex2f(x + width / 2 - borderDist, y + borderDist);
		// GL11.glTexCoord2f((float)1/5, 0);
		GL11.glTexCoord2f((float) (id_x + 1) / 5, (float) id_y / 5);
		GL11.glVertex2f(x + width / 2 - borderDist, y + height - borderDist);
		GL11.glEnd();

		GL11.glDisable(GL11.GL_TEXTURE_2D);
		// R, G, B, A

	}

	public void findLeftRight() {
		left = null;
		right = null;

		for (Tile t : tiles) {
			if (t != this) {
				if (t.getY() == y) {
					if (t.getX() == (x + width)) {
						right = t;
						t.addLeft(this);
						t.checkLeft();
					}
					if (t.getX() == (x - width)) {
						left = t;
						t.addRight(this);
						t.checkRight();
					}
				}
			}
		}
	}

	private void addRight(Tile tile) {
		right = tile;

	}

	private void addLeft(Tile tile) {
		left = tile;

	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public void addGround(Ground ground) {
		this.ground = ground;
	}

	public Boolean isGrounded() {
		return grounded;
	}

	public void addTiles(List<Tile> tiles) {
		this.tiles = tiles;
	}

	public boolean isCarried() {
		return carry;
	}

	public int getHeight() {
		return height;
	}

	public int getWidth() {
		return width;
	}

	public void setCarry(Actor a) {
		if (a != null) {
			removeBot();
			removeTop();
			removeLeft();
			removeRight();
			carry = true;
			owner = a;
		} else {
			removeBot();
			removeTop();
			carry = false;
			a = null;
			y_velocity = 0.1f;
			grounded = false;
			wait = 1f;
		}

	}

	private void removeRight() {
		if (right != null) {
			Tile tmp = right;
			right = null;
			this.checkRight();
			tmp.removeLeft();
			tmp.checkLeft();
		}

	}

	private void removeLeft() {
		if (left != null) {
			Tile tmp = left;
			left = null;
			this.checkLeft();
			tmp.removeRight();
			tmp.checkRight();
		}

	}

	public int moveUp(float wait2) {
		grounded = false;
		y += height;
		y_velocity = 0.1f;
		wait = wait2;

		Tile tmp = top;
		top = null;
		bot = null;

		int r = 0;
		if (tmp != null) {
			r += tmp.moveUp(wait2 + 1);
		}

		return r + 2;
	}

	public void setX(float x2) {
		this.x = x2;
	}

	public void setY(float y2) {
		this.y = y2;
	}

	public int getIDX() {
		return id_x;
	}

	public int getIDY() {
		return id_y;
	}

	public boolean checkTop() {
		// first row
		g_top = false;
		if ((id_y == 0) && (top == null)) {
			g_top = true;
		} else if ((top != null) && (top.getIDX() == id_x)
				&& (top.getIDY() == (id_y - 1))) {
			g_top = true;
		}
		// System.out.print("TOP: ");
		// System.out.println(top);
		updateGoal();
		return g_top;
	}

	public boolean checkBot() {
		// bottom
		g_bot = false;
		if ((id_y == 4) && (bot == null)) {
			g_bot = true;
		} else if ((bot != null) && (bot.getIDX() == id_x)
				&& (bot.getIDY() == (id_y + 1))) {
			g_bot = true;
		}
		// System.out.print("BOT: ");
		// System.out.println(bot);
		updateGoal();
		return g_bot;
	}

	public boolean checkLeft() {
		g_left = false;
		if ((id_x == 0) && (left == null)) {
			g_left = true;
		} else if ((left != null) && (left.getIDY() == id_y)
				&& (left.getIDX() == (id_x - 1))) {
			g_left = true;
		}
		// System.out.print("Left: ");
		// System.out.println(left);
		updateGoal();
		return g_left;
	}

	public boolean checkRight() {
		g_right = false;
		if ((id_x == 4) && (right == null)) {
			g_right = true;
		} else if ((right != null) && (right.getIDY() == id_y)
				&& (right.getIDX() == (id_x + 1))) {
			g_right = true;
		}

		// System.out.print("Right: ");
		// System.out.println(right);
		updateGoal();
		return g_right;
	}

	public boolean checkGoal() {
		this.findLeftRight();

		checkTop();
		checkBot();
		checkLeft();
		checkRight();

		updateGoal();

		/*
		 * System.out.println("X: " + String.valueOf(id_x) + " Y: " +
		 * String.valueOf(id_y) + " left " + String.valueOf(g_left) + " right "
		 * + String.valueOf(g_right) + " top " + String.valueOf(g_top) + " bot "
		 * + String.valueOf(g_bot));
		 */
		checkAll();

		return goal;
	}

	private void updateGoal() {
		goal = g_top && g_bot && g_left && g_right;
	}

	private boolean checkAll() {
		boolean all = true;
		for (Tile t : tiles) {
			if (!t.getGoal()) {
				// System.out.print("False ");
				// System.out.println(t);
				return false;
			}
		}
		// You WIN!!!
		for (Tile t : tiles) {
			t.setBorderDist(0);
		}

		return all;
	}

	private void setBorderDist(int i) {
		this.borderDist = i;
	}

	private boolean getGoal() {
		return goal;
	}

	public String toString() {
		return "Tile " + String.valueOf(id_x) + " " + String.valueOf(id_y);
	}
}

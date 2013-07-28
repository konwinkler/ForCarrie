import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

public class Actor implements Entity {
	/** position of quad */
	float x = 400, y = 300;
	int width = 32, height = 32;
	double y_velocity = 0;

	Ground ground;
	boolean grounded = false;
	List<Tile> tiles;
	boolean collision = true;
	float collisionTime = 0;

	boolean holding;
	Tile holdingTile;

	float blockSpace = 0f;

	float fallingTime = 16f;
	float jumpStrength = 0.7f;

	public void update(int delta) {
		Boolean checkGrounded = false;

		if ((Keyboard.isKeyDown(Keyboard.KEY_UP)) && (grounded)) {
			this.y_velocity = jumpStrength;
			// System.out.println("jump");
			grounded = false;
		}
		if ((Keyboard.isKeyDown(Keyboard.KEY_DOWN)) && grounded) {
			if (y > ground.getHeight()) {
				collision = false;
				grounded = false;
				collisionTime = fallingTime;
				y_velocity = -0.3f;
			}
		}

		if ((Keyboard.isKeyDown(Keyboard.KEY_SPACE)) && grounded) {
			if (blockSpace <= 0) {
				blockSpace = 2f;
				// pick something up or drop it
				if (!holding) {
					// pick up
					for (Tile t : tiles) {
						if (t.isGrounded()) {
							if (y == t.getY()) {

								if (x >= (t.getX() - t.getWidth() / 2)) {
									if (x <= (t.getX() + t.getWidth() / 2)) {
										//System.out.println("pick up");
										holdingTile = t;
										holding = true;
										t.setCarry(this);
										checkGrounded = true;
										
										tiles.remove(t);
										tiles.add(t);

										// free tiles above t
										t.removeTop();
										t.removeBot();

										break;
									}
								}
							}
						}
					}

				} else {
					// drop it

					// find if there is a block
					boolean block = false;
					for (Tile t : tiles) {
						if (y == t.getY()) {
							if (x >= (t.getX() - t.getWidth() / 2)) {
								if (x <= (t.getX() + t.getWidth() / 2)) {
									// there is a block in front of it;
									//System.out.println("drop it");
									block = true;
									int r = t.moveUp(2f);
									blockSpace += r / 5;

									holdingTile.setX(t.getX());
									holdingTile.setY(y);
									holdingTile.setCarry(null);

									holding = false;
									holdingTile = null;

									break;
								}
							}
						}
					}
					if (block == false) {
						// nothing in front of it
						// find out x
						float tmp_x = (int) (x / holdingTile.getWidth());
						tmp_x = (tmp_x * holdingTile.getWidth())
								+ holdingTile.getWidth() / 2;
						holdingTile.setX(tmp_x);
/*						System.out.println(x);
						System.out.println(tmp_x);
						System.out.println(holdingTile.getWidth());
						System.out.println(holdingTile.getX());
*/
						// holdingTile.setY(y);
						holdingTile.setCarry(null);

						holding = false;
						holdingTile = null;
					}

				}
			} else {
				blockSpace -= 0.01f * delta;
			}
		}

		if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
			x -= 0.3f * delta;
			checkGrounded = true;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
			x += 0.3f * delta;
			checkGrounded = true;
		}

		if (!collision) {
			collisionTime -= 0.1f * delta;
			if (collisionTime <= 0f) {
				collision = true;
			}
		}

		if (checkGrounded && grounded) {
			Boolean newGrounded = false;
			if (this.y == ground.getHeight()) {
				newGrounded = true;
			} else {
				for (Tile t : tiles) {
					if ((y == t.getY()) && !t.isCarried()) {
						if ((x + width / 2) >= (t.getX() - t.getWidth() / 2)) {
							if ((x - width / 2) <= (t.getX() + t.getWidth() / 2)) {
								newGrounded = true;
								break;
							}
						}
					}
				}
			}
			grounded = newGrounded;
		}

		if (x < 0)
			x = 0;
		if (x > Display.getWidth())
			x = Display.getWidth();

		// falling
		if (!grounded) {
			if (collision) {
				for (Tile t : tiles) {
					if (t.isGrounded() && !t.isCarried()) {
						if (this.y_velocity < 0) {
							if (y > (t.getY() + t.getHeight() / 2)) {
								// if it touches butts
								if ((x + width / 2) >= (t.getX() - t.getWidth() / 2)) {
									if ((x - width / 2) <= (t.getX() + t
											.getWidth() / 2)) {
										if (this.y < (t.getY() + t.getHeight())) {
											this.y_velocity = 0;
											this.y = t.getY() + t.getHeight();
											grounded = true;
										}
									}
								}
							}
						}
					}
				}

				if (ground != null) {
					if (y > ground.getHeight()) {
						this.y_velocity -= 0.002f * delta;
					} else if (this.y < ground.getHeight()) {
						this.y_velocity = 0;
						this.y = ground.getHeight();
						grounded = true;
					}

				}
			}
			if (!grounded) {
				y += y_velocity * delta;
			}
		}
		// System.out.print("velo: ");
		// System.out.println(y_velocity);
		// System.out.print("delta: ");
		// System.out.println(delta);

	}

	public void renderGL() {
		// R, G, B, A
		GL11.glColor3f(0.5f, 0.5f, 1.0f);
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glVertex2f(x - width / 2, y);
		GL11.glVertex2f(x + width / 2, y);
		GL11.glVertex2f(x + width / 2, y + height);
		GL11.glVertex2f(x - width / 2, y + height);
		GL11.glEnd();
	}

	public void addGround(Ground ground) {
		this.ground = ground;
	}

	public void addTiles(List<Tile> tiles) {
		this.tiles = tiles;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public int getHeight() {
		return height;
	}

}

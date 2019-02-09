package fr.badblock.bukkit.hub.v2.utils.effects.effect;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.bukkit.Location;
import org.bukkit.util.Vector;

import fr.badblock.bukkit.hub.v2.utils.effects.Effect;
import fr.badblock.bukkit.hub.v2.utils.effects.EffectManager;
import fr.badblock.bukkit.hub.v2.utils.effects.EffectType;
import fr.badblock.bukkit.hub.v2.utils.effects.util.MathUtils;
import fr.badblock.bukkit.hub.v2.utils.effects.util.ParticleEffect;
import fr.badblock.bukkit.hub.v2.utils.effects.util.VectorUtils;

public class ImageEffect extends Effect {

	public enum Plane {

		X, XY, XYZ, XZ, Y, YZ, Z;
	}

	/**
	 * Turns the cube by this angle each iteration around the x-axis
	 */
	public double angularVelocityX = Math.PI / 200;

	/**
	 * Turns the cube by this angle each iteration around the y-axis
	 */
	public double angularVelocityY = Math.PI / 170;

	/**
	 * Turns the cube by this angle each iteration around the z-axis
	 */
	public double angularVelocityZ = Math.PI / 155;

	/**
	 * Should it rotate?
	 */
	public boolean enableRotation = true;

	/**
	 * For configuration-driven files
	 */
	public String fileName = null;

	/**
	 * File of the gif if needed
	 */
	protected File gifFile = null;

	/**
	 * Image as BufferedImage
	 */
	protected BufferedImage image = null;

	/**
	 * Invert the image
	 */
	public boolean invert = false;

	/**
	 * Is this a gif image?
	 */
	protected boolean isGif = false;

	/**
	 * Particle to draw the image
	 */
	public ParticleEffect particle = ParticleEffect.FLAME;

	/**
	 * What plane should it rotate?
	 */
	public Plane plane = Plane.XYZ;

	/**
	 * Scale the image down
	 */
	public float size = (float) 1 / 40;

	/**
	 * Step counter
	 */
	protected int step = 0;

	/**
	 * Each stepX pixel will be shown. Saves packets for high resolutions.
	 */
	public int stepX = 10;

	/**
	 * Each stepY pixel will be shown. Saves packets for high resolutions.
	 */
	public int stepY = 10;

	public ImageEffect(EffectManager effectManager) throws IOException {
		super(effectManager);
		type = EffectType.REPEATING;
		period = 10;
		iterations = 60;
	}

	private BufferedImage getImg(int s) throws IOException {
		ArrayList<BufferedImage> images = new ArrayList<BufferedImage>();
		ImageReader reader = ImageIO.getImageReadersBySuffix("GIF").next();
		ImageInputStream in = ImageIO.createImageInputStream(gifFile);
		reader.setInput(in);
		for (int i = 0, count = reader.getNumImages(true); i < count; i++) {
			BufferedImage image = reader.read(i);
			images.add(image);
		}
		if (step >= reader.getNumImages(true)) {
			step = 0;
			return images.get(s - 1);
		}
		return images.get(s);
	}

	public void loadFile(File file) {
		try {
			image = ImageIO.read(file);
			this.isGif = file.getName().endsWith(".gif");
			this.gifFile = file;
		} catch (Exception ex) {
			ex.printStackTrace();
			image = null;
		}
	}

	@Override
	public void onRun() {
		if (image == null && fileName != null) {
			loadFile(new File(fileName));
		}
		if (image == null) {
			cancel();
			return;
		}
		if (isGif) {
			try {
				image = getImg(step);
			} catch (IOException e) {
				e.printStackTrace();
			}
			step++;
		}
		Location location = getLocation();
		int clr;
		for (int y = 0; y < image.getHeight(); y += stepY) {
			for (int x = 0; x < image.getWidth(); x += stepX) {
				clr = image.getRGB(x, y);
				if (!invert && Color.black.getRGB() != clr) {
					continue;
				} else if (invert && Color.black.getRGB() == clr) {
					continue;
				}
				Vector v = new Vector((float) image.getWidth() / 2 - x, (float) image.getHeight() / 2 - y, 0)
						.multiply(size);
				VectorUtils.rotateAroundAxisY(v, -location.getYaw() * MathUtils.degreesToRadians);
				display(particle, location.add(v));
				location.subtract(v);
			}
		}
	}

}

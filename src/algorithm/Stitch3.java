package algorithm;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Stitch3 {
	
	private static final int MAX_PIXEL_DIFF = 20;
	
	private static final int SAMPLE_NUMBER = 50;
	
	private static final int MAX_COMMON_HEIGHT = 100;
	private static final int NEXT_COMMON_SPACE = 67;
	
	private static final int UPWARDS_BEST_DETECTION_HEIGHT = 100;

	private BufferedImage[] images;
	private int N;
	private int width;
	private int height;
	private BufferedImage output;
	private int outputHeight;
	
	public Stitch3(File[] imageFiles, String uploadPath) {
		this.N = imageFiles.length;
		if (N < 2) {
			throw new IllegalArgumentException("At least 2 images are needed here!");
		}
		
		try {
			images = new BufferedImage[N];
			for (int i = 0; i < N; i++) {
				images[i] = ImageIO.read(imageFiles[i]);
			}
			width = images[0].getWidth();
			height = images[0].getHeight();
			output = new BufferedImage(width, height * N, 1);
			outputHeight = 0;
			
			stitch();
			System.out.println("End of Stitching");
//			getOutput(uploadPath + File.separator + "output.png");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private void stitch() {
		outputHeight = 0;
				
		for (int i = 0; i < N; i++) {
			if (i == 0) {
				for (int y = 0; y < height; y++, outputHeight++) {
					setLine(output, y, getLine(images[0], y));
				}
			} else {
				int header = getHeaderHeight(images[i - 1], images[i]);
				int[] offsets = new int [SAMPLE_NUMBER];
				int[][] lines = new int[SAMPLE_NUMBER][];
				int matchHeight = -1, commonStartHeight = 0;
				while (matchHeight == -1 && 
						header + commonStartHeight + MAX_COMMON_HEIGHT < height) {
					for (int s = 0; s < offsets.length; s++) {
						offsets[s] = (int) (Math.random() * MAX_COMMON_HEIGHT);
						lines[s] = getLine(images[i], header + commonStartHeight + offsets[s]);
					}
					matchHeight = matchWithOutput(lines, offsets);
					if (matchHeight == -1) {
						commonStartHeight += NEXT_COMMON_SPACE;
					}
				}
					
				if (matchHeight == -1) {
					matchHeight = outputHeight;
					commonStartHeight = 0;
				}
				System.out.println("i = " + i + ", header = " + header + " -> " + matchHeight + " -> " + commonStartHeight);
				for (int h = header + commonStartHeight; h < height; h++) {
					setLine(output, matchHeight + h - header - commonStartHeight, getLine(images[i], h));
				}
				outputHeight = matchHeight + height - header - commonStartHeight;
			}
		}
	}
	
	public File getOutput(String filePath) {
		File f = new File(filePath);
		try {
			ImageIO.write(output.getSubimage(0, 0, width, outputHeight), "png", f);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return f;
	}
	
	private int matchWithOutput(int[][] lines, int[] offsets) {
		for (int i = outputHeight - MAX_COMMON_HEIGHT; i > outputHeight - height; i--) {
			boolean isMatch = true;
			for (int s = 0; s < offsets.length; s++) {
				isMatch = isMatch && isRoughlySame(lines[s], getLine(output, i + offsets[s]));
				if (!isMatch) {
					break;
				}
			}
			if (isMatch) {
				int best = i, bestDiff = Integer.MAX_VALUE;
				for (int j = best; j >= 0 && j + UPWARDS_BEST_DETECTION_HEIGHT > best; j--) {
					int diff = 0;
					for (int s = 0; s < offsets.length; s++) {
						diff += getDiff(lines[s], getLine(output, j + offsets[s]));
					}
					if (diff < bestDiff) {
						best = j;
						bestDiff = diff;
					}
				}
				return best;
			}
		}
//		return outputHeight - COMMON_START_HEIGHT;
		return -1;
	}
	
	private int getHeaderHeight(BufferedImage img1, BufferedImage img2) {
		int h = 0;
		for (; h < height; h++) {
			int[] line1 = getLine(img1, h);
			int[] line2 = getLine(img2, h);
			if (!isRoughlySame(line1, line2)) {
				break;
			}
		}
		return h;
	}
	
	private int getDiff(int[] rgbArray1, int[] rgbArray2) {
		int diffSum = 0;
		for (int i = 0; i < rgbArray1.length; i++) {
			diffSum += Math.abs((rgbArray1[i] & 0xFF) - (rgbArray2[i] & 0xFF));
		}
		return diffSum;
	}
	
	private boolean isRoughlySame(int[] rgbArray1, int[] rgbArray2) {
		int diffSum = getDiff(rgbArray1, rgbArray2);
		if (diffSum < rgbArray1.length * MAX_PIXEL_DIFF) {
			return true;
		} else {
			return false;
		}
	}
	
	private int[] getLine(BufferedImage img, int line) {
		int[] result = new int[img.getWidth()];
		for (int i = 0; i < result.length; i++) {
			result[i] = img.getRGB(i, line);
		}
		return result;
	}
	
	private void setLine(BufferedImage img, int line, int[] rgbArray) {
		for (int i = 0; i < rgbArray.length; i++) {
			img.setRGB(i, line, rgbArray[i]);
		}
	}
}

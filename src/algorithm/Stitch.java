package algorithm;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Stitch {
	
	private final File[] imageFiles;
	private BufferedImage resultImage;
	private String path;
	public Stitch(File[] files, String path) {
		this.imageFiles = files;
		this.path = path;
		stitch();
	}
	
	public File getResult() {
		String filePath = path + File.separator + "output.png";
		File f = new File(filePath);
		try {
			ImageIO.write(resultImage, "png", f);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return f;
	}
	
	private void stitch() {
		int N = imageFiles.length;
		try {
			BufferedImage[] images = new BufferedImage[N];
			for (int i = 0; i < N; i++) {
				images[i] = ImageIO.read(imageFiles[i]);
			}
			int width = images[0].getWidth();
			int height = images[0].getHeight();
			System.out.println(height + " x " + width);
			
			int[][] imagesHash = new int[N][height];
			for (int idx = 0; idx < N; idx++) {
				BufferedImage image = images[idx];
				for (int i = 0; i < height; i++) {
					for (int j = 0; j < width; j++) {
						int pixel = image.getRGB(j, i);
//						int blue = pixel & 0xFF;
						int blue = (pixel & 0xFF) < 200 ? 0 : 255;
						imagesHash[idx][i] = imagesHash[idx][i] * 13 + blue;
					}
					
				}
			}

			int[][] result = new int[N - 1][3];
			int[] split = new int[N];
			for (int i = 0; i < N - 1; i++) {
				result[i] = longestCommon(imagesHash[i], imagesHash[i+1]);
				if (i == 0 && i == N - 2) {
					split[0] = result[i][0] + result[i][2];
					split[1] = split[0] + height - (result[i][1] + result[i][2]);
				} else if (i == 0) {
					split[i] = result[i][0] + result[i][2];
				} else if (i < N - 2) {
					split[i] = split[i - 1] + 
							result[i][0] + result[i][2] - (result[i - 1][1] + result[i - 1][2]);	
				} else {
					split[i] = split[i - 1] + 
							result[i][0] + result[i][2] - (result[i - 1][1] + result[i - 1][2]);
					split[i + 1] = split[i] + height - (result[i][1] + result[i][2]);
				}
				System.out.println(result[i][0] + " - " + result[i][1] + " - " + result[i][2]);
			}
			
			BufferedImage image = new BufferedImage(width, split[N - 1], 1);
			int curr = 0;
			for (int i = 0; i < image.getHeight(); i++) {
				for (int j = 0; j < image.getWidth(); j++) {
					if (i < split[curr]) {
						if (curr == 0) {
							image.setRGB(j, i, images[curr].getRGB(j, i));
						} else {
							int currHeight = i - split[curr - 1];
							int startHeight = result[curr - 1][1] + result[curr - 1][2];
							image.setRGB(j, i, images[curr].getRGB(j, startHeight + currHeight));
						}
					} else {
						curr++;
					}
				}
			}
			resultImage = image;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private int[] longestCommon(int[] arr1, int[] arr2) {
		int M = arr1.length;
		int N = arr2.length;
		int[] result = new int[3];
		int[][] mat = new int[M][N];
		int left = 0, right1 = M - 1, right2 = N - 1;
		while (arr1[left] == arr2[left]) {
			left++;
		}
		while (arr1[right1] == arr2[right2]) {
			right1--;
			right2--;
		}
		for (int i = left; i < right1; i++) {
			for (int j = left; j < right2; j++) {
				if (i == left || j == left) {
					mat[i][j] = arr1[i] == arr2[i] ? 1 : 0;
				} else {
					if (arr1[i] == arr2[j]) {
						mat[i][j] = mat[i - 1][j - 1] + 1;
					} else {
						mat[i][j] = 0;
					}
				}
				if (mat[i][j] > result[2]) {
					result[0] = i - mat[i][j] + 1;
					result[1] = j - mat[i][j] + 1;
					result[2] = mat[i][j];
				}
			}
		}
		return result;
	}
}

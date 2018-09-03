package algorithm;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class VideoStitcher {

	private FFmpegFrameGrabber frameGrabber;
	private int frameNum;
	private int height;
	private int width;
	
	private int scanRegionStart;
	private static final int SCAN_REGION_HEIGHT = 100;
	
	private int fixedRegionStart;
	
	private BufferedImage output;
    private int outputHeight;
    
    private static final int MAX_PIXEL_DIFF = 20;
	private static final int UPWARDS_BEST_DETECTION_HEIGHT = 10;

    Java2DFrameConverter converter = new Java2DFrameConverter();
    
    public VideoStitcher(File videoFile) {
    	frameGrabber = new FFmpegFrameGrabber(videoFile);
    	startFrameGrabber();
    	frameNum = frameGrabber.getLengthInFrames();
        	height = frameGrabber.getImageHeight();
        	width = frameGrabber.getImageWidth();
               
        	output = new BufferedImage(width, height * 5, 1);
		outputHeight = 0;
        	
    	stitch();
    	closeFrameGrabber();
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
	
    private void stitch() {
        try {
        		        	
            	System.out.println("Totally " + frameNum + " frames");
        		BufferedImage firstImage = converter.getBufferedImage(frameGrabber.grabImage());
        		for (int i = 0; i < height; i++) {
        			setLineForOutput(i, getLine(firstImage, i));
        		}
        		
        		outputHeight = height;
            	scanRegionStart = 3 * height / 4 - SCAN_REGION_HEIGHT / 2;
            	fixedRegionStart = 3 * height / 4 - SCAN_REGION_HEIGHT / 2;

            	for (int count = 1; count < frameNum; count += 1) {  
            		BufferedImage currentImage = converter.getBufferedImage(frameGrabber.grabImage());
            		int commonRegionStart = findCommonRegion(currentImage, fixedRegionStart);            		
            		if (commonRegionStart == -1) {
            			continue;
            		}
            		
            		for (int l = commonRegionStart + SCAN_REGION_HEIGHT; l < height; l++) {
            			setLineForOutput(scanRegionStart + l - commonRegionStart, getLine(currentImage, l));
            		}
            		
            		scanRegionStart += (fixedRegionStart - commonRegionStart);
            		outputHeight += (fixedRegionStart - commonRegionStart);
            	}            	
            frameGrabber.stop();
        } catch (Exception e) {
        		e.printStackTrace();
        }
    }
    
    private int findCommonRegion(BufferedImage img, int imgLineStart) {
    	for (int i = imgLineStart; i >= 0; i--) {
			boolean isMatch = true;
    		for (int l = 0; l < SCAN_REGION_HEIGHT; l++) {
    			isMatch = isMatch && isRoughlySame(
    								  getLine(output, scanRegionStart + l), 
    								  getLine(img, i + l));
				if (!isMatch) {
					break;
				}    		
			}
    		if (isMatch) {
    			int best = i, bestDiff = Integer.MAX_VALUE;
				for (int j = best; j >= 0 && j + UPWARDS_BEST_DETECTION_HEIGHT > best; j--) {
					int diff = 0;
					for (int l = 0; l < SCAN_REGION_HEIGHT; l++) {
						diff += getDiff(getLine(output, scanRegionStart + l), getLine(img, j + l));
					}
					if (diff < bestDiff) {
						best = j;
						bestDiff = diff;
					}
				}
				return best;
//    			return i;
    		}
    	}
    	return -1;
    }
    
    /**
     * Set Line For Output BufferedImage
     * @param img
     * @param height
     * @param width
     * @return
     */
    private void setLineForOutput(int line, int[] rgbArray) {
    	if (line >= output.getHeight()) {
    		System.out.println("Doubling the height");
        		BufferedImage oldOutput = output;
            output = new BufferedImage(width, 2 * oldOutput.getHeight(), 1);
            	for (int i = 0; i < oldOutput.getHeight(); i++) {
            		setLine(output, i, getLine(oldOutput, i));
            	}
    	}
    	setLine(output, line, rgbArray);
    }
    
    
    /**
     * Get a line from a BufferedImage
     * @param img
     * @param line
     * @return
     */
	private int[] getLine(BufferedImage img, int line) {
		int[] result = new int[img.getWidth()];
		for (int i = 0; i < result.length; i++) {
			result[i] = img.getRGB(i, line);
		}
		return result;
	}
	
	/**
	 * Set a line for a BufferedImage
	 * @param img
	 * @param line
	 * @param rgbArray
	 */
	private void setLine(BufferedImage img, int line, int[] rgbArray) {
		for (int i = 0; i < rgbArray.length; i++) {
			img.setRGB(i, line, rgbArray[i]);
		}
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
    
   /**
    * Start the frame grabber 
    */
    private void startFrameGrabber() {
    	try {
    		frameGrabber.start();
        } catch (Exception e) {
            System.err.println("Failed start the grabber.");
            e.printStackTrace();
        }
    }
    
    /**
     * Close the frame grabber 
     */
    private void closeFrameGrabber() {
    	try {
			frameGrabber.close();
		} catch (Exception e) {
            System.err.println("Failed close the grabber.");
            	e.printStackTrace();
		}
    }
}

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

/**
 * A class to store an image of the map and methods to overlay coloured data blocks on it.
 *
 * @author Anton Davidouski
 * @version 1.0
 */

public class MapImage {
    private Image baseImage;
    private WritableImage colourImage;
    private double blendAplha = 0.5;
    private int[] bounds;

    private static final HashMap<String, int[]> CITY_BOUNDARIES = City.getCitiesBoundaries();

    public MapImage(String city, String fileName) {
        if (city.equals("N/A")) {
            // maps like the tube map don't have city boundaries, and they are not used anyway, so just set to 0
            bounds = new int[]{0, 0, 0, 0};
        } else{
            bounds = CITY_BOUNDARIES.get(city);
        }
        try {
            FileInputStream input = new FileInputStream(fileName);
            baseImage = new Image(input);

            // make a duplicate blank image with same dimensions as base image
            colourImage = new WritableImage(
                    (int)baseImage.getWidth(),
                    (int)baseImage.getHeight()
            );

            PixelWriter writer = colourImage.getPixelWriter();
            int blankArgb = 0xFF << 24; // ARGB is a 32-bit integer, with 8 bits for each of the four components (alpha, r, g, b), so bitshift FF by 24 to set alpha to full and set the rest to 0
            for (int y = 0; y < baseImage.getHeight(); y++) {
                for (int x = 0; x < baseImage.getWidth(); x++) {
                    writer.setArgb(x, y, blankArgb);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error loading image: " + e.getMessage());
        }
    }

    /**
     * Returns the base image, no coloured overlay.
     *
     * @return the Image representing the base map image
     */
    public Image getImage() {
        return baseImage;
    }

    /**
     * Takes a single data point, figures out where it should be placed on the map,
     * figures out what colour it should be, and places a block of that colour on the overlay image.
     *
     * @param dataPoint The data point to be placed on the map
     * @param min The minimum value of the data set
     * @param max The maximum value of the data set
     * @param ratio The ratio to scale the size of the block by, since different maps have different sizes
     */
    public void processDataPoint(DataPoint dataPoint, double min, double max, int ratio) {
        double dataPercentage = (dataPoint.value() - min) / (max - min);
        int x = dataPoint.x();
        int y = dataPoint.y();

        // convert real world coordinates to image pixel coordinates. Finds how far along the image the point is percentage wise,
        // multiplied by the image dimensions to get the pixel location.
        int imageX = (int) Math.round((colourImage.getWidth() * (x - bounds[0]) / (bounds[1] - bounds[0])));
        int imageY = (int) Math.round((colourImage.getHeight() * (y - bounds[3]) / (bounds[2] - bounds[3])));

        // scale the block size by the ratio, so any sized map is filled by the blocks
        int width = 43*ratio;
        int height = 45*ratio;

        //offset the image coordinates so the block is centered on the point
        imageX -= ((width - 1)/ 2);
        imageY -= ((height - 1) / 2);
        placeOverlayBlock(imageX, imageY, width, height, dataPercentage);
    }

    /**
     * Place a colour block on the overlay colour image at full opacity.
     *
     * @param startX The x-coordinate of the top-left corner of the block
     * @param startY The y-coordinate of the top-left corner of the block
     * @param width The width of the block
     * @param height The height of the block
     * @param dataPercentage The data points location in the colour spectrum as a percentage relative
     *                       to min and max values. Used tio determine the colour of the block.
     */
    private void placeOverlayBlock(int startX, int startY, int width, int height, double dataPercentage) {
        PixelWriter writer = colourImage.getPixelWriter();
        int alpha = 255; // always full opacity
        int red, green, blue;

        // Depending on the data percentage, set the colour of the block.
        if (dataPercentage <= 0.25) {
            // green to yellow region, slowly increasing red
            red = (int) (255 * (dataPercentage / 0.25));
            green = 255;
        } else {
            // yellow to red region, slowly decreasing green
            red = 255;
            green = (int) (255 * (((-4/3) * dataPercentage) + 4/3)); // straight line equation for how to reduce green.
            // there is a visualisation of this in the report
        }

        // no blue component
        blue = 0;
        int argb = (alpha << 24) | (red << 16) | (green << 8) | blue; // combine into one argb int

        // set right range of pixels to the colour needed.
        for (int y = startY; y < startY + height; y++) {
            for (int x = startX; x < startX + width; x++) {
                int clampedX = Math.min((int) colourImage.getWidth() - 1, Math.max(0, x));
                int clampedY = Math.min((int) colourImage.getHeight() - 1, Math.max(0, y));
                writer.setArgb(clampedX, clampedY, argb);
            }
        }
    }

    /**
     * Overlay the colour image on top of the base image, using a blend ratio
     *
     * @return The combined image which can be added to the IMageView
     */
    public Image getCombined() {

        // create a new image to hold the combined image, with the same dimensions as the base and colour images
        WritableImage newImage = new WritableImage((int) baseImage.getWidth(), (int) baseImage.getHeight());
        int width = (int) Math.min(baseImage.getWidth(), colourImage.getWidth());
        int height = (int) Math.min(baseImage.getHeight(), colourImage.getHeight());

        PixelReader overlayReader = colourImage.getPixelReader();
        PixelReader baseReader = baseImage.getPixelReader();
        PixelWriter writer = newImage.getPixelWriter();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int baseArgb = baseReader.getArgb(x, y);
                int overlayArgb = overlayReader.getArgb(x, y);

                // Extract all components of argb values of base and overlay images using bit shifting and masking
                int baseRed = (baseArgb >> 16) & 0xFF;
                int baseGreen = (baseArgb >> 8) & 0xFF;
                int baseBlue = baseArgb & 0xFF;

                int overlayRed = (overlayArgb >> 16) & 0xFF;
                int overlayGreen = (overlayArgb >> 8) & 0xFF;
                int overlayBlue = overlayArgb & 0xFF;

                // Blend the two images together using the blend ratio. Cast back to int.
                int newRed = (int) (baseRed * (1 - blendAplha) + overlayRed * blendAplha);
                int newGreen = (int) (baseGreen * (1 - blendAplha) + overlayGreen * blendAplha);
                int newBlue = (int) (baseBlue * (1 - blendAplha) + overlayBlue * blendAplha);

                // check new values in correct range
                newRed = Math.min(255, Math.max(0, newRed));
                newGreen = Math.min(255, Math.max(0, newGreen));
                newBlue = Math.min(255, Math.max(0, newBlue));

                // combine into a single int and set to new image
                int newArgb = (0xFF << 24) | (newRed << 16) | (newGreen << 8) | newBlue;
                writer.setArgb(x, y, newArgb);
            }
        }
        return newImage;
    }

    /**
     * Apply the two blur functions to the image.
     * @param radius radius of the blur
     */
    public void applyBlur(int radius) {
        WritableImage horizontalBlur = applyHorizontalBlur(radius);
        WritableImage verticalBlur = applyVerticalBlur(horizontalBlur, radius);
        colourImage = verticalBlur;
    }

    /**
     * Apply block blur in the horizontal direction.
     *
     * @param radius radius of the blur
     * @return the image with the horizontal blur applied
     */
    private WritableImage applyHorizontalBlur(int radius) {
        int width = (int) colourImage.getWidth();
        int height = (int) colourImage.getHeight();
        WritableImage result = new WritableImage(width, height); // create a new image to hold the result
        PixelReader reader = colourImage.getPixelReader();
        PixelWriter writer = result.getPixelWriter();

        for (int y = 0; y < height; y++) {
            // sliding window to calculate average
            int[] cumulativeRed = new int[width];
            int[] cumulativeGreen = new int[width];

            int currentRed = 0;
            int currentGreen = 0;

            for (int x = 0; x < width; x++) {
                // calculate cumulative sums so can get sliding window sum faster by doing difference of array indexes
                int argb = reader.getArgb(x, y);
                int r = (argb >> 16) & 0xFF;
                int g = (argb >> 8) & 0xFF;

                currentRed += r;
                currentGreen += g;

                cumulativeRed[x] = currentRed;
                cumulativeGreen[x] = currentGreen;
            }

            for (int x = 0; x < width; x++) {
                // apply sliding window
                int startX = Math.max(0, x - radius); // make sure start and end not outside image bounds
                int endX = Math.min(width - 1, x + radius);
                int windowSize = endX - startX + 1;

                int startSumR, startSumG;

                if (startX > 0) {
                    startSumR = cumulativeRed[startX - 1];
                    startSumG = cumulativeGreen[startX - 1];
                } else {
                    startSumR = 0;
                    startSumG = 0;
                }

                int sumR = cumulativeRed[endX] - startSumR;
                int sumG = cumulativeGreen[endX] - startSumG; // find sums within window

                int avgR = (sumR / windowSize);
                int avgG = (sumG / windowSize); // find averages

                writer.setArgb(x, y, (255 << 24) | (avgR << 16) | (avgG << 8) | 0); // set pixel to average
            }
        }
        return result;
    }

    /**
     * Apply block blur in the vertical direction. Logic for horizontal and vertical is the same, see comments in horizontal
     *
     * @param radius radius of the blur
     * @return the image with the vertical blur applied
     */
    private WritableImage applyVerticalBlur(WritableImage source, int radius) {
        int width = (int) source.getWidth();
        int height = (int) source.getHeight();
        WritableImage result = new WritableImage(width, height);
        PixelReader reader = source.getPixelReader();
        PixelWriter writer = result.getPixelWriter();

        for (int x = 0; x < width; x++) {
            int[] cumulativeRed = new int[height];
            int[] cumulativeGreen = new int[height];

            int currentRed = 0, currentGreen = 0;
            for (int y = 0; y < height; y++) {
                int argb = reader.getArgb(x, y);
                int r = (argb >> 16) & 0xFF;
                int g = (argb >> 8) & 0xFF;

                currentRed += r;
                currentGreen += g;

                cumulativeRed[y] = currentRed;
                cumulativeGreen[y] = currentGreen;
            }

            for (int y = 0; y < height; y++) {
                int startY = Math.max(0, y - radius);
                int endY = Math.min(height - 1, y + radius);
                int windowSize = endY - startY + 1;

                int startSumR, startSumG;

                if (startY > 0) {
                    startSumR = cumulativeRed[startY - 1];
                    startSumG = cumulativeGreen[startY - 1];
                } else {
                    startSumR = 0;
                    startSumG = 0;
                }

                int sumR = cumulativeRed[endY] - startSumR;
                int sumG = cumulativeGreen[endY] - startSumG;

                int avgR = (sumR / windowSize);
                int avgG = (sumG / windowSize);

                writer.setArgb(x, y, (255 << 24) | (avgR << 16) | (avgG << 8) | 0);
            }
        }
        return result;
    }

    /**
     * Reset the overlay image to be fully transparent.
     */
    public void resetOverlay() {
        PixelWriter writer = colourImage.getPixelWriter();
        int blankArgb = 0xFF << 24; // Fully transparent (ARGB)
    
        for (int y = 0; y < colourImage.getHeight(); y++) {
            for (int x = 0; x < colourImage.getWidth(); x++) {
                writer.setArgb(x, y, blankArgb);
            }
        }
    }
}


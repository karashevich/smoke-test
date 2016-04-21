package automation.util;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by jetbrains on 30/03/16.
 */
public class PngUtils {


    public final static String img_file = "/Applications/IntelliJ IDEA 2016.2 EAP.app/Contents/bin/regionCapture.png";
    public final static int main_menu_height = 20;

    private static BufferedImage loadImage() throws IOException {
        File img = new File(img_file);
        return ImageIO.read(img);
    }

    public static BufferedImage loadImage(String path) throws IOException {
        File img = new File(path);
        return ImageIO.read(img);
    }

    public static BufferedImage getBWImage(BufferedImage master) {
        BufferedImage blackWhite = new BufferedImage(master.getWidth(), master.getHeight(), BufferedImage.TYPE_BYTE_BINARY);
        Graphics2D g2d = blackWhite.createGraphics();
        g2d.drawImage(master, 0, 0, null);
        g2d.dispose();
        return blackWhite;
    }

    public static int[] getLineFromBWImage(BufferedImage bwImage) throws Exception {
        WritableRaster raster = bwImage.getRaster();
        return getLineFromBWRaster(raster);
    }

    public static int[] getLineFromBWRaster(Raster raster) throws Exception {

        int[] line = new int[raster.getWidth()];
        for (int i = 0; i < raster.getWidth(); i++) {
            line[i] = 1;
            for (int j = 0; j < raster.getHeight(); j++) {
                try {
                    if (raster.getSample(i + raster.getMinX(), j + raster.getMinY(), 0) == 0) {
                        line[i] = 0;
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    System.err.println("i: " + i + "; j: " + j);
                    e.printStackTrace();
                    throw new Exception(e);
                }
            }
        }

        return line;
    }

    public static BufferedImage buildFromLine(int[] line, BufferedImage bwImage) {
        WritableRaster raster = bwImage.getRaster();
        WritableRaster rasterLined = WritableRaster.createWritableRaster(bwImage.getSampleModel(), new Point(0, 0));
        int[] white = new int[1];
        white[0] = 1;
        int[] black = new int[1];
        black[0] = 0;

        for (int i = 0; i < raster.getWidth(); i++) {
            for (int j = 0; j < raster.getHeight(); j++) {
                if (line[i] == 1) {
                    rasterLined.setPixel(i, j, white);
                } else {
                    rasterLined.setPixel(i, j, black);
                }
            }
        }
        ColorModel colorModel = bwImage.getColorModel();
        return new BufferedImage(colorModel, rasterLined, false, null);
    }

    public static int[] convolution(final int[] line) {
        final int threshold = 5;
        int[] convolute = Arrays.copyOf(line, line.length);

        for (int i = 0; i < convolute.length - threshold; i++) {
            if (convolute[i] == 0) {
                boolean whitespace = false;
                for (int k = 1; k < threshold; k++) {
                    if (convolute[i + k] == 1 && !whitespace) {
                        whitespace = true;
                    } else if (convolute[i + k] == 1 && whitespace) {
                        //do nothing
                    } else if (convolute[i + k] == 0 && whitespace) {
                        for (int z = 1; z < k; z++) {
                            convolute[i + z] = 0;
                        }
                        i = i + k - 1;
                        k = threshold;
                    }
                }
            }
        }

        return convolute;
    }


    public static int[] buildIntersections(int[] line) {
        ArrayList<Integer> diff = new ArrayList<>();

        boolean blackblock = false;
        for (int i = 0; i < line.length; i++) {
            if (line[i] == 0 && !blackblock) {
                diff.add(i);
                blackblock = true;
            } else if (line[i] == 1 && blackblock) {
                diff.add(i - 1);
                blackblock = false;
            }
        }
        int[] ret = new int[diff.size()];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = diff.get(i);
        }
        return ret;
    }


    public static int[] buildIntersectionsAlter(int[] line) {
        ArrayList<Integer> diff = new ArrayList<>();

        boolean blackblock = false;
        for (int i = 0; i < line.length; i++) {
            if (line[i] != 0 && !blackblock) {
                diff.add(i);
                blackblock = true;
            } else if (line[i] == 0 && blackblock) {
                diff.add(i - 1);
                blackblock = false;
            }
        }
        int[] ret = new int[diff.size()];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = diff.get(i);
        }
        return ret;
    }


    public static Point getMenuCenter(int menuIndex) throws Exception {
        return getMenuCenter(loadImage(), menuIndex);
    }

    public static Point getMenuCenter(BufferedImage regionCapture, int menuIndex) throws Exception {
        BufferedImage bwImage = getBWImage(regionCapture);
        int[] lineFromBWImage = getLineFromBWImage(bwImage);
        int[] convolutedLine = convolution(lineFromBWImage);
        int[] intersections = buildIntersections(convolutedLine);

        return new Point((intersections[2 * menuIndex + 1] - intersections[2 * menuIndex]) / 2 + intersections[2 * menuIndex], bwImage.getHeight() / 2);
    }

    public static void drawCenterCross(int menuIndex, Point basePoint, Graphics g) throws Exception {
        int crossLength = 5;
        Color crossColor = Color.RED;
        Color baseColor = g.getColor();
        g.setColor(crossColor);

        Point menuCenter = getMenuCenter(menuIndex);
        //horizontal line
        g.drawLine(menuCenter.x - crossLength / 2 + basePoint.x, menuCenter.y + basePoint.y,
                menuCenter.x + crossLength / 2 + basePoint.x, menuCenter.y + basePoint.y);
        //vertical line
        g.drawLine(menuCenter.x + basePoint.x, menuCenter.y - crossLength / 2 + basePoint.y,
                menuCenter.x + basePoint.x, menuCenter.y + crossLength / 2 + basePoint.y);
        g.setColor(baseColor);
    }

    public static void drawCenterCross(Point absolute, Graphics g) throws Exception {
        int crossLength = 5;
        Color crossColor = Color.RED;
        Color baseColor = g.getColor();
        g.setColor(crossColor);

        //horizontal line
        g.drawLine(absolute.x - crossLength / 2 , absolute.y ,
                absolute.x + crossLength / 2 , absolute.y );
        //vertical line
        g.drawLine(absolute.x, absolute.y - crossLength / 2 ,
                absolute.x, absolute.y + crossLength / 2);
        g.setColor(baseColor);
    }


    public static void mainOld(String[] args) throws IOException {
//        final BufferedImage lineImg = getWordsBlocksFromPng(loadImage());
        String[] labels = {"mac", "Main", "File", "Edit", "View", "Navigate", "Code", "Analyze", "Refactor", "Build", "Run", "Tools", "VCS", "Window", "Help"};

        BufferedImage myImage = loadImage();

        JFrame jframe = new JFrame("Test");
        JPanel jPanel = new JPanel() {
            @Override
            public void paint(Graphics g) {
                super.paint(g);
//                g.drawImage(myImage, 0, 0, null);
//                BufferedImage im = new BufferedImage(myImage.getWidth(), myImage.getHeight(), BufferedImage.TYPE_BYTE_BINARY);
//                Graphics2D g2d = im.createGraphics();
//                g2d.drawImage(myImage, 0, 0, null);
//                g.drawImage(im, 0, main_menu_height + 1, myImage.getWidth(), main_menu_height + 2, 0, main_menu_height/2, myImage.getWidth(), main_menu_height/2 + 1, null);
                try {
                    g.drawImage(loadImage(), 0, 0, null);
                    BufferedImage bwImage = getBWImage(loadImage());
                    int[] lineFromBWImage = getLineFromBWImage(bwImage);
                    int[] convolutedLine = convolution(lineFromBWImage);
                    int[] intersections = buildIntersections(convolutedLine);

                    g.drawImage(bwImage, 0, myImage.getHeight() + 1, null);
                    g.drawImage(buildFromLine(lineFromBWImage, bwImage), 0, 2 * myImage.getHeight() + 1, null);
                    g.drawImage(buildFromLine(convolutedLine, bwImage), 0, 3 * myImage.getHeight() + 1, null);
                    for (int i = 0; i < labels.length; i++) {
                        int x = intersections[2 * i];
                        int y = 5 * myImage.getHeight() + 1;
                        g.setFont(jframe.getFont().deriveFont(Font.BOLD));
                        g.drawChars(labels[i].toCharArray(), 0, labels[i].length(), x, y);
                        Point basePoint = new Point(0, 0);
                        drawCenterCross(i, basePoint, g);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        jPanel.setPreferredSize(new Dimension(myImage.getWidth(), 5 * myImage.getHeight()));

        jframe.add(jPanel);
        jframe.pack();
        jframe.setVisible(true);
    }

    public static Rectangle diffImg(BufferedImage before, BufferedImage after, BufferedImage diff) {
        WritableRaster rasteredImage = WritableRaster.createWritableRaster(before.getSampleModel(), new Point(0, 0));

        SampleModel beforeSampleModel = before.getSampleModel();
        SampleModel afterSampleModel = after.getSampleModel();

        int[] white = new int[1];
        white[0] = 1;
        int[] black = new int[1];
        black[0] = 0;

        int x0 = -1;
        int y0 = -1;

        int x1 = -1;
        int y1 = -1;


        for (int xi = 0; xi < before.getWidth(); xi++) {
            for (int yi = 0; yi < before.getHeight(); yi++) {
                if (before.getRGB(xi, yi) != after.getRGB(xi, yi)) {
                    int[] color = new int[1];
                    if (after.getRGB(xi, yi) == -1) color[0] = 1;
                    else color[0] = 0;
                    rasteredImage.setPixel(xi, yi, color);
                    if (x0 == -1) {
                        x0 = xi;
                    }
                    if (y0 == -1) {
                        y0 = yi;
                    } else {
                        y0 = Math.min(yi, y0);
                    }
                    x1 = xi;
                    if (y1 == -1) {
                        y1 = yi;
                    } else {
                        y1 = Math.max(yi, y1);
                    }
                } else {
                    rasteredImage.setPixel(xi, yi, white);
                }
            }
        }

        ColorModel colorModel = before.getColorModel();
        diff.setData(rasteredImage);
        return new Rectangle(x0, y0, x1 - x0, y1 - y0);

    }

    public static Point getSubmenuItemPoint(BufferedImage screenshotBefore, BufferedImage screenshotAfter, int menuIndex) throws Exception {
        BufferedImage before = getBWImage(screenshotBefore);
        BufferedImage after = getBWImage(screenshotAfter);

        File outputfileBefore = new File("before.png");
        ImageIO.write(before, "png", outputfileBefore);
        File outputfileAfter = new File("after.png");
        ImageIO.write(after, "png", outputfileAfter);
        BufferedImage diff = ImageIO.read(outputfileAfter);

//        BufferedImage diff = deepCopy(screenshotBefore);
        diffImg(before, after, diff);
        File outputfileDiff = new File("diff.png");
        ImageIO.write(diff, "png", outputfileDiff);

        File outputfileBefore2 = new File("befor" + menuIndex + "_" + System.currentTimeMillis() + ".png");
        ImageIO.write(before, "png", outputfileBefore2);
        File outputfileAfter2 = new File("after" + menuIndex + "_" + System.currentTimeMillis() + ".png");
        ImageIO.write(after, "png", outputfileAfter2);
        BufferedImage diff2 = ImageIO.read(outputfileAfter2);
        File outputfileDiff2 = new File("diff" + menuIndex + "_" + System.currentTimeMillis() + ".png");
        ImageIO.write(diff, "png", outputfileDiff2);

        int[] color = new int[1];
        int[] verticalLine = new int[diff.getHeight()];
        for (int i = 0; i < diff.getHeight(); i++) {
            //ignore mainMenuPanel
            if (i <= 20) verticalLine[i] = 0;
            else {
                for (int j = 0; j < diff.getWidth(); j++) {
                    if (diff.getRGB(j, i) != -1) verticalLine[i] += 1;
                }
            }
        }
        //remove noise
        final int noiseThreshold = 5;
        for (int i = 0; i < verticalLine.length; i++) {
            if (verticalLine[i] < noiseThreshold) verticalLine[i] = 0;
        }

        int[] intersections = buildIntersectionsAlter(verticalLine);

        //REMOVE SMALL GROUPS
        ArrayList<Integer> cleanGroups = new ArrayList<>();
        final int groupThreshold = 4;
        for (int i = 0; i < intersections.length / 2; i++) {
            if (intersections[2 * i + 1] - intersections[2 * i] >= groupThreshold) {
                cleanGroups.add(intersections[2 * i]);
                cleanGroups.add(intersections[2 * i + 1]);
            }
        }


        int y0 = cleanGroups.get(2 * menuIndex);
        int y1 = cleanGroups.get(2 * menuIndex + 1);

        final Raster data = diff.getData(new Rectangle(0, y0, diff.getWidth(), y1 - y0));
        final int[] lineFromBWRaster = getLineFromBWRaster(data);
        final int[] convoluctedLine = convolution(getLineFromBWRaster(data));
        final int[] groups = buildIntersections(convoluctedLine);
        //getMaxGroup
        int x0 = -1;
        int x1 = -1;

        for (int i = 0; i < groups.length / 2; i++) {
            if (x0 == -1) {
                x0 = groups[2 * i];
                x1 = groups[2 * i + 1];
            } else {
                if (groups[2 * i + 1] - groups[2 * i] > x1 - x0) {
                    x0 = groups[2 * i];
                    x1 = groups[2 * i + 1];
                }
            }
        }

        return new Point(x0 + (x1 - x0) / 2, y0 + (y1 - y0) / 2);
    }

    static BufferedImage deepCopy(BufferedImage bi) {
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }

    public static void main(String[] args) throws Exception {
        BufferedImage myImageAfter = loadImage("/Users/jetbrains/Development/after.png");
        BufferedImage after = getBWImage(myImageAfter);

        BufferedImage myImageBefore = loadImage("/Users/jetbrains/Development/before.png");
        BufferedImage before = getBWImage(myImageBefore);

        BufferedImage diff = getBWImage(loadImage("/Users/jetbrains/Development/after.png"));
        diffImg(before, after, diff);

        final Point p = getSubmenuItemPoint(myImageBefore, myImageAfter, 0);

        JFrame jframe = new JFrame("Test");
        JPanel jPanel = new JPanel() {
            @Override
            public void paint(Graphics g) {
                super.paint(g);
                try {
                    Rectangle rectangle = diffImg(before, after, diff);
                    g.drawImage(diff, 0, 0, null);
                    g.setColor(Color.RED);
                    g.drawRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
                    drawCenterCross(p, g);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        jPanel.setPreferredSize(new Dimension(before.getWidth(), before.getHeight()));

        jframe.add(jPanel);
        jframe.pack();
        jframe.setVisible(true);
    }
}

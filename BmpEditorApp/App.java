import Filters.*;
import Models.BmpHeader;
import Models.BmpImage;
import Models.Pixel;
import io.BmpReader;
import io.BmpWriter;
import Kernels.Kernel;
import Kernels.Kernels;
import imageProcessors.imageProcessor;

import java.io.IOException;
import java.util.*;

class FilterInfo {
    int id;
    String name;
    Object filterObject;
    String type; // "Filter", "HSV", "Convolution", "Pixelate", "Sequence", "SobelEdge"

    FilterInfo(int id, String name, Object filterObject, String type) {
        this.id = id;
        this.name = name;
        this.filterObject = filterObject;
        this.type = type;
    }
}

class Command {
    int filterId;
    int strength;

    Command(int filterId, int strength) {
        this.filterId = filterId;
        this.strength = strength;
    }
}

public class App {


        static String filepath = "/home/sharun/Downloads/test3.bmp";
        private static final Map<Integer, FilterInfo> availableFilters = new LinkedHashMap<>();
        // Instantiate filters needed globally or within sequences
        private static final Filters greyscale = new greyScale();
        private static final Filters posterize = new posterize();
        private static final Filters threshold = new threshold();
        private static final Filters negative = new negative();
        private static final Filters darken = new darken();
        private static final Filters brightness = new brightness();
        private static final Filters solarize = new solarize();
        private static final Filters swap = new swap();
        private static final Filters red = new red();
        private static final Filters blue = new blue();
        private static final HSVfilters saturation = new saturation();
        private static final HSVfilters hue = new hue();
        private static final HSVfilters value = new value();


        private static void initializeFilters () {
        availableFilters.clear();
        int id = 1;
        // Point Filters
        availableFilters.put(id++, new FilterInfo(id - 1, "Greyscale", greyscale, "Filter"));
        availableFilters.put(id++, new FilterInfo(id - 1, "Posterize", posterize, "Filter"));
        availableFilters.put(id++, new FilterInfo(id - 1, "Threshold", threshold, "Filter"));
        availableFilters.put(id++, new FilterInfo(id - 1, "Negative (Invert)", negative, "Filter"));
        availableFilters.put(id++, new FilterInfo(id - 1, "Darken", darken, "Filter"));
        availableFilters.put(id++, new FilterInfo(id - 1, "Brighten", brightness, "Filter"));
        availableFilters.put(id++, new FilterInfo(id - 1, "Solarize", solarize, "Filter"));
        availableFilters.put(id++, new FilterInfo(id - 1, "Swap Channels", swap, "Filter"));
        availableFilters.put(id++, new FilterInfo(id - 1, "Red Filter Adjust", red, "Filter"));
        availableFilters.put(id++, new FilterInfo(id - 1, "Blue Filter Adjust", blue, "Filter"));
        // HSV Filters
        availableFilters.put(id++, new FilterInfo(id - 1, "Adjust Saturation", saturation, "HSV"));
        availableFilters.put(id++, new FilterInfo(id - 1, "Adjust Hue", hue, "HSV"));
        availableFilters.put(id++, new FilterInfo(id - 1, "Adjust Value (Brightness)", value, "HSV"));
        // Convolutions
        availableFilters.put(id++, new FilterInfo(id - 1, "Sharpen", Kernels.SHARPEN, "Convolution"));
        availableFilters.put(id++, new FilterInfo(id - 1, "Box Blur 3x3", Kernels.BOX_BLUR_3x3, "Convolution"));
        availableFilters.put(id++, new FilterInfo(id - 1, "Gaussian Blur 3x3", Kernels.GAUSSIAN_BLUR_3x3, "Convolution"));
        availableFilters.put(id++, new FilterInfo(id - 1, "Gaussian Blur 7x7", Kernels.GAUSSIAN_BLUR_7x7, "Convolution"));
        availableFilters.put(id++, new FilterInfo(id - 1, "Strong Gaussian Blur 9x9", Kernels.STRONG_GAUSSIAN_BLUR_9x9, "Convolution"));
        availableFilters.put(id++, new FilterInfo(id - 1, "Emboss", Kernels.EMBOSS, "Convolution"));
        availableFilters.put(id++, new FilterInfo(id - 1, "Sobel X Gradient", Kernels.SOBEL_X, "Convolution"));
        availableFilters.put(id++, new FilterInfo(id - 1, "Sobel Y Gradient", Kernels.SOBEL_Y, "Convolution"));
        // Other Filters
        availableFilters.put(id++, new FilterInfo(id - 1, "Pixelate", null, "Pixelate"));
        // Sequences
        availableFilters.put(id++, new FilterInfo(id - 1, "Sobel Edge Detection", null, "SobelEdge"));
        availableFilters.put(id++, new FilterInfo(id - 1, "Comic Effect 1", null, "Sequence_Comic1"));
        availableFilters.put(id++, new FilterInfo(id - 1, "Comic Effect 2", null, "Sequence_Comic2"));
        availableFilters.put(id++, new FilterInfo(id - 1, "Grey Art Effect", null, "Sequence_GreyArt"));
        availableFilters.put(id++, new FilterInfo(id - 1, "Dreamy Effect", null, "Sequence_Dreamy"));

    }

        private static void displayMenu () {
        System.out.println("\n--- Available Filters & Sequences ---");
        for (FilterInfo info : availableFilters.values()) {
            System.out.printf("%d: %s (%s)\n", info.id, info.name, info.type);
        }
        System.out.println("-------------------------------------");
        System.out.println("Enter sequence as pairs: 'FilterNumber Strength FilterNumber Strength ...'");
        System.out.println("Strength is ignored for Sequences & most Convolutions.");
        System.out.println("Strength is BlockSize for Pixelate.");
        System.out.println("Strength is Level for Posterize, Threshold for Threshold, Degrees for Hue, -100 to 100 for Sat/Value.");
        System.out.println("Enter '0' as the FilterNumber to end the sequence and apply filters.");
        System.out.print("Enter sequence: ");
    }

        // --- Helper for Deep Copy ---
        private static Pixel[][] deepCopy (Pixel[][]original,int width, int height){
        if (original == null) return null;
        Pixel[][] copy = new Pixel[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (original[i][j] != null) {
                    Pixel p = original[i][j];
                    copy[i][j] = new Pixel(p.red, p.green, p.blue);
                } else {
                    copy[i][j] = null;
                }
            }
        }
        return copy;
    }


        // --- Filter Sequence Implementations ---

        static void applySobelEdgeDetection (BmpImage img1){
        int height = Math.abs(img1.header.height);
        int width = img1.header.width;

        System.out.println("Applying Greyscale for Sobel...");
        imageProcessor.applyFilter(img1, greyscale, 0); // Apply grayscale

        System.out.println("Creating copy for Gx...");
        Pixel[][] grayscaleGrid = deepCopy(img1.pixelGrid, width, height);
        if (grayscaleGrid == null) {
            System.err.println("Deep copy failed!");
            return;
        }

        System.out.println("Applying Sobel X...");
        imageProcessor.convolution(img1, Kernels.SOBEL_X); // img1 now holds Gx result
        Pixel[][] gxGrid = img1.pixelGrid; // Reference to Gx result

        img1.pixelGrid = grayscaleGrid; // Restore original grayscale grid to img1

        System.out.println("Applying Sobel Y...");
        imageProcessor.convolution(img1, Kernels.SOBEL_Y); // img1 now holds Gy result
        Pixel[][] gyGrid = img1.pixelGrid; // Reference to Gy result

        System.out.println("Calculating magnitude...");
        Pixel[][] magnitudeResult = new Pixel[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                // Check for nulls from convolution boundary handling maybe?
                if (gxGrid[i][j] == null || gyGrid[i][j] == null) {
                    magnitudeResult[i][j] = new Pixel(0, 0, 0); // Black if source is null
                    continue;
                }
                // Grayscale means R=G=B, so use any channel (e.g., red) for gradient value
                double gx = gxGrid[i][j].red;
                double gy = gyGrid[i][j].red;

                double magnitude = Math.sqrt(gx * gx + gy * gy);
                int grayValue = Math.max(0, Math.min(255, (int) magnitude));
                magnitudeResult[i][j] = new Pixel(grayValue, grayValue, grayValue);
            }
        }
        img1.pixelGrid = magnitudeResult; // Update image with final magnitude map
        System.out.println("Sobel Edge Detection complete.");
    }

        static void comic1 (BmpImage img1){
        System.out.println("Applying sequence: Comic Effect 1");
        imageProcessor.applyFilter(img1, greyscale, 1);
        imageProcessor.applyFilter(img1, posterize, 4); // Strength = 4 levels
        applySobelEdgeDetection(img1); // Apply edge detection
        imageProcessor.applyFilter(img1, threshold, 35); // Threshold the edges
    }

        static void grey_art (BmpImage img1){
        System.out.println("Applying sequence: Grey Art Effect");
//        imageProcessor.convolution(img1, Kernels.SHARPEN);
        imageProcessor.applyFilter(img1, greyscale, 0); // Strength 0 for standard grayscale
        imageProcessor.applyFilter(img1, posterize, 3);
    }

        static void comic2 (BmpImage img1)
        {
        System.out.println("Applying sequence: Comic Effect 2");
        imageProcessor.applyFilter(img1, greyscale, 1);
        Pixel[][] originalGrayscale = deepCopy(img1.pixelGrid, img1.header.width, Math.abs(img1.header.height)); // Need edges from grayscale
        applySobelEdgeDetection(img1); // img1 now has edges
        Pixel[][] edges = img1.pixelGrid; // Reference edges

        img1.pixelGrid = originalGrayscale;
        imageProcessor.applyFilter(img1, posterize, 4);


        for (int i = 0; i < Math.abs(img1.header.height); i++) {
            for (int j = 0; j < img1.header.width; j++) {
                if (edges[i][j] != null && edges[i][j].red > 50) {
                    img1.pixelGrid[i][j] = new Pixel(0, 0, 0);
                }

            }
        }
    }

        static void dreamy (BmpImage img1){
        System.out.println("Applying sequence: Dreamy Effect");
        imageProcessor.convolution(img1, Kernels.GAUSSIAN_BLUR_7x7); // Strong blur
        imageProcessor.applyFilter(img1, brightness, 20); // Increase brightness slightly
        imageProcessor.applyHSVfilter(img1, saturation, -30); // Decrease saturation
    }



    public static void main(String[] args) {
        initializeFilters();
        Scanner scanner = new Scanner(System.in);
        // Prompt user for file path
        System.out.print("Enter the path to the BMP image: ");
        String inputFilePath = scanner.nextLine().trim();
        if (inputFilePath.isEmpty()) {
            System.err.println("No file path provided. Exiting.");
            scanner.close();
            return;
        }
        String outputFilePath = "output_manual.bmp";

        try {
            System.out.println("Reading image: " + inputFilePath);
            BmpImage img1 = BmpReader.read(inputFilePath);
            if (img1 == null || img1.header == null || img1.pixelGrid == null) {
                System.err.println("Failed to read image or its components.");
                scanner.close();
                return;
            }
            System.out.println("Original Header:");
            System.out.println(img1.header);

            displayMenu();
            String line = scanner.nextLine();
            String[] parts = line.trim().split("\\s+");
            List<Command> commands = new ArrayList<>();

            try {
                for (int i = 0; i < parts.length; i += 2) {
                    int filterId = Integer.parseInt(parts[i]);
                    if (filterId == 0) {
                        break;
                    }
                    // Allow single '0' entry
                    if (parts.length == 1 && filterId == 0) break;

                    if (i + 1 >= parts.length) {
                        System.err.println("Error: Missing strength value for filter ID " + filterId + ". Aborting.");
                        scanner.close();
                        return;
                    }
                    int strength = Integer.parseInt(parts[i + 1]);
                    commands.add(new Command(filterId, strength));
                }
            } catch (NumberFormatException e) {
                System.err.println("Error: Invalid number format in input sequence. Please enter numbers only. Aborting.");
                scanner.close();
                return;
            } catch (ArrayIndexOutOfBoundsException e) {
                System.err.println("Error: Input sequence seems incomplete. Aborting.");
                scanner.close();
                return;
            }

            if (commands.isEmpty() && !(parts.length == 1 && parts[0].equals("0"))) {
                System.out.println("No valid commands entered. Saving original.");
            } else if (commands.isEmpty() && parts.length == 1 && parts[0].equals("0")) {
                System.out.println("Filter sequence ended immediately. Saving original.");
            }
            else {
                System.out.println("\nApplying " + commands.size() + " filter(s)/sequence(s)...");
                for (Command cmd : commands) {
                    FilterInfo info = availableFilters.get(cmd.filterId);
                    if (info == null) {
                        System.err.println("Warning: Filter ID " + cmd.filterId + " not found. Skipping.");
                        continue;
                    }

                    System.out.println("Applying: " + info.name + " with strength/param " + cmd.strength);

                    try {
                        switch(info.type) {
                            case "Filter":
                                if (info.filterObject instanceof Filters) {
                                    imageProcessor.applyFilter(img1, (Filters) info.filterObject, cmd.strength);
                                } else { throw new ClassCastException("Filter object mismatch"); }
                                break;
                            case "HSV":
                                if (info.filterObject instanceof HSVfilters) {
                                    imageProcessor.applyHSVfilter(img1, (HSVfilters) info.filterObject, cmd.strength);
                                } else { throw new ClassCastException("HSVFilter object mismatch"); }
                                break;
                            case "Convolution":
                                if (info.filterObject instanceof Kernel) {
                                    imageProcessor.convolution(img1, (Kernel) info.filterObject);
                                } else { throw new ClassCastException("Kernel object mismatch"); }
                                break;
                            case "Pixelate":
                                imageProcessor.pixelarise(img1, cmd.strength); // Strength is block size
                                break;
                            case "SobelEdge":
                                applySobelEdgeDetection(img1); // Strength ignored
                                break;
                            case "Sequence_Comic1":
                                comic1(img1); // Strength ignored
                                break;
                            case "Sequence_Comic2":
                                comic2(img1); // Strength ignored
                                break;
                            case "Sequence_GreyArt":
                                grey_art(img1); // Strength ignored
                                break;
                            case "Sequence_Dreamy":
                                dreamy(img1); // Strength ignored
                                break;
                            default:
                                System.err.println("Warning: Unknown filter type '" + info.type + "' for ID " + cmd.filterId + ". Skipping.");
                        }
                    } catch (Exception e) {
                        System.err.println("Error applying filter/sequence " + info.name + ": " + e.getMessage());
                        e.printStackTrace();
                        // Consider adding a flag to stop processing on error if desired
                    }
                }
                System.out.println("Filter application complete.");
            }


            System.out.println("Updating header for output...");

            int finalHeight = img1.pixelGrid.length;
            int finalWidth = img1.pixelGrid[0].length;

            img1.header.width = finalWidth;
            img1.header.height = finalHeight;
            img1.header.bitsPerPixel = 24;
            img1.header.planes = 1;
            img1.header.compression = 0;
            img1.header.headerSize = 40;
            img1.header.pixelOffset = 54;
            img1.header.colorsInColorTable = 0;
            img1.header.importantColors = 0;
            img1.header.reserved1 = 0;
            img1.header.reserved2 = 0;

            int rowDataSize = finalWidth * 3;
            int padding = (4 - (rowDataSize % 4)) % 4;
            int stride = rowDataSize + padding;
            img1.header.imageSize = stride * finalHeight;
            img1.header.fileSize = img1.header.pixelOffset + img1.header.imageSize;

            System.out.println("Final Header for Writing:");
            System.out.println(img1.header);

            try {
                System.out.println("Writing image to: " + outputFilePath);
                BmpWriter.write(outputFilePath, img1);
                System.out.println("Image written successfully.");
            } catch (IOException e) {
                System.err.println("Failed to write BMP file: " + e.getMessage());
                e.printStackTrace();
            }

        } catch (IOException e) {
            System.err.println("Error reading the input image: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
        }
        finally {
            scanner.close();
        }
    }
}
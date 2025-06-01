import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
// import java.awt.geom.AffineTransform; // Not strictly needed for current zoom
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
// Assuming these are your existing/correct import paths
import Models.BmpImage;
import Models.BmpHeader; // For deepCopyBmpImage
import Models.Pixel;
import io.BmpReader;
import io.BmpWriter;
import Filters.*; // Assuming wildcard import is okay or list them
import imageProcessors.imageProcessor; // Assuming class name is imageProcessor
import Kernels.Kernel; // For kernel types
import Kernels.Kernels; // For specific kernels

public class PhotoEditorUI extends JFrame {
    private JLabel imageLabel;
    private JScrollPane imageScrollPane; // For scrollability and zoom viewport
    private BmpImage currentImage;       // Last applied state
    private BmpImage originalImageBeforePreview; // Snapshot before a filter preview starts
    private BmpImage imageForPreview;    // Working copy for live preview

    private String currentFilePath;
    private JFileChooser fileChooser;

    // UI Elements for the control panel
    private JPanel controlPanel;
    private JSlider filterSlider;
    private JButton applyButton;
    private JButton cancelButton;
    private JLabel sliderValueLabel; // Optional: to display slider's current value

    private String activeFilterKey = null; // To track which filter's controls are up

    // Zoom related
    private double zoomFactor = 1.0;
    private Point mousePointForZoom; // For zoom centering (currently tracks last mouse position)

    // Filter instances
    private final Filters greyscaleFilter = new greyScale();
    private final Filters negativeFilter = new negative();
    private final Filters posterizeFilter = new posterize();
    private final Filters thresholdFilter = new threshold();
    private final HSVfilters hueFilter = new hue();
    private final HSVfilters saturationFilter = new saturation();
    private final HSVfilters valueFilter = new value();
    private final Kernel sharpenKernel = Kernels.SHARPEN;
    private final Kernel strongGaussianKernel = Kernels.STRONG_GAUSSIAN_BLUR_9x9;

    private final ChangeListener sliderChangeListener;


    public PhotoEditorUI() {
        setTitle("BMP Photo Editor");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 800);
        setLocationRelativeTo(null);

        // Define the single ChangeListener instance here
        sliderChangeListener = e -> {
            JSlider source = (JSlider) e.getSource();
            // Only proceed if a filter is active, the slider is meant to be used, and user finished adjusting
            if (activeFilterKey == null || !source.isVisible() || !controlPanel.isVisible()) {
                return;
            }
            if (!source.getValueIsAdjusting()) {
                updateSliderValueLabel(source.getValue());
                applyFilterToPreview(activeFilterKey, source.getValue());
            }
        };

        initializeComponents();
        setupMenuBar();
        setupLayout();
        // initializeFilterInstances(); // Filters are already initialized as final members
    }

    private void initializeFilterInstances() {
        // This method is not strictly needed if filters are declared final and initialized directly.
        // Kept for structure if you change initialization strategy later.
    }

    private void saveImage() {
        if (currentImage == null) {
            JOptionPane.showMessageDialog(this, "No image to save", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (currentFilePath == null) {
            saveImageAs();
            return;
        }
        try {
            updateImageHeaderBeforeSave(currentImage);
            BmpWriter.write(currentFilePath, currentImage);
            JOptionPane.showMessageDialog(this, "Image saved successfully to " + currentFilePath, "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving image: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void saveImageAs() {
        if (currentImage == null) {
            JOptionPane.showMessageDialog(this, "No image to save", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        fileChooser.setDialogTitle("Save Image As");
        int returnVal = fileChooser.showSaveDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            String newFilePath = fileToSave.getAbsolutePath();
            if (!newFilePath.toLowerCase().endsWith(".bmp")) {
                newFilePath += ".bmp";
            }
            try {
                updateImageHeaderBeforeSave(currentImage);
                BmpWriter.write(newFilePath, currentImage);
                currentFilePath = newFilePath;
                setTitle("BMP Photo Editor - " + fileToSave.getName());
                JOptionPane.showMessageDialog(this, "Image saved successfully to " + newFilePath, "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error saving image: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    private void updateImageHeaderBeforeSave(BmpImage image) {
        if (image == null || image.pixelGrid == null || image.header == null) {
            System.err.println("Cannot update header: image or its components are null.");
            return;
        }
        int height = image.pixelGrid.length;
        if (height == 0) {
            System.err.println("Cannot update header: image height is 0.");
            return;
        }
        int width = image.pixelGrid[0].length;
        if (width == 0) {
            System.err.println("Cannot update header: image width is 0.");
            return;
        }

        image.header.width = width;
        image.header.height = height;
        image.header.bitsPerPixel = 24;
        image.header.planes = 1;
        image.header.compression = 0;
        image.header.headerSize = 40;

        int rowDataSize = width * (image.header.bitsPerPixel / 8);
        int padding = (4 - (rowDataSize % 4)) % 4;
        int stride = rowDataSize + padding;
        image.header.imageSize = stride * height;

        image.header.pixelOffset = 14 + image.header.headerSize;
        image.header.fileSize = image.header.pixelOffset + image.header.imageSize;

        image.header.xPixelsPerMeter = 0;
        image.header.yPixelsPerMeter = 0;
        image.header.colorsInColorTable = 0;
        image.header.importantColors = 0;
        image.header.reserved1 = 0;
        image.header.reserved2 = 0;
    }

    private BmpHeader deepCopyBmpHeader(BmpHeader originalHeader) {
        if (originalHeader == null) return null;
        BmpHeader copy = new BmpHeader();
        copy.fileSize = originalHeader.fileSize;
        copy.reserved1 = originalHeader.reserved1;
        copy.reserved2 = originalHeader.reserved2;
        copy.pixelOffset = originalHeader.pixelOffset;
        copy.headerSize = originalHeader.headerSize;
        copy.width = originalHeader.width;
        copy.height = originalHeader.height;
        copy.planes = originalHeader.planes;
        copy.bitsPerPixel = originalHeader.bitsPerPixel;
        copy.compression = originalHeader.compression;
        copy.imageSize = originalHeader.imageSize;
        copy.xPixelsPerMeter = originalHeader.xPixelsPerMeter;
        copy.yPixelsPerMeter = originalHeader.yPixelsPerMeter;
        copy.colorsInColorTable = originalHeader.colorsInColorTable;
        copy.importantColors = originalHeader.importantColors;
        return copy;
    }

    private void initializeComponents() {
        fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("BMP Files", "bmp"));

        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        imageLabel.setVerticalAlignment(JLabel.CENTER);

        imageScrollPane = new JScrollPane(imageLabel);
        imageScrollPane.setBackground(Color.DARK_GRAY);
        imageLabel.setOpaque(true);
        imageLabel.setBackground(Color.LIGHT_GRAY);

        controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        filterSlider = new JSlider();
        sliderValueLabel = new JLabel("Value: ");
        applyButton = new JButton("Apply");
        cancelButton = new JButton("Cancel");

        filterSlider.setPreferredSize(new Dimension(200, 50));
        filterSlider.setVisible(false);
        sliderValueLabel.setVisible(false);

        controlPanel.add(sliderValueLabel);
        controlPanel.add(filterSlider);
        controlPanel.add(applyButton);
        controlPanel.add(cancelButton);
        controlPanel.setVisible(false);

        // Add the single, persistent listener
        filterSlider.addChangeListener(sliderChangeListener);

        applyButton.addActionListener(e -> applyPreviewChanges());
        cancelButton.addActionListener(e -> cancelPreviewChanges());

        imageScrollPane.addMouseWheelListener(new MouseWheelZoomListener());
        imageScrollPane.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                mousePointForZoom = e.getPoint();
            }
        });
    }

    private void setupMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem openItem = new JMenuItem("Open");
        openItem.addActionListener(e -> openImage());
        JMenuItem saveItem = new JMenuItem("Save");
        saveItem.addActionListener(e -> saveImage());
        JMenuItem saveCopyItem = new JMenuItem("Save As");
        saveCopyItem.addActionListener(e -> saveImageAs());
        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.add(saveCopyItem);

        JMenu viewFilterMenu = new JMenu("Filter");
        addFilterMenuItem(viewFilterMenu, "Greyscale", "GREYSCALE");
        addFilterMenuItem(viewFilterMenu, "Negative", "NEGATIVE");
        addFilterMenuItem(viewFilterMenu, "Posterize", "POSTERIZE");
        addFilterMenuItem(viewFilterMenu, "Threshold", "THRESHOLD");
        addFilterMenuItem(viewFilterMenu, "Comic Effect 2", "COMIC2");

        JMenu hsvMenu = new JMenu("HSV");
        addFilterMenuItem(hsvMenu, "Hue", "HUE");
        addFilterMenuItem(hsvMenu, "Saturation", "SATURATION");
        addFilterMenuItem(hsvMenu, "Value (Brightness)", "VALUE");

        JMenu adjustmentsMenu = new JMenu("Adjustments");
        addFilterMenuItem(adjustmentsMenu, "Blur (Strong 9x9)", "BLUR_STRONG");
        addFilterMenuItem(adjustmentsMenu, "Sharpen", "SHARPEN");

        menuBar.add(fileMenu);
        menuBar.add(viewFilterMenu);
        menuBar.add(hsvMenu);
        menuBar.add(adjustmentsMenu);
        setJMenuBar(menuBar);
    }

    private void addFilterMenuItem(JMenu menu, String name, String filterKey) {
        JMenuItem item = new JMenuItem(name);
        item.addActionListener(e -> prepareFilterPreview(filterKey));
        menu.add(item);
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        add(imageScrollPane, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);
    }

    private void openImage() {
        if (activeFilterKey != null) {
            cancelPreviewChanges();
        }
        int returnVal = fileChooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                currentFilePath = file.getAbsolutePath();
                currentImage = BmpReader.read(currentFilePath);
                if (currentImage == null || currentImage.header == null || currentImage.pixelGrid == null || currentImage.header.bitsPerPixel != 24) {
                    JOptionPane.showMessageDialog(this, "Only 24-bit BMP files are supported, or the file is invalid.", "Error", JOptionPane.ERROR_MESSAGE);
                    currentImage = null; imageLabel.setIcon(null); return;
                }
                // TODO: Add check for at least one non-null pixel if necessary

                zoomFactor = 1.0;
                originalImageBeforePreview = null;
                imageForPreview = null;
                activeFilterKey = null; // Ensure no filter is active
                controlPanel.setVisible(false); // Hide control panel
                displayBmpImage(currentImage);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error loading image: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                currentImage = null; imageLabel.setIcon(null);
                e.printStackTrace();
            }
        }
    }

    private void prepareFilterPreview(String filterKey) {
        if (currentImage == null) {
            JOptionPane.showMessageDialog(this, "Please open an image first.", "No Image", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        if (activeFilterKey != null && !activeFilterKey.equals(filterKey)) {
            cancelPreviewChanges();
        }

        activeFilterKey = filterKey;
        originalImageBeforePreview = deepCopyBmpImage(currentImage);
        imageForPreview = deepCopyBmpImage(originalImageBeforePreview);

        int L_defaultValue = 0; // Using local variables for clarity within the method
        boolean L_makeSliderVisible = true;

        switch (filterKey) {
            case "GREYSCALE":
            case "NEGATIVE":
            case "BLUR_STRONG":
            case "SHARPEN":
            case "COMIC2":
                L_makeSliderVisible = false;
                break;
            case "THRESHOLD":
                filterSlider.setMinimum(0); filterSlider.setMaximum(255); L_defaultValue = 127;
                break;
            case "POSTERIZE":
                filterSlider.setMinimum(2); filterSlider.setMaximum(20); L_defaultValue = 4;
                break;
            case "HUE":
                filterSlider.setMinimum(-180); filterSlider.setMaximum(180); L_defaultValue = 0;
                break;
            case "SATURATION":
            case "VALUE":
                filterSlider.setMinimum(-100); filterSlider.setMaximum(100); L_defaultValue = 0;
                break;
            default:
                System.err.println("Unknown filter key in prepareFilterPreview: " + filterKey);
                L_makeSliderVisible = false;
        }

        filterSlider.setVisible(L_makeSliderVisible);
        sliderValueLabel.setVisible(L_makeSliderVisible);

        if (L_makeSliderVisible) {
            filterSlider.setValue(L_defaultValue);          // 1. Set the slider's visual state and current value
            updateSliderValueLabel(L_defaultValue);         // 2. Update the accompanying label

            // 3. <<<< KEY CHANGE HERE >>>>
            //    Explicitly apply the filter with this default value for the initial preview.
            //    This ensures it's applied immediately when the controls appear.
            applyFilterToPreview(activeFilterKey, L_defaultValue);
        } else {
            // For filters WITHOUT a slider, directly apply the filter for the initial preview.
            // L_defaultValue (typically 0 here) serves as the dummy parameter.
            applyFilterToPreview(activeFilterKey, L_defaultValue);
        }

        controlPanel.setVisible(true);
        pack();
    }
    private void updateSliderValueLabel(int value) {
        if (activeFilterKey != null && filterSlider.isVisible()) { // Check if slider is meant to be visible
            String prefix = activeFilterKey.substring(0, 1).toUpperCase() + activeFilterKey.substring(1).toLowerCase().replace("_", " ");
            if (activeFilterKey.equals("HUE")) prefix += " (deg)";
            else if (activeFilterKey.equals("SATURATION") || activeFilterKey.equals("VALUE")) prefix += " (%)";
            else if (activeFilterKey.equals("POSTERIZE")) prefix += " (levels)";
            else if (activeFilterKey.equals("THRESHOLD")) prefix += " (threshold)";
            // Add more specific labels if needed
            sliderValueLabel.setText(prefix + ": " + value);
        } else {
            sliderValueLabel.setText("Value: "); // Default or clear
        }
    }

    private void applyFilterToPreview(String filterKey, int value) {
        if (imageForPreview == null || originalImageBeforePreview == null) {
            System.err.println("applyFilterToPreview: Preview images not initialized.");
            return;
        }
        if (filterKey == null) {
            System.err.println("applyFilterToPreview: filterKey is null.");
            return;
        }


        // Always re-apply from the state *before this specific filter preview started*
        // This ensures slider changes don't compound on an already filtered preview image.
        imageForPreview.pixelGrid = deepCopyPixelGrid(originalImageBeforePreview.pixelGrid, originalImageBeforePreview.header.width, Math.abs(originalImageBeforePreview.header.height));
        // Header might not need deep copy for each preview step if only pixel data changes,
        // but it's safer if filter operations *could* modify header (e.g. dimensions, though not here)
        imageForPreview.header = deepCopyBmpHeader(originalImageBeforePreview.header);


        switch (filterKey) {
            case "GREYSCALE":
                imageProcessor.applyFilter(imageForPreview, greyscaleFilter, 0);
                break;
            case "NEGATIVE":
                imageProcessor.applyFilter(imageForPreview, negativeFilter, 0);
                break;
            case "POSTERIZE":
                imageProcessor.applyFilter(imageForPreview, posterizeFilter, value);
                break;
            case "THRESHOLD":
                imageProcessor.applyFilter(imageForPreview, thresholdFilter, value);
                break;
            case "HUE":
                imageProcessor.applyHSVfilter(imageForPreview, hueFilter, value);
                break;
            case "SATURATION":
                imageProcessor.applyHSVfilter(imageForPreview, saturationFilter, value);
                break;
            case "VALUE":
                imageProcessor.applyHSVfilter(imageForPreview, valueFilter, value);
                break;
            case "BLUR_STRONG":
                imageProcessor.convolution(imageForPreview, strongGaussianKernel);
                break;
            case "SHARPEN":
                imageProcessor.convolution(imageForPreview, sharpenKernel);
                break;
            case "COMIC2":
                applyComic2Effect(imageForPreview);
                break;
            default:
                System.err.println("Unknown filter key in applyFilterToPreview: " + filterKey);
                return; // Don't try to display if filter is unknown
        }
        displayBmpImage(imageForPreview);
    }

    private void applyComic2Effect(BmpImage img) {
        // This is a placeholder. The actual logic from App.java for comic2
        // (including greyscale, Sobel edge detection, posterize, and combining them)
        // needs to be implemented here or in imageProcessor.
        // For now, a simple sequence:
        if (img == null) return;
        System.out.println("Applying Comic Effect 2 (Simplified for UI structure)...");
        imageProcessor.applyFilter(img, greyscaleFilter, 0); // Greyscale
        imageProcessor.applyFilter(img, posterizeFilter, 4);   // Posterize with 4 levels
        // Missing: Edge detection and combination.
        // To make it visually distinct for now:
        imageProcessor.convolution(img, Kernels.EMBOSS); // Add an emboss as a stand-in for edge effect
        System.out.println("Comic Effect 2 (Simplified) applied.");
    }

    private void applyPreviewChanges() {
        if (imageForPreview != null) {
            currentImage = deepCopyBmpImage(imageForPreview); // Make preview permanent
            imageForPreview = null;
            originalImageBeforePreview = null;
            controlPanel.setVisible(false);
            activeFilterKey = null;
            displayBmpImage(currentImage);
            pack();
        }
    }

    private void cancelPreviewChanges() {
        if (originalImageBeforePreview != null) {
            currentImage = deepCopyBmpImage(originalImageBeforePreview); // Revert currentImage
            displayBmpImage(currentImage); // Display the reverted state

            imageForPreview = null;
            originalImageBeforePreview = null;
            controlPanel.setVisible(false);
            activeFilterKey = null;
            pack();
        } else if (activeFilterKey != null) { // Handle case where preview started but original was null (should not happen)
            controlPanel.setVisible(false);
            activeFilterKey = null;
            if (currentImage != null) displayBmpImage(currentImage); // Display current image if available
            pack();
        }
    }

    private BmpImage deepCopyBmpImage(BmpImage original) {
        if (original == null) return null;
        BmpImage copy = new BmpImage();
        copy.header = deepCopyBmpHeader(original.header);
        // Ensure pixelGrid is not null before trying to get dimensions
        if (original.pixelGrid != null && original.header != null) {
            copy.pixelGrid = deepCopyPixelGrid(original.pixelGrid, original.header.width, Math.abs(original.header.height));
        } else {
            copy.pixelGrid = null; // Or handle error appropriately
        }
        return copy;
    }

    private Pixel[][] deepCopyPixelGrid(Pixel[][] original, int width, int height) {
        if (original == null || width == 0 || height == 0) return null;
        Pixel[][] copy = new Pixel[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (i < original.length && j < original[i].length && original[i][j] != null) {
                    Pixel p = original[i][j];
                    copy[i][j] = new Pixel(p.red, p.green, p.blue);
                } else {
                    // This case indicates a mismatch between header dimensions and actual grid,
                    // or a sparse/jagged array if not rectangular. For BMP, it should be rectangular.
                    // Filling with black or throwing an error might be options.
                    copy[i][j] = new Pixel(0,0,0); // Default to black if source is out of bounds/null
                }
            }
        }
        return copy;
    }

    private void displayBmpImage(BmpImage bmpImage) {
        if (bmpImage == null || bmpImage.header == null || bmpImage.pixelGrid == null) {
            imageLabel.setIcon(null);
            imageLabel.setText("No image loaded or image data is invalid.");
            return;
        }
        imageLabel.setText(null);

        int imgWidth = bmpImage.header.width;
        int imgHeight = Math.abs(bmpImage.header.height);

        if (imgWidth <= 0 || imgHeight <= 0) {
            imageLabel.setIcon(null);
            imageLabel.setText("Invalid image dimensions.");
            return;
        }

        // Check pixelGrid integrity against header dimensions
        if (bmpImage.pixelGrid.length != imgHeight || bmpImage.pixelGrid[0].length != imgWidth) {
            imageLabel.setIcon(null);
            imageLabel.setText("Image data inconsistent with header dimensions.");
            // Optionally, try to use Math.min to avoid array out of bounds if you want to be lenient
            // imgHeight = Math.min(imgHeight, bmpImage.pixelGrid.length);
            // if (imgHeight > 0) imgWidth = Math.min(imgWidth, bmpImage.pixelGrid[0].length); else imgWidth = 0;
            // if (imgWidth <= 0 || imgHeight <= 0) return;
            return;
        }


        Dimension viewportSize = imageScrollPane.getViewport().getExtentSize();
        if (viewportSize.width <= 0 || viewportSize.height <= 0) {
            viewportSize = imageScrollPane.getPreferredSize();
            if (viewportSize.width <= 0 || viewportSize.height <= 0) {
                viewportSize = new Dimension(Math.max(200, imgWidth), Math.max(200, imgHeight)); // Fallback with some size
            }
        }

        double scaleX = (double) viewportSize.width / imgWidth;
        double scaleY = (double) viewportSize.height / imgHeight;
        double initialScale = Math.min(scaleX, scaleY);
        if (initialScale <= 0) initialScale = 1.0; // Prevent zero or negative scale

        double effectiveScale = initialScale * zoomFactor;
        if (effectiveScale <= 0) effectiveScale = 0.01; // Prevent zero or negative scale

        int newWidth = (int) (imgWidth * effectiveScale);
        int newHeight = (int) (imgHeight * effectiveScale);

        if (newWidth <= 0 || newHeight <= 0) {
            // Fallback to a minimal size to avoid issues with getScaledInstance
            newWidth = Math.max(1, newWidth);
            newHeight = Math.max(1, newHeight);
        }

        BufferedImage bufferedImg = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < imgHeight; y++) {
            for (int x = 0; x < imgWidth; x++) {
                // Check array bounds for pixelGrid before accessing
                if (y < bmpImage.pixelGrid.length && x < bmpImage.pixelGrid[y].length) {
                    Pixel pixel = bmpImage.pixelGrid[y][x];
                    int rgb = (pixel != null) ? ((pixel.red << 16) | (pixel.green << 8) | pixel.blue) : 0;
                    bufferedImg.setRGB(x, y, rgb);
                } else {
                    bufferedImg.setRGB(x, y, 0); // Black for out-of-bounds (should not happen if checks above are good)
                }
            }
        }

        Image scaledImage = bufferedImg.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        ImageIcon icon = new ImageIcon(scaledImage);
        imageLabel.setIcon(icon);
        imageLabel.setPreferredSize(new Dimension(newWidth, newHeight));
        // imageLabel.revalidate(); // JScrollPane handles revalidation often
        imageScrollPane.revalidate();
        imageScrollPane.repaint(); // Ensure viewport updates
    }

    private class MouseWheelZoomListener implements MouseWheelListener {
        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            if (currentImage == null) return;

            double oldZoomFactor = zoomFactor;
            if (e.getWheelRotation() < 0) {
                zoomFactor *= 1.1;
            } else {
                zoomFactor /= 1.1;
            }
            // Basic zoom limits
            zoomFactor = Math.max(0.05, Math.min(zoomFactor, 20.0));


            // For zoom-to-cursor, we need the mouse position *relative to the imageLabel*
            // and the current scroll position.
            Point mouseRelativeToScrollPane = e.getPoint(); // Mouse position relative to scrollPane
            Point currentViewPosition = imageScrollPane.getViewport().getViewPosition();
            Dimension labelSize = imageLabel.getSize(); // Current scaled size

            // Calculate mouse position on the actual (scaled) imageLabel content
            int mouseXOnLabel = mouseRelativeToScrollPane.x - imageLabel.getX() + currentViewPosition.x;
            int mouseYOnLabel = mouseRelativeToScrollPane.y - imageLabel.getY() + currentViewPosition.y;


            displayBmpImage(imageForPreview != null ? imageForPreview : currentImage); // This will resize the label

            // After displayBmpImage, imageLabel has a new preferred size.
            // We need to adjust scroll position to keep mouseXOnLabel/mouseYOnLabel at the same *visual* spot.
            // New label size
            Dimension newLabelSize = imageLabel.getPreferredSize();

            // Point on the new scaled image that should be under the mouse
            double anchorXRatio = (labelSize.width > 0) ? (double)mouseXOnLabel / labelSize.width : 0.5;
            double anchorYRatio = (labelSize.height > 0) ? (double)mouseYOnLabel / labelSize.height : 0.5;

            int newViewX = (int)(newLabelSize.width * anchorXRatio - mouseRelativeToScrollPane.x + imageLabel.getX());
            int newViewY = (int)(newLabelSize.height * anchorYRatio - mouseRelativeToScrollPane.y + imageLabel.getY());

            // Constrain view position
            newViewX = Math.max(0, Math.min(newViewX, newLabelSize.width - imageScrollPane.getViewport().getWidth()));
            newViewY = Math.max(0, Math.min(newViewY, newLabelSize.height - imageScrollPane.getViewport().getHeight()));


            if(newLabelSize.width > imageScrollPane.getViewport().getWidth() ||
                    newLabelSize.height > imageScrollPane.getViewport().getHeight()){
                imageScrollPane.getViewport().setViewPosition(new Point(newViewX, newViewY));
            } else {
                imageScrollPane.getViewport().setViewPosition(new Point(0,0)); // Center if smaller than viewport
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PhotoEditorUI().setVisible(true));
    }
}
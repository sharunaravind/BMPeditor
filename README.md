# PhototSynthesis 📸

PhototSynthesis is a desktop application built with Java Swing for viewing and editing BMP (Bitmap) image files. It provides a user-friendly interface to apply various filters and adjustments to your images.

---
## 🌟 Features

* **BMP Image Loading & Saving:**
    * Open 24-bit BMP files.
    * Save changes to the current file.
    * Save edits as a new BMP file ("Save As").
* **Image Viewing:**
    * Display BMP images.
    * Zoom in/out using the mouse wheel, centered on the mouse cursor.
    * Scrollable image pane for large images.
* **Image Filters & Effects:**
    * **Standard Filters:**
        * Greyscale
        * Negative
        * Posterize (with adjustable levels)
        * Threshold (with adjustable level)
        * Comic Effect 2 (a black and white comic style effect combining greyscale, posterize, and emboss)
    * **HSV Adjustments:**
        * Hue (adjust color tones)
        * Saturation (adjust color intensity)
        * Value (Brightness)
    * **Kernel-Based Adjustments (Convolutions):**
        * Blur (Strong 9x9 Gaussian Blur)
        * Sharpen
* **Live Preview:**
    * See filter effects in real-time as you adjust parameters using a slider.
    * Apply or cancel changes made during preview.
* **User Interface:**
    * Menu-driven operations for file handling and filter selection.
    * Control panel with a slider for filters that require a parameter.
    * Displays current slider value and filter type.

---
## 💻 Tech Stack

* **Language:** Java (Swing for GUI)
* **Platform:** Desktop

---
## 📂 Project Structure
```text
.
├── BmpEditorApp
│   ├── App.java                 # Main application entry point (Console version)
│   ├── Filters                  # Image filter implementations
│   │   ├── blue.java
│   │   ├── brightness.java
│   │   ├── darken.java
│   │   ├── Filters.java         # Base filter interface/class
│   │   ├── greyScale.java
│   │   ├── HSVfilters.java      # Base for HSV-related filters
│   │   ├── hue.java
│   │   ├── negative.java
│   │   ├── posterize.java
│   │   ├── red.java
│   │   ├── saturation.java
│   │   ├── solarize.java
│   │   ├── solarizeTest.java
│   │   ├── swap.java
│   │   ├── threshold.java
│   │   └── value.java
│   ├── imageProcessors
│   │   └── imageProcessor.java  # Logic for applying filters and convolutions
│   ├── io
│   │   ├── BmpReader.java       # Reads BMP files
│   │   └── BmpWriter.java       # Writes BMP files
│   ├── Kernels
│   │   ├── Kernel.java          # Represents a convolution kernel
│   │   └── Kernels.java         # Predefined kernels (sharpen, blur, etc.)
│   ├── Models
│   │   ├── BmpHeader.java       # BMP file header data
│   │   ├── BmpImage.java        # Represents the BMP image (header + pixel data)
│   │   ├── HSV.java             # HSV color model representation
│   │   └── Pixel.java           # RGB pixel data
│   └── PhotoEditorUI.java       # Main GUI class & entry point
└── production
└── BMPeditor
├── App.class
```
---
## ⚙️ Prerequisites

* Java Development Kit (JDK) version 21 or higher.

---
---
## 🚀 Getting Started

1.  **Clone the repository (or download the source code):**
    ```bash
    git clone https://github.com/sharunaravind/BMPeditor
    cd <repository-directory>
    ```

2.  **Compile the Java files:**
    You have two main applications: a GUI-based editor (`PhotoEditorUI.java`) and a console-based one (`App.java`).
    * **Using an IDE (Recommended):**
        Open the project in an IDE like IntelliJ IDEA, Eclipse, or VS Code. Most IDEs will handle compilation automatically or with a simple "Build Project" command. This is the easiest way to manage dependencies and packages.
    * **Command Line Compilation:**
        Navigate to your project's root directory (the one that contains the `BmpEditorApp` folder).
        * To compile the GUI application (`PhotoEditorUI.java` and its dependencies):
            ```bash
            javac BmpEditorApp/PhotoEditorUI.java
            ```
            *(This command requires that all classes imported by `PhotoEditorUI.java`, such as those in `Filters`, `Models`, etc., are correctly located within their respective sub-packages inside `BmpEditorApp` and are accessible to the compiler.)*
        * To compile the console application (`App.java` and its dependencies):
            ```bash
            javac BmpEditorApp/App.java
            ```
        * To compile all Java files within the `BmpEditorApp` package and its sub-packages:
            ```bash
            javac BmpEditorApp/*.java BmpEditorApp/Filters/*.java BmpEditorApp/imageProcessors/*.java BmpEditorApp/io/*.java BmpEditorApp/Kernels/*.java BmpEditorApp/Models/*.java
            ```

3.  **Run the application:**
    After successful compilation, run the applications from your project's root directory (the one containing the `BmpEditorApp` folder where the compiled `.class` files corresponding to their packages should reside).
    * **To run the GUI Photo Editor:**
        ```bash
        java BmpEditorApp.PhotoEditorUI
        ```
    * **To run the console-driven application (offers additional features/filters):**
        ```bash
        java BmpEditorApp.App
        ```
        *(Note: For these commands to work, Java must be able to find the compiled `.class` files. If you compile into a separate output directory like `out`, you'll need to adjust your classpath accordingly when running, e.g., `java -cp out/production/BMPeditor BmpEditorApp.PhotoEditorUI`.)*

---
## 🛠️ How to Use

1.  Launch the application.
2.  Go to `File > Open` to load a 24-bit BMP image.
3.  The image will be displayed in the main panel.
4.  Select a filter from the `Filter`, `HSV`, or `Adjustments` menus.
5.  If the filter is adjustable (e.g., Posterize, Hue), a control panel will appear at the bottom with a slider.
    * Move the slider to see a live preview of the effect.
    * Click `Apply` to make the changes permanent to the current working image.
    * Click `Cancel` to discard the previewed changes for that filter.
6.  For non-adjustable filters (e.g., Greyscale, Sharpen), the effect is previewed immediately. Click `Apply` or `Cancel`.
7.  Use `File > Save` to save changes to the existing file or `File > Save As` to save to a new file.
8.  Use the mouse wheel over the image to zoom in and out.

---
## 🧑‍💻 Author

* **sharunaravind**

---

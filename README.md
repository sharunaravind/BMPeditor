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

.
├── BmpEditorApp
│   ├── App.java                 # Main application entry point
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
│   └── PhotoEditorUI.java       # Main GUI class


---
## ⚙️ Prerequisites

* Java Development Kit (JDK) version 21 or higher.

---
## 🚀 Getting Started

1.  **Clone the repository (or download the source code):**
    ```bash
    git clone <your-repository-url>
    cd <repository-directory>
    ```

2.  **Compile the Java files:**
    Navigate to the directory containing the `BmpEditorApp` folder (the root of your source code for this module).
    A simple way to compile, assuming all source files are within the `BmpEditorApp` directory and its subdirectories and properly declare their packages:

    * **If `App.java` and other files are directly in `BmpEditorApp` and sub-packages:**
        You would typically compile from the directory *above* `BmpEditorApp` or ensure your classpath is set. For example, if your project root is `BMPeditor` and `BmpEditorApp` is inside it:
        ```bash
        cd BMPeditor # Or wherever your project root is
        javac BmpEditorApp/*.java BmpEditorApp/Filters/*.java BmpEditorApp/imageProcessors/*.java BmpEditorApp/io/*.java BmpEditorApp/Kernels/*.java BmpEditorApp/Models/*.java
        ```
    * **Using an IDE (Recommended):**
        Open the project in an IDE like IntelliJ IDEA, Eclipse, or VS Code. Most IDEs will handle compilation automatically or with a simple "Build Project" command.

    * **User-provided compile command (may require being in the correct directory or adjusting for packages):**
        ```bash
        javac *.java
        ```
        *(Note: This command is very general. For a project with packages, you'll typically need to specify paths or compile from a source root directory, e.g., `javac BmpEditorApp/App.java`, or use an IDE's build system).*

3.  **Run the application:**
    Assuming `App.java` in the `BmpEditorApp` package is your main entry point (which instantiates `PhotoEditorUI`):
    From the directory *containing* the `BmpEditorApp` compiled output (e.g., if your compiled classes are in `out/production/BMPeditor` relative to project root, you might run from `out/production/BMPeditor`'s parent, or adjust classpath):

    ```bash
    java BmpEditorApp.App
    ```
    Or, if `PhotoEditorUI.java` itself contained a `main` method and was intended to be the entry point (and assuming it's in the default package or `BmpEditorApp` package and compiled correctly):
    ```bash
    java PhotoEditorUI # If in default package
    # or
    java BmpEditorApp.PhotoEditorUI # If in BmpEditorApp package
    ```
    *(Based on your input `java PhotoEditorUI` and the structure, ensure `PhotoEditorUI` has a `main` method, or adjust to run `BmpEditorApp.App` if `App.java` is the entry point.)*

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

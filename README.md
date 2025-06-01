# 🖼️ PhotoEditorUI

**PhotoEditorUI** is a Java-based desktop application for editing BMP images using a variety of filters and visual effects. It supports live previews, real-time slider controls, and intuitive zoom-to-cursor navigation.

## ✨ Features

- 🎨 **Filter Effects**
  - Greyscale
  - Negative
  - Posterize (with adjustable levels)
  - Threshold
  - HSV Filters (Hue, Saturation, Value)
  - Blur & Sharpen (convolution-based)
  - Comic Effect (simplified for now)

- 🔄 **Live Filter Preview**
  - Preview changes before applying them permanently
  - Slider values immediately update the image preview

- 🔍 **Zoom-to-Cursor Navigation**
  - Use mouse wheel to zoom in/out while keeping cursor location centered
  - Smart scroll behavior maintains position during zooming

- 🛠️ **Non-Destructive Editing**
  - Filters apply to a copy of the original image
  - Revert or cancel changes at any time

- ✅ **Robust Error Handling**
  - Deep copy of pixel data
  - Validates image header and pixel grid consistency


## 🧪 How It Works

- Upon selecting a filter:
  - If it uses a slider, the slider and label are shown.
  - A default filter value is applied immediately for preview.
- For non-slider filters, the effect is applied instantly.
- All operations work on a **deep copy** of the original image to preserve the original until changes are confirmed.

---

## 🚀 Getting Started

### 🔧 Requirements

- Java 8 or higher
- A Java IDE (e.g., IntelliJ, Eclipse) or terminal with `javac`
- Swing (comes bundled with Java SE)

### 📦 Compilation & Run

To compile:

```bash
javac *.java
java PhotoEditorUI
```

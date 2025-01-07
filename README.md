# Camera Test Android

This repository contains an Android application that demonstrates how to use the Camera2 API to capture images and integrate a TensorFlow Lite object detection model for real-time object detection.

## Features
- **Camera2 API Integration**: Leverages Android's Camera2 API for image capture and processing.
- **TensorFlow Lite Object Detection**: Performs real-time object detection using a pre-trained TensorFlow Lite model.
- **Bitmap Processing**: Rotates and saves captured images to the device storage.
- **Permission Management**: Handles runtime permissions for camera and storage access.

## Requirements
- **Android Studio**: Bumblebee or newer.
- **Android Device**: Running Android 6.0 (API 23) or higher.
- **TensorFlow Lite Model**: Ensure a valid `.tflite` model is placed in the `assets` directory.

## Setup Instructions

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/jinmc/camera_test_android.git
   ```

2. **Open in Android Studio**:
   - Launch Android Studio.
   - Open the project from the cloned repository.

3. **Add TensorFlow Lite Model**:
   - Download a pre-trained TensorFlow Lite model (e.g., MobileNet SSD).
   - Place the `.tflite` file in the `app/src/main/assets/` directory.

4. **Update Dependencies**:
   - Open `build.gradle` (Module: app).
   - Ensure the following dependencies are included:
     ```gradle
     implementation 'org.tensorflow:tensorflow-lite:2.13.0'
     implementation 'org.tensorflow:tensorflow-lite-task-vision:0.4.3'
     ```

5. **Run the Application**:
   - Connect an Android device or start an emulator.
   - Build and run the app using Android Studio.

## Usage
- **Capture Image**:
  - Launch the app.
  - Grant necessary permissions (camera and storage).
  - Press the capture button to take an image.

- **Perform Object Detection**:
  - After capturing an image, the TensorFlow Lite model will process it.
  - Detection results (labels, confidence scores, bounding boxes) will be logged in Logcat.

## Project Structure
- `MainActivity.kt`: Contains the main logic for the Camera2 API and TensorFlow Lite integration.
- `ObjectDetectionHelper.kt`: A helper class to load and run inference with TensorFlow Lite models.
- `activity_main.xml`: Defines the UI layout, including the camera preview and capture button.

## References
- [TensorFlow Lite Android Object Detection Tutorial](https://www.tensorflow.org/lite/android/tutorials/object_detection?hl=ko)
- [YouTube: Real-Time Object Detection with TensorFlow Lite](https://www.youtube.com/watch?v=S-7H72UTiBU)

## Known Issues
- **Permission Errors**: Ensure runtime permissions are granted for camera and storage access.
- **Model Not Found**: Confirm that the `.tflite` model is correctly placed in the `assets` folder.
- **Performance**: Object detection speed may vary based on device capabilities.

## Future Improvements
- Add a real-time camera preview overlay to display bounding boxes on detected objects.
- Optimize performance for low-end devices.
- Support multiple object detection models.

## License
This project is licensed under the MIT License. See the LICENSE file for details.

---

For any issues or contributions, feel free to open an issue or submit a pull request. Enjoy experimenting with Camera2 API and TensorFlow Lite!


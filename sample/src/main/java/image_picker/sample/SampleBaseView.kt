package image_picker.sample

import java.io.File

interface SampleBaseView {
  fun showImage(file: File)

  fun showImagePermissionRationale(requestCodeCameraPermissions: Any)
}
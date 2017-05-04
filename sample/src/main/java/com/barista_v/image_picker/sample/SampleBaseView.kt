package com.barista_v.image_picker.sample

interface SampleBaseView {
  fun showImage(path: String)

  fun showImagePermissionRationale(requestCodeCameraPermissions: Any)
}
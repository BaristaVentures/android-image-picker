package com.barista_v.image_picker.sample

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.barista_v.image_picker.ActivityResult
import com.barista_v.image_picker.AndroidImageManager
import com.barista_v.image_picker.sample.utils.State
import com.barista_v.image_picker.sample.utils.extensions.nowString
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_sample.*

/**
 * 1. Its important to save some variables on [AppCompatActivity.onRestoreInstanceState]
 *
 */
class SampleActivity : AppCompatActivity() {
  val requestCodeGalleryPermissions = 2
  val requestCodeCameraPhoto = 3
  val requestCodeGalleryPhoto = 4

  var androidImageManager: AndroidImageManager? = null
  var state = State(null)
  val currentDate: String get() = java.util.Calendar.getInstance().nowString()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_sample)

    androidImageManager = AndroidImageManager(this, BuildConfig.APPLICATION_ID)
    state = State(savedInstanceState)

    cameraButton.setOnClickListener { onCameraClick() }
    galleryButton.setOnClickListener { onGalleryClick() }

    waitForImagePath()
  }

  fun onGalleryClick() {
    state.thumbFileName = currentDate

    if (androidImageManager?.permissionsGranted() == true) {
      androidImageManager?.requestImageFromGallery(requestCodeGalleryPhoto)
    } else if (androidImageManager?.shouldShowPermissionRationale() == true) {
      showImagePermissionRationale()
    } else {
      androidImageManager?.requestPermission(requestCodeGalleryPermissions)
    }
  }

  fun onCameraClick() {
    val newFileName = currentDate

    state.thumbFileName = newFileName
    androidImageManager?.requestImageFromCamera(newFileName, requestCodeCameraPhoto)
  }

  override fun onSaveInstanceState(outState: Bundle?) {
    state.save(outState)
    super.onSaveInstanceState(outState)
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)

    state.thumbFileName?.let {
      androidImageManager?.handleOnActivityResult(ActivityResult(requestCode, data), it, 400, 400)
    }
  }

  /**
   * Handle both [AndroidImageManager.requestImageFromCamera] and
   * [AndroidImageManager.requestImageFromGallery] responses with the same function, it doesnt matter
   * if it comes from gallery or camera, we need to set it to the view (or do something with the path).
   */
  private fun waitForImagePath() {
    androidImageManager?.results?.subscribeOn(Schedulers.io())
        ?.observeOn(AndroidSchedulers.mainThread())
        ?.subscribe({ decodedImagePath ->
          resultImage.setImageBitmap(BitmapFactory.decodeFile(decodedImagePath))
        }, {
          manageError(it)
        })
  }

  fun manageError(throwable: Throwable?) {
    Log.e("android-image-picker", "Error getting image", throwable)
  }

  fun showImagePermissionRationale() {
    Toast.makeText(this, "We need the permissions because of this bla bla bla", Toast.LENGTH_LONG)
        .show()
  }

}

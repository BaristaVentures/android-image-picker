package com.barista_v.image_picker.sample

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.barista_v.image_picker.ActivityResult
import com.barista_v.image_picker.AndroidImageManager
import com.barista_v.image_picker.sample.utils.ReportState
import kotlinx.android.synthetic.main.activity_sample.*


class SampleActivity : AppCompatActivity(), SampleBaseView {
  val presenter = SamplePresenter()
  var presenterState: ReportState? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_sample)

    val newState = ReportState(savedInstanceState)
    presenter.attach(this, AndroidImageManager(this, BuildConfig.APPLICATION_ID), newState)
    presenterState = newState

    // Using kotlin extensions...
    cameraButton.setOnClickListener { presenter.onCameraButtonClick() }
    galleryButton.setOnClickListener { presenter.onGalleryButtonClick() }
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    presenter.onActivityResult(ActivityResult(requestCode, data))
  }

  override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    presenter.onActivityResult(ActivityResult(requestCode, null))
  }

  override fun onSaveInstanceState(outState: Bundle?) {
    super.onSaveInstanceState(outState)
    presenterState?.save(outState) // Save the image name (since its dynamic and get at runtime)
  }

  override fun showImagePermissionRationale(requestCodeCameraPermissions: Any) {
    Toast.makeText(this, "We need the permissions because of this bla bla bla", Toast.LENGTH_LONG)
        .show()
  }

  override fun showImage(path: String) {
    resultImage.setImageBitmap(BitmapFactory.decodeFile(path))
  }

}

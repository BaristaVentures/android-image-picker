package com.barista_v.image_picker.sample

import com.barista_v.image_picker.ActivityResult
import com.barista_v.image_picker.AndroidImageManager
import com.barista_v.image_picker.sample.utils.ReportState
import com.barista_v.image_picker.sample.utils.extensions.nowString
import java.io.File

class SamplePresenter {
  val requestCodeCameraPermissions = 1
  val requestCodeGalleryPermissions = 2

  val requestCodeCameraPhoto = 3
  val requestCodeGalleryPhoto = 4

  var view: SampleBaseView? = null
  var mAndroidImageManager: AndroidImageManager? = null
  var state: ReportState? = null

  fun attach(view: SampleBaseView, imageManager: AndroidImageManager, state: ReportState) {
    this.view = view
    this.mAndroidImageManager = imageManager
    this.state = state

    if (state.userIsPickingImage) {
      doSomething(imageManager.results)
    }
  }

  fun onGalleryButtonClick() {
    state?.thumbFileName = java.util.Calendar.getInstance().nowString()

    if (mAndroidImageManager?.isPermissionGranted() == true) {
      sendImageFromGallery()
    } else if (mAndroidImageManager?.shouldShowPermissionRationale() == true) {
      view?.showImagePermissionRationale(requestCodeGalleryPermissions)
    } else {
      mAndroidImageManager?.requestPermission(requestCodeGalleryPermissions)
    }
  }

  fun onCameraButtonClick() {
    state?.thumbFileName = java.util.Calendar.getInstance().nowString()

    if (mAndroidImageManager?.isPermissionGranted() == true ||
        mAndroidImageManager?.shouldAskForCameraPermissions == false) {
      sendImageFromCamera()
    } else if (mAndroidImageManager?.shouldShowPermissionRationale() ?: false) {
      view?.showImagePermissionRationale(requestCodeCameraPermissions)
    } else {
      mAndroidImageManager?.requestPermission(requestCodeCameraPermissions)
    }
  }

  fun onActivityResult(activityResult: ActivityResult) {
    state?.thumbFileName?.let {
      mAndroidImageManager?.handleOnActivityResult(activityResult, it, 400, 400)
    }
  }

  private fun sendImageFromGallery() {
    doSomething(mAndroidImageManager?.requestImageFromGallery(requestCodeGalleryPhoto))
  }

  private fun sendImageFromCamera() {
    state?.thumbFileName?.let {
      doSomething(mAndroidImageManager?.requestImageFromCamera(it, requestCodeCameraPhoto))
    }
  }

  private fun doSomething(imageObservable: rx.Observable<File>?) {
    state?.userIsPickingImage = true

    imageObservable?.subscribeOn(rx.schedulers.Schedulers.io())
        ?.observeOn(rx.android.schedulers.AndroidSchedulers.mainThread())
        ?.subscribe({ view?.showImage(it) }, { manageError(it) })
  }

  private fun manageError(error: Throwable) {

  }

}

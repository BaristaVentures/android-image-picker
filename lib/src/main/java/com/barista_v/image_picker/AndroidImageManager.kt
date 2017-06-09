package com.barista_v.image_picker

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import com.barista_v.image_picker.extensions.saveInFile
import rx.Observable
import rx.subjects.BehaviorSubject
import rx.subjects.Subject
import java.io.File
import java.lang.ref.WeakReference

/**
 * Sample (change quality and size) images obtained from the gallery/camera intent.
 *
 * You need to call [handleOnActivityResult] from the activity.
 */
open class AndroidImageManager(activity: Activity, val applicationPackage: String) {
  private var weakActivity = WeakReference(activity)
  private val permissionOwner = PermissionOwner(activity)
  private val storageDir: File? by lazy {
    activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
  }

  private val isExternalStorageWritable: Boolean
    get() = Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()

  var results: Subject<String, String> = BehaviorSubject.create<String>()
  var format = Bitmap.CompressFormat.JPEG
  var quality = 80

  /**
   * From SDK 18 (kitkat) you dont need to ask user permissions for
   * #android.Manifest.permission.WRITE_EXTERNAL_STORAGE
   */
  val isCameraPermissionsNeeded: Boolean
    get() = Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT

  /**q
   * Added camera permission because it creates a conflict if the app have camera permission
   * even if its in other place of the app.
   *
   * "Note: if you app targets M and above and declares as using the CAMERA permission
   * which is not granted, then try to use this action will result in a SecurityException."
   */
  val permissions = arrayOf(WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE)

  /**
   * Use #shouldAskForCameraPermissions to check if this method really needs a permission or not
   *
   * @return Observable with the result file path
   */
  //  @RequiresPermission(WRITE_EXTERNAL_STORAGE)
  open fun requestImageFromCamera(resultImageName: String, requestCode: Int): Observable<String> {
    if (isExternalStorageWritable) {
      weakActivity.get()?.let {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        if (takePictureIntent.resolveActivity(it.packageManager) != null) {
          val imageUri = getCameraImageUri(it, resultImageName)

          it.grantUriPermission(applicationPackage, imageUri,
              Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)

          takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
          takePictureIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION

          it.startActivityForResult(takePictureIntent, requestCode)
        }
      }
    } else {
      results.onError(Throwable("External storage is not available at the moment."))
      completeResults()
    }

    return results.asObservable()
  }

  //  @RequiresPermission(allOf = arrayOf(WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE))
  /**
   * @return Observable with the result file path
   */
  fun requestImageFromGallery(requestCode: Int): Observable<String> {
    if (isExternalStorageWritable) {
      val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
      weakActivity.get()?.startActivityForResult(intent, requestCode)
    } else {
      results.onError(Throwable("External storage is not available at the moment."))
    }

    return results.asObservable()
  }

  fun isPermissionGranted(): Boolean = permissionOwner.isPermissionGranted(permissions)

  fun shouldShowPermissionRationale(): Boolean = permissionOwner.shouldShowPermissionRationale(permissions)

  fun requestPermission(requestCodeGalleryPermissions: Int) =
      permissionOwner.requestPermission(permissions, requestCodeGalleryPermissions)

  open fun handleOnActivityResult(result: ActivityResult, imageName: String, width: Int, height: Int) {
    if (!isExternalStorageWritable) {
      results.onError(Throwable("External storage is not available at the moment."))
      completeResults()
      return
    }

    weakActivity.get()?.let { activity ->
      try {
        val imageUri = getCameraImageUri(activity, imageName)
        val sourceImage = readImageFileFromGallery(result.data) ?: readImageFileFromCamera(imageUri)

        val destinationFile = createInternalFile("$imageName.${format.name}")

        val bitmap = sourceImage.resizeRotatedBitmap(width, height)
        bitmap?.saveInFile(destinationFile, format, quality)?.let {
          results.onNext(it.absolutePath)
          completeResults()

          activity.revokeUriPermission(imageUri,
              Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        if (bitmap == null) {
          results.onError(Throwable("Image  $imageName.${format.name} could not be saved."))
          completeResults()
        }
      } catch (e: Exception) {
        results.onError(e)
        completeResults()
      }
    } ?: completeResults()
  }

  /**
   * Complete and init the observer so it can handle more items.
   */
  private fun completeResults() {
    results.onCompleted()
    results = BehaviorSubject.create<String>()
  }

  /**
   * Find the shareable Uri for an image with name.
   */
  private fun getCameraImageUri(context: Context, imageName: String): Uri {
    val cameraFile = createCameraFile("$imageName.${format.name}")

    return FileProvider.getUriForFile(context, "$applicationPackage.provider", cameraFile)
  }

  /**
   * Get the image path from an [Intent.ACTION_PICK] intent action created with
   * [MediaStore.Images.Media.EXTERNAL_CONTENT_URI]

   * @param data intent received in [Activity.onActivityResult]
   * *
   * @return image path
   * *
   * @see MediaStore.ACTION_IMAGE_CAPTURE
   * @see MediaStore.Images.Media.EXTERNAL_CONTENT_URI
   */
  private fun readImageFileFromGallery(resultIntent: Intent?): InternalImage? {
    return weakActivity.get()?.contentResolver?.let { contentResolver ->
      val columns = arrayOf(MediaStore.Images.Media.DATA, MediaStore.Images.ImageColumns.ORIENTATION)
      var path: String? = null
      var orientation: Int = 0

      resultIntent?.data?.let { uri ->
        contentResolver.query(uri, columns, null, null, null)?.apply {
          moveToFirst()

          path = getString(getColumnIndex(columns[0]))
          orientation = getInt(getColumnIndex(columns[1]))

          close()
        }

        return InternalImage(path, orientation)
      }
    }
  }

  private fun readImageFileFromCamera(imageUri: Uri): InternalImage {
    return InternalImage(createCameraFile(imageUri.lastPathSegment).absolutePath)
  }

  private fun createCameraFile(fullFileName: String) = File(storageDir, fullFileName)

  private fun createInternalFile(fileName: String) = File(storageDir, fileName).apply {
    mkdirs()
  }
}


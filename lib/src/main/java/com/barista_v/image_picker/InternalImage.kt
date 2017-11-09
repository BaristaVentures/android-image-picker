package com.barista_v.image_picker

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.support.media.ExifInterface
import android.util.Log
import com.barista_v.image_picker.extensions.orientation

/**
 * Image stored inside internal storage.
 *
 * - orientation: between 0 and 360 degrees of rotation if rotated.
 * - path: path of the image.
 *
 */
class InternalImage(var path: String?, var orientation: Int = -1) {
  init {
    if (orientation == -1) {
      try {
        orientation = path?.let { ExifInterface(it).orientation } ?: -1
      } catch (exception: Exception) {
        Log.e("InternalImage", "Getting exit interface info", exception)
      }
    }
  }

  /**
   * Return the image not rotated, and if it was rotated then it "re fixRotation" it to have 0 rotation.
   */
  fun resizeRotatedBitmap(width: Int, height: Int): Bitmap? {
    return path?.let {
      return resize(it, width, height)?.let { fixRotation(it) }
    }
  }

  fun fixRotation(bitmap: Bitmap): Bitmap? {
    var resultBitmap = bitmap

    if (orientation != 0) {
      val matrix = Matrix()

      matrix.setRotate(orientation.toFloat(), (bitmap.width / 2).toFloat(),
          (bitmap.height / 2).toFloat())

      try {
        resultBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
      } catch (e: Exception) {
        Log.e("InternalImage", "Error rotating bitmap", e)
      }
    }

    return resultBitmap
  }

  fun resize(path: String, width: Int, height: Int): Bitmap? {
    val options = BitmapFactory.Options()
    options.inJustDecodeBounds = true
    BitmapFactory.decodeFile(path, options)

    options.inSampleSize = calculateInSampleSize(options, width, height)

    options.inJustDecodeBounds = false
    return BitmapFactory.decodeFile(path, options)
  }

  // src = http://developer.android.com/training/displaying-bitmaps/load-bitmap.html
  fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
    // Raw height and width of image
    val height = options.outHeight
    val width = options.outWidth
    var inSampleSize = 1

    val newSizeIsSmaller = height > reqHeight || width > reqWidth
    val imageWasDecoded = options.outHeight != -1 && options.outWidth != -1

    if (imageWasDecoded && newSizeIsSmaller) {
      val halfHeight = height / 2
      val halfWidth = width / 2

      // Calculate the largest inSampleSize value that is a power of 2 and keeps both
      // height and width larger than the requested height and width.
      while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
        inSampleSize *= 2
      }
    }

    return inSampleSize
  }
}
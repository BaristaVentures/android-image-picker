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
        orientation = ExifInterface(path).orientation
      } catch(exception: Exception) {
        Log.e("InternalImage", "Getting exit interface info", exception)
      }
    }
  }

  /**
   * Return the image not rotated, and if it was rotated then it "re rotate" it to have 0 rotation.
   */
  fun resizeRotatedBitmap(width: Int, height: Int): Bitmap? {
    return path?.let {
      return resize(it, width, height)?.let { rotate(it) }
    }
  }

  fun rotate(bitmap: Bitmap): Bitmap? {
    var resultBitmap = bitmap

    if (orientation != 0) {
      val matrix = Matrix()

      matrix.setRotate(orientation.toFloat(), (bitmap.width / 2).toFloat(),
          (bitmap.height / 2).toFloat())

      try {
        resultBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
      } catch(e: Exception) {
        Log.e("InternalImage", "Error rotating bitmap", e)
      }
    }

    return resultBitmap
  }

  // src = http://developer.android.com/training/displaying-bitmaps/load-bitmap.html
  fun resize(path: String, width: Int, height: Int): Bitmap? {
    val options = BitmapFactory.Options()
    options.inJustDecodeBounds = true
    BitmapFactory.decodeFile(path, options)

    var newSampleSize = 1
    val imageWasDecoded = options.outHeight != -1 && options.outWidth != -1
    val newSizeIsSmaller = (height > options.outHeight || width > options.outWidth)

    if (imageWasDecoded && newSizeIsSmaller) {
      val halfHeight = height / 2
      val halfWidth = width / 2

      // Calculate the largest inSampleSize value that is a power of 2 and keeps both
      // height and width larger than the requested height and width.
      // Optimized with >= instead of > based on http://stackoverflow.com/a/28927163/273119
      while ((halfHeight / newSampleSize) >= options.outHeight &&
          (halfWidth / newSampleSize) >= options.outWidth) {
        if (newSampleSize * 2 > 0) {
          newSampleSize *= 2
        }
      }
    }

    options.inSampleSize = if (newSampleSize == 0) 1 else newSampleSize

    // Decode bitmap with inSampleSize set
    options.inJustDecodeBounds = false

    return BitmapFactory.decodeFile(path, options)
  }
}
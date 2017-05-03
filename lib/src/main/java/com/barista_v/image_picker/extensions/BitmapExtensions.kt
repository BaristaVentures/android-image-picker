package com.barista_v.image_picker.extensions

import android.graphics.Bitmap
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * Save the bitmap in File, ovewriting the previous file in destFile.
 * @throws IOException
 * @param quality [1, 100]
 */
@Throws(IOException::class)
fun Bitmap.saveInFile(destFile: File, format: Bitmap.CompressFormat, quality: Int): File? {
  var exception: IOException? = null
  var out: FileOutputStream? = null

  try {
    destFile.safeDelete()
    out = FileOutputStream(destFile, false)
    compress(format, quality, out)
    out.flush()
  } catch (e: IOException) {
    exception = e
    Log.e("AndroidImagePicker", "Error trying to flush FileOutputStream for image", e)
  }

  try {
    out?.close()
  } catch (e: IOException) {
    exception = e
    Log.e("AndroidImagePicker", "Error trying to close FileOutputStream for image", e)
  }

  if (exception == null) {
    return destFile
  } else {
    throw exception
  }
}


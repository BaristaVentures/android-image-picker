package com.barista_v.image_picker.extensions

import android.support.media.ExifInterface
import android.support.media.ExifInterface.ORIENTATION_UNDEFINED
import android.support.media.ExifInterface.TAG_ORIENTATION

val ExifInterface.orientation: Int
  get() = when (getAttributeInt(TAG_ORIENTATION, ORIENTATION_UNDEFINED)) {
    ExifInterface.ORIENTATION_ROTATE_90 -> 90
    ExifInterface.ORIENTATION_ROTATE_180 -> 180
    ExifInterface.ORIENTATION_ROTATE_270 -> 270
    else -> 0
  }
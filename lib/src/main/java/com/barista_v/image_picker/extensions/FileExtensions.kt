package com.barista_v.image_picker.extensions

import java.io.File

fun File.safeDelete() {
  if (exists()) {
    delete()
  }
}
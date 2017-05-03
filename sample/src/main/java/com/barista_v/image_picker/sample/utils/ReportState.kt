package com.barista_v.image_picker.sample.utils

import android.os.Bundle


/**
 * Handle restore/save of instance state
 */
class ReportState(bundle: Bundle?) {
  var thumbFileName = bundle?.getString("user.thumb.name")
  var userIsPickingImage = bundle?.getBoolean("user.thumb.picking") ?: false

  fun save(bundle: Bundle?): Bundle? {
    thumbFileName?.let { bundle?.putString("user.thumb.name", it) }
    bundle?.putBoolean("user.thumb.picking", userIsPickingImage)
    return bundle
  }

}
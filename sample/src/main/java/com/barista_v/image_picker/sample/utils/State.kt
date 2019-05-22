package com.barista_v.image_picker.sample.utils

import android.os.Bundle


/**
 * Handle restore/save of instance state "easy"
 */
class State(bundle: Bundle?) {
  var thumbFileName = bundle?.getString("user.thumb.name")

  fun save(bundle: Bundle?): Bundle? {
    thumbFileName?.let { bundle?.putString("user.thumb.name", it) }
    return bundle
  }

}
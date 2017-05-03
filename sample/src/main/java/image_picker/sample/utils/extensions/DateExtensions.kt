package image_picker.sample.utils.extensions

import java.text.SimpleDateFormat
import java.util.*

fun Calendar.nowString(format: String = "yyyy-MM-dd'T'HH_mm_ss_SSS'Z'", timeZone: String = "UTC")
    : String {
  time = Date()

  val dateFormatter = SimpleDateFormat(format)
  dateFormatter.timeZone = TimeZone.getTimeZone(timeZone)

  return dateFormatter.format(time)
}
# Sample

Some comments about the sample:

1. There are some steps in order to get images:
  * Do I have permissions to create the resampled image?
      * If `yes`: call `AndroidImageManager.requestImageFrom*` for gallery or camera based on user input.
      * If `not`: request permissions.
  * Show Rationale to user
  * Request Permissions

2. You need to create an `image name` for each image (we use current date in the sample).

__NOTE__ Its important to save some variables on `AppCompatActivity.onRestoreInstanceState` like the image name
because sometimes Android kills your activity launching the camera or a gallery app to get the image from.
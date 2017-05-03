# Android Image Picker

Ask for gallery/camera images "easy"

## Usage

Released on [Barista Archiva](http://archiva.barista-v.com:8080)

Add to your gradle file:

```
repositories {
  maven {
    url 'http://archiva.barista-v.com:8080/repository/internal/'
    credentials {
      username "${NEXUS_USERNAME}"
      password "${NEXUS_PASSWORD}"
    }
  }
}
  
dependencies {
  compile 'com.barista_v:image_picker:0.1.0'
}
```

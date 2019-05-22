#!/usr/bin/env bash
#
# Assemble the app, backup it and upload the apks to crashlytics
#

bash gradlew clean build lib:uploadArchives --daemon -x androidJavadocs

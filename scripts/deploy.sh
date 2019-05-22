#!/usr/bin/env bash
#
# Assemble the app, backup it and upload the apks to crashlytics
#

gradlew clean build lib:uploadArchives --daemon -x androidJavadocs
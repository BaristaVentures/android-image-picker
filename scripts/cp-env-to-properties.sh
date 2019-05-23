#!/usr/bin/env bash
#
# Prepare the project to be built from a server, creating the needed files from env variables.
#
# - Copy env variables to app module gradle properties file.
# - Decode variable names with "_BARISTA_DOT_" as ".".
# - Decode from base64 the secring.gpg
#

set +x // Hide all output

ENV_VARIABLES=$(printenv | tr ' ' '\n')

for PROPERTIES_FILE in gradle.properties lib/bintray.properties
do
  # Decode variables named like 'sign_BARISTA_DOT_password=123' to 'sign.password=123'.
  # Some ci servers like circle-ci don't support env variable names with a '.'
  echo ${ENV_VARIABLES//_BARISTA_DOT_/.} >> $PROPERTIES_FILE
done

set -x
#!/usr/bin/env bash
#
# Prepare the project to be built from a server, creating the needed files from env variables.
#
# - Copy env variables to app module gradle properties file.
# - Decode variable names with "_BARISTA_DOT_" as ".".
# - Decode from base64 the secring.gpg
#

set +x // Hide all output

PROPERTIES_FILE_PATH=gradle.properties
ENV_VARIABLES=$(printenv | tr ' ' '\n')

echo "\n" >> ${PROPERTIES_FILE_PATH}
printenv | tr ' ' '\n' >> ${PROPERTIES_FILE_PATH}

set -x
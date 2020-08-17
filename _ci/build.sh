#!/usr/bin/env bash

echo "=========================== Starting Build&Test Script ==========================="
PS4="\[\e[35m\]+ \[\e[m\]"
set -vex
pushd "$(dirname "${BASH_SOURCE[0]}")/../"

export AWS_ACCESS_KEY_ID=${CREATE_BUCKET_AWS_ACCESS_KEY}
export AWS_SECRET_ACCESS_KEY=${CREATE_BUCKET_AWS_SECRET_KEY}

export BUCKET_NAME="travis-lambda-java-utils-${TRAVIS_BUILD_NUMBER}-${TRAVIS_JOB_NUMBER}"

mvn -B -U clean install \
  -Dbuildnumber=${TRAVIS_BUILD_NUMBER}


popd
set +vex
echo "=========================== Finishing Build&Test Script =========================="
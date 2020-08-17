#!/usr/bin/env bash

echo "=========================== Starting Cleanup Script ==========================="
PS4="\[\e[35m\]+ \[\e[m\]"
set -vx
pushd "$(dirname "${BASH_SOURCE[0]}")/../"


# Stop and remove the containers
docker ps -a -q | xargs -l -r docker stop
docker ps -a -q | xargs -l -r docker rm

sudo apt-get update
sudo apt-get install -q -y awscli
printf "${CREATE_BUCKET_AWS_ACCESS_KEY}\n${CREATE_BUCKET_AWS_SECRET_KEY}\n\n\n" | aws configure

export BUCKET_NAME="travis-lambda-java-utils-${TRAVIS_BUILD_NUMBER}-${TRAVIS_JOB_NUMBER}"

aws s3 ls | awk '{print $3}' | grep "^${BUCKET_NAME}" | xargs -l -r -I{} aws s3 rb "s3://{}" --force


popd
set +vx
echo "=========================== Finishing Cleanup Script =========================="
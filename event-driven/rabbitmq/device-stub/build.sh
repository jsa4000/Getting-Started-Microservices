#!/usr/bin/env bash

set -e
# Build project and create Dockerfile
gradle jibExportDockerContext

# Build the docker image
docker build ./build/jib-docker-context -t devices-stub:latest
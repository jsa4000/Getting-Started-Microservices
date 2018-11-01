#!/usr/bin/env bash

set -e
# Build project and create docker image
gradle builDockerLocal

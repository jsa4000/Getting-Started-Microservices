#!/usr/bin/env bash
echo "- Updating Linux Distribution"
apt-get update -y
apt-get upgrade -y
echo "- Installing 7Zip"
apt-get install p7zip-full -y
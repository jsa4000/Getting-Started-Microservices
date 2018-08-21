#!/usr/bin/env bash
echo "- Updating Linux Distribution"
apt-get update -y
apt-get upgrade -y

echo "- Disable unwanted updates"
systemctl disable apt-daily.service
systemctl disable apt-daily.timer

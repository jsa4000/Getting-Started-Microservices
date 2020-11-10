#!/usr/bin/env bash

echo "Checking for pip"
pip3 -V

echo "Installing Selenium Python package..."
pip3 install selenium

echo "Running UI test using Selenium..."
python3 experience_test.py

#!/usr/bin/env python
import os
import shutil


PROJECT_DIRECTORY = os.path.realpath(os.path.curdir)

def remove_dir(dirPath):
    shutil.rmtree(os.path.join(PROJECT_DIRECTORY, dirPath))

def remove_file(filePath):
    os.remove(os.path.join(PROJECT_DIRECTORY, filePath))

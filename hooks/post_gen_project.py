#!/usr/bin/env python
import os
import shutil


PROJECT_DIRECTORY = os.path.realpath(os.path.curdir)

def remove_dir(dirPath):
    shutil.rmtree(os.path.join(PROJECT_DIRECTORY, dirPath))

def remove_file(filePath):
    os.remove(os.path.join(PROJECT_DIRECTORY, filePath))

if __name__ == '__main__':
	if '{{ cookiecutter.bamboo.create_bamboo_specs }}' != 'True':
            remove_dir('bamboo-specs')

	if '{{ cookiecutter.sonar.create_sonar_file }}' != 'True':
            remove_file('sonar-project.properties')

	if '{{ cookiecutter.jetpack.create_jetpack_file }}' != 'True':
            remove_file('jetpack.yml')

#!/bin/bash
#
# script to generate the CCU addon package.

# generate tempdir
mkdir -p build
rm -rf build/*

# copy all addon specific stuff
mkdir -p build/addon
cp -a ccu2 build/addon/
cp -a ccurm build/addon/
cp -a rc.d build/addon/
cp -a www build/addon/
cp -a ccu-historian-sample.config build/addon/
cp -a ccu-historian_addon.cfg build/addon

# copy ccu-historian stuff
cp -a ../src/main/dist build/ccu-historian

# delete unwanted ccu-historian stuff
rm -f build/ccu-historian/CCU-Historian_Kurzanleitung.pdf
rm -f build/ccu-historian/ccu-historian.exe

# copy update_script and VERSION info to root
cp -a update_script build/
cp -a VERSION build/

# generate archive
cd build
tar --owner=root --group=root --exclude=ccu-historian-addon-*tar.gz -czvf ccu-historian-addon-$(cat ../VERSION).tar.gz *

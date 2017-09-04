#!/bin/bash
#
# script to generate the CCU addon package.

# generate tempdir
mkdir -p tmp
rm -rf tmp/*

# copy all addon specific stuff
mkdir -p tmp/addon
cp -a addon/ccu2 tmp/addon/
cp -a addon/ccurm tmp/addon/
cp -a addon/rc.d tmp/addon/
cp -a addon/www tmp/addon/
cp -a addon/ccu-historian.config tmp/addon/
cp -a addon/ccu-historian_addon.cfg tmp/addon

# copy ccu-historian stuff
cp -a ccu-historian tmp/

# delete unwanted ccu-historian stuff
rm -f tmp/ccu-historian/CCU-Historian_Kurzanleitung.pdf
rm -f tmp/ccu-historian/ccu-historian.exe

# copy update_script and VERSION info to root
cp -a addon/update_script tmp/
cp -a VERSION tmp/

# generate archive
cd tmp
tar --owner=root --group=root -czvf ../ccu-historian-addon-$(cat ../VERSION).tar.gz *
cd ..
rm -rf tmp

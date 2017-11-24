#!/bin/bash

VERSION=$(curl -s https://api.github.com/repos/mdzio/ccu-historian/releases/latest |jq -r ".tag_name")
echo "Version=$VERSION"

URL=$(curl -s https://api.github.com/repos/mdzio/ccu-historian/releases/latest |jq -r ".assets[].browser_download_url" |grep .zip)
echo "Download URL=$URL"

TMPFILE=tmp.zip
TMPFOLDER=tmp
DESTFOLDER=CCU-Historian
DESTFILE=package.tgz
PKGFILE=CCU-Historian-$VERSION.spk

export COPYFILE_DISABLE=true

# Download
curl -o $TMPFILE -L $URL

# Extract
mkdir $TMPFOLDER
cd $TMPFOLDER
7z x ../$TMPFILE
rm *.exe
rm *.pdf

#Copy Lizenz.txt
if [ ! -x iconv ]
then
  cp -fpv ../$DESTFOLDER/LICENSE.orig ../$DESTFOLDER/LICENSE
else
  iconv -f iso8859-1 -t utf-8 Lizenz.txt > ../$DESTFOLDER/LICENSE.tmp
  tr -d '\r' < ../$DESTFOLDER/LICENSE.tmp > ../$DESTFOLDER/LICENSE
  rm ../$DESTFOLDER/LICENSE.tmp
fi

# Dos2Unix for ccu-historian-sample.config
mv ccu-historian-sample.config ccu-historian-sample.config.orig
tr -d '\r' < ccu-historian-sample.config.orig > ccu-historian-sample.config
rm ccu-historian-sample.config.orig

# Copy ui files
cp -a ../$DESTFOLDER/ui ./

# Compress
tar cvfz ../$DESTFOLDER/$DESTFILE *

# Cleanup
cd ..
rm -r $TMPFILE $TMPFOLDER

# Create Package
cd $DESTFOLDER

# Update INFO
sed -i "s/version=.*/version=\"$VERSION\"/" ./INFO

if [ ! -x md5 ]
then
  MD5=`md5sum -b $DESTFILE | head -c 32`
else
  MD5=`md5 -q $DESTFILE`
fi
sed -i "s/checksum=.*/checksum=\"$MD5\"/" ./INFO

sed -i "s/beta=.*/beta=\"no\"/" ./INFO

tar cvfz ../$PKGFILE --exclude '.*' --exclude 'ui' *
rm $DESTFILE LICENSE

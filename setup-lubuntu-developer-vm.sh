#!/bin/bash

## Make sure ZLIB is in place
if [ $(dpkg-query -W -f='${Status}' zlib1g-dev 2>/dev/null | grep -c "ok installed") -eq 0 ]; then
  sudo apt-get install zlib1g-dev
else
  echo "zlib1g-dev already installed"
fi

## Install GraalVM 19.2.x (Based on OpenJDK 1.8.x)
. $SDKMAN_DIR/bin/sdkman-init.sh
if [ $(sdk list java | grep -c "installed  | 19.2.1-grl") -eq 0 ]; then
  sdk install java 19.2.1-grl
else
  echo "GraalVM 19.2.x already installed"
fi

## Configure GraalVM
if grep -q "export GRAALVM_HOME" ~/.profile; then
   echo "GraalVM already exists in ~/.profile" 
else
   echo "Appending export of GRAALVM_HOME to ~/.profile" 
   echo "export GRAALVM_HOME=\"/home/developer/.sdkman/candidates/java/19.2.1-grl\"" >> ~/.profile
   source ~/.profile   
   $GRAALVM_HOME/bin/gu install native-image
fi

echo "Setup successfully finished"

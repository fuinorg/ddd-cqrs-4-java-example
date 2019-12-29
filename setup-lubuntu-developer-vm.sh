#!/bin/bash

## Make sure hostname is set
MY_HOSTNAME=$(hostname)
if [ $(cat /etc/hosts | grep -c "$MY_HOSTNAME") -eq 0 ]; then
  echo "Adding $MY_HOSTNAME to /etc/hosts"
  sudo -- sh -c -e "echo '127.0.0.1   $MY_HOSTNAME' >> /etc/hosts"
else
  echo "Host '$MY_HOSTNAME' already found in /etc/hosts"
fi

## Make sure ZLIB is in place
if [ $(dpkg-query -W -f='${Status}' zlib1g-dev 2>/dev/null | grep -c "ok installed") -eq 0 ]; then
  sudo apt-get install zlib1g-dev
else
  echo "zlib1g-dev already installed"
fi

## Install GraalVM 19.2.x (Based on OpenJDK 1.8.x)
. $SDKMAN_DIR/bin/sdkman-init.sh
if [ $(sdk list java | grep -c "installed  | 19.2.1-grl") -eq 0 ]; then
  sdk update
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

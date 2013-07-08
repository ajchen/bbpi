#!/bin/sh

#for windows with cygwin
TOMCAT_HOME=/cygdrive/c/tomcat/apache-tomcat-6.0.36
export JAVA_OPTS="-Xmx1000M -Dbbpi.conf=c:/data/bbpi/conf/bbpi.cf -Dfile.encoding=UTF-8 "

#for linux
#TOMCAT_HOME=/local/apache-tomcat-6.0.35
#export JAVA_OPTS="-Xmx1000M -Dbbpi.conf=/data/bbpi/conf/bbpi.cf -Dfile.encoding=UTF-8 "

#exec
echo Calling Tomcat startup script...
$TOMCAT_HOME/bin/startup.sh


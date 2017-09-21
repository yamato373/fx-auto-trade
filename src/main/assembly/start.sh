#!/bin/sh

PID_PATH_NAME=/var/run/${project.artifactId}/${project.artifactId}.pid

DIR=`dirname $0`
cd $DIR

echo "Starting ${project.artifactId} ..."
if [ ! -f $PID_PATH_NAME ]; then
	nohup java -jar lib/${project.artifactId}.jar /tmp 2>> /dev/null >> /dev/null &
	echo $! > $PID_PATH_NAME
	echo "${project.artifactId} started ..."
else
	echo "${project.artifactId} is already running ..."
fi
#!/bin/sh

PID_PATH_NAME=/var/run/${project.artifactId}/${project.artifactId}.pid

DIR=`dirname $0`
cd $DIR

if [ -f $PID_PATH_NAME ]; then
	PID=$(cat $PID_PATH_NAME);
	echo "${project.artifactId} stoping ..."
	kill $PID;
	echo "${project.artifactId} stopped ..."
	rm $PID_PATH_NAME
else
	echo "${project.artifactId} is not running ..."
fi
#!/bin/sh

echo "Started"
echo "Started" >>/karchan/mud/data/audit.trail
echo "Started" >>/karchan/mud/data/bigfile
./mmserver
while (sleep 10) do
	echo "Restarted"
	echo "Restarted" >>/karchan/mud/data/audit.trail
	echo "Restarted" >>/karchan/mud/data/bigfile
	./mmserver
done
echo "Stopped"
echo "Stopped" >>/karchan/mud/data/audit.trail


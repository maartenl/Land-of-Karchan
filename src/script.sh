#!/bin/sh

echo "Started"
echo "Started" >>/karchan/mud/data/audit.trail
while (sleep 10) do
	echo "mmserver started again" >>/karchan/mud/data/bigfile
	./mmserver 
done
echo "Stopped"
echo "Stopped" >>/karchan/mud/data/audit.trail


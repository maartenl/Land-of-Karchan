#!/bin/sh

echo "Started `date`"
echo "Started `date`" >>/karchan/mud/data/audit.trail
echo "Started `date`" >>/karchan/mud/data/bigfile
./mmserver
while (sleep 1) do
	echo "Restarted `date`"
	echo "Restarted `date`" >>/karchan/mud/data/audit.trail
	echo "Restarted `date`" >>/karchan/mud/data/bigfile
	./mmserver
done
echo "Stopped `date`"
echo "Stopped `date`" >>/karchan/mud/data/audit.trail


#!/bin/sh

echo "Started"
echo "Started" >>/karchan/mud/data/audit.trail
./mmserver
while (sleep 10) do
	./mmserver
done
echo "Stopped"
echo "Stopped" >>/karchan/mud/data/audit.trail


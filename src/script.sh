#!/bin/sh

echo "Started"
echo "Started" >>/karchan/mud/data/audit.trail
while (sleep 10) do
	./mmserver > ./nohup.out
done
echo "Stopped"
echo "Stopped" >>/karchan/mud/data/audit.trail


#!/bin/sh

echo "Started"
while (sleep 10) do
	./mmserver > ./nohup.out
done
echo "Stopped"

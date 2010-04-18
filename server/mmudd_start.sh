#!/bin/bash
#
# mmudd_start.sh        Startup script for the Mmud
#
# description: The startup script for my mmud deamon that restarts
#              itself when things go very very wrong.
# processname: 
# config: /etc/httpd/conf/httpd.conf
# config: /etc/sysconfig/httpd
# pidfile: /var/run/httpd/httpd.pid
#

# paths and names and stuff
mudpath=/home/maartenl/mmud/server
prog=mmud

# start the darned thing
start() {
	echo "Initialising."
	COUNTER=1
	while [ $COUNTER -lt 40 ]; do
		echo -n $"Starting $prog for the $COUNTER time: "
		make run
		RETVAL=$?
		echo "Program $prog exited with $RETVAL."
		sleep 10
		let COUNTER=${COUNTER}+1
	done
	echo "Closing."
	return $RETVAL
}

# stopping has not been implemented
# stop it from the inside.
stop() {
        echo -n $"Stopping $prog: "
}

# See how we were called.
case "$1" in
  start)
        start
        ;;
  stop)
        stop
        ;;
  restart)
        stop
        start
        ;;
  *)
        echo $"Usage: $prog {start|stop|restart}"
        RETVAL=3
esac

exit $RETVAL



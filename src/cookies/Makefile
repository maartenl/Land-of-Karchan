#
#  Synaptic Microsystems Cookie API
#  By Sid Young  syoung@powerup.com.au
#
#  Released for personal use.
#
#You may wish to add -DNO_UNISTD and -DNO_SYSTEM to CFLAGS if
#your system lacks the /usr/include/unistd.h header file
#or the system() function, respectively. Non-unix systems
#will also definitely have different linker syntax.

CFLAGS=
CC=gcc
AR=ar
LIBS=-L./ -lcookies

all: libcookies.a ck_test setcookie getcookie

libcookies.a: cookies.o cookies.h
	rm -f libcookies.a
	$(AR) rc libcookies.a cookies.o


ck_test: ck_test.o libcookies.a
	gcc ck_test.o -o ck_test ${LIBS}

setcookie: setcookie.o libcookies.a
	gcc setcookie.o -o setcookie ${LIBS}

getcookie: getcookie.o libcookies.a
	gcc getcookie.o -o getcookie ${LIBS}

clean:
	rm -f *.o
	rm -f *~
	rm -f core
	rm -f libcookies.a
	rm -f ck_test


#include <stdio.h>
#include "cgi.h"

void main()
{
	initvars();

	puts("First run");
	putvar("COMMAND", "Ik+heb+een+probleempje");

	viewvars();

	puts("Second run");
	killvar("COMMAND");

	viewvars();

	removevars();
}

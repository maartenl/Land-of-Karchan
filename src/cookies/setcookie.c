#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include "cookies.h"
 
int main()
{
	char szBuffer[1024];

	time_t ltime;

	sms_FreeResources();
	sms_PickupCookies();
	if(ck_JarPresent() != SMS_OK_FLAG)
	{
		printf("Content-type: text/html\n\n");
		printf("<HTML>\n<HEADER>\n<TITLE>Error : Cookie Jar missing!</TITLE>\n</HEADER>\n\n"
		"<BODY BGCOLOR=#FFFFFF>Couldn't find cookie jar!!!\n\n");
		printf("</BODY>\n</HTML>");
		exit(0);
	}
	
	sms_SetDomain("www.karchan.org");
	sms_SetPath("/");
	sms_SetCookie("CUSTOMERS", "ACME");
	/* sms_DebugCookies(); */
	sms_WriteCookies();
	printf("Content-type: text/html\n");
	sms_FreeResources();
	printf("\n<HTML>\n<HEADER>\n<TITLE>Cookie Page!</TITLE>\n</HEADER>\n\n"
	"<BODY BGCOLOR=#FFFFFF>\n\n");
	printf("</BODY>\n</HTML>");

	return 0;
}

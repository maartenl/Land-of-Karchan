#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include "cookies.h"
 
int main()
{
char szBuffer[1024];

time_t ltime;

printf("Content-type: text/html\n");
printf("Set-Cookie: CUSTOMER=WILE_E_COYOTE; path=/; expires=Wednesday, 09-Nov-2000 23:12:40 GMT\n\n");
printf("<HTML>\n<HEADER>\n<TITLE>Cookie Page!</TITLE>\n</HEADER>\n\n"
"<BODY BGCOLOR=#FFFFFF>\n\n");


	sms_FreeResources();
	printf("Path is: %s\n",sms_GetPath());
	if(sms_PickupCookies()!= SMS_OK_FLAG )
	  printf("WARNING - Memory fault or no HTTP_COOKIES to collect\n");
	if(ck_JarPresent() == SMS_OK_FLAG)
	 printf("Jar is NOW present\n");
	 else
	  printf("Jar - error\n");

	sms_SetDomain("synaptic.com.au");
	sms_SetPath("/root");
	printf("Root path is: %s\n",sms_GetPath());

	printf("\nAttempt cookie fetch -");
	if(sms_GetCookie("NotPOssible") == NULL)
	  printf("Cookie fetch sent back NULL - [Correct]\n");
	else
	  printf("Cookie fetch sent back a value - [Wrong!]\n");

	if(sms_SetCookie("TEST1", "1234") != SMS_OK_FLAG)
	  printf("ERROR - Cannot add cookie\n");
	if(sms_GetCookie("TEST1") == NULL)
	  printf("Cookie fetch sent back NULL - [Wrong]\n");
	else
	  printf("Cookie fetch sent back a value - [Correct]\n");
	sms_SetCookie( "TEST2", "456");
	sms_SetCookie( "TEST3", "456");
	if(sms_GetCookie("TEST2") == NULL)
	  printf("Cookie fetch sent back NULL - [Wrong]\n");
	sms_ClrCookie("TEST2");
	sms_SetCookie( "TEST4", "456");
	sms_DebugCookies();
	sms_SetCookie( "TEST4", "4564");
	sms_ClrSessionFlag( "TEST4" );
	sms_SetExpireValue( "TEST4", time(&ltime) );

	printf("Test ck_ConvertTime() - %s\n\n", ck_ConvertTime( time(&ltime)));

	sms_DebugCookies();
	printf("\nWrite to stream - all modified cookies\n\n");
	sms_WriteCookies();
	printf("\nWrite to stream - done!\n");
	sms_FreeResources();
	printf("</BODY>\n</HTML>");

return 0;
}

/*PROGRAM  cookies.h */
/*TITLE Introduction */
/* Cookies Processor Program
 *
 * By Sid Young 10 January 1999
 * http://www.synaptic.com.au/
 *
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>
#include <time.h>
#include <unistd.h>
#include <fcntl.h>


/*
 *  Flag settings
 */
#define MAX_COOKIES				32

#define SMS_OK_FLAG				1
#define SMS_FAILURE_FLAG		0
#define SMS_MODIFIED_FLAG		1
#define SMS_SET_SESSION_FLAG	1
#define SMS_CLR_SESSION_FLAG	0


/* envFlag defines */

#define	SMS_SET_USER_FLAG		0
#define	SMS_SET_ENV_FLAG		1



static char *smsDomainPtr;
static char *smsPathPtr;

/*
 *  Structure to hold all cookie data.
 */

typedef struct cookiejar
{
	char name[1024];
	char value[1024];
	char path[1024];       /* Path set to sms_GetPath() by default */
	long expire;           /* UNIX long time format */
	char expireDate[64];   /* RESERVED - String of GMT Date format */
	int  sessionCookie;    /* 1=session cookie */
	int  envFlag;          /* cookie set environment, or by user */
	int  modified;         /* Entry in table has been modified */

	struct COOKIEJAR *next;
	struct COOKIEJAR *previous;

} COOKIEJAR;

static COOKIEJAR *FirstNode;




/*
 *  Main Processor Function
 */
int   sms_PickupCookies( void );

/*
 *  Raw Cookie Handling
 */
char *sms_GetCookie( char * );
int   sms_SetCookie( char *, char * );
int   sms_ClrCookie( char * );
int   sms_EmptyJar( void );
int   sms_IsCookieSet( char * );

/*
 *  Session Control Functions
 */
int   sms_SetSessionFlag( char * );
int   sms_ClrSessionFlag( char * );
int   sms_SetExpireValue( char *, long );
long  sms_GetExpireValue( char * );


/*
 * Path & Domain Functions 
 */
int   sms_SetDomain( char * );
char *sms_GetDomain( void );
void  sms_ClrDomain( void );
int   sms_SetPath( char * );
char *sms_GetPath( void );
void  sms_ClrPath( void );


void  sms_FreeResources( void );

/*
 *  Cookie Output Routines
 */
void sms_WriteCookies( void );
void sms_WriteCookiesEx( FILE * );

void sms_ExpireCookie( char * );

/*
 *  Cookie Debug Routines
 */
void sms_DebugCookies( void );
void sms_DebugCookiesEx( FILE * );



char ck_x2c( char * );
void ck_UnEscapeUrl( char * );
void ck_Plus2Space( char * );
void ck_RemoveSpaces( char * );
char *ck_ConvertTime( long );


COOKIEJAR *ck_FindEntry( char * );



/* end of file */

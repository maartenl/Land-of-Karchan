/*PROGRAM  cookies.c */
/*TITLE Introduction */
/*
 *  Cookies C API
 *
 * By Sid Young 28 December 1999
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

#include "cookies.h"

char *pWeekDay[] = {"Sun","Mon","Tue","Wed","Thu","Fri","Sat"};
char *pMonth[] = {NULL,
		"Jan","Feb","Mar","Apr",
		"May","Jun","Jul",
		"Aug","Sep","Oct","Nov","Dec"};






/*TITLE URL Decode Routines */
/*============================================================
 *
 *   ck_x2c         - convert hex num to char
 *   ck_UnEscapeUrl - convert string to original string
 *   ck_Plus2Space  - convert any + to space in string
 *
 *  Convert URL encoded string back to ascii
 */

char ck_x2c(char *what)
{
register char digit;
  
	digit=(what[0] >= 'A'?((what[0] & 0xdf)-'A')+10 : (what[0]-'0'));
	digit*= 16;
	digit+=(what[1] >= 'A' ? ((what[1] & 0xdf)-'A')+10 : (what[1]-'0'));
	return (digit);
}



/*
 *  Convert URL encoded string back to ascii
 */
void ck_UnEscapeUrl(char *url)
{
register int x,y;
  
	for(x=0, y=0; url[y]; ++x, ++y)
	 {
	  if((url[x] = url[y]) == '%')
	   {
		url[x] = ck_x2c(&url[y+1]);
		y+=2;
	   }
	 }
	url[x] = '\0';
}


/*
 *  Convert Plus char + to Space in the string
 */
void ck_Plus2Space(char *string)
{
register int x;
  
	for(x=0; string[x]; x++)
	 if(string[x] == '+') string[x] = ' ';
}





/*TITLE String routines */
/*============================================================
 *
 *  Strip spaces from string.
 */
void ck_RemoveSpaces(char *string )
{
char *sPtr;
	
	sPtr = string;
	while(*string)
	 {
	  if(*string != ' ')
	    *sPtr++ = *string++;
	  else
	    string++;
	 }
	*sPtr=0;
}


char *ck_ConvertTime( long lTimeToConvert )
{
static char szTimeBuff[64];
time_t lTimeNow;
struct tm *tmTimeNow;


	time(&lTimeNow);
	tmTimeNow=gmtime(&lTimeNow);

	sprintf( szTimeBuff,"%s, %02d-%s-%4d %02d:%02d:%02d GMT",
		pWeekDay[ tmTimeNow->tm_wday],
		tmTimeNow->tm_mday,
		pMonth[ tmTimeNow->tm_mon+1 ],
		tmTimeNow->tm_year+1900,
		tmTimeNow->tm_hour,
		tmTimeNow->tm_min,
		tmTimeNow->tm_sec );
	return( szTimeBuff );
}






/*TITLE  Cookie Jar Manipulation */
/*============================================================
 *
 *  Cookie Jar is a linked list. If it is NOT present then 
 *  create an initial entry.
 *
 *  If We cannot create Jar then memory error.
 *
 *
 *============================================================
 */
int ck_JarPresent( void )
{
	if( FirstNode != (void *) NULL) return( SMS_OK_FLAG );

	if((FirstNode=(COOKIEJAR *) malloc( sizeof(COOKIEJAR)))!=NULL)
	 {
	  smsPathPtr=NULL;
	  smsDomainPtr=NULL;
	  strcpy(FirstNode->name,"(C)1999 Synaptic Micro Systems");
	  strcpy(FirstNode->value,"Sid Young");
	  strcpy(FirstNode->path,"http://www.synaptic.com.au/");

	  FirstNode->envFlag = 0;
	  FirstNode->modified= 0;
	  FirstNode->sessionCookie=SMS_SET_SESSION_FLAG;

	  FirstNode->next =    NULL;
	  FirstNode->previous= NULL;

	  return( SMS_OK_FLAG );
	 }
	else 
	  return( SMS_FAILURE_FLAG );
}



/*TITLE Find an item in the Jar */
/*============================================================
 *
 *  FIND ENTRY - Attempt to scan the jar from start to end to
 *               find match by name.
 *
 *  Return a pointer to structure if success else return NULL
 *  if not found.
 *
 *============================================================
 */
COOKIEJAR *ck_FindEntry( char *szName )
{
COOKIEJAR *thisNode;
COOKIEJAR *nextNode;
unsigned int nCount=0;

	if(ck_JarPresent() != SMS_FAILURE_FLAG )
	 {
	  thisNode = (COOKIEJAR *) FirstNode;
	  while(thisNode)
	   {
#ifdef SMS_DEBUG_FLAG
		  printf("\nCount: %d  ", nCount++);
		  printf("Name: [%s]   Value: [%s]\n", thisNode->name, thisNode->value);
		  printf("Path: %s    Environment: %s   Modified: %s\n",
		  	thisNode->path,
			(thisNode->envFlag ? "NO" : "YES"),
		    (thisNode->modified? "NO" : "YES"));
#endif
		if( !strcmp( thisNode->name, szName )) 
		  return( (COOKIEJAR *) thisNode );
		nextNode=(COOKIEJAR *)thisNode->next;
		thisNode=nextNode;
	   }
	 }
	return NULL;
}



/*TITLE Add an Entry to Jar */
/*============================================================
 *
 *  ADD ENTRY - Find the cookie in the linked list, if not found
 *              add it to the end and link it into chain.
 *
 *  Return SMS_OK_FLAG if all ok else error (memory).
 *
 *============================================================
 */
int ck_AddEntry( char *szName, char *szValue, int flagEnv  )
{
COOKIEJAR *thisNode;
COOKIEJAR *nextNode;
COOKIEJAR *lastNode;
COOKIEJAR *newNode;
unsigned int nCount=0;

	if(ck_JarPresent() != SMS_FAILURE_FLAG )
	 {
	  thisNode = (COOKIEJAR *) FirstNode;
	  lastNode = thisNode;
	  while(thisNode)
	   {
#ifdef SMS_DEBUG_FLAG
		 {
		  printf("\nCount: %d  ", nCount++);
		  printf("Name: [%s]   Value: [%s]\n", thisNode->name, thisNode->value);
		  printf("Path: %s    Environment: %s   Modified: %s\n",
		  	thisNode->path,
			(thisNode->envFlag ? "No" : "Yes"),
		    (thisNode->modified? "No" : "Yes"));
#endif
		if( !strcmp( thisNode->name, szName )) 
		 {
		  strcpy(thisNode->value, szValue);
		  thisNode->modified = SMS_MODIFIED_FLAG;
		  thisNode->envFlag  = flagEnv;
		  thisNode->sessionCookie = SMS_SET_SESSION_FLAG;
		  strcpy( thisNode->path, sms_GetPath() );
		  return( SMS_OK_FLAG );
		 }
		lastNode=thisNode;
		nextNode=(COOKIEJAR *)thisNode->next;
		thisNode=nextNode;
	   }
/*
 * Add a new node to the end of "lastNode"
 * and flag as a session cookie
 */
      if((newNode=(COOKIEJAR *) malloc( sizeof(COOKIEJAR)))!=NULL)
	   {
	    lastNode->next = (struct COOKIEJAR *) newNode;
		newNode->previous= (struct COOKIEJAR *)lastNode;
		newNode->next = NULL;
		strcpy(newNode->name, szName);
		strcpy(newNode->value, szValue);
		newNode->modified=0;
		newNode->envFlag=flagEnv;
		newNode->sessionCookie = SMS_SET_SESSION_FLAG;
		return( SMS_OK_FLAG );
	   }
	 }
	return( SMS_FAILURE_FLAG );
}






/*============================================================
 *
 * PICKUP COOKIES - Collect all cookies from the environment
 *
 *
 *
 *============================================================
 */
int sms_PickupCookies(void)
{
char *envPtr, *cPtr, *tmpBuff, *tPtr, *namePtr, *valPtr;
char szName[1024];
char szValue[1024];
unsigned int j=0;

	envPtr=getenv("HTTP_COOKIE");
	if(envPtr==NULL)
	  return( SMS_FAILURE_FLAG );
	if((tmpBuff=(char *)malloc(4096)) != NULL)
	 {
	  cPtr = strtok(envPtr,";");
	  while(cPtr!=NULL)
	   {
	    j=0;
	    strcpy(tmpBuff,cPtr);
	    ck_RemoveSpaces(tmpBuff);
		namePtr=szName;
		tPtr=tmpBuff;
		while( *tPtr != '=' )
		 { *namePtr++=*tPtr++; *namePtr=0;  }
	    ck_Plus2Space( szName );
	    ck_UnEscapeUrl( szName );
		valPtr=szValue;
		tPtr++;
		while( *tPtr != 0 )
		 { *valPtr++=*tPtr++; *valPtr=0;  }
	    ck_Plus2Space( szValue );
	    ck_UnEscapeUrl( szValue );
		ck_AddEntry( szName, szValue, SMS_SET_ENV_FLAG);
	    cPtr=strtok(NULL,";");
	   }
	  free(tmpBuff);
	  return( SMS_OK_FLAG );
	 }
	else
	  return( SMS_FAILURE_FLAG );
}






/*TITLE Debug Routines */
/*============================================================
 *
 *  DEBUG ROUTINES
 *
 * sms_DebugCookies - Dumps to web browser
 *
 * sms_DebugCookiesEx - Dumps to FILE pointer
 *
 *============================================================
 */
void sms_DebugCookies( void )
{
	sms_DebugCookiesEx( stdout );
}




void sms_DebugCookiesEx( FILE *fptr)
{
COOKIEJAR *thisNode;
COOKIEJAR *nextNode;
unsigned int nCount=0;
time_t ltime;


	if(ck_JarPresent() != SMS_FAILURE_FLAG )
	 {
	  fprintf(fptr,"\n<p>Current UNIX time: <b>%ld</b><br>\n", time(&ltime));
	  fprintf(fptr,"\n<p>Local time: <b>%s</b><p>\n", ctime(&ltime));
	  fprintf(fptr,"\n<p>GMT time: <b>%s</b><p>\n", ck_ConvertTime(time(&ltime)));
	  thisNode = (COOKIEJAR *) FirstNode;
	  while(thisNode)
	   {
		  fprintf(fptr,"\n<hr>Count: <b>%d</b>  ", nCount++);
		  fprintf(fptr,"Name: [<b>%s</b>]   Value: [<b>%s</b>]<br>\n", thisNode->name, thisNode->value);
		  fprintf(fptr,"Path: <b>%s</b>    Environment: <b>%d</b>   Modified: <b>%d</b><br>\n",
		  	thisNode->path,
		  	thisNode->envFlag,
		  	thisNode->modified);
		  fprintf(fptr,"Session Flag: <b>%d</b>    Expiry: <b>%ld</b>   Expire Date: <b>%s</b><br>\n",
		    thisNode->sessionCookie,
			thisNode->expire,
			thisNode->expireDate);
		nextNode=(COOKIEJAR *)thisNode->next;
		thisNode=nextNode;
	   }
	 }
}






/*TITLE Write Routines */
/*============================================================
 *
 *  sms_WriteCookies -
 *
 *
 *
 *============================================================
 */
void sms_WriteCookies( void )
{
	sms_WriteCookiesEx( stdout );
}


void sms_WriteCookiesEx( FILE *fPtr )
{
COOKIEJAR *thisNode;
COOKIEJAR *nextNode;
unsigned int nCount=0;
char szExpireBuffer[128];



	if(ck_JarPresent() != SMS_FAILURE_FLAG )
	 {
	  thisNode = (COOKIEJAR *) FirstNode->next;
	  while(thisNode)
	   {
	    if((thisNode->modified != 0) || (thisNode->envFlag==0))
		 {
		  if(thisNode->path[0] == 0) strcpy(thisNode->path, sms_GetPath());

		  if(thisNode->sessionCookie == 1)
		   {
		    strcpy(szExpireBuffer,"");
		   }
		  else
		   {
		    sprintf(szExpireBuffer,
				"expires=%s; ",
				ck_ConvertTime( thisNode->expire ));
		   }
		  
		  if(sms_GetDomain() != NULL)
		    fprintf(fPtr,"Set-Cookie: %s=%s; %s domain=%s; path=%s\n",
		  	thisNode->name,
		  	thisNode->value,
			szExpireBuffer,
		  	sms_GetDomain(),
		  	thisNode->path);
		  else
		    fprintf(fPtr,"Set-Cookie: %s=%s; %s path=%s\n",
		  	thisNode->name,
		  	thisNode->value,
			szExpireBuffer,
		  	thisNode->path);
		 }
		nextNode=(COOKIEJAR *)thisNode->next;
		thisNode=nextNode;
	   }
	 }
}






/*TITLE Domain & Path String Handling */
/*============================================================
 *
 * RETURN SMS_FAILURE_FLAG - Unable to allocate memory
 *        SMS_OK_FLAG      - Memory allocated, domain saved
 */
int sms_SetDomain( char *szDomainString )
{
	sms_ClrDomain();
	if((smsDomainPtr = (char *) malloc( strlen(szDomainString) +1))==NULL)
	  return( SMS_FAILURE_FLAG );
	strcpy(smsDomainPtr, szDomainString);
	return( SMS_OK_FLAG );
}



char *sms_GetDomain( void )
{
	if( smsDomainPtr == NULL) return NULL;
	return smsDomainPtr;
}



void sms_ClrDomain( void )
{
	if( smsDomainPtr ) free( smsDomainPtr );
	smsDomainPtr=NULL;
}



int sms_SetPath( char *szPathString )
{
	sms_ClrPath();
	if((smsPathPtr = (char *) malloc( strlen(szPathString) +1))==NULL)
	  return( SMS_FAILURE_FLAG );
	strcpy(smsPathPtr, szPathString);
	return( SMS_OK_FLAG );
}



char *sms_GetPath( void )
{
	if(smsPathPtr == NULL) return("/");
	return( smsPathPtr );
}


void sms_ClrPath( void )
{
	if( smsPathPtr ) free( smsPathPtr );
	smsPathPtr=NULL;
}




void sms_FreeResources( void )
{
	sms_ClrPath();
	sms_ClrDomain();
}




/*TITLE Cookie Manipulation */
/*============================================================
 *
 *  sms_GetCookie - Return a string (the cookie value) given
 *                  it's name.
 *
 *============================================================
 */
char *sms_GetCookie( char *szName )
{
COOKIEJAR *thisEntry;

	if( ck_JarPresent() == SMS_OK_FLAG )
	 {
	  if((thisEntry = (COOKIEJAR *)ck_FindEntry( szName )) == NULL)
	    return NULL;
	  return( thisEntry->value );
	 }
	else
	  return NULL;
}




/*============================================================
 *
 *  sms_IsCookieSet - Return a 1 if the cookie is present. 
 *
 *============================================================
 */
int sms_IsCookieSet( char *szName )
{
COOKIEJAR *thisEntry;

	if( ck_JarPresent() == SMS_OK_FLAG )
	 {
	  if((thisEntry = (COOKIEJAR *)ck_FindEntry( szName )) == NULL)
	    return 0;
	  return 1;
	 }
	else
	  return 0;
}



/*============================================================
 *
 *  sms_ClrCookie - Move pointer chains around to remove this
 *                  link and free the structure up.
 *
 *============================================================
 */
int sms_ClrCookie( char *szName )
{
COOKIEJAR *ThisEntry;
COOKIEJAR *PrevEntry;
COOKIEJAR *NextEntry;

	if( ck_JarPresent() == SMS_OK_FLAG )
	 {
	  if((ThisEntry = (COOKIEJAR *)ck_FindEntry( szName )) == NULL)
	    return SMS_FAILURE_FLAG;
	  PrevEntry = (COOKIEJAR *)ThisEntry->previous;
	  NextEntry = (COOKIEJAR *)ThisEntry->next;
	  PrevEntry->next = (struct COOKIEJAR *) NextEntry;
	  if(NextEntry) NextEntry->previous= (struct COOKIEJAR *)PrevEntry;
	  free(ThisEntry);
	  return( SMS_OK_FLAG );
	 }
	else
	  return SMS_FAILURE_FLAG;
}




/*============================================================
 *
 *  sms_SetCookie - Set a cookie by Name and Value
 *
 *  Write an entry into the cookie jar if the cookie is not present
 *  else update an existing entry and set the modified flag on the entry.
 *
 *
 *============================================================
 */
int sms_SetCookie( char *szName, char *szValue )
{
	return(ck_AddEntry(szName, szValue, SMS_SET_USER_FLAG));
}



/*============================================================
 *
 *  sms_ExpireCookie - Expitre the cookie in the browser now.
 *
 *
 *============================================================
 */
void sms_ExpireCookie( char *szCookieName )
{
	printf("Set-Cookie: %s=0; expires=Sun 27-Dec-1999 01:01:01 GMT\n",
		szCookieName);
}






/*TITLE Session Control */
/*============================================================
 *
 *  sms_SetSessionFlag -  Find cookie and set flag
 *
 *
 *============================================================
 */
int sms_SetSessionFlag( char *szName )
{
COOKIEJAR *ThisNode;

	if( ck_JarPresent() == SMS_OK_FLAG )
	 {
	  if((ThisNode = (COOKIEJAR *)ck_FindEntry( szName )) == NULL)
	    return SMS_FAILURE_FLAG;
	  ThisNode->sessionCookie = SMS_SET_SESSION_FLAG;
	  ThisNode->modified = SMS_MODIFIED_FLAG;
	  return( SMS_OK_FLAG );
	 }
	else
	  return SMS_FAILURE_FLAG;
}



int sms_ClrSessionFlag( char *szName )
{
COOKIEJAR *ThisNode;

	if( ck_JarPresent() == SMS_OK_FLAG )
	 {
	  if((ThisNode = (COOKIEJAR *)ck_FindEntry( szName )) == NULL)
	    return SMS_FAILURE_FLAG;
	  ThisNode->sessionCookie = SMS_CLR_SESSION_FLAG;
	  ThisNode->modified = SMS_MODIFIED_FLAG;
	  return( SMS_OK_FLAG );
	 }
	else
	  return SMS_FAILURE_FLAG;
}



int sms_SetExpireValue( char *szName, long lExpireValue )
{
COOKIEJAR *ThisNode;

	if( ck_JarPresent() == SMS_OK_FLAG )
	 {
	  if((ThisNode = (COOKIEJAR *)ck_FindEntry( szName )) == NULL)
	    return SMS_FAILURE_FLAG;
	  ThisNode->modified = SMS_MODIFIED_FLAG;
	  ThisNode->expire = lExpireValue;
	  return( SMS_OK_FLAG );
	 }
	else
	  return SMS_FAILURE_FLAG;
}


long  sms_GetExpireValue( char *szName )
{
COOKIEJAR *ThisNode;

	if( ck_JarPresent() == SMS_OK_FLAG )
	 {
	  if((ThisNode = (COOKIEJAR *)ck_FindEntry( szName )) == NULL)
	    return SMS_FAILURE_FLAG;
	  return( ThisNode->expire );
	 }
	else
	  return SMS_FAILURE_FLAG;
}




/* end of file */

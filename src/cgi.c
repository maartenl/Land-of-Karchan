#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "cgi.h"

#undef DEBUG

VARIABLE first;

int putvar(char *key, char *value)
{
	VARIABLE *t, *v = &first;
	int	r = -1;

	while (v->next != NULL && (r = strcmp(((VARIABLE *)v->next)->key, key)) < 0) {
		v = v->next;
	}

	if (r != 0) {
		t = (VARIABLE *)malloc(sizeof(VARIABLE));
		t->next  = v->next;
		v->next  = (void *)t;
#ifdef DEBUG
		printf("Inserted %s in list\n", key);
#endif
	} else {
        t = v;
    }

	t->key   = strdup(key);
	t->value = strdup(value);
#ifdef DEBUG
	printf("Stored %s in %s\n", value, key);
#endif
}

int killvar(char *key)
{
	VARIABLE *t, *v = &first;

	while (v->next != NULL && strcmp(((VARIABLE *)v->next)->key, key) != 0) {
		v = v->next;
	}

	if (v->next != NULL) {
		/* kill that sucker */
		t = ((VARIABLE *)v->next)->next;
		free(v->next);
		v->next = t;
#ifdef DEBUG
		printf("Removed %s from list\n", key);
#endif
		return 1;
		/* NOT REACHED */
	}

	return 0;
}

char *getvar(char *key)
{
	VARIABLE *t = &first;

	while (t != NULL && strcmp(key, t->key) != 0) t = t->next;

	if (t) {
		return(t->value);
	} else {
		return(NULL);
	}
}

int initvars()
{
	first.key = first.value = first.next = NULL;
}

int viewvars()
{
	VARIABLE *t = first.next;

	while (t != NULL) {
		printf("%s = %s\n", t->key, t->value);
		t = t->next;
	}
}

int removevars()
{
	VARIABLE *r, *t = first.next;

	while (t != NULL) {
		r = t->next;
		free(t->key);
		free(t->value);
		free(t);
		t = r;
	}

	first.next = NULL;
}

static int htoi(char c)
{
	char h = toupper(c);

	return (h >= 'A' && h <= 'Z'? h - 'A' + 10: h - '0');
}

static char *beautifyline(char *buf, char *l)
{
	char *o = l, *n;

	n = buf;

	while (*o != '\0') {
		if (isnspecial(*o)) {
			*(n++) = *(o++);
		} else if (*o == '+') {
			*(n++) = ' ';
			o++;
		} else if (*o == '%') {
			*(n++) = htoi(*(o + 1)) | (htoi(*(o + 1)) << 4);
			o+=3;
		}
	}

	*n = '\0';

	return buf;
}

int ReadParse()
{
	char 	*in, *line, *key, *value;
	int	inmalloc = 0;
	long	l;

	if (strcmp(getenv("REQUEST_METHOD"), "GET") == 0) {
		in = strdup(getenv("QUERY_STRING"));
	} else if (strcmp(getenv("REQUEST_METHOD"), "POST") == 0) {
			in = malloc(l = atoi(getenv("CONTENT_LENGTH")));
			fread(in, sizeof(char), l, stdin);
		}

	line = strtok(in, "&");

	while (line != NULL) {
		*(value = strchr(line, '=')) = '\0';
		beautifyline(key = strdup(line), line);
		line = value + 1;
		beautifyline(value = strdup(line), line);
		putvar(key, value);
		free(key);
		free(value);
		line = strtok(NULL, "&");
	}

	free(in);
}

char *PrintHeader()
{
	return "Content-type: text/html\n\n";
}


typedef struct {
	char *key;
	char *value;

	void *next;
} VARIABLE;

extern VARIABLE first;

#define isnspecial(c) ((c) != '+' && (c) != '%' && (c) != '&')

int putvar(char *, char *);
int killvar(char *);
char *getvar(char *);
int initvars();
int viewvars();
int removevars();
int ReadParse();
char *PrintHeader();

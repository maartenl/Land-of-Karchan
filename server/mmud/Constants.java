/*-------------------------------------------------------------------------
cvsinfo: $Header: /karchan/mud/cvsroot/server/mmud/Constants.java,v 1.28 2005/09/28 04:33:50 karn Exp $
Maarten's Mud, WWW-based MUD using MYSQL
Copyright (C) 1998  Maarten van Leunen

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.

Maarten van Leunen
Appelhof 27
5345 KA Oss
Nederland
Europe
maarten_l@yahoo.com
-------------------------------------------------------------------------*/

package mmud;

import java.io.FileReader;
import java.io.File;
import java.io.IOException;
import java.io.*;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.logging.Handler;
import java.util.Vector;
import java.util.Iterator;
import java.util.Calendar;
import java.util.Collection;

import mmud.commands.*;
import mmud.database.Database;

/**
 * Used constants in the game. Constants might have been read from a
 * properties file, or be default values. Also contains some simple global
 * functions we can use.
 */
public final class Constants
{
	/**
	 * Default room where new users appear. In this case it is the cave.
	 * This should be inline with the database setting.
	 */
	public final static int DEFAULT_ROOM = 1;
	
	/**
	 * Default for whimpy setting. The default is NOT whimpy.
	 * This should be inline with the database setting.
	 */
	public final static int DEFAULT_WHIMPY = 0;

	/**
	 * Default drink setting. The default is thirsty.
	 * This should be inline with the database setting.
	 */
	public final static int DEFAULT_DRINK = 0;
	
	/**
	 * Default eat setting. The default is hungry.
	 * This should be inline with the database setting.
	 */
	public final static int DEFAULT_EAT = 0;
	
	/**
	 * Default level when first starting. The default is 0.
	 * This should be inline with the database setting.
	 */
	public final static int DEFAULT_LEVEL = 0;

	/**
	 * Default health is 1000, top health.
	 * This should be inline with the database setting.
	 */
	public final static int DEFAULT_HEALTH = 1000;
	
	/**
	 * Default alignment is good (8).
	 * This should be inline with the database setting.
	 */
	public final static int DEFAULT_ALIGNMENT = 8;
	
	/**
	 * Default movement is 1000 (not tired).
	 * This should be inline with the database setting.
	 */
	public final static int DEFAULT_MOVEMENT = 1000;
	
	/**
	 * the logger for logging messages. The log level is set in the 
	 * properties file and is provided both to the logger as well as
	 * the handlers of the root logger.
	 */
	 public final static Logger logger = Logger.getLogger("mmud");

	// the defaults
	public final static boolean SHUTDOWN = false;

	public final static String CONFIG_FILE = "config";
	public final static String config_file = CONFIG_FILE;

	public final static String MUDFILEPATH = "/home/karchan/mud";

	/**
	 * A variable containing the date and time when the server was 
	 * started. This is used in admin commands.
	 */
	public final static Calendar theGameStartupTime = Calendar.getInstance();

	/**
	 * a property list with the default values
	 */
	private static Properties theDefaults;

	/**
	 * a property list with the actual configuration values
	 */
	private static Properties theValues;

	public final static String DBNAME = "mud";
	public final static String DBHOST = "localhost";
	public final static String DBDOMAIN = "";
	public final static String DBUSER = "root";
	public final static String DBPASSWD = "";
	public final static String DBJDBCCLASS = "com.mysql.jdbc.Driver";
	public final static String DBURL = "jdbc:mysql";

	public final static String NOTAUSERERROR = "this is not a valid user";
	public final static String USERNOTFOUNDERROR = "unable to locate user";
	public final static String ROOMNOTFOUNDERROR = "unable to locate room";
	public final static String METHODDOESNOTEXISTERROR = "unable to locate method";
	public final static String PWDINCORRECTERROR = "password is incorrect";
	public final static String USERALREADYACTIVEERROR = "the user is already playing the game";
	public final static String USERALREADYEXISTSERROR = "the user already exists";
	public final static String USERBANNEDERROR = "the user has been banned from the game";
	public final static String MULTIUSERERROR = "the user is already playing the game as another character";
	public final static String INVALIDFRAMEERROR = "this is not a valid frame";
	public final static String INVALIDCOMMANDERROR = "this is not a valid command";
	public final static String INVALIDMAILERROR = "mud mail not found";
	public final static String ITEMDEFDOESNOTEXISTERROR = "the definition of the item does not exist";
	public final static String ITEMDOESNOTEXISTERROR = "the item does not exist";
	public final static String ITEMCANNOTBEWORNERROR = "the item cannot be worn (at least not there)";
	public final static String NOTENOUGHMONEYERROR = "you do not have enough money";
	public final static String UNABLETOPARSEPROPERLY = "the entire command or part of the command could not be parsed correctly";
	public final static String DATABASECONNECTIONERROR = "error connecting to database";

	/*! version number of mmserver */
	public final static String MMVERSION = "4.01b"; // the mmud version in general
	/*! protocol version of mmserver, should be kept backwards compatible,
		if backwards compatibility should be broken, update the major
		version number */
	public final static String MMPROTVERSION = "1.0"; // the protocol version used in this mud
	/*! identity string of mmserver, sent immediately to client when
		the client connects */
	public final static String IDENTITY = "Maartens Mud (MMud) Version " + MMVERSION;
	public final static int MUDPORTNUMBER = 3339;
	public final static String MUDHOST = "127.0.0.1";

	// the variables, initialised on defaults
	public static int mudportnumber = MUDPORTNUMBER;
	public static String mudhost = MUDHOST;

	public static String dbname = DBNAME;
	public static boolean shutdown = SHUTDOWN;
	public static String dbhost = DBHOST;
	public static String dbdomain = DBDOMAIN;
	public static String dbuser = DBUSER;
	public static String dbpasswd = DBPASSWD;
	public static String dbjdbcclass = DBJDBCCLASS;
	public static String dburl = DBURL;

	public static String mudfilepath = MUDFILEPATH;
	public static String mudofflinefile = "offline.txt";
	public static String mudhelpfile = "help.txt";
	public static String mudauditfile = "audit.trail.txt";
	public static String mudbigfile = "bigfile.txt";
	public static String muderrorfile = "error.txt";
	public static String mudnewcharfile = "newchar.html";
	public static String goodbyefile = "goodbye.html";

	public static String mudcgi = "/cgi-bin/mud.cgi";
	public static String leftframecgi = "/cgi-bin/leftframe.cgi";
	public static String logonframecgi = "/cgi-bin/logonframe.cgi";
	public static String nph_leftframecgi = "/cgi-bin/nph-leftframe.cgi";
	public static String nph_logonframecgi = "/cgi-bin/nph-logonframe.cgi";
	public static String nph_javascriptframecgi = "/cgi-bin/nph-javascriptframe.cgi";

	public static String mudtitle = "Land of Karchan";
	public static String mudbackground = "/images/gif/webpic/back4.gif";
	public static String mudcopyright = "&copy; Copyright Maarten van Leunen";

	public static String logoninputerrormessage = "<HTML><HEAD>" +
		"<HTML><HEAD><TITLE>Error</TITLE></HEAD>\n\n" +
		"<BODY>\n" +
		"<BODY BGCOLOR=#FFFFFF BACKGROUND=\"/images/gif/webpic/back4.gif\"><H1>Error</H1><HR>\n" +
		"The following rules need to be followed when filling out a name and password:<P>\r\n" +
		"<UL><LI>the following characters are valid in a name: {A..Z, a..z, _}" +
		"<LI>all characters are valid in a password except {\"} and {'}" +
		"<LI>at least 3 characters are required for a name" +
		"<LI>at least 5 characters are required for a password" +
		"</UL><P>These are the rules.<P>\r\n" +
		"<A HREF=\"/karchan/enter.html\">Click here to retry</A></body>\n" +
		"</body>\n" +
		"</HTML>\n";
	public static String goodbyemessage = "<H1><IMG SRC=\"/images/gif/dragon.gif\">Goodbye</H1>" +
		" Your game has been saved, and we look forward to seeing you again in the" +
		" near future.<P>" +
		" <A HREF=\"/karchan/index.html\">" +
		" <IMG SRC=\"/images/gif/webpic/buttono.gif\"" +
		" BORDER=\"0\"></A><P>" +
		" </BODY>" +
		" </HTML>";
	public static String unknownerrormessage = "<HTML><HEAD>" +
		"<HTML><HEAD><TITLE>Unknown Error</TITLE></HEAD>\n\n" +
		"<BODY>\n" +
		"<BODY BGCOLOR=#FFFFFF BACKGROUND=\"/images/gif/webpic/back4.gif\"><H1>Unknown Error</H1><HR>\n" +
		"An unknown error has occurred. Please try to relogin to the game.<P>\r\n" +
		"<A HREF=\"/karchan/enter.html\">Click here to retry</A></body>\n" +
		"</body>\n" +
		"</HTML>\n";

	public static final String[] whimpy = {
		"",
		"feeling well",
		"feeling fine",
		"feeling quite nice",
		"slightly hurt",
		"hurt",
		"quite hurt",
		"extremely hurt",
		"terribly hurt",
		"feeling bad",
		"feeling very bad",
		"at death's door"};

	public static final String[] health = {
		"at death's door",
		"feeling very bad",
		"feeling bad",
		"terribly hurt",
		"extremely hurt",
		"quite hurt",
		"hurt",
		"slightly hurt",
		"feeling quite nice",
		"feeling fine",
		"feeling well",
		"feeling very well"};

	public static final String[] movement = {
		"fully exhausted",
		"almost exhausted",
		"very tired",
		"slightly tired",
		"slightly fatigued",
		"not tired at all"};

	public static final String[] alignment = {
		"evil",
		"bad",
		"mean",
		"untrustworthy",
		"neutral",
		"trustworthy",
		"kind",
		"awfully good",
		"good"};

	public static final String[][] emotions = {
	{"agree", "agrees"},
	{"apologize", "apologizes"},
	{"blink", "blinks"},
	{"cheer", "cheers"},
	{"chuckle", "chuckles"},
	{"cough", "coughs"},
	{"dance", "dances"},
	{"disagree", "disagrees"},
	{"flinch", "flinches"},
	{"flirt", "flirts"},
	{"frown", "frowns"},
	{"giggle", "giggles"},
	{"glare", "glares"},
	{"grimace", "grimaces"},
	{"grin", "grins"},
	{"groan", "groans"},
	{"growl", "growls"},
	{"grumble", "grumbles"},
	{"grunt", "grunts"},
	{"hmm", "hmms"},
	{"howl", "howls"},
	{"hum", "hums"},
	{"kneel", "kneels"},
	{"kneel", "kneels"},
	{"listen", "listens"},
	{"melt", "melts"},
	{"mumble", "mumbles"},
	{"mutter", "mutters"},
	{"nod", "nods"},
	{"purr", "purrs"},
	{"shrug", "shrugs"},
	{"sigh", "sighs"},
	{"smile", "smiles"},
	{"smirk", "smirks"},
	{"snarl", "snarls"},
	{"sneeze", "sneezes"},
	{"stare", "stares"},
	{"think", "thinks"},
	{"wave", "waves"},
	{"whistle", "whistles"},
	{"wink", "winks"},
	{"laugh", "laughs out loud"},
	{"wonder", "wonders"},
	{"wince", "winces"}};

	public static final String[][] emotions2 = {
	{"caress", "caresses"},
	{"comfort", "comforts"},
	{"confuse", "confuses"},
	{"congratulate", "congratulates"},
	{"cuddle", "cuddles"},
	{"fondle", "fondles"},
	{"greet", "greets"},
	{"hug", "hugs"},
	{"ignore", "ignores"},
	{"kick", "kicks"},
	{"kiss", "kisses"},
	{"knee", "knees"},
	{"lick", "licks"},
	{"like", "likes"},
	{"love", "loves"},
	{"nudge", "nudges"},
	{"pat", "pats"},
	{"pinch", "pinches"},
	{"poke", "pokes"},
	{"slap", "slaps"},
	{"smooch", "smooches"},
	{"sniff", "sniffes"},
	{"squeeze", "squeezes"},
	{"tackle", "tackles"},
	{"thank", "thanks"},
	{"tickle", "tickles"},
	{"worship", "worships"}};

	public final static String[] adverb = {
	"absentmindedly",
	"aimlessly",
	"amazedly",
	"amusedly",
	"angrily",
	"anxiously",
	"appreciatively",
	"appropriately",
	"archly",
	"astonishingly",
	"attentively",
	"badly",
	"barely",
	"belatedly",
	"bitterly",
	"boringly",
	"breathlessly",
	"briefly",
	"brightly",
	"brotherly",
	"busily",
	"carefully",
	"cautiously",
	"charmingly",
	"cheerfully",
	"childishly",
	"clumsily",
	"coaxingly",
	"coldly",
	"completely",
	"confidently",
	"confusedly",
	"contentedly",
	"coquetishly",
	"courageously",
	"coyly",
	"crazily",
	"cunningly",
	"curiously",
	"cutely",
	"cynically",
	"dangerously",
	"deeply",
	"defiantly",
	"dejectedly",
	"delightedly",
	"delightfully",
	"deliriously",
	"demonically",
	"depressively",
	"derisively",
	"desperately",
	"devilishly",
	"dirtily",
	"disappointedly",
	"discretely",
	"disgustedly",
	"doubtfully",
	"dreamily",
	"dubiously",
	"earnestly",
	"egocentrically",
	"egoistically",
	"encouragingly",
	"endearingly",
	"enthusiastically",
	"enviously",
	"erotically",
	"evilly",
	"exhaustedly",
	"exuberantly",
	"faintly",
	"fanatically",
	"fatherly",
	"fiercefully",
	"firmly",
	"foolishly",
	"formally",
	"frantically",
	"friendly",
	"frostily",
	"funnily",
	"furiously",
	"generously",
	"gleefully",
	"gracefully",
	"graciously",
	"gratefully",
	"greedily",
	"grimly",
	"happily",
	"harmonically",
	"headlessly",
	"heartbrokenly",
	"heavily",
	"helpfully",
	"helplessly",
	"honestly",
	"hopefully",
	"humbly",
	"hungrily",
	"hysterically",
	"ignorantly",
	"impatiently",
	"inanely",
	"indecently",
	"indifferently",
	"innocently",
	"inquiringly",
	"inquisitively",
	"insanely",
	"instantly",
	"intensely",
	"interestedly",
	"ironically",
	"jauntily",
	"jealously",
	"joyfully",
	"joyously",
	"kindly",
	"knowingly",
	"lazily",
	"loudly",
	"lovingly",
	"lustfully",
	"madly",
	"maniacally",
	"melancholically",
	"menacingly",
	"mercilessly",
	"merrily",
	"mischieviously",
	"motherly",
	"musically",
	"mysteriously",
	"nastily",
	"naughtily",
	"nervously",
	"nicely",
	"noisily",
	"nonchalantly",
	"outrageously",
	"overwhelmingly",
	"painfully",
	"passionately",
	"patiently",
	"patronizingly",
	"perfectly",
	"personally",
	"physically",
	"pitifully",
	"playfully",
	"politely",
	"professionally",
	"profoundly",
	"profusely",
	"proudly",
	"questioningly",
	"quickly",
	"quietly",
	"quizzically",
	"randomly",
	"rapidly",
	"really",
	"rebelliously",
	"relieved",
	"reluctantly",
	"remorsefully",
	"repeatedly",
	"resignedly",
	"respectfully",
	"romantically",
	"rudely",
	"sadistically",
	"sadly",
	"sarcastically",
	"sardonically",
	"satanically",
	"scornfully",
	"searchingly",
	"secretively",
	"seductively",
	"sensually",
	"seriously",
	"sexily",
	"shamelessly",
	"sheepishly",
	"shyly",
	"sickly",
	"significantly",
	"silently",
	"sisterly",
	"skilfully",
	"sleepily",
	"slightly",
	"slowly",
	"slyly",
	"smilingly",
	"smugly",
	"socially",
	"softly",
	"solemnly",
	"strangely",
	"stupidly",
	"sweetly",
	"tearfully",
	"tenderly",
	"terribly",
	"thankfully",
	"theoretically",
	"thoughtfully",
	"tightly",
	"tiredly",
	"totally",
	"tragically",
	"truly",
	"trustfully",
	"uncontrollably",
	"understandingly",
	"unexpectedly",
	"unhappily",
	"unintentionally",
	"unknowingly",
	"vaguely",
	"viciously",
	"vigorously",
	"violently",
	"virtually",
	"warmly",
	"wearily",
	"wholeheartedly",
	"wickedly",
	"wildly",
	"wisely",
	"wistfully"};

	private static FightingThread theFightingThread;
	
	/**
	 * Resets the fighting thread back to null. Used when nobody
	 * is fighting anymore.
	 */
	public static void emptyFightingThread()
	{
	  theFightingThread = null;
		}
	
	/**
	 * This is basically used for 
	 * creating and running the thread that takes care of the fighting.
	 * The one command that should do this is the FightCommand.
	 * @see mmud.commands.FightCommand
	 */
	public static void wakeupFightingThread()
	{
	  if (theFightingThread == null)
	  {
		Constants.logger.info("Starting fightingthread...");
		theFightingThread = new FightingThread();
		theFightingThread.start();
	  }
	}

	/**
	 * standard tostring implementation.
	 * @return String in the format Values(dbname=...
	 */
	public String toString()
	{
		return "Values(dbname=" + dbname + ",shutdown=" + shutdown
			+ ",mmversion=" + MMVERSION 
			+ ",mmprotversion=" + MMPROTVERSION
			+ ",identity=" + IDENTITY + ")";
	}

	/**
	 * reads a file.
	 * @param aFile file to be read
	 * @return String containing the entire file.
	 */
	public static String readFile(File aFile)
		throws IOException
	{
		logger.finer("");
		FileReader myFileReader = new FileReader(aFile);
		StringBuffer myResult = new StringBuffer();
		char[] myArray = new char[1024];
		int i = myFileReader.read(myArray, 0, myArray.length);
		while (i>0)
		{
			myResult.append(new String(myArray, 0, i));
			i = myFileReader.read(myArray, 0, myArray.length);
		}
		myFileReader.close();
		String returnResult = myResult.toString();
		logger.finest("returns: [" + returnResult + "]");
		return returnResult;
	}

	/**
	 * reads a file.
	 * @param aFilename the name of the file to be read.
	 * @return String containing the entire file.
	 */
	public static String readFile(String aFilename)
		throws IOException
	{
		logger.finer("aFilename=" + aFilename);
		return readFile(new File(aFilename));
	}

	private static TreeMap theCommandStructure = new TreeMap();
	private static Collection theUserCommandStructure = new Vector();
	private static TreeMap theEmotionStructure = new TreeMap();
	private static TreeMap theEmotion2Structure = new TreeMap();
	private static TreeSet theAdverbStructure = new TreeSet();

	static
	{
		theDefaults = new Properties();
		theDefaults.setProperty("mudfilepath", MUDFILEPATH);
		theDefaults.setProperty("dbname", DBNAME);
		theDefaults.setProperty("dbhost", DBHOST);
		theDefaults.setProperty("dbuser", DBUSER);
		theDefaults.setProperty("dbpasswd", DBPASSWD);
		theDefaults.setProperty("dbjdbcclass", DBJDBCCLASS);
		theDefaults.setProperty("dburl", DBURL);
		theDefaults.setProperty("mudofflinefile", "offline.txt");
		theDefaults.setProperty("mudhelpfile", "help.txt");
		theDefaults.setProperty("mudauditfile", "audit.trail.txt");
		theDefaults.setProperty("mudbigfile", "bigfile.txt");
		theDefaults.setProperty("muderrorfile", "error.txt");
		theDefaults.setProperty("mudnewcharfile", "newchar.html");
		theDefaults.setProperty("goodbyefile", "goodbye.html");
		theDefaults.setProperty("mudcgi", "/cgi-bin/mud.cgi");
		theDefaults.setProperty("leftframecgi", "/cgi-bin/leftframe.cgi");
		theDefaults.setProperty("logonframecgi", "/cgi-bin/logonframe.cgi");
		theDefaults.setProperty("nph_leftframecgi", "/cgi-bin/nph-leftframe.cgi");
		theDefaults.setProperty("nph_logonframecgi", "/cgi-bin/nph-logonframe.cgi");
		theDefaults.setProperty("nph_javascriptframecgi", "/cgi-bin/nph-javascriptframe.cgi");
		theDefaults.setProperty("mudtitle", "Maarten's Mud");
		theDefaults.setProperty("mudbackground", "");
		theDefaults.setProperty("mudcopyright", "&copy; Copyright Maarten van Leunen");
		theDefaults.setProperty("logginglevel", "all");
		theDefaults.setProperty("portnumber", "3339");

		theCommandStructure.put("bow", new BowCommand("bow( to (\\w)+)?( (\\w)+)?"));
		theCommandStructure.put("me", new MeCommand("me .+"));
		theCommandStructure.put("quit", new QuitCommand("quit"));
		theCommandStructure.put("sleep", new SleepCommand("sleep"));
		theCommandStructure.put("awaken", new AwakenCommand("awaken"));
		theCommandStructure.put("ask", new AskCommand("ask (to (\\w)+ )?.+"));
		theCommandStructure.put("tell", new TellCommand("tell to (\\w)+ .+"));
		theCommandStructure.put("say", new SayCommand("say (to (\\w)+ )?.+"));
		theCommandStructure.put("shout", new ShoutCommand("shout (to (\\w )+)?.+"));
		theCommandStructure.put("whisper", new WhisperCommand("whisper (to (\\w)+ )?.+"));
		theCommandStructure.put("clear", new ClearCommand("clear"));
		theCommandStructure.put("time", new TimeCommand("time"));
		theCommandStructure.put("date", new DateCommand("date"));
		theCommandStructure.put("south", new SouthCommand("south"));
		theCommandStructure.put("north", new NorthCommand("north"));
		theCommandStructure.put("east", new EastCommand("east"));
		theCommandStructure.put("west", new WestCommand("west"));
		theCommandStructure.put("s", new SouthCommand("s"));
		theCommandStructure.put("n", new NorthCommand("n"));
		theCommandStructure.put("e", new EastCommand("e"));
		theCommandStructure.put("w", new WestCommand("w"));
		theCommandStructure.put("up", new UpCommand("up"));
		theCommandStructure.put("down", new DownCommand("down"));
		theCommandStructure.put("go", new GoCommand("go (up|down|north|south|east|west)?"));
		theCommandStructure.put("help", new HelpCommand("help( (\\w)+)?"));
		theCommandStructure.put("bigtalk", new BigTalkCommand("bigtalk"));
		theCommandStructure.put("curtsey", new CurtseyCommand("curtsey( to (\\w)+)?"));
		theCommandStructure.put("eyebrow", new EyebrowCommand("eyebrow"));
		theCommandStructure.put("whimpy", new WhimpyCommand("whimpy( .+|help)?"));
		theCommandStructure.put("who", new WhoCommand("who"));
		theCommandStructure.put("deletemail", new DeleteMailCommand("deletemail (\\d)+"));
		theCommandStructure.put("listmail", new ListMailCommand("listmail"));
		theCommandStructure.put("readmail", new ReadMailCommand("readmail (\\d)+"));
		theCommandStructure.put("mail", new MailCommand("mail"));
		theCommandStructure.put("sendmail", new SendMailCommand("sendmail .+"));
		theCommandStructure.put("pkill", new PkillCommand("pkill( (\\w)+)?"));
		theCommandStructure.put("fight", new FightCommand("fight (\\w)+"));
		theCommandStructure.put("stop", new FightCommand("stop fighting"));
		theCommandStructure.put("stats", new StatsCommand("stats"));
		theCommandStructure.put("inventory", new InventoryCommand("inventory"));
		theCommandStructure.put("i", new InventoryCommand("i"));
		theCommandStructure.put("drink", new DrinkCommand("drink( (\\w|-)+){1,4}"));
		theCommandStructure.put("eat", new EatCommand("eat( (\\w|-)+){1,4}"));
		theCommandStructure.put("wear", new WearCommand("wear( (\\w|-)+){1,4} on (\\w)+"));
		theCommandStructure.put("remove", new UnwearCommand("remove( (\\w|-)+){1,4} from (\\w)+"));
		theCommandStructure.put("wield", new WieldCommand("wield( (\\w|-)+){1,4} with (\\w)+"));
		theCommandStructure.put("unwield", new UnwieldCommand("unwield( (\\w|-)+){1,4} from (\\w)+"));
		theCommandStructure.put("drop", new DropCommand("drop( (\\w|-)+){1,4}"));
		theCommandStructure.put("get", new GetCommand("get( (\\w|-)+){1,4}"));
		theCommandStructure.put("search", new SearchCommand("search( (\\w|-)+){1,4}"));
		theCommandStructure.put("put", new PutCommand("put( (\\w|-)+){1,4} in( (\\w|-)+){1,4}"));
		theCommandStructure.put("retrieve", new RetrieveCommand("retrieve( (\\w|-)+){1,4} from( (\\w|-)+){1,4}"));
		theCommandStructure.put("lock", new LockCommand("lock( (\\w|-)+){1,4} with( (\\w|-)+){1,4}"));
		theCommandStructure.put("unlock", new UnlockCommand("unlock( (\\w|-)+){1,4} with( (\\w|-)+){1,4}"));
		theCommandStructure.put("give", new GiveCommand("give( (\\w|-)+){1,4} to (\\w)+"));
		theCommandStructure.put("open", new OpenCommand("open( (\\w|-)+){1,4}"));
		theCommandStructure.put("close", new CloseCommand("close( (\\w|-)+){1,4}"));
		theCommandStructure.put("readroleplay", new ReadRpgBoardCommand("readroleplay"));
		theCommandStructure.put("read", new ReadCommand("read( (\\w|-)+){1,4}"));
		theCommandStructure.put("readpublic", new ReadPublicCommand("readpublic"));
		theCommandStructure.put("public", new PostPublicCommand("public .+"));
		theCommandStructure.put("l", new LookCommand("l"));
		theCommandStructure.put("look", new LookCommand("look ((at)|(in))( (\\w|-)+){1,4}"));
		theCommandStructure.put("buy", new BuyCommand("buy( (\\w|-)+){1,4} from (\\w)+"));
		theCommandStructure.put("sell", new SellCommand("sell( (\\w|-)+){1,4} to (\\w)+"));
		theCommandStructure.put("title", new TitleCommand("title .+"));
		theCommandStructure.put("admin", new AdminCommand("admin .+"));
		theCommandStructure.put("roleplay", new PostRpgBoardCommand("roleplay .+"));

		for (int i=0;i<emotions.length;i++)
		{
			theCommandStructure.put(emotions[i][0], new EmotionCommand(".+"));
			theEmotionStructure.put(emotions[i][0], emotions[i][1]);
		}
		for (int i=0;i<emotions2.length;i++)
		{
			theCommandStructure.put(emotions2[i][0], new EmotionToCommand(".+"));
			theEmotion2Structure.put(emotions2[i][0], emotions2[i][1]);
		}
		for (int i=0;i<adverb.length;i++)
		{
			theAdverbStructure.add(adverb[i]);
		}
	}

	/**
	 * initialise this class. Sets default properties, load properties
	 * from file if possible, initializes command structure.
	 */
	public static void init()
	{
		logger.finer("");

		theValues = new Properties(theDefaults);
		loadInfo();
	}

	/**
	 * set the user command structure for using user commands.
	 */
	public static void setUserCommands(Collection aCollection)
	{
		logger.finer("aCollection=" + aCollection);
		theUserCommandStructure = aCollection;
	}
		
	/**
	 * remove a command from the collection of special user commands.
	 * Usually this is used for when a command has broken
	 * down.
	 * @param aCommand the command that is one of the collection of commands
	 * and which needs to be removed.
	 * @throws MudException if the command to be removed is not present in the
	 * collection.
	 */
	public static void removeUserCommand(UserCommandInfo aCommand)
	  throws MudException
	{
	  if (!theUserCommandStructure.remove(aCommand))
	  {
		throw new MudException("Special user command to be removed not found in collection...");
	  }
	}
	
	/**
	 * Returns the commands to be used, based on the first word in the
	 * command entered by the user.
	 * @param aCommand String containing the command entered by the user.
	 * @return Collection (Vector) containing the commands that fit the
	 * description. The commands that are contained are in the following
	 * order:
	 * <ol><li>special commands retrieved from the database
	 * <li>normal commands
	 * <li>bogus command (the ultimate failover, "I don't understand
	 * that.".)
	 * </ol>It also means that this collection will always carry at least
	 * one command, the bogus command.<P>
	 * All commands are newly created.
	 */
	public static Collection getCommand(String aCommand)
	{
		logger.finer("aCommand=" + aCommand);
		Vector result = new Vector(5);
		Iterator myI = theUserCommandStructure.iterator();
		while (myI.hasNext())
		{
			UserCommandInfo myCom = (UserCommandInfo) myI.next();
			logger.finer("retrieved usercommand " + myCom.getCommand());
			if (aCommand.matches(myCom.getCommand()))
			{
				logger.finer("matches " + myCom.getCommand());
				ScriptCommand scriptCommand;
				scriptCommand = new ScriptCommand(myCom);
				scriptCommand.setCommand(aCommand);
				result.add(scriptCommand);
			}
		}
		int i = aCommand.indexOf(' ');
		String key = (i != -1 ? aCommand.substring(0, i) : aCommand);
		Command myCommand = (Command) theCommandStructure.get(key);
		if (myCommand != null)
		{
			myCommand.setCommand(aCommand);
			result.add(myCommand);
		}
		myCommand = new BogusCommand(".+");
		myCommand.setCommand(aCommand);
		result.add(myCommand);
		return result;
	}

	/**
	 * split up the command into different words.
	 * @param aCommand String containing the command
	 * @return String array where each String contains a word
	 * from the command.
	 */
	public static String[] parseCommand(String aCommand)
	{
		logger.finer("aCommand=" + aCommand);
		return aCommand.split("( )+", 50);
	}

	/**
	 * returns the appropriate emotion for a third person view.
	 * @param anEmotion the emotion, for example "whistle".
	 * @return the third person grammer, for example "whistles".
	 * @see #returnEmotionTo
	 */
	public static String returnEmotion(String anEmotion)
	{
		logger.finer("anEmotion=" + anEmotion);
		return (String) theEmotionStructure.get(anEmotion);
	}

	/**
	 * returns the appropriate emotion for a third person view. The
	 * difference with returnEmotion is that this has a target.
	 * @param anEmotion the emotion, for example "caress".
	 * @return the third person grammer, for example "caresses".
	 * @see #returnEmotion
	 */
	public static String returnEmotionTo(String anEmotion)
	{
		logger.finer("anEmotion=" + anEmotion);
		return (String) theEmotion2Structure.get(anEmotion);
	}

	/**
	 * Checks to see that an adverb is valid.
	 * @param anAdverb String containing the adverb to check, for example
	 * "aimlessly".
	 * @return boolean which is true if the adverb is real.
	 */
	public static boolean existsAdverb(String anAdverb)
	{
		logger.finer("anAdverb=" + anAdverb);
		return theAdverbStructure.contains(anAdverb.toLowerCase());
	}

	/**
	 * Returns wether or not a character is a vowel.
	 * @param aChar the character to check
	 * @return boolean which is true if the character is a vowel.
	 */
	public static boolean isQwerty(char aChar)
	{
		return aChar == 'a' ||
				aChar == 'e' ||
				aChar == 'u' ||
				aChar == 'o' ||
				aChar == 'i' ||
				aChar == 'A' ||
				aChar == 'E' ||
				aChar == 'U' ||
				aChar == 'I' ||
				aChar == 'O';
	}

	/**
	 * parse an item description. Entries in the parameters could look like
	 * this:
	 * <ul><li>bucket, length=1
	 * <li>wooden bucket, length=2
	 * <li>old wooden bucket, length=3
	 * <li>small old wooden bucket, length=4
	 * <li>2 bucket, length=2
	 * <li>2 wooden bucket, length=3
	 * <li>2 old wooden bucket, length=4
	 * <li>2 small old wooden bucket, length=5
	 * </ul>
	 * @param words String array containing words per element
	 * @param begin the position in the string array where the item
	 * description starts.
	 * @param length the length of the item description (1..5)
	 * @return Vector of length 5, first is the amount (Integer),
	 * next three are adjectives in Strings and
	 * then comes the name of the item in String. Adjectives are allowed
	 * to be null values.
	 * @throws ParseException when the amount requested is less than 1, or
	 * the number of words in the array does not match a proper item
	 * description.
	 */
	public static Vector parseItemDescription(String []words,
			int begin, 
			int length)
	throws ParseException
	{
		String output = "";
		for (int i=begin;i<begin+length;i++)
		{
			output+=",words[" + i + "]=" + words[i];
		}
		logger.finer(output + ",words.length=" + words.length + 
			",begin=" + begin + 
			",length=" + length);
		String adject1, adject2, adject3, name;
		// get [amount] [adject1..3] name
		int amount = 1;
		try
		{
			amount = Integer.parseInt(words[begin]);
			if (amount < 1)
			{
				throw new ParseException();
			}
			switch (length)
			{
				case 2:
				adject1 =  null;
				adject2 =  null;
				adject3 =  null;
				name = words[begin + 1];
				break;
				case 3:
				adject1 = words[begin + 1];
				adject2 =  null;
				adject3 =  null;
				name = words[begin + 2];
				break;
				case 4:
				adject1 = words[begin + 1];
				adject2 = words[begin + 2];
				adject3 =  null;
				name = words[begin + 3];
				break;
				case 5:
				adject1 = words[begin + 1];
				adject2 = words[begin + 2];
				adject3 = words[begin + 3];
				name = words[begin + 4];
				break;
				default:
				throw new ParseException();
			}
		}
		catch (NumberFormatException e)
		{
			switch (length)
			{
				case 1:
				adject1 =  null;
				adject2 =  null;
				adject3 =  null;
				name = words[begin];
				break;
				case 2:
				adject1 = words[begin];
				adject2 =  null;
				adject3 =  null;
				name = words[begin + 1];
				break;
				case 3:
				adject1 = words[begin];
				adject2 = words[begin + 1];
				adject3 =  null;
				name = words[begin + 2];
				break;
				case 4:
				adject1 = words[begin];
				adject2 = words[begin + 1];
				adject3 = words[begin + 2];
				name = words[begin + 3];
				break;
				default:
				throw new ParseException();
			}
		}
		Vector myVector = new Vector();
		myVector.add(new Integer(amount));
		myVector.add(adject1);
		myVector.add(adject2);
		myVector.add(adject3);
		myVector.add(name);
		logger.finer("returns adject1=" + adject1 +
			",adject2=" + adject2 +
			",adject3=" + adject3 +
			",name=" + name +
			",amount=" + amount);
		return myVector;
	}

	/**
	 * load properties from file.
	 * @param aFilename the name of the file containing the property
	 * settings.
	 */
	public static void loadInfo(String aFilename)
	{
		logger.finer("aFilename=" + aFilename);
		try
		{
			FileInputStream reader = new FileInputStream(aFilename);
			theValues.load(reader);
			reader.close();
		}
		catch (FileNotFoundException e)
		{
			System.out.println("File not found (" + aFilename + ")...");
			e.printStackTrace();
		}
		catch (IOException e)
		{
			System.out.println("Error reading file (" + aFilename + ")...");
			e.printStackTrace();
		}
		dbname = theValues.getProperty("dbname");
		dbhost = theValues.getProperty("dbhost");
		dbdomain = theValues.getProperty("dbdomain");
		dbuser = theValues.getProperty("dbuser");
		dbpasswd = theValues.getProperty("dbpasswd");
		dbjdbcclass = theValues.getProperty("dbjdbcclass");
		dburl = theValues.getProperty("dburl");

		try
		{
			mudportnumber =
				Integer.parseInt(theValues.getProperty("portnumber"));
		}
		catch (NumberFormatException e)
		{
			System.out.println("Unable to interpret portnumber");
			e.printStackTrace();
		}
		mudhost = theValues.getProperty("host");

		mudfilepath = theValues.getProperty("mudfilepath");
		mudofflinefile = theValues.getProperty("mudofflinefile");
		mudhelpfile = theValues.getProperty("mudhelpfile");
		mudauditfile = theValues.getProperty("mudauditfile");
		mudbigfile = theValues.getProperty("mudbigfile");
		muderrorfile = theValues.getProperty("muderrorfile");
		mudnewcharfile = theValues.getProperty("mudnewcharfile");
		goodbyefile = theValues.getProperty("goodbyefile");

		mudcgi = theValues.getProperty("mudcgi");
		leftframecgi = theValues.getProperty("leftframecgi");
		logonframecgi = theValues.getProperty("logonframecgi");
		nph_leftframecgi = theValues.getProperty("nph_leftframecgi");
		nph_logonframecgi = theValues.getProperty("nph_logonframecgi");
		nph_javascriptframecgi = theValues.getProperty("nph_javascriptframecgi");

		mudtitle = theValues.getProperty("mudtitle");
		mudbackground = theValues.getProperty("mudbackground");
		mudcopyright = theValues.getProperty("mudcopyright");

		// set the logging level
		String level = theValues.getProperty("logginglevel");
		Level logLevel = Level.ALL;
		if (level.equalsIgnoreCase("all"))
		{
			logLevel = Level.ALL;
		}	
		else if (level.equalsIgnoreCase("off"))
		{
			logLevel = Level.OFF;
		}
		else if (level.equalsIgnoreCase("severe"))
		{
			logLevel = Level.SEVERE;
		}
		else if (level.equalsIgnoreCase("warning"))
		{
			logLevel = Level.WARNING;
		}
		else if (level.equalsIgnoreCase("info"))
		{
			logLevel = Level.INFO;
		}
		else if (level.equalsIgnoreCase("config"))
		{
			logLevel = Level.CONFIG;
		}
		else if (level.equalsIgnoreCase("fine"))
		{
			logLevel = Level.FINE;
		}
		else if (level.equalsIgnoreCase("finer"))
		{
			logLevel = Level.FINER;
		}
		else if (level.equalsIgnoreCase("finer"))
		{
			logLevel = Level.FINEST;
		}
		else if (level.equalsIgnoreCase("none"))
		{
			logLevel = Level.OFF;
		}
		else if (level.equalsIgnoreCase("off"))
		{
			logLevel = Level.OFF;
		}
		// The root logger's handlers default to INFO. We have to
		// crank them up. We could crank up only some of them
		// if we wanted, but we will turn them all up.
		Handler[] handlers =
			Logger.getLogger( "" ).getHandlers();
		for ( int index = 0; index < handlers.length; index++ ) 
		{
			handlers[index].setLevel( logLevel );
		}
		logger.setLevel(logLevel);
		logger.info("Logging level set to " + level);
		logger.finest(
			"\nsevere :" + logger.isLoggable(Level.SEVERE) +
			"\nwarning:" + logger.isLoggable(Level.WARNING) +
			"\nconfig :" + logger.isLoggable(Level.CONFIG) +
			"\nfinest :" + logger.isLoggable(Level.INFO) +
			"\nfine   :" + logger.isLoggable(Level.FINE) +
			"\nfiner  :" + logger.isLoggable(Level.FINER) +
			"\nfinest :" + logger.isLoggable(Level.FINEST));
	}

	/**
	 * load properties from file. Default filename is "config".
	 */
	public static void loadInfo()
	{
		loadInfo(config_file);
	}

	/**
	 * save properties to file.
	 */
	public static void saveInfo()
	{
		logger.finer("");
		try
		{
			FileOutputStream writer = new FileOutputStream(CONFIG_FILE);
			theValues.store(writer,
			" contains persistent configuration information for server\n" +
			"# this file is generated, do not edit unless you really want to.");
			writer.close();
		}
		catch (FileNotFoundException e)
		{
			System.out.println("File not found...");
			e.printStackTrace();
			return;
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return;
		}
	}

	private static String theOfflineDescription = null;

	/**
	 * Returns the offline description. Either this is read from file,
	 * or it is set during gameplay.<BR>
	 * Setting from file is for external reasons, so we do not have to stop
	 * the game. Setting from inside the game is for serious reasons
	 * like a database connection that is down or something similar.
	 * @return the String containing the description of the game when
	 * the game is offline. If the game is in perfect working order,
	 * a null pointer is returned.
	 */
	public static String getOfflineDescription()
	throws IOException
	{
		if (theOfflineDescription == null)
		{
			File myFile = new File(Constants.mudofflinefile);
			if (!myFile.exists())
			{
				return null;
			}
			if (!myFile.canRead())
			{
				return null;
			}
			return Constants.readFile(Constants.mudofflinefile);
		}
		return theOfflineDescription;
	}
	
	/**
	 * Sets the description in game. This is primarily used for serious problems
	 * like a broken down database connection or something.
	 * @param aDesc the description to be returned upon entering the mud.
	 */
	public static void setOfflineDescription(String aDesc)
	{
		theOfflineDescription = aDesc;
	}
}

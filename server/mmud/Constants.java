/*-------------------------------------------------------------------------
cvsinfo: $Header$
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

import mmud.commands.*;

public final class Constants
{
	/**
	 * the logger for logging messages. The log level is set in the 
	 * properties file and is provided both to the logger as well as
	 * the handlers of the root logger.
	 */
    public final static Logger logger = Logger.getLogger("mmud");

	// the defaults
	public final static boolean SHUTDOWN = false;
	public final static boolean OFFLINE = false;

	public final static String CONFIG_FILE = "config";
	public final static String config_file = CONFIG_FILE;

	public final static String MUDFILEPATH = "/home/karchan/mud";

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
	public final static String NOTENOUGHMONEYERROR = "you do not have enough money";

	/*! version number of mmserver */
	public final static String MMVERSION = "4.01b"; // the mmud version in general
	/*! protocol version of mmserver, should be kept backwards compatible,
		if backwards compatibility should be broken, update the major
		version number */
	public final static String MMPROTVERSION = "1.0"; // the protocol version used in this mud
	/*! identity string of mmserver, sent immediately to client when
		the client connects */
	public final static String IDENTITY = "Maartens Mud (MMud) Version " + MMVERSION;

	// the variables, initialised on defaults
	public static String dbname = DBNAME;
	public static boolean shutdown = SHUTDOWN;
	public static String dbhost = DBHOST;
	public static String dbdomain = DBDOMAIN;
	public static String dbuser = DBUSER;
	public static String dbpasswd = DBPASSWD;
	public static String dbjdbcclass = DBJDBCCLASS;
	public static String dburl = DBURL;

	public static boolean offline = OFFLINE;

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
	{"wonder", "wonders"}};

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


	public String toString()
	{
		return "Values(dbname=" + dbname + ",shutdown=" + shutdown
			+ ",mmversion=" + MMVERSION 
			+ ",mmprotversion=" + MMPROTVERSION
			+ ",identity=" + IDENTITY + ")";
	}

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

	public static String readFile(String aFilename)
		throws IOException
	{
		logger.finer("aFilename=" + aFilename);
		return readFile(new File(aFilename));
	}

	private static TreeMap theCommandStructure = new TreeMap();
	private static TreeMap theEmotionStructure = new TreeMap();
	private static TreeMap theEmotion2Structure = new TreeMap();
	private static TreeSet theAdverbStructure = new TreeSet();

	public static void init()
	{
		logger.finer("");

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
		theValues = new Properties(theDefaults);
		loadInfo();

		theCommandStructure.put("bow", new BowCommand());
		theCommandStructure.put("me", new MeCommand());
		theCommandStructure.put("quit", new QuitCommand());
		theCommandStructure.put("sleep", new SleepCommand());
		theCommandStructure.put("awaken", new AwakenCommand());
		theCommandStructure.put("ask", new AskCommand());
		theCommandStructure.put("tell", new TellCommand());
		theCommandStructure.put("say", new SayCommand());
		theCommandStructure.put("shout", new ShoutCommand());
		theCommandStructure.put("whisper", new WhisperCommand());
		theCommandStructure.put("clear", new ClearCommand());
		theCommandStructure.put("time", new TimeCommand());
		theCommandStructure.put("date", new DateCommand());
		theCommandStructure.put("south", new SouthCommand());
		theCommandStructure.put("north", new NorthCommand());
		theCommandStructure.put("east", new EastCommand());
		theCommandStructure.put("west", new WestCommand());
		theCommandStructure.put("s", new SouthCommand());
		theCommandStructure.put("n", new NorthCommand());
		theCommandStructure.put("e", new EastCommand());
		theCommandStructure.put("w", new WestCommand());
		theCommandStructure.put("up", new UpCommand());
		theCommandStructure.put("down", new DownCommand());
		theCommandStructure.put("go", new GoCommand());
		theCommandStructure.put("help", new HelpCommand());
		theCommandStructure.put("bigtalk", new BigTalkCommand());
		theCommandStructure.put("curtsey", new CurtseyCommand());
		theCommandStructure.put("eyebrow", new EyebrowCommand());
		theCommandStructure.put("whimpy", new WhimpyCommand());
		theCommandStructure.put("who", new WhoCommand());
		theCommandStructure.put("deletemail", new DeleteMailCommand());
		theCommandStructure.put("listmail", new ListMailCommand());
		theCommandStructure.put("readmail", new ReadMailCommand());
		theCommandStructure.put("mail", new MailCommand());
		theCommandStructure.put("sendmail", new SendMailCommand());
		theCommandStructure.put("pkill", new PkillCommand());
		theCommandStructure.put("inventory", new InventoryCommand());
		theCommandStructure.put("i", new InventoryCommand());
		theCommandStructure.put("drink", new DrinkCommand());
		theCommandStructure.put("eat", new EatCommand());
		theCommandStructure.put("drop", new DropCommand());
		theCommandStructure.put("get", new GetCommand());
		theCommandStructure.put("give", new GiveCommand());
		theCommandStructure.put("read", new ReadCommand());
		theCommandStructure.put("l", new LookCommand());
		theCommandStructure.put("look", new LookCommand());
		theCommandStructure.put("buy", new BuyCommand());

		for (int i=0;i<emotions.length;i++)
		{
			theCommandStructure.put(emotions[i][0], new EmotionCommand());
			theEmotionStructure.put(emotions[i][0], emotions[i][1]);
		}
		for (int i=0;i<emotions2.length;i++)
		{
			theCommandStructure.put(emotions2[i][0], new EmotionToCommand());
			theEmotion2Structure.put(emotions2[i][0], emotions2[i][1]);
		}
		for (int i=0;i<adverb.length;i++)
		{
			theAdverbStructure.add(adverb[i]);
		}
	}

	public static Command getCommand(String key)
	{
		logger.finer("");
		Command myCommand = (Command) theCommandStructure.get(key);
		return (myCommand != null ? myCommand : new BogusCommand());
	}

	public static String[] parseCommand(String aCommand)
	{
		logger.finer("aCommand=" + aCommand);
		return aCommand.split("( )+", 50);
	}

	public static String returnEmotion(String anEmotion)
	{
		logger.finer("anEmotion=" + anEmotion);
		return (String) theEmotionStructure.get(anEmotion);
	}

	public static String returnEmotionTo(String anEmotion)
	{
		logger.finer("anEmotion=" + anEmotion);
		return (String) theEmotion2Structure.get(anEmotion);
	}

	public static boolean existsAdverb(String anAdverb)
	{
		logger.finer("anAdverb=" + anAdverb);
		return theAdverbStructure.contains(anAdverb.toLowerCase());
	}

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
		// The root logger's handlers default to INFO. We have to
		// crank them up. We could crank up only some of them
		// if we wanted, but we will turn them all up.
		Handler[] handlers =
			Logger.getLogger( "" ).getHandlers();
		for ( int index = 0; index < handlers.length; index++ ) {
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


}

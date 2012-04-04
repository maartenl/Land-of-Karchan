/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mmud.functions;

import mmud.webservices.*;
import java.sql.Connection;
import java.sql.SQLException;
import org.owasp.validator.html.*;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 * Util class for some methods that are very often used.
 * @author maartenl
 */
public class Utils {

    private static final String POLICY_FILE_LOCATION = "/usr/share/glassfish3/glassfish/domains/domain1/config/antisamy-myspace-1.4.4.xml";

    private static Logger itsLog = Logger.getLogger("mmudrest");


    /**
     * Returns the database connection
     * @return database connection Connection object
     * @throws SQLException
     * @throws NamingException
     */
    public static Connection getDatabaseConnection() throws SQLException, NamingException {
        itsLog.finest("Utils: connection with database opened.");
        Connection con;
        javax.naming.Context ctx = new InitialContext();
        DataSource ds = (DataSource) ctx.lookup("jdbc/mmud");
        con = ds.getConnection();
        return con;
    }

    /**
     * Returns a safe string, containing no javascript at all.
     * @param dirtyInput the original string.
     * @return the new string, sanse javascript.
     */
    public static String security(String dirtyInput)
    throws Exception
    {
        Policy policy = Policy.getInstance(POLICY_FILE_LOCATION);

        AntiSamy as = new AntiSamy();

        CleanResults cr = as.scan(dirtyInput, policy);
        return cr.getCleanHTML(); // some custom function
    }

    /**
     * Returns a safe string, containing no html tags at all, in other words:
     * <ul>
     * <li><blaat> is replaced with empty string</li>
     * <li>'&' is replaced with &amp;</li>
     * <li>'<' is replaced with &lt;</li>
     * <li>'>' is replaced with &gt;</li>
     * </ul>
     * @param dirtyInput the original string.
     * @return the new string, replaced html tags.
     */
    public static String topSecurity(String dirtyInput)
    throws Exception
    {
        return dirtyInput.replaceAll("<.*>","").replace("&", "&amp;").replace("<","&lt;").replace(">","&gt;").replace("\"", "&quot;"); // some custom function
    }
}

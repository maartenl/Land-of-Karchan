/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mmud.functions.mail;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import mmud.functions.Authentication;
import mmud.webservices.webentities.MmudMail;
import mmud.functions.Utils;

/**
 * All support functions for sending, reading and deleting mail.
 * @author maartenl
 */
public class Mail {

    private static Logger itsLog = Logger.getLogger("mmudrest");

    /**
     * Contains the item ids of the different items that represent letters/mail.
     * The readdescription of said letters looks a little like the following:
     * <p>"stuffletterhead
     * letterbody
     * letterfooter"</p>
     * That way, the letterhead, letterbody and letterfooter are automatically
     * replaced.
     */
    public static final int[] ITEMS = {
        8008,
        8009,
        8010,
        8011,
        8012,
        8013,
        8014,
        8015
    };


    public static final String LISTMAIL_SQL = "select mm_mailtable.* from mm_mailtable, mm_usertable where toname = ? and mm_usertable.name = mm_mailtable.toname and mm_usertable.lok is not null and trim(mm_usertable.lok) <> \"\" and mm_usertable.lok = ? and mm_mailtable.deleted <> 1 order by id desc limit ?, 20";

    public static final String GETMAIL_SQL = "select mm_mailtable.* from mm_mailtable, mm_usertable where toname = ? and mm_mailtable.id = ? and mm_usertable.name = mm_mailtable.toname and mm_usertable.lok is not null and trim(mm_usertable.lok) <> \"\" and mm_usertable.lok = ? and mm_mailtable.deleted <> 1 ";

    public static final String DELETEMAIL_SQL = "update mm_mailtable, mm_usertable set deleted = 1 where toname = ? and mm_mailtable.id = ? and mm_usertable.name = mm_mailtable.toname and mm_usertable.lok is not null and trim(mm_usertable.lok) <> \"\" and mm_usertable.lok = ? and mm_mailtable.deleted <> 1";

    public static final String NEWMAIL_SQL = "insert into mm_mailtable (name, toname, subject, body, whensent, newmail, haveread) values(?, ?, ?, ?, now(), 1, 0)";

    public static final String GETMAXIDITEMDEF_SQL = "select (max(id)) as max from mm_items";

    public static final String SETIDITEMDEF_IN_MAIL_SQL = "update mm_mailtable set item_id = ? where id = ?";

    public static final String TURNON_HAVEREAD_IN_MAIL_SQL = "update mm_mailtable set haveread = 1 where id = ?";

    public static final String TURNOFF_NEWMAIL_IN_MAIL_SQL = "update mm_mailtable set newmail = 0 where toname = ?";

    public static final String CHECK_NEWMAIL_IN_MAIL_SQL = "select 1 from mm_mailtable where newmail = 1 and deleted = 0 and toname = ?";

    public static final String CHECKITEMDEF_SQL = "select 1 from mm_items where id = ?";

    public static final String CREATEMAILITEM_IN_INVENTORY_SQL = "insert into mm_charitemtable (id, belongsto) values(?, ?)";
    public static final String CREATEMAILITEM_INSTANCE_SQL = "insert into mm_itemtable (itemid, owner) values(?, \"Karn\")";

    public static final String CREATEMAILITEMDEF_SQL = "insert into mm_items " +
            "(id, name, adject1, adject2, adject3, getable, dropable, visible, description, readdescr, copper, owner, notes) " +
            "select ?, name, adject1, adject2, adject3, getable, dropable, visible, description, " +
            "replace(replace(replace(readdescr, \"letterhead\", ?), \"letterbody\", ?), \"letterfooter\", ?), " +
            "copper, owner, notes " +
            "from mm_items where id = ?";

    public List<MmudMail> listMail(String name, int offset, String lok)
    {
        Connection con=null;
        ResultSet rst=null;
        PreparedStatement stmt=null;
        List<MmudMail> res = new ArrayList<MmudMail>();
        try
        {

            con = Utils.getDatabaseConnection();

            (new Authentication()).authentication(con, name, lok);

            stmt=con.prepareStatement(LISTMAIL_SQL);
            stmt.setString(1, name);
            stmt.setString(2, lok);
            stmt.setInt(3, offset);
            rst=stmt.executeQuery();
            while(rst.next())
            {
                MmudMail mail = new MmudMail(null, rst.getString("toname"), rst.getString("name"), rst.getString("subject"), rst.getString("body"),
                        rst.getLong("id"), rst.getBoolean("haveread"), rst.getBoolean("newmail"), rst.getDate("whensent"),
                        rst.getBoolean("deleted"), rst.getInt("item_id"));
                res.add(mail);
            }

            // turn off the "newmail" sign.
            stmt=con.prepareStatement(TURNOFF_NEWMAIL_IN_MAIL_SQL);
            stmt.setString(1, name);
            stmt.executeUpdate();
        }
        catch(WebApplicationException e)
        {
            //ignore
            throw e;
        }
        catch(Exception e)
        {
            itsLog.throwing(this.getClass().getName(), "listMail", e);
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        finally
        {
            if (rst != null) {try {rst.close();} catch (Exception e){}}
            if (stmt != null) {try {stmt.close();} catch (Exception e){}}
            if (con != null) {try {con.close();} catch (Exception e){}}
            itsLog.finest(this.getClass().getName() + ": connection with database closed.");
        }
        return res;
    }

    /**
     * Creates a new mudmail.
     * @param newMail the new mud mail to create
     * @param name the name of the person to create the mudmail from.
     * @return true upon success, false otherwise.
     */
    public boolean newMail(MmudMail newMail, String name)
    {
        itsLog.entering(this.getClass().getName(), "newMail");
        Connection con=null;
        ResultSet rst=null;
        PreparedStatement stmt=null;
        try
        {
            String lok = newMail.getLok();

            con = Utils.getDatabaseConnection();

            Authentication auth = new Authentication();
            auth.authentication(con, name, lok);
            auth.verify(con, newMail.getToname());

            stmt=con.prepareStatement(NEWMAIL_SQL); // name, toname, subject, body
            stmt.setString(1, name);
            stmt.setString(2, Utils.topSecurity(newMail.getToname()));
            stmt.setString(3, Utils.topSecurity(newMail.getSubject()));
            stmt.setString(4, Utils.security(newMail.getBody()));

            int result = stmt.executeUpdate();
            if (result != 1)
            {
                itsLog.severe("Unable to store new mud mail.");
                throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
            }
        }
        catch(WebApplicationException e)
        {
            //ignore
            throw e;
        }
        catch(Exception e)
        {
            itsLog.throwing(this.getClass().getName(), "newMail", e);
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        finally
        {
            if (rst != null) {try {rst.close();} catch (Exception e){}}
            if (stmt != null) {try {stmt.close();} catch (Exception e){}}
            if (con != null) {try {con.close();} catch (Exception e){}}
            itsLog.finest(this.getClass().getName() + ": connection with database closed.");
        }

        itsLog.exiting(this.getClass().getName(), "newMail");
        return true;
    }

    public MmudMail getMail(String name, String lok, long id)
    {
        itsLog.entering(this.getClass().getName(), "getMail");
        Connection con=null;
        ResultSet rst=null;
        PreparedStatement stmt=null;
        MmudMail res = null;
        try
        {
            con = Utils.getDatabaseConnection();
            (new Authentication()).authentication(con, name, lok);

            res = getMyMail(con, name, id, lok);

            // turn off the "have not read" sign.
            stmt=con.prepareStatement(TURNON_HAVEREAD_IN_MAIL_SQL);
            stmt.setLong(1, id);
            int result = stmt.executeUpdate();
            if (result != 1)
            {
                throw new WebApplicationException(Response.Status.NOT_FOUND);
            }
        }
        catch(WebApplicationException e)
        {
            //ignore
            throw e;
        }
        catch(Exception e)
        {
            itsLog.throwing(this.getClass().getName(), "getMail", e);
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        finally
        {
            if (rst != null) {try {rst.close();} catch (Exception e){}}
            if (stmt != null) {try {stmt.close();} catch (Exception e){}}
            if (con != null) {try {con.close();} catch (Exception e){}}
            itsLog.finest(this.getClass().getName() + ": connection with database closed.");
        }

        itsLog.exiting(this.getClass().getName(), "getMail");
        return res;
    }

    private MmudMail getMyMail(Connection con, String name, Long id, String lok)
    {
        PreparedStatement stmt=null;
        ResultSet rst=null;
        MmudMail result = null;
        try
        {
            stmt=con.prepareStatement(GETMAIL_SQL);
            stmt.setString(1, name);
            stmt.setLong(2, id);
            stmt.setString(3, lok);
            rst=stmt.executeQuery();
            if(rst.next())
            {
                result = new MmudMail(null, rst.getString("toname"), rst.getString("name"), rst.getString("subject"), rst.getString("body"), rst.getLong("id"),
                        rst.getBoolean("haveread"), rst.getBoolean("newmail"), rst.getDate("whensent"), rst.getBoolean("deleted"), rst.getInt("item_id"));
            }
            else
            {
                throw new WebApplicationException(Response.Status.NOT_FOUND);
            }
        }
        catch(Exception e)
        {
            itsLog.throwing(this.getClass().getName(), "getMyMail", e);
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        finally
        {
            if (rst != null) {try {rst.close();} catch (Exception e){}}
            if (stmt != null) {try {stmt.close();} catch (Exception e){}}
            itsLog.finest(this.getClass().getName() + "getMyMail: resultset closed.");
        }
        return result;
    }

    public void createMailItem(String name, String lok, long id, int item)
    {
        itsLog.entering(this.getClass().getName(), "createMailItem");
        if (item >= ITEMS.length && item < 0)
        {
            itsLog.entering(this.getClass().getName(), "createMailItem: wrong item def");
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        Connection con=null;
        ResultSet rst=null;
        PreparedStatement stmt=null;
        MmudMail res = null;
        try
        {
            con = Utils.getDatabaseConnection();
            itsLog.entering(this.getClass().getName(), "createMailItem: authentication");
            (new Authentication()).authentication(con, name, lok);

            // get the sepcific mail with id {id}
            itsLog.entering(this.getClass().getName(), "createMailItem: getMail");
            res = getMyMail(con, name, id, lok);

            int item_id = 0;
            if (res.getItem_id() == null || res.getItem_id() == 0)
            {
                int max_id = 0;
                // retrieve max item_id
                itsLog.entering(this.getClass().getName(), "createMailItem: retrieve max id");
                stmt=con.prepareStatement(GETMAXIDITEMDEF_SQL);
                rst=stmt.executeQuery();
                if (rst.next())
                {
                    max_id = rst.getInt("max");
                }
                else
                {
                    itsLog.entering(this.getClass().getName(), "createMailItem: no max itemdefsid determined");
                    throw new WebApplicationException(Response.Status.BAD_REQUEST);
                }
                // create item definition
                itsLog.entering(this.getClass().getName(), "createMailItem: create item definition");
                stmt=con.prepareStatement(CREATEMAILITEMDEF_SQL);
                stmt.setInt(1, max_id + 1);
                stmt.setString(2, "<div id=\"karchan_letterhead\">" + res.getSubject() + "</div>");
                stmt.setString(3, "<div id=\"karchan_letterbody\">" + res.getBody() + "</div>");
                stmt.setString(4, "<div id=\"karchan_letterfooter\">" + res.getName() + "</div>");
                stmt.setInt(5, ITEMS[item]);
                int result = stmt.executeUpdate();
                if (result != 1)
                {
                    itsLog.entering(this.getClass().getName(), "createMailItem: could not create itemdef");
                    throw new WebApplicationException(Response.Status.BAD_REQUEST);
                }

                // set item definition into the mail
                itsLog.entering(this.getClass().getName(), "createMailItem: set itemdefinition into the mail");
                stmt=con.prepareStatement(SETIDITEMDEF_IN_MAIL_SQL);
                stmt.setInt(1, max_id + 1);
                stmt.setLong(2, id);
                result = stmt.executeUpdate();
                if (result != 1)
                {
                    itsLog.entering(this.getClass().getName(), "createMailItem: could not update item_id in mail");
                    throw new WebApplicationException(Response.Status.BAD_REQUEST);
                }
                item_id = max_id + 1;
            }
            else
            {
                // retrieve item definition
                itsLog.entering(this.getClass().getName(), "createMailItem: retrieve item def");
                stmt=con.prepareStatement(CHECKITEMDEF_SQL);
                stmt.setLong(1, res.getItem_id());
                rst=stmt.executeQuery();
                if (!rst.next())
                {
                    itsLog.entering(this.getClass().getName(), "createMailItem: no itemdef found");
                    throw new WebApplicationException(Response.Status.BAD_REQUEST);
                }
                item_id = res.getItem_id();
            }

            if (item_id == 0)
            {
                itsLog.entering(this.getClass().getName(), "createMailItem: could not get itemdef");
                throw new WebApplicationException(Response.Status.BAD_REQUEST);
            }

            // create item instance
            itsLog.entering(this.getClass().getName(), "createMailItem: create instance object " + item_id + " " + name);
            stmt=con.prepareStatement(CREATEMAILITEM_INSTANCE_SQL, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, item_id);
            int result = stmt.executeUpdate();
            if (result != 1)
            {
                itsLog.entering(this.getClass().getName(), "createMailItem: could not create item instance");
                throw new WebApplicationException(Response.Status.BAD_REQUEST);
            }
            ResultSet keys = stmt.getGeneratedKeys();
            keys.next();
            int key = keys.getInt(1);

            // put item instance into inventory
            itsLog.entering(this.getClass().getName(), "createMailItem: create inventory object " + key + " " + name);
            stmt=con.prepareStatement(CREATEMAILITEM_IN_INVENTORY_SQL);
            stmt.setInt(1, key);
            stmt.setString(2, name);
            result = stmt.executeUpdate();
            if (result != 1)
            {
                itsLog.entering(this.getClass().getName(), "createMailItem: could not add item instance to inventory");
                throw new WebApplicationException(Response.Status.BAD_REQUEST);
            }

        }
        catch(WebApplicationException e)
        {
            //ignore
            throw e;
        }
        catch(Exception e)
        {
            itsLog.throwing(this.getClass().getName(), "createMailItem", e);
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        finally
        {
            if (rst != null) {try {rst.close();} catch (Exception e){}}
            if (stmt != null) {try {stmt.close();} catch (Exception e){}}
            if (con != null) {try {con.close();} catch (Exception e){}}
            itsLog.finest(this.getClass().getName() + ": connection with database closed.");
        }

        itsLog.exiting(this.getClass().getName(), "createMailItem");
    }

    public void deleteMail(String name, String lok, long id)
    {
        itsLog.entering(this.getClass().getName(), "deleteMail");
        Connection con=null;
        PreparedStatement stmt=null;
        try
        {
            con = Utils.getDatabaseConnection();
            (new Authentication()).authentication(con, name, lok);

            stmt=con.prepareStatement(DELETEMAIL_SQL);
            stmt.setString(1, name);
            stmt.setLong(2, id);
            stmt.setString(3, lok);
            int rst = stmt.executeUpdate();
            if (rst != 1)
            {
                throw new WebApplicationException(Response.Status.NOT_FOUND);
            }
        }
        catch(WebApplicationException e)
        {
            //ignore
            throw e;
        }
        catch(Exception e)
        {
            itsLog.throwing(this.getClass().getName(), "deleteMail", e);
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        finally
        {
            if (stmt != null) {try {stmt.close();} catch (Exception e){}}
            if (con != null) {try {con.close();} catch (Exception e){}}
            itsLog.finest(this.getClass().getName() + ": connection with database closed.");
        }

        itsLog.exiting(this.getClass().getName(), "deleteMail");
    }

    public boolean hasNewMail(String name, String lok) {
        itsLog.entering(this.getClass().getName(), "hasNewMail");
        Connection con=null;
        PreparedStatement stmt=null;
        ResultSet rst=null;
        boolean result = false;
        try
        {
            con = Utils.getDatabaseConnection();
            (new Authentication()).authentication(con, name, lok);

            stmt=con.prepareStatement(CHECK_NEWMAIL_IN_MAIL_SQL);
            stmt.setString(1, name);
            rst=stmt.executeQuery();
            if(rst.next())
            {
                result = true;
            }
        }
        catch(Exception e)
        {
            itsLog.throwing(this.getClass().getName(), "getMyMail", e);
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        finally
        {
            if (rst != null) {try {rst.close();} catch (Exception e){}}
            if (stmt != null) {try {stmt.close();} catch (Exception e){}}
            if (con != null) {try {con.close();} catch (Exception e){}}
            itsLog.finest(this.getClass().getName() + "hasNewMail: resultset closed.");
        }
        return result;
    }
}

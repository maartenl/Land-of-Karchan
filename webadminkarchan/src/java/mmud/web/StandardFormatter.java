/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mmud.web;

/**
 *
 * @author maartenl
 */
public class StandardFormatter implements Formatter {

    private boolean itsNewlines;
    private String td;
    private String nottd;

    public StandardFormatter(boolean newLines) {
        itsNewlines = newLines;
        td = (!newLines ? "<td>" : "");
        nottd = (!newLines ? "</td>" : "<br/>");

    }

    public String returnOptionsString(String tableName, String id) {
        StringBuffer result = new StringBuffer();
        result.append(td + "<a HREF=\"" + tableName.replace("mm_", "").toLowerCase() +
                ".jsp?id=" + id + "\">E</a> ");
        result.append("<a HREF=\"remove_" + tableName.replace("mm_", "").toLowerCase() +
                ".jsp?id=" + id + "\">X</a> ");
        result.append("<a HREF=\"remove_ownership.jsp?id=" +
                id + "&table=" + tableName.replace("mm_", "") + "\">O</a>" + nottd);
        return result.toString();
    }
}

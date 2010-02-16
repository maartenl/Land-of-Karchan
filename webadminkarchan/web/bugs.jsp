<HTML>
<TITLE>Bugs
</TITLE>
            <%@include file="includes/head.jsp" %>
<body>
<A HREF="scripts/bugs.jsp">Show all bugs</A><P>
<FORM METHOD="GET" ACTION="scripts/bugs.jsp">
Show bugs that are:
<SELECT NAME="open">
<option value=0 selected>Open
<option value=1>Closed
</SELECT>
<P>
<INPUT TYPE="submit" VALUE="Submit">
<INPUT TYPE="reset" VALUE="Clear"><P>
</FORM>

</body>
</HTML>

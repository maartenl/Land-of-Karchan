<%--
-----------------------------------------------------------------------
svninfo: $Id: charactersheets.php 1078 2006-01-15 09:25:36Z maartenl $
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
-----------------------------------------------------------------------

--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCtype HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
           <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Land of Karchan - Logonmessage</title>

        <link rel="stylesheet" type="text/css" href="/css/karchangame.css" />
        <!-- ** CSS ** -->
        <!-- base library -->
        <link rel="stylesheet" type="text/css" href="/ext-3.2.1/resources/css/ext-all.css" />
                <!-- overrides to base library -->


        <!-- ** Javascript ** -->
        <!-- ExtJS library: base/adapter -->
         <script type="text/javascript" src="/ext-3.2.1/adapter/ext/ext-base.js"></script>
        <!-- ExtJS library: all widgets -->
         <script type="text/javascript" src="/ext-3.2.1/ext-all-debug.js"></script>

        <!-- overrides to base library -->

        <!-- extensions -->

        <!-- page specific -->

        <script type="text/javascript">

        // Path to the blank image should point to a valid location on your server
        Ext.BLANK_IMAGE_URL = '/ext-3.2.1/resources/images/default/s.gif';

        Ext.onReady(function(){

        var top = new Ext.FormPanel({
                labelAlign: 'top',
                frame:true,
                bodyStyle:'padding:0px 0px 0',
                width: 600,
                url:'/karchan/admin/resources/admin/logonmessage',
                items: [{
                        xtype:'htmleditor',
                        id:'message',
                        name: 'message',
                        fieldLabel:'Message',
                        height:200,
                        anchor:'98%'
                },
                {
                        id:'datetime',
                        xtype:'hidden',
                        name: 'datetime'
                },
                {
                        id:'name',
                        xtype:'hidden',
                        name: 'name'
                }],
                buttons: [{
                        text: 'Retrieve', // GET
                        scope: this,
                        handler: function(b){
                                top.load({
                                        url:top.url,
                                        method:"GET",
                                        failure: function()
                                        {
                                                alert("Failure retrieving information.");
                                        },
                                        waitMsg:'Loading...'
                                });
                        }
                },{
                        text: 'New', // POST
                        handler: function(){
                                var f = top.getForm();
                                Ext.Ajax.request({
                                        url: top.url,
                                        method:"POST",
                                        headers: {'Content-Type':'application/json;charset=utf-8'},
                                        params: Ext.util.JSON.encode(f.getValues())
                                });
                        }
                },{
                        text: 'Update', // PUT
                        handler: function(){
                                var f = top.getForm();
                                Ext.Ajax.request({
                                        url: top.url,
                                        method:"PUT",
                                        headers: {'Content-Type':'application/json;charset=utf-8'},
                                        params: Ext.util.JSON.encode(f.getValues())
                                });
                        }
                }]
        });
        top.render("CommandForm");
        }); //end onReady
        </script>
    </head>
    <body>
        <a href="help/logonmessage.html" target="_blank"><img src="/images/icons/9pt4a.gif"/></a>
        <h1>Logonmessage</h1>
        <div id="CommandForm"></div>
    </body>
</html>

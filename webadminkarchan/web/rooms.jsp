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

		var roomId = 1;

		Ext.onReady(function(){

		var simple = new Ext.FormPanel({
			labelWidth: 75, // label settings here cascade unless overridden
			url:'/karchan/admin/resources/admin/rooms',
			frame:true,
			title: "Rooms",
			bodyStyle:'padding:5px 5px 0',
			width: 800,
			defaults: {width: 230},
			defaultType: 'textfield',
			items:
			[{
				fieldLabel:  'Id',
				id:'id',
				name:'id',
				allowBlank:false
			},{
				fieldLabel: 'Title',
				name:'title',
				id:'title'
			},{
				fieldLabel: 'Picture',
				id: 'picture',
				name: 'picture'
			},{
				fieldLabel: 'Area',
				id: 'area',
				name: 'area'
			},{
				xtype: 'label',
				fieldLabel: 'Creation',
				id: 'creation',
				name: 'creation'
			},{
				fieldLabel:'West',
				name: 'west',
				vtype:'west'
			},{
				fieldLabel:'East',
				name: 'east',
				vtype:'east'
			},{
				fieldLabel:'North',
				name: 'north',
				vtype:'north'
			},{
				fieldLabel:'South',
				name: 'south',
				vtype:'south'
			},{
				fieldLabel:'Up',
				name: 'up',
				vtype:'up'
			},{
				fieldLabel:'Down',
				name: 'down',
				vtype:'down'
			},{
				xtype:'htmleditor',
				id:'contents',
				name: 'contents',
				fieldLabel:'Contents',
				height:200,
				anchor:'98%'
			},{
				xtype: 'label',
				fieldLabel: 'Owner',
				id: 'owner',
				name: 'owner'
			}
			],
			buttons: [{
				text: 'Retrieve', // GET
				scope: this,
				handler: function(b){
					simple.load({
						url:simple.url + "/" + Ext.get("id").getValue(),
						method:"GET",
						failure: function()
						{
							alert("Failure retrieving information.");
						},
						waitMsg:'Loading...'
					});
				}
			},{
				text: 'Update', // PUT
				handler: function(){
					var f = simple.getForm();
					Ext.Ajax.request({
						url: simple.url + "/" + Ext.get("id").getValue(),
						method:"PUT",
						headers: {'Content-Type':'application/json;charset=utf-8'},
						params: Ext.util.JSON.encode(f.getValues())
					});
				}
			}]
	});

	simple.render(document.body);

		}); //end onReady
		</script>


    </head>
    <body><a href="help/help.html" target="_blank"><img src="/images/icons/9pt4a.gif"/></a>
        <h1>Rooms</h1>
	<div style="margin:10px;" id="CommandForm"></div>
    </body>
</html>


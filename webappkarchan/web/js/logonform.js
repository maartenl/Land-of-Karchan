Ext.BLANK_IMAGE_URL = '/ext/resources/images/default/s.gif';
 
// application main entry point
Ext.onReady(
function() 
{
	var win = new Ext.Window(
	{
		title:Ext.get('page-title').dom.innerHTML
			,renderTo:Ext.getBody()
			,iconCls:'icon-bulb'
			,width:420
			,height:240
			,border:false
			,layout:'fit'
			,items:[{
				// form as the only item in window
				xtype:'form'
				,labelWidth:60
				,frame:true
				,items:[
				{
				 // textfield:name
				 fieldLabel:'Name'
				 ,xtype:'textfield'
				 ,anchor:'-18'
				 },
				{
				 // textfield
				 fieldLabel:'Password'
				 ,xtype:'textfield'
				 ,anchor:'-18'
				 },
				 {
				 xtype:'combo'
				 ,fieldLabel:'Frames'
				 ,store:['No', 'Yes']
				 }] // items containing form elements
				 }] // items containing only the form
}); // new ext window
	win.show();
}); // eo function onReady
 
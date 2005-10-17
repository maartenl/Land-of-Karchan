/*-------------------------------------------------------------------------
cvsinfo: $Header: /karchan/mud/cvsroot/server/mmud/items/Item.java,v 1.16 2005/09/21 04:41:48 karn Exp $
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
package mmud.items;     

import java.util.TreeMap;
import java.util.logging.Logger;
import java.util.Vector;
import java.util.Hashtable;
import java.io.StringReader;

import mmud.*;
import mmud.characters.*;
import mmud.items.*;
import mmud.rooms.*;
import mmud.database.*;

import simkin.*;

/**
 * An item in the mud.
 * Basically consists of an ItemDefinition and a number of Attributes
 * specific to this item.
 */
public class Item implements Executable, AttributeContainer
{
	private ItemDef theItemDef;
	private int theId;
	private TreeMap theAttributes = new TreeMap();

	/**
	 * Default is null pointer, if it is not being worn/wielded.
	 */
	private PersonPositionEnum thePlaceOnBody;

	/**
	 * Create this item object with a default Item Definition and id.
	 * This method is usually only used by the database.
	 * @param anItemDef definition of the item
	 * @param anId integer identification of the item
	 */
	public Item(ItemDef anItemDef, int anId, PersonPositionEnum aPosBody)
	{
		this(anItemDef, anId);
		thePlaceOnBody = aPosBody;
	} 

	/**
	 * Create this item object with a default Item Definition and id.
	 * This method is usually only used by the database.
	 * @param anItemDef definition of the item
	 * @param anId integer identification of the item
	 */
	public Item(ItemDef anItemDef, int anId)
	{
		theItemDef = anItemDef;
		theId = anId;
	} 

	/**
	 * Create this item object with a default Item Definition and id.
	 * This method is usually only used by the database.
	 * @param anItemDef integer definition identification of the item
	 * @param anId integer identification of the item
	 */
	public Item(int anItemDef, int anId, PersonPositionEnum aPosBody)
	{
		this(ItemDefs.getItemDef(anItemDef), anId, aPosBody);
	} 

	/**
	 * Create this item object with a default Item Definition and id.
	 * This method is usually only used by the database.
	 * @param anItemDef integer definition identification of the item
	 * @param anId integer identification of the item
	 */
	public Item(int anItemDef, int anId)
	{
		this(ItemDefs.getItemDef(anItemDef), anId);
	} 

	/**
	 * Returns the id of this Item. Every instance of an ItemDef
	 * has a unique item id.
	 * @return integer identifying the Item.
	 */
	public int getId()
	{
		return theId;
	}

	/**
	 * Returns the position on the body where this item is worn/wielded.
	 * Will return a null pointer if the item is not worn/wielded at all.
	 * @return PersonPositionEnum identifying the position on the body.
	 */
	public PersonPositionEnum getWearing()
	{
		return thePlaceOnBody;
	}

	/**
	 * Returns wether or not an item is being worn/wielded by a person.
	 * This will always return false if the item is lying in a room.
	 * @return true if the item is being worn/wielded, otherwise false.
	 */
	public boolean isWearing()
	{
		return thePlaceOnBody != null;
	}

	/**
	 * Return if the position entered is a member
	 * of the possible positions that this item can be worn on.
	 * @return boolean, true if this item is wearable there.
	 */
	public boolean isWearable(PersonPositionEnum aPersonPosition)
	{
		return getItemDef().isWearable(aPersonPosition);
	}

	/**
	 * Set the position on the body where this item is worn. Set the null
	 * pointer if the item does not need to be worn.
	 * @param aPersonPosition identifying the position on the body.
	 * @throws RuntimeException when unable to attach the item
	 * to this position on the body, because the item won't fit.
	 * @throws ItemDoesNotExistException if the item could not be found.
	 */
	public void setWearing(PersonPositionEnum aPersonPosition)
	throws ItemDoesNotExistException
	{
		if (!isWearable(aPersonPosition))
		{
			throw new RuntimeException("unable to attach item to position," +
				" position illegal for this item.");
		}
		thePlaceOnBody = aPersonPosition;
		ItemsDb.changeWearing(this);
	}

	/**
	 * get the verb of the item.
	 * @return String containing the verb.
	 */
	public String getVerb()
	{
		return theItemDef.getVerb();
	}

	/**
	 * get the first adjective of the item.
	 * @return String containing the first adjective.
	 */
	public String getAdjective1()
	{
		return theItemDef.getAdjective1();
	}

	/**
	 * get the second adjective of the item.
	 * @return String containing the second adjective.
	 */
	public String getAdjective2()
	{
		return theItemDef.getAdjective2();
	}

	/**
	 * get the third adjective of the item.
	 * @return String containing the third adjective.
	 */
	public String getAdjective3()
	{
		return theItemDef.getAdjective3();
	}

	/**
	 * Returns the amount of money something costs in total copper coins.
	 * @return integer, gold*100+silver*10+copper
	 * @see Item#getGold
	 * @see Item#getCopper
	 * @see Item#getSilver
	 */
	public int getValue()   
	{
		int i = theItemDef.getValue();
		Logger.getLogger("mmud").finer("returns " + i);
		return i;
	}

	/**
	 * get the gold coins part of the value of the item.
	 * @return integer containing the number of gold coins.
	 */
	public int getGold()
	{
		return theItemDef.getGold();
	}

	/**
	 * get the silver coins part of the value of the item.
	 * @return integer containing the number of silver coins.
	 */
	public int getSilver()
	{
		return theItemDef.getSilver();
	}

	/**
	 * get the copper coins part of the value of the item.
	 * @return integer containing the number of copper coins.
	 */
	public int getCopper()
	{
		return theItemDef.getCopper();
	}

	/**
	 * get the item definition belonging to this item.
	 * @return ItemDef object containing the item definition.
	 */
	public ItemDef getItemDef()
	{
		return theItemDef;
	}

	/**
	 * get a description of the item.
	 * @return String containing the short description.
	 * @see ItemDef#getDescription
	 */
	public String getDescription()
	{
		return getItemDef().getDescription();
	}

	private String getStringForWearing(PersonPositionEnum aPos)
	{
		if (aPos == null)
		{
			return "";
		}
		if (isWearable(aPos))
		{
			return ("You can " + (aPos.isWielding()?"wield":"wear") +
				" it " + 
				aPos + 
				".<BR>").replaceAll("%SHISHER", "your");
		}
		return "";
	}

	/**
	 * get the description of the item (the long one). If the attribute
	 * <I>description</I> exists, than this one is used instead.
	 * @return String containing the description.
	 */
	public String getLongDescription()
	{
		StringBuffer myString = new StringBuffer(isAttribute("description") ? 
				getAttribute("description").getValue() : 
				theItemDef.getLongDescription());
		myString.append(getStringForWearing(PersonPositionEnum.WIELD_RIGHT));
		myString.append(getStringForWearing(PersonPositionEnum.WIELD_LEFT));
		myString.append(getStringForWearing(PersonPositionEnum.WIELD_BOTH));
		myString.append(getStringForWearing(PersonPositionEnum.ON_HEAD));
		myString.append(getStringForWearing(PersonPositionEnum.ON_NECK));
		myString.append(getStringForWearing(PersonPositionEnum.ON_TORSO));
		myString.append(getStringForWearing(PersonPositionEnum.ON_ARMS));
		myString.append(getStringForWearing(PersonPositionEnum.ON_LEFT_WRIST));
		myString.append(getStringForWearing(PersonPositionEnum.ON_RIGHT_WRIST));
		myString.append(getStringForWearing(PersonPositionEnum.ON_LEFT_FINGER));
		myString.append(getStringForWearing(PersonPositionEnum.ON_RIGHT_FINGER));
		myString.append(getStringForWearing(PersonPositionEnum.ON_FEET));
		myString.append(getStringForWearing(PersonPositionEnum.ON_HANDS));
		myString.append(getStringForWearing(PersonPositionEnum.ON_WAIST));
		myString.append(getStringForWearing(PersonPositionEnum.ON_LEGS));
		myString.append(getStringForWearing(PersonPositionEnum.ON_EYES));
		myString.append(getStringForWearing(PersonPositionEnum.ON_EARS));
		myString.append(getStringForWearing(PersonPositionEnum.ABOUT_BODY));
		return myString.toString();
	}

	/**
	 * standard tostring implementation.
	 * @return String containing the super.tostring + : + item def.
	 */
	public String toString()
	{
		return super.toString() + ":" + getId() + ":" + getItemDef();
	}

	/**
	 * Set or add an attribute of this item.
	 * @param anAttribute the attribute to be added/set.
	 */
	public void setAttribute(Attribute anAttribute)
	{
		theAttributes.put(anAttribute.getName(), anAttribute);
		AttributeDb.setAttribute(anAttribute, this);
	}

	/**
	 * returns the attribute found with name aName or null
	 * if it does not exist.
	 * @param aName the name of the attribute to search for
	 * @return Attribute object containing the attribute foudn or null.
	 */
	public Attribute getAttribute(String aName)
	{
		Attribute myAttrib = (Attribute) theAttributes.get(aName);
		if (myAttrib == null)
		{
			myAttrib = getItemDef().getAttribute(aName);
		}
		return myAttrib;
	}

	/**
	 * Remove a specific attribute from the item.
	 * @param aName the name of the attribute to be removed.
	 */
	public void removeAttribute(String aName)
	{
		theAttributes.remove(aName);
		AttributeDb.removeAttribute(new Attribute(aName, null, null), this);
	}

	/**
	 * returns true if the attribute with name aName
	 * exists.
	 * @param aName the name of the attribute to check
	 * @return boolean, true if the attribute exists for this item,
	 * otherwise returns false.
	 */
	public boolean isAttribute(String aName)
	{
		return theAttributes.containsKey(aName) ||
			getItemDef().isAttribute(aName);
	}

//	public boolean isWorn
//	public boolean isWielded
//	public PersonPositionEnum getWorn
//	public PersonPositionEnum getWielded
//	public void setWorn(
//	public void setWielded(PersonPositionEnum aNew

	public void setValue(String field_name, String attrib_name,
		Object value, ExecutableContext ctxt)
	throws FieldNotSupportedException
	{
		Logger.getLogger("mmud").finer("field_name=" + field_name +
			", atttrib_name=" + attrib_name + ", value=" +
			value + "[" + value.getClass() + "]");
		if (field_name.equals("description"))
		{
			if (value instanceof Null)
			{
				throw new FieldNotSupportedException(field_name + " not set, cannot be null.");
			}
			if (value instanceof String)
			{
				// TODO: perhaps
				// setDescription((String) value);
				return;
			}
			throw new FieldNotSupportedException(field_name + " not set, not string.");
		}
		throw new FieldNotSupportedException(field_name + " not found.");
	}

	public void setValueAt(Object array_index,
		String attrib_name,
		Object value, ExecutableContext ctxt)
	{
		Logger.getLogger("mmud").finer("array_index=" + array_index +
			", atttrib_name=" + attrib_name + ", value=" +
			value);
	}
																																							
	public ExecutableIterator createIterator()
	{
		Logger.getLogger("mmud").finer("");
		return null;
	}
																																							
	public ExecutableIterator createIterator(String qualifier)
	{
		Logger.getLogger("mmud").finer("qualifier=" + qualifier);
		return createIterator();
	}
																																							
	public Hashtable getAttributes()
	{
		Logger.getLogger("mmud").finer("");
		return null;
	}
																																							
	public Hashtable getInstanceVariables()
	{
		Logger.getLogger("mmud").finer("");
		return null;
	}
																																							
	public String getSource(String location)
	{
		Logger.getLogger("mmud").finer("location=" + location);
		return null;
	}
																																							
	public Object getValue(String field_name, String
		attrib_name, ExecutableContext ctxt)
	throws FieldNotSupportedException
	{
		Logger.getLogger("mmud").finer("field_name=" + field_name +
			", atttrib_name=" + attrib_name);
		if (field_name.equals("description"))
		{
			return getDescription();
		}
		if (field_name.equals("itemdef"))
		{
			return new Integer(getItemDef().getId());
		}
		throw new FieldNotSupportedException(field_name + " not found.");
	}
																																							
	public Object getValueAt(Object array_index,
		String attrib_name, ExecutableContext ctxt)
	{
		Logger.getLogger("mmud").finer("array_index=" + array_index +
			", atttrib_name=" + attrib_name);
		return null;
	}
																																							
	public Object methodAttribute(String method_name, Object[]
		arguments, ExecutableContext ctxt)
	throws MethodNotSupportedException
	{
		Logger.getLogger("mmud").finer("method_name=" + method_name +
			", arguments=" + arguments);
		if (method_name.equals("getAttribute"))
		{
			if (arguments.length == 1)
			{
				if (!(arguments[0] instanceof String))
				{
					throw new MethodNotSupportedException(method_name +
						" does not contain a String as argument.");
				}
				Attribute mAttrib = getAttribute((String) arguments[0]);
				if (mAttrib == null)
				{
					return null;
				}
				if (mAttrib.getValueType().equals("string"))
				{
					return mAttrib.getValue();
				}
				if (mAttrib.getValueType().equals("boolean"))
				{
					return new Boolean(mAttrib.getValue());
				}
				if (mAttrib.getValueType().equals("integer"))
				{
					try
					{
						return new Integer(mAttrib.getValue());
					}
					catch (NumberFormatException e)
					{
						throw new MethodNotSupportedException(method_name +
						" attribute " + mAttrib.getName() + " does not contain expected number.");
					}
				}
				throw new MethodNotSupportedException(method_name +
					" unknown value type in attribute " + 
					mAttrib.getName() + ". (" + 
					mAttrib.getValueType() + ")");
			}
		}
		if (method_name.equals("removeAttribute"))
		{
			if (arguments.length == 1)
			{
				if (!(arguments[0] instanceof String))
				{
					throw new MethodNotSupportedException(method_name +
						" does not contain a String as argument.");
				}
				removeAttribute((String) arguments[0]);
				Database.writeLog("root", "removed attribute (" + arguments[0] + ") from item " +
					this);
				return null;
			}
		}
		if (method_name.equals("setAttribute"))
		{
			if (arguments.length == 2)
			{
				if (!(arguments[0] instanceof String))
				{
					throw new MethodNotSupportedException(method_name +
						" does not contain a String as first argument.");
				}
				String mType = "object";
				if (arguments[1] instanceof String)
				{
					mType = "string";
				}
				if (arguments[1] instanceof Integer)
				{
					mType = "integer";
				}
				if (arguments[1] instanceof Boolean)
				{
					mType = "boolean";
				}
				Attribute mAttrib = new Attribute((String) arguments[0],
					arguments[1] + "", 
					mType);
				setAttribute(mAttrib);
				Database.writeLog("root", "set attribute (" + arguments[0] + ") in item " +
					this);
				return null;
			}
		}
		if (method_name.equals("isAttribute"))
		{
			if (arguments.length == 1)
			{
				if (!(arguments[0] instanceof String))
				{
					throw new MethodNotSupportedException(method_name +
						" does not contain a String as first argument.");
				}
				return new Boolean(isAttribute((String) arguments[0]));
			}
		}
		throw new MethodNotSupportedException(method_name + " not found.");
	}

	public Object method(String method_name, Object[]
		arguments, ExecutableContext ctxt)
	throws MethodNotSupportedException
	{
		Logger.getLogger("mmud").finer("method_name=" + method_name +
			", arguments=" + arguments);
		if (method_name.equals("addItem"))
		{
			if (arguments.length == 1)
			{
				if (!(arguments[0] instanceof Integer))
				{
					throw new MethodNotSupportedException(method_name +
						" does not contain a Integer as argument.");
				}
				ItemDef myItemDef = ItemDefs.getItemDef( 
					((Integer) arguments[0]).intValue());
				if (myItemDef == null)
				{
					throw new MethodNotSupportedException(method_name + " tried to use an unknown item definition.");
				}
				Item myItem = ItemsDb.addItem(myItemDef);
				try
				{
					ItemsDb.addItemToContainer(myItem, this);
				}
				catch (ItemDoesNotExistException e)
				{
					throw new MethodNotSupportedException(e.getMessage());
				}
				Database.writeLog("root", "created item (" + myItem + ") in item " + this);
				return myItem;
			}
		}
		if (method_name.equals("isWielding"))
		{
			if (arguments.length == 0)
			{
				return new Boolean(getWearing().isWielding());
			}
		}
		return methodAttribute(method_name, arguments, ctxt);
//		throw new MethodNotSupportedException(method_name + " not found.");
	}

	/**
	 * Executes a script with this person as the focus point.
	 * @param aScript a String containing the script to execute.
	 * @param aXmlMethodName the name of the method in the xml
	 * script that you wish to execute.
	 * @param aPerson the person that is responsible for the mutation
	 * on the item, causing the event.
	 * If it is 
	 * a null value, the scripted method will not be called with this array.
	 * This also means that the method in the xml script needs
	 * to either have this parameter set, or not set in the
	 * declaration.
	 * @see <A HREF="http://www.simkin.co.uk">Simkin</A>
	 * @throws MudException if something goes wrong.
	 */ 
	public Object runScript(String aXmlMethodName, String aScript, 
		Person aPerson)
	throws MudException
	{
		Logger.getLogger("mmud").finer("");
		try
		{
			// Create an interpreter and a context
			Interpreter interp=new Interpreter();
			interp.addGlobalVariable("rooms", Rooms.create());
			interp.addGlobalVariable("persons", Persons.create());
			ExecutableContext ctxt=new ExecutableContext(interp);
	
			// create an XMLExecutable object with the xml string
			XMLExecutable executable = 
				new XMLExecutable(getId() + "", new StringReader(aScript));
	
			// call the "main" method with the person as an argument
			// or with the person as well as the command (split into
			// different words in the array.)
			if (aPerson == null)
			{
				Object args[]= { this } ;
				return executable.method(aXmlMethodName, args, ctxt);
			}
			Object args[]= { aPerson, this };
			return executable.method(aXmlMethodName, args, ctxt);
		}
		catch (simkin.ParseException aParseException)
		{
			System.out.println("Unable to parse command.");
			aParseException.printStackTrace();
			throw new MudException("Unable to parse command.", aParseException);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new MudException("Unable to run script.", e);
		}
	}

	/**
	 * Executes a script with this person as the focus point.
	 * @param aScript a String containing the script to execute.
	 * @param aXmlMethodName the name of the method in the xml
	 * script that you wish to execute.
	 * @see <A HREF="http://www.simkin.co.uk">Simkin</A>
	 * @see #runScript(String,String,String[])
	 * @throws MudException if something goes wrong.
	 */ 
	public Object runScript(String aXmlMethodName, String aScript)
	throws MudException
	{
		Logger.getLogger("mmud").finer("");
		return runScript(aXmlMethodName, aScript, null);
	}
	 
}

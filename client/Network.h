/*-------------------------------------------------------------------------
svninfo: $Id: Database.java 1091 2006-03-08 22:05:19Z maartenl $
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
#ifndef __Network_h__
#define __Network_h__

#include <stdio.h>
#include <string.h>
#include <ogre.h>
#include <winsock2.h>

using namespace Ogre;
namespace Mmud
{

class Network
{
public:
   Network(void);
   virtual ~Network(void);

   virtual void open(void);//const String&,  const int);
   virtual void send(void);//const String& aMessage);
   virtual void receive(void);
   virtual void close(void);

protected:
   SOCKET m_socket;
};

} // end of namespace
#endif // __Network_h__

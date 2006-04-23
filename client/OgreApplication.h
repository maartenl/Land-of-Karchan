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
#ifndef __OgreApplication_h__
#define __OgreApplication_h__

#include <ogre.h>
#include <OgreFrameListener.h>
#include <ExampleFrameListener.h>

using namespace Ogre;

class OgreApplication
{
public:
   OgreApplication(void);
   virtual ~OgreApplication(void);

   virtual void go(void);

protected:
   virtual bool setup();
   virtual bool configure(void);
   virtual void chooseSceneManager(void);
   virtual void setupResources(void);
   virtual void createCamera(void);
   virtual void createViewports(void);
   virtual void createScene(void);
   virtual void createResourceListener(void);
   virtual void loadResources(void);
   virtual void destroyScene(void);

   virtual void createFrameListener(void);

   // Framelistener overridden functions
   virtual bool frameStarted(const FrameEvent& evt);
   virtual bool frameEnded(const FrameEvent& evt);

   Root *mRoot;
   Camera* mCamera;
   SceneManager* mSceneMgr;
   RenderWindow* mWindow;
   ExampleFrameListener* mFrameListener;
};

#endif // __OgreApplication_h__

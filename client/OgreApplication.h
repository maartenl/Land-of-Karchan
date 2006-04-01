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

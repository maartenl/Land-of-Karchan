#include "OgreApplication.h"

//-------------------------------------------------------------------------------------
OgreApplication::OgreApplication(void)
 : mRoot(0)
{
    mFrameListener = 0;
}

//-------------------------------------------------------------------------------------
OgreApplication::~OgreApplication(void)
{
    if (mFrameListener)
        delete mFrameListener;
    if (mRoot)
        delete mRoot;
}

//-------------------------------------------------------------------------------------
void OgreApplication::go(void)
{
   if (!setup())
      return;

   mRoot->startRendering();

   // clean up
   destroyScene();
}

//-------------------------------------------------------------------------------------
bool OgreApplication::setup(void)
{
   mRoot = new Root();

   setupResources();

   bool carryOn = configure();
   if (!carryOn) return false;

   chooseSceneManager();
   createCamera();
   createViewports();

   // Set default mipmap level (NB some APIs ignore this)
   TextureManager::getSingleton().setDefaultNumMipmaps(5);

   // Create any resource listeners (for loading screens)
   createResourceListener();
   // Load resources
   loadResources();

   // Create the scene
   createScene();

    // setup the appropriate frame listener.
   createFrameListener();

   return true;
};


//-------------------------------------------------------------------------------------
bool OgreApplication::configure(void)
{
   // Show the configuration dialog and initialise the system
   // You can skip this and use root.restoreConfig() to load configuration
   // settings if you were sure there are valid ones saved in ogre.cfg
   if(mRoot->restoreConfig() || mRoot->showConfigDialog())
   {
      // If returned true, user clicked OK so initialise
      // Here we choose to let the system create a default rendering window by passing 'true'
      mWindow = mRoot->initialise(true);
      return true;
   }
   else
   {
      return false;
   }
}

//-------------------------------------------------------------------------------------
void OgreApplication::chooseSceneManager(void)
{
   // Get the SceneManager, in this case a generic one
   mSceneMgr = mRoot->getSceneManager(ST_GENERIC);
}

//-------------------------------------------------------------------------------------
void OgreApplication::setupResources(void)
{
   // Load resource paths from config file
   ConfigFile cf;
   cf.load("resources.cfg");

   // Go through all sections & settings in the file
   ConfigFile::SectionIterator seci = cf.getSectionIterator();

   String secName, typeName, archName;
   while (seci.hasMoreElements())
   {
      secName = seci.peekNextKey();
      ConfigFile::SettingsMultiMap *settings = seci.getNext();
      ConfigFile::SettingsMultiMap::iterator i;
      for (i = settings->begin(); i != settings->end(); ++i)
      {
         typeName = i->first;
         archName = i->second;
         ResourceGroupManager::getSingleton().addResourceLocation(archName, typeName, secName);
      }
   }
}

void OgreApplication::createCamera(void)
{
    /**
    The following steps are logical:
    - the scenemanager creates a new camera.
    - the renderwindow knows which cameras to use to display on the screen
    - the part of the renderwindow to use for the camera is called
      the ViewPort.
    */
    // create the camera
    mCamera = mSceneMgr->createCamera("PlayerCam");
    // set its position, direction
    mCamera->setPosition(Vector3(0,10,500));
    mCamera->lookAt(Vector3(0,0,0));
    // make sure that objects that are really really close are not visible
    mCamera->setNearClipDistance(5);
    // make sure that objects that are far away are not visible,
    // optimalisation.
    // mCamera->setFarClipDistance(5000);
    // Create one viewport, entire window


}

void OgreApplication::createViewports(void)
{
    Viewport* vp = mWindow->addViewport(mCamera, 0, 0.0f, 0.0f, 1.0f, 1.0f);
    vp->setBackgroundColour(ColourValue(0,0,0));
    // Alter the camera aspect ratio to match the viewport
    // not really necessary as we are using the entire screen,
    // but what the hell
    mCamera->setAspectRatio(Real(vp->getActualWidth()) / Real(vp->getActualHeight()));
}

void OgreApplication::createScene(void)
{
    LogManager *logger = Ogre::LogManager::getSingletonPtr();
    logger->logMessage("entering createScene()");
    // Create the SkyBox
    mSceneMgr->setSkyBox(true, "Examples/CloudyNoonSkyBox");
    // Create a light
    Light* myLight = mSceneMgr->createLight("Light0");
    myLight->setType(Light::LT_POINT);
    myLight->setPosition(0, 40, 0);
    myLight->setDiffuseColour(1, 1, 1);
    myLight->setSpecularColour(1, 1, 1);

    Plane plane( Vector3::UNIT_Y, 0 );
    MeshManager::getSingleton().createPlane("ground",
        ResourceGroupManager::DEFAULT_RESOURCE_GROUP_NAME, plane,
        1500,1500,20,20,true,1,5,5,Vector3::UNIT_Z);
    Entity *ent = mSceneMgr->createEntity( "GroundEntity", "ground" );
    mSceneMgr->getRootSceneNode()->createChildSceneNode()->attachObject(ent);
    ent->setMaterialName("Mmud/rockwall");
    ent->setCastShadows(false);

    // create an entity
    Entity *ent1 = mSceneMgr->createEntity( "Robot", "chair.mesh" );
    // create a child node attached to the root scene node.
    SceneNode *node1 = mSceneMgr->getRootSceneNode()->createChildSceneNode( "RobotNode" );
    // around the z-axis, counterclockwise
    //node1->yaw( Degree( -90 ) );
    // rotate around the x-axis, counterclockwise
    //node1->pitch( Degree( -90 ) );
    // rotate around the y-axis, counterclockwise
    node1->roll( Degree( 90 ) );
    node1->yaw( Degree( 15 ) );
    node1->translate( Vector3( 0, 6, 0 ) );

    // already included in the mesh.
    //ent1->setMaterialName("Mmud/ashwood");
    ent1->setCastShadows(true);

    // attach the robot to the scene node.
    node1->attachObject( ent1 );
    logger->logMessage("leaving createScene()");
}

//-------------------------------------------------------------------------------------
void OgreApplication::createResourceListener(void)
{

}

//-------------------------------------------------------------------------------------
void OgreApplication::loadResources(void)
{
   // Initialise, parse scripts etc
   ResourceGroupManager::getSingleton().initialiseAllResourceGroups();
}

//-------------------------------------------------------------------------------------
void OgreApplication::destroyScene(void)
{
}

//-------------------------------------------------------------------------------------
bool OgreApplication::frameStarted(const FrameEvent& evt)
{
   return true;
}

//-------------------------------------------------------------------------------------
bool OgreApplication::frameEnded(const FrameEvent& evt)
{
   return true;
}

//-------------------------------------------------------------------------------------
void OgreApplication::createFrameListener(void)
{
    mFrameListener= new ExampleFrameListener(mWindow, mCamera);
    // show the debug stuff, comment this out if you don't like it.
    mFrameListener->showDebugOverlay(true);
    mRoot->addFrameListener(mFrameListener);
}



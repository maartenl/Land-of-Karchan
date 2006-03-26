// ----------------------------------------------------------------------------
// Include the main OGRE header files
// Ogre.h just expands to including lots of individual OGRE header files
// ----------------------------------------------------------------------------
#include <Ogre.h>
// ----------------------------------------------------------------------------
// Include the OGRE example framework
// This includes the classes defined to make getting an OGRE application running
// a lot easier. It automatically sets up all the main objects and allows you to
// just override the bits you want to instead of writing it all from scratch.
// ----------------------------------------------------------------------------
#include <ExampleApplication.h>

namespace Mmud {

// ----------------------------------------------------------------------------
// Define the application object
// This is derived from ExampleApplication which is the class OGRE provides to
// make it easier to set up OGRE without rewriting the same code all the time.
// You can override extra methods of ExampleApplication if you want to further
// specialise the setup routine, otherwise the only mandatory override is the
// 'createScene' method which is where you set up your own personal scene.
// ----------------------------------------------------------------------------
class SampleApp : public ExampleApplication
{
public:
    // Basic constructor
    SampleApp()
    {}

protected:

    // Just override the mandatory create scene method
    void createScene(void)
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
        ent->setMaterialName("Examples/Rockwall");
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

    virtual void createCamera(void)
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

    virtual void createViewports(void)
    {
        Viewport* vp = mWindow->addViewport(mCamera, 0, 0.0f, 0.0f, 1.0f, 1.0f);
        vp->setBackgroundColour(ColourValue(0,0,0));
        // Alter the camera aspect ratio to match the viewport
        // not really necessary as we are using the entire screen,
        // but what the hell
        mCamera->setAspectRatio(Real(vp->getActualWidth()) / Real(vp->getActualHeight()));
    }
};


// ----------------------------------------------------------------------------
// Main function, just boots the application object
// ----------------------------------------------------------------------------
#if OGRE_PLATFORM == OGRE_PLATFORM_WIN32
#define WIN32_LEAN_AND_MEAN
#include "windows.h"
INT WINAPI WinMain( HINSTANCE hInst, HINSTANCE, LPSTR strCmdLine, INT )
#else
int main(int argc, char **argv)
#endif
{
    // Create application object
    SampleApp app;

    try
    {
        app.go();
    }
    catch( Exception& e )
    {
#if OGRE_PLATFORM == OGRE_PLATFORM_WIN32
        MessageBox( NULL, e.getFullDescription().c_str(), "An exception has occured!", MB_OK | MB_ICONERROR | MB_TASKMODAL);
#else

        std::cerr << "An exception has occured: " << e.getFullDescription();
#endif
    }

    return 0;
}

} // end of namespace

//==============================================================================
/*
    Software License Agreement (BSD License)
    Copyright (c) 2003-2016, CHAI3D.
    (www.chai3d.org)

    All rights reserved.

    Redistribution and use in source and binary forms, with or without
    modification, are permitted provided that the following conditions
    are met:

    * Redistributions of source code must retain the above copyright
    notice, this list of conditions and the following disclaimer.

    * Redistributions in binary form must reproduce the above
    copyright notice, this list of conditions and the following
    disclaimer in the documentation and/or other materials provided
    with the distribution.

    * Neither the name of CHAI3D nor the names of its contributors may
    be used to endorse or promote products derived from this software
    without specific prior written permission.

    THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
    "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
    LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
    FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
    COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
    INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
    BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
    LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
    CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
    LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
    ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
    POSSIBILITY OF SUCH DAMAGE.

    \author    <http://www.chai3d.org>
    \author    Francois Conti          modifications : Wilfrid Grassi  28/12/2016
    \version   3.2.0 $Rev: 1925 $
*/
//==============================================================================

//------------------------------------------------------------------------------
#include "../../../src/chai3d.h"
#include "COculus.h"
//------------------------------------------------------------------------------
#include <../../../extras/GLFW/include/GLFW/glfw3.h>
#include "../../../src/virtualpicturemapping/cFileMappingPictureClient.h"
#include <../../../src/FMP_Telemetrie/cFileMappingTelloEduTelemetryClient.h>
#include <../../../src/FMP_PosRobotHaptique/cFileMappingRobotHaptiqueDroneServeur.h>
#include <../../../src/virtualpicturemapping/cFileMappingPictureServeur.h>
#include <FMPRobotHaptiqueCHAIVR/cFileMappingRobotHaptiqueDroneClientCHAIVR.h>
#include <FMP_PosRobotHaptique/cFileMappingRobotHaptiqueDroneClient.h>
//------------------------------------------------------------------------------
using namespace chai3d;
using namespace std;
//------------------------------------------------------------------------------

//------------------------------------------------------------------------------
// GENERAL SETTINGS
//------------------------------------------------------------------------------

// stereo Mode
/*
    C_STEREO_DISABLED:            Stereo is disabled
    C_STEREO_ACTIVE:              Active stereo for OpenGL NVDIA QUADRO cards
    C_STEREO_PASSIVE_LEFT_RIGHT:  Passive stereo where L/R images are rendered next to each other
    C_STEREO_PASSIVE_TOP_BOTTOM:  Passive stereo where L/R images are rendered above each other
*/
cStereoMode stereoMode = C_STEREO_ACTIVE;

// fullscreen mode
bool fullscreen = false;

// mirrored display
bool mirroredDisplay = false;

//------------------------------------------------------------------------------
// OCULUS RIFT
//------------------------------------------------------------------------------

// display context
cOVRRenderContext renderContext;

// oculus device    
cOVRDevice oculusVR;


//------------------------------------------------------------------------------
// DECLARED VARIABLES
//------------------------------------------------------------------------------

// create new bitmap object
cBitmap* bitmap = new cBitmap();

// a world that contains all objects of the virtual environment
cWorld* world;

// a camera to render the world in the window display
cCamera* camera;

// a light source to illuminate the objects in the world
//cSpotLight* light;
cDirectionalLight* light;

// a virtual tool representing the haptic device in the scene
cToolCursor* tool;

// a colored background
cBackground* background;

// global variable to store the position [m] of each haptic device
cVector3d hapticDevicePosition;

// a flag that indicates if the haptic simulation is currently running
bool simulationRunning = false;

// a flag that indicates if the haptic simulation has terminated
bool simulationFinished = true;

// a frequency counter to measure the simulation graphic rate
cFrequencyCounter freqCounterGraphics;

// a frequency counter to measure the simulation haptic rate
cFrequencyCounter freqCounterHaptics;

// haptic thread
cThread* hapticsThread;

//File Mapping Thread
cThread* FMPThreadPicture;

//File Mapping Thread
cThread* FMPThreadPictureTelemetrie;

//Gestion Collision Manette Thread
cThread* ThreadGestionCollisionManetteG;

//Gestion Collision Manette Thread
cThread* ThreadGestionCollisionManetteD;

// a handle to window display context
GLFWwindow* window = NULL;

// current width of window
int width = 0;

// current height of window
int height = 0;

// swap interval for the display context (vertical synchronization)
int swapInterval = 1;

// root resource path
string resourceRoot;

// Spheres et Dimension pour Contrainte du Robot
cMesh* SphereMax;
cShapeSphere* SphereMed;
cShapeSphere* SphereMin;
double HauteurCylindre = 0.3;
//Obtention des dimensions de la fenetre et conversion avec l unite utilisee
double RayonMax;
double RayonMin;
double Resolution = 40;

// Cones de direction
cShapeCylinder* CylinderXMoins;
cShapeCylinder* CylinderXPlus;
cShapeCylinder* CylinderYMoins;
cShapeCylinder* CylinderYPlus;
cShapeCylinder* CylinderZMoins;
cShapeCylinder* CylinderZPlus;

// FileMapping Picture Client
cFileMappingPictureClient* monClientFMPPictureDrone1 = NULL;
std::vector<unsigned char> monByteArrayImage;
cImagePtr ImageCompose;
cBitmap* BitmapBackgroundVideoStream = new cBitmap();
bool ImageDisponible = false;

double RapportTaille = 8.0 / 3.0;

// Image de la camera
cImagePtr Image_Camera = cImage::create();
cFileMappingPictureServeur* monServeurFMPPictureDrone = NULL;
cVirtualPicture* maVirtualPictureCamera = new cVirtualPicture();
bool ImageCameraReady = false;
unsigned char* monByteArrayImageServeur;

//Texture qui represente la video des drones pour la VR
cTexture2dPtr texture;
cMesh* canvasTelemetry;

// Recuperation de la position du robot haptique
cFileMappingRobotHaptiqueDroneClientCHAIVR* monClientFMPPosRobot = NULL;
cFileMappingRobotHaptiqueDroneClient* monClientFMPForceRobot = NULL;
int AxeXForce = 0;
int AxeYForce = 0;
int AxeZForce = 0;

// Recuperation de la telemetrie
cFileMappingPictureClient* monClientFMPPictureTelemetrie = NULL;
cTexture2dPtr texture_telemetrie;
cImagePtr Image_Telemetrie;
//cVirtualPicture* maVirtualPictureTelemetrie = new cVirtualPicture();
std::vector<unsigned char> monByteArrayImageTelemetrie;
bool ImageTelemetrieDisponible = false;

//Recuperation des inputs des manettes
ovrInputState inputState;
ovrTrackingState trackState;

//Tool pour chaque manette
cToolCursor* tool_MainD;
cToolCursor* tool_MainG;

double displayMidpointSeconds;


ovrVector3f posLeft;
ovrVector3f posRight;

cHapticPoint* hapticPointG;
cHapticPoint* hapticPointD;

bool isGrabG = false;
bool isGrabD = false;

cMesh* BaseDroiteDirection;
cMesh* CubeDirectionDroit;

cMesh* BaseGaucheDirection;
cMesh* CubeDirectionGauche;

cVector3d bMinBaseDroite;
cVector3d bMaxBaseDroite;
cVector3d bMinBaseGauche;
cVector3d bMaxBaseGauche;

cVector3d bMinCubeDroit;
cVector3d bMaxCubeDroit;
cVector3d bMinCubeGauche;
cVector3d bMaxCubeGauche;

bool isGrabCubeD = false;
bool isGrabCubeG = false;

double OffsetXD = 0.0;
double OffsetYD = 0.0;
double OffsetXG = 0.0;
double OffsetYG = 0.0;

double smoothFactor = 0.15; // Facteur de lissage

double currentX;
double newX;

double currentY;
double newY;

//Defintion des limites de mouvements des Cubes de Direction
double MaxXDroite;
double MaxYDroite;
double MaxXGauche;
double MaxYGauche;

//Textures indicatrices
cTexture2dPtr textureIndicatrice_Droite;
cTexture2dPtr textureIndicatrice_Gauche;

//Serveur FMP de diffusion des commandes des manettes pour controler les drones


//------------------------------------------------------------------------------
// DECLARED MACROS
//------------------------------------------------------------------------------
// convert to resource path
#define RESOURCE_PATH(p)    (char*)((resourceRoot+string(p)).c_str())

//------------------------------------------------------------------------------
// DECLARED FUNCTIONS
//------------------------------------------------------------------------------

//Methode permettant de reset les cones de direction
void ResetConesDirection();

// callback when an error GLFW occurs
void errorCallback(int error, const char* a_description);

// callback when a key is pressed
void keyCallback(GLFWwindow* a_window, int a_key, int a_scancode, int a_action, int a_mods);

// this function renders the scene
void updateGraphics(ovrSizei _size);

// this function contains the main haptics simulation loop
void updateHaptics(void);

// methode permettant la composition des deux images des drones
void ComposeImage();

//Methode permettant d acquerir la position du robot haptique
void GetPosRobotHaptique();

//Methode permettant d acquerir les directions du robot haptique
void GetForceRobotHaptique();

//Methode permettant d acquerir l image contenant la telemetrie
void GetPictureTelemetrie();

//Methode permettant la gestion des collisions avec la mannette G et l environnement
void HandleCollisionManettesG();

//Methode permettant la gestion des collisions avec la mannette D et l environnement
void HandleCollisionManettesD();

//Methode permettant la gestion des deplacements des cube de position par rapport aux manettes
void ChangePosMainCube();

// this function closes the application
void close(void);


//==============================================================================
/*
    DEMO:   12-polygons.cpp

    This example illustrates how to build an object composed of triangle
    with individual colors. A finger-proxy algorithm is used to compute
    the interaction force between the tool and the object.
    The color of each triangle can be switched to red by selecting and clicking
    the window display with the computer mouse.
*/
//==============================================================================

int main(int argc, char* argv[])
{
    _CrtSetDbgFlag(_CRTDBG_ALLOC_MEM_DF | _CRTDBG_LEAK_CHECK_DF);
    //--------------------------------------------------------------------------
    // INITIALIZATION
    //--------------------------------------------------------------------------

    cout << endl;
    cout << "-----------------------------------" << endl;
    cout << "CHAI3D VR" << endl;
    cout << "Copyright 2003-2016" << endl;
    cout << "CHAI 3D v3.2 - 64bits - VS2015 - 28/12/2016" << endl;
    cout << "-----------------------------------" << endl;

    //--------------------------------------------------------------------------
     // SETUP DISPLAY CONTEXT
     //--------------------------------------------------------------------------
  
     // initialize GLFW library
    if (!glfwInit())
    {
        cout << "failed initialization" << endl;
        cSleepMs(1000);
        return 1;
    }

    // set error callback
    glfwSetErrorCallback(errorCallback);

    // set OpenGL version
    glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 2);
    glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 0);

    // create display context
    GLFWwindow* window = glfwCreateWindow(640, 480, "Surveillance Drones VR", NULL, NULL);
    if (!window)
    {
        cout << "failed to create window" << endl;
        cSleepMs(1000);
        glfwTerminate();
        return 1;
    }

    // set key callback
    glfwSetKeyCallback(window, keyCallback);

    // set current display context
    glfwMakeContextCurrent(window);

    // sets the swap interval for the current display context
    glfwSwapInterval(0);

#ifdef GLEW_VERSION
    // initialize GLEW library
    if (glewInit() != GLEW_OK)
    {
        oculusVR.destroyVR();
        renderContext.destroy();
        glfwTerminate();
        return 1;
    }
#endif
    

    
    // initialize oculus
    if (!oculusVR.initVR())
    {
        cout << "failed to initialize Oculus" << endl;
        cSleepMs(1000);
        glfwTerminate();
        return 1;
    }
    

    // get oculus display resolution
    ovrSizei hmdResolution = oculusVR.getResolution();

    // setup mirror display on computer screen
    ovrSizei windowSize = { hmdResolution.w / 2, hmdResolution.h / 2 };

    // inialize buffers
    if (!oculusVR.initVRBuffers(windowSize.w, windowSize.h))
    {
        cout << "failed to initialize Oculus buffers" << endl;
        cSleepMs(1000);
        oculusVR.destroyVR();
        renderContext.destroy();
        glfwTerminate();
        return 1;
    }

    // set window size
    glfwSetWindowSize(window, windowSize.w, windowSize.h);



    //--------------------------------------------------------------------------
    // WORLD - CAMERA - LIGHTING
    //--------------------------------------------------------------------------

    // create a new world.
    world = new cWorld();

    world->m_backgroundColor.setBrown();

    // create a camera and insert it into the virtual world
    camera = new cCamera(world);
    world->addChild(camera);

    //On oriente la camera vers lecran
    camera->set(cVector3d(5.0, 0.0, 0.0),    // Position de la camera (eye)
        cVector3d(0.0, 0.0, 0.0),     // Point d interet (target)
        cVector3d(0.0, 0.0, 1.0));    // Vecteur "up

    // set the near and far clipping planes of the camera
    // anything in front or behind these clipping planes will not be rendered
    camera->setClippingPlanes(0.06, 25.0);

    camera->setUseMultipassTransparency(true);

    camera->setStereoMode(stereoMode);

    //Creation des commandes de controle des dornes via les manettes

    double ToolMainRadius = 0.02;

    //Base Direction Droite//
    BaseDroiteDirection = new cMesh();

    //cCreateBox(BaseDroiteDirection, 0.28, 0.28, 0.015);

    cCreatePlane(BaseDroiteDirection, 0.28, 0.28);

    camera->addChild(BaseDroiteDirection);

    BaseDroiteDirection->setLocalPos(-0.35, 0.58, -0.56);

    BaseDroiteDirection->m_material->setRedCrimson();

    BaseDroiteDirection->computeAllNormals();

    BaseDroiteDirection->computeBoundaryBox(true);

    BaseDroiteDirection->createAABBCollisionDetector(ToolMainRadius);

    BaseDroiteDirection->m_material->setStiffness(0.1 * 100);

    BaseDroiteDirection->m_material->setStaticFriction(0.8);

    BaseDroiteDirection->m_material->setHapticTriangleSides(true, true);

    BaseDroiteDirection->setHapticEnabled(true);

    BaseDroiteDirection->setShowEnabled(true);

    BaseDroiteDirection->setUseMaterial(false);

    BaseDroiteDirection->setUseTexture(true);


    //Affichage de l image indicatrice Droite
    textureIndicatrice_Droite = cTexture2d::create();

    // load image files
    bool fileload = textureIndicatrice_Droite->loadFromFile(RESOURCE_PATH("Droite.png"));
    if (!fileload)
    {
#if defined(_MSVC)
        fileload = textureIndicatrice_Droite->loadFromFile("Droite.png");
#endif
    }
    if (!fileload)
    {
        cout << "Error - image file failed to load correctly." << endl;
        close();
        return (-1);
    }

    BaseDroiteDirection->setTexture(textureIndicatrice_Droite);

    //Cube Direction Droit//

    CubeDirectionDroit = new cMesh();

    cCreateBox(CubeDirectionDroit, 0.04,0.04, 0.09);

    BaseDroiteDirection->addChild(CubeDirectionDroit);

    bMinBaseDroite = BaseDroiteDirection->getBoundaryMin();
    bMaxBaseDroite = BaseDroiteDirection->getBoundaryMax();

    CubeDirectionDroit->m_material->setGray();

    CubeDirectionDroit->computeAllNormals();

    CubeDirectionDroit->computeBoundaryBox(true);

    CubeDirectionDroit->createAABBCollisionDetector(ToolMainRadius);

    CubeDirectionDroit->m_material->setStiffness(0.1 * 100);

    CubeDirectionDroit->m_material->setStaticFriction(0.8);

    CubeDirectionDroit->m_material->setHapticTriangleSides(true, true);

    CubeDirectionDroit->setHapticEnabled(true);

    CubeDirectionDroit->setShowEnabled(true);

    CubeDirectionDroit->setUseMaterial(true);

    bMinCubeDroit = CubeDirectionDroit->getBoundaryMin();
    bMaxCubeDroit = CubeDirectionDroit->getBoundaryMax();

    CubeDirectionDroit->setLocalPos(0.0, 0.0, bMaxBaseDroite.z() - bMinBaseDroite.z() + (bMaxCubeDroit.z() - bMinCubeDroit.z())/2.0);


    //Base Direction Gauche//
    BaseGaucheDirection = new cMesh();

    //cCreateBox(BaseGaucheDirection, 0.28, 0.28, 0.015);

    cCreatePlane(BaseGaucheDirection, 0.28, 0.28);

    camera->addChild(BaseGaucheDirection);

    BaseGaucheDirection->setLocalPos(-0.35, -0.58, -0.56);

    BaseGaucheDirection->m_material->setRedCrimson();

    BaseGaucheDirection->computeAllNormals();

    BaseGaucheDirection->computeBoundaryBox(true);

    BaseGaucheDirection->createAABBCollisionDetector(ToolMainRadius);

    BaseGaucheDirection->m_material->setStiffness(0.1 * 100);

    BaseGaucheDirection->m_material->setStaticFriction(0.8);

    BaseGaucheDirection->m_material->setHapticTriangleSides(true, true);

    BaseGaucheDirection->setHapticEnabled(true);

    BaseGaucheDirection->setShowEnabled(true);

    BaseGaucheDirection->setUseMaterial(false);

    BaseGaucheDirection->setUseTexture(true);

    //Affichage de l image indicatrice Droite
    textureIndicatrice_Gauche = cTexture2d::create();

    // load image files
    fileload = textureIndicatrice_Gauche->loadFromFile(RESOURCE_PATH("Gauche.png"));
    if (!fileload)
    {
#if defined(_MSVC)
        fileload = textureIndicatrice_Gauche->loadFromFile("Gauche.png");
#endif
    }
    if (!fileload)
    {
        cout << "Error - image file failed to load correctly." << endl;
        close();
        return (-1);
    }

    BaseGaucheDirection->setTexture(textureIndicatrice_Gauche);

    //Cube Direction Gauche//

    CubeDirectionGauche = new cMesh();

    cCreateBox(CubeDirectionGauche, 0.04, 0.04, 0.09);

    BaseGaucheDirection->addChild(CubeDirectionGauche);

    bMinBaseGauche = BaseGaucheDirection->getBoundaryMin();
    bMaxBaseGauche = BaseGaucheDirection->getBoundaryMax();

    CubeDirectionGauche->m_material->setGray();

    CubeDirectionGauche->computeAllNormals();

    CubeDirectionGauche->computeBoundaryBox(true);

    CubeDirectionGauche->createAABBCollisionDetector(ToolMainRadius);

    CubeDirectionGauche->m_material->setStiffness(0.1 * 100);

    CubeDirectionGauche->m_material->setStaticFriction(0.8);

    CubeDirectionGauche->m_material->setHapticTriangleSides(true, true);

    CubeDirectionGauche->setHapticEnabled(true);

    CubeDirectionGauche->setShowEnabled(true);

    CubeDirectionGauche->setUseMaterial(true);

    bMinCubeGauche = CubeDirectionGauche->getBoundaryMin();
    bMaxCubeGauche = CubeDirectionGauche->getBoundaryMax();

    CubeDirectionGauche->setLocalPos(0.0, 0.0, bMaxBaseGauche.z() - bMinBaseGauche.z() + (bMaxCubeGauche.z() - bMinCubeGauche.z()) / 2.0);

    MaxXDroite = bMaxBaseDroite.x() - bMinBaseDroite.x() - ((bMaxCubeDroit.x() - bMinCubeDroit.x()) / 1.2);
    MaxYDroite = bMaxBaseDroite.y() - bMinBaseDroite.y() - ((bMaxCubeDroit.y() - bMinCubeDroit.y()) / 1.2);
    MaxXGauche = bMaxBaseGauche.x() - bMinBaseGauche.x() - ((bMaxCubeGauche.x() - bMinCubeGauche.x()) / 1.2);
    MaxYGauche = bMaxBaseGauche.y() - bMinBaseGauche.y() - ((bMaxCubeGauche.y() - bMinCubeGauche.y()) / 1.2);

    // create a canvas mesh plane to display Telemetry informations
    canvasTelemetry = new cMesh();

    // create a plane

    cCreatePlane(canvasTelemetry, 0.7, 0.4);

    // add object to world
    camera->addChild(canvasTelemetry);

    // set the position of the object
    canvasTelemetry->setTransparencyLevel(0.5, false, false, true);

    canvasTelemetry->m_material->setWhite();

    canvasTelemetry->rotateAboutLocalAxisDeg(cVector3d(0.0, 0.0, 1.0), 90.0); //Permet a la texture de safficher dans le bon sens

    canvasTelemetry->rotateAboutGlobalAxisDeg(cVector3d(0.0, 1.0, 0.0), 60);

    canvasTelemetry->setLocalPos(-0.8, 0.0, -0.4);

    texture_telemetrie = cTexture2d::create();


    canvasTelemetry->setTexture(texture_telemetrie);

    canvasTelemetry->setUseTexture(true);

    // create a light source
    light = new cDirectionalLight(world);

    // attach light to camera
    camera->addChild(light);

    // enable light source
    light->setEnabled(true);

    // define the direction of the light beam
    light->setDir(-3.0, -0.5, 0.0);

    // set lighting conditions
    light->m_ambient.set(0.4f, 0.4f, 0.4f);
    light->m_diffuse.set(0.8f, 0.8f, 0.8f);
    light->m_specular.set(1.0f, 1.0f, 1.0f);


    //--------------------------------------------------------------------------
    // TOOLS
    //--------------------------------------------------------------------------

    displayMidpointSeconds = ovr_GetPredictedDisplayTime(oculusVR.getSession(), 0);

    trackState = ovr_GetTrackingState(oculusVR.getSession(), displayMidpointSeconds, ovrTrue);

    posLeft = trackState.HandPoses[ovrHand_Left].ThePose.Position;

    posRight = trackState.HandPoses[ovrHand_Right].ThePose.Position;


    //Configuration du Tool Main Gauche

    tool_MainG = new cToolCursor(world);

    camera->addChild(tool_MainG);

    tool_MainG->setRadius(ToolMainRadius);

    tool_MainG->setShowContactPoints(true, false);

    tool_MainG->m_hapticPoint->m_sphereProxy->m_material->setBlueRoyal();

    tool_MainG->enableDynamicObjects(true);

    tool_MainG->setShowEnabled(true);

    hapticPointG = tool_MainG->getHapticPoint(0);

    //Configuration du Tool Main Droite

    tool_MainD = new cToolCursor(world);

    camera->addChild(tool_MainD);

    tool_MainD->setRadius(ToolMainRadius);

    tool_MainD->setShowContactPoints(true, false);

    tool_MainD->m_hapticPoint->m_sphereProxy->m_material->setGreen();

    tool_MainD->enableDynamicObjects(true);

    tool_MainD->setShowEnabled(true);

    hapticPointD = tool_MainD->getHapticPoint(0);

    // create a tool (cursor) and insert into the world
    tool = new cToolCursor(world);

    world->addChild(tool);

    // define the radius of the tool (sphere)
    double toolRadius = 0.05;

    // define a radius for the tool
    tool->setRadius(toolRadius);

    // hide the device sphere. only show proxy.
    tool->setShowContactPoints(true, false);

    // create a red cursor
    tool->m_hapticPoint->m_sphereProxy->m_material->setRed();

    // enable if objects in the scene are going to rotate of translate
    // or possibly collide against the tool. If the environment
    // is entirely static, you can set this parameter to "false"
    tool->enableDynamicObjects(false);

    // map the physical workspace of the haptic device to a larger virtual workspace.
    tool->setWorkspaceRadius(0.8);

    // haptic forces are enabled only if small forces are first sent to the device;
    // this mode avoids the force spike that occurs when the application starts when 
    // the tool is located inside an object for instance. 
    tool->setWaitForSmallForce(true);

    tool->setShowEnabled(true);

    // start the haptic tool
    tool->start();


    //--------------------------------------------------------------------------
    // CREATE OBJECTS
    //--------------------------------------------------------------------------

    RayonMax = 0.8 - HauteurCylindre;

    RayonMin = RayonMax - 0.1;

    //--------------------------------------------------------------//
    SphereMax = new cMesh();

    world->addChild(SphereMax);

    cCreateSphere(SphereMax, RayonMax, Resolution, Resolution);

    SphereMax->m_material->setWhite();
    SphereMax->m_material->setTransparencyLevel(0.1);
    SphereMax->setUseTransparency(true);

    SphereMax->computeAllNormals();

    // we indicate that we ware rendering triangles by using specific colors for each of them (see above)
    SphereMax->setUseVertexColors(false);

    // we indicate that we also using material properties. If you set this parameter to  false 
    // you will notice that only vertex colors are used to render triangle, and lighting
    // will not longer have any effect.
    SphereMax->setUseMaterial(true);

    // compute a boundary box
    SphereMax->computeBoundaryBox(true);

    // compute collision detection algorithm
    SphereMax->createAABBCollisionDetector(toolRadius);

    // define a default stiffness for the object
    SphereMax->m_material->setStiffness(0.1 * 100);

    SphereMax->m_material->setStaticFriction(0.8);

    // render triangles hapticaly on both sides
    SphereMax->m_material->setHapticTriangleSides(true, true);

    SphereMax->setHapticEnabled(false);

    SphereMax->setShowEnabled(true);


    //--------------------------------------------------------------//
    SphereMed = new cShapeSphere(RayonMax);
    world->addChild(SphereMed);

    SphereMed->m_material->setBlue();

    SphereMed->m_material->setTransparencyLevel(0.1);

    SphereMed->setUseTransparency(true);

    SphereMed->m_material->setViscosity(0.7 * 100);            // % of maximum linear damping

    SphereMed->createEffectViscosity();

    SphereMed->computeBoundaryBox();

    SphereMed->setShowEnabled(true);

    //--------------------------------------------------------------//
    SphereMin = new cShapeSphere(RayonMin);
    world->addChild(SphereMin);

    SphereMin->m_material->setBlueLight();

    SphereMin->m_material->setTransparencyLevel(0.1);

    SphereMin->setUseTransparency(true);

    SphereMin->m_material->setViscosity(0);            // % of maximum linear damping

    SphereMin->createEffectViscosity();

    SphereMin->setHapticEnabled(false);

    SphereMin->setShowEnabled(true);



    //Forme permettant d indiquer la direction

    CylinderZPlus = new cShapeCylinder(0.08, 0.001, HauteurCylindre);

    SphereMax->addChild(CylinderZPlus);

    CylinderZPlus->m_material->setGreen();

    CylinderZPlus->setLocalPos(0.0, 0.0, RayonMax);

    CylinderZPlus->setShowEnabled(false);

    CylinderZMoins = new cShapeCylinder(0.08, 0.001, HauteurCylindre);

    SphereMax->addChild(CylinderZMoins);

    CylinderZMoins->m_material->setGreen();

    CylinderZMoins->setLocalPos(0.0, 0.0, -RayonMax);

    CylinderZMoins->rotateAboutLocalAxisDeg(cVector3d(0.0, 1.0, 0.0), 180.0);

    CylinderZMoins->setShowEnabled(false);

    CylinderYPlus = new cShapeCylinder(0.08, 0.001, HauteurCylindre);

    SphereMax->addChild(CylinderYPlus);

    CylinderYPlus->m_material->setBlue();

    CylinderYPlus->setLocalPos(0.0, RayonMax, 0.0);

    CylinderYPlus->rotateAboutLocalAxisDeg(cVector3d(1.0, 0.0, 0.0), -90.0);

    CylinderYPlus->setShowEnabled(false);

    CylinderYMoins = new cShapeCylinder(0.08, 0.001, HauteurCylindre);

    SphereMax->addChild(CylinderYMoins);

    CylinderYMoins->m_material->setBlue();

    CylinderYMoins->setLocalPos(0.0, -RayonMax, 0.0);

    CylinderYMoins->rotateAboutLocalAxisDeg(cVector3d(1.0, 0.0, 0.0), 90.0);

    CylinderYMoins->setShowEnabled(false);

    CylinderXPlus = new cShapeCylinder(0.08, 0.001, HauteurCylindre);

    SphereMax->addChild(CylinderXPlus);

    CylinderXPlus->m_material->setRed();

    CylinderXPlus->setLocalPos(RayonMax, 0.0, 0.0);

    CylinderXPlus->rotateAboutLocalAxisDeg(cVector3d(0.0, 1.0, 0.0), 90.0);

    CylinderXPlus->setShowEnabled(false);

    CylinderXMoins = new cShapeCylinder(0.08, 0.001, HauteurCylindre);

    SphereMax->addChild(CylinderXMoins);

    CylinderXMoins->m_material->setRed();

    CylinderXMoins->setLocalPos(-RayonMax, 0.0, 0.0);

    CylinderXMoins->rotateAboutLocalAxisDeg(cVector3d(0.0, 1.0, 0.0), -90.0);

    CylinderXMoins->setShowEnabled(false);

    //--------------------------------------------------------------------------
    // LOGO CHAI3D 
    //--------------------------------------------------------------------------

    texture = cTexture2d::create();

    // Parametres du secteur de cylindre
    int numSegments = 512;         // Resolution le long de l arc
    double radius = 25.0;          // Rayon du cylindre
    double height = 15;         // Hauteur totale du cylindre (axe Z)
    double angleRange = 2*C_PI/4.2; 

    // Creation du maillage pour le secteur de cylindre
    cMesh* cylinderSector = new cMesh();
    world->addChild(cylinderSector);

    // Creation des vertices le long de l arc
    for (int i = 0; i <= numSegments; i++)
    {
        double angle = (angleRange * i / numSegments) - (angleRange / 2.0);
        // Coordonnees dans le plan XY en utilisant laforme polaire ce qui permet de calculer la position d un point sur le cercle de rayon radius
        double x = radius * cos(angle);
        double y = radius * sin(angle);

        // Deux vertices pour chaque segment : un pour le bas et un pour le haut
        int idxBottom = cylinderSector->newVertex(x, y, -height / 2.0);
        int idxTop = cylinderSector->newVertex(x, y, height / 2.0);

        // Pour l axe  u , on repartit uniformement sur l arc (0 a 1)
        double u = (double)i / numSegments;
        // Pour  v , on utilise 0 pour le bas et 1 pour le haut
        double v_bottom = 0.0;
        double v_top = 1.0;

        cylinderSector->m_vertices->setTexCoord(idxBottom, u, v_bottom);
        cylinderSector->m_vertices->setTexCoord(idxTop, u, v_top);
    }

    // Creation des triangles pour former la surface laterale
    for (int i = 0; i < numSegments; i++)
    {
        int bottomIdx1 = i * 2;
        int topIdx1 = i * 2 + 1;
        int bottomIdx2 = (i + 1) * 2;
        int topIdx2 = (i + 1) * 2 + 1;

        // Premier triangle
        cylinderSector->newTriangle(topIdx2, topIdx1, bottomIdx1);
        // Deuxie2me triangle
        cylinderSector->newTriangle(bottomIdx2, topIdx2, bottomIdx1);
    }

    cylinderSector->rotateAboutGlobalAxisDeg(cVector3d(0.0,0.0,1.0),180);
    
    // Appliquer la texture sur le maillage du cylindre
    cylinderSector->setTexture(texture);
    cylinderSector->setUseTexture(true);

    // (Optionnel) Desactiver le culling et la gestion de materiaux si besoin
    cylinderSector->setUseCulling(false);
    cylinderSector->setUseMaterial(false);


    //--------------------------------------------------------------------------
    // FILE MAPPING POS ROBOT HAPTIQUE 
    //--------------------------------------------------------------------------

    monClientFMPPosRobot = new cFileMappingRobotHaptiqueDroneClientCHAIVR(false);
    monClientFMPPosRobot->OpenClient("RobotHaptique_PositionCHAIVR");

    monClientFMPForceRobot = new cFileMappingRobotHaptiqueDroneClient(false);
    monClientFMPForceRobot->OpenClient("RobotHaptique_Position");

    //--------------------------------------------------------------------------
    // WIDGETS
    //--------------------------------------------------------------------------


    // create a background
    background = new cBackground();
    camera->m_backLayer->addChild(background);

    // set background color properties
    // set background properties
    // Fond noir uni
    background->setCornerColors(
        cColorf(0.0f, 0.0f, 0.0f),  // haut gauche
        cColorf(0.0f, 0.0f, 0.0f),  // haut droit
        cColorf(0.0f, 0.0f, 0.0f),  // bas gauche
        cColorf(0.0f, 0.0f, 0.0f)); // bas droit


    //------------------------------------------------------------------------------
    //  FILE MAPPING PICTURE
    //------------------------------------------------------------------------------

    monClientFMPPictureDrone1 = new cFileMappingPictureClient(false);
    monClientFMPPictureDrone1->OpenClient("Video_Drone");

    monClientFMPPictureTelemetrie = new cFileMappingPictureClient(false);
    monClientFMPPictureTelemetrie->OpenClient("Telemetrie_CHAI3D");

    //--------------------------------------------------------------------------
    // START SIMULATION
    //--------------------------------------------------------------------------
  

    BitmapBackgroundVideoStream->setShowEnabled(true);

    SphereMax->setLocalPos(tool->getDeviceGlobalPos() - SphereMax->getGlobalPos());
    SphereMed->setLocalPos(tool->getDeviceGlobalPos() - SphereMed->getGlobalPos());
    SphereMin->setLocalPos(tool->getDeviceGlobalPos() - SphereMin->getGlobalPos());
    tool_MainD->setDeviceLocalPos(0.0, 0.0, 0.0);
    tool_MainG->setDeviceLocalPos(0.0, 0.0, 0.0);
    cout << "###################################################" << endl;
    cout << "Keyboard Options:" << endl << endl;
    cout << "[q] - Exit application" << endl;
    cout << endl << endl;

    //Image de fond
    camera->m_backLayer->addChild(BitmapBackgroundVideoStream);


    // create a thread which starts the main haptics rendering loop
    hapticsThread = new cThread();
    hapticsThread->start(updateHaptics, CTHREAD_PRIORITY_HAPTICS);

    // creation des threads de FMP
    FMPThreadPicture = new cThread();
    FMPThreadPicture->start(ComposeImage, CTHREAD_PRIORITY_GRAPHICS);

    FMPThreadPictureTelemetrie = new cThread();
    FMPThreadPictureTelemetrie->start(GetPictureTelemetrie, CTHREAD_PRIORITY_GRAPHICS);

    ThreadGestionCollisionManetteG = new cThread();
    ThreadGestionCollisionManetteG->start(HandleCollisionManettesG, CTHREAD_PRIORITY_HAPTICS);

    ThreadGestionCollisionManetteD = new cThread();
    ThreadGestionCollisionManetteD->start(HandleCollisionManettesD, CTHREAD_PRIORITY_HAPTICS);


    // setup callback when application exits
    atexit(close);

    cSleepMs(500);
    //--------------------------------------------------------------------------
    // MAIN GRAPHIC LOOP
    //--------------------------------------------------------------------------

    // recenter oculus
    oculusVR.recenterPose();
    int index = 0;
    // main graphic rendering loop
    while (!glfwWindowShouldClose(window) && !simulationFinished)
    {
        
        int width, height;
        glfwGetFramebufferSize(window, &width, &height);

        glfwGetWindowSize(window, &width, &height);

        world->updateShadowMaps(false, false);
        
        // start rendering
        oculusVR.onRenderStart();

        // render frame for each eye
        for (int eyeIndex = 0; eyeIndex < ovrEye_Count; eyeIndex++)
        {
            // retrieve projection and modelview matrix from oculus
            cTransform projectionMatrix, modelViewMatrix;
            oculusVR.onEyeRender(eyeIndex, projectionMatrix, modelViewMatrix);

            camera->m_useCustomProjectionMatrix = true;
            camera->m_projectionMatrix = projectionMatrix;

            camera->m_useCustomModelViewMatrix = true;
            camera->m_modelViewMatrix = modelViewMatrix;


            // render world
            ovrSizei size = oculusVR.getEyeTextureSize(eyeIndex);

            updateGraphics(size);

            camera->renderView(size.w, size.h, C_STEREO_RIGHT_EYE, false);

            // finalize rendering  
            oculusVR.onEyeRenderFinish(eyeIndex);

            glFinish();
        }

        // update frames
        oculusVR.submitFrame();
        oculusVR.blitMirror();

        glfwSwapBuffers(window);
        glfwPollEvents();
    }

    // cleanup
    oculusVR.destroyVR();
    renderContext.destroy();

    glfwDestroyWindow(window);

    // exit glfw
    glfwTerminate();

    return (0);
}

//------------------------------------------------------------------------------

void ChangePosMainCube()
{

    if (isGrabD) // Si le bouton est active
    {
        if (!isGrabCubeD && tool_MainD->isInContact(CubeDirectionDroit))
        {
            // Premier contact avec le cube
            isGrabCubeD = true;
            OffsetXD = tool_MainD->getDeviceLocalPos().x() - CubeDirectionDroit->getLocalPos().x();
            OffsetYD = tool_MainD->getDeviceLocalPos().y() - CubeDirectionDroit->getLocalPos().y();
        }

        if (isGrabCubeD)
        {
            // Calculer la nouvelle position cible
            double targetX = tool_MainD->getDeviceLocalPos().x() - OffsetXD;
            double targetY = tool_MainD->getDeviceLocalPos().y() - OffsetYD;

            // Limiter les mouvements à l interieur des limites de la base et on limite a un axe a la fois
            targetX = cClamp(targetX, -MaxXDroite / 2, MaxXDroite / 2);
            targetY = cClamp(targetY, -MaxYDroite / 2, MaxYDroite / 2);

            currentX = CubeDirectionDroit->getLocalPos().x();
            currentY = CubeDirectionDroit->getLocalPos().y();

            if (abs(currentY) < 0.02)
            {
                // Appliquer un lissage pour eviter les mouvements saccades
                newX = currentX + (targetX - currentX) * smoothFactor;
            }
            else
            {
                newX = 0.0;
            }

            if (abs(currentX) < 0.02)
            {
                // Appliquer un lissage pour eviter les mouvements saccades
                newY = currentY + (targetY - currentY) * smoothFactor;
            }
            else
            {
                newY = 0.0;
            }

            // Appliquer la nouvelle position
            CubeDirectionDroit->setLocalPos(newX, newY, CubeDirectionDroit->getLocalPos().z());
            
            ovr_SetControllerVibration(
                oculusVR.getSession(),
                ovrControllerType_RTouch,
                1.0f,     // frequence
                1.0f      // amplitude
            );
            
        }
    }
    else
    {
        // Relachement du bouton
        if (isGrabCubeD)
        {
            // Retour progressif à la position centrale
            double currentX = CubeDirectionDroit->getLocalPos().x();
            double currentY = CubeDirectionDroit->getLocalPos().y();
            double returnSpeed = 0.2; // Vitesse de retour au centre

            double newX = currentX * (1.0 - returnSpeed);
            double newY = currentY * (1.0 - returnSpeed);

            CubeDirectionDroit->setLocalPos(newX, newY, CubeDirectionDroit->getLocalPos().z());

            // Si on est proche du centre, on reset completement
            if (abs(newX) < 0.01 && abs(newY) < 0.01)
            {
                CubeDirectionDroit->setLocalPos(0.0, 0.0, CubeDirectionDroit->getLocalPos().z());
                isGrabCubeD = false;
            }
        }
    }

    if (isGrabG) // Si le bouton est active
    {
        if (!isGrabCubeG && tool_MainG->isInContact(CubeDirectionGauche))
        {
            // Premier contact avec le cube
            isGrabCubeG = true;
            OffsetXG = tool_MainG->getDeviceLocalPos().x() - CubeDirectionGauche->getLocalPos().x();
            OffsetYG = tool_MainG->getDeviceLocalPos().y() - CubeDirectionGauche->getLocalPos().y();
        }

        if (isGrabCubeG)
        {
            // Calculer la nouvelle position cible
            double targetX = tool_MainG->getDeviceLocalPos().x() - OffsetXG;
            double targetY = tool_MainG->getDeviceLocalPos().y() - OffsetYG;

            // Limiter les mouvements à l interieur des limites de la base
            targetX = cClamp(targetX, -MaxXGauche / 2, MaxXGauche / 2);
            targetY = cClamp(targetY, -MaxYGauche / 2, MaxYGauche / 2);

            // Appliquer un lissage pour eviter les mouvements saccades
            double currentX = CubeDirectionGauche->getLocalPos().x();
            double currentY = CubeDirectionGauche->getLocalPos().y();
            double smoothFactor = 0.15; // Facteur de lissage

            double newX = currentX + (targetX - currentX) * smoothFactor;
            double newY = currentY + (targetY - currentY) * smoothFactor;

            // Appliquer la nouvelle position
            CubeDirectionGauche->setLocalPos(newX, newY, CubeDirectionGauche->getLocalPos().z());
            
            ovr_SetControllerVibration(
                oculusVR.getSession(),
                ovrControllerType_LTouch,
                1.0f,     // frequence
                1.0f      // amplitude
            );
            
        }
    }
    else
    {
        // Relachement du bouton
        if (isGrabCubeG)
        {
            // Retour progressif à la position centrale
            double currentX = CubeDirectionGauche->getLocalPos().x();
            double currentY = CubeDirectionGauche->getLocalPos().y();
            double returnSpeed = 0.2; // Vitesse de retour au centre

            double newX = currentX * (1.0 - returnSpeed);
            double newY = currentY * (1.0 - returnSpeed);

            CubeDirectionGauche->setLocalPos(newX, newY, CubeDirectionGauche->getLocalPos().z());

            // Si on est proche du centre, on reset completement
            if (abs(newX) < 0.01 && abs(newY) < 0.01)
            {
                CubeDirectionGauche->setLocalPos(0.0, 0.0, CubeDirectionGauche->getLocalPos().z());
                isGrabCubeG = false;
            }
        }
    }
}

//------------------------------------------------------------------------------

void HandleCollisionManettesD()
{
    int numContactsD;
    float PuissanceVibrationD;
    int CoeffPente = 1;
    
    while (simulationRunning)
    {
        //On recupere le nombre de point de contact
        numContactsD = hapticPointD->getNumCollisionEvents();

        if (!isGrabCubeD)
        {
            if (numContactsD > 0)
            {
                cVector3d collisionForceD = hapticPointD->getLastComputedForce();
                double fx = std::abs(collisionForceD.x());
                double fy = std::abs(collisionForceD.y());
                double fz = std::abs(collisionForceD.z());
                if (fx > 0.2 || fy > 0.2 || fz > 0.2)
                {
                    //Grace a une fonction exponentielle on calcul l amplitude de la vibration
                    PuissanceVibrationD = (float)exp(CoeffPente * (max({ fx,fy,fz }) - 3.0)); //Ici une force de valeur 3.0 est fixee comme le max
                    if (PuissanceVibrationD > 1.0f)
                    {
                        PuissanceVibrationD = 1.0f;
                    }
                    // Vibrer la manette gauche
                    ovr_SetControllerVibration(
                        oculusVR.getSession(),
                        ovrControllerType_RTouch,
                        1.0f,     // frequence
                        PuissanceVibrationD // amplitude
                    );
                }
                else
                {
                    ovr_SetControllerVibration(
                        oculusVR.getSession(),
                        ovrControllerType_RTouch,
                        0.0f,     // frequence
                        0.0f      // amplitude
                    );
                }
            }
            else
            {
                ovr_SetControllerVibration(
                    oculusVR.getSession(),
                    ovrControllerType_RTouch,
                    0.0f,     // frequence
                    0.0f      // amplitude
                );
            }
        }
        cSleepMs(10);
    }
}

//------------------------------------------------------------------------------

void HandleCollisionManettesG()
{
    int numContactsG;
    float PuissanceVibrationG;
    int CoeffPente = 1;

    while (simulationRunning)
    {
        //On recupere le nombre de point de contact
        numContactsG = hapticPointG->getNumCollisionEvents();

        if (!isGrabCubeG)
        {
            if (numContactsG > 0)
            {
                cVector3d collisionForceG = hapticPointG->getLastComputedForce();
                double fx = std::abs(collisionForceG.x());
                double fy = std::abs(collisionForceG.y());
                double fz = std::abs(collisionForceG.z());
                if (fx > 0.2 || fy > 0.2 || fz > 0.2)
                {
                    //Grace a une fonction exponentielle on calcul l amplitude de la vibration
                    PuissanceVibrationG = (float)exp(CoeffPente * (max({ fx,fy,fz }) - 3)); //Ici une force de valeur 3.0 est fixee comme le max
                    if (PuissanceVibrationG > 1.0f)
                    {
                        PuissanceVibrationG = 1.0f;
                    }
                    // Vibrer la manette gauche
                    ovr_SetControllerVibration(
                        oculusVR.getSession(),
                        ovrControllerType_LTouch,
                        1.0f,     // frequence
                        PuissanceVibrationG // amplitude
                    );
                }
                else
                {
                    ovr_SetControllerVibration(
                        oculusVR.getSession(),
                        ovrControllerType_LTouch,
                        0.0f,     // frequence
                        0.0f      // amplitude
                    );
                }
            }
            else
            {
                ovr_SetControllerVibration(
                    oculusVR.getSession(),
                    ovrControllerType_LTouch,
                    0.0f,     // frequence
                    0.0f      // amplitude
                );
            }
        }
        cSleepMs(10);
    }
}

//------------------------------------------------------------------------------

void GetPictureTelemetrie()
{
    const int MAX_SIZE = 2560 * 1440 * 3;
    int length = 0;
    monByteArrayImageTelemetrie.reserve(MAX_SIZE);
    Image_Telemetrie = cImage::create();
    Image_Telemetrie->allocate(2560, 1440, GL_RGB, GL_UNSIGNED_INT);
    while (simulationRunning)
    {
        if (!ImageTelemetrieDisponible && !monClientFMPPictureTelemetrie->getVirtualPictureMutexBlocAccess())
        {
            monClientFMPPictureTelemetrie->setVirtualPictureMutexBlocAccess(true);
            length = monClientFMPPictureTelemetrie->getVirtualPictureDataSize();
            if (length <= MAX_SIZE) // Si la taille de l image recue est plus grande que la taille max du buffer on attend une autre photo
            {
                monByteArrayImageTelemetrie.resize(length);
                for (int i = 0; i < length; i++)
                {
                    monByteArrayImageTelemetrie[i] = monClientFMPPictureTelemetrie->getMapFileOneByOneUnsignedChar(i);
                }
                if (cLoadPNG(Image_Telemetrie->getImage(), monByteArrayImageTelemetrie.data(), length))
                {
                    // a background picture is ready to update
                    ImageTelemetrieDisponible = true;
                }
            }
            monClientFMPPictureTelemetrie->setVirtualPictureMutexBlocAccess(false);
            cSleepMs(2);
        }
        cSleepMs(20);
    }
    
    monClientFMPPictureTelemetrie->CloseClient();
    Image_Telemetrie->clear();
    Image_Telemetrie.reset();
}

//------------------------------------------------------------------------------

void GetForceRobotHaptique()
{
    if (monClientFMPForceRobot) //On verifie si l objet est initialise
    {
        while (monClientFMPForceRobot->getVirtualRobotHaptiqueDroneMutexBlocAccess())
        {
            cSleepMs(1);
        }
        monClientFMPForceRobot->setVirtualRobotHaptiqueDroneMutexBlocAccess(true);

        AxeXForce = monClientFMPForceRobot->getVirtualRobotHaptiqueDroneAxeX();
        AxeYForce = monClientFMPForceRobot->getVirtualRobotHaptiqueDroneAxeY();
        AxeZForce = monClientFMPForceRobot->getVirtualRobotHaptiqueDroneAxeZ();

        monClientFMPForceRobot->setVirtualRobotHaptiqueDroneMutexBlocAccess(false);

        ResetConesDirection();

        if (AxeXForce == 1)
        {
            CylinderXMoins->setShowEnabled(true);
        }
        else if (AxeXForce == -1)
        {
            CylinderXPlus->setShowEnabled(true);
        }
        else if (AxeYForce == 1)
        {
            CylinderYMoins->setShowEnabled(true);
        }
        else if (AxeYForce == -1)
        {
            CylinderYPlus->setShowEnabled(true);
        }
        else if (AxeZForce == 1)
        {
            CylinderZMoins->setShowEnabled(true);
        }
        else if (AxeZForce == -1)
        {
            CylinderZPlus->setShowEnabled(true);
        }
    }

    cSleepMs(2);
}

//------------------------------------------------------------------------------

void GetPosRobotHaptique()
{
    if (tool && monClientFMPForceRobot) //On verifie si les objets sont initialises
    {
        while (monClientFMPPosRobot->getVirtualRobotHaptiqueDroneMutexBlocAccess())
        {
            cSleepMs(1);
        }
        monClientFMPPosRobot->setVirtualRobotHaptiqueDroneMutexBlocAccess(true);

        tool->setLocalPos(monClientFMPPosRobot->getVirtualRobotHaptiqueDroneAxeX() * 8, monClientFMPPosRobot->getVirtualRobotHaptiqueDroneAxeY() * 8, monClientFMPPosRobot->getVirtualRobotHaptiqueDroneAxeZ() * 8);

        monClientFMPPosRobot->setVirtualRobotHaptiqueDroneMutexBlocAccess(false);
    }
    cSleepMs(2);
}

//------------------------------------------------------------------------------

void ComposeImage()
{
    const int MAX_SIZE = 960 * 720 * 3;
    int length = 0;
    monByteArrayImage.reserve(MAX_SIZE);
    ImageCompose = cImage::create();
    ImageCompose->allocate(960 * 2, 720, GL_RGB, GL_UNSIGNED_INT);
    while (simulationRunning)
    {
        if (!ImageDisponible && !monClientFMPPictureDrone1->getVirtualPictureMutexBlocAccess())
        {
            monClientFMPPictureDrone1->setVirtualPictureMutexBlocAccess(true);
            length = monClientFMPPictureDrone1->getVirtualPictureDataSize();
            if (length <= MAX_SIZE) // Si la taille de l image recue est plus grande que la taille max du buffer on attend une autre photo
            {
                monByteArrayImage.resize(length);
                for (int i = 0; i < length; i++)
                {
                    monByteArrayImage[i] = monClientFMPPictureDrone1->getMapFileOneByOneUnsignedChar(i);
                }

                if (cLoadJPG(ImageCompose->getImage(), monByteArrayImage.data(), length))
                {
                    // a background picture is ready to update
                    ImageDisponible = true;
                }
            }
            monClientFMPPictureDrone1->setVirtualPictureMutexBlocAccess(false);
        }
        cSleepMs(20);
    }
    
    monClientFMPPictureDrone1->CloseClient();
    ImageCompose->clear();
    ImageCompose.reset();
}

//------------------------------------------------------------------------------

void ResetConesDirection()
{
    CylinderXMoins->setShowEnabled(false);
    CylinderXPlus->setShowEnabled(false);
    CylinderYMoins->setShowEnabled(false);
    CylinderYPlus->setShowEnabled(false);
    CylinderZMoins->setShowEnabled(false);
    CylinderZPlus->setShowEnabled(false);
}


//------------------------------------------------------------------------------

void errorCallback(int a_error, const char* a_description)
{
    cout << "Error: " << a_description << endl;
}

//------------------------------------------------------------------------------

void keyCallback(GLFWwindow* a_window, int a_key, int a_scancode, int a_action, int a_mods)
{
    // filter calls that only include a key press
    if ((a_action != GLFW_PRESS) && (a_action != GLFW_REPEAT))
    {
        return;
    }

    // option - exit
    else if ((a_key == GLFW_KEY_ESCAPE) || (a_key == GLFW_KEY_Q))
    {
        glfwSetWindowShouldClose(a_window, GLFW_TRUE);
    }

    // option - toggle fullscreen
    else if (a_key == GLFW_KEY_F)
    {
        // toggle state variable
        fullscreen = !fullscreen;

        // get handle to monitor
        GLFWmonitor* monitor = glfwGetPrimaryMonitor();

        // get information about monitor
        const GLFWvidmode* mode = glfwGetVideoMode(monitor);

        // set fullscreen or window mode
        if (fullscreen)
        {
            glfwSetWindowMonitor(window, monitor, 0, 0, mode->width, mode->height, mode->refreshRate);
            glfwSwapInterval(swapInterval);
        }
        else
        {
            int w = 0.8 * mode->height;
            int h = 0.5 * mode->height;
            int x = 0.5 * (mode->width - w);
            int y = 0.5 * (mode->height - h);
            glfwSetWindowMonitor(window, NULL, x, y, w, h, mode->refreshRate);
            glfwSwapInterval(swapInterval);
        }
    }

    // option - toggle vertical mirroring
    else if (a_key == GLFW_KEY_M)
    {
        mirroredDisplay = !mirroredDisplay;
        camera->setMirrorVertical(mirroredDisplay);
    }
}

//------------------------------------------------------------------------------

void close(void)
{
    // stop the simulation
    simulationRunning = false;

    // wait for graphics and haptics loops to terminate
    while (!simulationFinished) { cSleepMs(100); }

    // close haptic device
    tool->stop();

    cout << "##Fermeture du Programme##" << endl;
    // delete resources
    Image_Camera->clear();
    delete hapticsThread;
    delete FMPThreadPicture;
    delete FMPThreadPictureTelemetrie;
    delete ThreadGestionCollisionManetteG;
    delete ThreadGestionCollisionManetteD;
    delete bitmap;
    delete BitmapBackgroundVideoStream;
    delete world;
    delete monClientFMPPictureTelemetrie;
    delete monClientFMPForceRobot;
    delete monClientFMPPictureDrone1;
    delete monClientFMPPosRobot;
    delete monServeurFMPPictureDrone;
    //delete handler;
}

//------------------------------------------------------------------------------

void updateGraphics(ovrSizei _size)
{

    GetForceRobotHaptique();

    /////////////////////////////////////////////////////////////////////
    // UPDATE BACKGROUND
    /////////////////////////////////////////////////////////////////////
    if (ImageDisponible)
    {
        //On applique un mirroir vertical sur l image
        unsigned int width_img = ImageCompose->getWidth();
        unsigned int height_img = ImageCompose->getHeight();
        for (unsigned int y = 0; y < height_img; y++)
        {
            for (unsigned int x = 0; x < width_img / 2; x++)
            {
                // Obtenir la couleur du pixel en (x, y)
                cColorb colorLeft;
                ImageCompose->getPixelColor(x, y, colorLeft);

                // Obtenir la couleur du pixel en (width_img - x - 1, y)
                cColorb colorRight;
                ImageCompose->getPixelColor(width_img - x - 1, y, colorRight);

                // Échanger les couleurs
                ImageCompose->setPixelColor(x, y, colorRight);
                ImageCompose->setPixelColor(width_img - x - 1, y, colorLeft);
            }
        }
        texture->setImage(ImageCompose);
    }

    if (ImageTelemetrieDisponible)
    {
        texture_telemetrie->setImage(Image_Telemetrie);
    }

    ChangePosMainCube();

    camera->computeGlobalPositions(true);

    ImageDisponible = false;
    ImageTelemetrieDisponible = false;


}

//------------------------------------------------------------------------------

void updateHaptics(void)
{
    // simulation in now running
    simulationRunning = true;
    simulationFinished = false;

    // main haptic simulation loop
    while (simulationRunning)
    {

        /////////////////////////////////////////////////////////////////////
        // HAPTIC FORCE COMPUTATION
        /////////////////////////////////////////////////////////////////////

        // compute global reference frames for each object
        world->computeGlobalPositions(true);

        GetPosRobotHaptique();

        trackState = ovr_GetTrackingState(oculusVR.getSession(), displayMidpointSeconds, ovrTrue);
        posLeft = trackState.HandPoses[ovrHand_Left].ThePose.Position;
        posRight = trackState.HandPoses[ovrHand_Right].ThePose.Position;

        tool_MainG->setDeviceLocalPos(posLeft.z, posLeft.x, posLeft.y);
        tool_MainD->setDeviceLocalPos(posRight.z, posRight.x, posRight.y);

        if (OVR_SUCCESS(ovr_GetInputState(oculusVR.getSession(), ovrControllerType_Touch, &inputState)))
        {
            float handValueD = inputState.HandTrigger[1];   // gachette grip Droite
            if (handValueD > 0.7f)
            {
                tool_MainD->m_hapticPoint->m_sphereProxy->m_material->setOrange();
                isGrabD = true;
            }
            else
            {
                tool_MainD->m_hapticPoint->m_sphereProxy->m_material->setGreen();
                isGrabD = false;
            }
            float handValueG = inputState.HandTrigger[0];   // gachette grip Gauche
            if (handValueG > 0.7f)
            {
                tool_MainG->m_hapticPoint->m_sphereProxy->m_material->setOrange();
                isGrabG = true;
            }
            else
            {
                tool_MainG->m_hapticPoint->m_sphereProxy->m_material->setBlueRoyal();
                isGrabG = false;
            }
        }

        tool_MainG->computeInteractionForces();
        tool_MainD->computeInteractionForces();

        freqCounterHaptics.signal(1);
    }

    // exit haptics thread
    simulationFinished = true;
}

//------------------------------------------------------------------------------

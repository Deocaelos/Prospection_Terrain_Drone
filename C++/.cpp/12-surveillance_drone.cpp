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
#include "chai3d.h"

//------------------------------------------------------------------------------
#include <GLFW/glfw3.h>
#include <virtualpicturemapping/cFileMappingPictureClient.h>
#include <FMP_Telemetrie/cFileMappingTelloEduTelemetryClient.h>
#include <FMP_PosRobotHaptique/cFileMappingRobotHaptiqueDroneServeur.h>
#include <FMP_PosRobotHaptiqueCHAIVR/cFileMappingRobotHaptiqueDroneServeurCHAIVR.h>
#include <virtualpicturemapping/cFileMappingPictureServeur.h>
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
cStereoMode stereoMode = C_STEREO_DISABLED;

// fullscreen mode
bool fullscreen = false;

// mirrored display
bool mirroredDisplay = false;


//------------------------------------------------------------------------------
// DECLARED VARIABLES
//------------------------------------------------------------------------------

// a world that contains all objects of the virtual environment
cWorld* world;

// a camera to render the world in the window display
cCamera* camera;

// a light source to illuminate the objects in the world
cSpotLight *light;

// a haptic device handler
cHapticDeviceHandler* handler;

// a pointer to the current haptic device
cGenericHapticDevicePtr hapticDevice;

cHapticPoint* hapticPoint;

// a virtual tool representing the haptic device in the scene
cToolCursor* tool;

// a colored background
cBackground* background;

// a label to display the rate [Hz] at which the simulation is running
cLabel* labelRates;

// labels to display each haptic device model
cLabel* labelHapticDeviceModel;

// labels to display the position [m] of each haptic device
cLabel* labelHapticDevicePosition;

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
cThread* FMPThreadPictureServeur;

//File Mapping Thread
cThread* FMPThreadPictureServeurTelemetrie;

//File Mapping Thread
cThread* FMPThreadServeurPosRobotVR;


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

//booleen representant le mode VR
bool VR_Mode = false;

// Spheres et Dimension pour Contrainte du Robot
cMesh* SphereMax;
cShapeSphere* SphereMed;
cShapeSphere* SphereMin;
double HauteurCylindre = 0.3;
//Obtention des dimensions de la fenetre et conversion avec l unite utilisee
double distanceFromCamera;  // Distance camera-sphere
double fovY;  // Champ de vision vertical (en radians)
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
std::vector<unsigned char> monByteArrayImage1;
cImagePtr ImageCompose;
cBitmap* BitmapBackgroundVideoStream = new cBitmap();
bool ImageDisponible = false;

double RapportTaille = 8.0 / 3.0;

//Telemetrie Drones
cFileMappingTelloEduTelemetryClient* monClientFMPTelemetrieDrone1 = NULL;
cFileMappingTelloEduTelemetryClient* monClientFMPTelemetrieDrone2 = NULL;
//-------------------------------------------//
cScope* scope_Drone_1_Acceleration = NULL; //Creation d un graphique de type line pour les accelerations des drones
cScope* scope_Drone_2_Acceleration = NULL; //Creation d un graphique de type line pour les accelerations des drones
//-------------------------------------------//
cScope* scope_Drone_1_Vitesse = NULL; //Creation d un graphique de type line pour les Vitesses des drones
cScope* scope_Drone_2_Vitesse = NULL; //Creation d un graphique de type line pour les Vitesses des drones
//-------------------------------------------//
cLevel* level_Batterie_Drone_1 = NULL; //Creation d un graphique de type cLevel pour le niveau de batterie du drone 1
cLevel* level_Batterie_Drone_2 = NULL; //Creation d un graphique de type cLevel pour le niveau de batterie du drone 2
//-------------------------------------------//
cLevel* level_H_Drone_1 = NULL; //Creation d un graphique de type cLevel pour le hauteur du drone 1
cLevel* level_H_Drone_2 = NULL; //Creation d un graphique de type cLevel pour la hauteur du drone 2
//-------------------------------------------//
cDial* dialPitch_Drone_1 = NULL; //Creation d un graphique de type cDial pour le pitch du drone 1
cDial* dialPitch_Drone_2 = NULL; //Creation d un graphique de type cDial pour le pitch du drone 2
//-------------------------------------------//
cDial* dialRoll_Drone_1 = NULL; //Creation d un graphique de type cDial pour le roll du drone 1
cDial* dialRoll_Drone_2 = NULL; //Creation d un graphique de type cDial pour le roll du drone 2
//-------------------------------------------//
cDial* dialYaw_Drone_1 = NULL; //Creation d un graphique de type cDial pour le yaw du drone 1
cDial* dialYaw_Drone_2 = NULL; //Creation d un graphique de type cDial pour le yaw du drone 2
//-------------------------------------------//
cFontPtr font_LabelTelemetry;
cLabel* labelPitch_Drone_1 = NULL;
cLabel* labelPitch_Drone_2 = NULL;
cLabel* labelYaw_Drone_1 = NULL;
cLabel* labelYaw_Drone_2 = NULL;
cLabel* labelRoll_Drone_1 = NULL;
cLabel* labelRoll_Drone_2 = NULL;
cLabel* labelBatterie_Drone_1 = NULL;
cLabel* labelBatterie_Drone_2 = NULL;
cLabel* labelValueBatterie_Drone_1 = NULL;
cLabel* labelValueBatterie_Drone_2 = NULL;
cLabel* labelHauteur_Drone_1 = NULL;
cLabel* labelHauteur_Drone_2 = NULL;
cLabel* labelValueHauteur_Drone_1 = NULL;
cLabel* labelValueHauteur_Drone_2 = NULL;
cLabel* labelVitesse = NULL;
cLabel* labelAcce = NULL;
cLabel* labelDrone1 = NULL;
cLabel* labelDrone2 = NULL;

// FileMapping PosRobotHaptique Server
cFileMappingRobotHaptiqueDroneServeur* monServeurPosRobotHaptique = NULL;
int ForceXRobotHaptique = 0;
int ForceYRobotHaptique = 0;
int ForceZRobotHaptique = 0;
bool PresenceUser = false;
cThread* FMP_ThreadPosRobotHaptique;
cFileMappingRobotHaptiqueDroneServeurCHAIVR* monServeurPosRobotHaptiqueCHAIVR = NULL;

// Image de la camera
cImagePtr Image_Camera = cImage::create();
cImagePtr Image_Camera_HUD = cImage::create();
cFileMappingPictureServeur* monServeurFMPPictureDrone = NULL;
cFileMappingPictureServeur* monServeurFMPPictureTelemetrie = NULL;
cVirtualPicture* maVirtualPictureCamera = new cVirtualPicture();
cVirtualPicture* maVirtualPictureTelemetrie = new cVirtualPicture();
bool ImageCameraReady = false;
bool ImageCameraHUDReady = false;
bool isSphereShow = false;


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

// callback when the window display is resized
void windowSizeCallback(GLFWwindow* a_window, int a_width, int a_height);

// callback when an error GLFW occurs
void errorCallback(int error, const char* a_description);

// callback when a key is pressed
void keyCallback(GLFWwindow* a_window, int a_key, int a_scancode, int a_action, int a_mods);

// this function renders the scene
void updateGraphics(void);

// this function contains the main haptics simulation loop
void updateHaptics(void);

// methode permettant la composition des deux images des drones
void ComposeImage();

// methode permettant la recupêration des donnees telemetriques des drones 
void getTelemetry();

// methode permettant l ecriture sur le fichier de file mapping des posiitons du robot haptique
void SendPosRobotHaptique();

// methode permettant l ecriture sur le fichier de file mapping de l image vue par la camera de chai3d
void SendPictureCamera();

// methode permettant l ecriture sur le fichier de file mapping de l image vue par la camera de chai3d
void SendPictureTelemetrie();

// methode permettant l ecriture sur le fichier de file mapping de la postion du robot haptique pour la partie VR
void sendPosRobotHaptiqueCHAIVR();

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
    //--------------------------------------------------------------------------
    // INITIALIZATION
    //--------------------------------------------------------------------------

    cout << endl;
    cout << "-----------------------------------" << endl;
    cout << "CHAI3D" << endl;
    cout << "Surveillance Drones" << endl;
    cout << "Copyright 2003-2016" << endl;
    cout << "-----------------------------------" << endl;
    
    //--------------------------------------------------------------------------
    // OPEN GL - WINDOW DISPLAY
    //--------------------------------------------------------------------------

    // initialize GLFW library
    if (!glfwInit())
    {
        cout << "failed initialization" << endl;
        cSleepMs(1000);
        return 1;
    }

    string Mode_VR;

    cout << "Voulez-vous activer la VR ? (Y/N)" << endl;
    do
    {
        cin >> Mode_VR;
        if (Mode_VR == "Y" || Mode_VR == "y")
        {
            cout << "Le mode VR est actif" << endl;
            VR_Mode = true;
        }
        else if(Mode_VR == "N" || Mode_VR == "n")
        {
            cout << "Le mode VR n est pas actif" << endl;
            VR_Mode = false;
        }
        else
        {
            cout << "Saisie incorrecte veuillez recommencer" << endl;
        }
    } while (Mode_VR != "Y" && Mode_VR != "y" && Mode_VR != "N" && Mode_VR != "n");

    // set error callback
    glfwSetErrorCallback(errorCallback);

    // compute desired size of window
    const GLFWvidmode* mode = glfwGetVideoMode(glfwGetPrimaryMonitor());
    int w = 0.8 * mode->height;
    int h = 0.5 * mode->height;
    int x = 0.5 * (mode->width - w);
    int y = 0.5 * (mode->height - h);

    // set OpenGL version
    glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 2);
    glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 1);

    // set active stereo mode
    if (stereoMode == C_STEREO_ACTIVE)
    {
        glfwWindowHint(GLFW_STEREO, GL_TRUE);
    }
    else
    {
        glfwWindowHint(GLFW_STEREO, GL_FALSE);
    }

    // create display context
    if (VR_Mode)
    {
        glfwWindowHint(GLFW_RESIZABLE, GL_FALSE); //On eneleve la possibilite de redimensionner la fenetre
        window = glfwCreateWindow(w, h, "Surveillance Drones avec VR", NULL, NULL);
        
    }
    else
    {
        glfwWindowHint(GLFW_RESIZABLE, GL_TRUE);
        window = glfwCreateWindow(w, h, "Surveillance Drones", NULL, NULL);
    }
	if (!window)
    {
        cout << "failed to create window" << endl;
        cSleepMs(1000);
        glfwTerminate();
        return 1;
    }

    // get width and height of window
    glfwGetWindowSize(window, &width, &height);

    // set position of window
    glfwSetWindowPos(window, x, y);

    // set key callback
    glfwSetKeyCallback(window, keyCallback);

    // set resize callback
    glfwSetWindowSizeCallback(window, windowSizeCallback);

    // set current display context
    glfwMakeContextCurrent(window);

    // sets the swap interval for the current display context
    glfwSwapInterval(swapInterval);

#ifdef GLEW_VERSION
    // initialize GLEW library
    if (glewInit() != GLEW_OK)
    {
        cout << "failed to initialize GLEW library" << endl;
        glfwTerminate();
        return 1;
    }
#endif


    //--------------------------------------------------------------------------
    // WORLD - CAMERA - LIGHTING
    //--------------------------------------------------------------------------

    // create a new world.
    world = new cWorld();

    // set the background color of the environment
    world->m_backgroundColor.setBlack();

    // create a camera and insert it into the virtual world
    camera = new cCamera(world);
    world->addChild(camera);

    // position and orient the camera
    camera->set(cVector3d(3.0, 0.0, 0.0),    // camera position (eye)
                cVector3d(0.0, 0.0, 0.0),    // lookat position (target)
                cVector3d(0.0, 0.0, 1.0));   // direction of the (up) vector

    // set the near and far clipping planes of the camera
    // anything in front or behind these clipping planes will not be rendered
    camera->setClippingPlanes(0.01, 10.0);

    // set stereo mode
    camera->setStereoMode(stereoMode);

    // set stereo eye separation and focal length (applies only if stereo is enabled)
    camera->setStereoEyeSeparation(1.3);
    camera->setStereoFocalLength(3.0);

    // set vertical mirrored display mode
    camera->setMirrorVertical(mirroredDisplay);

    // create a light source
    light = new cSpotLight(world);

    // attach light to camera
    camera->addChild(light);

    // enable light source
    light->setEnabled(true);

    // position the light source
    light->setLocalPos(0.0, 0.0, 0.8);

    // define the direction of the light beam
    light->setDir(-3.0,-0.5, 0.0);

    // enable this light source to generate shadows
    light->setShadowMapEnabled(true);

    // set the resolution of the shadow map
    light->m_shadowMap->setQualityLow();

    // set light cone half angle
    light->setCutOffAngleDeg(40);


    //--------------------------------------------------------------------------
    // HAPTIC DEVICES / TOOLS
    //--------------------------------------------------------------------------

    // create a haptic device handler
    handler = new cHapticDeviceHandler();

    // get access to the first available haptic device found
    handler->getDevice(hapticDevice, 0);

    // retrieve information about the current haptic device
    cHapticDeviceInfo hapticDeviceInfo = hapticDevice->getSpecifications();

    // if the device has a gripper, enable the gripper to simulate a user switch
    hapticDevice->setEnableGripperUserSwitch(true);

    // calibrate if necessary
    hapticDevice->calibrate();
    
    // create a tool (cursor) and insert into the world
    tool = new cToolCursor(world);
    world->addChild(tool);

    // connect the haptic device to the virtual tool
    tool->setHapticDevice(hapticDevice);

    // define the radius of the tool (sphere)
    double toolRadius = 0.08;

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
    tool->setWorkspaceRadius(1.4);

    // haptic forces are enabled only if small forces are first sent to the device;
    // this mode avoids the force spike that occurs when the application starts when 
    // the tool is located inside an object for instance. 
    tool->setWaitForSmallForce(true);

    hapticPoint = tool->getHapticPoint(0);

    // start the haptic tool
    tool->start();


    //--------------------------------------------------------------------------
    // CREATE OBJECTS
    //--------------------------------------------------------------------------

    // read the scale factor between the physical workspace of the haptic
    // device and the virtual workspace defined for the tool
    double workspaceScaleFactor = tool->getWorkspaceScaleFactor();

    // stiffness properties
    double maxStiffness	= hapticDeviceInfo.m_maxLinearStiffness / workspaceScaleFactor;

    double maxDamping = hapticDeviceInfo.m_maxLinearDamping / workspaceScaleFactor;

    distanceFromCamera = camera->getLocalPos().length();  // Distance camera-sphere
    fovY = camera->getFieldViewAngleRad();  // Champ de vision vertical (en radians)
    RayonMax = distanceFromCamera * tan(fovY / 2.0) - 0.04 * distanceFromCamera - HauteurCylindre;
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

    // we indicate that we also using material properties. If you set this parameter to 'false'
    // you will notice that only vertex colors are used to render triangle, and lighting
    // will not longer have any effect.
    SphereMax->setUseMaterial(true);

    // compute a boundary box
    SphereMax->computeBoundaryBox(true);

    // compute collision detection algorithm
    SphereMax->createAABBCollisionDetector(toolRadius);

    // define a default stiffness for the object
    SphereMax->m_material->setStiffness(0.1*maxStiffness);

    SphereMax->m_material->setStaticFriction(0.8);

    // render triangles hapticaly on both sides
    SphereMax->m_material->setHapticTriangleSides(true, true);

    SphereMax->setHapticEnabled(false);

    SphereMax->setShowEnabled(false);
    
    //--------------------------------------------------------------//
    SphereMed = new cShapeSphere(RayonMax);
    world->addChild(SphereMed);

    SphereMed->m_material->setBlue();

    SphereMed->m_material->setTransparencyLevel(0.1);

    SphereMed->setUseTransparency(true);

    SphereMed->m_material->setViscosity(0.7*maxDamping);            // % of maximum linear damping

    SphereMed->createEffectViscosity();

    SphereMed->computeBoundaryBox();

    SphereMed->setShowEnabled(false);

    //--------------------------------------------------------------//
    SphereMin = new cShapeSphere(RayonMin);
    world->addChild(SphereMin);

    SphereMin->m_material->setBlueLight();

    SphereMin->m_material->setTransparencyLevel(0.1);

    SphereMin->setUseTransparency(true);

    SphereMin->m_material->setViscosity(0);            // % of maximum linear damping

    SphereMin->createEffectViscosity();

    SphereMin->setHapticEnabled(false);

    SphereMin->setShowEnabled(false);


    //Forme permettant d indiquer la direction

    CylinderZPlus = new cShapeCylinder(0.08, 0.001, HauteurCylindre);
    world->addChild(CylinderZPlus);

    CylinderZPlus->m_material->setGreen();

    CylinderZPlus->setLocalPos(0.0, 0.0, RayonMax);
    CylinderZPlus->setShowEnabled(false);

    CylinderZMoins = new cShapeCylinder(0.08, 0.001, HauteurCylindre);
    world->addChild(CylinderZMoins);

    CylinderZMoins->m_material->setGreen();

    CylinderZMoins->setLocalPos(0.0, 0.0, -RayonMax);
    CylinderZMoins->rotateAboutLocalAxisDeg(cVector3d(0.0, 1.0, 0.0), 180.0);

    CylinderZMoins->setShowEnabled(false);
    CylinderYPlus = new cShapeCylinder(0.08, 0.001, HauteurCylindre);
    world->addChild(CylinderYPlus);

    CylinderYPlus->m_material->setBlue();

    CylinderYPlus->setLocalPos(0.0,RayonMax, 0.0);
    CylinderYPlus->rotateAboutLocalAxisDeg(cVector3d(1.0, 0.0, 0.0), -90.0);
    CylinderYPlus->setShowEnabled(false);

    CylinderYMoins = new cShapeCylinder(0.08, 0.001, HauteurCylindre);
    world->addChild(CylinderYMoins);

    CylinderYMoins->m_material->setBlue();

    CylinderYMoins->setLocalPos(0.0, -RayonMax, 0.0);
    CylinderYMoins->rotateAboutLocalAxisDeg(cVector3d(1.0, 0.0, 0.0), 90.0);
    CylinderYMoins->setShowEnabled(false);

    CylinderXPlus = new cShapeCylinder(0.08, 0.001, HauteurCylindre);
    world->addChild(CylinderXPlus);

    CylinderXPlus->m_material->setRed();

    CylinderXPlus->setLocalPos(RayonMax, 0.0, 0.0);
    CylinderXPlus->rotateAboutLocalAxisDeg(cVector3d(0.0, 1.0, 0.0), 90.0);
    CylinderXPlus->setShowEnabled(false);

    CylinderXMoins = new cShapeCylinder(0.08, 0.001, HauteurCylindre);
    world->addChild(CylinderXMoins);

    CylinderXMoins->m_material->setRed();

    CylinderXMoins->setLocalPos(-RayonMax, 0.0, 0.0);
    CylinderXMoins->rotateAboutLocalAxisDeg(cVector3d(0.0, 1.0, 0.0), -90.0);
    CylinderXMoins->setShowEnabled(false);

    //--------------------------------------------------------------------------
    // TELEMETRIE DRONES ET FILE MAPPING TELEMETRIE 
    //--------------------------------------------------------------------------

    // create a font
    font_LabelTelemetry = NEW_CFONTCALIBRI72();

    monClientFMPTelemetrieDrone1 = new cFileMappingTelloEduTelemetryClient(false);
    //Ouverture du fichier de memoire partagee ou sont stockees les valeurs de telemetrie du drone 1
    monClientFMPTelemetrieDrone1->OpenClient("TelemetrieDrone1");
    //--------------------------------------------------------------//
    monClientFMPTelemetrieDrone2 = new cFileMappingTelloEduTelemetryClient(false);
    //Ouverture du fichier de memoire partagee ou sont stockees les valeurs de telemetrie du drone 2
    monClientFMPTelemetrieDrone2->OpenClient("TelemetrieDrone2");

    //Creation des differents elements qui stocke les valeurs de telemetrie des drones
    //--------------------------------------------------------------//
    scope_Drone_1_Acceleration = new cScope();

    camera->m_frontLayer->addChild(scope_Drone_1_Acceleration);

    scope_Drone_1_Acceleration->setRange(-1000, 1000);

    scope_Drone_1_Acceleration->setSignalEnabled(true, true, true);

    scope_Drone_1_Acceleration->setTransparencyLevel(0.3);
    //--------------------------------------------------------------//
    scope_Drone_2_Acceleration = new cScope();

    camera->m_frontLayer->addChild(scope_Drone_2_Acceleration);

    scope_Drone_2_Acceleration->setRange(-1000, 1000);

    scope_Drone_2_Acceleration->setSignalEnabled(true, true, true);

    scope_Drone_2_Acceleration->setTransparencyLevel(0.3);
    //--------------------------------------------------------------//
    scope_Drone_1_Vitesse = new cScope();

    camera->m_frontLayer->addChild(scope_Drone_1_Vitesse);

    scope_Drone_1_Vitesse->setRange(-25, 25);

    scope_Drone_1_Vitesse->setSignalEnabled(true, true, true);

    scope_Drone_1_Vitesse->setTransparencyLevel(0.3);
    //--------------------------------------------------------------//
    scope_Drone_2_Vitesse = new cScope();

    camera->m_frontLayer->addChild(scope_Drone_2_Vitesse);

    scope_Drone_2_Vitesse->setRange(-25, 25);

    scope_Drone_2_Vitesse->setSignalEnabled(true, true, true);

    scope_Drone_2_Vitesse->setTransparencyLevel(0.3);
    //--------------------------------------------------------------//
    level_Batterie_Drone_1 = new cLevel();

    camera->m_frontLayer->addChild(level_Batterie_Drone_1);

    level_Batterie_Drone_1->setRange(0, 100);

    level_Batterie_Drone_1->setNumIncrements(50);

    level_Batterie_Drone_1->m_colorActive = cColorf(0.0, 0.8, 0.0, 0.4);

    level_Batterie_Drone_1->setUseTexture(false);

    //--------------------------------------------------------------//
    level_Batterie_Drone_2 = new cLevel();

    camera->m_frontLayer->addChild(level_Batterie_Drone_2);

    level_Batterie_Drone_2->setRange(0, 100);

    level_Batterie_Drone_2->setNumIncrements(50);

    level_Batterie_Drone_2->m_colorActive = cColorf(0.0, 0.8, 0.0, 0.4);
    //--------------------------------------------------------------//
    level_H_Drone_1 = new cLevel();

    camera->m_frontLayer->addChild(level_H_Drone_1);

    level_H_Drone_1->setRange(0, 7);

    level_H_Drone_1->setNumIncrements(50);

    level_H_Drone_1->m_colorActive = cColorf(1.0, 0.647, 0.0, 1.0);
    //--------------------------------------------------------------//
    level_H_Drone_2 = new cLevel();

    camera->m_frontLayer->addChild(level_H_Drone_2);

    level_H_Drone_2->setRange(0, 7);

    level_H_Drone_2->setNumIncrements(50);

    level_H_Drone_2->m_colorActive = cColorf(1.0, 0.647, 0.0, 1.0);
    //--------------------------------------------------------------//
    dialPitch_Drone_1 = new cDial();

    camera->m_frontLayer->addChild(dialPitch_Drone_1);

    dialPitch_Drone_1->setRange(-360, 360);

    dialPitch_Drone_1->setSingleIncrementDisplay(true);

    dialPitch_Drone_1->m_colorActive = cColorf(0.0, 1.0, 0.0, 1.0);
    //--------------------------------------------------------------//
    dialPitch_Drone_2 = new cDial();

    camera->m_frontLayer->addChild(dialPitch_Drone_2);

    dialPitch_Drone_2->setRange(-360, 360);

    dialPitch_Drone_2->setSingleIncrementDisplay(true);

    dialPitch_Drone_2->m_colorActive = cColorf(0.0, 1.0, 0.0, 1.0);
    //--------------------------------------------------------------//
    dialRoll_Drone_1 = new cDial();

    camera->m_frontLayer->addChild(dialRoll_Drone_1);

    dialRoll_Drone_1->setRange(-360, 360);

    dialRoll_Drone_1->setSingleIncrementDisplay(true);

    dialRoll_Drone_1->m_colorActive = cColorf(1.0, 0.0, 0.0, 1.0);
    //--------------------------------------------------------------//
    dialRoll_Drone_2 = new cDial();

    camera->m_frontLayer->addChild(dialRoll_Drone_2);

    dialRoll_Drone_2->setRange(-360, 360);

    dialRoll_Drone_2->setSingleIncrementDisplay(true);

    dialRoll_Drone_2->m_colorActive = cColorf(1.0, 0.0, 0.0, 1.0);
    //--------------------------------------------------------------//
    dialYaw_Drone_1 = new cDial();

    camera->m_frontLayer->addChild(dialYaw_Drone_1);

    dialYaw_Drone_1->setRange(-360, 360);

    dialYaw_Drone_1->setSingleIncrementDisplay(true);

    dialYaw_Drone_1->m_colorActive = cColorf(0.0, 0.0, 1.0, 1.0);
    //--------------------------------------------------------------//
    dialYaw_Drone_2 = new cDial();

    camera->m_frontLayer->addChild(dialYaw_Drone_2);

    dialYaw_Drone_2->setRange(-360, 360);

    dialYaw_Drone_2->setSingleIncrementDisplay(true);

    dialYaw_Drone_2->m_colorActive = cColorf(0.0, 0.0, 1.0, 1.0);
    //--------------------------------------------------------------//
    labelPitch_Drone_1 = new cLabel(font_LabelTelemetry);

    camera->m_frontLayer->addChild(labelPitch_Drone_1);

    labelPitch_Drone_1->m_fontColor.setGreen();

    labelPitch_Drone_1->setText("P");
    //--------------------------------------------------------------//
    labelPitch_Drone_2 = new cLabel(font_LabelTelemetry);

    camera->m_frontLayer->addChild(labelPitch_Drone_2);

    labelPitch_Drone_2->m_fontColor.setGreen();

    labelPitch_Drone_2->setText("P");
    //--------------------------------------------------------------//
    labelYaw_Drone_1 = new cLabel(font_LabelTelemetry);

    camera->m_frontLayer->addChild(labelYaw_Drone_1);

    labelYaw_Drone_1->m_fontColor.setBlue();

    labelYaw_Drone_1->setText("Y");
    //--------------------------------------------------------------//
    labelYaw_Drone_2 = new cLabel(font_LabelTelemetry);

    camera->m_frontLayer->addChild(labelYaw_Drone_2);

    labelYaw_Drone_2->m_fontColor.setBlue();

    labelYaw_Drone_2->setText("Y");
    //--------------------------------------------------------------//
    labelRoll_Drone_1 = new cLabel(font_LabelTelemetry);

    camera->m_frontLayer->addChild(labelRoll_Drone_1);

    labelRoll_Drone_1->m_fontColor.setRed();

    labelRoll_Drone_1->setText("R");
    //--------------------------------------------------------------//
    labelRoll_Drone_2 = new cLabel(font_LabelTelemetry);

    camera->m_frontLayer->addChild(labelRoll_Drone_2);

    labelRoll_Drone_2->m_fontColor.setRed();

    labelRoll_Drone_2->setText("R");
    //--------------------------------------------------------------//
    labelBatterie_Drone_1 = new cLabel(font_LabelTelemetry);

    camera->m_frontLayer->addChild(labelBatterie_Drone_1);

    labelBatterie_Drone_1->m_fontColor.set(0.0, 0.8, 0.0, 1.0);

    labelBatterie_Drone_1->setText("B\nA\nT\nT\nE\nR\nI\nE");
    //--------------------------------------------------------------//
    labelBatterie_Drone_2 = new cLabel(font_LabelTelemetry);

    camera->m_frontLayer->addChild(labelBatterie_Drone_2);

    labelBatterie_Drone_2->m_fontColor.set(0.0, 0.8, 0.0, 1.0);

    labelBatterie_Drone_2->setText("B\nA\nT\nT\nE\nR\nI\nE");
    //--------------------------------------------------------------//
    labelHauteur_Drone_1 = new cLabel(font_LabelTelemetry);

    camera->m_frontLayer->addChild(labelHauteur_Drone_1);

    labelHauteur_Drone_1->m_fontColor.set(1.0, 0.647, 0.0, 1.0);

    labelHauteur_Drone_1->setText("H\nA\nU\nT\nE\nU\nR");
    //--------------------------------------------------------------//
    labelHauteur_Drone_2 = new cLabel(font_LabelTelemetry);

    camera->m_frontLayer->addChild(labelHauteur_Drone_2);

    labelHauteur_Drone_2->m_fontColor.set(1.0, 0.647, 0.0, 1.0);

    labelHauteur_Drone_2->setText("H\nA\nU\nT\nE\nU\nR");
    //--------------------------------------------------------------//
    labelValueBatterie_Drone_1 = new cLabel(font_LabelTelemetry);

    camera->m_frontLayer->addChild(labelValueBatterie_Drone_1);

    labelValueBatterie_Drone_1->m_fontColor.set(0.0, 1.0, 0.0, 1.0);
    //--------------------------------------------------------------//
    labelValueBatterie_Drone_2 = new cLabel(font_LabelTelemetry);

    camera->m_frontLayer->addChild(labelValueBatterie_Drone_2);

    labelValueBatterie_Drone_2->m_fontColor.set(0.0, 1.0, 0.0, 1.0);
    //--------------------------------------------------------------//
    labelValueHauteur_Drone_1 = new cLabel(font_LabelTelemetry);

    camera->m_frontLayer->addChild(labelValueHauteur_Drone_1);

    labelValueHauteur_Drone_1->m_fontColor.set(1.0, 0.647, 0.0, 1.0);
    //--------------------------------------------------------------//
    labelValueHauteur_Drone_2 = new cLabel(font_LabelTelemetry);

    camera->m_frontLayer->addChild(labelValueHauteur_Drone_2);

    labelValueHauteur_Drone_2->m_fontColor.set(1.0, 0.647, 0.0, 1.0);
    //--------------------------------------------------------------//
    labelAcce = new cLabel(font_LabelTelemetry);

    camera->m_frontLayer->addChild(labelAcce);

    labelAcce->m_fontColor.set(1.0, 1.0, 1.0, 1.0);

    labelAcce->setText("A\nC\nC\nE");
    //--------------------------------------------------------------//
    labelVitesse = new cLabel(font_LabelTelemetry);

    camera->m_frontLayer->addChild(labelVitesse);

    labelVitesse->m_fontColor.set(1.0, 1.0, 1.0, 1.0);

    labelVitesse->setText("S\nP\nE\nE\nD");
    //--------------------------------------------------------------//
    labelDrone1 = new cLabel(font_LabelTelemetry);

    camera->m_frontLayer->addChild(labelDrone1);

    labelDrone1->m_fontColor.set(1.0, 1.0, 1.0, 1.0);

    labelDrone1->setText("Drone 1:");
    //--------------------------------------------------------------//
    labelDrone2 = new cLabel(font_LabelTelemetry);

    camera->m_frontLayer->addChild(labelDrone2);

    labelDrone2->m_fontColor.set(1.0, 1.0, 1.0, 1.0);
        
    labelDrone2->setText("Drone 2:");
    //--------------------------------------------------------------//

    //--------------------------------------------------------------------------
    // FILE MAPPING POS ROBOT HAPTIQUE 
    //--------------------------------------------------------------------------

    monServeurPosRobotHaptique = new cFileMappingRobotHaptiqueDroneServeur(false);
    //Ouverture du fichier de memoire partagee ou sera stocke les commandes liees au robot haptique
    monServeurPosRobotHaptique->OpenServer("RobotHaptique_Position");

    if (VR_Mode)
    {
        monServeurPosRobotHaptiqueCHAIVR = new cFileMappingRobotHaptiqueDroneServeurCHAIVR(false);
        //Ouverture du fichier de memoire partagee ou sera stocke la postion X Y Z du robot haptique
        monServeurPosRobotHaptiqueCHAIVR->OpenServer("RobotHaptique_PositionCHAIVR");
    }

    //--------------------------------------------------------------------------
    // WIDGETS
    //--------------------------------------------------------------------------

    // create a label to display the haptic and graphic rate of the simulation
    labelRates = new cLabel(font_LabelTelemetry);
    camera->m_frontLayer->addChild(labelRates);

    // create a background
    background = new cBackground();
    camera->m_backLayer->addChild(background);

    // set background color properties
    // set background properties
    background->setCornerColors(cColorf(0.44f, 0.44f, 0.88f),
                                cColorf(0.44f, 0.44f, 0.88f),
                                cColorf(0.00f, 0.00f, 0.00f),
                                cColorf(0.00f, 0.00f, 0.00f));

    //------------------------------------------------------------------------------
    //  FILE MAPPING PICTURE
    //------------------------------------------------------------------------------

    monClientFMPPictureDrone1 = new cFileMappingPictureClient(false);
    //Ouverture du fichier de memoire partagee stockant le flux traite par JAVA
    monClientFMPPictureDrone1->OpenClient("Video_Drone");
    monServeurFMPPictureDrone = new cFileMappingPictureServeur(false);
    //Ouverture du fichier de memoire partagee ou sera stocke le contexte CHAI3D
    monServeurFMPPictureDrone->OpenServer("Video_Drone_CHAI3D");
    if (VR_Mode)
    {
        monServeurFMPPictureTelemetrie = new cFileMappingPictureServeur(false);
        //Ouverture du fichier de memoire partagee ou sera stocke la partie frontlayer du contexte CHAI3D
        monServeurFMPPictureTelemetrie->OpenServer("Telemetrie_CHAI3D");
    }

    //--------------------------------------------------------------------------
    // START SIMULATION
    //--------------------------------------------------------------------------

    cout << "###################################################" << endl;
    cout << "##Initialisation du Robot Haptique" << endl;
    cout << "##Quand celui-ci est initialise, Merci d appuyer sur le bouton central" << endl;
    bool button_Central = false;
    while (!button_Central && hapticDeviceInfo.m_modelName != "VirtualDevice-WG")
    {
        hapticDevice->getUserSwitch(0, button_Central);
        cSleepMs(100);
    }

    tool->getHapticPoint(0)->initialize();
    
    cout << "##Veuillez lacher le robot, Il va se positionner" << endl;

    cSleepMs(3000);

    BitmapBackgroundVideoStream->setShowEnabled(true);
    
    cVector3d force(0, 0, 0);
    cVector3d torque(0, 0, 0);

    // desired position
    cVector3d desiredPosition;
    desiredPosition.set(0.0, 0.0, 0.0);

    // desired orientation
    cMatrix3d desiredRotation;
    desiredRotation.identity();

    // read linear velocity 
    cVector3d linearVelocity;
    hapticDevice->getLinearVelocity(linearVelocity);

    // read angular velocity
    cVector3d angularVelocity;
    hapticDevice->getAngularVelocity(angularVelocity);

    // read position 
    cVector3d position;
    hapticDevice->getPosition(position);
    cVector3d difference = position - desiredPosition;
    double tolerance = 1e-3; // Tolérance pour la precision des positions
    // read orientation 
    cMatrix3d rotation;

    double Kp = 15.0;
    double Dp = 0.5;  // Amortissement
    double Kr = 0.05; // Gain de rotation

    while (difference.length() > tolerance)
    {
        // Lire la vitesse
        cVector3d linearVelocity;
        hapticDevice->getLinearVelocity(linearVelocity);

        // Calcul de la force de rappel proportionnelle
        cVector3d forceField = Kp * (desiredPosition - position);

        // Ajout d'un amortissement
        cVector3d dampingForce = -Dp * linearVelocity;

        // Calcul du couple angulaire
        cVector3d axis;
        double angle;
        cMatrix3d deltaRotation = cTranspose(rotation) * desiredRotation;
        deltaRotation.toAxisAngle(axis, angle);
        torque = rotation * ((Kr * angle) * axis);

        // Application des forces et torques
        force = forceField + dampingForce;
        hapticDevice->setForceAndTorque(force, torque);

        // Affichage pour debogage
        // cout << "Position: " << position << endl;

        // Mise à jour du device
        tool->updateFromDevice();
        cSleepMs(0.1);

        // Lire la position et l'orientation actuelles
        hapticDevice->getPosition(position);
        hapticDevice->getRotation(rotation);
        difference = position - desiredPosition;
        Kp = Kp * 1.002;
    }
    tool->updateFromDevice();
    SphereMax->setLocalPos(tool->getLocalPos());
    SphereMed->setLocalPos(tool->getLocalPos());
    SphereMin->setLocalPos(tool->getLocalPos());
    cout << "##Robot positionne avec succes" << endl;
    cout << "###################################################" << endl;
    cout << "Keyboard Options:" << endl << endl;
    cout << "[1] - Wireframe (ON/OFF)" << endl;
    cout << "[f] - Enable/Disable full screen mode" << endl;
    cout << "[m] - Enable/Disable vertical mirroring" << endl;
    cout << "[q] - Exit application" << endl;
    cout << endl << endl;

    //Image de fond
    camera->m_backLayer->addChild(BitmapBackgroundVideoStream);
    // create a thread which starts the main haptics rendering loop
    hapticsThread = new cThread();
    hapticsThread->start(updateHaptics, CTHREAD_PRIORITY_HAPTICS);

    // creation des threads de FMP

    //Thread permettant de recuperer le flux traite par JAVA
    FMPThreadPicture = new cThread();
    FMPThreadPicture->start(ComposeImage, CTHREAD_PRIORITY_GRAPHICS);

    //Thread permettant d envoyer a JAVA les commandes liees au robot haptique
    FMP_ThreadPosRobotHaptique = new cThread();
    FMP_ThreadPosRobotHaptique->start(SendPosRobotHaptique, CTHREAD_PRIORITY_HAPTICS);
    
    //Thread permettant d envoyer le contexte chai3d au serveur de diffusion JAVA
    FMPThreadPictureServeur = new cThread();
    FMPThreadPictureServeur->start(SendPictureCamera, CTHREAD_PRIORITY_GRAPHICS);

    if (VR_Mode)
    {
        //Thread permettant d envoyer le contexte chai3d representant uniquement la telemetrie c est a dire le frontlayer
        FMPThreadPictureServeurTelemetrie = new cThread();
        FMPThreadPictureServeurTelemetrie->start(SendPictureTelemetrie, CTHREAD_PRIORITY_GRAPHICS);

        //Thread permettant d envoyer la position X Y Z du robot haptique
        FMPThreadServeurPosRobotVR = new cThread();
        FMPThreadServeurPosRobotVR->start(sendPosRobotHaptiqueCHAIVR, CTHREAD_PRIORITY_HAPTICS);
    }

    // setup callback when application exits
    atexit(close);

	// create a label to display the haptic device model
	labelHapticDeviceModel = new cLabel(font_LabelTelemetry);
	camera->m_frontLayer->addChild(labelHapticDeviceModel);
	labelHapticDeviceModel->setText("Peripherique connecte : " + hapticDeviceInfo.m_modelName);

	// create a label to display the position of haptic device
	labelHapticDevicePosition = new cLabel(font_LabelTelemetry);
	camera->m_frontLayer->addChild(labelHapticDevicePosition);
    cSleepMs(500);
    SphereMax->setHapticEnabled(true);

    //Lancement de la VR si demande
    if (VR_Mode)
    {
        //Ici execute l autre CHAI3DVR
    }

    //--------------------------------------------------------------------------
    // MAIN GRAPHIC LOOP
    //--------------------------------------------------------------------------

    // call window size callback at initialization
    windowSizeCallback(window, width, height);

    Image_Camera->allocate(width, height, GL_RGB, GL_UNSIGNED_BYTE);
    Image_Camera_HUD->allocate(width, height, GL_RGBA, GL_UNSIGNED_BYTE);
    

    // main graphic loop
    while (!glfwWindowShouldClose(window))
    {
        // get width and height of window
        glfwGetWindowSize(window, &width, &height);

        // render graphics
        updateGraphics();

        // swap buffers
        glfwSwapBuffers(window);

        // process events
        glfwPollEvents();

        // signal frequency counter
        freqCounterGraphics.signal(1);
    }

    // close window
    glfwDestroyWindow(window);

    // terminate GLFW library
    glfwTerminate();

    // exit
    return (0);
}

//------------------------------------------------------------------------------
void sendPosRobotHaptiqueCHAIVR()
{
    while (simulationRunning)
    {
        if (!monServeurPosRobotHaptiqueCHAIVR->getVirtualRobotHaptiqueDroneMutexBlocAccess())
        {
            monServeurPosRobotHaptiqueCHAIVR->setVirtualRobotHaptiqueDroneMutexBlocAccess(true);
            monServeurPosRobotHaptiqueCHAIVR->setVirtualRobotHaptiqueDroneAxeX(hapticDevicePosition.x());
            monServeurPosRobotHaptiqueCHAIVR->setVirtualRobotHaptiqueDroneAxeY(hapticDevicePosition.y());
            monServeurPosRobotHaptiqueCHAIVR->setVirtualRobotHaptiqueDroneAxeZ(hapticDevicePosition.z());
            monServeurPosRobotHaptiqueCHAIVR->setVirtualRobotHaptiqueDroneMutexBlocAccess(false);
            cSleepMs(2);
        }
        cSleepMs(1);
    }
    monServeurPosRobotHaptiqueCHAIVR->CloseServer();
}

//------------------------------------------------------------------------------

void SendPictureCamera()
{
    bool ret = false;
    unsigned int size = 0;
    unsigned char* Buffer = nullptr;
    while (simulationRunning)
    {
        if (!monServeurFMPPictureDrone->getVirtualPictureMutexBlocAccess() && ImageCameraReady && width > 0 && height > 0)
        {
            monServeurFMPPictureDrone->setVirtualPictureMutexBlocAccess(true);
            ret = cSaveJPG(Image_Camera->getImage(), &Buffer, &size);
            if (ret)
            {
                CopyMemory((unsigned char*)maVirtualPictureCamera->PictureData,Buffer,size);
                monServeurFMPPictureDrone->setVirtualPictureDataSize((int) size);
                maVirtualPictureCamera->DataPictureSize = (int) size;

                monServeurFMPPictureDrone->WriteVirtualPictureStructToMapFile(maVirtualPictureCamera);
            }
            if (Buffer)
            {
                free(Buffer);
                Buffer = nullptr;
            }
            monServeurFMPPictureDrone->setVirtualPictureMutexBlocAccess(false);
            ImageCameraReady = false;
            cSleepMs(2);
        }
        cSleepMs(1);
    }
    monServeurFMPPictureDrone->CloseServer();
}

//------------------------------------------------------------------------------

void SendPictureTelemetrie()
{
    bool ret = false;
    unsigned int size = 0;
    unsigned char* Buffer = nullptr;
    while (simulationRunning)
    {
        if (!monServeurFMPPictureTelemetrie->getVirtualPictureMutexBlocAccess() && ImageCameraHUDReady && width > 0 && height > 0)
        {
            monServeurFMPPictureTelemetrie->setVirtualPictureMutexBlocAccess(true);
            ret = cSavePNG(Image_Camera_HUD->getImage(), &Buffer, &size);
            if (ret)
            {
                CopyMemory((unsigned char*)maVirtualPictureTelemetrie->PictureData,Buffer, size);
                monServeurFMPPictureTelemetrie->setVirtualPictureDataSize((int)size);
                maVirtualPictureTelemetrie->DataPictureSize = (int)size;
                monServeurFMPPictureTelemetrie->WriteVirtualPictureStructToMapFile(maVirtualPictureTelemetrie);
            }
            if (Buffer)
            {
                free(Buffer);
                Buffer = nullptr;
            }
            monServeurFMPPictureTelemetrie->setVirtualPictureMutexBlocAccess(false);
            ImageCameraHUDReady = false;
            cSleepMs(20);
        }
        cSleepMs(1);
    }
    monServeurFMPPictureTelemetrie->CloseServer();
}

//------------------------------------------------------------------------------

void SendPosRobotHaptique()
{
    
    while (simulationRunning)
    {
        if (!monServeurPosRobotHaptique->getVirtualRobotHaptiqueDroneMutexBlocAccess())
        {
            monServeurPosRobotHaptique->setVirtualRobotHaptiqueDroneMutexBlocAccess(true);
            monServeurPosRobotHaptique->setVirtualRobotHaptiqueDroneAxeX(ForceXRobotHaptique);
            monServeurPosRobotHaptique->setVirtualRobotHaptiqueDroneAxeY(ForceYRobotHaptique);
            monServeurPosRobotHaptique->setVirtualRobotHaptiqueDroneAxeZ(ForceZRobotHaptique);
            monServeurPosRobotHaptique->setVirtualRobotHaptiqueDroneUserPresence(PresenceUser);
            monServeurPosRobotHaptique->setVirtualRobotHaptiqueDroneMutexBlocAccess(false);
        }
        cSleepMs(5);
    }
    monServeurPosRobotHaptique->CloseServer();
    
}

//------------------------------------------------------------------------------

void getTelemetry()
{
    //Recuperation de la telemetrie dans le fichier de memoire partagee

    scope_Drone_1_Acceleration->setSignalValues(monClientFMPTelemetrieDrone1->getVirtualTelloEduTelemetryAgx(), monClientFMPTelemetrieDrone1->getVirtualTelloEduTelemetryAgy(), monClientFMPTelemetrieDrone1->getVirtualTelloEduTelemetryAgz());
    scope_Drone_2_Acceleration->setSignalValues(monClientFMPTelemetrieDrone2->getVirtualTelloEduTelemetryAgx(), monClientFMPTelemetrieDrone2->getVirtualTelloEduTelemetryAgy(), monClientFMPTelemetrieDrone2->getVirtualTelloEduTelemetryAgz());
    //-------------------------------------------------//
    scope_Drone_1_Vitesse->setSignalValues(monClientFMPTelemetrieDrone1->getVirtualTelloEduTelemetryVgx(), monClientFMPTelemetrieDrone1->getVirtualTelloEduTelemetryVgy(), monClientFMPTelemetrieDrone1->getVirtualTelloEduTelemetryVgz());
    scope_Drone_2_Vitesse->setSignalValues(monClientFMPTelemetrieDrone2->getVirtualTelloEduTelemetryVgx(), monClientFMPTelemetrieDrone2->getVirtualTelloEduTelemetryVgy(), monClientFMPTelemetrieDrone2->getVirtualTelloEduTelemetryVgz());
    //-------------------------------------------------//
    level_Batterie_Drone_1->setValue(monClientFMPTelemetrieDrone1->getVirtualTelloEduTelemetryBatteryValue());
    level_Batterie_Drone_2->setValue(monClientFMPTelemetrieDrone2->getVirtualTelloEduTelemetryBatteryValue());
    //-------------------------------------------------//
    labelValueBatterie_Drone_1->setText(std::to_string(monClientFMPTelemetrieDrone1->getVirtualTelloEduTelemetryBatteryValue())+"%");
    labelValueBatterie_Drone_2->setText(std::to_string(monClientFMPTelemetrieDrone2->getVirtualTelloEduTelemetryBatteryValue()) + "%");
    //-------------------------------------------------//
    level_H_Drone_1->setValue(monClientFMPTelemetrieDrone1->getVirtualTelloEduTelemetryToF()/100.0);
    level_H_Drone_2->setValue(monClientFMPTelemetrieDrone2->getVirtualTelloEduTelemetryToF()/100.0);
    //-------------------------------------------------//
    labelValueHauteur_Drone_1->setText(std::to_string(monClientFMPTelemetrieDrone1->getVirtualTelloEduTelemetryToF()/100)+"m");
    labelValueHauteur_Drone_2->setText(std::to_string(monClientFMPTelemetrieDrone2->getVirtualTelloEduTelemetryToF()/ 100) + "m");
    //-------------------------------------------------//
    dialPitch_Drone_1->setValue(monClientFMPTelemetrieDrone1->getVirtualTelloEduTelemetryPitch());
    dialPitch_Drone_2->setValue(monClientFMPTelemetrieDrone2->getVirtualTelloEduTelemetryPitch());
    //-------------------------------------------------//
    dialRoll_Drone_1->setValue(monClientFMPTelemetrieDrone1->getVirtualTelloEduTelemetryRoll());
    dialRoll_Drone_2->setValue(monClientFMPTelemetrieDrone2->getVirtualTelloEduTelemetryRoll());
    //-------------------------------------------------//
    dialYaw_Drone_1->setValue(monClientFMPTelemetrieDrone1->getVirtualTelloEduTelemetryYaw());
    dialYaw_Drone_2->setValue(monClientFMPTelemetrieDrone2->getVirtualTelloEduTelemetryYaw());
}

//------------------------------------------------------------------------------

void ComposeImage()
{
    const int MAX_SIZE = 960 * 720 * 3;
	int length = 0;
    monByteArrayImage1.reserve(MAX_SIZE);
    ImageCompose = cImage::create();
    ImageCompose->allocate(960*2, 720, GL_RGB, GL_UNSIGNED_INT);
    while (simulationRunning)
    {
        if (!ImageDisponible && !monClientFMPPictureDrone1->getVirtualPictureMutexBlocAccess())
        {
			monClientFMPPictureDrone1->setVirtualPictureMutexBlocAccess(true);
			length = monClientFMPPictureDrone1->getVirtualPictureDataSize();
            monByteArrayImage1.resize(length);
			for (int i = 0; i < length; i++)
			{
				monByteArrayImage1[i] = monClientFMPPictureDrone1->getMapFileOneByOneUnsignedChar(i);
			}

			if (cLoadJPG(ImageCompose->getImage(), monByteArrayImage1.data(), length))
			{
				// a background picture is ready to update
				ImageDisponible = true;
			}
			monClientFMPPictureDrone1->setVirtualPictureMutexBlocAccess(false);
		}
        cSleepMs(20);
    }
    monClientFMPPictureDrone1->CloseClient();
    ImageCompose->clear();
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
    ForceXRobotHaptique = 0;
    ForceYRobotHaptique = 0;
    ForceZRobotHaptique = 0;
}


//------------------------------------------------------------------------------

void windowSizeCallback(GLFWwindow* a_window, int a_width, int a_height)
{
    // update window size
    width = a_width;
    height = a_height;

	// update position of label model name
    labelHapticDeviceModel->setFontScale((height / 540) / 5.0);
	labelHapticDeviceModel->setLocalPos(width  - labelHapticDeviceModel->getTextWidth()-5, labelRates->getLocalPos().y()- labelHapticDeviceModel->getTextHeight()-5, 0);

	// update position of label device position
    labelHapticDevicePosition->setFontScale((height / 540) / 5.0);
	labelHapticDevicePosition->setLocalPos(width - labelHapticDevicePosition->getTextWidth()-5, labelHapticDeviceModel->getLocalPos().y() - labelHapticDevicePosition->getTextHeight() - 5, 0);

    // update background pos and size
    int HeightBackground = width / RapportTaille;
    double PosYBackGround = (height - HeightBackground) / 2.0;
    BitmapBackgroundVideoStream->setSize(width, HeightBackground);
    BitmapBackgroundVideoStream->setLocalPos(0, PosYBackGround);

    // update front Telemetry
    scope_Drone_1_Acceleration->setSize(width*0.23, (width * 0.23) /2.5);
    scope_Drone_1_Acceleration->setLocalPos((width / 2.0) - (scope_Drone_1_Acceleration->getWidth() + width * 0.01), scope_Drone_1_Acceleration->getHeight() + (scope_Drone_1_Acceleration->getHeight() * 0.1));
    //--------------------------------------------//
    scope_Drone_2_Acceleration->setSize(width * 0.23, (width * 0.23) / 2.5);
    scope_Drone_2_Acceleration->setLocalPos(((width / 2.0) + width * 0.01), scope_Drone_2_Acceleration->getHeight() + (scope_Drone_2_Acceleration->getHeight()*0.1));
    //--------------------------------------------//
    scope_Drone_1_Vitesse->setSize(width * 0.23, (width * 0.23) / 2.5);
    scope_Drone_1_Vitesse->setLocalPos((width / 2.0) - (scope_Drone_1_Acceleration->getWidth() + width * 0.01), 5);
    //--------------------------------------------//
    scope_Drone_2_Vitesse->setSize(width * 0.23, (width * 0.23) / 2.5);
    scope_Drone_2_Vitesse->setLocalPos(((width / 2.0) + width * 0.01), 5);
    //--------------------------------------------//
    level_Batterie_Drone_1->setWidth(width * (0.05));
    level_Batterie_Drone_1->setLocalPos(5,5);
    //--------------------------------------------//
    level_Batterie_Drone_2->setWidth(width * (0.05));
    level_Batterie_Drone_2->setLocalPos(width-(level_Batterie_Drone_2->getWidth()+5), 5);
    //--------------------------------------------//
    level_H_Drone_1->setWidth(width * (0.05));
    level_H_Drone_1->setLocalPos(5+ level_Batterie_Drone_1->getWidth()+width*0.05, 5);
    //--------------------------------------------//
    level_H_Drone_2->setWidth(width * (0.05));
    level_H_Drone_2->setLocalPos(width - (level_H_Drone_2->getWidth()+5+ level_Batterie_Drone_2->getWidth()+width* 0.05), 5);
    //--------------------------------------------//
    dialPitch_Drone_1->setSize(width * 0.05);
    dialPitch_Drone_1->setLocalPos(level_H_Drone_1->getLocalPos().x() + 2*dialPitch_Drone_1->getWidth() + width*0.01, level_H_Drone_1->getHeight());
    //--------------------------------------------//
    dialPitch_Drone_2->setSize(width * 0.05);
    dialPitch_Drone_2->setLocalPos(level_H_Drone_2->getLocalPos().x() - dialPitch_Drone_2->getWidth() - width * 0.01, level_H_Drone_2->getHeight());
    //--------------------------------------------//
    dialYaw_Drone_1->setSize(width * 0.05);
    dialYaw_Drone_1->setLocalPos(level_H_Drone_1->getLocalPos().x() + 2 * dialYaw_Drone_1->getWidth() + width * 0.01, 5 + (width * 0.05) / 2);
    //--------------------------------------------//
    dialYaw_Drone_2->setSize(width * 0.05);
    dialYaw_Drone_2->setLocalPos(level_H_Drone_2->getLocalPos().x() - dialYaw_Drone_2->getWidth() - width * 0.01, 5 + (width * 0.05)/2);
    //--------------------------------------------//
    dialRoll_Drone_1->setSize(width * 0.05);
    dialRoll_Drone_1->setLocalPos(level_H_Drone_1->getLocalPos().x() + 2 * dialRoll_Drone_1->getWidth() + width * 0.01, (dialYaw_Drone_1->getLocalPos().y() + dialPitch_Drone_1->getLocalPos().y())/2.0);
    //--------------------------------------------//
    dialRoll_Drone_2->setSize(width * 0.05);
    dialRoll_Drone_2->setLocalPos(level_H_Drone_2->getLocalPos().x() - dialRoll_Drone_2->getWidth() - width * 0.01, (dialYaw_Drone_2->getLocalPos().y() + dialPitch_Drone_2->getLocalPos().y()) / 2.0);
    //--------------------------------------------//
    labelPitch_Drone_1->setFontScale((height/540)/2.25); //Ici 2.25 car on a une police de 72 de base qui doit etre a 32 pour une taille de 864 * 540
    labelPitch_Drone_1->setLocalPos(dialPitch_Drone_1->getLocalPos().x()-(labelPitch_Drone_1->getTextWidth()/2), dialPitch_Drone_1->getLocalPos().y() - (labelPitch_Drone_1->getTextHeight() / 2));
    //--------------------------------------------//
    labelPitch_Drone_2->setFontScale((height / 540) / 2.25); //Ici 2.25 car on a une police de 72 de base qui doit etre a 32 pour une taille de 864 * 540
    labelPitch_Drone_2->setLocalPos(dialPitch_Drone_2->getLocalPos().x() - (labelPitch_Drone_2->getTextWidth() / 2), dialPitch_Drone_2->getLocalPos().y() - (labelPitch_Drone_2->getTextHeight() / 2));
    //--------------------------------------------//
    labelRoll_Drone_1->setFontScale((height / 540) / 2.25); //Ici 2.25 car on a une police de 72 de base qui doit etre a 32 pour une taille de 864 * 540
    labelRoll_Drone_1->setLocalPos(dialRoll_Drone_1->getLocalPos().x() - (labelRoll_Drone_1->getTextWidth() / 2), dialRoll_Drone_1->getLocalPos().y() - (labelRoll_Drone_1->getTextHeight() / 2));
    //--------------------------------------------//
    labelRoll_Drone_2->setFontScale((height / 540) / 2.25); //Ici 2.25 car on a une police de 72 de base qui doit etre a 32 pour une taille de 864 * 540
    labelRoll_Drone_2->setLocalPos(dialRoll_Drone_2->getLocalPos().x() - (labelRoll_Drone_2->getTextWidth() / 2), dialRoll_Drone_2->getLocalPos().y() - (labelRoll_Drone_2->getTextHeight() / 2));
    //--------------------------------------------//
    labelYaw_Drone_1->setFontScale((height / 540) / 2.25); //Ici 2.25 car on a une police de 72 de base qui doit etre a 32 pour une taille de 864 * 540
    labelYaw_Drone_1->setLocalPos(dialYaw_Drone_1->getLocalPos().x() - (labelYaw_Drone_1->getTextWidth() / 2), dialYaw_Drone_1->getLocalPos().y() - (labelYaw_Drone_1->getTextHeight() / 2));
    //--------------------------------------------//
    labelYaw_Drone_2->setFontScale((height / 540) / 2.25); //Ici 2.25 car on a une police de 72 de base qui doit etre a 32 pour une taille de 864 * 540
    labelYaw_Drone_2->setLocalPos(dialYaw_Drone_2->getLocalPos().x() - (labelYaw_Drone_2->getTextWidth() / 2), dialYaw_Drone_2->getLocalPos().y() - (labelYaw_Drone_2->getTextHeight() / 2));
    //--------------------------------------------//
    labelBatterie_Drone_1->setFontScale((level_Batterie_Drone_1->getHeight()/8)/72.0); //On adapte la taille du texte en fonction de la hauteur du level
    labelBatterie_Drone_1->setLocalPos(level_Batterie_Drone_1->getLocalPos().x()+ level_Batterie_Drone_1->getWidth()+ labelBatterie_Drone_1->getTextWidth()/2, 0);
    //--------------------------------------------//
    labelBatterie_Drone_2->setFontScale((level_Batterie_Drone_2->getHeight() / 8) / 72.0); //On adapte la taille du texte en fonction de la hauteur du level
    labelBatterie_Drone_2->setLocalPos(level_Batterie_Drone_2->getLocalPos().x() - 1.5 * labelBatterie_Drone_2->getTextWidth(), 0);
    //--------------------------------------------//
    labelHauteur_Drone_1->setFontScale((level_H_Drone_1->getHeight() / 8) / 72.0); //On adapte la taille du texte en fonction de la hauteur du level
    labelHauteur_Drone_1->setLocalPos(level_H_Drone_1->getLocalPos().x() + level_H_Drone_1->getWidth() + labelHauteur_Drone_1->getTextWidth() / 2, 0);
    //--------------------------------------------//
    labelHauteur_Drone_2->setFontScale((level_H_Drone_2->getHeight() / 8) / 72.0); //On adapte la taille du texte en fonction de la hauteur du level
    labelHauteur_Drone_2->setLocalPos(level_H_Drone_2->getLocalPos().x() - 1.5 * labelHauteur_Drone_2->getTextWidth(), 0);
    //--------------------------------------------//
    labelValueBatterie_Drone_1->setFontScale((height / 540) / 2.25);
    labelValueBatterie_Drone_1->setLocalPos(level_Batterie_Drone_1->getLocalPos().x()+ level_Batterie_Drone_1->getWidth()/2- labelValueBatterie_Drone_1->getTextWidth()/2, level_Batterie_Drone_1->getHeight());
    //--------------------------------------------//
    labelValueBatterie_Drone_2->setFontScale((height / 540) / 2.25);
    labelValueBatterie_Drone_2->setLocalPos(level_Batterie_Drone_2->getLocalPos().x() + level_Batterie_Drone_2->getWidth() / 2 - labelValueBatterie_Drone_2->getTextWidth() / 2, level_Batterie_Drone_2->getHeight());
    //--------------------------------------------//
    labelValueHauteur_Drone_1->setFontScale((height / 540) / 2.25);
    labelValueHauteur_Drone_1->setLocalPos(level_H_Drone_1->getLocalPos().x() + level_H_Drone_1->getWidth() / 2 - labelValueHauteur_Drone_1->getTextWidth() / 2, level_H_Drone_1->getHeight());
    //--------------------------------------------//
    labelValueHauteur_Drone_2->setFontScale((height / 540) / 2.25);
    labelValueHauteur_Drone_2->setLocalPos(level_H_Drone_2->getLocalPos().x() + level_H_Drone_2->getWidth() / 2 - labelValueHauteur_Drone_2->getTextWidth() / 2, level_H_Drone_2->getHeight());
    //--------------------------------------------//
    labelAcce->setFontScale((scope_Drone_1_Acceleration->getHeight() / 4) / 72.0);
    labelAcce->setLocalPos(width/2 - labelAcce->getTextWidth()/2,scope_Drone_1_Acceleration->getLocalPos().y());
    //--------------------------------------------//
    labelVitesse->setFontScale((scope_Drone_1_Vitesse->getHeight() / 5) / 72.0);
    labelVitesse->setLocalPos(width / 2 - labelVitesse->getTextWidth() / 2, scope_Drone_1_Vitesse->getLocalPos().y());
    //--------------------------------------------//
    labelDrone1->setFontScale((height / 540) / 5.0);
    labelDrone1->setLocalPos(5, height - labelDrone1->getTextHeight());
    //--------------------------------------------//
    labelDrone2->setFontScale((height / 540) / 5.0);
    labelDrone2->setLocalPos((width/2)+5, height - labelDrone2->getTextHeight());
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

    hapticDevice->close();

    cout << "##Fermeture du Programme##" << endl;
    // delete resources
    monClientFMPTelemetrieDrone1->CloseClient();
    monClientFMPTelemetrieDrone2->CloseClient();
    Image_Camera->clear();
    delete hapticsThread;
    delete FMPThreadPicture;
    delete FMP_ThreadPosRobotHaptique;
    delete FMPThreadPictureServeur;
    delete world;
    delete handler;
}

//------------------------------------------------------------------------------

void updateGraphics(void)
{
    /////////////////////////////////////////////////////////////////////
    // UPDATE WIDGETS
    /////////////////////////////////////////////////////////////////////
	// update haptic and graphic device positions
	labelHapticDevicePosition->setText(hapticDevicePosition.str(3));

    // update haptic and graphic rate data
    labelRates->setText(cStr(freqCounterGraphics.getFrequency(), 0) + " Hz / " +
                        cStr(freqCounterHaptics.getFrequency(), 0) + " Hz");

    // update position of label
    labelRates->setFontScale((height / 540) / 5.0);
    labelRates->setLocalPos(width - labelRates->getWidth()-5, camera->getDisplayHeight() - (labelRates->getTextHeight()+5));


    
    /////////////////////////////////////////////////////////////////////
    // UPDATE BACKGROUND
    /////////////////////////////////////////////////////////////////////
    if (ImageDisponible)
    {
        BitmapBackgroundVideoStream->loadFromImage(ImageCompose);
        windowSizeCallback(window, width, height);
    }

    /////////////////////////////////////////////////////////////////////
    // UPDATE TELEMETRY
    /////////////////////////////////////////////////////////////////////
    getTelemetry();
    
    /////////////////////////////////////////////////////////////////////
    // RENDER SCENE
    /////////////////////////////////////////////////////////////////////

    // update shadow maps (if any)
    world->updateShadowMaps(false, mirroredDisplay);

    // render world
    camera->renderView(width, height);

    ImageDisponible = false;

    // wait until all GL commands are completed
    glFinish();

    if (!ImageCameraReady && !ImageCameraHUDReady && width > 0 && height > 0)
    {
        Image_Camera->setSize(width, height);
        camera->copyImageBuffer(Image_Camera);
        if (!VR_Mode)
        {
            ImageCameraReady = true;
        }
    }

    if (VR_Mode)
    {
        //On effectue plusieurs renders afin de donner une image sans le backlayer a la partie VR

        camera->m_backLayer->setShowEnabled(false);

        isSphereShow = SphereMax->getShowEnabled();

        tool->setShowEnabled(false);
        SphereMax->setShowEnabled(false);
        SphereMed->setShowEnabled(false);
        SphereMin->setShowEnabled(false);

        // render world
        camera->renderView(width, height);

        // wait until all GL commands are completed
        glFinish();

        if (!ImageCameraReady && !ImageCameraHUDReady && width > 0 && height > 0)
        {
            Image_Camera_HUD->setSize(width, height);
            camera->copyImageBuffer(Image_Camera_HUD);
            ImageCameraReady = true;
            ImageCameraHUDReady = true;
        }

        camera->m_backLayer->setShowEnabled(true);

        tool->setShowEnabled(true);

        if (isSphereShow)
        {
            SphereMax->setShowEnabled(true);
            SphereMed->setShowEnabled(true);
            SphereMin->setShowEnabled(true);
        }

        // render world
        camera->renderView(width, height);

        // wait until all GL commands are completed
        glFinish();
    }

    // check for any OpenGL errors
    GLenum err = glGetError();
    if (err != GL_NO_ERROR) cout << "Error: " << gluErrorString(err) << endl;
    
}

//------------------------------------------------------------------------------

void updateHaptics(void)
{
    // simulation in now running
    simulationRunning  = true;
    simulationFinished = false;

    bool button0 = false;
    bool button1 = false;
    bool button2 = false;
    bool button3 = false;
    bool Visible = true;
    double axeYCam = 0.0;

    double distanceToolFromCenter = tool->getDeviceGlobalPos().length();

    // main haptic simulation loop
    while(simulationRunning)
    {
		/////////////////////////////////////////////////////////////////////
		// READ HAPTIC DEVICE
		/////////////////////////////////////////////////////////////////////

		// read position 
		cVector3d position;
		hapticDevice->getPosition(position);

		// update global variable for graphic display update
		hapticDevicePosition = position;

        int numContacts = hapticPoint->getNumCollisionEvents();
        hapticDevice->getUserSwitch(0, button0);
        PresenceUser = button0;
        hapticDevice->getUserSwitch(1, button1);
        hapticDevice->getUserSwitch(2, button2);
        hapticDevice->getUserSwitch(3, button3);
        if (numContacts > 0) //Collision detectee
        {
            // Recuperer la force appliquee au tool (vecteur 3D)
            cVector3d collisionForce = hapticPoint->getLastComputedForce();

            double fx = collisionForce.x();
            double fy = collisionForce.y();
            double fz = collisionForce.z();

            if (std::abs(fx) > 2.5 || std::abs(fy) > 2.5 || std::abs(fz) > 2.5)
            {
                // Comparer les composantes et identifier la direction avec la force maximale
                double maxForce = std::abs(fx);
                int direction = 0;

                if (std::abs(fy) > maxForce)
                {
                    maxForce = std::abs(fy);
                    direction = 1;
                }

                if (std::abs(fz) > maxForce)
                {
                    maxForce = std::abs(fz);
                    direction = 2;
                }

                switch (direction)
                {
                case 0:
                    ResetConesDirection();
                    if (fx > 0)
                    {
                        CylinderXMoins->setShowEnabled(true);
                        ForceXRobotHaptique = 1;
                    }
                    else
                    {
                        CylinderXPlus->setShowEnabled(true);
                        ForceXRobotHaptique = -1;
                    }
                    break;
                case 1:
                    ResetConesDirection();
                    if (fy > 0)
                    {

                        CylinderYMoins->setShowEnabled(true);
                        ForceYRobotHaptique = 1;
                    }
                    else
                    {
                        CylinderYPlus->setShowEnabled(true);
                        ForceYRobotHaptique = -1;
                    }
                    break;
                case 2:
                    ResetConesDirection();
                    if (fz > 0)
                    {
                        CylinderZMoins->setShowEnabled(true);
                        ForceZRobotHaptique = 1;
                    }
                    else
                    {
                        CylinderZPlus->setShowEnabled(true);
                        ForceZRobotHaptique = -1;
                    }
                    break;
                }
            }

        }
        else
        {
            ResetConesDirection();
        }

        if (button2)
        {
            if (Visible)
            {
                if (!SphereMax->getShowEnabled())
                {
                    SphereMax->setShowEnabled(true);
                    SphereMed->setShowEnabled(true);
                    SphereMin->setShowEnabled(true);
                }
                else
                {
                    SphereMax->setShowEnabled(false);
                    SphereMed->setShowEnabled(false);
                    SphereMin->setShowEnabled(false);
                }

            }
            Visible = false;
        }
        else
        {
            if (!Visible)
            {
                Visible = true;
            }
        }

        if (button1 && button3)
        {
            axeYCam = 0.0;
            camera->set(cVector3d(3.0, axeYCam, 0.0),
                cVector3d(0.0, 0.0, 0.0),
                cVector3d(0.0, 0.0, 1.0));
        }
        else if (button1)
        {
            if (axeYCam > -4.0)
            {
                axeYCam = axeYCam - 0.001;
                camera->set(cVector3d(3.0, axeYCam, 0.0),
                    cVector3d(0.0, 0.0, 0.0),
                    cVector3d(0.0, 0.0, 1.0));

            }
        }
        else if (button3)
        {
            if (axeYCam < 4.0)
            {
                axeYCam = axeYCam + 0.001;
                camera->set(cVector3d(3.0, axeYCam, 0.0),
                    cVector3d(0.0, 0.0, 0.0),
                    cVector3d(0.0, 0.0, 1.0));

            }
        }




        /////////////////////////////////////////////////////////////////////
        // VERIFY SPHERE CONTACT
        /////////////////////////////////////////////////////////////////////
        distanceToolFromCenter = tool->getDeviceGlobalPos().length();

        if (distanceToolFromCenter < RayonMin - 0.1)
        {
            SphereMax->setHapticEnabled(false);
            SphereMed->setHapticEnabled(false);
        }
        else
        {
            SphereMax->setHapticEnabled(true);
            SphereMed->setHapticEnabled(true);
        }

        /////////////////////////////////////////////////////////////////////
        // HAPTIC FORCE COMPUTATION
        /////////////////////////////////////////////////////////////////////

        // compute global reference frames for each object
        world->computeGlobalPositions(true);

        // update position and orientation of tool
        tool->updateFromDevice();

        // compute interaction forces
        tool->computeInteractionForces();

        // send forces to haptic device
        tool->applyToDevice();

        freqCounterHaptics.signal(1);
    }
    
    // exit haptics thread
    simulationFinished = true;
}

//------------------------------------------------------------------------------

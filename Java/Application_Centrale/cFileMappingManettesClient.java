/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 4.0.2
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */


public class cFileMappingManettesClient {
  private transient long swigCPtr;
  protected transient boolean swigCMemOwn;

  protected cFileMappingManettesClient(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(cFileMappingManettesClient obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  @SuppressWarnings("deprecation")
  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        JNIFileMappingManettesClientJNI.delete_cFileMappingManettesClient(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public cFileMappingManettesClient(boolean _DebugFlag) {
    this(JNIFileMappingManettesClientJNI.new_cFileMappingManettesClient(_DebugFlag), true);
  }

  public boolean OpenClient(String NameSharedMem) {
    return JNIFileMappingManettesClientJNI.cFileMappingManettesClient_OpenClient(swigCPtr, this, NameSharedMem);
  }

  public void CloseClient() {
    JNIFileMappingManettesClientJNI.cFileMappingManettesClient_CloseClient(swigCPtr, this);
  }

  public cVirtualManettes ReadMapFileToVirtualManettesStruct(cVirtualManettes Data) {
    long cPtr = JNIFileMappingManettesClientJNI.cFileMappingManettesClient_ReadMapFileToVirtualManettesStruct(swigCPtr, this, cVirtualManettes.getCPtr(Data), Data);
    return (cPtr == 0) ? null : new cVirtualManettes(cPtr, false);
  }

  public boolean WriteVirtualManettesStructToMapFile(cVirtualManettes Data) {
    return JNIFileMappingManettesClientJNI.cFileMappingManettesClient_WriteVirtualManettesStructToMapFile(swigCPtr, this, cVirtualManettes.getCPtr(Data), Data);
  }

  public void PrintDebug(String msg, boolean _Return) {
    JNIFileMappingManettesClientJNI.cFileMappingManettesClient_PrintDebug(swigCPtr, this, msg, _Return);
  }

  public void PrintStruct(cVirtualManettes Data) {
    JNIFileMappingManettesClientJNI.cFileMappingManettesClient_PrintStruct(swigCPtr, this, cVirtualManettes.getCPtr(Data), Data);
  }

  public cVirtualManettes getVirtualManettesPtr() {
    long cPtr = JNIFileMappingManettesClientJNI.cFileMappingManettesClient_getVirtualManettesPtr(swigCPtr, this);
    return (cPtr == 0) ? null : new cVirtualManettes(cPtr, false);
  }

  public boolean getDebugMode() {
    return JNIFileMappingManettesClientJNI.cFileMappingManettesClient_getDebugMode(swigCPtr, this);
  }

  public boolean getVirtualManettesMutexBlocAccess() {
    return JNIFileMappingManettesClientJNI.cFileMappingManettesClient_getVirtualManettesMutexBlocAccess(swigCPtr, this);
  }

  public double getVirtualManettesAxeX_Gauche() {
    return JNIFileMappingManettesClientJNI.cFileMappingManettesClient_getVirtualManettesAxeX_Gauche(swigCPtr, this);
  }

  public double getVirtualManettesAxeY_Gauche() {
    return JNIFileMappingManettesClientJNI.cFileMappingManettesClient_getVirtualManettesAxeY_Gauche(swigCPtr, this);
  }

  public double getVirtualManettesAxeX_Droite() {
    return JNIFileMappingManettesClientJNI.cFileMappingManettesClient_getVirtualManettesAxeX_Droite(swigCPtr, this);
  }

  public double getVirtualManettesAxeY_Droite() {
    return JNIFileMappingManettesClientJNI.cFileMappingManettesClient_getVirtualManettesAxeY_Droite(swigCPtr, this);
  }

  public void setVirtualManettesPtr(cVirtualManettes VPStruct) {
    JNIFileMappingManettesClientJNI.cFileMappingManettesClient_setVirtualManettesPtr(swigCPtr, this, cVirtualManettes.getCPtr(VPStruct), VPStruct);
  }

  public void setDebugMode(boolean _DebugMode) {
    JNIFileMappingManettesClientJNI.cFileMappingManettesClient_setDebugMode(swigCPtr, this, _DebugMode);
  }

  public void setVirtualManettesMutexBlocAccess(boolean blocaccess) {
    JNIFileMappingManettesClientJNI.cFileMappingManettesClient_setVirtualManettesMutexBlocAccess(swigCPtr, this, blocaccess);
  }

}

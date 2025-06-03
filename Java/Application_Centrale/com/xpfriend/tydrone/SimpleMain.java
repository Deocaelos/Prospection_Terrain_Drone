package com.xpfriend.tydrone;

import com.xpfriend.tydrone.core.Facade;
import com.xpfriend.tydrone.core.Info;
import com.xpfriend.tydrone.core.Runner;
import com.xpfriend.tydrone.factory.SimpleStartableFactory;

import Interfaces.IHM_ControleDrone;
import Threads.ThreadGestionTraitementImage;

import java.io.IOException;

public class SimpleMain extends Facade 
{
	private String monInterfaceWLAN = null;
	private int Indice_Drone = 0;
	private IHM_ControleDrone monIHMDrone;
	private ThreadGestionTraitementImage monThreadGestionTraitementImage = null;
	
	public SimpleMain(String _Inet4Address, int _Indice, IHM_ControleDrone _IHM_Pluri_Drone, ThreadGestionTraitementImage _ThreadGestionTraitementImage)
	{
		this.Indice_Drone = _Indice;
		this.monInterfaceWLAN = _Inet4Address;
		this.monIHMDrone = _IHM_Pluri_Drone;
		this.monThreadGestionTraitementImage = _ThreadGestionTraitementImage;
	}
    @Override
    protected void handleRun(Info info) throws IOException {
        SimpleStartableFactory factory = SimpleStartableFactory.getInstance();
        try {
			new Runner(factory.getLogger()).run(info, factory.createStartables(this.monInterfaceWLAN,this.Indice_Drone,this.monIHMDrone,this.monThreadGestionTraitementImage));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}

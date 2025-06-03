package Utils;

import java.util.Timer;
import java.util.TimerTask;

public class GestionTimer 
{
	private static Timer ChronoTempsVol = null;
	private static TimerTask task = null;
	private static int SecondeEcoulee = 0;
	
	public static void StartTimerVol()
	{
		if(GestionTimer.ChronoTempsVol != null)
		{
			GestionTimer.ChronoTempsVol.cancel();
			GestionTimer.ChronoTempsVol = null;
		}
		
		GestionTimer.ChronoTempsVol = new Timer();
		
		
		if(GestionTimer.task != null)
		{
			GestionTimer.task.cancel();
			GestionTimer.task = null;
		}
		
		//On definit la tache qui va etre executee durant le timer
        GestionTimer.task = new TimerTask() 
        {
            @Override
            public void run() 
            {
                GestionTimer.SecondeEcoulee++;
            }
        };
		GestionTimer.ChronoTempsVol.scheduleAtFixedRate(GestionTimer.task, 0, 1000);
	}
	
	public static int getTempsEcoule()
	{
		return GestionTimer.SecondeEcoulee;
	}
	
	public static void StopTimerVol()
	{
		if(GestionTimer.ChronoTempsVol != null)
		{
			GestionTimer.ChronoTempsVol.cancel();
			GestionTimer.ChronoTempsVol = null;
		}
		
		if(GestionTimer.task != null)
		{
			GestionTimer.task.cancel();
			GestionTimer.task = null;
		}
		GestionTimer.SecondeEcoulee = 0;
	}
}

package Utils;


/**
 * @author Grassi Wilfrid
 * 
 * @Description A Thread sleep simplified class
 * 
 * @Part of TelloDroneConsoleVideo_v1.2
 * @Date 13/09/2023
 * @Modification 17/09/2023
 * 
 * @version 1.2 Eleve
 *
 */
public class Tempo 
{
	public Tempo(int delai)
	{
		try 
		{
			Thread.sleep(delai);
		}
		catch (InterruptedException e1) 
		{

		}
	}
}

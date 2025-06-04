package com.example.infosdrones;

public class DataService
{
    private static ThreadConnexionServeur _ThreadConnexionServeur = null;

    public DataService() {}

    public static void set_ThreadConnexionServeur(ThreadConnexionServeur __ThreadConnexionServeur)
    {
        DataService._ThreadConnexionServeur = __ThreadConnexionServeur;
    }

    public static ThreadConnexionServeur get_ThreadConnexionServeur()
    {
        return(DataService._ThreadConnexionServeur);
    }
}

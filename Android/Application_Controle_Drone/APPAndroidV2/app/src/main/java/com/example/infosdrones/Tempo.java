package com.example.infosdrones;

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
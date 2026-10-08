package com.example.storebackend.Support.Exceptions;

public class IndirizzoInesistenteException extends Exception{

    public IndirizzoInesistenteException(){
        super();
        System.out.println("L'indirizzo cercato non esiste");
    }
}

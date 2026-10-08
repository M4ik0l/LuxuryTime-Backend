package com.example.storebackend.Support.Exceptions;

public class UtenteInesistenteException extends Exception{

    public UtenteInesistenteException(){
        super();
        System.out.println("L'utente non esiste");
    }
}

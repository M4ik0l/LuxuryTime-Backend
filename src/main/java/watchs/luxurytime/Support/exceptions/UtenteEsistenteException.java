package com.example.storebackend.Support.Exceptions;

public class UtenteEsistenteException extends Exception{

    public UtenteEsistenteException(){
        super();
        System.out.println("L'utente esiste già");
    }
}

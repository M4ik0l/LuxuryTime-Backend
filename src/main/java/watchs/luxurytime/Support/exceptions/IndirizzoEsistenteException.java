package com.example.storebackend.Support.Exceptions;

public class IndirizzoEsistenteException extends Exception{

    public IndirizzoEsistenteException(){
        super();
        System.out.println("L'indirizzo indicato esiste già e non è valido");
    }
}

package com.example.storebackend.Support.Exceptions;

public class OrdineInesistenteException extends Exception{

    public OrdineInesistenteException(){
        super();
        System.out.println("L'ordine cercato non esiste");
    }
}

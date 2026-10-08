package com.example.storebackend.Support.Exceptions;

public class ProdottoInesistenteException extends Exception{
    public ProdottoInesistenteException() {
        super();
        System.out.println("Il prodotto cercato non esiste");
    }
}

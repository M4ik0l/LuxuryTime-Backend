package com.example.storebackend.Support.Exceptions;

public class ProdottoEsistenteException extends Exception{
    public ProdottoEsistenteException() {
        super();
        System.out.println("Prodotto già esistente.");
    }
}

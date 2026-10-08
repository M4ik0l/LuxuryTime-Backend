package com.example.storebackend.Support.Exceptions;

public class PrezzoCambiatoException extends Exception{

    public PrezzoCambiatoException(){
        super();
        System.out.println("Il prezzo del prodotto è cambiato");
    }
}

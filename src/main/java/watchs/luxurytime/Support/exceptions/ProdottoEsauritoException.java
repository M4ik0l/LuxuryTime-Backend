package com.example.storebackend.Support.Exceptions;

public class ProdottoEsauritoException extends Exception{
    public ProdottoEsauritoException() {
        super();
        System.out.println("La quantità che si vuole acquistare non è più disponibile!");
    }
}

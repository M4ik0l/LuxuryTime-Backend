package com.example.storebackend.Support.Exceptions;

public class CarrelloVuotoException extends Exception{
    public CarrelloVuotoException() {
        super();
        System.out.println("Il Carrello è vuoto!");
    }
}

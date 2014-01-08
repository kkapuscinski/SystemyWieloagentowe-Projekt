/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package jade;

import java.io.Serializable;

// informacje o aukcji przeznaczone dla kupujących 
public class Auction implements Serializable {
    public int Id; // Id aukcji u brokera
    public ProductType Product; // Typ produktu w aukcji
    public AuctionType AuctionType; // Typ aukcji - Angielska, Holenderska czy Vickereya
    public int Amount; // ilość produktu w aukcji
    public float MinimalStep; // minimalny krok dla kolejngo bidu dotyczy tylko aukcji Angielskiej 
    public float StartingPrice; // Wartość początkowa aukcji dotyczy Angielskiej i Holenderskiej
    
    public Auction(int id, ProductType product, AuctionType auctionType, int amount, float minimalStep, float startingPrice)
    {
        Id = id;
        Product = product;
        AuctionType = auctionType;
        Amount = amount;
        MinimalStep = minimalStep;
        StartingPrice = startingPrice;
        
    }
    
}

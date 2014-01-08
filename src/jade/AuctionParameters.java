/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package jade;

import jade.core.AID;
import java.io.Serializable;

// informacje o aukcji dla brokera oraz sprzedającego
public class AuctionParameters implements Serializable
{
    public int Id; // identyfikator aukcji u sprzedającego
    public ProductType Product; // typ produktu sprzedawanego
    public int Amount; // ilość produktu
    public float EnglishAuctionStartingPrice; // Cena początkowa aukcji Angielskiej
    public float DutchAuctionStartingPrice; // Cena początkowa dla aukcji Holenderskiej
    public float AuctionMinimalPrice; // Cena minimalna aukcji niezależna od typu
    public float MinimalStep; // minimalny krok bidu (dotyczy aukcji angielskiej i holenderskiej)
    public AID SellerAID; // AID agenta sprzedającego
    public float SoldPrice; // Cena jaką zapłacano za aukcję
    
    public AuctionParameters(int id, ProductType product, int amount, float easp, float dasp, float amp,  float minimalStep, AID sellerAID)
    {
        Id = id;
        Product = product;
        Amount = amount;
        EnglishAuctionStartingPrice = easp;
        DutchAuctionStartingPrice = dasp;
        AuctionMinimalPrice = amp;
        MinimalStep = minimalStep;
        SellerAID = sellerAID;
    }
}

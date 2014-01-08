package Seller;

import jade.AgentExtension;
import jade.AuctionParameters;
import jade.ProductType;
import jade.core.AID;
import jade.core.Agent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// Klasa Agenta sprzedawcy
public class AgentSeller extends Agent 
{
    public  List<SellerAuction> Auctions; // Lista aukcji wystawionych przez sprzedawcę
    public  AID BrokerAID ; // Identyfikator AID Brokera
	
    protected void setup() 
    {
        
        Auctions = new ArrayList<>();
        Random random = new Random();
        
        // tworzę losową listę aukcji wystawionych przez sprzedawcę
        for (int i = 0; i < 2; i++) {
            // losuje produkt
            ProductType[] values = ProductType.values();
            ProductType prodtype = values[random.nextInt(5)];
            // losuję ilość produktu
            int amountOfProduct = random.nextInt(20)+1;
            
            // tworzę aukcję
            AuctionParameters tmpAuction = new AuctionParameters(i, prodtype, amountOfProduct, amountOfProduct*20, amountOfProduct*40, amountOfProduct*25, 0.5F, this.getAID());
            Auctions.add(new SellerAuction(tmpAuction));
        }

        System.out.println("Hello! Seller-agent "+getAID().getName()+" is ready.");

        AgentExtension.DFRegister(this, "Seller");
        
        BehaviourSeller behaviourSeller = new BehaviourSeller(this);
        addBehaviour(behaviourSeller);
    }

    protected void takeDown() 
    {
        System.out.println("Seller-agent "+getAID().getName()+" terminating.");
    }
		

    
}

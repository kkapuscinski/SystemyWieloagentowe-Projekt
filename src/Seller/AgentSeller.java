package Seller;

import Seller.BehaviourSeller;
import jade.AgentExtension;
import jade.AuctionParameters;
import jade.AuctionParameters;
import jade.ProductType;
import jade.ProductType;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;


public class AgentSeller extends Agent 
{
    public  List<SellerAuction> Auctions;
    public  AID BrokerAID ;
	
    protected void setup() 
    {
        
        Auctions = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < 2; i++) {
            ProductType[] values = ProductType.values();
            ProductType prodtype = values[random.nextInt(5)];
            int amountOfProduct = random.nextInt(20)+1;
            AuctionParameters tmpAuction = new AuctionParameters(i, prodtype, amountOfProduct, amountOfProduct*20, amountOfProduct*40, amountOfProduct*25, 0.5F, this.getAID());
            Auctions.add(new SellerAuction(tmpAuction));
        }

        System.out.println("Hello! Seller-agent "+getAID().getName()+" is ready.");
        Object[] args = getArguments();

        AgentExtension.DFRegister(this, "Seller");
        
        BehaviourSeller behaviourSeller = new BehaviourSeller(this);
        addBehaviour(behaviourSeller);
    }

    protected void takeDown() 
    {
        System.out.println("Seller-agent "+getAID().getName()+" terminating.");
    }
		

    
}

package Seller;

import Seller.BehaviourSeller;
import jade.Auction;
import jade.Auction;
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
    public Codec Codec = new SLCodec();
	
    protected void setup() 
    {
        
        Auctions = new ArrayList<SellerAuction>();
        Random random = new Random();
        for (int i = 0; i < 2; i++) {
            ProductType[] values = ProductType.values();
            ProductType prodtype = values[random.nextInt(5)];
            int amountOfProduct = random.nextInt(20);
            Auction tmpAuction = new Auction(i, prodtype, amountOfProduct, amountOfProduct*20, amountOfProduct*40, amountOfProduct*20, 1, this.getAID());
            Auctions.add(new SellerAuction(tmpAuction));
        }

        System.out.println("Hello! Seller-agent "+getAID().getName()+" is ready.");
        Object[] args = getArguments();
        getContentManager().registerLanguage(Codec);

        DFRegister("seller", this);
        
        BrokerAID = findBroker();
        try {
            Thread.sleep(15000);
        } catch (InterruptedException ex) {
            Logger.getLogger(AgentSeller.class.getName()).log(Level.SEVERE, null, ex);
        }
        BehaviourSeller behaviourSeller = new BehaviourSeller(this);
        addBehaviour(behaviourSeller);
    }

    protected void takeDown() 
    {
        System.out.println("Seller-agent "+getAID().getName()+" terminating.");
    }
		

    public static void DFRegister(String typAgenta, Agent agent)
    {
        DFAgentDescription template = new DFAgentDescription();
        template.setName( agent.getAID() ); 
        ServiceDescription sd  = new ServiceDescription();
        sd.setType(typAgenta );
        sd.setName( agent.getName() );
        template.addServices(sd);

        try
        {
            DFService.register(agent, template );  
        } 
        catch (FIPAException fe) { fe.printStackTrace(); }	
    }
    
    private AID findBroker()
	{
            DFAgentDescription template = new DFAgentDescription();
            ServiceDescription sd = new ServiceDescription();
            sd.setType("Broker");
            template.addServices(sd);
            AID brokerAID = null;
            try {
        	DFAgentDescription[] result = DFService.search(this, template); 
        	
                if(result.length>0)
                    {
                        brokerAID= result[0].getName();
                    } 
            }
                catch (FIPAException fe) {
                fe.printStackTrace();
            }
            return brokerAID;
	}
    
    
}

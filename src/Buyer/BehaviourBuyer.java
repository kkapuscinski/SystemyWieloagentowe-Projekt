package Buyer;

import Buyer.AgentBuyer;
import jade.content.lang.sl.SLCodec;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class BehaviourBuyer extends SimpleBehaviour {

	
	private int step = 0;

	AgentBuyer agent ;
	AID seller;

	public BehaviourBuyer (Agent a)
	{
	  super(a);	
	  agent = (AgentBuyer) myAgent;
	}
	
	public void action() {
		  switch (step) {
		    case 0:
		    	seller = findSeller();
		    	if(null!=seller)
		    			step++;
		    	else step=3;
		    	break;
		    case 1:
		    	askForBook();
		    	if(getMsgAboutBook()==true) step++;
		    	else step+=2;
		    	break;
		    case 2:		
		    	buyBook();
		    	step++;
		    	break;
		    case 3:
		    	deregister();
		    	step = 0;
		    	break;
		  }

	}

	private boolean getMsgAboutBook() {
		String reply =  receiveMsg(ACLMessage.INFORM, seller,1000);
		if(reply.equals("OK")) 
			return true;
		else
			return false;
	}

	private void askForBook() {
	
	}

	private void buyBook() {
		System.out.println("Buy a book!");
		
	}

	private void deregister() {
		myAgent.removeBehaviour(this) ;	
	}

	private String receiveMsg(int msgType, AID fromAgent, long time) {
		String replyMsg = null;
		MessageTemplate mt = MessageTemplate.and(  
	 	           MessageTemplate.MatchPerformative(msgType ),
	 	           MessageTemplate.MatchSender(fromAgent)) ;

			ACLMessage reply = myAgent.blockingReceive(mt, time); 
			
			if(reply!=null)
			{
                            try {
				replyMsg =  reply.getContentObject().toString();
                                }
                            catch (Exception ex) { ex.printStackTrace(); }
			}
		return replyMsg;
	}

	private AID findSeller()
	{
		DFAgentDescription template = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
        sd.setType("seller");
        template.addServices(sd); 
        try {
        	DFAgentDescription[] result = DFService.search(myAgent, template); 
        	
            if(result.length>0)
         		{
            	seller= result[0].getName();		
         		} 
         }
         catch (FIPAException fe) {
           fe.printStackTrace();
         }
		return seller;
	}
	
	

	 void sendMsg(int msgType, String content) {
		      if (seller == null) {
		         System.out.println("Find seller!");
		         return;
		      }
		      ACLMessage msg = new ACLMessage(msgType);
		      msg.setLanguage((new SLCodec()).getName());

		      try {
		    	 msg.setContentObject(content);
		         msg.addReceiver(seller);
		         myAgent.send(msg);
		         
		      }
		      catch (Exception ex) { ex.printStackTrace(); }
		   }

	
	
	@Override
	public boolean done() {
		System.out.println("Done");
		return false;
	}

}

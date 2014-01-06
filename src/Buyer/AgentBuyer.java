import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;


public class AgentBuyer extends Agent {
	private String targetBookTitle;
	private Codec codec = new SLCodec();
	
	protected void setup() {
		System.out.println("Hello! Buyer-agent "+getAID().getName()+" is ready.");
		Object[] args = getArguments();
		getContentManager().registerLanguage(codec);
	    
		DFRegister("buyer", this);
		BehaviourBuyer behaviourBuyer = new BehaviourBuyer(this);
		addBehaviour(behaviourBuyer);
		
		if (args != null && args.length > 0) {
		targetBookTitle = (String) args[0];
		System.out.println("Trying to buy "+targetBookTitle);}
		else {
		System.out.println("No book title specified");
		doDelete();}}

		protected void takeDown() {
		System.out.println("Buyer-agent "+getAID().getName()+" terminating.");
		}
		
		public String getTargetBookTitle() {
			return targetBookTitle;
		}

		public static void DFRegister(String typAgenta, Agent agent)
		{
			   DFAgentDescription template = new DFAgentDescription();
		       template.setName( agent.getAID() ); 
		       ServiceDescription sd  = new ServiceDescription();
		       sd.setType(typAgenta );
		       sd.setName( agent.getName() );
		       template.addServices(sd);

	  
	     try{
	     DFService.register(agent, template );  
	     	} catch (FIPAException fe) { fe.printStackTrace(); }	
		}
}


public class Main {

	public static final int GATE_WAY_INDEX_DEFAULT = 0;
	public static final int DELTA_T = 10000;
	
	public static void main(String[] args) {
		IAS ias = IAS.getInstance();
		
		// setup 5 gateway
		ias.systemSetupPhase(5);
		
		User user = new User("abc", "123");
		user.registerUser();

		//authenticate 1 and 2 in smart card
		user.authenticate("abc", "123");
		SmartCard userSmartCard = user.getSmartCard();

		//authenticate 3 in gate way
		GateWay gateWayDefault = IAS.getInstance().getListGW().get(GATE_WAY_INDEX_DEFAULT);
		if (!gateWayDefault.authenticateStep3(userSmartCard.t1, userSmartCard.m1, userSmartCard.m2, userSmartCard.m3, userSmartCard.m4, user.PU))
			return;

		//authenticate 4 in IoT App Server
		if (!ias.authenticateStep4(userSmartCard.t1, gateWayDefault.t_2, userSmartCard.m1, userSmartCard.m2, userSmartCard.m3, userSmartCard.m4,
				gateWayDefault.m5, gateWayDefault.m6, gateWayDefault.m7, gateWayDefault.m8, user.PU, gateWayDefault.getPGW(), user.getMID()))
			return;


	}
	
}

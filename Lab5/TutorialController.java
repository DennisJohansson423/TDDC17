public class TutorialController extends Controller {

    public SpringObject object;

    ComposedSpringObject cso;

    /* These are the agents senses (inputs) */
	DoubleFeature x; /* Positions */
	DoubleFeature y;
	DoubleFeature vx; /* Velocities */
	DoubleFeature vy;
	DoubleFeature angle; /* Angle */

    /* Example:
     * x.getValue() returns the vertical position of the rocket 
     */

	/* These are the agents actuators (outputs)*/
	RocketEngine leftRocket;
	RocketEngine middleRocket;
	RocketEngine rightRocket;

    /* Example:
     * leftRocket.setBursting(true) turns on the left rocket 
     */
	
	public void init() {
		cso = (ComposedSpringObject) object;
		x = (DoubleFeature) cso.getObjectById("x");
		y = (DoubleFeature) cso.getObjectById("y");
		vx = (DoubleFeature) cso.getObjectById("vx");
		vy = (DoubleFeature) cso.getObjectById("vy");
		angle = (DoubleFeature) cso.getObjectById("angle");

		leftRocket = (RocketEngine) cso.getObjectById("rocket_engine_left");
		rightRocket = (RocketEngine) cso.getObjectById("rocket_engine_right");
		middleRocket = (RocketEngine) cso.getObjectById("rocket_engine_middle");
	}

    public void tick(int currentTime) {

    	/* TODO: Insert your code here */
    	
    	
    	System.out.println("Angle is: " + angle.getValue());
    	System.out.println("Vx is: " + vx.getValue());
    	System.out.println("Vy is: " + vy.getValue());
    	System.out.println("Y is: " + y.getValue());
    	
    	if(y.getValue() >= 700) {
    		//rockets fire
    		System.out.println("We get here1");
    		leftRocket.setBursting(true);
    		rightRocket.setBursting(true);
    		middleRocket.setBursting(true);
    		
    	}
    	else if (y.getValue() < -1400) {
    		//rockets stop
    		System.out.println("We get here2");
    		leftRocket.setBursting(false);
    		rightRocket.setBursting(false);
    		middleRocket.setBursting(false);
    	}
    	else {
    		leftRocket.setBursting(false);
    		rightRocket.setBursting(false);
    		middleRocket.setBursting(false);
    		
    	}
    	
    	
    }

}

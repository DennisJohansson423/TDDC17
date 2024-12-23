public class StateAndReward {

	
	/* State discretization function for the angle controller */
	public static String getStateAngle(double angle, double vx, double vy) {

		String state = "OneStateToRuleThemAll";
		double correct = -0.01206915150499173;
		
		String qFourState = "Q4";
		String qThreeState = "Q3";	
		String qOneState = "Q1";	
		String qTwoState = "Q2";	
		
		String correctAngle = "CORRECT";
		
		
		if (angle < correct && angle >= -1.5) {
			state = qFourState;
		}
		else if (angle < -1.5 && angle >= -3) {
			state = qThreeState;
		}
		else if (angle <= 1.5 && angle > correct) {
			state = qOneState;
		}
		else if (angle <= 3 && angle > 1.5) {
			state = qTwoState;
		}
		else if (angle == correct) {
			state = correctAngle;
		}
		return state;
	}

	
	/* Reward function for the angle controller */
	public static double getRewardAngle(double angle, double vx, double vy) {
		
		int nrValues = 5;
		double minAngle = -3;
		double maxAngle = 3;
		int angleState = discretize2(angle, nrValues, minAngle, maxAngle);
		double reward = 0;
		
		if (angleState == 0 || angleState == 4) {
			reward = -10;
		}
		else if (angleState == 1 || angleState == 3) {
			reward = -5;
		}
		else if (angleState == 2) {
			reward = 10;
		}
		return reward;
	}

	/* State discretization function for the full hover controller */
	public static String getStateHover(double angle, double vx, double vy) {

		String hovering = "hovering";
		String state = "OneStateToRuleThemAll2";
		String lowY = "lowY";
		String highY = "highY";
		String lowX = "lowX";
		String highX = "highX";
		String lowY_lowX = "lowY_lowX";
		String lowY_highX = "lowY_highX";
		String highY_lowX = "highY_lowX";
		String highY_highX = "highY_highX";
		
		if (vx >= -0.5 && vx <= 0.5) {
			if (vy > 0.5) {
				state = highY;
			} 
			else if (vy < -0.5) {
				state = lowY;
			}
			else {
				state = hovering;
			}
		}
		else if (vy >= -0.5 && vy <= 0.5) {
			if (vx > 0.5) {
				state = highX;
			} 
			else if (vx < -0.5) {
				state = lowX;
			}
			else {
				state = hovering;
			}
		}
		else if (vy <= -0.5) {
			if (vx <= -0.5) {
				state = lowY_lowX;
			}
			else if (vx >= 0.5) {
				state = lowY_highX;
			}
		}
		else if (vy >= 0.5) {
			if (vx <= -0.5) {
				state = highY_lowX;
			}
			else if (vx >= 0.5) {
				state = highY_highX;
			}
		}
		return state;
	}

	/* Reward function for the full hover controller */
	public static double getRewardHover(double angle, double vx, double vy) {

		double reward = 0;

		int nrValues = 3;
		double minV = -0.5;
		double maxV = 0.5;
		int vy_State = discretize(vy, nrValues, minV, maxV);
		int vx_State = discretize(vx, nrValues, minV, maxV);
				
		if ((vy_State == 0 && vx_State == 2) || (vy_State == 2 && vx_State == 0) || (vy_State == 2 && vx_State == 2) || (vy_State == 0 && vx_State == 0)) {
			reward = -10;
		}
		else if (vy_State == 1 && vx_State == 1) {
			reward = 10;
		}
		else {
			reward = -5;
		}
		return reward;
	}

	// ///////////////////////////////////////////////////////////
	// discretize() performs a uniform discretization of the
	// value parameter.
	// It returns an integer between 0 and nrValues-1.
	// The min and max parameters are used to specify the interval
	// for the discretization.
	// If the value is lower than min, 0 is returned
	// If the value is higher than min, nrValues-1 is returned
	// otherwise a value between 1 and nrValues-2 is returned.
	//
	// Use discretize2() if you want a discretization method that does
	// not handle values lower than min and higher than max.
	// ///////////////////////////////////////////////////////////
	public static int discretize(double value, int nrValues, double min,
			double max) {
		if (nrValues < 2) {
			return 0;
		}

		double diff = max - min;

		if (value < min) {
			return 0;
		}
		if (value > max) {
			return nrValues - 1;
		}

		double tempValue = value - min;
		double ratio = tempValue / diff;

		return (int) (ratio * (nrValues - 2)) + 1;
	}

	// ///////////////////////////////////////////////////////////
	// discretize2() performs a uniform discretization of the
	// value parameter.
	// It returns an integer between 0 and nrValues-1.
	// The min and max parameters are used to specify the interval
	// for the discretization.
	// If the value is lower than min, 0 is returned
	// If the value is higher than min, nrValues-1 is returned
	// otherwise a value between 0 and nrValues-1 is returned.
	// ///////////////////////////////////////////////////////////
	public static int discretize2(double value, int nrValues, double min,
			double max) {
		double diff = max - min;

		if (value < min) {
			return 0;
		}
		if (value > max) {
			return nrValues - 1;
		}

		double tempValue = value - min;
		double ratio = tempValue / diff;

		return (int) (ratio * nrValues);
	}

}

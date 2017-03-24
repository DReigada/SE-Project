package pt.ulisboa.tecnico.softeng.broker.domain;

import pt.ulisboa.tecnico.softeng.activity.exception.ActivityException;
import pt.ulisboa.tecnico.softeng.broker.domain.Adventure.State;
import pt.ulisboa.tecnico.softeng.broker.exception.RemoteAccessException;
import pt.ulisboa.tecnico.softeng.broker.interfaces.ActivityInterface;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ReserveActivityState extends AdventureState {
	private static Logger logger = LoggerFactory.getLogger(CancelledState.class);
	
	@Override
	public State getState() {
		return State.CANCELLED;
	}

	@Override
	public void process(Adventure adventure) {
		logger.debug("process");
		try {
			String activityConfirmation = ActivityInterface.reserveActivity(adventure.getBegin(), adventure.getEnd(), adventure.getAge());
		} catch (ActivityException ae) {
			adventure.setState(State.UNDO);
		} catch (RemoteAccessException rae) {
			incNumOfRemoteErrors();
			if (getNumOfRemoteErrors() == 5) 
			adventure.setState(Adventure.State.UNDO);
			return;
		}

		if (adventure.getBegin().equals(adventure.getEnd())) {
			adventure.setState(State.CONFIRMED);
		} else {
			adventure.setState(State.BOOK_ROOM);
		}
	}
}



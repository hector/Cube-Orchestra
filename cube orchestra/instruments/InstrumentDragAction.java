package instruments;

import org.mt4j.components.MTComponent;
import org.mt4j.components.interfaces.IMTComponent3D;
import org.mt4j.input.inputProcessors.IGestureEventListener;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.input.inputProcessors.componentProcessors.dragProcessor.DragEvent;


public class InstrumentDragAction implements IGestureEventListener {
	private IMTComponent3D dragTarget;
	private boolean useCustomTarget;
	
	public InstrumentDragAction(){
		this.useCustomTarget = false;
	}
	
	public InstrumentDragAction(IMTComponent3D dragTarget){
		this.dragTarget = dragTarget;
		this.useCustomTarget = true;
	}
	

	/* (non-Javadoc)
	 * @see com.jMT.input.gestureAction.IGestureAction#processGesture(com.jMT.input.inputAnalyzers.GestureEvent)
	 */
	public boolean processGestureEvent(MTGestureEvent g) {
		if (g instanceof DragEvent){
			DragEvent dragEvent = (DragEvent)g;
			
			if (!useCustomTarget)
				dragTarget = dragEvent.getTargetComponent(); 
			
			switch (dragEvent.getId()) {
			case MTGestureEvent.GESTURE_DETECTED:
				//Put target on top -> draw on top of others
				if (dragTarget instanceof MTComponent){
					MTComponent baseComp = (MTComponent)dragTarget;
					
					baseComp.sendToFront();
					
					/*
					//End all animations of the target
					Animation[] animations = AnimationManager.getInstance().getAnimationsForTarget(dragTarget);
					for (int i = 0; i < animations.length; i++) {
						Animation animation = animations[i];
						animation.stop();
					}
					*/
				}
				dragTarget.translateGlobal(dragEvent.getTranslationVect());
				break;
			case MTGestureEvent.GESTURE_UPDATED:
				dragTarget.translateGlobal(dragEvent.getTranslationVect());
				break;
			case MTGestureEvent.GESTURE_ENDED:
				break;
			default:
				break;
			}
		}
		return false;
	}

}

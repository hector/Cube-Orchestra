package effects;

import org.mt4j.components.MTComponent;
import org.mt4j.components.interfaces.IMTComponent3D;
import org.mt4j.input.inputProcessors.IGestureEventListener;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.input.inputProcessors.componentProcessors.scaleProcessor.ScaleEvent;
import org.mt4j.util.math.Vector3D;


public class EffectScaleAction implements IGestureEventListener {
	
	/** The target. */
	private IMTComponent3D target;
	
	private boolean hasScaleLimit;
	
	private float minScale;
	
	private float maxScale;
	
	/**
	 * Instantiates a new default scale action.
	 */
	public EffectScaleAction(){
		this(null, 0,0, false);
	}
	
	/**
	 * Instantiates a new default scale action.
	 * 
	 * @param customTarget the custom target
	 */
	public EffectScaleAction(IMTComponent3D customTarget){
		this(customTarget, 0,0, false);
	}
	
	
	
	
	/**
	 * Instantiates a new default scale action.
	 *
	 * @param minScaleFactor the min scale factor
	 * @param maxScaleFactor the max scale factor
	 */
	public EffectScaleAction(float minScaleFactor, float maxScaleFactor){
		this(null, minScaleFactor, maxScaleFactor, true);
	}
	
	/**
	 * Instantiates a new default scale action.
	 *
	 * @param customTarget the custom target
	 * @param minScaleFactor the min scale factor
	 * @param maxScaleFactor the max scale factor
	 */
	public EffectScaleAction(IMTComponent3D customTarget, float minScaleFactor, float maxScaleFactor){
		this(customTarget, minScaleFactor, maxScaleFactor, true);
	}
	
	
	/**
	 * Instantiates a new default scale action.
	 *
	 * @param customTarget the custom target
	 * @param minScaleFactor the min scale factor
	 * @param maxScaleFactor the max scale factor
	 * @param useScaleLimit  use scale limit
	 */
	private EffectScaleAction(IMTComponent3D customTarget, float minScaleFactor, float maxScaleFactor, boolean useScaleLimit){
		this.target = customTarget;
		if (minScaleFactor < 0 || maxScaleFactor < 0){
			System.err.println("minScaleFactor < 0 || maxScaleFactor < 0    invalid settings!");
			this.hasScaleLimit = false;
		}else{
			this.hasScaleLimit = useScaleLimit;
		}
		this.minScale = minScaleFactor;
		this.maxScale = maxScaleFactor;
	}
	


	/* (non-Javadoc)
	 * @see com.jMT.input.gestureAction.IGestureAction#processGesture(com.jMT.input.inputAnalyzers.GestureEvent)
	 */
	public boolean processGestureEvent(MTGestureEvent g) {
		if (g instanceof ScaleEvent){
			ScaleEvent scaleEvent = (ScaleEvent)g;
			
			if (target == null)
				target = scaleEvent.getTargetComponent(); 
			
			switch (scaleEvent.getId()) {
			case MTGestureEvent.GESTURE_DETECTED:
				if (target instanceof MTComponent){
					((MTComponent)target).sendToFront();
					/*
					Animation[] animations = AnimationManager.getInstance().getAnimationsForTarget(target);
					for (int i = 0; i < animations.length; i++) {
						Animation animation = animations[i];
						animation.stop();
					}
					*/
				}
				break;
			case MTGestureEvent.GESTURE_UPDATED:
				
				if (this.hasScaleLimit){
					if (target instanceof MTComponent) {
						MTComponent comp = (MTComponent) target;
						
						//FIXME actually we should use globalmatrix but performance is better for localMatrix..
						Vector3D currentScale = comp.getLocalMatrix().getScale(); 
						
//						if (currentScale.x != currentScale.y){
//							System.out.println("non uniform scale!");
//						}
						
						//We only check X because only uniform scales (x=y factor) should be used!
						if (currentScale.x * scaleEvent.getScaleFactorX() > this.maxScale){
//							System.out.println("Scale MAX Limit Hit!");
							//We should set to min scale, but we choose performance over accuracy
							//float factor = (1f/currentScale.x) * maxScale;
							//target.scaleGlobal(factor, factor, scaleEvent.getScaleFactorZ(), scaleEvent.getScalingPoint());
						}else if (currentScale.x * scaleEvent.getScaleFactorX() < this.minScale){
//							System.out.println("Scale MIN Limit Hit!");
							//We should set to min scale, but we choose performance over accuracy
							//float factor = (1f/currentScale.x) * minScale;
							//target.scaleGlobal(factor, factor, scaleEvent.getScaleFactorZ(), scaleEvent.getScalingPoint());
						}else{
							target.scaleGlobal(
									scaleEvent.getScaleFactorX(), 
									scaleEvent.getScaleFactorY(), 
									scaleEvent.getScaleFactorZ(), 
									scaleEvent.getScalingPoint());
							checkEffects(target);
						}
						
					}
				}else{
					target.scaleGlobal(
							scaleEvent.getScaleFactorX(), 
							scaleEvent.getScaleFactorY(), 
							scaleEvent.getScaleFactorZ(), 
							scaleEvent.getScalingPoint());
					checkEffects(target);
				}
				break;
			case MTGestureEvent.GESTURE_ENDED:
				break;
			default:
				break;
			}
		}
		return false;
	}

	private void checkEffects(IMTComponent3D target) {
		if (target instanceof Effect) {
			Effect effect = (Effect) target;
			effect.checkEnabledInstruments();
			effect.setAmount();
		}
	}

}

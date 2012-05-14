/* 
sliceplay~ version 0.9.12, an object class for Pure Data.

Created by Katja Vetter on 11/17/09,  Copyright 2009 Katja Vetter. 
Version 0.9.12 created on 12/10/2010.
(katjavetter@gmail.com, www.katjaas.nl)

This software is published under standard BSD licensing terms.
THE AUTHOR MAKES NO WARRANTY, EXPRESS OR IMPLIED,
IN CONNECTION WITH THIS SOFTWARE! 


Based on Pure Data by Miller Puckette and others.


version 0.9.12:
- addition of 'interrupt' method and fade-out of interrupted slice
- addition of method and message selector 'minspeed' for minimum playback speed
- addition of play status message outlet
- negative cuepoints are now ignored, not interpreted modulo-loopsize


version 0.9.11: 
- used garray_getfloatwords() instead of garray_getfloatarray(), for 64 bit compatibility


version 0.91:
- fixed bug with playback speed
- amplitude compensation is now variable user parameter


version 0.9: initial version


*/


#include "m_pd.h"							// Pure Data header file
#include <math.h>
#include <stdlib.h>							// for abs()

#define PI 3.14159265
#define COMPENSATIONDEFAULT 0.			// amplitude compensation factor default
#define SPEEDDEFAULT 1.					// default playback speed
#define MINSPEEDDEFAULT 0.125			// minimum playback speed
#define INTERRUPTDEFAULT 0				// default option for interrupt of actual slice playback


// global pointer to class struct
static t_class *sliceplay_class;


// slicrec~ class struct
typedef struct
{
	t_object x_obj;							
	t_float startindex;						// floating point 'index' because of variable playback speed
	t_float actualindex;						// last used index for playback
	t_float speed;							// playback speed of next slice
	t_float increment;						// floating point index increment
	t_float fadeoutpoint;						// index where a fade out of previous slice shall start
	t_float fadeoutincrement;					// index increment of previous slice
	t_float compensationsetting;				// amplitude compensation user parameter 0 - 1
	t_float compensationfactor;				// amplitude compensation factor
	t_float fadeoutcompensation;				// amplitude compensation factor of fade out tail
	t_float minspeed;						// minimum playback speed, user variable
	t_int interrupt;							// a flag
	t_int loopsize;							// largest power of two that fits in external array
	t_int loopmask;						// bitwise-and mask for loopsize
	t_int playtimer;							// playback countdown timer
	t_int counter;							// to count upwards for indexing
	t_int fadeouttimer;						// fade out countdown timer
	t_int fadeoutcounter;					// to count upwards for fadeout
	t_word *arraypointer;					// pointer to the global / external array
	t_symbol *arrayname;					// pointer to struct containing global / external array name
	t_outlet *playstatusoutlet;					// outlet for play status message
	t_clock *statusclock;						// timer for play status message
	t_float testvalue;						// for debug
	
} t_sliceplay;


static void sliceplay_tick(t_sliceplay *x, t_int playstatus)
{
	outlet_float(x->playstatusoutlet, playstatus);		// send message with playstatus 1 or 0
}


static void *sliceplay_new(t_symbol *symbol)
{
	t_sliceplay *x = (t_sliceplay *)pd_new(sliceplay_class);
	inlet_new(&x->x_obj, &x->x_obj.ob_pd, gensym ("list"), gensym("cuepoints"));
	outlet_new(&x->x_obj, gensym("signal"));
	x->playstatusoutlet = outlet_new(&x->x_obj, &s_float);
	x->arrayname = symbol;
	x->playtimer = 0;
	x->speed = SPEEDDEFAULT;
	x->compensationfactor = 1.;
	x->compensationsetting = COMPENSATIONDEFAULT;
	x->minspeed = MINSPEEDDEFAULT;
	x->interrupt = INTERRUPTDEFAULT;
	
	return (x);
}
// end of sliceplay_new function definition


static void sliceplay_free(t_sliceplay *x)
{
	if(x->statusclock) clock_free(x->statusclock);
}


static t_int *sliceplay_perform(t_int *w)
{
	t_sample *output = (t_sample *)(w[1]);						// audio signal input
	t_sliceplay *x = (t_sliceplay *)(w[2]);
	t_int blocksize = (t_int)(w[3]);
	
	if((!x->playtimer) && (!x->fadeouttimer))
	{
		while(blocksize--) *output++ = 0.;
		return (w+4);
	}
	
	t_word *arraypointer = x->arraypointer;
	
	t_int loopmask = x->loopmask;
	
	// variables for actual slice
	t_float value1, value2, fraction;
	t_int index = 0;
	t_int counter = x->counter;
	t_int playtimer = x->playtimer;
	t_float compensationfactor = x->compensationfactor;
	t_float startindex = x->startindex;
	t_float increment = x->increment;
	t_float floatindex = startindex + ((t_float)counter * increment);
	
	// variables for eventual fade out of previous slice
	t_float fvalue1, fvalue2, ffraction;
	t_int findex = 0;
	t_int fadeoutcounter = x->fadeoutcounter;
	t_int fadeouttimer = x->fadeouttimer;
	t_float fadeoutcompensation = 0., fadeoutpoint = 0.;						
	t_float fadeoutincrement = 0., fadeoutindex = 0.;
	
	if(fadeouttimer)
	{
		fadeoutcompensation = x->fadeoutcompensation;						
		fadeoutpoint = x->fadeoutpoint;						
		fadeoutincrement = x->fadeoutincrement;				
		fadeoutindex = fadeoutpoint + ((t_float)fadeoutcounter * fadeoutincrement);
	}
	
	while (blocksize--)
	{
		*output = 0.;
		
		if(playtimer)
		{
			index = (t_int)floatindex;
			fraction = (t_float)floatindex - (t_float)index;
			index &= loopmask;
			value1 = arraypointer[index].w_float;
			value2 = arraypointer[index+1].w_float;
			*output = (value1 + (fraction * (value2 - value1))) * compensationfactor;
			counter++; playtimer--;
			floatindex = startindex + ((t_float)counter * increment);
		}
			
		if(fadeouttimer)	// eventual previous slice tail
		{
			findex = (t_int)fadeoutindex;
			ffraction = (t_float)fadeoutindex - (t_float)findex;
			findex &= loopmask;
			fvalue1 = arraypointer[findex].w_float;
			fvalue2 = arraypointer[findex+1].w_float;
			*output += (fvalue1 + (ffraction * (fvalue2 - fvalue1))) * fadeoutcompensation * (0.5 - (0.5 * cos_table[fadeouttimer]));
			fadeoutcounter++; fadeouttimer--;
			fadeoutindex = fadeoutpoint + ((t_float)fadeoutcounter * fadeoutincrement);
		}
		output++;
	}
	// end of while(blocksize)
	
	x->actualindex = index & loopmask;						// just in case next block starts with new slice
	x->counter = counter;
	x->playtimer = playtimer;
	x->fadeoutcounter = fadeoutcounter;
	x->fadeouttimer = fadeouttimer;
	if((!playtimer) && (!fadeouttimer)) sliceplay_tick(x, 0);			// send playstatus = 0 message
	
	x->testvalue = (0.5 - (0.5 * cos_table[COSTABSIZE>>2]));
	
	return (w+4);
}
// end of attackpoint_perform function definition


static void sliceplay_set(t_sliceplay *x)
{
	t_garray *array;
	int arraysize, logloopsize;
	
	if(!(array = (t_garray *)pd_findbyclass(x->arrayname, garray_class)))
	{
		if (x->arrayname->s_name) pd_error(x, "sliceplay~: %s: no such array", x->arrayname->s_name);
		x->arraypointer = 0;
	}
	
	else if (!garray_getfloatwords(array, &arraysize, &x->arraypointer))
	{
		pd_error(x, "%s: bad template for sliceplay~", x->arrayname->s_name);
		x->arraypointer = 0;
	}
	
	else
	{
		logloopsize = ilog2(arraysize-1);			// arraysize must be at least loopsize + 1 sample
		x->loopsize = 1<<logloopsize;				// loopsize is rounded to a power of two
		x->loopmask = x->loopsize - 1;			// bitwise-and mask for loopsize
		garray_usedindsp(array);
	}
}


static void sliceplay_dsp(t_sliceplay *x, t_signal **sp)
{
	sliceplay_set(x);	
	dsp_add(sliceplay_perform, 3, sp[0]->s_vec, x, sp[0]->s_n);
}


static inline void sliceplay_compensationfactor(t_sliceplay *x)					// amplitude compensation
{	
	t_float speed = abs(x->speed);
	t_float compensationfactor;
	compensationfactor = x->compensationsetting * (1. - speed) + 1.;
	if(compensationfactor < 0.7) compensationfactor = 0.7;
	x->compensationfactor = compensationfactor;
}


static void sliceplay_compensation(t_sliceplay *x, t_floatarg compensation)
{
	if(compensation > 1.) compensation = 1.;
	else if(compensation < 0.) compensation = 0.;
	x->compensationsetting = compensation;							// user parameter for compensation
}


static void sliceplay_interrupt(t_sliceplay *x, t_floatarg interrupt)
{
	if((interrupt != -1) && (interrupt != 1)) interrupt = 0;
	x->interrupt = interrupt;
}
	

static void sliceplay_cuepoints(t_sliceplay *x, t_floatarg startindex, t_floatarg stopindex)
{
	if((startindex < 0.) || (stopindex < 0.)) return;							// negative cuepoints are ignored
	
	t_int samples, start, stop;
	t_int accept = 0;												
	t_int interrupt = x->interrupt;										// x->interrupt can be -1, 0 or 1
	
	if(interrupt == 1) accept = 1;										// if interrupt = 1, always interrupt
	else if(!x->playtimer) accept = 1;									// if not playing, always accept new cuepoints
	else if((interrupt == -1) && (x->increment < 0)) accept = 1;				// if interrupt = -1, only interrupt reversed playback
	
	if(accept)														// accept new cuepoints conditionally
	{
		if(x->playtimer)												// if interrupted, fade out previous slice
		{
			if(x->playtimer < COSTABSIZE>>1)
				x->fadeouttimer = x->playtimer;
			else x->fadeouttimer = COSTABSIZE>>1;
			x->fadeoutcounter = 0;
			x->fadeoutpoint = x->actualindex + x->loopsize;				// hand over previous settings
			x->fadeoutincrement = x->increment;
			x->fadeoutcompensation = x->compensationfactor;
		}
		
		if(x->speed > 0) x->startindex = (t_float)startindex + x->loopsize;		// forward 
		if(x->speed < 0) x->startindex = (t_float)stopindex + x->loopsize - 1.;	// reverse
		x->counter = 0;
		start = (t_int)startindex;
		stop = (t_int)stopindex;
		samples = ((stop - start + x->loopsize) & (x->loopmask)) - 1;
		x->playtimer = (t_int)(samples / x->speed);						// compute playback time in number of samples
		x->playtimer = abs(x->playtimer);
		x->increment = x->speed;
		sliceplay_compensationfactor(x);
		sliceplay_tick(x, abs(x->playtimer));								// report slice length in nr of samples
	}
}

static void sliceplay_test(t_sliceplay *x)
{
	post("x->testvalue %f", x->testvalue);
}


static void sliceplay_speed(t_sliceplay *x, t_floatarg speed)					// playback speed
{
	t_float minspeed = x->minspeed;
	
	if(speed >= 0)													// exclude very slow speeds
	{
		if(speed < minspeed) x->speed = minspeed;
		else x->speed = (t_float)speed;
	}
	else
	{
		if(speed > -minspeed) x->speed = -minspeed;
		else x->speed = (t_float)speed;
	}
}


static void sliceplay_minspeed(t_sliceplay *x, t_floatarg minspeed)			// playback playback speed
{
	x->minspeed = abs(minspeed);
}


void sliceplay_tilde_setup(void)
{
	sliceplay_class = class_new(gensym("sliceplay~"), (t_newmethod)sliceplay_new, 
		(t_method)sliceplay_free, sizeof(t_sliceplay), 0, A_DEFSYM, 0);
	class_addfloat(sliceplay_class, (t_method)sliceplay_speed);
	class_addmethod(sliceplay_class, (t_method)sliceplay_dsp, gensym("dsp"), 0);
	class_addmethod(sliceplay_class, (t_method)sliceplay_cuepoints, gensym("cuepoints"), A_FLOAT, A_FLOAT, 0);
	class_addmethod(sliceplay_class, (t_method)sliceplay_compensation, gensym("compensation"), A_FLOAT, 0);
	class_addmethod(sliceplay_class, (t_method)sliceplay_interrupt, gensym("interrupt"), A_FLOAT, 0);
	class_addmethod(sliceplay_class, (t_method)sliceplay_minspeed, gensym("minspeed"), A_FLOAT, 0);
	class_addmethod(sliceplay_class, (t_method)sliceplay_test, gensym("test"), 0);
	class_sethelpsymbol(sliceplay_class, gensym("slicerec~-help.pd"));
	post("sliceplay~ version 0.9.12 written by Katja Vetter");
}


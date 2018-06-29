# speechwave - custom speechrecognizer

This project demonstrates the customisation of speech recognizer in android. We can add our own UI and equalizer animation based on voice amplitude.

![alt text](https://github.com/arjun-droid/speechwave/blob/master/screencapture.png)

We can change the interpolator as needed during aniamtion,

        waveAnimator.setInterpolator(new LinearInterpolator());
        
List of interpolators as per docs,
AccelerateDecelerateInterpolator,
AccelerateInterpolator,
AnticipateInterpolator,
AnticipateOvershootInterpolator,
BaseInterpolator,
BounceInterpolator,
CycleInterpolator,
DecelerateInterpolator,
LinearInterpolator,
OvershootInterpolator,
PathInterpolator.

Please don't forget to destroy the recognizer object onDestroy(),

        @Override
        public void onDestroy() {
        super.onDestroy();
        if (recognizer != null) {
            recognizer.stopListening();
            recognizer.destroy();
         }
        }

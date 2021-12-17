# WaveLoading
  WaveLoading: This library provides a wave loading animation as a PixelMapElement.
 
## WaveLoading includes:
*this library solution can help developers display animation in waveloading with different size and types of wave speed .

## Usage Instructions
1. A sample project which provides runnable code examples that demonstrate uses of the classes in this project is available in the sample/ folder.

2. The following core classes are the essential interface to waveloading:
WaveLoading : this class basically we are creating the custom PixelMapElement layout for waveloading animation.

3. The steps to initialize the waveloading and the core classes:

   WaveDrawable  mWaveDrawable = new WaveDrawable(otherDrawable);
 
   // Use as common drawable
   imageView.setImageDrawable(mWaveDrawable);
  
  Other configurable APIs:
  
  public void setWaveAmplitude(int amplitude), set wave amplitude (in pixels)
  
  public void setWaveLength(int length), set wave length (in pixels)
  
  public void setWaveSpeed(int step), set wave move speed (in pixels)
  
  public void setIndeterminate(boolean indeterminate), like progress bar, 
  if run in indeterminate mode, it'll increase water level over and over again, otherwise, you can use boolean setLevel(int level) to set the water level, acting as loading progress.
 
  public void setIndeterminateAnimator(AnimatorValue animator), set you customised animator for wave loading animation in indeterminate mode.

 ## Installation instruction

1.For using  module in sample app,include the below library dependency to generate hap/library.har:
Add the dependencies in entry/build.gradle as below :

      dependencies {
          implementation project(path: ':library')
        }

2. Using the waveLoading har, make sure to add library.har file in the entry/libs folder and add the below dependency
in build.gradle.
Modify the dependencies in the entry/build.gradle file.

    dependencies {
        implementation fileTree(dir: 'libs', include: ['*.jar', '*.har'])
  }
 
 ## License
 
  MIT




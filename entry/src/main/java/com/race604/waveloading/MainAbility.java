package com.race604.waveloading;

import com.race604.drawable.wave.WaveDrawable;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;
//import ohos.agp.animation.Animator;
//import ohos.agp.animation.AnimatorValue;
import ohos.agp.colors.RgbColor;
import ohos.agp.components.Component;
import ohos.agp.components.Image;
import ohos.agp.components.RadioContainer;
import ohos.agp.components.Slider;
import ohos.agp.components.element.ShapeElement;

//import static ohos.agp.animation.Animator.CurveType.BOUNCE;

/**
 * MainAbility class
 */
public class MainAbility extends Ability {
    private Image mImageView;
    private WaveDrawable mWaveDrawable;
    private Slider mLevelSlider;
    private Slider mAmplitudeSlider;
    private Slider mSpeedSlider;
    private Slider mLengthSlider;
    private RadioContainer mRadioGroup;

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_main);

        mImageView = (Image) findComponentById(ResourceTable.Id_image);
        mWaveDrawable = new WaveDrawable(getContext(), ResourceTable.Media_android_robot).attachComponent(mImageView);
        mImageView.setImageElement(mWaveDrawable);

        mLevelSlider = (Slider) findComponentById(ResourceTable.Id_level_seek);
        mLevelSlider.setValueChangedListener(new SimpleOnSliderChangeListener() {
            @Override
            public void onProgressUpdated(Slider slider, int i, boolean b) {
                mWaveDrawable.setLevel(i);
            }
        });

        mAmplitudeSlider = (Slider) findComponentById(ResourceTable.Id_amplitude_seek);
        mAmplitudeSlider.setValueChangedListener(new SimpleOnSliderChangeListener() {
            @Override
            public void onProgressUpdated(Slider slider, int i, boolean b) {
                mWaveDrawable.setWaveAmplitude(i);
            }
        });

        mLengthSlider = (Slider) findComponentById(ResourceTable.Id_length_seek);
        mLengthSlider.setValueChangedListener(new SimpleOnSliderChangeListener() {
            @Override
            public void onProgressUpdated(Slider slider, int i, boolean b) {
                mWaveDrawable.setWaveLength(i);
            }
        });

        mSpeedSlider = (Slider) findComponentById(ResourceTable.Id_speed_seek);
        mSpeedSlider.setValueChangedListener(new SimpleOnSliderChangeListener() {
            @Override
            public void onProgressUpdated(Slider slider, int i, boolean b) {
                mWaveDrawable.setWaveSpeed(i);
            }
        });

        mRadioGroup = (RadioContainer) findComponentById(ResourceTable.Id_modes);
        mRadioGroup.setMarkChangedListener((radioContainer, i) -> setIndeterminateMode(i == 0));

        mRadioGroup.mark(1);
        setIndeterminateMode(mRadioGroup.getMarkedButtonId() == ResourceTable.Id_rb_yes);

        Image imageView2 = (Image) findComponentById(ResourceTable.Id_image2);
        WaveDrawable chromeWave = new WaveDrawable(getContext(), ResourceTable.Media_chrome_logo).attachComponent(imageView2);
        chromeWave.setIndeterminate(true);
        imageView2.setImageElement(chromeWave);

        // Set customised animator here
//        AnimatorValue animator = new AnimatorValue();
//        animator.setLoopedCount(Animator.INFINITE);
//        animator.setDuration(4000);
//        animator.setCurveType(BOUNCE);
//        chromeWave.setIndeterminateAnimator(animator);
//        chromeWave.setIndeterminate(true);

        Component component = findComponentById(ResourceTable.Id_component);
        int color = getColor(ResourceTable.Color_colorAccent);
        ShapeElement shapeElement = new ShapeElement();
        shapeElement.setRgbColor(RgbColor.fromRgbaInt(color));
        shapeElement.setBounds(0, 0, 100, 100);
        WaveDrawable colorWave = new WaveDrawable(shapeElement).attachComponent(component);
        component.setBackground(colorWave);
        colorWave.setIndeterminate(true);
    }

    private void setIndeterminateMode(boolean indeterminate) {
        mWaveDrawable.setIndeterminate(indeterminate);
        mLevelSlider.setEnabled(!indeterminate);

        if (!indeterminate) {
            mWaveDrawable.setLevel(mLevelSlider.getProgress());
        }
        mWaveDrawable.setWaveAmplitude(mAmplitudeSlider.getProgress());
        mWaveDrawable.setWaveLength(mLengthSlider.getProgress());
        mWaveDrawable.setWaveSpeed(mSpeedSlider.getProgress());
    }

    private static class SimpleOnSliderChangeListener implements Slider.ValueChangedListener{
        @Override
        public void onProgressUpdated(Slider slider, int i, boolean b) {

        }

        @Override
        public void onTouchStart(Slider slider) {

        }

        @Override
        public void onTouchEnd(Slider slider) {

        }
    }
}

package com.race604.waveloading;

import com.race604.drawable.wave.WaveDrawable;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;
import ohos.agp.colors.RgbColor;
import ohos.agp.components.Component;
import ohos.agp.components.Image;
import ohos.agp.components.RadioContainer;
import ohos.agp.components.Slider;
import ohos.agp.components.element.ShapeElement;

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
        mWaveDrawable = new WaveDrawable(getContext(), ResourceTable.Media_android_robot, mImageView);
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
        mRadioGroup.setMarkChangedListener((radioContainer, i) -> {
            int id = radioContainer.getComponentAt(i).getId();
            switch (id) {
                case ResourceTable.Id_rb_yes:
                    setIndeterminateMode(true);
                    break;
                case ResourceTable.Id_rb_no:
                    setIndeterminateMode(false);
            }
        });

        mRadioGroup.mark(1);

        Image imageView = (Image) findComponentById(ResourceTable.Id_image2);
        WaveDrawable chromeWave = new WaveDrawable(this, ResourceTable.Media_chrome_logo, imageView);
        imageView.setImageElement(chromeWave);
        chromeWave.setComponent(imageView);
        chromeWave.setIndeterminate(true);
        Component component = findComponentById(ResourceTable.Id_view);
        int color = getColor(ResourceTable.Color_colorPrimaryDark);
        ShapeElement shapeElement = new ShapeElement();
        shapeElement.setRgbColor(RgbColor.fromRgbaInt(color));
        shapeElement.setBounds(0, 0, 100, 100);
        WaveDrawable colorWave = new WaveDrawable(shapeElement, component);
        component.setBackground(shapeElement);
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

    private static class SimpleOnSliderChangeListener implements Slider.ValueChangedListener {
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

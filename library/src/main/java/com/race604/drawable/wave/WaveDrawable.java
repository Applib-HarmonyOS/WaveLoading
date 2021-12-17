package com.race604.drawable.wave;

import ohos.agp.animation.AnimatorValue;
import ohos.agp.components.Component;
import ohos.agp.components.Image;
import ohos.agp.components.element.Element;
import ohos.agp.components.element.PixelMapElement;
import ohos.agp.render.*;
import ohos.agp.utils.Color;
import ohos.agp.utils.Matrix;
import ohos.agp.utils.Rect;
import ohos.agp.utils.RectFloat;
import ohos.app.Context;
import ohos.media.image.PixelMap;
import ohos.media.image.common.PixelFormat;

import static com.race604.drawable.wave.utils.ResUtil.createByResourceId;
import static ohos.agp.animation.Animator.CurveType.DECELERATE;
import static ohos.agp.animation.Animator.CurveType.LINEAR;

/**
 * Created by jing on 16-12-6.
 */
public class WaveDrawable extends PixelMapElement implements Animatable, AnimatorValue.ValueUpdateListener, Element.OnChangeListener, Component.DrawTask {

    private static final String TAG = WaveDrawable.class.getSimpleName();
    private static final float WAVE_HEIGHT_FACTOR = 0.2f;
    private static final float WAVE_SPEED_FACTOR = 0.02f;
    private static final int UNDEFINED_VALUE = Integer.MIN_VALUE;
    private final Element mDrawable;
    private int mWidth, mHeight;
    private int mWaveHeight = UNDEFINED_VALUE;
    private int mWaveLength = UNDEFINED_VALUE;
    private int mWaveStep = UNDEFINED_VALUE;
    private int mWaveOffset = 0;
    private int mWaveLevel = 10;
    private AnimatorValue mAnimator = null;
    private AnimatorValue mAnimatorInfinite = null;
    private float mProgress = 0.3f;
    private Paint mPaint;
    private PixelMap mMask;
    private int mLevel = 0;
    private final Matrix mMatrix = new Matrix();
    private boolean mRunning = false;
    private boolean mIndeterminate = false;
    private Component mComponent;

    private static final ColorMatrix sGrayFilter = new ColorMatrix(new float[]{
            0.264F, 0.472F, 0.088F, 0, 0,
            0.264F, 0.472F, 0.088F, 0, 0,
            0.264F, 0.472F, 0.088F, 0, 0,
            0, 0, 0, 1, 0
    });
    private ColorMatrix mCurFilter = null;

    public WaveDrawable(Element drawable, Component component) {
        super(getPixelMap(drawable));
        mDrawable = drawable;
        setComponent(component);
        init();
    }

    private static PixelMap getPixelMap(Element drawableelement) {
        PixelMap.InitializationOptions pi = new PixelMap.InitializationOptions();
        pi.pixelFormat = PixelFormat.ARGB_8888;
        pi.size = new ohos.media.image.common.Size(drawableelement.getWidth(), drawableelement.getHeight());
        return PixelMap.create(pi);
    }

    public WaveDrawable(Context context, int imgRes , Component component) {
        super(createByResourceId(context, imgRes));
        mDrawable = new PixelMapElement(createByResourceId(context, imgRes));
        setComponent(component);
        init();
    }

    private void init() {
        mMatrix.reset();
        mPaint = new Paint();
        mPaint.setFilterBitmap(false);
        mPaint.setColor(Color.BLACK);
        mPaint.setBlendMode(BlendMode.DST_IN);

        mWidth = mDrawable.getWidth();
        mHeight = mDrawable.getHeight();

        if (mWidth > 0 && mHeight > 0) {
            mWaveLength = mWidth;
            mWaveHeight = Math.max(8, (int) (mHeight * WAVE_HEIGHT_FACTOR));
            mWaveStep = Math.max(1, (int) (mWidth * WAVE_SPEED_FACTOR));
            updateMask(mWidth, mWaveLength, mWaveHeight);
        }

        setProgress(0);
        start();
    }

    /**
     * Set wave move distance (in pixels) in very animation frame
     *
     * @param step distance in pixels
     */
    public void setWaveSpeed(int step) {
        mWaveStep = Math.min(step, mWidth / 2);
    }

    /**
     * Set wave amplitude (in pixels)
     *
     * @param amplitude
     */
    public void setWaveAmplitude(int amplitude) {
        amplitude = Math.max(1, Math.min(amplitude, mHeight / 2));
        int height = amplitude * 2;
        if (mWaveHeight != height) {
            mWaveHeight = height;
            updateMask(mWidth, mWaveLength, mWaveHeight);
            invalidateSelf();
        }
    }

    /**
     * Set wave length (in pixels)
     *
     * @param length
     */
    public void setWaveLength(int length) {
        length = Math.max(8, Math.min(mWidth * 2, length));
        if (length != mWaveLength) {
            mWaveLength = length;
            updateMask(mWidth, mWaveLength, mWaveHeight);
            invalidateSelf();
        }
    }

    /**
     * Set the wave loading in indeterminate mode or not
     *
     * @param indeterminate
     */
    public void setIndeterminate(boolean indeterminate) {
        mIndeterminate = indeterminate;
        if (mIndeterminate) {
            if (mAnimator == null) {
                mAnimator = getDefaultAnimator();
            }
            mAnimator.setValueUpdateListener(this);
            mAnimator.start();
        } else {
            if (mAnimator != null) {
                mAnimator.setValueUpdateListener(null);
                mAnimator.cancel();
            }
            setLevel(calculateLevel());
        }
    }

    public void setLevel(int level) {
        if (mLevel != level) {
            mLevel = level;
            onLevelChange(mLevel);
        }
    }

    /**
     * Set customised animator for wave loading animation
     *
     * @param animator
     */
    public void setIndeterminateAnimator(AnimatorValue animator) {
        if (mAnimator == animator) {
            return;
        }

        if (mAnimator != null) {
            mAnimator.setValueUpdateListener(null);
            mAnimator.cancel();
        }

        mAnimator = animator;
        if (mAnimator != null) {
            mAnimator.setValueUpdateListener(this);
        }
    }

    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left, top, right, bottom);
        mDrawable.setBounds(left, top, right, bottom);
    }

    private void updateBounds(Rect bounds) {
        if (bounds.getWidth() <= 0 || bounds.getHeight() <= 0) {
            return;
        }

        if (mWidth < 0 || mHeight < 0) {
            mWidth = bounds.getWidth();
            mHeight = bounds.getHeight();
            if (mWaveHeight == UNDEFINED_VALUE) {
                mWaveHeight = Math.max(8, (int) (mHeight * WAVE_HEIGHT_FACTOR));
            }

            if (mWaveLength == UNDEFINED_VALUE) {
                mWaveLength = mWidth;
            }

            if (mWaveStep == UNDEFINED_VALUE) {
                mWaveStep = Math.max(1, (int) (mWidth * WAVE_SPEED_FACTOR));
            }

            updateMask(mWidth, mWaveLength, mWaveHeight);
        }
    }

    @Override
    public void drawToCanvas(Canvas canvas) {
        LogUtil.debug("drawtocanvas canvas", "start");
        mDrawable.setColorMatrix(sGrayFilter);
        mDrawable.drawToCanvas(canvas);
        mDrawable.setColorMatrix(null);

        if (mProgress <= 0.001f) {
            return;
        }

        int sc = canvas.saveLayer(new RectFloat(0, 0, mWidth, mHeight), new Paint());//,

        if (mWaveLevel > 0) {
            canvas.clipRect(0, mWaveLevel, mWidth, mHeight);
        }

        mDrawable.drawToCanvas(canvas);


        mWaveOffset += mWaveStep;
        if (mWaveOffset > mWaveLength) {
            mWaveOffset -= mWaveLength;
        }

        if (mMask != null) {
            Matrix matrix = new Matrix();//
            matrix.translate(-mWaveOffset, mWaveLevel);

            canvas.concat(matrix);
            canvas.drawPixelMapHolder(new PixelMapHolder(mMask), 0, 0, mPaint);
        }

        canvas.restoreToCount(sc);
    }

    void onLevelChange(int level) {
        setProgress(level / 10000f);
    }

    @Override
    public void setAlpha(int i) {
        mDrawable.setAlpha(i);
    }

    @Override
    public void start() {
        mRunning = true;
    }

    @Override
    public void stop() {
        mRunning = false;
    }

    @Override
    public boolean isRunning() {
        return mRunning;
    }

    public boolean isIndeterminate() {
        return mIndeterminate;
    }

    private void getInfiniteAnimator() {
        mAnimatorInfinite = new AnimatorValue();
        mAnimatorInfinite.setLoopedCount(AnimatorValue.INFINITE);
        mAnimatorInfinite.setDuration(5000);
        mAnimatorInfinite.setCurveType(LINEAR);
        mAnimatorInfinite.setValueUpdateListener(new AnimatorValue.ValueUpdateListener() {
            @Override
            public void onUpdate(AnimatorValue animatorValue, float v) {
                if (mRunning)
                    invalidateSelf();
            }
        });
        mAnimatorInfinite.start();
    }

    private AnimatorValue getDefaultAnimator() {
        AnimatorValue animator = new AnimatorValue();//.ofFloat(0, 1);
        animator.setCurveType(DECELERATE);
        animator.setLoopedCount(AnimatorValue.INFINITE);
        animator.setDuration(5000);
        return animator;
    }

    private void setProgress(float progress) {
        mProgress = progress;
        mWaveLevel = mHeight - (int) ((mHeight + mWaveHeight) * mProgress);
        invalidateSelf();
    }

    private int calculateLevel() {
        return (mHeight - mWaveLevel) * 10000 / (mHeight + mWaveHeight);
    }

    private void updateMask(int width, int length, int height) {
        if (width <= 0 || length <= 0 || height <= 0) {
            LogUtil.info(TAG, "updateMask: size must > 0");
            mMask = null;
            return;
        }

        final int count = (int) Math.ceil((width + length) / (float) length);

        PixelMap.InitializationOptions pi = new PixelMap.InitializationOptions();
        pi.pixelFormat = PixelFormat.ARGB_8888;
        pi.size = new ohos.media.image.common.Size(length * count, height);
        PixelMap bm = PixelMap.create(pi);
        Texture texture = new Texture(bm);
        Canvas c = new Canvas(texture);
        Paint p = new Paint();
        p.setAntiAlias(true);

        int amplitude = height / 2;
        Path path = new Path();
        path.moveTo(0, amplitude);

        final float stepX = length / 4f;
        float x = 0;
        float y = -amplitude;
        for (int i = 0; i < count * 2; i++) {
            x += stepX;
            path.quadTo(x, y, x + stepX, amplitude);
            x += stepX;
            y = texture.getHeight() - y;
        }
        path.lineTo(texture.getWidth(), height);
        path.lineTo(0, height);
        path.close();

        c.drawPath(path, p);
        mMask = texture.getPixelMap();
    }

    @Override
    public void onUpdate(AnimatorValue animatorValue, float v) {
        if (mIndeterminate) {
            setProgress(v);
            if (!mRunning) {
                invalidateSelf();
            }
        }
    }

    @Override
    public void onChange(Element element) {
        updateBounds(element.getBounds());
    }

    @Override
    public void onDraw(Component component, Canvas canvas) {
        drawToCanvas(canvas);
    }

    private void invalidateSelf() {
        if (mComponent != null)
            mComponent.invalidate();
    }

    public void setComponent(Component component) {
        component.addDrawTask(this);
        mComponent = component;
        if(! (component instanceof Image)){
            mDrawable.setBounds(0, 0, component.getWidth(), component.getHeight());
        }
        getInfiniteAnimator();
    }
}

package uz.xia.ivat.uzbpersiandictionary.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import uz.xia.ivat.uzbpersiandictionary.R;

public class CheckedFrameLayout extends FrameLayout implements Checkable {
    private final int[] checkedStateSet = new int[]{android.R.attr.state_checked};
    private boolean mIsChecked = false;

    public CheckedFrameLayout(@NonNull Context context) {
        this(context, null);
    }

    public CheckedFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CheckedFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    public CheckedFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CheckedFrameLayout, defStyleAttr, 0);
        mIsChecked = a.getBoolean(R.styleable.CheckedFrameLayout_android_checked, true);
        a.recycle();
    }

    @Override
    public boolean isChecked() {
        return mIsChecked;
    }

    @Override
    public void setChecked(boolean checked) {
        mIsChecked = checked;

        refreshDrawableState();
    }

    @Override
    public void toggle() {
        mIsChecked = !mIsChecked;
    }

    @Override
    protected int[] onCreateDrawableState(int extraSpace) {
        int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (isChecked())
            mergeDrawableStates(drawableState, checkedStateSet);
        return drawableState;
    }
}

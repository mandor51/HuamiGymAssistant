package com.mandor51.huamigymassistant;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.wearable.view.BoxInsetLayout;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.WindowInsets;

public class HuamiBoxInsetLayout extends BoxInsetLayout {

    private static float FACTOR = 0.146467f; //(1 - sqrt(2)/2)/2
    private static final int DEFAULT_CHILD_GRAVITY = Gravity.TOP | Gravity.START;
    private Rect mForegroundPadding;
    private boolean mLastKnownRound;
    private Rect mInsets;

    public HuamiBoxInsetLayout(Context context) {
        this(context, null);
    }

    public HuamiBoxInsetLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HuamiBoxInsetLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if (mForegroundPadding == null) {
            mForegroundPadding = new Rect();
        }
        if (mInsets == null) {
            mInsets = new Rect();
        }
    }

    @Override
    public WindowInsets onApplyWindowInsets(WindowInsets insets) {
        insets = super.onApplyWindowInsets(insets);
        final boolean round = true;
        if (round != mLastKnownRound) {
            mLastKnownRound = round;
            requestLayout();
        }
        mInsets.set(
                insets.getSystemWindowInsetLeft(),
                insets.getSystemWindowInsetTop(),
                insets.getSystemWindowInsetRight(),
                insets.getSystemWindowInsetBottom());
        return insets;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int count = getChildCount();
        // find max size
        int maxWidth = 0;
        int maxHeight = 0;
        int childState = 0;
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                BoxInsetLayout.LayoutParams lp = (BoxInsetLayout.LayoutParams) child.getLayoutParams();
                int marginLeft = 0;
                int marginRight = 0;
                int marginTop = 0;
                int marginBottom = 0;
                if (mLastKnownRound) {
                    // round screen, check boxed, don't use margins on boxed
                    if ((lp.boxedEdges & BoxInsetLayout.LayoutParams.BOX_LEFT) == 0) {
                        marginLeft = lp.leftMargin;
                    }
                    if ((lp.boxedEdges & BoxInsetLayout.LayoutParams.BOX_RIGHT) == 0) {
                        marginRight = lp.rightMargin;
                    }
                    if ((lp.boxedEdges & BoxInsetLayout.LayoutParams.BOX_TOP) == 0) {
                        marginTop = lp.topMargin;
                    }
                    if ((lp.boxedEdges & BoxInsetLayout.LayoutParams.BOX_BOTTOM) == 0) {
                        marginBottom = lp.bottomMargin;
                    }
                } else {
                    // rectangular, ignore boxed, use margins
                    marginLeft = lp.leftMargin;
                    marginTop = lp.topMargin;
                    marginRight = lp.rightMargin;
                    marginBottom = lp.bottomMargin;
                }
                measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
                maxWidth = Math.max(maxWidth,
                        child.getMeasuredWidth() + marginLeft + marginRight);
                maxHeight = Math.max(maxHeight,
                        child.getMeasuredHeight() + marginTop + marginBottom);
                childState = combineMeasuredStates(childState, child.getMeasuredState());
            }
        }
        // Account for padding too
        maxWidth += getPaddingLeft() + mForegroundPadding.left
                + getPaddingRight() + mForegroundPadding.right;
        maxHeight += getPaddingTop() + mForegroundPadding.top
                + getPaddingBottom() + mForegroundPadding.bottom;
        // Check against our minimum height and width
        maxHeight = Math.max(maxHeight, getSuggestedMinimumHeight());
        maxWidth = Math.max(maxWidth, getSuggestedMinimumWidth());
        // Check against our foreground's minimum height and width
        final Drawable drawable = getForeground();
        if (drawable != null) {
            maxHeight = Math.max(maxHeight, drawable.getMinimumHeight());
            maxWidth = Math.max(maxWidth, drawable.getMinimumWidth());
        }
        setMeasuredDimension(resolveSizeAndState(maxWidth, widthMeasureSpec, childState),
                resolveSizeAndState(maxHeight, heightMeasureSpec,
                        childState << MEASURED_HEIGHT_STATE_SHIFT));
        // determine boxed inset
        int boxInset = (int) (FACTOR * Math.max(getMeasuredWidth(), getMeasuredHeight()));
        // adjust the match parent children
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            final BoxInsetLayout.LayoutParams lp = (BoxInsetLayout.LayoutParams) child.getLayoutParams();
            int childWidthMeasureSpec;
            int childHeightMeasureSpec;
            int plwf = getPaddingLeft() + mForegroundPadding.left;
            int prwf = getPaddingRight() + mForegroundPadding.right;
            int ptwf = getPaddingTop() + mForegroundPadding.top;
            int pbwf = getPaddingBottom() + mForegroundPadding.bottom;
            // adjust width
            int totalPadding = 0;
            int totalMargin = 0;
            // BoxInset is a padding. Ignore margin when we want to do BoxInset.
            if (mLastKnownRound && ((lp.boxedEdges & BoxInsetLayout.LayoutParams.BOX_LEFT) != 0)) {
                totalPadding = boxInset;
            } else {
                totalMargin = plwf + lp.leftMargin;
            }
            if (mLastKnownRound && ((lp.boxedEdges & BoxInsetLayout.LayoutParams.BOX_RIGHT) != 0)) {
                totalPadding += boxInset;
            } else {
                totalMargin += prwf + lp.rightMargin;
            }
            if (lp.width == BoxInsetLayout.LayoutParams.MATCH_PARENT) {
                //  Only subtract margin from the actual width, leave the padding in.
                childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(
                        getMeasuredWidth() - totalMargin, MeasureSpec.EXACTLY);
            } else {
                childWidthMeasureSpec = getChildMeasureSpec(widthMeasureSpec,
                        totalPadding + totalMargin, lp.width);
            }
            // adjust height
            if (mLastKnownRound && ((lp.boxedEdges & BoxInsetLayout.LayoutParams.BOX_TOP) != 0)) {
                totalPadding = boxInset;
            } else {
                totalMargin = ptwf + lp.topMargin;
            }
            if (mLastKnownRound && ((lp.boxedEdges & BoxInsetLayout.LayoutParams.BOX_BOTTOM) != 0)) {
                totalPadding += boxInset;
            } else {
                totalMargin += pbwf + lp.bottomMargin;
            }
            if (lp.height == BoxInsetLayout.LayoutParams.MATCH_PARENT) {
                childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(
                        getMeasuredHeight() - totalMargin, MeasureSpec.EXACTLY);
            } else {
                childHeightMeasureSpec = getChildMeasureSpec(heightMeasureSpec,
                        totalPadding + totalMargin, lp.height);
            }
            child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        layoutBoxChildren(left, top, right, bottom, false /* no force left gravity */);
    }

    private void layoutBoxChildren(int left, int top, int right, int bottom,
                                   boolean forceLeftGravity) {
        final int count = getChildCount();
        int boxInset = (int) (FACTOR * Math.max(right - left, bottom - top));
        final int parentLeft = getPaddingLeft() + mForegroundPadding.left;
        final int parentRight = right - left - getPaddingRight() - mForegroundPadding.right;
        final int parentTop = getPaddingTop() + mForegroundPadding.top;
        final int parentBottom = bottom - top - getPaddingBottom() - mForegroundPadding.bottom;
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                final BoxInsetLayout.LayoutParams lp = (BoxInsetLayout.LayoutParams) child.getLayoutParams();
                final int width = child.getMeasuredWidth();
                final int height = child.getMeasuredHeight();
                int childLeft;
                int childTop;
                int gravity = lp.gravity;
                if (gravity == -1) {
                    gravity = DEFAULT_CHILD_GRAVITY;
                }
                final int layoutDirection = getLayoutDirection();
                final int absoluteGravity = Gravity.getAbsoluteGravity(gravity, layoutDirection);
                final int verticalGravity = gravity & Gravity.VERTICAL_GRAVITY_MASK;
                // These values are replaced with boxInset below as necessary.
                int paddingLeft = child.getPaddingLeft();
                int paddingRight = child.getPaddingRight();
                int paddingTop = child.getPaddingTop();
                int paddingBottom = child.getPaddingBottom();
                switch (absoluteGravity & Gravity.HORIZONTAL_GRAVITY_MASK) {
                    case Gravity.CENTER_HORIZONTAL:
                        childLeft = parentLeft + (parentRight - parentLeft - width) / 2 +
                                lp.leftMargin - lp.rightMargin;
                        break;
                    case Gravity.RIGHT:
                        if (!forceLeftGravity) {
                            if (mLastKnownRound
                                    && ((lp.boxedEdges & BoxInsetLayout.LayoutParams.BOX_RIGHT) != 0)) {
                                paddingRight = boxInset;
                                childLeft = right - left - width;
                            } else {
                                childLeft = parentRight - width - lp.rightMargin;
                            }
                            break;
                        }
                    case Gravity.LEFT:
                    default:
                        if (mLastKnownRound && ((lp.boxedEdges & BoxInsetLayout.LayoutParams.BOX_LEFT) != 0)) {
                            paddingLeft = boxInset;
                            childLeft = 0;
                        } else {
                            childLeft = parentLeft + lp.leftMargin;
                        }
                }
                switch (verticalGravity) {
                    case Gravity.TOP:
                        if (mLastKnownRound && ((lp.boxedEdges & BoxInsetLayout.LayoutParams.BOX_TOP) != 0)) {
                            paddingTop = boxInset;
                            childTop = 0;
                        } else {
                            childTop = parentTop + lp.topMargin;
                        }
                        break;
                    case Gravity.CENTER_VERTICAL:
                        childTop = parentTop + (parentBottom - parentTop - height) / 2 +
                                lp.topMargin - lp.bottomMargin;
                        break;
                    case Gravity.BOTTOM:
                        if (mLastKnownRound && ((lp.boxedEdges & BoxInsetLayout.LayoutParams.BOX_BOTTOM) != 0)) {
                            paddingBottom = boxInset;
                            childTop = bottom - top - height;
                        } else {
                            childTop = parentBottom - height - lp.bottomMargin;
                        }
                        break;
                    default:
                        childTop = parentTop + lp.topMargin;
                }
                child.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
                child.layout(childLeft, childTop, childLeft + width, childTop + height);
            }
        }
    }
}

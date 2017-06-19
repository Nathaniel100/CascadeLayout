package io.ginger.cascadelayout

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT

/**
 * Created by wufan on 2017/6/19.
 *
 * A simple custom ViewGroup that is implemented by kotlin
 *
 * 1. define attr in attrs.xml
 * 2. create custom LayoutParams, then override methods like checkLayoutParams, generateDefaultLayoutParams, generateLayoutParams
 * 3. override onMeasure, measure child, set layout params, setMeasureDimension
 * 4. override onLayout, read layout params, layout child
 *
 */
class CascadeLayout(context: Context, attrs: AttributeSet?, defStyle: Int)
  : ViewGroup(context, attrs, defStyle) {

  val horizontalSpacing: Int
  val verticalSpacing: Int

  constructor(context: Context) : this(context, null, 0)

  constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

  init {
    val a = context.obtainStyledAttributes(attrs, R.styleable.CascadeLayout)
    horizontalSpacing = a.getDimensionPixelSize(R.styleable.CascadeLayout_horizontal_spacing,
        context.resources.getDimensionPixelSize(R.dimen.default_horizontal_spacing))
    verticalSpacing = a.getDimensionPixelSize(R.styleable.CascadeLayout_vertical_spacing,
        context.resources.getDimensionPixelSize(R.dimen.default_vertical_spacing))

    a.recycle()
  }

  override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
    for (i in 0..childCount - 1) {
      val child = getChildAt(i)
      if (child.visibility == GONE) continue
      val lp = child.layoutParams as CascadeLayout.LayoutParams

      child.layout(lp.x, lp.y, lp.x + child.measuredWidth, lp.y + child.measuredHeight)
    }
  }

  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    if (childCount == 0) {
      super.onMeasure(widthMeasureSpec, heightMeasureSpec)
      return
    }
    var width: Int = 0
    var height: Int = paddingTop
    for (i in 0..childCount - 1) {
      var verticalSpacing = this.verticalSpacing
      val child = getChildAt(i)
      if (child.visibility == GONE) continue
      measureChild(child, widthMeasureSpec, heightMeasureSpec)

      val lp = child.layoutParams as LayoutParams
      if (lp.verticalSpacing > 0) {
        verticalSpacing = lp.verticalSpacing
      }
      width = paddingLeft + horizontalSpacing * i

      lp.x = width
      lp.y = height

      width += child.measuredWidth
      height += verticalSpacing
    }
    width += paddingRight
    height += getChildAt(childCount - 1).measuredHeight + paddingBottom

    setMeasuredDimension(View.resolveSize(width, widthMeasureSpec),
        View.resolveSize(height, heightMeasureSpec))
  }

  override fun checkLayoutParams(p: ViewGroup.LayoutParams?): Boolean {
    return p is CascadeLayout.LayoutParams
  }

  override fun generateDefaultLayoutParams(): ViewGroup.LayoutParams {
    return CascadeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
  }

  override fun generateLayoutParams(attrs: AttributeSet?): ViewGroup.LayoutParams {
    return CascadeLayout.LayoutParams(context, attrs)
  }

  override fun generateLayoutParams(
      p: ViewGroup.LayoutParams?): ViewGroup.LayoutParams {
    return CascadeLayout.LayoutParams(p)
  }

  class LayoutParams : ViewGroup.LayoutParams {
    var x: Int = 0
    var y: Int = 0
    var verticalSpacing: Int = 0

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
      val a = context.obtainStyledAttributes(attrs, R.styleable.CascadeLayoutParams)

      verticalSpacing = a.getDimensionPixelSize(R.styleable.CascadeLayoutParams_layout_vertical_spacing, 0)

      a.recycle()
    }

    constructor(w: Int, h: Int) : super(w, h)

    constructor(layoutParams: ViewGroup.LayoutParams?) : super(layoutParams)

  }
}
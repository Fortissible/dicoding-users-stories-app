package com.example.androidintermediate_sub1_wildanfajrialfarabi.ui.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.example.androidintermediate_sub1_wildanfajrialfarabi.R

class InsertEmailText : AppCompatEditText, View.OnTouchListener {

    private lateinit var clearEmailText : Drawable

    constructor(context: Context) :
    super(context){
        init()
    }

    constructor(context: Context, attrset: AttributeSet) :
    super(context, attrset){
        init()
    }

    constructor(context: Context, attrset: AttributeSet, defStyleAttr: Int) :
    super(context, attrset, defStyleAttr){
        init()
    }

    private fun init(){
        clearEmailText = ContextCompat.getDrawable(context, R.drawable.ic_baseline_clear_24) as Drawable
        setOnTouchListener(this)

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //no action
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0.toString().isNotEmpty()) {
                    checkEmail()
                    showClearEmail()
                } else hideClearEmail()
            }

            override fun afterTextChanged(p0: Editable?) {
                //no action
            }
        })
    }

    private fun showClearEmail(){
        setClearEmailDrawable(endOfTheText = clearEmailText)
    }

    private fun hideClearEmail(){
        setClearEmailDrawable()
    }

    private fun setClearEmailDrawable(
        startOfTheText: Drawable? = null,
        topOfTheText: Drawable? = null,
        endOfTheText: Drawable? = null,
        bottomOfTheText: Drawable? = null,
    ){
        setCompoundDrawablesWithIntrinsicBounds(
            startOfTheText,
            topOfTheText,
            endOfTheText,
            bottomOfTheText
        )

    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
    }

    override fun onTouch(p0: View?, p1: MotionEvent): Boolean {
        if (compoundDrawables[2] != null){
            val clearPasswordStart: Float
            val clearPasswordEnd: Float
            var isClearPasswordClicked = false
            if (layoutDirection == View.LAYOUT_DIRECTION_RTL){
                clearPasswordEnd = (clearEmailText.intrinsicWidth + paddingStart).toFloat()
                when{
                    p1.x < clearPasswordEnd -> isClearPasswordClicked = true
                }
            } else {
                clearPasswordStart = (width - paddingEnd - clearEmailText.intrinsicWidth).toFloat()
                when{
                    p1.x > clearPasswordStart -> isClearPasswordClicked = true
                }
            }
            if (isClearPasswordClicked){
                when (p1.action){
                    MotionEvent.ACTION_DOWN -> {
                        clearEmailText = ContextCompat.getDrawable(context,
                            R.drawable.ic_baseline_clear_24
                        ) as Drawable
                        showClearEmail()
                        return true
                    }
                    MotionEvent.ACTION_UP -> {
                        clearEmailText = ContextCompat.getDrawable(context,
                            R.drawable.ic_baseline_clear_24
                        ) as Drawable
                        when {
                            text != null -> text?.clear()
                        }
                        hideClearEmail()
                        return true
                    }
                }
            }
        }
        return false
    }

    private fun checkEmail(){
        val strEmail = text.toString() as CharSequence
        error = if (!Patterns.EMAIL_ADDRESS.matcher(strEmail).matches()) "Alamat email salah"
                else null
    }
}
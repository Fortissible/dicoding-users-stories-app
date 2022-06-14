package com.example.androidintermediate_sub1_wildanfajrialfarabi.ui.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.example.androidintermediate_sub1_wildanfajrialfarabi.R

class InsertPasswordText: AppCompatEditText, View.OnTouchListener {
    private lateinit var showPasswordText : Drawable

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
        showPasswordText = ContextCompat.getDrawable(context,
            R.drawable.ic_baseline_remove_red_eye_24
        ) as Drawable
        setOnTouchListener(this)

        addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //no action
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0.toString().isNotEmpty()) {
                    checkPass()
                    showShowPassword()
                } else hideShowPassword()
            }

            override fun afterTextChanged(p0: Editable?) {
                //no action
            }
        })
    }

    private fun showShowPassword(){
        setShowPasswordDrawable(endOfTheText = showPasswordText)
    }

    private fun hideShowPassword(){
        setShowPasswordDrawable()
    }

    private fun setShowPasswordDrawable(
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
            val showPasswordStart: Float
            val showPasswordEnd: Float
            var isClearPasswordClicked = false
            if (layoutDirection == View.LAYOUT_DIRECTION_RTL){
                showPasswordEnd = (showPasswordText.intrinsicWidth + paddingStart).toFloat()
                when{
                    p1.x < showPasswordEnd -> isClearPasswordClicked = true
                }
            } else {
                showPasswordStart = (width - paddingEnd - showPasswordText.intrinsicWidth).toFloat()
                when{
                    p1.x > showPasswordStart -> isClearPasswordClicked = true
                }
            }
            if (isClearPasswordClicked){
                when (p1.action){
                    MotionEvent.ACTION_DOWN -> {
                        showPasswordText = ContextCompat.getDrawable(context,
                            R.drawable.ic_baseline_remove_red_eye_24
                        ) as Drawable
                        showShowPassword()
                        transformationMethod = HideReturnsTransformationMethod.getInstance()
                        return true
                    }
                    MotionEvent.ACTION_UP -> {
                        showPasswordText = ContextCompat.getDrawable(context,
                            R.drawable.ic_baseline_remove_red_eye_24
                        ) as Drawable
                        hideShowPassword()
                        transformationMethod = PasswordTransformationMethod.getInstance()
                        return true
                    }
                }
            }
        }
        return false
    }

    private fun checkPass(){
        error = if (text.toString().length < 6) "Panjang password minimal 6"
                else null
    }
}
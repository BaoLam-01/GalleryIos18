/**
 * Created by Matthew Stewart on 10/30/2017 10:47:01 AM
 */
package com.filter.transition;

import android.content.Context;

import com.filter.transition.base.BaseTransition;

public class TransitionFilter extends BaseTransition {

    public TransitionFilter() {
        super(NO_FILTER_FRAGMENT_SHADER);
    }

//    public TransitionFilter(int raw) {
//        super(OpenGlUtils.readShaderFromRawResource(raw));
//    }

    public TransitionFilter(Context context, String raw) {
//        super(readShaderFromRawResource(context, raw));
        super(loadShaderFromAssets(context, raw));
    }

    public void setFragmentShader(Context context, String raw) {
//        mFragmentShader = readShaderFromRawResource(context, raw);
        mFragmentShader = loadShaderFromAssets(context,raw);
    }

    @Override
    protected void onInitTransition() {

    }

    @Override
    protected void onInitializedTransition() {
    }
}

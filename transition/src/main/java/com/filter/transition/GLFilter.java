/**
 * Created by Matthew Stewart on 10/30/2017 10:47:01 AM
 */
package com.filter.transition;

import android.content.Context;

import com.filter.transition.base.BaseGLFilter;

public class GLFilter extends BaseGLFilter {
    private String filter;
    public GLFilter() {
        super(NO_FILTER_FRAGMENT_SHADER);
    }
//    public TransitionFilter(int raw) {
//        super(OpenGlUtils.readShaderFromRawResource(raw));
//    }

    public GLFilter(Context context, String raw) {
//        super(readShaderFromRawResource(context, raw));
        super(loadShaderFromAssets(context, raw));
    }

    public GLFilter(String raw) {
//        super(readShaderFromRawResource(context, raw));
        super(raw);
    }

    public void setFragmentShader(Context context, String raw) {
//        mFragmentShader = readShaderFromRawResource(context, raw);
        mFragmentShader = loadShaderFromAssets(context, raw);
        this.filter = raw;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    @Override
    protected void onInitTransition() {

    }

    @Override
    protected void onInitializedTransition() {
    }
}

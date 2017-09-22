package com.github.keyboard3.developerinterview.pattern;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.design.widget.FloatingActionButton;
import android.view.View;

import com.github.keyboard3.developerinterview.fragments.ProblemsFragment;
import com.github.keyboard3.developerinterview.R;

/**
 * Created by keyboard3 on 2017/9/7.
 */

public class HtmlType extends ProblemType {
    public static final int type = 3;
    public static final String typeStr = "ProblemHtml";

    static class Single {
        static HtmlType instance = new HtmlType();
    }

    public static HtmlType getInstance() {
        return Single.instance;
    }

    @Override
    public ProblemType setFragmentByType(FloatingActionButton fab, FragmentManager fragmentManager) {
        fab.setVisibility(View.VISIBLE);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, ProblemsFragment.newInstance(typeStr), typeStr);
        fragmentTransaction.commit();
        return this;
    }

    @Override
    public int getTypeIcon() {
        return R.mipmap.ic_menu_html;
    }

    @Override
    public String getTypeStr() {
        return typeStr;
    }

    @Override
    public int getType() {
        return type;
    }
}

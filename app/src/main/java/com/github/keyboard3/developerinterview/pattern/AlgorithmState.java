package com.github.keyboard3.developerinterview.pattern;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.design.widget.FloatingActionButton;
import android.view.View;

import com.github.keyboard3.developerinterview.fragment.ProblemsFragment;
import com.github.keyboard3.developerinterview.R;

/**
 * 算法状态
 *
 * @author keyboard3
 * @date 2017/9/7
 */

public class AlgorithmState extends BaseProblemState {
    public static final int type = 5;
    public static final String typeStr = "ProblemAlgorithm";

    static class Single {
        static AlgorithmState instance = new AlgorithmState();
    }

    public static AlgorithmState getInstance() {
        return Single.instance;
    }

    @Override
    public BaseProblemState setFragmentByType(FloatingActionButton fab, FragmentManager fragmentManager) {
        fab.setVisibility(View.VISIBLE);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, ProblemsFragment.newInstance(this));
        fragmentTransaction.commit();
        return this;
    }

    @Override
    public int getTypeIcon() {
        return R.mipmap.ic_menu_algorithm;
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

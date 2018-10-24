package io.github.android9527.aop_app;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import io.github.android9527.apt_api.APTButterKnife;
import io.github.android9527.apt_library.BindView;
import io.github.android9527.apt_library.OnClick;

public class APTTestActivity extends AppCompatActivity
{

    @BindView(R.id.fab)
    FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        APTButterKnife.bind(this);
    }

    @OnClick(R.id.fab)
    void onClick(View view) {
        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }
}

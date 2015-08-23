package com.allpolls.mogoneba.activities;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;

import com.allpolls.mogoneba.R;

public class ExternalLoginActivity extends BaseActivity implements View.OnClickListener {
    public static final String EXTRA_EXTERNAL_SERVICE = "EXTRA_EXTERNAL_SERVICE";
    private Button loginButton;
    private WebView webView;


    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        setContentView(R.layout.activity_external_login);

        loginButton = (Button) findViewById(R.id.activity_external_login_loginButton);
        webView = (WebView) findViewById(R.id.activity_external_login_webView);

        loginButton.setOnClickListener(this);
        loginButton.setText("Log in with: " + getIntent().getStringExtra(EXTRA_EXTERNAL_SERVICE));
    }

    @Override
    public void onClick(View view) {
        if (view == loginButton) {
            application.getAuth().getUser().setLoggedIn(true);
            setResult(RESULT_OK);
            finish();
        }
    }
}

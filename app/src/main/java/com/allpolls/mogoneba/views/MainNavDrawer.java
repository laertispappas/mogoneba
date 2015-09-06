package com.allpolls.mogoneba.views;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.allpolls.mogoneba.R;
import com.allpolls.mogoneba.activities.BaseActivity;
import com.allpolls.mogoneba.activities.ContactsActivity;
import com.allpolls.mogoneba.activities.MainActivity;
import com.allpolls.mogoneba.activities.ProfileActivity;
import com.allpolls.mogoneba.activities.SentMessagesActivity;
import com.allpolls.mogoneba.infrastructure.User;
import com.allpolls.mogoneba.services.Account;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

public class MainNavDrawer extends NavDrawer {
    private final TextView displayNameText;
    private final ImageView avatarImage;

    public MainNavDrawer(final BaseActivity activity) {
        super(activity);

        addItem(new ActivityNavDrawerItem(MainActivity.class, "Inbox", null, R.drawable.abc_ic_ab_back_mtrl_am_alpha, R.id.include_main_nav_drawer_topItems));
        addItem(new ActivityNavDrawerItem(SentMessagesActivity.class, "Sent messages", null, R.drawable.abc_ic_menu_copy_mtrl_am_alpha, R.id.include_main_nav_drawer_topItems));
        addItem(new ActivityNavDrawerItem(ContactsActivity.class, "Contacts", null, R.drawable.abc_ic_go_search_api_mtrl_alpha, R.id.include_main_nav_drawer_topItems));
        addItem(new ActivityNavDrawerItem(ProfileActivity.class, "Profile", null, R.drawable.abc_ic_menu_copy_mtrl_am_alpha, R.id.include_main_nav_drawer_topItems));

        addItem(new BasicNavDrawerItem("Logout", null, R.drawable.abc_btn_borderless_material, R.id.include_main_nav_drawer_bottomItems) {
            @Override
            public void onClick(View view) {
                activity.getMogonebaApplication().getAuth().logout();
            }
        });

        displayNameText = (TextView) navDrawerView.findViewById(R.id.include_main_nav_drawer_displayName);
        avatarImage = (ImageView) navDrawerView.findViewById(R.id.include_main_nav_drawer_avatar);

        User loggedInUser = activity.getMogonebaApplication().getAuth().getUser();
        displayNameText.setText(loggedInUser.getDisplayName());

        Picasso.with(activity).load(loggedInUser.getAvatarUrl()).into(avatarImage);

    }

    @Subscribe
    public void onUserDetailsUpdated(Account.UserDetailsUpdatedEvent event) {
        Picasso.with(activity).load(event.User.getAvatarUrl()).into(avatarImage);
        displayNameText.setText(event.User.getDisplayName());
    }
}

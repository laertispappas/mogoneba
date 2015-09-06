package com.allpolls.mogoneba.services;

import com.allpolls.mogoneba.infrastructure.Auth;
import com.allpolls.mogoneba.infrastructure.MogonebaApplication;
import com.allpolls.mogoneba.infrastructure.User;
import com.squareup.otto.Subscribe;


// services Mocks
// TODO: Unit testing
public class InMemoryAccountService extends BaseInMemoryService {

    public InMemoryAccountService(MogonebaApplication application) {
        super(application);
    }

    @Subscribe
    public void updateProfile(final Account.UpdateProfileRequest request) {
        final Account.UpdateProfileResponse response = new Account.UpdateProfileResponse();

        if(request.DisplayName.equals("nelson")) {
            response.setPropertyError("displayName", "You may not be named nelson");
        }

        invokeDelayed(new Runnable() {
            @Override
            public void run() {
                User user = application.getAuth().getUser();
                user.setDisplayName(request.DisplayName);
                user.setEmail(request.Email);

                bus.post(response);
                bus.post(new Account.UserDetailsUpdatedEvent(user));
            }
        }, 2000, 3000);
    }

    @Subscribe
    public void updateAvatar(final Account.ChangeAvatarRequest request) {
        invokeDelayed(new Runnable() {
            @Override
            public void run() {
                User user = application.getAuth().getUser();
                user.setAvatarUrl(request.NewAvatarUri.toString());

                bus.post(new Account.ChangeAvatarResponse());
                bus.post(new Account.UserDetailsUpdatedEvent(user));
            }
        }, 4000, 5000);
    }

    @Subscribe
    public void changePassword(Account.ChangePasswordRequest request) {
        Account.ChangePasswordResponse response = new Account.ChangePasswordResponse();

        if(!request.NewPassword.equals(request.ConfirmNewPassword)){
            response.setPropertyError("confirmNewPassword", "Passwords must match");
        }

        if (request.NewPassword.length() < 5) {
            response.setPropertyError("newPassword", "Password be larger that 5 characters");
        }

        postDelayed(response);
    }

    @Subscribe
    public void loginWithUsername(final Account.LoginWithUsernameRequest request) {
        invokeDelayed(new Runnable() {
            @Override
            public void run() {
                Account.LoginWithUsernameResponse response = new Account.LoginWithUsernameResponse();
                if (request.Username.equals("nelson"))
                    response.setPropertyError("userName", "Invalid username or password");

                loginUser(response);
                bus.post(response);
            }
        }, 1000, 2000);
    }

    @Subscribe
    public void LoginWithExternalToken(Account.LoginWithExternalTokenRequest request) {
        invokeDelayed(new Runnable() {
            @Override
            public void run() {
                Account.LoginWithExternalTokenResponse response = new Account.LoginWithExternalTokenResponse();
                loginUser(response);
                bus.post(response);
            }
        }, 1000, 2000);
    }

    @Subscribe
    public void register(Account.RegisterRequest request){
        invokeDelayed(new Runnable() {
            @Override
            public void run() {
                Account.RegisterResponse response = new Account.RegisterResponse();
                loginUser(response);
                bus.post(response);
            }
        }, 1000, 2000);
    }

    @Subscribe
    public void externalRegister(Account.RegisterWithExternalTokenRequest request) {
        invokeDelayed(new Runnable() {
            @Override
            public void run() {
                Account.RegisterWithExternalTokenResponse response = new Account.RegisterWithExternalTokenResponse();
                loginUser(response);
                bus.post(response);
            }
        }, 1000, 2000);
    }

    @Subscribe
    public void loginWithLocalToken(Account.LoginWithLocalTokenRequest request) {
        invokeDelayed(new Runnable() {
            @Override
            public void run() {
                Account.LoginWithLocalTokenResponse response = new Account.LoginWithLocalTokenResponse();
                loginUser(response);
                bus.post(response);
            }
        }, 1000, 2000);
    }

    private void loginUser(Account.UserResponse response){
        Auth auth = application.getAuth();
        User user = auth.getUser();

        user.setDisplayName("Laerti Papa");
        user.setUserName("anonymous");
        user.setEmail("l.pappas@gmail.com");
        user.setAvatarUrl("http://www.gravatar.com/avatar/1?=identicon");
        user.setLoggedIn(true);
        user.setId(123);
        bus.post(new Account.UserDetailsUpdatedEvent(user));

        auth.setAuthToken("fakeAuthToken");

        response.DisplayName = user.getDisplayName();
        response.UserName = user.getUserName();
        response.Email = user.getEmail();
        response.AvatarUrl = user.getAvatarUrl();
        response.Id = user.getId();
        response.AuthToken = auth.getAuthToken();
    }
}

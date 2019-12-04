package muchbeer.raum.firebaselivedata.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

import muchbeer.raum.firebaselivedata.R;
import muchbeer.raum.firebaselivedata.data.MyFragmentListenerImpl;
import muchbeer.raum.firebaselivedata.databinding.MessageListFragmentBinding;
import muchbeer.raum.firebaselivedata.model.Entity;
import muchbeer.raum.firebaselivedata.repository.FirebaseRepository;
import muchbeer.raum.firebaselivedata.rvadapter.MessageAdapter;
import muchbeer.raum.firebaselivedata.viewmodel.MessageListViewModel;

public class MessageListFragment extends Fragment {

    private static final String TAG = MessageListFragment.class.getSimpleName();
    private static final String ANONYMOUS = "anonymous";
    private static final int RC_SIGN_IN = 1;
    private static final int RESULT_CANCELED = 2016 ;

    private MessageListViewModel mModel ;
    private MessageListFragmentBinding mBinding;
    private MyFragmentListenerImpl mFragmentCallback;
    private MessageAdapter mMessageAdapter = new MessageAdapter();
    private Toast toast;

    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    private FirebaseAuth.AuthStateListener mAuthStateListener;

 /*   interface MyFragmentListenerImpl {
        void onFabButtonClicked();
    }*/

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mModel = ViewModelProviders.of(getActivity()).get(MessageListViewModel.class);


        FirebaseApp.initializeApp(getActivity());
        mAuthStateListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if(user != null){
                FirebaseRepository.getInstance().setUserName(user.getDisplayName());
            } else {
                Log.d(TAG, "user is null");
                FirebaseRepository.getInstance().setUserName(ANONYMOUS);
                // Choose authentication providers
                List<AuthUI.IdpConfig> providers = Arrays.asList(

                        new AuthUI.IdpConfig.EmailBuilder().build(),
                        new AuthUI.IdpConfig.GoogleBuilder().build());
                // Create and launch sign-in intent
                startActivityForResult(
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setIsSmartLockEnabled(false)
                                .setAvailableProviders(providers)
                                .setLogo(R.mipmap.ic_launcher)
                                .build(),
                        RC_SIGN_IN);
            }
        };



    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.message_list_fragment,
                container, false );


        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mBinding.recyclerview.setLayoutManager(layoutManager);


        mBinding.fab.setOnClickListener(v -> {
            mFragmentCallback.onFabButtonClicked();
        });

        return mBinding.getRoot();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.d(TAG, "on attach");

        try {
            mFragmentCallback = (MyFragmentListenerImpl) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.d(TAG, "onActivityCreated");

        mBinding.recyclerview.setAdapter(mMessageAdapter);

        // Update the list when the data changes
        if(mModel != null){
            LiveData<List<Entity>> liveData = mModel.getMessageListLiveData();

            liveData.observe(getActivity(), (List<Entity> mEntities) -> {
                mMessageAdapter.setMessageList(mEntities);
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "result code = " + String.valueOf(resultCode));

        if(resultCode == RESULT_CANCELED){
            return;
        }
        switch(requestCode){
            case RC_SIGN_IN:
                toast.makeText(getActivity(),"Signed in",Toast.LENGTH_SHORT).show();

                break;

            default: Log.w(TAG, "switch(requestCode), case not implemented.");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();
        if(mAuthStateListener != null){
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
    }
}

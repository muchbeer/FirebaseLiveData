package muchbeer.raum.firebaselivedata.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;

import muchbeer.raum.firebaselivedata.R;
import muchbeer.raum.firebaselivedata.databinding.NewPostFragmentBinding;
import muchbeer.raum.firebaselivedata.repository.FirebaseRepository;
import muchbeer.raum.firebaselivedata.viewmodel.MessageListViewModel;

import static android.app.Activity.RESULT_CANCELED;

public class NewPostFragment extends Fragment {

    private static final String TAG = NewPostFragment.class.getSimpleName();
    private static final int RC_PHOTO_PICKER = 1;
    //public static final String ARG_POSITION = "USER_NAME";

    private NewPostFragmentBinding mBinding;
    private MessageListViewModel mViewModel;
    private String mUserName;
    private Toast toast;
    private NewPostFragmentClickEvent clickImage;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        mUserName = FirebaseRepository.getInstance().getUserName();

        // DataBinding
        mBinding = DataBindingUtil.inflate(inflater, R.layout.new_post_fragment, container, false);

        mViewModel = ViewModelProviders.of(getActivity()).get(MessageListViewModel.class);



        // attach listeners
        mViewModel.getPictureUploadIsSuccessful().observe(this, isSuccess -> {
            if(isSuccess){


                if(!mViewModel.getPhotoUrl().isEmpty()){
                    Glide.with(mBinding.photoView.getContext())
                            .load(mViewModel.getPhotoUrl())
                            .into(mBinding.photoView);
                    toast.makeText(getContext(),"Picture Upload successful",Toast.LENGTH_SHORT).show();

                }
            } else{
                toast.makeText(getContext(),"Could not fetch the picture!",Toast.LENGTH_LONG).show();
            }
        });

        mViewModel.getMessageUploadIsSuccessful().observe(this, isSuccessful -> {
            if(isSuccessful && !mViewModel.getPhotoUrl().isEmpty()){
                toast.makeText(getContext(),"Message Upload successful",Toast.LENGTH_SHORT).show();
            }
        });

        //Handler click event using udacity
        clickImage = new NewPostFragmentClickEvent(getActivity());
       mBinding.setClickImage(clickImage);

        mBinding.messageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    mBinding.sendButton.setEnabled(true);
                } else {
                    mBinding.sendButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });


        // Send button sends a message and clears the EditText
        mBinding.sendButton.setOnClickListener(view -> {
            String mText = mBinding.messageEditText.getText().toString();

            if(mViewModel.getPhotoUrl() != null && !mViewModel.getPhotoUrl().isEmpty()){
                mViewModel.createAndSendToDataBase(
                        mUserName,
                        mText,
                        mViewModel.getPhotoUrl());

                mBinding.messageEditText.setText("");
                mViewModel.setPhotoUrl("");
                dismissKeyboard();
                getActivity().onBackPressed();
            } else {
                Toast.makeText(getContext(),"Please, choose a picture.",Toast.LENGTH_SHORT).show();
            }

        });


        return mBinding.getRoot();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "result code = " + String.valueOf(resultCode));

        if(resultCode == RESULT_CANCELED){
            toast.makeText(getContext(),"Action canceled",Toast.LENGTH_SHORT).show();
            return;
        }

        switch(requestCode){
            case RC_PHOTO_PICKER:

                mViewModel.uploadPicture(data);
                break;
            default: Log.w(TAG, "switch(requestCode), case not implemented.");
        }
    }

    private void dismissKeyboard(){
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && null != getActivity().getCurrentFocus())
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus()
                    .getApplicationWindowToken(), 0);
    }

    public class NewPostFragmentClickEvent {
        //get the context to pass to the Mainthread
        Context context;

        public NewPostFragmentClickEvent(Context context) {
            this.context = context;
        }

        public void openCamera(View view) {
            Toast.makeText(getActivity(), "Now the camera is suppose to open",Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/jpeg");
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
            startActivityForResult(Intent.createChooser(intent, "complete action using"), RC_PHOTO_PICKER);

         /*   mBinding.photoPickerButton.setOnClickListener(view -> {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "complete action using"), RC_PHOTO_PICKER);
            });*/

        }
    }
}

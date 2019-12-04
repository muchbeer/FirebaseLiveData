package muchbeer.raum.firebaselivedata.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import muchbeer.raum.firebaselivedata.R;
import muchbeer.raum.firebaselivedata.data.MyFragmentListenerImpl;
import muchbeer.raum.firebaselivedata.databinding.MessageListFragmentBinding;
import muchbeer.raum.firebaselivedata.rvadapter.MessageAdapter;
import muchbeer.raum.firebaselivedata.viewmodel.MessageListViewModel;

public class MainActivity extends AppCompatActivity implements MyFragmentListenerImpl {

    private static final String TAG = MainActivity.class.getSimpleName();

    private final NewPostFragment mComposerFragment = new NewPostFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onFabButtonClicked() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, mComposerFragment)
                .addToBackStack(null)
                .commit();
    }
}

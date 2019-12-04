package muchbeer.raum.firebaselivedata.rvadapter;


import android.view.View;

public class BindingAdapter {

    @androidx.databinding.BindingAdapter("visibleGone")
    public static void showHide(View view, boolean show) {
        view.setVisibility(show ? View.VISIBLE : View.GONE);
    }

}

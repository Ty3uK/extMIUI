package tk.ty3uk.extmiuiv7.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import tk.ty3uk.extmiuiv7.R;
import tk.ty3uk.extmiuiv7.BuildConfig;

/**
 * Created by ty3uk on 01.09.16.
 */
public class AboutFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.about_fragment, container, false);

        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.about);

        TextView appName = (TextView) view.findViewById(R.id.app_name);
        TextView thanksTo = (TextView) view.findViewById(R.id.thanks_to);
        ImageView telegram = (ImageView) view.findViewById(R.id.telegram);
        ImageView forpda = (ImageView) view.findViewById(R.id.forpda);
        ImageView gmail = (ImageView) view.findViewById(R.id.gmail);

        appName.setText(String.format("%1$s %2$s", appName.getText(), BuildConfig.VERSION_NAME));
        thanksTo.setMovementMethod(LinkMovementMethod.getInstance());
        telegram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://telegram.me/xxxTy3uKxxx")));
            }
        });
        forpda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://4pda.ru/forum/index.php?showtopic=741043")));
            }
        });
        gmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:max.karelov@gmail.com")));
            }
        });

        return view;
    }
}

package me.bkkn.translate.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import me.bkkn.translate.data.DB;
import me.bkkn.translate.R;
import me.bkkn.translate.data.NetworkTask;

public class TranslateFragment extends Fragment {

    private static final int histStrMaxLength = 24;
    TextView header;
    private TextView outputText;
    EditText inputText;
    public static final String APP_PREFERENCES = "shared";
    public static final String APP_PREFERENCES_FROM = "from_text";
    public static final String APP_PREFERENCES_TO = "to_text";
    public static final String APP_PREFERENCES_LANG_FROM = "lang_from";
    public static final String APP_PREFERENCES_LANG_TO = "lang_to";
    SharedPreferences mShare;
    DB db;
    ImageView imageView;
    NetworkTask networkTask;

    public TranslateFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View translView = inflater.inflate(R.layout.fragment_translate, container, false);

        header = (TextView) translView.findViewById(R.id.header);
        header.setText(R.string.title_translate);

        mShare = getActivity().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        String mShareFrom = mShare.getString(APP_PREFERENCES_FROM, "");
        String mShareTo = mShare.getString(APP_PREFERENCES_TO, "");
        String mShareLangFrom = mShare.getString(APP_PREFERENCES_LANG_FROM, "");
        String mShareLangTO = mShare.getString(APP_PREFERENCES_LANG_TO, "");

        inputText = (EditText) translView.findViewById(R.id.inputText);
        if (mShare.contains(APP_PREFERENCES_FROM))
            inputText.setText(mShareFrom);

        outputText = (TextView) translView.findViewById(R.id.textView);
        if (mShare.contains(APP_PREFERENCES_TO))
            outputText.setText(mShareTo);
        else outputText.setText("");

        outputText.setMovementMethod(new ScrollingMovementMethod());

        String[] list = getResources().getStringArray(R.array.languages_list);

        final Spinner spin_from = (Spinner) translView.findViewById(R.id.spinner_from);
        final ArrayAdapter<String> fromAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, list);
        fromAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin_from.setAdapter(fromAdapter);
        if (mShare.contains(APP_PREFERENCES_LANG_FROM))
            spin_from.setSelection(Integer.parseInt(mShareLangFrom));
        else spin_from.setSelection(0);

        final Spinner spin_to = (Spinner) translView.findViewById(R.id.spinner_to);
        final ArrayAdapter<String> toAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, list);
        toAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin_to.setAdapter(toAdapter);
        if (mShare.contains(APP_PREFERENCES_LANG_TO))
            spin_to.setSelection(Integer.parseInt(mShareLangTO));
        else spin_to.setSelection(1);

        ImageView img = (ImageView) translView.findViewById(R.id.imageView);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int m_to = spin_to.getSelectedItemPosition();
                int m_from = spin_from.getSelectedItemPosition();
                SpinnerAdapter a_to = spin_to.getAdapter();
                SpinnerAdapter a_from = spin_from.getAdapter();
                spin_to.setAdapter(a_from);
                spin_from.setAdapter(a_to);
                spin_to.setSelection(m_from);
                spin_from.setSelection(m_to);
            }

        });

        imageView = (ImageView) translView.findViewById(R.id.zImage);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                outputText.setText("");
                inputText.setText("");
            }
        });

        db = new DB(getActivity());
        db.open();

        inputText.addTextChangedListener(new TextWatcher() {
             private Timer timer = new Timer();
             private final long DELAY = 1000;

             @Override
             public void afterTextChanged(Editable s) {
                 timer.cancel();
                 timer = new Timer();

                 timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                networkTask = new NetworkTask();
                                String k_from = spin_from.getSelectedItem().toString();
                                String k_to = spin_to.getSelectedItem().toString();

                                int langFromPos = spin_from.getSelectedItemPosition();
                                int langToPos = spin_to.getSelectedItemPosition();
                                final String strFrom = inputText.getText().toString();
                                String strLangFrom = k_from.substring(0, k_from.indexOf("-"));
                                String strLangTo = k_to.substring(0, k_to.indexOf("-"));
                                networkTask.execute(strFrom, strLangFrom + '-' + strLangTo);
                                try {
                                    String str = networkTask.get().toString();
                                    String strCode = str.substring(str.indexOf("<Translation") + 19, str.indexOf("<Translation") + 22);

                                    // Code 200 - success
                                    if (strCode.equals("200")) {
                                        if (str.indexOf("<text>") + 6 < str.indexOf("</text>") - 1) {
                                            String strTo = str.substring(str.indexOf("<text>") + 7, str.indexOf("</text>") - 1);
                                            outputText.setText(strTo);

                                            SharedPreferences.Editor editor = mShare.edit();
                                            editor.putString(APP_PREFERENCES_FROM, strFrom);
                                            editor.putString(APP_PREFERENCES_TO, outputText.getText().toString());
                                            editor.putString(APP_PREFERENCES_LANG_FROM, String.valueOf(langFromPos));
                                            editor.putString(APP_PREFERENCES_LANG_TO, String.valueOf(langToPos));
                                            editor.apply();

                                            if ((strFrom.length() > 0) & (strFrom.length() < histStrMaxLength))
                                                db.addRec(strFrom,
                                                        outputText.getText().toString(),
                                                        (strLangFrom + '-' + strLangTo).toUpperCase(),
                                                        R.drawable.ic_bookmark_grey_48dp);
                                            else if (strFrom.length() > histStrMaxLength)
                                                db.addRec(strFrom.substring(0, 21) + "...",
                                                        outputText.getText().toString().substring(0, 21) + "...",
                                                        (strLangFrom + '-' + strLangTo).toUpperCase(),
                                                        R.drawable.ic_bookmark_grey_48dp);
                                        }
                                        //Возможные кода ответа от Яндекса
                                    } else if (strCode.equals(R.string.err404))
                                        outputText.setText(R.string.traffic_limit_overcome);
                                    else if (strCode.equals(R.string.err413))
                                        outputText.setText(R.string.text_size_limit_overcome);
                                    else if (strCode.equals(R.string.err422))
                                        outputText.setText(R.string.text_translation_impossible);
                                    else if (strCode.equals(R.string.err501))
                                        outputText.setText(R.string.unsupported_translation_direction);
                                    else outputText.setText(R.string.connection_error);
                                } catch (InterruptedException | ExecutionException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                },DELAY );
             }

             @Override
             public void beforeTextChanged(CharSequence s, int start, int count, int after) {
             }

             @Override
             public void onTextChanged(CharSequence s, int start, int before, int count) {
             }
         }
        );
        setRetainInstance(true);
        return translView;
    }
}

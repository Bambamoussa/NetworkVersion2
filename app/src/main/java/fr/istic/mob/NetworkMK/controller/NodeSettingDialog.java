package fr.istic.mob.NetworkMK.controller;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.istic.mob.NetworkMK.R;
import fr.istic.mob.NetworkMK.model.Arc;
import fr.istic.mob.NetworkMK.model.Node;

/**
 * Classe pour gérer la fenetre de l'edition de l'étiquette d'un Noeud
 *
 */
public class NodeSettingDialog extends Dialog {

    private EditText mEtiquette;
    private EditText mSize;
    private Spinner mColor;
    private Button mBtnSave;
    private Button mBtnDelete;
    private ImageView mBtnCancel;


    private MainActivity mainActivity;

    public NodeSettingDialog(@NonNull MainActivity mainActivity) {
        super(mainActivity);
        this.mainActivity = mainActivity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.node_editing);

        mEtiquette = findViewById(R.id.te_etiquette);
        mSize = findViewById(R.id.te_size);
        mColor = findViewById(R.id.te_color);
        mBtnSave = findViewById(R.id.btn_valider);
        mBtnDelete = findViewById(R.id.btn_delete_node);
        mBtnCancel = findViewById(R.id.btn_cancel);

        mEtiquette.setText(this.mainActivity.getNodeSelected().getEtiquete());
        mSize.setText(String.valueOf(this.mainActivity.getNodeSelected().getmWidth()));
        mColor.setSelection(getIndex(this.mainActivity.getNodeSelected().getColor()));

        mEtiquette.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mBtnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.getMyDraw().getGraph().removeNode(mainActivity.getNodeSelected());
                mainActivity.getSupportView().postInvalidate();
                dismiss();
            }
        });

        mBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Les vérifs
                Pattern p = Pattern.compile("[^0-9.]", Pattern.CASE_INSENSITIVE);
                Matcher m = p.matcher(mSize.getText().toString());
                boolean b = m.find();
                if(mEtiquette.getText().length()==0 || mSize.getText().length()==0 || b){
                    Toast toast = Toast.makeText (getContext().getApplicationContext (), getContext().getString(R.string.wrong_entry) , Toast.LENGTH_SHORT);
                    toast.setMargin ( 50 , 50 );
                    toast.show ();
                }
                else {
                    Node nodeSelected = mainActivity.getNodeSelected();
                    nodeSelected.setEtiquete(mEtiquette.getText().toString());
                    // For spinner
                    // To get a String
                    // String size = mColor.getSelectedItem().toString();
                    int spinner_pos = mColor.getSelectedItemPosition();
                    int[] colors_values = mainActivity.getResources().getIntArray(R.array.colors_values);
                    int color = Integer.valueOf(colors_values[spinner_pos]);
                    nodeSelected.setColor(color);
                    nodeSelected.setmCustomWidth(Integer.valueOf(mSize.getText().toString()));
                    Set<Arc> arcs = new HashSet<Arc>(mainActivity.getGraph().getmArcList());

                    nodeSelected.upDateCenter();
                    mainActivity.getSupportView().postInvalidate();
                    dismiss();
                }
            }
        });

        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

    }

    private int getIndex(int currentColor){
        int[] colors_values = mainActivity.getResources().getIntArray(R.array.colors_values);
        for (int i=0;i<colors_values.length;i++){
            if (colors_values[i] == currentColor){
                return i;
            }
        }
        return 0;
    }

}

package fr.istic.mob.NetworkMK.controller;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import fr.istic.mob.NetworkMK.R;
import fr.istic.mob.NetworkMK.model.Arc;
import fr.istic.mob.NetworkMK.model.DrawableGraph;
import fr.istic.mob.NetworkMK.model.Graph;
import fr.istic.mob.NetworkMK.model.Node;

public class MainActivity extends AppCompatActivity{

    /**
     * Pour gérer le tag parcelable
     */
    public static final String GRAPH="GRAPH";
    /**
     * Pour la Gestion de la détection du touché
     */
    private static final String DEBUG_TAG = "Gestures";
    GestureDetector mDetector;
    View.OnTouchListener listener;
    private boolean mIsScrolling = false;
    private DrawableGraph myDraw;

    private Graph graph;

    public Graph getGraph() {
        return graph;
    }

    public void setGraph(Graph graph) {
        this.graph = graph;
    }


    public DrawableGraph getMyDraw() {
        return myDraw;
    }

    public void setMyDraw(DrawableGraph myDraw) {
        this.myDraw = myDraw;
    }

    public ImageView getSupportView() {
        return supportView;
    }

    /**
     * supportView est l'image qui contient le dessin du graph.
     *
     */
    private ImageView supportView;

    private enum mOptionMode{
        REINITIALISER("REINITIALISER"),
        AJOUTER_NOEUDS("AJOUTER_NOEUDS"),
        AJOUTER_ARC_BOUCLES("AJOUTER_ARC_BOUCLES"),
        MODIFICATION("MODIFICATION"),
        SAVE_GRAPH("SAVE GRAPH"),
        IMPORT_GRAPH("IMPORT_GRAPH"),
        SEND_BY_EMAIL("SEND BY EMAIL"),
        CHANGER_DE_LANGUE("CHANGER_DE_LANGUE");

        private String name="";
        mOptionMode(String name) {
            this.name=name;
        }

        @NonNull
        @Override
        public String toString() {
            return this.name;
        }
    }

    private mOptionMode mModeSelected = null;

    private Node nodeSelected = null;

    private Arc arcSelected = null;


    public Node getNodeSelected() {
        return nodeSelected;
    }

    public void setNodeSelected(Node nodeSelected) {
        this.nodeSelected = nodeSelected;
    }


    public Arc getArcSelected() {
        return arcSelected;
    }

    public void setArcSelected(Arc arcSelected) {
        this.arcSelected = arcSelected;
    }

    int mDownx = 0;
    int mDowny = 0;
    int upx = 0;
    int upy = 0;

    final static long LONG_TOUCH_DURATION = 1000;
    long touchStartTime = 0;

    /**
     * A la création de l'activié
     * @param savedInstanceState
     */
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        graph = new Graph(this);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getResources().getString(R.string.app_name));
        mModeSelected = mOptionMode.REINITIALISER;
        getSupportActionBar().setTitle(appname(R.string.reinitialiser));

        if(savedInstanceState!=null)
        {
            graph =savedInstanceState.getParcelable(GRAPH);
            mModeSelected = mOptionMode.valueOf(savedInstanceState.getString("mode"));
        }
        myDraw = new DrawableGraph(this.graph);
        setContentView(R.layout.activity_main);


        mDetector = new GestureDetector(this, new MyGestureDetector());
        listener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mDetector.onTouchEvent(event)) {
                    return true;
                }

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    switch (mModeSelected) {
                        case AJOUTER_ARC_BOUCLES: {
                            Log.d(DEBUG_TAG, "ACTION_UP");
                            createArc();
                        }
                    }
                }

                return false;
            }

        };

        supportView = (ImageView) findViewById(R.id.imageView);
        supportView.setImageDrawable(myDraw);
        supportView.setOnTouchListener(listener);

    }

    /**
     * Création de l'arc lorsque l'utilisateur lève le doigt sur un Noeud.
     *
     * 1. En posant le doigt sur un nœud et en le suivant jusqu’à un autre
     * nœud (l’arc temporaire est visible et est modifié quand on déplace le doigt).
     *
     * 2. L’arc n’est pas créé si le doigt est lâché ailleurs que sur un nœud.
     *
     *  Etape 1 est gérer dans onScroll de MyGestureDetector.
     *  Puis Etape 2  avec notre méthode createArc()
     */
    public void createArc(){
        if (mIsScrolling) {
            myDraw.setTempArc(null);
            Log.d(DEBUG_TAG, "OnTouchListener --> onTouch ACTION_UP");
            mIsScrolling = false;
            Node n1 = myDraw.getGraph().selectNodeAround(mDownx, mDowny);
            Node n2 = myDraw.getGraph().selectNodeAround(upx, upy);

            if ((n1 != null) && (n2 != null)) {

                final EditText input = new EditText(MainActivity.this);
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(getResources().getString(R.string.arc_label))

                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Check Entry value
                                if(input.getText().toString().length()>0){
                                    Arc arc = new Arc(myDraw.getGraph().selectNodeAround(mDownx, mDowny), myDraw.getGraph().selectNodeAround(upx, upy),input.getText().toString());
                                    myDraw.getGraph().addArc(arc);
                                }
                                else {
                                    Toast toast = Toast.makeText (getApplicationContext(), getResources().getString(R.string.wrong_entry) , Toast.LENGTH_SHORT);
                                    toast.setMargin ( 50 , 50 );
                                    toast.show ();
                                }
                                supportView.invalidate();
                            }
                        })
                        .setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                supportView.invalidate();
                            }
                        })
                        .setView(input)
                        .create()
                        .show();
            }
            else if ((n1 == null) || (n2 == null)){
                myDraw.setTempArc(null);
                supportView.invalidate();
            }
        }

        /**
         * Todo Anh Troisième à faire
         *
         * Implement le zoom sur l'image
         */
    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (myDraw.getGraph() != null)
            outState.putParcelable(GRAPH, myDraw.getGraph());
            outState.putString("mode",mModeSelected.toString());
    }
    @Override
    public void onPause() {
        super.onPause();

    }

    /**
     * Classe pour gérer le touch sur supportView
     */
    class MyGestureDetector implements GestureDetector.OnGestureListener,GestureDetector.OnDoubleTapListener{
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {

            Log.d(DEBUG_TAG, "onSingleTapConfirmed: " + e.toString());
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            Log.d(DEBUG_TAG, "onDoubleTap: " + e.toString());
            return true;
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            Log.d(DEBUG_TAG, "onDoubleTapEvent: " + e.toString());
            return true;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            // On va maintenant vérifier si aux positions x, y lors du Down
            // Un neoud ou un arc s'y trouve
            System.out.println("DOODODOODODOOD");
            mDownx = (int) e.getX();
            mDowny = (int) e.getY();
            nodeSelected = myDraw.getGraph().selectNodeAround(mDownx,mDowny);
            arcSelected= myDraw.getGraph().selectArcAround(mDownx,mDowny);

            Log.d(DEBUG_TAG, "OnDown: " + e.toString());
            return true;
        }

        @Override
        public void onShowPress(MotionEvent e) {
            Log.d(DEBUG_TAG, "onShowPress: " + e.toString());
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            upx = (int) e.getX();
            upy = (int) e.getY();
            switch (mModeSelected) {
                case AJOUTER_NOEUDS: {
                    if ((Math.abs(upx - mDownx) == 0) || (Math.abs(upy - mDowny) == 0)) {
                        int number = 1 + myDraw.getGraph().getNodeList().size();
                        Node node = new Node(upx, upy,Graph.DEFAULT_WIDTH,Graph.DEFAULT_HEIGHT, "" + number);
                        if (myDraw.getGraph().addNode(node)) {
                        }
                        supportView.invalidate();
                    }
                    break;
                }

            }
            Log.d(DEBUG_TAG, "ClickSimple: " + e.toString());
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

            mIsScrolling = true;
            mDownx = (int) e1.getX();
            mDowny = (int) e1.getY();
            upx = (int) e2.getX();
            upy = (int) e2.getY();

            switch (mModeSelected) {
                case AJOUTER_ARC_BOUCLES: {
                    System.out.println("AAA" + (int)e1.getX() + "BBB" + (int)e1.getY()+ "SSSSS" + nodeSelected);
                    Node tempNode = new Node((int)e2.getX(), (int)e2.getY());
                    if (nodeSelected != null && tempNode != null) {
                        tempNode.setBounds(nodeSelected.getBounds());
                        System.out.println("node " + nodeSelected + "Selected");
                        Arc tempArc = new Arc(nodeSelected, tempNode);
                        myDraw.setTempArc(tempArc);
                        supportView.invalidate();
                    }
                    break;
                }

                case MODIFICATION:{
                    // TODO Yves
                    // Lorsqu'on tire sur l'arc, Modifier le Mid Point
                    // Ne fonctionne pas bien, Nécéssie une améliorationLorsqu'on tire sur l'arc, Modifier le Mid Point

                    if (nodeSelected != null)
                        nodeSelected.setNewPosition((int)e2.getX(),(int)e2.getY());
                    else if (arcSelected != null) {
                        System.out.println("Aricirireiiririri");
                        arcSelected.setMiddlePoint(new float[]{(int)e2.getX(),(int)e2.getY()});
                    }
                    supportView.invalidate();
                    return true;
                }
            }
            Log.d(DEBUG_TAG, "Scroll 1: " + e1.toString());
            Log.d(DEBUG_TAG, "Scroll 2: " + e2.toString());
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            System.out.println("LONNNNNNNNNNNNNNNNNNG");
            switch (mModeSelected) {
                case MODIFICATION: {
                    if (nodeSelected != null) {
                        dialogNodeEditing();
                        supportView.invalidate();
                        Log.d(DEBUG_TAG, "onLongPress: " + e.toString());
                    }
                    else if (arcSelected != null){
                        System.out.println("DANS ARC");
                        System.out.println("ARC NOn null");
                        dialogArcEditing();
                    }
                }

            }
        }


            // TODO Anh Deuxieme à faire
            /**
             *
             * Je veux que ici tu gère l'edition de l'étiqute de l'arc.
             *
             * Reste à faire lageur de la boucle
             * — modifier la largeur de la boucle (seulement pour les boucles bien sûr).
             *
             *
             */


        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            // Remise à null le Noeud selectionné pour les mouvements et Options
            nodeSelected = null;
            arcSelected = null;

            myDraw.setTempArc(null);
            supportView.invalidate();
            Log.d(DEBUG_TAG, "Flig 1: " + e1.toString());
            Log.d(DEBUG_TAG, "Flig 2: " + e2.toString());
            switch (mModeSelected) {
                case AJOUTER_ARC_BOUCLES: {
                    createArc();
                }
            }
            return true;
        }


    }

        //==================      MENU     ========================


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    private String appname(int choose){
        return getResources().getString(R.string.app_name)+" "+getResources().getString(R.string.app_name_separator)+" "+getResources().getString(choose);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.reinitialiser:
                getSupportActionBar().setTitle(appname(R.string.reinitialiser));
                myDraw.initialise(this);
                supportView.invalidate();
                return true;
            case R.id.node_addition_mode:
                getSupportActionBar().setTitle(appname(R.string.mode_ajout_noeud));
                mModeSelected = mOptionMode.AJOUTER_NOEUDS;
                return true;
            case R.id.edge_addition_mode:
                getSupportActionBar().setTitle(appname(R.string.mode_ajout_arc_bloucle));
                mModeSelected = mOptionMode.AJOUTER_ARC_BOUCLES;
                return true;
            case R.id.modification:
                getSupportActionBar().setTitle(appname(R.string.mode_modification));
                mModeSelected = mOptionMode.MODIFICATION;
                return true;
            case R.id.sauver:
                saveGraph();
                /**
                 *
                 * ToDo Yves
                 * Pour sauvegarder le Graph en Json
                 * https://stackoverflow.com/questions/28495830/android-convert-parcelable-to-json
                 */
                return true;
            case R.id.importer:
                /**
                 * Utilisé
                 * https://stackoverflow.com/questions/14376807/how-to-read-write-string-from-a-file-in-android
                 */
                importerGraph();
                return true;
            case R.id.send:
                getSupportActionBar().setTitle(appname(R.string.envoyer));
                supportView.setBackgroundColor(Color.rgb(255, 255, 255));
                supportView.invalidateDrawable(myDraw);
                supportView.buildDrawingCache();
                Bitmap bm=supportView.getDrawingCache();
                String path = MediaStore.Images.Media.insertImage(getContentResolver(), bm,"title", null);
                Uri screenshotUri = Uri.parse(path);
                final Intent emailIntent1 = new Intent(     android.content.Intent.ACTION_SEND);
                emailIntent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                emailIntent1.putExtra(Intent.EXTRA_STREAM, screenshotUri);
                emailIntent1.setType("image/png");
                startActivity(Intent.createChooser(emailIntent1, "Send email using"));
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    /**
     * Importation du graph.
     *
     * On importe un graph s'il existe un graph déjà enregistré
     *
     * Pour cela on utilise lit le fichier Gson du graph
     * On lit le fichier txt contenant la descritption du graph sous forme json.
     *
     * Ensuite on contruit un nouveau graph qu'on fait passé à my Draw.
     * Enfin il faut reconnecter les Noeud aux arc comme ils étaient
     * en utilisant pour chaque arc les coordonnées de son neoud de départ et son noeud de fin.
     */
    public void importerGraph(){

        try {
            InputStream inputStream = this.openFileInput("config.txt");

            if ( inputStream != null ) {
                String ret = "";
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();

                if(new Gson().fromJson(ret, Graph.class)!=null) {
                    myDraw.setGraph(new Gson().fromJson(ret, Graph.class));
                    myDraw.getGraph().linkNodesWithArcs();
                    supportView.invalidate();
                    Toast.makeText(getBaseContext(), getResources().getString(R.string.done), Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(getBaseContext(), getResources().getString(R.string.none), Toast.LENGTH_LONG).show();
                }
            }
        }
        catch (FileNotFoundException e) {
            Toast.makeText(getBaseContext(), getResources().getString(R.string.none), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(getBaseContext(), getResources().getString(R.string.none), Toast.LENGTH_LONG).show();
        }

    }


    /**
     * Sauvegarde du graph.
     *
     * Pour cela on utilise la librairy Gson pour stocker le graph parcelable sous forme Json dans un string
     * Et on stock le string dans un fichier local
     *
     */
    public void saveGraph(){

        String graphInJson = new Gson().toJson(myDraw.getGraph());
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(this.openFileOutput("config.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(graphInJson);
            outputStreamWriter.close();
        } catch (IOException e) {
            Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
        Toast.makeText(getBaseContext(), getResources().getString(R.string.done), Toast.LENGTH_LONG).show();

    }


    /**
     * Mehtode pour afficher la fenetre d'édition d'un Noeud
     */
    public void dialogNodeEditing(){
        if(true)//mModeSelected.toString().equals("MODIFICATION_NEOUDS_ARC_BOUCLES")
        {
            NodeSettingDialog nodeSettingDialog = new NodeSettingDialog(this);
            nodeSettingDialog.show();
        }
    }

    /**
     * Mehtode pour afficher la fenetre d'édition d'un Arc
     */
    public void dialogArcEditing(){
        if(true)//mModeSelected.toString().equals("MODIFICATION_NEOUDS_ARC_BOUCLES")
        {
            ArcSettingDialog arcSettingDialog = new ArcSettingDialog(this);
            arcSettingDialog.show();
        }
    }
}

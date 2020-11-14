package fr.istic.mob.NetworkMK.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fr.istic.mob.NetworkMK.R;

public class Graph implements Parcelable {
    private  List<Node> mNodeList = new ArrayList<>();
    private List<Arc> mArcList = new ArrayList<>();
    private Context context;
    int mHeight=800;
    int mWidth=600;
    private static final int N_NODE_INIT = 3;// Pour construire un arbre avec 9 noeuds
    private static int spaceBetweenNodeInit;
    private static int MargeNodeInit;
    public static int LABEL_TEXT_SIZE;
    public static int DEFAULT_WIDTH;
    public static int DEFAULT_HEIGHT;
    public static int DEFAULT_WIDTH_ARC;
    public static int DEFAULT_COLOR;

    /**
     * contructeur du Graph
     * @param context la MainActivity
     */
    public Graph(Context context) {
        LABEL_TEXT_SIZE= (int)context.getResources().getDimension(R.dimen.LABEL_TEXT_SIZE);
        DEFAULT_WIDTH= (int)context.getResources().getDimension(R.dimen.DEFAULT_WIDTH);
        DEFAULT_HEIGHT= (int)context.getResources().getDimension(R.dimen.DEFAULT_HEIGHT);
        DEFAULT_WIDTH_ARC= (int)context.getResources().getDimension(R.dimen.DEFAULT_WIDTH_ARC);
        DEFAULT_COLOR = (int)context.getResources().getColor(R.color.Bleu);
        spaceBetweenNodeInit = (int) context.getResources().getDimension(R.dimen.spaceBetweenNodeInit);
        MargeNodeInit =(int) context.getResources().getDimension(R.dimen.MargeNodeInit);
        buildGraph();
    }

    public Graph( int height, int width) {
        mHeight = height;
        mWidth = width;
        buildGraph();
    }

    public static final Creator<Graph> CREATOR = new Creator<Graph>() {
        @Override
        public Graph createFromParcel(Parcel in) {
            return new Graph(in);
        }

        @Override
        public Graph[] newArray(int size) {
            return new Graph[size];
        }
    };

    public Graph(List<Node> nodeList, int height, int width) {
        mNodeList = nodeList;
        mHeight = height;
        mWidth = width;
    }

    /**
     * Contructeur pour le Parcelable
     * @param in
     */
    public Graph(Parcel in) {
        mNodeList = in.createTypedArrayList(Node.CREATOR);
        mArcList = in.createTypedArrayList(Arc.CREATOR);
        mHeight = in.readInt();
        mWidth = in.readInt();
    }

    public void linkNodesWithArcs(){
        for (Arc arc : mArcList){
            for(Node node : mNodeList){
                if(arc.getNodeStart().getmCenterX()==node.getmCenterX() && arc.getNodeStart().getmCenterY()==node.getmCenterY()){
                    arc.setNodeStart(node);
                }else if(arc.getNodeEnd().getmCenterX()==node.getmCenterX() && arc.getNodeEnd().getmCenterY()==node.getmCenterY()){
                    arc.setNodeEnd(node);
                }
            }
        }
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelableList(mNodeList,0);
        dest.writeParcelableList(mArcList,0);
    }

    public void buildGraph(){
        for (int i = 0; i<N_NODE_INIT; i ++){
            for(int j = 0; j<N_NODE_INIT; j++){
                int num = getNodeList().size() + 1;
                Node node = new Node(j*spaceBetweenNodeInit+MargeNodeInit,i*spaceBetweenNodeInit+MargeNodeInit,DEFAULT_WIDTH,DEFAULT_HEIGHT,""+num);
                node.setColor(DEFAULT_COLOR);
                mNodeList.add(node);
                // Lors du premier chargement il ya pas de
                if(!mArcList.isEmpty()){
                    while(mArcList.iterator().hasNext())
                    {
                        // On se retrouve dans le cas d'un demarrage
                        // de l'application sur un ancien graph
                    }
                }
            }
        }
    }

    public List<Node> getNodeList() {
        return mNodeList;
    }

    public boolean addNode(Node node){
        //Véritication avant ajout, si noeud pas trop proche d'un autre noeud
        return mNodeList.add(node);

    }
    public void setNodeList(List<Node> nodeList) {
        mNodeList = nodeList;
    }



    public int getHeight() {
        return mHeight;
    }

    public void setHeight(int height) {
        mHeight = height;
    }

    public int getWidth() {
        return mWidth;
    }

    public void setWidth(int width) {
        mWidth = width;
    }

    public List<Arc> getmArcList() {
        return mArcList;
    }

    /**
     * Ajoute un Arc
     *
     * @param arc
     */
    public void addArc(Arc arc) {
        if (!overlap(arc.getNodeStart(), arc.getNodeEnd().getPositionX(),arc.getNodeEnd().getPositionY()))
            this.mArcList.add(arc);
    }

    /**
     * Ajoute un arc entre les nodes index1 et index2 de la liste de nodes
     *
     * @param index1
     * @param index2
     */
    public void addArc(int index1, int index2) {
        if (index1 != index2) {
            Node n1 = getNodeList().get(index1);
            Node n2 = getNodeList().get(index2);
            if (!overlap(n1, n2.getPositionX(),n2.getPositionY())) {
                Log.d("XXXXAD", "add arc ");
                this.mArcList.add(new Arc(n1, n2));
            }
        }
    }
    public  boolean selectAndEditNodeifExists(int x,int y){
        int index=0;
        Iterator<Node> i = mNodeList.iterator();
        while (i.hasNext()) {
            Node nodeInList = i.next();
            if (overlap(nodeInList, x,y)) {
                mNodeList.get(index).setNewPosition(x,y);
                return true;
            }
            index++;
        }
        return false;
    }
    public Node selectNodeAround(int x, int y){
        for (Node nodeInList : mNodeList){
            if (overlap(nodeInList, x,y)) return nodeInList;
        }
        return null;
    }
    public static boolean overlap(Node nodeInList, int x, int y){
        int xNode = nodeInList.getPositionX();
        int yNode = nodeInList.getPositionY();
        int widthNode = nodeInList.getmWidth();
        int heightNode = nodeInList.getmHeight();
        if( x >= xNode && x <= xNode+widthNode && y >= yNode && y <= yNode+heightNode) return true;
        return false;

    }

    public void removeNode(Node node){
        mNodeList.remove(node);
    }

    public void removeArc(Arc arc){
        mArcList.remove(arc);
    }

    public Arc selectArcAround(int mDownx, int mDowny) {
        for (Arc arcInList : mArcList){
            if (overlapArc(arcInList, mDownx,mDowny)) return arcInList;
        }
        return null;
    }

    private boolean overlapArc(Arc arcInList, int mDownx, int mDowny) {
        if(Math.abs(arcInList.getMiddlePoint()[0]-mDownx)<2*arcInList.getTexSize() && Math.abs(arcInList.getMiddlePoint()[1]-mDowny)<30) return true;
        return false;
    }
}
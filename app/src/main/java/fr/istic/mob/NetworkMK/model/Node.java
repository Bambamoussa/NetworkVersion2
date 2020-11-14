package fr.istic.mob.NetworkMK.model;

import android.graphics.RectF;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * La Classe Noeud
 *
 * Qui contient
 * <ul>
 *    <li>Le positionnement du Neoud</li>
 *    <li>L'étiquette du Noeud</li>
 *    <li>La couleur du Noeud</li>
 *    <li>La taille du Noeud</li>
 * </ul>
 */
public class Node implements Parcelable{
    private int mPositionX;
    private int mPositionY;
    private int mWidth;

    private int mCustomWidth;
    private int mHeight;
    private int mCenterX ;
    private int mCenterY;
    private String mEtiquete="";
    private int mColor;

    public RectF getBounds() {
        return bounds;
    }

    public void setBounds(RectF bounds) {
        this.bounds = bounds;
    }

    private RectF bounds;

    public int getmCenterX() {
        return mCenterX;
    }

    public void setmCenterX(int mCenterX) {
        this.mCenterX = mCenterX;
    }

    public int getmCenterY() {
        return mCenterY;
    }

    public void setmCenterY(int mCenterY) {
        this.mCenterY = mCenterY;
    }

    /**
     * Contructeur pour le Parcelable
     * @param in
     */
    private Node(Parcel in){
        this.mPositionX = in.readInt();
        this.mPositionY = in.readInt();
        this.mWidth = in.readInt();
        this.mHeight = in.readInt();
        this.mCenterX = in.readInt();
        this.mCenterY = in.readInt();
        this.mEtiquete = in.readString();
        this.mColor = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mPositionX);
        dest.writeInt(this.mPositionY);
        dest.writeInt(this.mWidth);
        dest.writeInt(this.mHeight);
        dest.writeInt(this.mCenterX);
        dest.writeInt(this.mCenterY);
        dest.writeString(this.mEtiquete);
        dest.writeInt(this.mColor);
    }

    public static final Parcelable.Creator<Node> CREATOR = new Creator<Node>() {
        @Override
        public Node createFromParcel(Parcel source) {
            return new Node(source);
        }

        @Override
        public Node[] newArray(int size) {
            return new Node[size];
        }
    };

    public Node(int positionX, int positionY,int width,int height, String etiquete) {
        mCenterX = positionX;
        mCenterY = positionY;
        mHeight = height;
        mWidth = width;
        mPositionX = (2*mCenterX - mWidth)/2;
        mPositionY = (2*mCenterY-mHeight)/2;
        // Quand on a l'étiquête on a le Rayon
        setEtiquete(etiquete);
        mCustomWidth=0;
        mColor=Graph.DEFAULT_COLOR;
    }


    public Node(int positionX, int positionY,String etiquete) {
        mPositionX = positionX;
        mPositionY = positionY;
        mColor=Graph.DEFAULT_COLOR;
        mEtiquete = etiquete;
        //Afficher l'étiquette
        computeCenterCoord();

    }

    public Node(int positionX, int positionY) {
        mPositionX = positionX;
        mPositionY = positionY;
        computeCenterCoord();
    }

    public void setNewPosition(int x, int y){
        mCenterX = x;
        mCenterY = y;
        if(mCustomWidth>mWidth) mWidth = mCustomWidth;
        mPositionX = (2*x - mWidth)/2;
        mPositionY = (2*y-mHeight)/2;
    }

    public void upDateCenter(){
        if(mCustomWidth>mWidth)
        mCenterX = (mPositionX+ mPositionX+mCustomWidth)/2;
        else
        {
            mPositionX = (2*mCenterX - Math.max(Graph.DEFAULT_WIDTH,mCustomWidth))/2;

        }
    }

    public void computeCenterCoord(){
        //Afficher l'étiquette
        //Si A(xA;yA) et C(xC;yC), alors les coordonnées du milieu M de [AC] sont
        // (x_M,y_M) = (x_A + x_C)/2;(y_A + y_C)/2)
        mCenterX = (mPositionX+ mPositionX+mWidth)/2;
        mCenterY = (mPositionY+mPositionY+mHeight)/2;
    }

    public int getmWidth() {
        return mWidth;
    }

    public void setmWidth(int mWidth) {
        this.mWidth = mWidth;
    }

    public int getmHeight() {
        return mHeight;
    }

    public void setmHeight(int mHeight) {
        this.mHeight = mHeight;
    }

    public int getPositionX() {
        return mPositionX;
    }

    public void setPositionX(int positionX) {
        mPositionX = positionX;
    }

    public int getPositionY() {
        return mPositionY;
    }

    public void setPositionY(int positionY) {
        mPositionY = positionY;
    }

    public String getEtiquete() {
        return mEtiquete;
    }

    public void setEtiquete(String etiquete) {
        mEtiquete = etiquete;
    }

    public int getmCustomWidth() {
        return mCustomWidth;
    }

    public void setmCustomWidth(int mCustomWidth) {
        this.mCustomWidth = mCustomWidth;
    }

    public int getColor() {
        return mColor;
    }

    public void setColor(int color) {
        mColor = color;
    }

}

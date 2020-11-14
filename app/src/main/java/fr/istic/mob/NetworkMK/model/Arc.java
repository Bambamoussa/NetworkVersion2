package fr.istic.mob.NetworkMK.model;

import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.graphics.Region;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * La Classe Arc
 *
 * Qui contient
 * <ul>
 *     <li>Le Noeud de départ</li>
 *    <li>Le Noeud de fin</li>
 *    <li>L'étiquette de l'arc</li>
 *    <li>La couleur de l'arc</li>
 *    <li>La taille de l'arc</li>
 * </ul>
 */
public class Arc implements Parcelable {
    private Node mNodeStart;
    private Node mNodeEnd;
    private int mColor;
    private boolean isEdited;

    private int mWidth=Graph.DEFAULT_WIDTH_ARC;

    private int texSize=Graph.LABEL_TEXT_SIZE;

    private String mLabel;
    private float [] middlePoint= {0f, 0f};
    private float [] tangent = {0f, 0f};

    /**
     * Contructeur pour le Parcelable
     * @param in
     */
    private Arc(Parcel in){
        this.mNodeStart = in.readParcelable(Node.class.getClassLoader());
        this.mNodeEnd = in.readParcelable(Node.class.getClassLoader());
        this.mLabel = in.readString();
        this.mColor = in.readInt();
        in.readFloatArray(this.middlePoint);
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(mNodeStart,flags);
        dest.writeParcelable(mNodeEnd,flags);
        dest.writeString(mLabel);
        dest.writeInt(mColor);
        dest.writeFloatArray(middlePoint);
    }

    public static final Parcelable.Creator<Arc> CREATOR = new Creator<Arc>() {
        @Override
        public Arc createFromParcel(Parcel source) {
            return new Arc(source);
        }

        @Override
        public Arc[] newArray(int size) {
            return new Arc[size];
        }
    };

    public Arc(Node nodeStart, Node nodeEnd, int color, String label, float[] middlePoint) {
        this.mNodeStart = nodeStart;
        this.mNodeEnd = nodeEnd;
        this.mColor = color;
        this.mLabel = label;
        this.middlePoint = middlePoint;
    }

    public Arc(Node nodeStart, Node nodeEnd, float[] middlePoint) {
        this.mNodeStart = nodeStart;
        this.mNodeEnd = nodeEnd;
        this.middlePoint = middlePoint;
    }

    public Arc(Node nodeStart, Node nodeEnd) {
        this.mColor = nodeStart.getColor();
        this.mNodeStart = nodeStart;
        this.mNodeEnd = nodeEnd;
        this.mLabel=nodeStart.getEtiquete()+"⇨"+nodeEnd.getEtiquete();
    }

    public Arc(Node nodeStart, Node nodeEnd,String label) {
        this.mColor = nodeStart.getColor();
        this.mNodeStart = nodeStart;
        this.mNodeEnd = nodeEnd;
        this.mLabel=label;
    }
    public Node getNodeStart() {
        return mNodeStart;
    }

    public void setNodeStart(Node nodeStart) {
        mNodeStart = nodeStart;
    }

    public Node getNodeEnd() {
        return mNodeEnd;
    }

    public void setNodeEnd(Node nodeEnd) {
        mNodeEnd = nodeEnd;
    }

    public int getColor() {
        return mColor;
    }

    public void setColor(int color) {
        mColor = color;
    }

    public String getLabel() {
        return mLabel;
    }

    public void setLabel() {
        mLabel = mNodeStart.getEtiquete()+"⇨"+mNodeEnd.getEtiquete();
    }

    public void setLabel(String label) {
        mLabel = label;
    }

    public void updateLabel(String label) {
        mLabel = label;
    }



    public float[] getMiddlePoint() {
        return middlePoint;
    }

    public void setMiddlePoint(float[] midllePoint) {
        isEdited = true;
        this.middlePoint = midllePoint;
    }

    public boolean isLoop(){
        return Graph.overlap(mNodeStart,mNodeEnd.getPositionX(),mNodeEnd.getPositionY());
    };

    /**
     * Le path de l'arc
     * @return
     */
    public Path getPath(){
        setMidPointAndTan();
        int xA = this.getNodeStart().getmCenterX();
        int xB = this.getNodeEnd().getmCenterX();
        int yA = this.getNodeStart().getmCenterY();
        int yB = this.getNodeEnd().getmCenterY();
        final Path path = new Path();

        path.moveTo(xA, yA);
        if(isLoop()){

        }else {
            path.cubicTo(xA, yA, middlePoint[0], middlePoint[1], xB, yB);
        }return path;
    }
    public void setMidPointAndTan (){
        Path p = new Path();
        p.moveTo(this.getNodeStart().getmCenterX(), this.getNodeStart().getmCenterY());

        if(isLoop()){

        }
        else if (isEdited){

        }
        else {
            p.lineTo(this.getNodeEnd().getmCenterX(), this.getNodeEnd().getmCenterY());
            final PathMeasure pm = new PathMeasure(p, false);
            pm.getPosTan(pm.getLength() * 0.50f, this.middlePoint, this.tangent);
        }
    }

    /**
     * Pour dessiner la fleche
     *
     * @return Le {@link Path} pour dessiner la fleche
     */
    public Path arrowPath() {
        double offset = 1e-5;
        float[] dot = {0f, 0f};
        float inf = 0, sup = 1, mid;
        PathMeasure patchMesure = new PathMeasure(getPath(), false);
        while (inf < sup - offset) {
            Path p = new Path();
            RectF rectFofNodeEnd = this.mNodeEnd.getBounds();
            p.addRoundRect(rectFofNodeEnd, 40, 40, Path.Direction.CW);
            p.computeBounds(rectFofNodeEnd, true);
            Region reg = new Region();
            reg.setPath(p, new Region((int) rectFofNodeEnd.left, (int) rectFofNodeEnd.top, (int) rectFofNodeEnd.right, (int) rectFofNodeEnd.bottom));
            mid = (inf + sup) / 2;
            patchMesure.getPosTan(patchMesure.getLength() * mid, dot, null);

            if (reg.contains((int) dot[0], (int) dot[1]))
                sup = mid;
            else
                inf = mid;
        }

        float[] tmp = {0f, 0f};
        int anchorWidth = 25;
        patchMesure.getPosTan(patchMesure.getLength() * (sup - anchorWidth / patchMesure.getLength()), tmp, null);

        float[] xPoint = {tmp[0] + dot[1] - tmp[1], tmp[1] + tmp[0] - dot[0]},
                yPoint = {tmp[0] + tmp[1] - dot[1], tmp[1] + dot[0] - tmp[0]};

        Path p = new Path();
        p.moveTo(dot[0], dot[1]);
        p.lineTo(xPoint[0], xPoint[1]);
        p.moveTo(dot[0], dot[1]);
        p.lineTo(yPoint[0], yPoint[1]);

        return p;
    }

    public int getTexSize() {
        return texSize;
    }

    public void setTexSize(int texSize) {
        this.texSize = texSize;
    }

    public int getmWidth() {
        return mWidth;
    }

    public void setmWidth(int mWidth) {
        this.mWidth = mWidth;
    }
}

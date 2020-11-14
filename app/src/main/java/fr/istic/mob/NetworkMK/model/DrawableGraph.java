package fr.istic.mob.NetworkMK.model;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PathMeasure;
import android.graphics.Rect;
import android.graphics.RectF;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * La Classe DrawableGraph
 * est celle qui dessin notre Graph
 *
 */
public class DrawableGraph extends android.graphics.drawable.BitmapDrawable{

    /**
     * Paint pour le Noeud
     */
    Paint mNodePaint;

    /**
     * Paint pour l'étiquette du Npeud
     */
    private Paint mNodeEtiqPaint;
    /**
     * Paint pour l'Arc
     */
    private Paint arcPaint;
    /**
     * Paint pour l'étiquette de l'Arc
     */
    private Paint arcEtiqPaint;
    private Paint arcEtiqBackPaint;
    private int margin;

    /**
     * Canvas pour dessiner
     */
    Canvas mCanvas;
    /**
     * Le graph à dessiner
     */
    Graph mGraph;

    /**
     * arc temporaire pour suivre les mouvements
     */
    private Arc tempArc = null;

    public Arc getTempArc() {
        return tempArc;
    }

    public void setTempArc(Arc tempArc) {
        this.tempArc = tempArc;
    }

    public DrawableGraph(Graph graph) {
        this.mGraph = graph;

        mNodePaint = new Paint();

        arcPaint = new Paint();
        arcPaint.setAntiAlias(true);
        arcPaint.setStyle(Paint.Style.STROKE);
        arcPaint.setColor(Color.RED);

        arcEtiqPaint = new Paint();
        arcEtiqPaint.setFakeBoldText(true);
        arcEtiqPaint.setColor(Color.WHITE);
        mNodeEtiqPaint= new Paint();
        mNodeEtiqPaint.setColor(Color.WHITE);
        mNodeEtiqPaint.setTextSize(graph.LABEL_TEXT_SIZE);
        mNodeEtiqPaint.setFakeBoldText(true);
    }

    public void drawNodes(){
        int ic = 0;
        for (Node node : mGraph.getNodeList()) {
            drawNode(node);
            ic++;
        }
        if (ic == 0) {
            mCanvas.drawCircle(0, 0, 1500,mNodePaint);
        }
    }

    public void drawNode(Node node) {
        mNodePaint.setColor(node.getColor());
        Rect rect = new Rect();
        mNodePaint.getTextBounds(node.getEtiquete(),0,node.getEtiquete().length(),rect);
        float textSize =  mNodeEtiqPaint.measureText(node.getEtiquete());
        node.setmWidth(Math.max(node.getmCustomWidth(),(int)Math.max(Graph.DEFAULT_WIDTH,textSize)));
        if(node.getmWidth()>50)margin = 10 ; else margin =0;
        RectF rectF = new RectF(node.getPositionX()-margin,node.getPositionY()-margin,node.getPositionX()+node.getmWidth()+margin,node.getPositionY()+Math.max(node.getmHeight(), rect.left)+margin);
        mCanvas.drawOval(rectF,mNodePaint);
        node.setBounds(rectF);
        int xPos;
        if(node.getmWidth()<=(int)textSize)
            xPos = node.getPositionX();
        else
            xPos = node.getmCenterX() - (int) (textSize/2);
        int yPos = node.getmCenterY() - (int) ((mNodeEtiqPaint.descent() + mNodeEtiqPaint.ascent()) / 2);
        mCanvas.drawText(node.getEtiquete(), xPos, yPos, mNodeEtiqPaint);
    }

    /**
     * Dessiner tout les arcs du graph
     * ou l'arc temporaire pendant la création d'un nouveau arc
     */
    public void drawArcs() {
        for (Arc arcs : mGraph.getmArcList()) {
            drawArc(arcs);
        }
        if (this.tempArc != null) {
            drawArc(tempArc);

        }
    }


    /**
     * Dessiner tout les arcs du graph
     */
    public void drawArc(Arc arc) {
        arcPaint.setColor(arc.getColor());
        arcPaint.setStrokeWidth(arc.getmWidth());
        arcEtiqPaint.setTextSize(arc.getTexSize());


        arcEtiqPaint.setTextAlign(Paint.Align.CENTER);
        arcEtiqBackPaint = new Paint();
        arcEtiqBackPaint.setColor(arc.getColor());
        mCanvas.drawPath(arc.getPath(), arcPaint);



        // Afficher l'étiquette
        float[] midPoint = {0f, 0f};
        float[] tangent = {0f, 0f};
        PathMeasure pm = new PathMeasure(arc.getPath(), false);
        pm.getPosTan(pm.getLength() * 0.50f, midPoint, tangent);

        int xPos = (int) midPoint[0] - (int) (mNodeEtiqPaint.measureText(arc.getLabel()) / 2);
        int yPos = (int) (midPoint[1] - ((mNodeEtiqPaint.descent() + mNodeEtiqPaint.ascent()) / 2));
        float w = arcEtiqPaint.measureText(arc.getLabel())/2;
        float textSize = arcEtiqPaint.getTextSize();
        arcEtiqPaint.setTextAlign(Paint.Align.CENTER);
        mCanvas.drawRect(xPos-w, yPos - textSize, xPos + w, yPos, arcEtiqBackPaint);
        mCanvas.drawText(arc.getLabel(), xPos, yPos, arcEtiqPaint);
        mCanvas.drawPath(arc.arrowPath(),arcPaint);
//        arc.setMiddlePoint(new float[]{xPos,yPos});
    }

    public Graph getGraph() {
        return mGraph;
    }

    public void setGraph(Graph graph) {
        mGraph = graph;
    }

    /**
     * surcharge de la méthode draw pour Dessiner le graph
     * @param canvas
     */
    @Override
    public void draw(@NonNull Canvas canvas) {
        this.mCanvas = canvas;
        drawArcs();
        drawNodes();
    }

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return 0;
    }


    public void initialise(Context context){
        mGraph = new Graph(context);
    }
}

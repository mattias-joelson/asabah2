package se.stny.thegridclient.util;


public class tgcStruct {
    private String ingressText;
    private String gridName;
    private int splitpos;
    private boolean isUsed;
    private boolean hasSibling;
    private int Sibling;

    public tgcStruct() {
        this.ingressText = "";
        this.gridName = "";
        this.splitpos = 0;
        this.isUsed = false;
    }


    public tgcStruct(String ingress, String grid, Integer split) {
        this.ingressText = ingress;
        this.gridName = grid;
        this.splitpos = split;
    }

    public tgcStruct(String ingress, String grid, Integer split, Integer sibling) {
        this.ingressText = ingress;
        this.gridName = grid;
        this.splitpos = split;
        this.hasSibling = true;
        this.Sibling = sibling;
    }

    public String getIngressText() {
        return this.ingressText;
    }

    public void setIngressText(String txt) {
        this.ingressText = txt;
    }

    public String getGridText() {
        return this.gridName;
    }

    public void setGridText(String txt) {
        this.gridName = txt;
    }

    public Integer getSplitPos() {
        return this.splitpos;
    }

    public void setSplitPos(Integer num) {
        this.splitpos = num;
    }


}
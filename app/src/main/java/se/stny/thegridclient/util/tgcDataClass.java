package se.stny.thegridclient.util;


public class tgcDataClass {
    private String ingressText;
    private String gridName;
    private int splitPositions;
    private boolean isUsed;
    private boolean hasSibling;
    private int Sibling;

    public tgcDataClass() {
        defValues();
    }

    public tgcDataClass(String ingress, String grid, Integer split) {
        defValues();
        this.ingressText = ingress;
        this.gridName = grid;
        this.splitPositions = split;

    }

    public tgcDataClass(String ingress, String grid, Integer split, Integer sibling) {
        defValues();
        this.ingressText = ingress;
        this.gridName = grid;
        this.splitPositions = split;
        this.Sibling = sibling;

    }

    private void defValues() {
        this.ingressText = "";
        this.gridName = "";
        this.splitPositions = 0;
        this.isUsed = false;
        this.hasSibling = false;
        this.Sibling = 0;

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
        return this.splitPositions;
    }

    public void setSplitPos(Integer num) {
        this.splitPositions = num;
    }

    public boolean hasSibling() {
        return this.hasSibling;
    }

    public Integer getSibling() {
        return this.Sibling;
    }

    public void setAsUsed() {
        this.isUsed = false;
    }

    public boolean checkUsed() {
        return this.isUsed;
    }


}
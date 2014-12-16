package se.stny.thegridclient.util;


public class tgcDataClass {
    private String ingressText;
    private String gridName;
    private int splitPositions;
    private boolean isUsed;
    private boolean hasSibling;
    private int Sibling;

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
        this.hasSibling = true;

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
    public String getGridText() {
        return this.gridName;
    }
    public Integer getSplitPos() {
        return this.splitPositions;
    }
    public boolean hasSibling() {
        return this.hasSibling;
    }
    public Integer getSibling() {
        return this.Sibling;
    }
    public void setAsUsed() {
        this.isUsed = true;
    }
    public boolean checkUsed() {
        return this.isUsed;
    }


}
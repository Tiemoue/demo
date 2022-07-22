package com.example.easycloset.Models;

import com.parse.ParseClassName;
import com.parse.ParseObject;


public class Suggest {

    String outer;
    String base;
    String bottom;
    String feet;
    String outerImgUrl;
    String baseImgUrl;
    String bottomImgUrl;
    String feetImgUrl;
    String outerColor;
    String baseColor;
    String bottomColor;
    String feetColor;


    public Suggest(){

    }

    public String getOuter() {
        return outer;
    }

    public void setOuter(String outer) {
        this.outer = outer;
    }

    public String getBase() {
        return base;
    }

    public String getOuterImgUrl() {
        return outerImgUrl;
    }

    public void setOuterImgUrl(String outerImgUrl) {
        this.outerImgUrl = outerImgUrl;
    }

    public String getBaseImgUrl() {
        return baseImgUrl;
    }

    public void setBaseImgUrl(String baseImgUrl) {
        this.baseImgUrl = baseImgUrl;
    }

    public String getBottomImgUrl() {
        return bottomImgUrl;
    }

    public void setBottomImgUrl(String bottomImgUrl) {
        this.bottomImgUrl = bottomImgUrl;
    }

    public String getFeetImgUrl() {
        return feetImgUrl;
    }

    public void setFeetImgUrl(String feetImgUrl) {
        this.feetImgUrl = feetImgUrl;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public String getBottom() {
        return bottom;
    }

    public void setBottom(String bottom) {
        this.bottom = bottom;
    }

    public String getFeet() {
        return feet;
    }

    public void setFeet(String feet) {
        this.feet = feet;
    }

    public String getOuterColor() {
        return outerColor;
    }

    public void setOuterColor(String outerColor) {
        this.outerColor = outerColor;
    }

    public String getBaseColor() {
        return baseColor;
    }

    public void setBaseColor(String baseColor) {
        this.baseColor = baseColor;
    }

    public String getBottomColor() {
        return bottomColor;
    }

    public void setBottomColor(String bottomColor) {
        this.bottomColor = bottomColor;
    }

    public String getFeetColor() {
        return feetColor;
    }

    public void setFeetColor(String feetColor) {
        this.feetColor = feetColor;
    }
}

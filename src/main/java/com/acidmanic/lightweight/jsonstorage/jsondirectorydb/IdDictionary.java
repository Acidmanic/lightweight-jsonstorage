/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.acidmanic.lightweight.jsonstorage.jsondirectorydb;

import java.util.HashMap;

/**
 * It serves as a simple pojo for string filename/ids. allows ids be not
 * filename-compatible
 *
 * @author diego
 */
public class IdDictionary {

    protected HashMap<String, String> data;

    public IdDictionary() {
        this.data = new HashMap<>();
    }

    public HashMap<String, String> getData() {
        return data;
    }

    public void setData(HashMap<String, String> data) {
        this.data = data;
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.imos.pi.ui.utils;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

/**
 *
 * @author Pintu
 */
public class DashboardUtils {

    public static void setMessage(String status, String msg) {
        FacesMessage message = new FacesMessage(status, msg);
        FacesContext.getCurrentInstance().addMessage(null, message);
    }
}

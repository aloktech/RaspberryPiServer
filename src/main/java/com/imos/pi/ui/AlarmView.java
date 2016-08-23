/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.imos.pi.ui;

import com.imos.pi.ui.utils.AlarmType;
import com.imos.pi.ui.utils.RecursionType;
import java.io.Serializable;
import java.util.Date;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Pintu
 */
@Getter
@Setter
@ManagedBean(name = "alarmv")
public class AlarmView implements Serializable {

    private String alarmName;
    
    private String songName;

    private boolean alarmEnable;

    private AlarmType alarmType;
    
    private RecursionType recursionType;

    private Date date;

    private int frequency;
    
    @PostConstruct
    public void init() {
        alarmType = AlarmType.DAILY;
        
        recursionType = RecursionType.NONE;
        
        frequency = 1;
        
        alarmEnable = true;
        
        alarmName = "";
        
        songName = "";
    }
}

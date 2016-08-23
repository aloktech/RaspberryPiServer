/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.imos.pi.alarm;

import java.util.Date;
import javax.faces.bean.ManagedBean;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * @author Alok
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode(exclude = {"id", "enable", "active", "executed"})
@ManagedBean(name = "alarm")
public class AlarmBean {

    private int id;
    private SongBean song;
    private Date dateAndTime;
    private AlarmType alarmType;
    private boolean enable;
    private boolean active;
    private boolean executed;
    private String alarmName;
    private int incrementByDays;
    private int incrementByHours;
    private int incrementByMinutes;
    private int frequency;
}

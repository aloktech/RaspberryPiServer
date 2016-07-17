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
@EqualsAndHashCode(exclude = "id")
@ManagedBean(name = "alarm")
public class AlarmBean {
    private int id;
    private SongBean song;
    private Date hourAndMinute;
    private Date dateAndTimeToPlay;
    private AlarmType alarmType;
    private boolean enable;
    private String alarmName;
}

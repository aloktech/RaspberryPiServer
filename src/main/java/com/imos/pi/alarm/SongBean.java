/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.imos.pi.alarm;

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
@EqualsAndHashCode(exclude = {"id", "songData"})
@ToString
public class SongBean {

    private int id;
    private String songName;
    private String songPath;
    private byte[] songData;
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.imos.pi.ui.utils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.faces.bean.ManagedBean;
import lombok.Getter;

/**
 *
 * @author Pintu
 */
@Getter
@ManagedBean(name = "utility")
public class MenuUtils {

    private final List<RecursionType> recursionTypes = Arrays.asList(RecursionType.values());

    private final List<AlarmType> alarmTypes = Arrays.asList(AlarmType.values());

    private final List<Integer> frequencies = IntStream.range(1, 23).boxed().collect(Collectors.toList());
}

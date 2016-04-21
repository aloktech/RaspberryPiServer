/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.imos.pi.utils;

import com.hazelcast.config.Config;
import com.hazelcast.config.MapStoreConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

/**
 *
 * @author Alok
 */
public class HazelcastFactory {

    private final HazelcastInstance hazelcastInstance;
    private static HazelcastFactory INSTANCE;

    public HazelcastFactory() {
        Config config = new Config();
        NetworkConfig networkConfig = new NetworkConfig();
//        networkConfig.setPort(5703);
        networkConfig.setPublicAddress("10.0.0.10");
        config.setNetworkConfig(networkConfig);

        MapStoreConfig storeConfig = new MapStoreConfig();
        storeConfig.setEnabled(true);
        config.getMapConfig("posts").setMapStoreConfig(storeConfig);

        ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(Hazelcast.class.getClassLoader());
        
        hazelcastInstance = Hazelcast.newHazelcastInstance(config);
        
        Thread.currentThread().setContextClassLoader(tccl);
    }

    public static HazelcastFactory getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new HazelcastFactory();
        }
        return INSTANCE;
    }

    public HazelcastInstance getHazelcastInstance() {
        return hazelcastInstance;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.imos.pi.utils;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.config.Config;
import com.hazelcast.config.MapStoreConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 *
 * @author Alok
 */
public class HazelcastFactory {

    private HazelcastInstance hazelcastInstance;
    private static HazelcastFactory INSTANCE;

    public HazelcastFactory() {
        Context ctx;
        try {
            ctx = new InitialContext();
            hazelcastInstance = (HazelcastInstance) ctx.lookup("payara/Hazelcast");
        } catch (NamingException ex) {
            try {
                hazelcastInstance = HazelcastClient.newHazelcastClient();

                if (hazelcastInstance == null || !hazelcastInstance.getLifecycleService().isRunning()) {
                    configure();
                }
            } catch (Exception e) {
                configure();
            }
        }
    }

    private void configure() {
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

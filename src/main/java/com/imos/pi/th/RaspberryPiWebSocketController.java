/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.imos.pi.th;

import com.hazelcast.core.HazelcastInstance;
import com.imos.pi.utils.HazelcastFactory;
import static com.imos.pi.utils.RaspberryPiConstant.CURRENT;
import static com.imos.pi.utils.RaspberryPiConstant.TEMP_HUMID_CURRENT;
import com.imos.pi.utils.WebSocketServerManagement;
import java.io.IOException;
import java.net.URI;
import java.util.concurrent.ConcurrentMap;
import javax.annotation.PostConstruct;
import javax.ejb.DependsOn;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.WebSocketContainer;
import lombok.extern.java.Log;

/**
 *
 * @author Alok
 */
@ApplicationScoped
@Log
@DependsOn({"RaspberryPiWebSocketClient"})
public class RaspberryPiWebSocketController {

//    @Inject
//    private RaspberryPiWebSocketClient client;
//    private WebSocketContainer container;
//    private ConcurrentMap<String, String> currentMap;
//    private HazelcastInstance hazelcastInstance;
//
//    public RaspberryPiWebSocketController() {
//    }
//
//    @PostConstruct
//    public void init() {
//        container = ContainerProvider.getWebSocketContainer();
//        hazelcastInstance = HazelcastFactory.getInstance().getHazelcastInstance();
//    }
//
//    void updateCurrentTempHumid() {
//        try {
////            String url = WebSocketServerManagement.URL + "/webserver";
//            String url = WebSocketServerManagement.URL;
//            container.connectToServer(client, null, URI.create(url));
//            currentMap = hazelcastInstance.getMap(TEMP_HUMID_CURRENT);
//            if (currentMap != null) {
//                client.sendMessage(currentMap.get(CURRENT));
//                log.info(currentMap.get(CURRENT));
//            }
//        } catch (DeploymentException | IOException ex) {
//            log.severe(ex.getMessage());
//        }
//    }
}

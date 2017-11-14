package com.cetc.hubble.metagrid.elasticsearch;

import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeValidationException;
import org.elasticsearch.transport.Netty4Plugin;

import java.io.IOException;
import java.util.Arrays;

/**
 * Created by dahey on 2017/4/14.
 */
public class EmbedSearchServer {
        private Node node;
        private EmbedSearchServer(){};
        public EmbedSearchServer(String dataPath,String httpPort,String transportTcpPort){
            node = new EmbedNode(
                    Settings.builder()
                            .put("transport.type", "netty4")
                            .put("http.type", "netty4")
                            .put("http.enabled", "true")
                            .put("http.port", httpPort)
                            .put("transport.tcp.port", transportTcpPort)
                            .put("path.home", "classpath")
//                            .put("path.logs", logPath)
                            .put("path.data", dataPath)
                            .build(),
                    Arrays.asList(Netty4Plugin.class)
            );
        }
        public void start() throws NodeValidationException {
            node.start();
        }
        public void stop() throws IOException {
            node.close();
        }
        public Client getClient() {
            return node.client();
        }

//    public static void main(String[] args) throws Exception{
//        new EmbedSearchServer("D:\\elasticsearch\\embed_server_data").start();
//        Thread.sleep(100000);
//    }
}

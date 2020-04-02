package com.sarang.rabbimq;


import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.GetResponse;
import org.apache.http.ssl.SSLContexts;

import javax.net.ssl.SSLContext;

public class App {
    public static void main(String[] args) throws Exception {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setPort(5671);
        factory.setUsername("admin");
        factory.setPassword("admin");

        SSLContext c = SSLContexts.custom().setProtocol("TLSv1.1").build();

        factory.useSslProtocol(c);
        // Tells the library to setup the default Key and Trust managers for you
        // which do not do any form of remote server trust verification

        Connection conn = null;
        Channel channel = null;
        try {
            conn = factory.newConnection();
            channel = conn.createChannel();

            // non-durable, exclusive, auto-delete queue
            channel.queueDeclare("rabbitmq-java-test", false, true, true, null);
            channel.basicPublish("", "rabbitmq-java-test", null, "Hello, World".getBytes());

            GetResponse chResponse = channel.basicGet("rabbitmq-java-test", false);
            if (chResponse == null) {
                System.out.println("No message retrieved");
            } else {
                byte[] body = chResponse.getBody();
                System.out.println("Received: " + new String(body));
            }

        } catch (Exception e) {
            System.out.println("Error in making connection: " + e);
        } finally {
            if (null != channel) {
                channel.close();
            }
            if (null != conn) {
                conn.close();
            }
        }


    }
}
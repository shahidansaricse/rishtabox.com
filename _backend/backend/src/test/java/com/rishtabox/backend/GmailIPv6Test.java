package com.rishtabox.backend;

import java.net.Inet6Address;
import java.net.InetSocketAddress;
import java.net.Socket;

public class GmailIPv6Test {

    public static void main(String[] args) {

        String ipv6 = "2404:6800:4000:1025::6d";

        System.out.println("=================================");
        System.out.println("GMAIL IPv6 CONNECTION TEST");
        System.out.println("=================================");
        System.out.println("IPv6: " + ipv6);
        System.out.println("Port: 587");
        System.out.println();

        try (Socket socket = new Socket()) {

            long start = System.currentTimeMillis();

            socket.connect(
                    new InetSocketAddress(
                            Inet6Address.getByName(ipv6),
                            587
                    ),
                    10000
            );

            long time = System.currentTimeMillis() - start;

            System.out.println("CONNECTION SUCCESS");
            System.out.println("Remote: "
                    + socket.getRemoteSocketAddress());
            System.out.println("Time: " + time + " ms");

        } catch (Exception e) {

            System.out.println("CONNECTION FAILED");
            e.printStackTrace();
        }

        System.out.println();
        System.out.println("=================================");
        System.out.println("TEST FINISHED");
        System.out.println("=================================");
    }
}
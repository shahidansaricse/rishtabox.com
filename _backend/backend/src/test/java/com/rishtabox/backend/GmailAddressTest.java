package com.rishtabox.backend;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class GmailAddressTest {

    public static void main(String[] args) {

        System.out.println("=================================");
        System.out.println("GMAIL ADDRESS + CONNECTION TEST");
        System.out.println("=================================");

        try {

            InetAddress[] addresses =
                    InetAddress.getAllByName("smtp.gmail.com");

            System.out.println("Total addresses: " + addresses.length);

            for (InetAddress address : addresses) {

                System.out.println();
                System.out.println("---------------------------------");
                System.out.println("Address: " + address.getHostAddress());
                System.out.println("Type: " + address.getClass().getSimpleName());
                System.out.println("---------------------------------");

                try (Socket socket = new Socket()) {

                    long start = System.currentTimeMillis();

                    socket.connect(
                            new InetSocketAddress(address, 587),
                            10000
                    );

                    long time =
                            System.currentTimeMillis() - start;

                    System.out.println("CONNECTION SUCCESS");
                    System.out.println("Remote: "
                            + socket.getRemoteSocketAddress());
                    System.out.println("Time: " + time + " ms");

                } catch (Exception e) {

                    System.out.println("CONNECTION FAILED");
                    System.out.println(
                            "Error: "
                                    + e.getClass().getSimpleName()
                                    + " - "
                                    + e.getMessage()
                    );
                }
            }

        } catch (Exception e) {

            System.out.println("DNS TEST FAILED");
            e.printStackTrace();
        }

        System.out.println();
        System.out.println("=================================");
        System.out.println("TEST FINISHED");
        System.out.println("=================================");
    }
}
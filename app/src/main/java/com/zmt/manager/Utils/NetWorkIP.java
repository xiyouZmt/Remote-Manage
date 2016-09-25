package com.zmt.manager.Utils;

/**
 * Created by Dangelo on 2016/7/22.
 */
import android.util.Log;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

public class NetWorkIP {

    public String getIP(){
        Enumeration allNetInterfaces = null;
        try {
            allNetInterfaces = NetworkInterface.getNetworkInterfaces();
        } catch (java.net.SocketException e) {
            e.printStackTrace();
        }
        InetAddress ip ;
        while (allNetInterfaces.hasMoreElements()) {
            NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
            System.out.println(netInterface.getName());
            Enumeration addresses = netInterface.getInetAddresses();
            while (addresses.hasMoreElements()) {
                ip = (InetAddress) addresses.nextElement();
                if (ip != null) {
                    if(ip instanceof Inet4Address){
                        System.out.println("/u672c/u673a/u7684IP = "
                                + ip.getHostAddress());
                        if(ip.getHostAddress().equals("127.0.0.1")){
                            continue;
                        }
                        return ip.getHostAddress();
                    } else {
                        Log.e("ip--->", ip.getHostAddress());
                    }
                }
            }
        }
        return null;
    }
}



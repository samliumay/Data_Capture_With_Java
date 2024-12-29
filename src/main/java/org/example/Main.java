package org.example;
import org.pcap4j.core.*;
import org.pcap4j.packet.Packet;
import org.pcap4j.packet.TcpPacket;
import org.pcap4j.packet.UdpPacket;

import java.nio.charset.StandardCharsets;
import java.util.List;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) throws PcapNativeException {
        try {
            // Belirli bir ağ arayüzü seçin
            String nifName = "\\Device\\NPF_{EEB89974-01AC-4628-8AC8-657D52163A0F}";
            PcapNetworkInterface nif = Pcaps.getDevByName(nifName);

            if (nif == null) {
                System.out.println("Ağ arayüzü bulunamadı: " + nifName);
                return;
            }

            // Yakalama işlemi için PcapHandle oluşturun
            int snapLen = 65536; // Maksimum paket boyutu
            PcapNetworkInterface.PromiscuousMode mode = PcapNetworkInterface.PromiscuousMode.PROMISCUOUS; // Tüm trafiği yakala
            int timeout = 10; // Zaman aşımı (ms)

            try (PcapHandle handle = nif.openLive(snapLen, mode, timeout)) {
                System.out.println("Paket yakalamaya başlanıyor...");

                // Paket dinleme döngüsü
                handle.loop(-1, (PacketListener) packet -> {
                    System.out.println("Yakalanan Paket: " + packet);
                    if (packet.getPayload() != null) {
                        byte[] payload = packet.getPayload().getRawData();
                        String readableData = new String(payload, StandardCharsets.UTF_8);
                        //System.out.println("Veri (okunabilir): " + readableData);
                        if (packet.contains(UdpPacket.class)) {
                            UdpPacket udpPacket = packet.get(UdpPacket.class);
                            System.out.println("UDP Verisi: " + udpPacket);
                        }
                        if (packet.contains(TcpPacket.class)) {
                            TcpPacket tcpPacket = packet.get(TcpPacket.class);
                            System.out.println("TCP Verisi: " + tcpPacket);
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
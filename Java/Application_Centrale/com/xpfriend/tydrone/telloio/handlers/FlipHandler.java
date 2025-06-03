package com.xpfriend.tydrone.telloio.handlers;

import com.xpfriend.tydrone.core.Info;
import com.xpfriend.tydrone.telloio.CRC;
import com.xpfriend.tydrone.telloio.Message;
import com.xpfriend.tydrone.telloio.MessageHandler;

import java.io.IOException;

public class FlipHandler extends MessageHandler {
    private byte flipAxis = 0x00;

    @Override
    public void sendRequest(String command, Info info) throws IOException {
        String[] values = command.split(" ");
        if (values.length != 2) {
            loge("invalid command: " + command);
        } else {
           flipAxis = (byte)(Integer.valueOf(values[1]).intValue());
        }
        
        byte[] data = new byte[]{(byte) 0xcc, // header (always 0xcc)
                (byte) 0x60, 0x00,   // packet size
                0x27,                // CRC-8
                0x70,                // packet type
                0x5C, 0x00,          // message id
                (byte)0xEE, 0x02,    // seq
                (byte)flipAxis,		 // axis
                0x00, 0x00           // CRC-16
        };
        
        // calcul et ajout du CRC16
        int crc16Value = CRC.crc16(data,10);
        data[10] = (byte)(crc16Value & 0x00FF);
        data[11] = (byte)( (crc16Value & 0xFF00) >> 8 );
        
        System.out.println("\t\t\t\tFlipHandler [" + flipAxis + "]  execute");
        send(data, false);
        
        logd("Flip " + (int)flipAxis );
        info.setSentCommand(command);
    }

    @Override
    public void handleMessage(Message message, Info info) {
    }
}

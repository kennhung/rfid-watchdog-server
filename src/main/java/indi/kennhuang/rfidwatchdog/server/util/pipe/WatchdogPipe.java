package indi.kennhuang.rfidwatchdog.server.util.pipe;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Pipe;
import java.nio.charset.Charset;

public class WatchdogPipe {
    private Pipe pipe;
    private Pipe.SinkChannel sinkChannel;
    private Pipe.SourceChannel sourceChannel;

    public WatchdogPipe() throws IOException {
        pipe = Pipe.open();
        sinkChannel = pipe.sink();
        sourceChannel = pipe.source();
    }

    public void send(String msg) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(512);
        buffer.clear();
        buffer.put(msg.getBytes());

        buffer.flip();
        //write data into sink channel.
        while(buffer.hasRemaining()) {
            sinkChannel.write(buffer);
        }
    }

    public String listen() throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(512);
        sourceChannel.read(buffer);
        buffer.flip();
        String out = "";
        while(buffer.hasRemaining()){
            char ch = (char) buffer.get();
            out += ch;
        }
        buffer.clear();
        return out;
    }

}

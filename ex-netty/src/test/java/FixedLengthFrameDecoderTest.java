
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import lucky.baijunhan.ex.netty.FixedLengthFrameDecoder;
import org.junit.Test;
import static org.junit.Assert.*;

public class FixedLengthFrameDecoderTest {

    @Test
    public void testFrameDecoder(){
        ByteBuf buf = Unpooled.buffer();
        for(int i = 0; i < 9;i ++)
            buf.writeByte(i);
        ByteBuf input = buf.duplicate();
        EmbeddedChannel channel = new EmbeddedChannel(new FixedLengthFrameDecoder(3));

        assertTrue(channel.writeInbound(input.retain()));
        assertTrue(channel.finish());

        ByteBuf read = channel.readInbound();
        assertEquals(buf.readSlice(3),read);
        read.release();
        read = channel.readInbound();
        assertEquals(buf.readSlice(3),read);
        read.release();

        read = channel.readInbound();
        assertEquals(buf.readSlice(3),read);
        read.release();

        assertNull(channel.readInbound());
        buf.release();
    }
}

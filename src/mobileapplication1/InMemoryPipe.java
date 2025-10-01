package mobileapplication1;

import java.io.*;

// In-memory blocking pipe for J2ME (CLDC 1.1)
public final class InMemoryPipe {
  private final byte[] buf;
  private int readPos = 0, writePos = 0, count = 0;
  private boolean inputClosed = false;
  private boolean outputClosed = false;

  public InMemoryPipe(int capacity) {
    if (capacity <= 0) throw new IllegalArgumentException("capacity");
    this.buf = new byte[capacity];
  }

  public InputStream getInputStream() { return new PipeInputStream(); }
  public OutputStream getOutputStream() { return new PipeOutputStream(); }

  private final class PipeInputStream extends InputStream {
    public int read() throws IOException {
      byte[] b = new byte[1];
      int n = read(b, 0, 1);
      return (n == -1) ? -1 : (b[0] & 0xFF);
    }

    public int read(byte[] b, int off, int len) throws IOException {
      if (b == null) throw new NullPointerException();
      if (off < 0 || len < 0 || off + len > b.length) throw new IndexOutOfBoundsException();
      synchronized (InMemoryPipe.this) {
        while (count == 0 && !outputClosed) {
          try { InMemoryPipe.this.wait(); } catch (InterruptedException ie) { /* ignore */ }
        }
        if (count == 0 && outputClosed) return -1; // EOF

        int total = 0;
        while (len > 0 && count > 0) {
          int chunk = Math.min(len, Math.min(buf.length - readPos, count));
          System.arraycopy(buf, readPos, b, off, chunk);
          readPos = (readPos + chunk) % buf.length;
          count -= chunk;
          off += chunk;
          len -= chunk;
          total += chunk;
        }
        InMemoryPipe.this.notifyAll();
        return total;
      }
    }

    public int available() {
      synchronized (InMemoryPipe.this) { return count; }
    }

    public void close() {
      synchronized (InMemoryPipe.this) {
        inputClosed = true;
        InMemoryPipe.this.notifyAll();
      }
    }
  }

  private final class PipeOutputStream extends OutputStream {
    public void write(int b) throws IOException {
      byte[] one = new byte[] { (byte)b };
      write(one, 0, 1);
    }

    public void write(byte[] b, int off, int len) throws IOException {
      if (b == null) throw new NullPointerException();
      if (off < 0 || len < 0 || off + len > b.length) throw new IndexOutOfBoundsException();
      synchronized (InMemoryPipe.this) {
        if (outputClosed) throw new IOException("Pipe output closed");
        while (len > 0) {
          while (count == buf.length) {             // buffer full ⇒ backpressure
            if (inputClosed) throw new IOException("Pipe broken (reader closed)");
            try { InMemoryPipe.this.wait(); } catch (InterruptedException ie) { /* ignore */ }
          }
          int space = buf.length - count;
          int chunk = Math.min(len, Math.min(space, buf.length - writePos));
          System.arraycopy(b, off, buf, writePos, chunk);
          writePos = (writePos + chunk) % buf.length;
          count += chunk;
          off += chunk;
          len -= chunk;
          InMemoryPipe.this.notifyAll();
        }
      }
    }

    public void flush() { /* no-op for memory pipe */ }

    public void close() {
      synchronized (InMemoryPipe.this) {
        if (!outputClosed) {
          outputClosed = true;      // signals EOF to reader
          InMemoryPipe.this.notifyAll();
        }
      }
    }
  }
}

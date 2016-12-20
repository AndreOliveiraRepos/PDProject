package common;

import java.io.Serializable;

/**
 *
 * @author André Oliveira
 */
public class FileObject implements Serializable {
    private boolean isEOF;
    private byte [] fileChunk;
    private int nBytes;
    
    
    public FileObject(){
        isEOF = false;
        fileChunk = null;
        nBytes = 0;
    }

    public boolean isIsEOF() {
        return isEOF;
    }

    public void setIsEOF(boolean isEOF) {
        this.isEOF = isEOF;
    }

    public byte[] getFileChunk() {
        return fileChunk;
    }

    public void setFileChunk(byte[] fileChunk) {
        this.fileChunk = fileChunk.clone();
    }

    public int getnBytes() {
        return nBytes;
    }

    public void setnBytes(int nBytes) {
        this.nBytes = nBytes;
    }
    
}

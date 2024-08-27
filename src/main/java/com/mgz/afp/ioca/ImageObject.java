package com.mgz.afp.ioca;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.mgz.afp.enums.AFPUnitBase;

/**
 * Represent an image object, everything between BIM_BeginImageObject and EIM_EndImageObject
 */
public class ImageObject {
  
  List<Byte> imageData = new ArrayList<Byte>();
  int remainingBytes = 0; // bytes remaining across IPD_ImagePictureData
  boolean complete = false;
  
  // ImageSize
  AFPUnitBase unitBase;
  short xUnitsPerUnitBase;
  short yUnitsPerUnitBase;
  short xImageSize;
  short yImageSize;
  int xOrigin;
  int yOrigin;

  int[] color;
  
  short imageElementSize;
  
  // ImageEncoding
  IPD_Segment.IPD_CompressionAlgorithm compressionAlgorithm;
  IPD_Segment.IPD_RecordingAlgorithm recordingAlgorithm;
  IPD_Segment.IPD_BitOrder bitOrder;
  
  public boolean isComplete() {
    return complete;
  }
  public void setComplete(boolean complete) {
    this.complete = complete;
  }
  public int getRemainingBytes() {
    return remainingBytes;
  }
  public void setRemainingBytes(int remainingBytes) {
    this.remainingBytes = remainingBytes;
  }

  public void setOffset(int x, int y) {
    this.xOrigin = x;
    this.yOrigin = y;
  }

  public void setBilevelColor(int[] rgb) {
    this.color = rgb;
  }
  
  public void setImageSize(AFPUnitBase unitBase, short xUnitsPerUnitBase, short yUnitsPerUnitBase, short xImageSize, short yImageSize) {
    this.unitBase = unitBase;
    this.xUnitsPerUnitBase = xUnitsPerUnitBase;
    this.yUnitsPerUnitBase = yUnitsPerUnitBase;
    this.xImageSize = xImageSize;
    this.yImageSize = yImageSize;
  }
  
  public void setImageEncoding(IPD_Segment.IPD_CompressionAlgorithm compressionAlgorithm, 
        IPD_Segment.IPD_RecordingAlgorithm recordingAlgorithm, IPD_Segment.IPD_BitOrder bitOrder) {

    this.compressionAlgorithm = compressionAlgorithm;
    this.recordingAlgorithm = recordingAlgorithm;
    this.bitOrder = bitOrder;

  }

  public short getImageElementSize() {
    return imageElementSize;
  }
  public void setImageElementSize(short imageElementSize) {
    this.imageElementSize = imageElementSize;
  }

  public void addImageData(byte[] imageData) {
    for (int i=0; i < imageData.length; i++) {
      this.imageData.add(Byte.valueOf(imageData[i]));
    }
  }
  public List<Byte> getImageData() {
    return this.imageData;
  }

  public String toString() {
    return String.format("data size : %d bytes\n"
            + "xUnits: %d per %d (1=10cm, 0=10in)\n"
            + "yUnits: %d per %d\n"
            + "xSize:  %d\n"
            + "ySize:  %d\n"
            + "depth:  %d\n"
            + "compression algorithm: %d\n (3=none)\n"
            + "recording algorithm: %d (1=RDIC)\n"
        
        , imageData.size(), xUnitsPerUnitBase, unitBase.toByte(),
        yUnitsPerUnitBase,unitBase.toByte(),xImageSize, yImageSize, imageElementSize,
        compressionAlgorithm.toByte(), recordingAlgorithm.toByte());
  }

  // write bytes to file, flipping every bit
  public void bytesOut(String file) throws IOException {

    FileOutputStream os = new FileOutputStream(file);
    
    byte[] bytes =  new byte[imageData.size()];
    int i = 0;
    for (Byte b: imageData) {
      bytes[i++] = (byte) ~b;
    }
    os.write(bytes);

    os.close();
  }
  
  // write bits to file, ascii art style
  public void bitsOut(String file) throws IOException {
    FileOutputStream os = new FileOutputStream(file);

    int xBytes = xImageSize/8;
    
    for (int y=0; y < yImageSize; y++) {
      for (int x=0; x < xBytes; x++) {
        os.write(bits(imageData.get(x + (y*xBytes) )));
      }
      os.write(0x0A);
    }
    os.close();
  }
  
  // return bits as array of characters
  private byte[] bits(byte b) {
    byte[] bits = new byte[8];
    
    bits[0] = (byte) (((~b & 0x80) == 0) ? '0' : '1'); 
    bits[1] = (byte) (((~b & 0x40) == 0) ? '0' : '1'); 
    bits[2] = (byte) (((~b & 0x20) == 0) ? '0' : '1'); 
    bits[3] = (byte) (((~b & 0x10) == 0) ? '0' : '1'); 
    bits[4] = (byte) (((~b & 0x08) == 0) ? '0' : '1'); 
    bits[5] = (byte) (((~b & 0x04) == 0) ? '0' : '1'); 
    bits[6] = (byte) (((~b & 0x02) == 0) ? '0' : '1'); 
    bits[7] = (byte) (((~b & 0x01) == 0) ? '0' : '1'); 
    
    return bits;
  }

  public int getXOrigin() {
    return this.xOrigin;
  }
  
  public int getYOrigin() {
    return this.yOrigin;
  }
  
  public short getHeight() {
    return this.yImageSize;
  }
  
  public short getWidth() {
    return this.xImageSize;
  }
  
  public int[] getColor() {
    return this.color;
  }
}

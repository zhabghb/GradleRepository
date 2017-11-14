package com.cetc.hubble.metagrid.vo;

import java.util.List;

/**
 * Created by jinyi on 17-8-3.
 */
public class HdfsLocatedBlock {

    private long blockId;
    private String blockPoolId;
    private long generationStamp;
    private long numBytes;
    private boolean isCorrupt;
    private List<HdfsLocatedBlockLocation> locations;

    public long getBlockId() {
        return blockId;
    }

    public void setBlockId(long blockId) {
        this.blockId = blockId;
    }

    public String getBlockPoolId() {
        return blockPoolId;
    }

    public void setBlockPoolId(String blockPoolId) {
        this.blockPoolId = blockPoolId;
    }

    public long getGenerationStamp() {
        return generationStamp;
    }

    public void setGenerationStamp(long generationStamp) {
        this.generationStamp = generationStamp;
    }

    public long getNumBytes() {
        return numBytes;
    }

    public void setNumBytes(long numBytes) {
        this.numBytes = numBytes;
    }

    public boolean isCorrupt() {
        return isCorrupt;
    }

    public void setCorrupt(boolean corrupt) {
        isCorrupt = corrupt;
    }

    public List<HdfsLocatedBlockLocation> getLocations() {
        return locations;
    }

    public void setLocations(List<HdfsLocatedBlockLocation> locations) {
        this.locations = locations;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.uva.p2psearch;

/**
 *
 * @author S. Koulouzis
 */
class Entry implements EntryI {

    /**
     * 
     */
    private String description;
    private String tags;
    private long creationDate;
    private String creator;
    private String format;
    private String contentHash;

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * @return the tag
     */
    public String getTags() {
        return tags;
    }

    /**
     * @param tag the tag to set
     */
    public void setTags(final String tags) {
        this.tags = tags;
    }

    /**
     * @return the creationDate
     */
    public long getCreationDate() {
        return creationDate;
    }

    /**
     * @param creationDate the creationDate to set
     */
    public void setCreationDate(long creationDate) {
        this.creationDate = creationDate;
    }

    /**
     * @return the creator
     */
    public String getCreator() {
        return creator;
    }

    /**
     * @param creator the creator to set
     */
    public void setCreator(final String creator) {
        this.creator = creator;
    }

    /**
     * @return the format
     */
    public String getFormat() {
        return format;
    }

    /**
     * @param format the format to set
     */
    public void setFormat(final String format) {
        this.format = format;
    }

    /**
     * @return the contentHash
     */
    @Override
    public String getContentHash() {
        return contentHash;
    }

    /**
     * @param contentHash the contentHash to set
     */
    public void setContentHash(final String contentHash) {
        this.contentHash = contentHash;
    }
}

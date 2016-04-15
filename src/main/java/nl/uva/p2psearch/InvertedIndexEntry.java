/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.uva.p2psearch;

import java.util.List;
import net.tomp2p.peers.Number160;

/**
 *
 * @author S. Koulouzis
 */
class InvertedIndexEntry {

    /**
     *
     */
    private final String contentHash;
    /**
     *
     */
    private final String term;
    /**
     *
     */
    private final List<Number160> metadataEntryIDs;
    private Integer tf;

    public InvertedIndexEntry(final String contentHash, final String term, final Integer tf, final List<Number160> entryIDs) {
        this.metadataEntryIDs = entryIDs;
        this.term = term;
        this.contentHash = contentHash;
        this.tf = tf;
    }

    /**
     * @return the term
     */
    public String getTerm() {
        return term;
    }

    /**
     * @return the entryIDs
     */
    public List<Number160> getMetadataEntryIDs() {
        return metadataEntryIDs;
    }

    public String getID() {
        return contentHash;
    }

    /**
     * @return the tf
     */
    public Integer getTf() {
        return tf;
    }

    void setTF(Integer tf) {
        this.tf = tf;
    }
}
